package ca.fuwafuwa.kaku

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.LinearLayout
import android.widget.VideoView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import ca.fuwafuwa.kaku.Dialogs.TutorialExplainDialogFragment

class TutorialFragment : Fragment()
{
    private lateinit var mRootView : View
    private lateinit var mVideoView : VideoView
    private lateinit var mButtonLayout : LinearLayout
    private lateinit var mExplainButton: Button
    private var mPos : Int = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        mRootView = inflater.inflate(R.layout.fragment_tutorial, container, false)

        mVideoView = mRootView.findViewById(R.id.instruction_video_view) as VideoView
        mButtonLayout = mRootView.findViewById(R.id.tutorial_buttons) as LinearLayout
        mExplainButton = mRootView.findViewById(R.id.tutorial_button_explain)

        mPos = arguments?.getInt(ARG_SECTION_NUMBER)!!

        mExplainButton.setOnClickListener {
            getExplainDialogForFragment(mPos).show(fragmentManager!!, "ExplainDialog$mPos")
        }

        Log.d(TAG, "onCreateView $mPos")

        return mRootView
    }

    override fun onResume()
    {
        super.onResume()

        mVideoView.setVideoURI(Uri.parse("android.resource://ca.fuwafuwa.kaku/${getVideoForSectionNumber(mPos)}"))
        mVideoView.setOnPreparedListener { it.isLooping = true }
        mVideoView.start()
    }

    override fun onStart()
    {
        super.onStart()

        mButtonLayout.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener
        {
            override fun onGlobalLayout()
            {
                val drawableHeight = mButtonLayout.y.toInt()

                mVideoView.layoutParams.height = drawableHeight - dpToPx(context!!, 20) // TODO: NRE here, probably LayoutParams?
                mVideoView.requestLayout()

                mButtonLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    private fun getExplainDialogForFragment(num: Int) : DialogFragment
    {
        return TutorialExplainDialogFragment.newInstance(getTitleTextForSectionNumber(num), getTextForSectionNumber(num))
    }

    private fun getVideoForSectionNumber(num: Int): Int
    {
        when (num){
            1 -> return R.raw.tut1
            2 -> return R.raw.tut2
            3 -> return R.raw.tut3
            4 -> return R.raw.tut4
            5 -> return R.raw.tut5
            6 -> return R.raw.tut6
            7 -> return R.raw.tut7
            8 -> return R.raw.tut8
            9 -> return R.raw.tut9
        }

        return 0
    }

    private fun getTitleTextForSectionNumber(num: Int): String
    {
        when (num){
            1 -> return "BASIC USAGE"
            2 -> return "INSTANT MODE"
            3 -> return "QUICK IMAGE ACTION - FILTER"
            4 -> return "QUICK TEXT ACTION - SWAP"
            5 -> return "QUICK TEXT ACTION - EDIT"
            6 -> return "QUICK TEXT ACTION - DELETE"
            7 -> return "SEND TO GOOGLE TRANSLATE"
            8 -> return "NOTIFICATION CONTROLS"
            9 -> return "SELECT TO LOOKUP"
        }

        return ""
    }

    private fun getTextForSectionNumber(num: Int): String
    {
        when (num){
            1 -> return "Drag the capture window to move the window. Drag the bottom right corner to resize. Double tap to start OCR and recognize text. Tip: resize area is inside the capture window."
            2 -> return "If instant mode is turned on in the settings and the capture window is fairly small, OCR will start immediately. This mode was intended to recognize words, not sentences."
            3 -> return "If the background of the text you want to recognize is translucent, you can try adjusting the image filter settings by doing a long press, then dragging left or right. Note: image filter setting must be turned on."
            4 -> return "Sometimes Kaku misrecognizes the kanji but can be easily corrected. Perform a quick swipe downward on the kanji for possible alternate recognitions."
            5 -> return "In the case that the correct kanji was not present in the swap quick action, perform a quick swipe to the upper-left to manually input the kanji. For manual correction, you must have a handwriting keyboard installed - for example, Gboard w/ Japanese Handwriting by Google."
            6 -> return "If you need to delete any extraneous characters, swipe to the upper right. For all text quick actions, the swipe direction may be reversed in instant mode when there is not enough screen space."
            7 -> return "Tap and hold on any kanji to copy recognized text to the clipboard. If you have \"Tap to Translate\" enabled in the Google Translate app, that will also be brought up."
            8 -> return "Quickly show/hide Kaku or change Kaku's settings through the notification."
            9 -> return "In the case that you can select the text and don't need OCR, simply select the text and send it to Kaku to bring up the dictionary."
        }

        return ""
    }

    companion object
    {
        private val TAG = TutorialFragment::class.java.name
        private val ARG_SECTION_NUMBER = "section_number"

        fun newInstance(sectionNumber: Int): TutorialFragment
        {
            val fragment = TutorialFragment()
            val args = Bundle()
            args.putInt(ARG_SECTION_NUMBER, sectionNumber)
            fragment.arguments = args
            return fragment
        }
    }
}