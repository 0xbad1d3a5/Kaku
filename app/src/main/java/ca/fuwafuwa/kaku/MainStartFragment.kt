package ca.fuwafuwa.kaku

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
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

        if (adsEnabled)
        {
            val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)
        }

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
        }, 3000)
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
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=60911262
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=60871513
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=60987977
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=60860221
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=60254903
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=60128230
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=59980990
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=59770209
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=59521673
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=59907147
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=59536025
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=59420528
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=59213920
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=59283209
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=59145679
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=58950389
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=58574011
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=58034287
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=57976156
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=57758102
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=63206782
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=57615504
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=57126244
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=62991879
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=53882354
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=59642129
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=59989777
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=54029940
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=53105182
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=51178608
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=59695678
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=51344144
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=49375938
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=49664984
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=47906259
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=47913066
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=59537085
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=46008331
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=43285880
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=46606608
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=41998732
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=36906360
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=33659173
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=29860757
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=32349060
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=59587222
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=32728220
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=42247716
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=68160948
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=73269188
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=73671294
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=73262536
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=73191911
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=73112698
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=56959669
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=56880664
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=59882410
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=72524370
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=44565332
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=57042341
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=72574413
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=72300606
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=72319649
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=72091546
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=72010515
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=71846364
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=71576086
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=71445592
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=61286524
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=71264363
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=71223663
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=68815242
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=72880878
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=69776652
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=70563899
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=69883575
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=61621521
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=69759435
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=69644506
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=69541575
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=67888363
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=69598154
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=69176611
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=69297967
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=68515979
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=55885699
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=66886065
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=67197908
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=67050561
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=61808784
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=62053790
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=55045464
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=68054335
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=61583824
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=66970564
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=66662471
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=61854663
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=61246834
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=66365255
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=66254674
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=66285209
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=46928698
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=66066299
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=65987865
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=65879293
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=62559423
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=65712553
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=61596965
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=46623623
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=63860848
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=61191380
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=65481843
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=60867649
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=63856945
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=65490674
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=63372013
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=63350634
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=64114259
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=61097071
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=41853425
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=27614826
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=61628173
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=63524058
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=61897582
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=62103143
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=65479372
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=25326404
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=65479726
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=26138046
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=60727676
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=61046820
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=65479738
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=63951102
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=65028532
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=24908091
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=64290897
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=58546816
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=62556140
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=59703065
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=64076559
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=36896429
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=31847612
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=52125154
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=32441611
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=57866673
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=54074753
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=56498215
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=57458361
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=60134647
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=58973257
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=52640454
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=51140952
                //https://twitter.com/_citrusmikan/status/1084315711516770305
                //https://twitter.com/frenchmaid_/status/794449492128825344

                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=46620217
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=60477054
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=63879982
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=60685026
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=54111389
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=60556417
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=61999933
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=61127239
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=59531868
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=60211876
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=62135077
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=59731482
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=58925725
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=53024162
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=66740209
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=68011261
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=43284648
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=59848031
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=73629542
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=56041501
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=70976755
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=67062837
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=42178521
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=70270394
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=62495444
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=68958428
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=68433759
                //https://www.pixiv.net/member_illust.php?mode=medium&illust_id=57793633

                //ImgData(R.raw.sae, "", "", "")
        )

        return imgs.random()
    }

    private fun writeSupportText()
    {
        if (!adsEnabled && imgData.hasImg)
        {
            val saeName = "小早川紗枝"
            val artText = "\uD83C\uDF38 $saeName by ${imgData.name} \uD83C\uDF38"
            val spannableStringBuilder = SpannableStringBuilder(artText)

            val clickableSae = object : ClickableSpan()
            {
                override fun onClick(view: View)
                {
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse("https://twitter.com/hashtag/%E5%B0%8F%E6%97%A9%E5%B7%9D%E7%B4%97%E6%9E%9D")
                    startActivity(i)
                }

                override fun updateDrawState(ds: TextPaint)
                {
                    ds.color = ContextCompat.getColor(activity!!, R.color.blue_dark)
                    ds.isUnderlineText = false;
                }
            }
            spannableStringBuilder.setSpan(clickableSae, artText.indexOf(saeName), artText.indexOf(saeName) + saeName.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            val clickableLink = object : ClickableSpan()
            {
                override fun onClick(view: View)
                {
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(imgData.nameLink)
                    startActivity(i)
                }

                override fun updateDrawState(ds: TextPaint)
                {
                    ds.color = ContextCompat.getColor(activity!!, R.color.blue_dark)
                    ds.isUnderlineText = false;
                }
            }
            spannableStringBuilder.setSpan(clickableLink, artText.indexOf(imgData.name), artText.indexOf(imgData.name) + imgData.name.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            supportText.text = spannableStringBuilder
            supportText.movementMethod = LinkMovementMethod.getInstance()
        }
        else
        {
            supportText.text = getString(R.string.support_text)
        }
    }
}
