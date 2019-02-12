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
import android.widget.ProgressBar
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView

class StartFragment : Fragment()
{
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        val mainActivity = activity as MainActivity
        val rootView = inflater.inflate(R.layout.fragment_start, container, false)
        val adView = rootView.findViewById<AdView>(R.id.adView)
        val progressBar = rootView.findViewById<ProgressBar>(R.id.progress_bar)

        if (mainActivity.kakuAlreadyStarted)
        {
            progressBar.isIndeterminate = false
            progressBar.progress = 100
        }

        adView.adListener = object: AdListener()
        {
            override fun onAdLoaded()
            {
                super.onAdLoaded()
                mainActivity.startKakuService(progressBar)
            }

            override fun onAdFailedToLoad(p0: Int)
            {
                super.onAdFailedToLoad(p0)
                mainActivity.startKakuService(progressBar)
            }
        }

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        return rootView
    }

    companion object
    {
        fun newInstance() : StartFragment {
            return StartFragment()
        }
    }
}
