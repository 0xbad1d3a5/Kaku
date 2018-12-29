package ca.fuwafuwa.kaku

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class StartFragment : Fragment()
{
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        val rootView = inflater.inflate(R.layout.fragment_start, container, false)

        val closeButton = rootView.findViewById(R.id.close_button) as Button

        closeButton.setOnClickListener { activity?.stopService(Intent(activity, MainService::class.java)) }

        return rootView
    }

    companion object
    {
        fun newInstance() : StartFragment {
            return StartFragment()
        }
    }
}
