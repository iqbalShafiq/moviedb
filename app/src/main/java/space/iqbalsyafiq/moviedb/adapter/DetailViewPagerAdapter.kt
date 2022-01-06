package space.iqbalsyafiq.moviedb.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import space.iqbalsyafiq.moviedb.view.fragments.AboutMovieFragment
import space.iqbalsyafiq.moviedb.view.fragments.ReviewFragment

class DetailViewPagerAdapter(fragment: Fragment) :
    FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        var fragment = Fragment()
        when (position) {
            0 -> fragment = AboutMovieFragment()
            1 -> fragment = ReviewFragment()
        }

        return fragment
    }

}