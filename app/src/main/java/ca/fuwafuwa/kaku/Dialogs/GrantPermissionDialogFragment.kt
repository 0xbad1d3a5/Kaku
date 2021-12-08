package ca.fuwafuwa.kaku.Dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import ca.fuwafuwa.kaku.KAKU_PREF_FILE
import ca.fuwafuwa.kaku.KAKU_PREF_FIRST_LAUNCH
import ca.fuwafuwa.kaku.MainActivity

class GrantPermissionDialogFragment : DialogFragment()
{
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
    {
        return activity?.let {

            val builder = AlertDialog.Builder(it)

            builder.setTitle("Grant Kaku Permissions")
                    .setMessage("Kaku uses optical character recognition (OCR) to detect text from images and works by automatically taking screenshots of your screen when active. After granting permissions, please restart Kaku.\n\nKaku works completely offline and WILL NEVER transmit ANY user data encountered during usage.")
                    .setPositiveButton("GRANT")
                    {
                        _, _ ->
                        run {
                            val prefs = context!!.getSharedPreferences(KAKU_PREF_FILE, Context.MODE_PRIVATE)
                            prefs.edit().putBoolean(KAKU_PREF_FIRST_LAUNCH, false).apply()

                            startActivity(Intent(activity, MainActivity::class.java))
                            (activity as FragmentActivity).finish()
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