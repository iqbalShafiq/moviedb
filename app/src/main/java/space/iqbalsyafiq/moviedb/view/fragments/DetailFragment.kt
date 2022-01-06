package space.iqbalsyafiq.moviedb.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import space.iqbalsyafiq.moviedb.adapter.DetailViewPagerAdapter
import space.iqbalsyafiq.moviedb.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {

    private lateinit var binding: FragmentDetailBinding
    private lateinit var viewPagerAdapter: DetailViewPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Do something here
        viewPagerAdapter = DetailViewPagerAdapter(this)
        initiateTabPager()
    }

    private fun initiateTabPager() {
        with(binding) {
            vpDetail.adapter = viewPagerAdapter
            TabLayoutMediator(tlMenuDetail, vpDetail) { tab, position ->
                when (position) {
                    0 -> tab.text = "Top Rated"
                    1 -> tab.text = "Popular"
                    2 -> tab.text = "Now Playing"
                    3 -> tab.text = "Upcoming"
                }
            }.attach()
        }
    }
}