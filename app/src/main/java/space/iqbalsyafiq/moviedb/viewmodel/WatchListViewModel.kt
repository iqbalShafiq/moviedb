package space.iqbalsyafiq.moviedb.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.launch
import space.iqbalsyafiq.moviedb.db.MovieDatabase
import space.iqbalsyafiq.moviedb.model.movie.Movie

class WatchListViewModel(application: Application) : BaseViewModel(application) {

    private val disposable = CompositeDisposable()

    // Live Data
    val movies = MutableLiveData<List<Movie>>()
    val loadEmpty = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()

    fun refresh() {
        loading.value = true
        launch {
            val listMovie =
                MovieDatabase(getApplication()).movieDao().getMoviesByCategory("Watch List")
            retrieveMovie(listMovie)
        }
    }

    fun deleteWatchListById(id: Int) {
        launch {
            val dao = MovieDatabase(getApplication()).movieDao()
            dao.deleteWatchListById(id)

            val listMovie =
                MovieDatabase(getApplication()).movieDao().getMoviesByCategory("Watch List")
            retrieveMovie(listMovie)
        }
    }

    fun rateMovie(rating: String, movie: Movie) {
        launch {
            val dao = MovieDatabase(getApplication()).movieDao()
            val category = movie.category
            dao.setRatingById(movie.id, rating.toDouble())

            // set category
            movie.category = "Rated"
            movie.uuid = System.nanoTime().toInt()

            // set rating and insert
            movie.rating = rating.toDouble()
            dao.insertAll(movie)

            // retrieve by category
            retrieveMovie(dao.getMoviesByCategory(category!!))
        }
    }

    private fun retrieveMovie(listMovie: List<Movie>) {
        movies.value = listMovie
        loadEmpty.value = listMovie.isEmpty()
        loading.value = false
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}