package ca.fuwafuwa.kaku

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import java.util.*


data class ImgData(val res: Int, val name: String, val nameLink: String, val imgLink: String, val hasImg: Boolean = true)

class MainStartFragment : Fragment()
{
    private lateinit var mainActivity : MainActivity
    private lateinit var rootView : View

    private lateinit var kakuLogo : TextView
    private lateinit var kakuTitle : TextView
    private lateinit var tutorialText : TextView

    private lateinit var supportText : TextView
    private lateinit var progressBar : ProgressBar

    private lateinit var promoView : ViewGroup
    private lateinit var adView : AdView
    private lateinit var saeView : ImageView

    private lateinit var imgData : ImgData

    private val adsEnabled = false

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        if (adsEnabled)
        {
            MobileAds.initialize(requireActivity(), resources.getString(R.string.ads_app_id))
        }
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

        promoView = rootView.findViewById(R.id.promoView)
        adView = rootView.findViewById(R.id.adView)
        saeView = rootView.findViewById(R.id.saeView)

        configureBottomPromo(adsEnabled)

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
            val drawableHeight = rootView.height - pos[1] - dpToPx(mainActivity, 30)

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

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        if (!MainService.IsRunning())
        {
            onKakuLoadStart()
        }

        Timer().schedule(object : TimerTask()
        {
            override fun run()
            {
                mainActivity.runOnUiThread {
                    mainActivity.startKaku(this@MainStartFragment)
                }
            }
        }, 4000)
    }

    fun onKakuLoadStart()
    {
        progressBar.isIndeterminate = true
        progressBar.progress = 0
        supportText.text = getString(R.string.kaku_loading)
    }

    fun onKakuLoaded()
    {
        progressBar.isIndeterminate = false
        progressBar.progress = 100
        writeSupportText()
    }

    private fun configureBottomPromo(adsEnabled: Boolean)
    {
        if (adsEnabled)
        {
            promoView.removeView(saeView)
            setupAds()
        }
        else
        {
            promoView.removeView(adView)
            setupImage()
        }

        if (MainService.IsRunning())
        {
            onKakuLoaded()
        }
    }

    private fun setupAds()
    {
        adView.adListener = object: AdListener()
        {
            override fun onAdLoaded()
            {
                super.onAdLoaded()
                mainActivity.startKaku(this@MainStartFragment)
            }

            override fun onAdFailedToLoad(p0: Int)
            {
                super.onAdFailedToLoad(p0)
                mainActivity.startKaku(this@MainStartFragment)
            }
        }
    }

    private fun setupImage()
    {
        imgData = getImageResources()

        if (imgData.hasImg)
        {
            saeView.setImageBitmap(BitmapFactory.decodeStream(resources.openRawResource(imgData.res)))
            saeView.setOnClickListener {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(imgData.imgLink)
                startActivity(i)
            }
        }
    }

    private fun getImageResources(): ImgData
    {
        val imgs = listOf(
                ImgData(R.raw.sae1, "@ultonesan", "https://twitter.com/ultonesan", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=62885659"),
                ImgData(R.raw.sae2, "@ultonesan", "https://twitter.com/ultonesan", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=68641857"),
                ImgData(R.raw.sae3, "@ultonesan", "https://twitter.com/ultonesan", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=71950613"),
                ImgData(R.raw.sae21, "@ultonesan", "https://twitter.com/ultonesan", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=73297155"),
                ImgData(R.raw.sae4, "@yamoyamo18", "https://twitter.com/yamoyamo18", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=73628894"),
                ImgData(R.raw.sae5, "@yamoyamo18", "https://twitter.com/yamoyamo18", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=72424727"),
                ImgData(R.raw.sae9, "@yamoyamo18", "https://twitter.com/yamoyamo18", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=72008999"),
                ImgData(R.raw.sae16, "@yamoyamo18", "https://twitter.com/yamoyamo18", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=66871255"),
                ImgData(R.raw.sae22, "@yamoyamo18", "https://twitter.com/yamoyamo18", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=72822939"),
                ImgData(R.raw.sae23, "@yamoyamo18", "https://twitter.com/yamoyamo18", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=71623460"),
                ImgData(R.raw.sae24, "@yamoyamo18", "https://twitter.com/yamoyamo18", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=71994817"),
                ImgData(R.raw.sae25, "@yamoyamo18", "https://twitter.com/yamoyamo18", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=73311041"),
                ImgData(R.raw.sae26, "@yamoyamo18", "https://twitter.com/yamoyamo18", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=73166793"),
                ImgData(R.raw.sae27, "@yamoyamo18", "https://twitter.com/yamoyamo18", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=73056502"),
                ImgData(R.raw.sae28, "@yamoyamo18", "https://twitter.com/yamoyamo18", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=71894148"),
                ImgData(R.raw.sae29, "@yamoyamo18", "https://twitter.com/yamoyamo18", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=71741734"),
                ImgData(R.raw.sae30, "@yamoyamo18", "https://twitter.com/yamoyamo18", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=71550295"),
                ImgData(R.raw.sae6, "@Sutoroa_", "https://twitter.com/Sutoroa_", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=72284325"),
                ImgData(R.raw.sae7, "栗羊", "https://www.pixiv.net/member.php?id=7231087", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=70943811"),
                ImgData(R.raw.sae8, "fevri", "https://www.pixiv.net/member.php?id=23625153", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=71230492"),
                ImgData(R.raw.sae10, "@ultimate_force6", "https://twitter.com/ultimate_force6", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=68200934"),
                ImgData(R.raw.sae19, "@ultimate_force6", "https://twitter.com/ultimate_force6", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=65491060"),
                ImgData(R.raw.sae20, "@ultimate_force6", "https://twitter.com/ultimate_force6", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=71977064"),
                ImgData(R.raw.sae11, "@RomanticGACHA", "https://twitter.com/RomanticGACHA", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=71885557"),
                ImgData(R.raw.sae13, "ちゅんこ", "https://www.pixiv.net/member.php?id=15933874", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=70193706"),
                ImgData(R.raw.sae14, "ちゅんこ", "https://www.pixiv.net/member.php?id=15933874", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=69105225"),
                ImgData(R.raw.sae15, "ちゅんこ", "https://www.pixiv.net/member.php?id=15933874", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=64939768"),
                ImgData(R.raw.sae17, "@kusanosinta", "https://twitter.com/kusanosinta", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=66630846"),
                ImgData(R.raw.sae18, "@jksh5056", "https://twitter.com/jksh5056", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=66503611"),
                ImgData(R.raw.sae12, "@syounenkross", "https://twitter.com/syounenkross", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=64177621"),
                ImgData(R.raw.sae31, "@Nb_mk2", "https://twitter.com/Nb_mk2", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=63674704"),
                ImgData(R.raw.sae32, "@Hachita888", "https://twitter.com/Hachita888", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=68277697"),
                ImgData(R.raw.sae33, "@Hachita888", "https://twitter.com/Hachita888", "https://twitter.com/Hachita888/status/1101056129159749632"),
                ImgData(R.raw.sae34, "Toffee", "https://www.pixiv.net/member.php?id=13274275", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=62893744"),
                ImgData(R.raw.sae35, "@N1__03", "https://twitter.com/N1__03", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=63248104"),
                ImgData(R.raw.sae36, "@yu_hi0420", "https://twitter.com/yu_hi0420", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=60998374")
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=63199962
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=62945449
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=62176436
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=61738328
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=61109132

                //ImgData(R.raw.sae, "", "", "")
        )

        return imgs.random()
    }

    private fun writeSupportText()
    {
        if (imgData.hasImg)
        {
            val artText = "Art by ${imgData.name}"
            val spannableStringBuilder = SpannableStringBuilder(artText)
            val clickableSpan = object : ClickableSpan()
            {
                override fun onClick(view: View)
                {
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(imgData.nameLink)
                    startActivity(i)
                }
            }

            spannableStringBuilder.setSpan(clickableSpan, artText.indexOf(imgData.name), artText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            supportText.text = spannableStringBuilder
            supportText.movementMethod = LinkMovementMethod.getInstance()
        }
        else
        {
            supportText.text = getString(R.string.support_text)
        }
    }
}
