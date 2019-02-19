package ca.fuwafuwa.kaku

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class TutorialWelcomeFragment : Fragment()
{
    private lateinit var rootView : View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        rootView = inflater.inflate(R.layout.fragment_welcome, container, false)

        return rootView
    }

    companion object
    {
        fun newInstance() : TutorialWelcomeFragment {
            return TutorialWelcomeFragment()
        }
    }
}