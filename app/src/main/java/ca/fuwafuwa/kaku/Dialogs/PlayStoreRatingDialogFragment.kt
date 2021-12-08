package ca.fuwafuwa.kaku.Dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.widget.Toast
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import ca.fuwafuwa.kaku.KAKU_PREF_FILE
import ca.fuwafuwa.kaku.KAKU_PREF_PLAY_STORE_RATED


class PlayStoreRatingDialogFragment : DialogFragment()
{
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
    {
        return activity?.let {

            val builder = AlertDialog.Builder(it)
            val prefs = requireContext().getSharedPreferences(KAKU_PREF_FILE, Context.MODE_PRIVATE)

            builder.setTitle("Thanks for your feedback!")
                    .setMessage("Would you like to rate the app in the Play Store? It would mean a lot to me and helps support the app!")
                    .setPositiveButton("OK")
                    {
                        _, _ ->
                        run {
                            launchMarket()
                            prefs.edit().putBoolean(KAKU_PREF_PLAY_STORE_RATED, true).apply()
                        }
                    }
                    .setNegativeButton("NO")
                    {
                        _, _ ->
                        run {
                        }
                    }
                    .setNeutralButton("NEVER ASK AGAIN")
                    {
                        _, _ ->
                        run {
                            prefs.edit().putBoolean(KAKU_PREF_PLAY_STORE_RATED, true).apply()
                        }
                    }

            builder.create()

        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun launchMarket()
    {
        val uri = Uri.parse("market://details?id=" + requireActivity().packageName)
        val linkToMarket = Intent(Intent.ACTION_VIEW, uri)

        try
        {
            startActivity(linkToMarket)
        } catch (e: ActivityNotFoundException)
        {
            Toast.makeText(requireContext(), "Unable to launch Play Store", Toast.LENGTH_LONG).show()
        }
    }
}