package ca.fuwafuwa.kaku

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class TutorialEndFragment : Fragment()
{
    private lateinit var rootView : View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        rootView = inflater.inflate(R.layout.fragment_end, container, false)

        val button = rootView.findViewById<Button>(R.id.tutorial_end_start_kaku)

        button.setOnClickListener {
            val prefs = context!!.getSharedPreferences(KAKU_PREF_FILE, Context.MODE_PRIVATE)
            prefs.edit().putBoolean(KAKU_PREF_FIRST_LAUNCH, false).apply()

            startActivity(Intent(activity, MainActivity::class.java))
            activity!!.finish()
        }

        return rootView
    }

    companion object
    {
        fun newInstance() : TutorialEndFragment {
            return TutorialEndFragment()
        }
    }
}