package space.iqbalsyafiq.moviedb.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_movie.view.*
import space.iqbalsyafiq.moviedb.R
import space.iqbalsyafiq.moviedb.constants.ApiConstants.Companion.IMAGE_BASE_URL
import space.iqbalsyafiq.moviedb.databinding.ItemMovieBinding
import space.iqbalsyafiq.moviedb.model.movie.Movie
import space.iqbalsyafiq.moviedb.utils.getProgressDrawable
import space.iqbalsyafiq.moviedb.utils.loadImage
import space.iqbalsyafiq.moviedb.view.fragments.HomeFragment
import space.iqbalsyafiq.moviedb.view.fragments.WatchListFragment

class MovieListAdapter(
    val context: Context,
    val listMovie: ArrayList<Movie>,
    val fragment: Fragment
) : RecyclerView.Adapter<MovieListAdapter.ViewHolder>() {

    private lateinit var binding: ItemMovieBinding

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemMovieBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )

        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = listMovie[position]
        with(holder.itemView) {
            tvMovieTitle.text = movie.title
            tvMovieReleaseDate.text = movie.releaseDate
            tvAverageRating.text = movie.voteAverage.toString()
            ivMoviePoster.loadImage(
                "${IMAGE_BASE_URL}${movie.posterPath}",
                getProgressDrawable(context)
            )

            // Watch List
            ivBookmark.setOnClickListener {
                if (fragment is HomeFragment) fragment.watchListed(movie)
                else if (fragment is WatchListFragment) fragment.deleteWatchListById(movie.id)
            }

            if (movie.isWatchListed) ivBookmark.setBackgroundResource(R.drawable.ic_bookmarked)
            else ivBookmark.setBackgroundResource(R.drawable.ic_bookmark)

            // Rating
            ivRate.setOnClickListener {
                if (fragment is HomeFragment) fragment.showDialogRating(movie)
                else if (fragment is WatchListFragment) fragment.showDialogRating(movie)
            }

            if (movie.rating != 0.0) {
                ivRate.setBackgroundResource(R.drawable.ic_rate_after)
                tvRating.text = movie.rating.toString()
            } else ivRate.setBackgroundResource(R.drawable.ic_rate_before)
        }
    }

    override fun getItemCount(): Int = listMovie.size

    @SuppressLint("NotifyDataSetChanged")
    fun clearMovieList() {
        listMovie.clear()
        notifyDataSetChanged()
    }
}