package ca.fuwafuwa.kaku.Dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import ca.fuwafuwa.kaku.KAKU_PREF_FILE
import ca.fuwafuwa.kaku.KAKU_PREF_PLAY_STORE_RATED

class FeedbackDialogFragment : DialogFragment()
{
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
    {
        return activity?.let {

            val builder = AlertDialog.Builder(it)

            builder.setTitle("Thanks for your feedback!")
                    .setMessage("Do you have anything you wish to say to the developer about Kaku? Bugs, feature requests, annoyances, anything goes!")
                    .setPositiveButton("SEND EMAIL")
                    {
                        _, _ ->
                        run {
                            val emailIntent = Intent(android.content.Intent.ACTION_SENDTO)
                            emailIntent.type = "text/plain"
                            emailIntent.data = Uri.parse("mailto:")
                            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, arrayOf("0xbad1d3a5@gmail.com"))
                            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Kaku Feedback - ${android.os.Build.BRAND + " " + android.os.Build.MODEL}")

                            startActivity(Intent.createChooser(emailIntent, "Send e-mail using..."))
                        }
                    }
                    .setNegativeButton("CANCEL")
                    {
                        _, _ ->
                        run {
                        }
                    }

            builder.create()

        } ?: throw IllegalStateException("Activity cannot be null")
    }
}