package space.iqbalsyafiq.moviedb.view.fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.dialog_rating.*
import space.iqbalsyafiq.moviedb.R
import space.iqbalsyafiq.moviedb.adapter.MovieListAdapter
import space.iqbalsyafiq.moviedb.databinding.DialogRatingBinding
import space.iqbalsyafiq.moviedb.databinding.FragmentHomeBinding
import space.iqbalsyafiq.moviedb.model.movie.Movie
import space.iqbalsyafiq.moviedb.viewmodel.MovieListViewModel

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: MovieListViewModel by activityViewModels()
    private lateinit var adapter: MovieListAdapter
    private var selectedCategory = "Top Rated"
    private lateinit var layoutManager: LinearLayoutManager
    private var page = 1

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

            // recycler view
            rvMovies.apply {
                layoutManager = LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.VERTICAL,
                    false
                )

                adapter = adapter
            }
            layoutManager = rvMovies.layoutManager as LinearLayoutManager

            // on refresh
            refreshLayout.setOnRefreshListener {
                rvMovies.visibility = View.GONE
                progressLoad.visibility = View.VISIBLE
                tvErrorLoad.visibility = View.GONE
                tvLoadMore.visibility = View.GONE
                etSearch.setText("")

                page = 1
                adapter.clearMovieList()
                viewModel.refreshBypassCache(selectedCategory)
                refreshLayout.isRefreshing = false
            }

            // search
            etSearch.addTextChangedListener {
                Log.d("TAG", "onViewCreated: ${it.toString()}")
                viewModel.searchMovies(it.toString(), selectedCategory)
            }

            // load more on clicked
            tvLoadMore.setOnClickListener {
                page++
                viewModel.refresh(selectedCategory, page)
            }

            // watch list on click
            llWatchList.setOnClickListener {
                val action = HomeFragmentDirections
                    .navigateToWatchList()
                Navigation.findNavController(binding.root).navigate(action)
            }
        }

        // category on click
        categoryOnClick()

        // observe view model
        observeViewModel()
    }

    fun showDialogRating(movie: Movie) {
        val dialog = Dialog(requireContext())
        val dialogBinding = DialogRatingBinding.inflate(layoutInflater)

        dialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(true)
            setContentView(dialogBinding.root)
        }

        with(dialogBinding) {
            btnCancel.setOnClickListener {
                dialog.dismiss()
            }

            btnSubmit.setOnClickListener {
                Log.d("TAG", "showDialogRating: ${dialogBinding.etRate.text}")
                val value = dialogBinding.etRate.text.toString()
                if (value.toDouble() > 0.0 && value.toDouble() <= 10) {
                    viewModel.rateMovie(value, movie)
                    dialog.dismiss()
                }
            }
        }

        dialog.apply {
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            show()
            window?.setLayout(800, 750)
        }
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
        page = 1
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
        viewModel.storeWatchList(movie)
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
                binding.tvLoadMore.visibility = if (movieList.isEmpty()) View.GONE else View.VISIBLE
            }
        })

        viewModel.searching.observe(viewLifecycleOwner, { isSearching ->
            isSearching?.let {
                binding.horizontalScrollView.visibility =
                    if (isSearching) View.GONE else View.VISIBLE
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
                binding.tvLoadMore.text = if (isLoading) "Loading ..." else "Load More"
            }
        })
    }

}