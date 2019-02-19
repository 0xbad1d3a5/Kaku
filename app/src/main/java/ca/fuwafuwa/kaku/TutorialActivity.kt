package ca.fuwafuwa.kaku

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import kotlinx.android.synthetic.main.activity_tutorial.*

class TutorialActivity : AppCompatActivity()
{
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm)
    {
        override fun getItem(position: Int): Fragment
        {
            if (position == 0){
                return TutorialWelcomeFragment.newInstance()
            }
            return TutorialFragment.newInstance(position + 1)
        }

        override fun getCount(): Int
        {
            return 10
        }

        fun setProgress(progress: Int)
        {

        }
    }

    private var mSectionsPagerAdapter: FragmentStatePagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        setContentView(R.layout.activity_tutorial)

        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        container.adapter = mSectionsPagerAdapter
        container.offscreenPageLimit = 1

        tab_indicator.addTab(tab_indicator.newTab())
        tab_indicator.addTab(tab_indicator.newTab())
        tab_indicator.addTab(tab_indicator.newTab())
        tab_indicator.addTab(tab_indicator.newTab())
        tab_indicator.addTab(tab_indicator.newTab())
        tab_indicator.addTab(tab_indicator.newTab())
        tab_indicator.addTab(tab_indicator.newTab())
        tab_indicator.addTab(tab_indicator.newTab())
        tab_indicator.addTab(tab_indicator.newTab())
        tab_indicator.addTab(tab_indicator.newTab())
        tab_indicator.getTabAt(4)!!.select()
    }
}