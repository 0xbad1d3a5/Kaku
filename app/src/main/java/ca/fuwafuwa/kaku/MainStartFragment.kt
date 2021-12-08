package ca.fuwafuwa.kaku

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkInfo
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
import com.google.android.gms.ads.*
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

    private var showAds = false

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        // Switch off deprecated network API
        val cm = requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo

        val adsEnabled = false
        val internetEnabled = activeNetwork?.isConnectedOrConnecting == true

        showAds = adsEnabled && internetEnabled
        if (showAds)
        {
            MobileAds.initialize(requireActivity())
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

        configureBottomPromo(showAds)

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

        if (showAds)
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

            override fun onAdFailedToLoad(p0: LoadAdError) {
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
                ImgData(R.raw.sae0001, "@ultonesan", "https://twitter.com/ultonesan", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=62885659"),
                ImgData(R.raw.sae0002, "@ultonesan", "https://twitter.com/ultonesan", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=68641857"),
                ImgData(R.raw.sae0003, "@ultonesan", "https://twitter.com/ultonesan", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=71950613"),
                ImgData(R.raw.sae0021, "@ultonesan", "https://twitter.com/ultonesan", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=73297155"),
                ImgData(R.raw.sae0044, "@ultonesan", "https://twitter.com/ultonesan", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=60987977"),
                ImgData(R.raw.sae0004, "@yamoyamo18", "https://twitter.com/yamoyamo18", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=73628894"),
                ImgData(R.raw.sae0005, "@yamoyamo18", "https://twitter.com/yamoyamo18", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=72424727"),
                ImgData(R.raw.sae0009, "@yamoyamo18", "https://twitter.com/yamoyamo18", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=72008999"),
                ImgData(R.raw.sae0016, "@yamoyamo18", "https://twitter.com/yamoyamo18", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=66871255"),
                ImgData(R.raw.sae0022, "@yamoyamo18", "https://twitter.com/yamoyamo18", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=72822939"),
                ImgData(R.raw.sae0023, "@yamoyamo18", "https://twitter.com/yamoyamo18", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=71623460"),
                ImgData(R.raw.sae0024, "@yamoyamo18", "https://twitter.com/yamoyamo18", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=71994817"),
                ImgData(R.raw.sae0025, "@yamoyamo18", "https://twitter.com/yamoyamo18", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=73311041"),
                ImgData(R.raw.sae0026, "@yamoyamo18", "https://twitter.com/yamoyamo18", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=73166793"),
                ImgData(R.raw.sae0027, "@yamoyamo18", "https://twitter.com/yamoyamo18", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=73056502"),
                ImgData(R.raw.sae0028, "@yamoyamo18", "https://twitter.com/yamoyamo18", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=71894148"),
                ImgData(R.raw.sae0029, "@yamoyamo18", "https://twitter.com/yamoyamo18", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=71741734"),
                ImgData(R.raw.sae0030, "@yamoyamo18", "https://twitter.com/yamoyamo18", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=71550295"),
                ImgData(R.raw.sae0006, "@Sutoroa_", "https://twitter.com/Sutoroa_", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=72284325"),
                ImgData(R.raw.sae0098, "@Sutoroa_", "https://twitter.com/Sutoroa_", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=73191911"),
                ImgData(R.raw.sae0007, "栗羊", "https://www.pixiv.net/member.php?id=7231087", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=70943811"),
                ImgData(R.raw.sae0008, "fevri", "https://www.pixiv.net/member.php?id=23625153", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=71230492"),
                ImgData(R.raw.sae0010, "@ultimate_force6", "https://twitter.com/ultimate_force6", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=68200934"),
                ImgData(R.raw.sae0019, "@ultimate_force6", "https://twitter.com/ultimate_force6", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=65491060"),
                ImgData(R.raw.sae0020, "@ultimate_force6", "https://twitter.com/ultimate_force6", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=71977064"),
                ImgData(R.raw.sae0057, "@ultimate_force6", "https://twitter.com/ultimate_force6", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=74363079"),
                ImgData(R.raw.sae0011, "@RomanticGACHA", "https://twitter.com/RomanticGACHA", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=71885557"),
                ImgData(R.raw.sae0013, "ちゅんこ", "https://www.pixiv.net/member.php?id=15933874", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=70193706"),
                ImgData(R.raw.sae0014, "ちゅんこ", "https://www.pixiv.net/member.php?id=15933874", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=69105225"),
                ImgData(R.raw.sae0015, "ちゅんこ", "https://www.pixiv.net/member.php?id=15933874", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=64939768"),
                ImgData(R.raw.sae0065, "ちゅんこ", "https://www.pixiv.net/member.php?id=15933874", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=57615504"),
                ImgData(R.raw.sae0066, "ちゅんこ", "https://www.pixiv.net/member.php?id=15933874", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=57126244"),
                ImgData(R.raw.sae0067, "ちゅんこ", "https://www.pixiv.net/member.php?id=15933874", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=57126244"),
                ImgData(R.raw.sae0068, "ちゅんこ", "https://www.pixiv.net/member.php?id=15933874", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=62991879"),
                ImgData(R.raw.sae0075, "ちゅんこ", "https://www.pixiv.net/member.php?id=15933874", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=59695678"),
                ImgData(R.raw.sae0076, "ちゅんこ", "https://www.pixiv.net/member.php?id=15933874", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=59695678"),
                ImgData(R.raw.sae0096, "ちゅんこ", "https://www.pixiv.net/member.php?id=15933874", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=73671294"),
                ImgData(R.raw.sae0097, "ちゅんこ", "https://www.pixiv.net/member.php?id=15933874", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=73262536"),
                ImgData(R.raw.sae0103, "ちゅんこ", "https://www.pixiv.net/member.php?id=15933874", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=59882410"),
                ImgData(R.raw.sae0017, "@kusanosinta", "https://twitter.com/kusanosinta", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=66630846"),
                ImgData(R.raw.sae0018, "@jksh5056", "https://twitter.com/jksh5056", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=66503611"),
                ImgData(R.raw.sae0012, "@syounenkross", "https://twitter.com/syounenkross", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=64177621"),
                ImgData(R.raw.sae0031, "@Nb_mk2", "https://twitter.com/Nb_mk2", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=63674704"),
                ImgData(R.raw.sae0032, "@Hachita888", "https://twitter.com/Hachita888", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=68277697"),
                ImgData(R.raw.sae0033, "@Hachita888", "https://twitter.com/Hachita888", "https://twitter.com/Hachita888/status/1101056129159749632"),
                ImgData(R.raw.sae0034, "Toffee", "https://www.pixiv.net/member.php?id=13274275", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=62893744"),
                ImgData(R.raw.sae0035, "@N1__03", "https://twitter.com/N1__03", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=63248104"),
                ImgData(R.raw.sae0036, "@yu_hi0420", "https://twitter.com/yu_hi0420", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=60998374"),
                ImgData(R.raw.sae0037, "@nike_abc", "https://twitter.com/nike_abc", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=63199962"),
                ImgData(R.raw.sae0038, "iwawo", "https://www.pixiv.net/member.php?id=1926865", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=62945449"),
                ImgData(R.raw.sae0039, "tamamooon", "https://www.pixiv.net/member.php?id=3796056", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=62176436"),
                ImgData(R.raw.sae0040, "Ametama", "https://www.pixiv.net/member.php?id=10122880", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=61738328"),
                ImgData(R.raw.sae0041, "@2cFirefly", "https://twitter.com/2cFirefly", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=61109132"),
                ImgData(R.raw.sae0042, "LP", "https://www.pixiv.net/member.php?id=9774145", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=60911262"),
                ImgData(R.raw.sae0043, "@haikimono", "https://twitter.com/haikimono", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=60871513"),
                ImgData(R.raw.sae0045, "@xx__lotus", "https://twitter.com/xx__lotus", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=60860221"),
                ImgData(R.raw.sae0046, "@kamonabe_44", "https://twitter.com/kamonabe_44", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=60254903"),
                ImgData(R.raw.sae0047, "@Yunagi_Amane", "https://twitter.com/Yunagi_Amane", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=60128230"),
                ImgData(R.raw.sae0048, "@Azmo_dan", "https://twitter.com/Azmo_dan", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=59980990"),
                ImgData(R.raw.sae0049, "@takeashiro", "https://twitter.com/takeashiro", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=59770209"),
                ImgData(R.raw.sae0050, "@watanseru", "https://twitter.com/watanseru", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=59521673"),
                ImgData(R.raw.sae0051, "@gin_no_te", "https://twitter.com/gin_no_te", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=59907147"),
                ImgData(R.raw.sae0052, "miazi", "https://www.pixiv.net/member.php?id=2551745", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=59536025"),
                ImgData(R.raw.sae0053, "ちゅんこ", "https://www.pixiv.net/member.php?id=15933874", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=59420528"),
                ImgData(R.raw.sae0054, "@shiredo326", "https://twitter.com/shiredo326", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=59213920"),
                ImgData(R.raw.sae0055, "@kashinoshishi", "https://twitter.com/kashinoshishi", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=59283209"),
                ImgData(R.raw.sae0056, "@xxSuite_Peexx", "https://twitter.com/xxSuite_Peexx", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=74367771"),
                ImgData(R.raw.sae0058, "@minami_nyan", "https://twitter.com/minami_nyan", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=59145679"),
                ImgData(R.raw.sae0059, "@magchomp8", "https://twitter.com/magchomp8", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=58950389"),
                ImgData(R.raw.sae0060, "みなみ茶哂", "https://www.pixiv.net/member.php?id=4939449", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=58574011"),
                ImgData(R.raw.sae0061, "@k_nishiwaki", "https://twitter.com/k_nishiwaki", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=58034287"),
                ImgData(R.raw.sae0062, "Koji", "https://www.pixiv.net/member.php?id=19228899", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=57976156"),
                ImgData(R.raw.sae0063, "@P_KiGiSi", "https://twitter.com/P_KiGiSi", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=57758102"),
                ImgData(R.raw.sae0064, "@TakahashiMitama", "https://twitter.com/Takahashimitama", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=63206782"),
                ImgData(R.raw.sae0069, "有河サトル", "https://www.pixiv.net/member.php?id=28781", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=53882354"),
                ImgData(R.raw.sae0072, "有河サトル", "https://www.pixiv.net/member.php?id=28781", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=54029940"),
                ImgData(R.raw.sae0070, "@25irohaxx", "https://twitter.com/25irohaxx", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=59642129"),
                ImgData(R.raw.sae0071, "@pizzasi7", "https://twitter.com/pizzasi7", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=59989777"),
                ImgData(R.raw.sae0073, "@frenchmaid_", "https://twitter.com/frenchmaid_", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=53105182"),
                ImgData(R.raw.sae0074, "P-", "https://www.pixiv.net/member.php?id=1032188", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=51178608"),
                ImgData(R.raw.sae0077, "P-", "https://www.pixiv.net/member.php?id=1032188", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=51344144"),
                ImgData(R.raw.sae0106, "P-", "https://www.pixiv.net/member.php?id=1032188", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=57042341"),
                ImgData(R.raw.sae0078, "@__KFR", "https://twitter.com/__KFR", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=49375938"),
                ImgData(R.raw.sae0079, "AGG", "https://www.pixiv.net/member.php?id=12310765", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=49664984"),
                ImgData(R.raw.sae0080, "みじんこうか", "https://www.pixiv.net/member.php?id=770137", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=47906259"),
                ImgData(R.raw.sae0081, "@mazakaaaan", "https://twitter.com/mazakaaaan", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=47913066"),
                ImgData(R.raw.sae0083, "@mazakaaaan", "https://twitter.com/mazakaaaan", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=46008331"),
                ImgData(R.raw.sae0086, "@mazakaaaan", "https://twitter.com/mazakaaaan", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=41998732"),
                ImgData(R.raw.sae0082, "H2O", "https://www.pixiv.net/member.php?id=18180240", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=59537085"),
                ImgData(R.raw.sae0084, "@natuya777", "https://twitter.com/natuya777", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=43285880"),
                ImgData(R.raw.sae0085, "@natuya777", "https://twitter.com/natuya777", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=46606608"),
                ImgData(R.raw.sae0087, "しの", "https://www.pixiv.net/member.php?id=35037", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=36906360"),
                ImgData(R.raw.sae0088, "美月めいあ", "https://www.pixiv.net/member.php?id=383657", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=33659173"),
                ImgData(R.raw.sae0089, "@mckeeeeelog", "https://twitter.com/mckeeeeelog", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=29860757"),
                ImgData(R.raw.sae0090, "@miyakoazu", "https://twitter.com/miyakoazu", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=32349060"),
                ImgData(R.raw.sae0091, "@WzK7VHEGkO0J4E4", "https://twitter.com/WzK7VHEGkO0J4E4", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=59587222"),
                ImgData(R.raw.sae0092, "@yuntayu", "https://twitter.com/yuntayu", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=32728220"),
                ImgData(R.raw.sae0093, "@jackallllllllll", "https://twitter.com/jackallllllllll", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=42247716"),
                ImgData(R.raw.sae0094, "@akihiko_05", "https://twitter.com/akihiko_05", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=68160948"),
                ImgData(R.raw.sae0095, "Manyo", "https://www.pixiv.net/member.php?id=5940914", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=73269188"),
                ImgData(R.raw.sae0099, "@migi_mawashi", "https://twitter.com/migi_mawashi", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=73112698"),
                ImgData(R.raw.sae0100, "@syatly", "https://twitter.com/syatly", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=56959669"),
                ImgData(R.raw.sae0101, "@syatly", "https://twitter.com/syatly", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=56959669"),
                ImgData(R.raw.sae0102, "@ponyui0728", "https://twitter.com/ponyui0728", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=56880664"),
                ImgData(R.raw.sae0104, "縞七", "https://www.pixiv.net/member.php?id=6454840", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=72524370"),
                ImgData(R.raw.sae0105, "@elesake", "https://twitter.com/elesake", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=44565332")

                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=72574413"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=72300606"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=72319649"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=72091546"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=72010515"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=71846364"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=71576086"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=71445592"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=61286524"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=71264363"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=71223663"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=68815242"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=72880878"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=69776652"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=70563899"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=69883575"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=61621521"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=69759435"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=69644506"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=69541575"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=67888363"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=69598154"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=69176611"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=69297967"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=68515979"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=55885699"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=66886065"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=67197908"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=67050561"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=61808784"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=62053790"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=55045464"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=68054335"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=61583824"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=66970564"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=66662471"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=61854663"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=61246834"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=66365255"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=66254674"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=66285209"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=46928698"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=66066299"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=65987865"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=65879293"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=62559423"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=65712553"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=61596965"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=46623623"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=63860848"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=61191380"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=65481843"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=60867649"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=63856945"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=65490674"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=63372013"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=63350634"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=64114259"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=61097071"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=41853425"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=27614826"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=61628173"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=63524058"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=61897582"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=62103143"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=65479372"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=25326404"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=65479726"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=26138046"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=60727676"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=61046820"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=65479738"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=63951102"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=65028532"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=24908091"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=64290897"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=58546816"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=62556140"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=59703065"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=64076559"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=36896429"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=31847612"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=52125154"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=32441611"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=57866673"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=54074753"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=56498215"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=57458361"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=60134647"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=58973257"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=52640454"),
                //ImgData(R.raw.sae, "", "", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=51140952"),
                //ImgData(R.raw.sae, "", "", "https://twitter.com/_citrusmikan/status/1084315711516770305"),
                //ImgData(R.raw.sae, "", "", "https://twitter.com/frenchmaid_/status/794449492128825344"),

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
        )

        return imgs.random()
    }

    private fun writeSupportText()
    {
        if (!showAds && imgData.hasImg)
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
