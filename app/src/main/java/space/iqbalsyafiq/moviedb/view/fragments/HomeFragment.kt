package space.iqbalsyafiq.moviedb.view.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import space.iqbalsyafiq.moviedb.R
import space.iqbalsyafiq.moviedb.adapter.MovieListAdapter
import space.iqbalsyafiq.moviedb.databinding.FragmentHomeBinding
import space.iqbalsyafiq.moviedb.model.movie.Movie
import space.iqbalsyafiq.moviedb.viewmodel.MovieListViewModel

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    val viewModel: MovieListViewModel by activityViewModels()
    private lateinit var adapter: MovieListAdapter
    private var selectedCategory = "Top Rated"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // initiate adapter
        adapter = MovieListAdapter(requireContext(), arrayListOf(), this)

        // fetch data
        viewModel.refresh("Now Playing")

        // set view
        with(binding) {
            // set selected button category
            setSelectedButton(
                btnNowPlaying,
                btnTopRated,
                btnPopular,
                btnUpcoming
            )

            rvMovies.apply {
                layoutManager = LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.VERTICAL,
                    false
                )

                adapter = adapter
            }

            refreshLayout.setOnRefreshListener {
                rvMovies.visibility = View.GONE
                progressLoad.visibility = View.VISIBLE
                tvErrorLoad.visibility = View.GONE
                tvLoadMore.visibility = View.GONE

                adapter.clearMovieList()
                viewModel.refreshBypassCache(selectedCategory)
                refreshLayout.isRefreshing = false
            }
        }

        // category on click
        categoryOnClick()

        // observe view model
        observeViewModel()
    }

    private fun categoryOnClick() {
        with(binding) {
            btnTopRated.setOnClickListener {
                setSelectedButton(btnTopRated, btnPopular, btnNowPlaying, btnUpcoming)
            }

            btnPopular.setOnClickListener {
                setSelectedButton(btnPopular, btnTopRated, btnNowPlaying, btnUpcoming)
            }

            btnNowPlaying.setOnClickListener {
                setSelectedButton(btnNowPlaying, btnTopRated, btnPopular, btnUpcoming)
            }

            btnUpcoming.setOnClickListener {
                setSelectedButton(btnUpcoming, btnTopRated, btnPopular, btnNowPlaying)
            }
        }
    }

    private fun setSelectedButton(
        selectedButton: Button,
        unselectedButton1: Button,
        unselectedButton2: Button,
        unselectedButton3: Button
    ) {
        // set selected category and load api from view model
        selectedCategory = selectedButton.text.toString()
        viewModel.refresh(selectedCategory)

        // Set selected category button
        selectedButton.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.bg_radius_16dp)
        selectedButton.setTextColor(Color.parseColor("#242A32"))

        // Set unselected button 1..3
        setUnselectedButton(unselectedButton1)
        setUnselectedButton(unselectedButton2)
        setUnselectedButton(unselectedButton3)
    }

    private fun setUnselectedButton(unselectedButton: Button) {
        unselectedButton.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.bg_radius_16dp_dark)
        unselectedButton.setTextColor(Color.parseColor("#EEEEEE"))
    }

    fun watchListed(movie: Movie) {
        viewModel.storeWatchList(movie, selectedCategory)
    }

    private fun observeViewModel() {
        viewModel.movies.observe(viewLifecycleOwner, { movieList ->
            movieList?.let {
                binding.rvMovies.apply {
                    visibility = View.VISIBLE
                    adapter = MovieListAdapter(
                        requireContext(),
                        movieList as ArrayList<Movie>,
                        this@HomeFragment
                    )
                }
            }
        })

        viewModel.loadError.observe(viewLifecycleOwner, { isError ->
            isError?.let {
                binding.tvErrorLoad.visibility = if (isError) View.VISIBLE else View.GONE
                binding.tvLoadMore.visibility = if (isError) View.GONE else View.VISIBLE
            }
        })

        viewModel.loading.observe(viewLifecycleOwner, { isLoading ->
            isLoading?.let {
                binding.progressLoad.visibility = if (isLoading) View.VISIBLE else View.GONE
                binding.rvMovies.visibility = if (isLoading) View.GONE else View.VISIBLE
                binding.tvLoadMore.visibility = if (isLoading) View.GONE else View.VISIBLE
            }
        })
    }

}