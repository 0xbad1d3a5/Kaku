package ca.fuwafuwa.kaku

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import android.widget.VideoView
import androidx.fragment.app.Fragment

class TutorialFragment : Fragment()
{
    private lateinit var mRootView : View
    private lateinit var mVideoView : VideoView
    private lateinit var mTutorialButtons : LinearLayout
    private var mPos : Int = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        mRootView = inflater.inflate(R.layout.fragment_tutorial, container, false)

        mVideoView = mRootView.findViewById(R.id.instruction_video_view) as VideoView
        mTutorialButtons = mRootView.findViewById(R.id.tutorial_buttons) as LinearLayout

        mPos = arguments?.getInt(ARG_SECTION_NUMBER)!!

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

        mTutorialButtons.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener
        {
            override fun onGlobalLayout()
            {
                val drawableHeight = mTutorialButtons.y.toInt()

                mVideoView.layoutParams.height = drawableHeight - dpToPx(context!!, 20)
                mVideoView.requestLayout()

                mTutorialButtons.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    private fun getVideoForSectionNumber(num: Int): Int
    {
        val pos = num
        when (pos){
            1 -> return R.raw.tut1_drag_to_move
            2 -> return R.raw.tut2_drag_to_resize
            3 -> return R.raw.tut3_holddrag_to_set_threshold
            4 -> return R.raw.tut4_doubletap_to_ocr
            5 -> return R.raw.tut5_tap_to_lookup
            6 -> return R.raw.tut6_slide_to_swap
            7 -> return R.raw.tut7_slide_to_edit
            8 -> return R.raw.tut8_slide_to_delete
            9 -> return R.raw.tut9_select_to_lookup
        }

        return 0
    }

    private fun getTitleTextForSectionNumber(num: Int): String
    {
        val pos = num
        when (pos){
            1 -> return "DRAG TO MOVE"
            2 -> return "DRAG TO RESIZE"
            3 -> return "HOLD THEN DRAG"
            4 -> return "DOUBLE TAP OCR"
            5 -> return "TAP TO LOOKUP"
            6 -> return "SLIDE TO SWAP"
            7 -> return "SLIDE TO EDIT"
            8 -> return "SLIDE TO DELETE"
            9 -> return "SELECT TO LOOKUP"
        }

        return ""
    }

    private fun getTextForSectionNumber(num: Int): String
    {
        val pos = num - 1
        when (pos){
            1 -> return "Drag the capture window around to move the window"
            2 -> return "Drag the bottom right corner of the capture window to resize"
            3 -> return "To change the threshold value, hold then drag left and right"
            4 -> return "Perform OCR with a double tap"
            5 -> return "Tap a character in the dictionary window to lookup the meaning"
            6 -> return "Slide down to swap characters if OCR was wrong but almost had it"
            7 -> return "Slide top-left to edit - Gboard handwriting keyboard recommended"
            8 -> return "Slide top-right to delete a character"
            9 -> return "Select text in any app then tap Kaku for the dictionary"
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