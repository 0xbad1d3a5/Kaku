package ca.fuwafuwa.kaku

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import java.util.*

class MainStartFragment : Fragment()
{
    private lateinit var mainActivity : MainActivity
    private lateinit var rootView : View

    private lateinit var kakuLogo : TextView
    private lateinit var kakuTitle : TextView
    private lateinit var tutorialText : TextView

    private lateinit var supportText : TextView
    private lateinit var progressBar : ProgressBar
    private lateinit var adView : AdView

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(requireActivity(), resources.getString(R.string.ads_app_id))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        mainActivity = activity as MainActivity

        rootView = inflater.inflate(R.layout.fragment_start, container, false)

        kakuLogo = rootView.findViewById(R.id.kaku_logo)
        kakuTitle = rootView.findViewById(R.id.kaku_title)
        tutorialText = rootView.findViewById(R.id.kaku_tutorial)

        supportText = rootView.findViewById(R.id.support_text)
        progressBar = rootView.findViewById(R.id.progress_bar)
        adView = rootView.findViewById(R.id.adView)

        if (MainService.IsRunning())
        {
            progressBar.isIndeterminate = false
            progressBar.progress = 100
            supportText.text = getString(R.string.support_text)
        }

        adView.adListener = object: AdListener()
        {
            override fun onAdLoaded()
            {
                super.onAdLoaded()
                mainActivity.startKaku(progressBar, supportText)
            }

            override fun onAdFailedToLoad(p0: Int)
            {
                super.onAdFailedToLoad(p0)
                mainActivity.startKaku(progressBar, supportText)
            }
        }

        tutorialText.setOnClickListener {
            startActivity(Intent(mainActivity, TutorialActivity::class.java))
        }

        return rootView
    }

    override fun onStart()
    {
        super.onStart()

        supportText.viewTreeObserver.addOnGlobalLayoutListener {
            var pos = IntArray(2)
            supportText.getLocationInWindow(pos)
            val drawableHeight = rootView.height - pos[1]

            val logoSize = drawableHeight.toFloat() / 2
            val titleSize = logoSize / 5
            val textSize = titleSize / 2

            kakuLogo.setTextSize(TypedValue.COMPLEX_UNIT_PX, logoSize)
            kakuTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize)
            tutorialText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
            supportText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
        }
    }

    override fun onResume()
    {
        super.onResume()

        if (!MainService.IsRunning())
        {
            progressBar.isIndeterminate = true
            progressBar.progress = 0
            supportText.text = getString(R.string.kaku_loading)
        }

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        Timer().schedule(object : TimerTask()
        {
            override fun run()
            {
                mainActivity.runOnUiThread {
                    mainActivity.startKaku(progressBar, supportText)
                }
            }
        }, 4000)
    }

    companion object
    {
        fun newInstance() : MainStartFragment {
            return MainStartFragment()
        }
    }
}
