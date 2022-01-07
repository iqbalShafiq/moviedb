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
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import space.iqbalsyafiq.moviedb.adapter.MovieListAdapter
import space.iqbalsyafiq.moviedb.databinding.DialogRatingBinding
import space.iqbalsyafiq.moviedb.databinding.FragmentWatchListBinding
import space.iqbalsyafiq.moviedb.model.movie.Movie
import space.iqbalsyafiq.moviedb.viewmodel.WatchListViewModel

class WatchListFragment : Fragment() {

    private lateinit var binding: FragmentWatchListBinding
    private val viewModel: WatchListViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentWatchListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // fetch watch movie
        viewModel.refresh()

        // set view
        with(binding) {
            rvWatchList.apply {
                layoutManager = LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.VERTICAL,
                    false
                )

                adapter = MovieListAdapter(requireContext(), arrayListOf(), this@WatchListFragment)
            }

            llBack.setOnClickListener {
                requireActivity().onBackPressed()
            }
        }

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

    fun deleteWatchListById(id: Int) {
        viewModel.deleteWatchListById(id)
    }

    private fun observeViewModel() {
        viewModel.movies.observe(viewLifecycleOwner, { movieList ->
            movieList?.let {
                binding.rvWatchList.adapter = MovieListAdapter(
                    requireContext(),
                    movieList as ArrayList<Movie>,
                    this@WatchListFragment
                )
            }
        })

        viewModel.loading.observe(viewLifecycleOwner, { isLoading ->
            isLoading?.let {
                with(binding) {
                    progressLoad.visibility = if (isLoading) View.VISIBLE else View.GONE
                    rvWatchList.visibility = if (isLoading) View.GONE else View.VISIBLE
                }
            }
        })

        viewModel.loadEmpty.observe(viewLifecycleOwner, { isEmpty ->
            isEmpty?.let {
                with(binding) {
                    tvEmpty.visibility = if (isEmpty) View.VISIBLE else View.GONE
                    progressLoad.visibility = if (isEmpty) View.GONE else View.VISIBLE
                    rvWatchList.visibility = if (isEmpty) View.GONE else View.VISIBLE
                }
            }
        })
    }
}