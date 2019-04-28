package ca.fuwafuwa.kaku.Windows

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.media.Image
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView

import com.googlecode.leptonica.android.GrayQuant
import com.googlecode.leptonica.android.Pix
import com.googlecode.leptonica.android.ReadFile
import com.googlecode.leptonica.android.WriteFile

import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer

import androidx.core.content.ContextCompat
import ca.fuwafuwa.kaku.*
import ca.fuwafuwa.kaku.MainService
import ca.fuwafuwa.kaku.Ocr.BoxParams
import ca.fuwafuwa.kaku.Ocr.OcrParams
import ca.fuwafuwa.kaku.Ocr.OcrRunnable
import ca.fuwafuwa.kaku.Prefs
import ca.fuwafuwa.kaku.R
import ca.fuwafuwa.kaku.TextDirection
import ca.fuwafuwa.kaku.Windows.Interfaces.WindowListener
import ca.fuwafuwa.kaku.XmlParsers.CommonParser

/**
 * Created by 0xbad1d3a5 on 4/13/2016.
 */
class CaptureWindow(context: Context, windowCoordinator: WindowCoordinator) : Window(context, windowCoordinator, R.layout.window_capture), WindowListener
{
    private val mOcr: OcrRunnable
    private val mWindowBox: View
    private val mImageView: ImageView
    private val mFadeRepeat: Animation
    private val mBorderDefault: Drawable
    private val mBorderReady: Drawable

    private var mPrefs: Prefs? = null
    private var mThreshold: Int = 0
    private var mLastDoubleTapTime: Long = 0
    private val mLastDoubleTapIgnoreDelay: Long
    private var mInLongPress: Boolean = false
    private var mProcessingPreview: Boolean = false
    private var mProcessingOcr: Boolean = false
    private var mScreenshotForOcr: ScreenshotForOcr? = null

    private var mCommonParser: CommonParser? = null

    private val screenshotForOcr: ScreenshotForOcr?
        get()
        {
            val viewPos = IntArray(2)
            mWindowBox.getLocationOnScreen(viewPos)
            val box = BoxParams(viewPos[0], viewPos[1], params.width, params.height)

            try
            {
                return getReadyScreenshot(box)
            } catch (e: Exception)
            {
                e.printStackTrace()
            }

            return null
        }

    private inner class ScreenshotForOcr(val crop: Bitmap?, val orig: Bitmap?, val params: BoxParams?, private var mSetThreshold: Int)
    {

        private var mCropProcessed: Bitmap? = null

        val cachedScreenshot: Bitmap?
            get()
            {
                if (mCropProcessed == null)
                {
                    mCropProcessed = getProcessedScreenshot(mSetThreshold)
                }

                return mCropProcessed
            }


        init
        {
            this.mCropProcessed = null
        }

        fun getProcessedScreenshot(threshold: Int): Bitmap
        {
            val pix = ReadFile.readBitmap(crop)
            val pixT = GrayQuant.pixThresholdToBinary(pix, threshold)

            val binarizedBitmap = WriteFile.writeBitmap(pixT)

            pix.recycle()
            pixT.recycle()

            if (mCropProcessed != null) mCropProcessed!!.recycle()
            mCropProcessed = binarizedBitmap
            mSetThreshold = threshold

            return binarizedBitmap
        }

        private fun kMeansClustering()
        {

        }

        private fun calculateFuriganaPosition(bitmap: Bitmap): Bitmap
        {
            val screen = bitmap.copy(bitmap.config, true)

            val screenHeight = screen.height
            val screenHeightHalf = (screenHeight / 2).toFloat()
            val screenWidth = screen.width

            val histogram = IntArray(screenWidth)
            val histogramBoost = FloatArray(screenWidth)

            for (x in 0 until screenWidth)
            {
                for (y in 0 until screenHeight)
                {
                    val pixel = screen.getPixel(x, y)
                    val R = pixel shr 16 and 0xff
                    val G = pixel shr  8 and 0xff
                    val B = pixel        and 0xff
                    if (!(R < 10 && G < 10 && B < 10))
                    {
                        histogram[x]++
                        histogramBoost[x] += if (y.toFloat() / screenHeight < 0.5) (screenHeightHalf - y) / screenHeightHalf else -((y - screenHeightHalf) / screenHeightHalf)
                    }
                }
            }

            // Calculate boost
            var boostTotal = 0f
            var boostAvg = 0f
            var boostMax = 0f
            // Find highest boost value
            for (x in 0 until screenWidth)
            {
                if (histogramBoost[x] > boostMax) boostMax = histogramBoost[x]
            }
            // Stretch boost by itself (higher boosts will be even higher), and normalize all boost values by boostMax
            for (x in 0 until screenWidth)
            {
                histogramBoost[x] = histogramBoost[x] * histogramBoost[x] / boostMax
            }
            // Find highest boost value
            for (x in 0 until screenWidth)
            {
                if (histogramBoost[x] > boostMax) boostMax = histogramBoost[x]
            }
            // Normalize all boost values by boostMax again
            for (x in 0 until screenWidth)
            {
                histogramBoost[x] = histogramBoost[x] / boostMax
                boostTotal += histogramBoost[x]
            }
            boostAvg = boostTotal / screenWidth

            // Calculate histogram average excluding white columns
            var averageTotal = 0
            var averageNonZero = 0
            for (i in histogram.indices)
            {
                if (histogram[i] != 0)
                {
                    averageTotal += histogram[i]
                    averageNonZero++
                }
            }
            val avg = if (averageNonZero == 0) screenHeight else averageTotal / averageNonZero
            var avgLine = screenHeight - (screenHeight - avg)
            val maxBoostTimes = screenHeight - avg
            avgLine = if (avgLine >= screenHeight) screenHeight - 1 else avgLine

            // Draw histogram
            for (x in 0 until screenWidth)
            {
                if (histogram[x] == 0)
                {
                    continue
                }

                var y: Int
                y = screenHeight - 1
                while (y >= screenHeight - (screenHeight - histogram[x]))
                {
                    screen.setPixel(x, y, screen.getPixel(x, y) and -0x3738)
                    y--
                }

                if (histogramBoost[x] > 0)
                {
                    val timesToBoost = (histogramBoost[x] * screenHeight).toInt()
                    for (i in 0 until timesToBoost)
                    {
                        if (y > 0)
                        {
                            screen.setPixel(x, y, screen.getPixel(x, y) and -0x373701)
                            y--
                        }
                    }
                }

                if (histogram[x] != screenHeight)
                {
                    while (y > 0)
                    {
                        screen.setPixel(x, y, screen.getPixel(x, y) and -0x373738)
                        y--
                    }
                }
            }

            // Draw average histogram line
            val avgLineM = if (avgLine - 1 < 0) 0 else avgLine - 1
            val avgLineP = if (avgLine + 1 > screenHeight - 1) screenHeight - 1 else avgLine + 1
            for (x in 0 until screenWidth)
            {
                screen.setPixel(x, avgLineM, Color.GREEN)
                screen.setPixel(x, avgLine, Color.GREEN)
                screen.setPixel(x, avgLineP, Color.GREEN)
            }

            return screen
        }
    }

    init
    {
        show()

        this.mCommonParser = CommonParser(context)

        mImageView = window.findViewById(R.id.capture_image)
        mFadeRepeat = AnimationUtils.loadAnimation(this.context, R.anim.fade_repeat)
        mBorderDefault = this.context.resources.getDrawable(R.drawable.bg_translucent_border_0_blue_blue, null)
        mBorderReady = this.context.resources.getDrawable(R.drawable.bg_transparent_border_0_nil_ready, null)

        mThreshold = 128
        mLastDoubleTapTime = System.currentTimeMillis()
        mLastDoubleTapIgnoreDelay = 500
        mInLongPress = false
        mProcessingPreview = false
        mProcessingOcr = false
        mScreenshotForOcr = null

        mPrefs = getPrefs(context)

        mOcr = OcrRunnable(this.context, this)
        val tessThread = Thread(mOcr)
        tessThread.name = String.format("TessThread%d", System.nanoTime())
        tessThread.isDaemon = true
        tessThread.start()

        windowManager.defaultDisplay.rotation

        // Need to wait for the view to finish updating before we try to determine it's location
        mWindowBox = window.findViewById(R.id.capture_box)
        mWindowBox.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener
        {
            override fun onGlobalLayout()
            {
                (context as MainService).onCaptureWindowFinishedInitializing()
                mWindowBox.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        //windowCoordinator.getWindow(Constants.WINDOW_HISTORY).show();
    }

    override fun reInit(options: Window.ReinitOptions)
    {
        mPrefs = getPrefs(context)
        super.reInit(options)
    }

    override fun onDoubleTap(e: MotionEvent): Boolean
    {
        mLastDoubleTapTime = System.currentTimeMillis()
        performOcr(false)

        //        try {
        //            mCommonParser.parseJmDict();
        //        } catch (Exception e1) {
        //            e1.printStackTrace();
        //        }

        return true
    }

    override fun onTouch(e: MotionEvent): Boolean
    {
        hideInstantWindows()

        if (!mInLongPress && !mProcessingOcr)
        {
            mImageView.setImageResource(0)
            setBorderStyle(e)
        }

        when (e.action)
        {
            MotionEvent.ACTION_MOVE ->
            {
                if (System.currentTimeMillis() > mLastDoubleTapTime + mLastDoubleTapIgnoreDelay)
                {
                    mOcr.cancel()
                }

                if (mInLongPress && mPrefs!!.imageFilterSetting)
                {
                    setPreviewImageForThreshold(e)
                }
            }
        }

        return super.onTouch(e)
    }

    override fun onLongPress(e: MotionEvent)
    {
        Log.d(TAG, "onLongPress")

        mInLongPress = true
        setPreviewImageForThreshold(e)
    }

    override fun onResize(e: MotionEvent): Boolean
    {
        hideInstantWindows()

        mOcr.cancel()
        mImageView.setImageResource(0)
        setBorderStyle(e)
        return super.onResize(e)
    }

    override fun onUp(e: MotionEvent): Boolean
    {

        Log.d(TAG, String.format("onUp - mImageFilterSetting: %b | mInLongPress: %b | mProcessingPreview: %b | mProcessingOcr: %b", mPrefs!!.imageFilterSetting, mInLongPress, mProcessingPreview, mProcessingOcr))

        if (!mInLongPress && !mProcessingPreview && !mProcessingOcr)
        {
            Log.d(TAG, "onUp - SetPreviewImage")
            setBorderStyle(e)
            mProcessingPreview = true
            setCroppedScreenshot()
        }

        mInLongPress = false

        return true
    }

    override fun stop()
    {
        mOcr.stop()
        //windowCoordinator.getWindow(Constants.WINDOW_HISTORY).hide();
        super.stop()
    }

    fun showLoadingAnimation()
    {
        (context as MainService).handler.post {
            Log.d(TAG, "showLoadingAnimation")

            mWindowBox.background = mBorderDefault
            mImageView.imageAlpha = 0
            mWindowBox.animation = mFadeRepeat
            mWindowBox.startAnimation(mFadeRepeat)
        }
    }

    fun stopLoadingAnimation(instant: Boolean)
    {
        (context as MainService).handler.post {
            mProcessingOcr = false
            mWindowBox.background = mBorderReady
            mWindowBox.clearAnimation()
            Log.d(TAG, "stopLoadingAnimation - instant: $instant")
            if (instant)
            {
                mImageView.imageAlpha = 255
            } else
            {
                mImageView.imageAlpha = 255
                mImageView.setImageResource(0)
                mScreenshotForOcr = null
            }
        }
    }

    fun hideInstantWindows()
    {
        windowCoordinator.getWindow(WINDOW_INSTANT_KANJI).hide()
    }

    override fun getDefaultParams(): WindowManager.LayoutParams
    {
        val params = super.getDefaultParams()
        params.x = realDisplaySize.x / 2 - params.width / 2
        params.y = realDisplaySize.y / 4 - params.height / 2
        return params
    }

    private fun setPreviewImageForThreshold(e: MotionEvent)
    {
        if (mPrefs!!.imageFilterSetting && mScreenshotForOcr != null)
        {
            mThreshold = (e.rawX / realDisplaySize.x * 255).toInt()
            val bitmap = mScreenshotForOcr!!.getProcessedScreenshot(mThreshold)
            mImageView.setImageBitmap(bitmap)
        }
    }

    private fun setCroppedScreenshot()
    {
        val thread = Thread(Runnable {
            val ocrScreenshot = screenshotForOcr

            if (ocrScreenshot == null || ocrScreenshot.crop == null || ocrScreenshot.orig == null || ocrScreenshot.params == null)
            {
                mProcessingPreview = false
                return@Runnable
            }

            // Generate a cached screenshot in worker thread before setting it in the UI thread
            ocrScreenshot.cachedScreenshot

            (context as MainService).handler.post {
                mScreenshotForOcr = ocrScreenshot

                if (mPrefs!!.imageFilterSetting)
                {
                    mImageView.setImageBitmap(mScreenshotForOcr!!.cachedScreenshot)
                }

                if (mPrefs!!.instantModeSetting && System.currentTimeMillis() > mLastDoubleTapTime + mLastDoubleTapIgnoreDelay)
                {
                    val sizeForInstant = minSize * 3
                    if (sizeForInstant >= mScreenshotForOcr!!.params!!.width || sizeForInstant >= mScreenshotForOcr!!.params!!.height)
                    {
                        performOcr(true)
                    }
                }

                mProcessingPreview = false
            }
        })
        thread.start()
    }

    private fun setBorderStyle(e: MotionEvent)
    {
        when (e.action)
        {
            MotionEvent.ACTION_DOWN -> mWindowBox.background = mBorderDefault
            MotionEvent.ACTION_UP -> mWindowBox.background = mBorderReady
        }
    }

    private fun performOcr(instant: Boolean)
    {
        mProcessingOcr = true

        try
        {
            if (!instant)
            {
                while (!mOcr.isReadyForOcr)
                {
                    mOcr.cancel()
                    Thread.sleep(10)
                }
            }

            if (mScreenshotForOcr == null)
            {
                mProcessingOcr = false
                return
            }

            val processedImage = if (mPrefs!!.imageFilterSetting) mScreenshotForOcr!!.cachedScreenshot else mScreenshotForOcr!!.crop

            var textDirection = mPrefs!!.textDirectionSetting
            if (textDirection === TextDirection.AUTO)
            {
                textDirection = if (mScreenshotForOcr!!.params!!.width >= mScreenshotForOcr!!.params!!.height) TextDirection.HORIZONTAL else TextDirection.VERTICAL
            }

            mOcr.runTess(OcrParams(processedImage!!, mScreenshotForOcr!!.crop!!, mScreenshotForOcr!!.params!!, textDirection, instant))
        } catch (e: Exception)
        {
            e.printStackTrace()
        }

    }

    @Throws(Exception::class)
    private fun getReadyScreenshot(box: BoxParams): ScreenshotForOcr?
    {
        Log.d(TAG, String.format("X:%d Y:%d (%dx%d)", box.x, box.y, box.width, box.height))

        var screenshotReady: Boolean
        val startTime = System.nanoTime()
        var screenshot: Bitmap

        do
        {

            val rawScreenshot = (context as MainService).screenshot
            if (rawScreenshot == null)
            {
                Log.d(TAG, "getReadyScreenshot - rawScreenshot null")
                return null
            }

            screenshot = convertImageToBitmap(rawScreenshot)
            screenshotReady = checkScreenshotIsReady(screenshot, box)

            val viewPos = IntArray(2)
            mWindowBox.getLocationOnScreen(viewPos)
            box.x = viewPos[0]
            box.y = viewPos[1]
            box.width = params.width
            box.height = params.height

        } while (!screenshotReady && System.nanoTime() < startTime + 4000000000L)

        val croppedBitmap = getCroppedBitmap(screenshot, box)

        //saveBitmap(screenshot, String.format("debug_(%d,%d)_(%d,%d)", box.x, box.y, box.width, box.height));
        if (!screenshotReady)
        {
            saveBitmap(screenshot, String.format("error_(%d,%d)_(%d,%d)", box.x, box.y, box.width, box.height))
            saveBitmap(croppedBitmap, String.format("error_(%d,%d)_(%d,%d)", box.x, box.y, box.width, box.height))
            return null
        }

        return ScreenshotForOcr(croppedBitmap, screenshot, box, mThreshold)
    }

    private fun checkScreenshotIsReady(screenshot: Bitmap, box: BoxParams): Boolean
    {
        var readyColor = ContextCompat.getColor(context, R.color.red_capture_window_ready)
        val screenshotColor = screenshot.getPixel(box.x, box.y)

        if (readyColor != screenshotColor && isAcceptableAlternateReadyColor(screenshotColor))
        {
            readyColor = screenshotColor
        }

        for (x in box.x until box.x + box.width)
        {
            if (!isRGBWithinTolorance(readyColor, screenshot.getPixel(x, box.y)))
            {
                return false
            }
        }

        for (x in box.x until box.x + box.width)
        {
            if (!isRGBWithinTolorance(readyColor, screenshot.getPixel(x, box.y + box.height - 1)))
            {
                return false
            }
        }

        for (y in box.y until box.y + box.height)
        {
            if (!isRGBWithinTolorance(readyColor, screenshot.getPixel(box.x, y)))
            {
                return false
            }
        }

        for (y in box.y until box.y + box.height)
        {
            if (!isRGBWithinTolorance(readyColor, screenshot.getPixel(box.x + box.width - 1, y)))
            {
                return false
            }
        }

        return true
    }

    /**
     * Looks like sometimes the screenshot just has a color that is 100% totally wrong. Let's just accept any red that's "red enough"
     * @param screenshotColor
     * @return
     */
    private fun isAcceptableAlternateReadyColor(screenshotColor: Int): Boolean
    {
        val R = screenshotColor shr 16 and 0xFF
        val G = screenshotColor shr 8 and 0xFF
        val B = screenshotColor and 0xFF

        var isValid = true

        if (G * 10 > R)
        {
            isValid = false
        }

        if (B * 10 > R)
        {
            isValid = false
        }

        return isValid
    }

    private fun isRGBWithinTolorance(color: Int, colorToCheck: Int): Boolean
    {
        var isColorWithinTolorance = true

        isColorWithinTolorance = isColorWithinTolorance and isColorWithinTolorance(color and 0xFF, colorToCheck and 0xFF)
        isColorWithinTolorance = isColorWithinTolorance and isColorWithinTolorance(color shr 8 and 0xFF, colorToCheck shr 8 and 0xFF)
        isColorWithinTolorance = isColorWithinTolorance and isColorWithinTolorance(color shr 16 and 0xFF, colorToCheck shr 16 and 0xFF)

        return isColorWithinTolorance
    }

    private fun isColorWithinTolorance(color: Int, colorToCheck: Int): Boolean
    {
        return color - 2 <= colorToCheck && colorToCheck <= color + 2
    }

    @Throws(OutOfMemoryError::class)
    private fun convertImageToBitmap(image: Image): Bitmap
    {
        val planes = image.planes
        val buffer = planes[0].buffer
        val pixelStride = planes[0].pixelStride
        val rowStride = planes[0].rowStride
        val rowPadding = rowStride - pixelStride * image.width

        val bitmap = Bitmap.createBitmap(image.width + rowPadding / pixelStride, image.height, Bitmap.Config.ARGB_8888)
        bitmap.copyPixelsFromBuffer(buffer)
        image.close()

        return bitmap
    }

    private fun getCroppedBitmap(screenshot: Bitmap, box: BoxParams): Bitmap
    {
        val borderSize = dpToPx(context, 1) + 1 // +1 due to rounding errors
        return Bitmap.createBitmap(screenshot, box.x + borderSize,
                box.y + borderSize,
                box.width - 2 * borderSize,
                box.height - 2 * borderSize)
    }

    @Throws(IOException::class)
    private fun saveBitmap(bitmap: Bitmap, name: String)
    {
        val fs = String.format("%s/%s/%s_%d.png", context.filesDir.absolutePath, SCREENSHOT_FOLDER_NAME, name, System.nanoTime())
        Log.d(TAG, fs)
        val fos = FileOutputStream(fs)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.close()
    }

    companion object
    {

        private val TAG = CaptureWindow::class.java.name
    }
}
