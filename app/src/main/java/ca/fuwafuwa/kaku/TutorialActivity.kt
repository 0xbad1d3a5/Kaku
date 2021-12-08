package ca.fuwafuwa.kaku

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import ca.fuwafuwa.kaku.databinding.ActivityTutorialBinding

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
    private lateinit var mBinding: ActivityTutorialBinding

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        mBinding = ActivityTutorialBinding.inflate(layoutInflater)

        supportActionBar?.hide()
        setContentView(mBinding.root)

        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        mBinding.container.adapter = mSectionsPagerAdapter
        mBinding.container.offscreenPageLimit = 1
        mBinding.tabIndicator.setupWithViewPager(mBinding.container)
    }

    companion object
    {
        private val TAG = TutorialActivity::class.java.name
    }
}