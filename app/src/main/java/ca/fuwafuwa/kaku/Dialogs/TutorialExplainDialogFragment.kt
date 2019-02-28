package ca.fuwafuwa.kaku.Dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment


class TutorialExplainDialogFragment : DialogFragment()
{
    private lateinit var mTitle : String
    private lateinit var mMessage : String

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
    {
        mTitle = arguments?.getString(ARG_TITLE)!!
        mMessage = arguments?.getString(ARG_MESSAGE)!!

        return activity?.let {

            val builder = AlertDialog.Builder(it)

            builder.setTitle(mTitle)
                    .setMessage(mMessage)
                    .setPositiveButton("OK")
                    {
                        _, _ ->
                        run {
                        }
                    }

            builder.create()

        } ?: throw IllegalStateException("Activity cannot be null")
    }

    companion object
    {
        private val ARG_TITLE = "arg_title"
        private val ARG_MESSAGE = "arg_message"

        fun newInstance(title: String, message: String) : TutorialExplainDialogFragment
        {
            val dialog = TutorialExplainDialogFragment()
            val args = Bundle()
            args.putString(ARG_TITLE, title)
            args.putString(ARG_MESSAGE, message)
            dialog.arguments = args
            return dialog
        }
    }
}