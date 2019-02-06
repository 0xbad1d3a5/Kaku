package ca.fuwafuwa.kaku.Windows

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.Build
import android.util.Log
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.ImageView

import ca.fuwafuwa.kaku.R
import ca.fuwafuwa.kaku.Windows.Data.DisplayDataOcr
import ca.fuwafuwa.kaku.Windows.Data.ISquareChar
import ca.fuwafuwa.kaku.Windows.Data.SquareCharOcr
import ca.fuwafuwa.kaku.Windows.Interfaces.IRecalculateKanjiViews
import ca.fuwafuwa.kaku.Windows.Views.ChoiceEditText

/**
 * Created by 0xbad1d3a5 on 4/23/2016.
 */
class EditWindow(context: Context, windowCoordinator: WindowCoordinator) : Window(context, windowCoordinator, R.layout.window_edit), ChoiceEditText.InputDoneListener
{
    private val mChoiceEditText: ChoiceEditText = window.findViewById(R.id.edit_text)

    private lateinit var mCallback: IRecalculateKanjiViews
    private lateinit var mSquareChar: ISquareChar

    init
    {
        mChoiceEditText.setInputDoneCallback(this)
    }

    override fun onTouch(e: MotionEvent): Boolean
    {
        return false
    }

    override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean
    {
        return false
    }

    override fun onResize(e: MotionEvent): Boolean
    {
        return false
    }

    override fun show()
    {
        super.show()
        mChoiceEditText.setText("")
        mChoiceEditText.showKeyboard()
    }

    override fun onEditTextInputDone(input: String?)
    {
        if (input != null && !input.trim { it <= ' ' }.isEmpty())
        {
            mSquareChar.text = input

            mCallback.recalculateKanjiViews()
        }

        hide()
    }

    /**
     * EditWindow does not need to reInit layout as its getDefaultParams() are all relative. Re-initing will cause bugs.
     */
    override fun reInit(options: Window.ReinitOptions)
    {
        options.reinitViewLayout = false
        super.reInit(options)
    }

    fun setInputDoneCallback(callback: IRecalculateKanjiViews)
    {
        mCallback = callback
    }

    fun setInfo(squareChar: ISquareChar)
    {
        mSquareChar = squareChar
        val displayData = squareChar.displayData

        if (displayData is DisplayDataOcr && squareChar is SquareCharOcr)
        {
            val pos = squareChar.bitmapPos

            var orig = displayData.bitmap
            orig = orig.copy(orig.config, true)

            var width = pos[2] - pos[0]
            var height = pos[3] - pos[1]
            var xPos = pos[0]
            var yPos = pos[1]

            Log.d(TAG, "Initial | Orig: (${orig.width}x${orig.height}) Box: ($xPos, $yPos) (${width}x${height})")

            width = if (width <= 0) 1 else width
            height = if (height <= 0) 1 else height

            val wPadding = ((width * 1.5).toInt() - width) / 2
            val hPadding = ((height * 1.5).toInt() - height) / 2

            xPos -= wPadding
            yPos -= hPadding
            width += wPadding * 2
            height += hPadding * 2

            if (xPos < 0) xPos = 0
            if (yPos < 0) yPos = 0
            if (width + xPos > orig.width) width = orig.width - xPos - 1
            if (height + yPos > orig.height) height = orig.height - yPos - 1

            Log.d(TAG, "After | Orig: (${orig.width}x${orig.height}) Box: ($xPos, $yPos) (${width}x${height})")

            for (xTop in xPos until width + xPos)
            {
                orig.setPixel(xTop, yPos, Color.RED)
            }
            for (xBottom in xPos until width + xPos)
            {
                orig.setPixel(xBottom, yPos + height, Color.RED)
            }
            for (yLeft in yPos until height + yPos)
            {
                orig.setPixel(xPos, yLeft, Color.RED)
            }
            for (yRight in yPos until height + yPos)
            {
                orig.setPixel(xPos + width, yRight, Color.RED)
            }
            orig.setPixel(xPos + width, yPos + height, Color.RED)

            val avgSize = calculateMedianSquareSize(squareChar)
            val avgWidth = avgSize.x
            val avgHeight = avgSize.y

            xPos -= avgWidth * 6
            yPos -= avgHeight * 6
            width += avgWidth * 12
            height += avgHeight * 12

            if (xPos < 0) xPos = 0
            if (yPos < 0) yPos = 0
            if (width + xPos > orig.width) width = orig.width - xPos
            if (height + yPos > orig.height) height = orig.height - yPos

            val bitmapChar = Bitmap.createBitmap(orig, xPos, yPos, width, height)

            val iv = window.findViewById<ImageView>(R.id.edit_kanji_image)
            iv.setImageBitmap(bitmapChar)
        }
        else {
            val iv = window.findViewById<ImageView>(R.id.edit_kanji_image)
            iv.setBackgroundColor(0x44000000)
            return
        }
    }

    private fun calculateMedianSquareSize(squareChar: SquareCharOcr) : Point
    {
        val squareChars = (squareChar.displayData.squareChars as List<SquareCharOcr>).toList()
        val widths = squareChars.map { it.bitmapPos[2] - it.bitmapPos[0] }.sorted()
        val heights = squareChars.map { it.bitmapPos[3] - it.bitmapPos[1] }.sorted()

        return Point(widths[widths.size / 2], heights[heights.size / 2])
    }

    /**
     * We need to override here because we need cannot have the FLAG_NOT_FOCUSABLE flag set in [Window.getDefaultParams]
     */
    override fun getDefaultParams(): WindowManager.LayoutParams
    {
        val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                if (Build.VERSION.SDK_INT > 25) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE,
                0,
                PixelFormat.TRANSLUCENT)
        params.x = 0
        params.y = 0
        return params
    }

    companion object
    {

        val TAG = EditWindow::class.java.name
    }
}
