package ca.fuwafuwa.kaku

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.VideoView
import androidx.fragment.app.Fragment

class InstructionFragment : Fragment()
{
    lateinit var mVideoView: VideoView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val rootView = inflater.inflate(R.layout.fragment_instruction, container, false)

        mVideoView = rootView.findViewById(R.id.instruction_video_view) as VideoView
        val pos = arguments?.getInt(ARG_SECTION_NUMBER)!!
        Log.d(TAG, "Video View for fragment $pos created")

        mVideoView.setVideoURI(Uri.parse("android.resource://ca.fuwafuwa.kaku/${getVideoForSectionNumber(pos)}"))
        mVideoView.setOnPreparedListener { it.isLooping = true }
        mVideoView.start()

        val mTitleText = rootView.findViewById(R.id.instruction_title_text) as TextView
        mTitleText.text = getTitleTextForSectionNumber(pos)

        val mText = rootView.findViewById(R.id.instruction_text) as TextView
        mText.text = getTextForSectionNumber(pos)

        return rootView
    }

    private fun getVideoForSectionNumber(num: Int): Int
    {
        val pos = num - 1
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
        val pos = num - 1
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
            7 -> return "Slide to the upper-left to perform a manual substitution"
            8 -> return "Slide to the upper-right to delete a character"
            9 -> return "Select text in any app then tap Kaku for the dictionary"
        }

        return ""
    }

    companion object
    {
        private val TAG = InstructionFragment::class.java.name
        private val ARG_SECTION_NUMBER = "section_number"

        fun newInstance(sectionNumber: Int): InstructionFragment
        {
            val fragment = InstructionFragment()
            val args = Bundle()
            args.putInt(ARG_SECTION_NUMBER, sectionNumber)
            fragment.arguments = args
            return fragment
        }
    }
}