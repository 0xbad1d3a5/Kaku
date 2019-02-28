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
            if (position in 1..9)
            {
                return TutorialFragment.newInstance(position)
            }

            return TutorialEndFragment.newInstance()
        }

        override fun getCount(): Int
        {
            return 11
        }
    }

    private lateinit var mSectionsPagerAdapter: FragmentStatePagerAdapter

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        setContentView(R.layout.activity_tutorial)

        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        container.adapter = mSectionsPagerAdapter
        container.offscreenPageLimit = 1
        tab_indicator.setupWithViewPager(container)
    }

    companion object
    {
        private val TAG = TutorialActivity::class.java.name
    }
}