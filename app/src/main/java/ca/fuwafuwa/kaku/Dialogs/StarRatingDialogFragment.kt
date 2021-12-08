package ca.fuwafuwa.kaku.Dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import ca.fuwafuwa.kaku.KAKU_PREF_FILE
import ca.fuwafuwa.kaku.KAKU_PREF_PLAY_STORE_RATED
import ca.fuwafuwa.kaku.R

class StarRatingDialogFragment : DialogFragment()
{
    private var rating = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
    {
        return activity?.let {

            val builder = AlertDialog.Builder(it)

            val inflater = requireActivity().layoutInflater;
            val view = inflater.inflate(R.layout.dialog_rating_stars, null)

            val star1 = view.findViewById<TextView>(R.id.dialog_rating_star1)
            val star2 = view.findViewById<TextView>(R.id.dialog_rating_star2)
            val star3 = view.findViewById<TextView>(R.id.dialog_rating_star3)
            val star4 = view.findViewById<TextView>(R.id.dialog_rating_star4)
            val star5 = view.findViewById<TextView>(R.id.dialog_rating_star5)

            star1.setOnClickListener {
                star1.text = "★"
                star2.text = "☆"
                star3.text = "☆"
                star4.text = "☆"
                star5.text = "☆"
                rating = 1
            }

            star2.setOnClickListener {
                star1.text = "★"
                star2.text = "★"
                star3.text = "☆"
                star4.text = "☆"
                star5.text = "☆"
                rating = 2
            }

            star3.setOnClickListener {
                star1.text = "★"
                star2.text = "★"
                star3.text = "★"
                star4.text = "☆"
                star5.text = "☆"
                rating = 3
            }

            star4.setOnClickListener {
                star1.text = "★"
                star2.text = "★"
                star3.text = "★"
                star4.text = "★"
                star5.text = "☆"
                rating = 4
            }

            star5.setOnClickListener {
                star1.text = "★"
                star2.text = "★"
                star3.text = "★"
                star4.text = "★"
                star5.text = "★"
                rating = 5
            }

            builder.setTitle("What do you think of Kaku?")
                    .setView(view)
                    .setPositiveButton("Ok")
                    {
                        _, _ ->
                        run {
                            if (rating == 5)
                            {
                                PlayStoreRatingDialogFragment().show(requireActivity().supportFragmentManager, "PlayStoreRating")
                            } else
                            {
                                val prefs = requireContext().getSharedPreferences(KAKU_PREF_FILE, Context.MODE_PRIVATE)
                                prefs.edit().putBoolean(KAKU_PREF_PLAY_STORE_RATED, true).apply()

                                FeedbackDialogFragment().show(requireActivity().supportFragmentManager, "Feedback")
                            }
                        }
                    }
                    .setNegativeButton("Cancel")
                    {
                        _, _ ->
                        run {
                        }
                    }

            builder.create()

        } ?: throw IllegalStateException("Activity cannot be null")
    }
}