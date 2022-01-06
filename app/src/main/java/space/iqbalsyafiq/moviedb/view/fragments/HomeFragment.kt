package space.iqbalsyafiq.moviedb.view.fragments

import android.graphics.Color
import android.graphics.Typeface
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
import space.iqbalsyafiq.moviedb.model.movie.MovieResponse
import space.iqbalsyafiq.moviedb.viewmodel.MovieListViewModel

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: MovieListViewModel by activityViewModels()
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
        adapter = MovieListAdapter(requireContext(), arrayListOf())

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
                viewModel.refresh(selectedCategory)
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
                selectedCategory = btnTopRated.text.toString()
                viewModel.refresh("Top Rated")
                setSelectedButton(
                    btnTopRated,
                    btnPopular,
                    btnNowPlaying,
                    btnUpcoming
                )
            }

            btnPopular.setOnClickListener {
                selectedCategory = btnPopular.text.toString()
                viewModel.refresh("Popular")
                setSelectedButton(
                    btnPopular,
                    btnTopRated,
                    btnNowPlaying,
                    btnUpcoming
                )
            }

            btnNowPlaying.setOnClickListener {
                selectedCategory = btnNowPlaying.text.toString()
                viewModel.refresh("Now Playing")
                setSelectedButton(
                    btnNowPlaying,
                    btnTopRated,
                    btnPopular,
                    btnUpcoming
                )
            }

            btnUpcoming.setOnClickListener {
                selectedCategory = btnUpcoming.text.toString()
                viewModel.refresh("Upcoming")
                setSelectedButton(
                    btnUpcoming,
                    btnTopRated,
                    btnPopular,
                    btnNowPlaying
                )
            }
        }
    }

    private fun setSelectedButton(
        selectedButton: Button,
        unselectedButton1: Button,
        unselectedButton2: Button,
        unselectedButton3: Button
    ) {
        selectedButton.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.bg_radius_16dp)
        selectedButton.setTextColor(Color.parseColor("#242A32"))
        selectedButton.setTypeface(selectedButton.typeface, Typeface.BOLD)

        unselectedButton1.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.bg_radius_16dp_dark)
        unselectedButton1.setTextColor(Color.parseColor("#EEEEEE"))
        unselectedButton1.setTypeface(selectedButton.typeface, Typeface.NORMAL)

        unselectedButton2.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.bg_radius_16dp_dark)
        unselectedButton2.setTextColor(Color.parseColor("#EEEEEE"))
        unselectedButton2.setTypeface(selectedButton.typeface, Typeface.NORMAL)

        unselectedButton3.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.bg_radius_16dp_dark)
        unselectedButton3.setTextColor(Color.parseColor("#EEEEEE"))
        unselectedButton3.setTypeface(selectedButton.typeface, Typeface.NORMAL)
    }

    private fun observeViewModel() {
        viewModel.movies.observe(viewLifecycleOwner, { movieList ->
            movieList?.let {
                binding.rvMovies.apply {
                    visibility = View.VISIBLE
                    adapter = MovieListAdapter(
                        requireContext(),
                        movieList.results as ArrayList<MovieResponse>
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