package space.iqbalsyafiq.moviedb.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import space.iqbalsyafiq.moviedb.api.MovieApiService
import space.iqbalsyafiq.moviedb.db.MovieDatabase
import space.iqbalsyafiq.moviedb.model.movie.Movie
import space.iqbalsyafiq.moviedb.model.movie.MovieListResponse
import space.iqbalsyafiq.moviedb.utils.SharedPreferencesHelper

class MovieListViewModel(application: Application) :
    BaseViewModel(application) {

    val TAG = "MovieListViewModel"

    private val movieService = MovieApiService()
    private val disposable = CompositeDisposable()
    private val prefHelper = SharedPreferencesHelper(getApplication())
    private val refreshTime = 5 * 60 * 1000 * 1000 * 1000L

    val movies = MutableLiveData<List<Movie>>()
    val loadError = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()

    fun refresh(category: String) {
        val updateTime = prefHelper.getListUpdateTime("List Time $category")

        if (updateTime != null && updateTime != 0L && System.nanoTime() - updateTime < refreshTime) {
            fetchFromDatabaseByCategory(category)
        } else {
            when (category) {
                "Now Playing" -> fetchNowPlayingRemotely()
                "Top Rated" -> fetchTopRatedRemotely()
                "Popular" -> fetchPopularRemotely()
                "Upcoming" -> fetchUpcomingRemotely()
            }
        }
    }

    fun refreshBypassCache(category: String) {
        when (category) {
            "Now Playing" -> fetchNowPlayingRemotely()
            "Top Rated" -> fetchTopRatedRemotely()
            "Popular" -> fetchPopularRemotely()
            "Upcoming" -> fetchUpcomingRemotely()
        }
    }

    private fun fetchFromDatabaseByCategory(category: String) {
        loading.value = true
        launch {
            val listMovie = MovieDatabase(getApplication()).movieDao().getMoviesByCategory(category)
            retrieveMovie(listMovie)
        }
    }

    private fun fetchUpcomingRemotely() {
        loading.value = true

        disposable.add(
            movieService.getUpcomingMovies()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<MovieListResponse>() {
                    override fun onSuccess(listMovie: MovieListResponse) {
                        Log.d(TAG, "onSuccess: ${movies.value}")
                        storeMovieLocally("Upcoming", listMovie.results)
                    }

                    override fun onError(e: Throwable) {
                        loadError.value = true
                        loading.value = false
                        Log.d(TAG, "onError: $e")
                    }

                })
        )
    }

    private fun fetchNowPlayingRemotely() {
        loading.value = true

        disposable.add(
            movieService.getNowPlayingMovies()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<MovieListResponse>() {
                    override fun onSuccess(listMovie: MovieListResponse) {
                        Log.d(TAG, "onSuccess: ${movies.value}")
                        storeMovieLocally("Now Playing", listMovie.results)
                    }

                    override fun onError(e: Throwable) {
                        loadError.value = true
                        loading.value = false
                        Log.d(TAG, "onError: $e")
                    }

                })
        )
    }

    private fun fetchTopRatedRemotely() {
        loading.value = true

        disposable.add(
            movieService.getTopRatedMovies()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<MovieListResponse>() {
                    override fun onSuccess(listMovie: MovieListResponse) {
                        Log.d(TAG, "onSuccess: ${movies.value}")
                        storeMovieLocally("Top Rated", listMovie.results)
                    }

                    override fun onError(e: Throwable) {
                        loadError.value = true
                        loading.value = false
                        Log.d(TAG, "onError: $e")
                    }

                })
        )
    }

    private fun fetchPopularRemotely() {
        loading.value = true

        disposable.add(
            movieService.getPopularMovies()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<MovieListResponse>() {
                    override fun onSuccess(listMovie: MovieListResponse) {
                        Log.d(TAG, "onSuccess: ${movies.value}")
                        storeMovieLocally("Popular", listMovie.results)
                    }

                    override fun onError(e: Throwable) {
                        loadError.value = true
                        loading.value = false
                        Log.d(TAG, "onError: $e")
                    }

                })
        )
    }

    private fun storeMovieLocally(category: String, listMovie: List<Movie>) {
        launch {
            // initiate dao and clear all
            val dao = MovieDatabase(getApplication()).movieDao()
            dao.deleteMoviesByCategory(category)

            // get watch list
            val watchList = dao.getMoviesByCategory("Watch List")

            // set the category of movie
            for (movie in listMovie) {
                movie.category = category
                if (watchList.contains(movie)) movie.isWatchListed = true
            }

            // store to roomDB
            dao.insertAll(*listMovie.toTypedArray())

            // retrieve movies from database
            retrieveMovie(listMovie)
        }

        prefHelper.saveListUpdateTime(System.nanoTime(), "List Time $category")
    }

    fun storeWatchList(movie: Movie, category: String) {
        launch {
            // initiate dao & store to watchlist
            val dao = MovieDatabase(getApplication()).movieDao()
            Log.d(TAG, "storeWatchList1: ${movie.uuid}")

            // store watchlist to roomDB
            movie.isWatchListed = !movie.isWatchListed
            if (movie.isWatchListed) {
                // create new movie to roomDB
                movie.category = "Watch List"
                movie.uuid = System.nanoTime().toInt()
                dao.insertAll(movie)
            } else {
                dao.deleteWatchListById(movie.id)
            }

            // update watch list stats
            dao.setWatchListById(movie.id, movie.isWatchListed)

            // get movie by category
            retrieveMovie(dao.getMoviesByCategory(category))
        }
    }

    private fun retrieveMovie(listMovie: List<Movie>) {
        movies.value = listMovie
        loadError.value = false
        loading.value = false
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

}