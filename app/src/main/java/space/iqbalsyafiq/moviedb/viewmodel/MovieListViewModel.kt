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
//    private val refreshTime = 30 * 60 * 1000 * 1000 * 1000L
    private var movieList: MutableList<Movie> = mutableListOf()

    // Live Data
    val movies = MutableLiveData<List<Movie>>()
    val loadError = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()
    val searching = MutableLiveData<Boolean>()

    fun refresh(category: String, page: Int = 1) {
        /** Error when paginating
        val updateTime = prefHelper.getListUpdateTime(category)
        if (
        updateTime != null &&
        updateTime != 0L &&
        System.nanoTime() - updateTime < refreshTime &&
        page == 1
        ) {
        fetchFromDatabaseByCategory(category)
        } else {
        loading.value = true
        when (category) {
        "Now Playing" -> fetchNowPlayingRemotely(page)
        "Top Rated" -> fetchTopRatedRemotely(page)
        "Popular" -> fetchPopularRemotely(page)
        "Upcoming" -> fetchUpcomingRemotely(page)
        }
        }
         **/

        if (page == 1) movieList.clear()
        loading.value = true
        when (category) {
            "Now Playing" -> fetchNowPlayingRemotely(page)
            "Top Rated" -> fetchTopRatedRemotely(page)
            "Popular" -> fetchPopularRemotely(page)
            "Upcoming" -> fetchUpcomingRemotely(page)
        }
    }

    fun refreshBypassCache(category: String) {
        movieList.clear()
        loading.value = true
        when (category) {
            "Now Playing" -> fetchNowPlayingRemotely(1)
            "Top Rated" -> fetchTopRatedRemotely(1)
            "Popular" -> fetchPopularRemotely(1)
            "Upcoming" -> fetchUpcomingRemotely(1)
        }
    }

    fun searchMovies(title: String, selectedCategory: String) {
        Log.d(TAG, "searchMovies: $title")
        Log.d(TAG, "searchMovies: ${title.isBlank()}")

        if (title.isBlank()) {
            searching.value = false
            when (selectedCategory) {
                "Now Playing" -> fetchNowPlayingRemotely()
                "Top Rated" -> fetchTopRatedRemotely()
                "Popular" -> fetchPopularRemotely()
                "Upcoming" -> fetchUpcomingRemotely()
            }
        } else {
            getSearchResult(title)
        }
    }

    private fun getSearchResult(title: String) {
        loading.value = true
        searching.value = true

        disposable.add(
            movieService.getSearchResult(title)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<MovieListResponse>() {
                    override fun onSuccess(listMovie: MovieListResponse) {
                        storeMovieLocally("Search Result", listMovie.results)
                    }

                    override fun onError(e: Throwable) {
                        loadError.value = true
                        loading.value = false
                        Log.d(TAG, "onError: $e")
                    }

                })
        )
    }

    /**
    private fun fetchFromDatabaseByCategory(category: String) {
    Log.d(TAG, "fetchFrom: database")
    loading.value = true
    launch {
    val listMovie = MovieDatabase(getApplication()).movieDao().getMoviesByCategory(category)
    retrieveMovie(listMovie)
    }
    }
     */

    private fun fetchUpcomingRemotely(page: Int = 1) {
        disposable.add(
            movieService.getUpcomingMovies(page)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<MovieListResponse>() {
                    override fun onSuccess(listMovie: MovieListResponse) {
                        val newMovieList = listMovie.results - movieList.toSet()
                        movieList.addAll(newMovieList)
                        storeMovieLocally("Upcoming", movieList)
                    }

                    override fun onError(e: Throwable) {
                        loadError.value = true
                        loading.value = false
                        Log.d(TAG, "onError: $e")
                    }

                })
        )
    }

    private fun fetchNowPlayingRemotely(page: Int = 1) {
        disposable.add(
            movieService.getNowPlayingMovies(page)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<MovieListResponse>() {
                    override fun onSuccess(listMovie: MovieListResponse) {
                        val newMovieList = listMovie.results - movieList.toSet()
                        Log.d(TAG, "fetchFrom: $newMovieList")
                        movieList.addAll(newMovieList)
                        storeMovieLocally("Now Playing", movieList)
                    }

                    override fun onError(e: Throwable) {
                        loadError.value = true
                        loading.value = false
                        Log.d(TAG, "onError: $e")
                    }

                })
        )
    }

    private fun fetchTopRatedRemotely(page: Int = 1) {
        disposable.add(
            movieService.getTopRatedMovies(page)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<MovieListResponse>() {
                    override fun onSuccess(listMovie: MovieListResponse) {
                        Log.d(TAG, "onSuccess: ${movies.value}")
                        val newMovieList = listMovie.results - movieList.toSet()
                        movieList.addAll(newMovieList)
                        storeMovieLocally("Top Rated", movieList)
                    }

                    override fun onError(e: Throwable) {
                        loadError.value = true
                        loading.value = false
                        Log.d(TAG, "onError: $e")
                    }

                })
        )
    }

    private fun fetchPopularRemotely(page: Int = 1) {
        disposable.add(
            movieService.getPopularMovies(page)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<MovieListResponse>() {
                    override fun onSuccess(listMovie: MovieListResponse) {
                        Log.d(TAG, "onSuccess: ${movies.value}")
                        val newMovieList = listMovie.results - movieList.toSet()
                        movieList.addAll(newMovieList)
                        storeMovieLocally("Popular", movieList)
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
            val iterator = listMovie.iterator()
            while (iterator.hasNext()) {
                // set category and check does it listed on watchlist
                val movie = iterator.next()
                movie.category = category
                if (watchList.contains(movie)) movie.isWatchListed = true

                // set rate
                val ratedMovie = dao.getRatingById(movie.id)
                if (ratedMovie != null) movie.rating = ratedMovie.rating
            }

            // store to roomDB
            dao.insertAll(*listMovie.toTypedArray())

            // retrieve movies from database
            retrieveMovie(listMovie)
        }

        Log.d(TAG, "storeMovieLocally: $category")
        prefHelper.saveListUpdateTime(System.nanoTime(), category)
    }

    fun storeWatchList(movie: Movie) {
        launch {
            // initiate dao & store to watchlist
            val dao = MovieDatabase(getApplication()).movieDao()
            val categoryMovie = movie.category
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
            retrieveMovie(dao.getMoviesByCategory(categoryMovie!!))
        }
    }

    private fun retrieveMovie(listMovie: List<Movie>) {
        movies.value = listMovie
        loadError.value = false
        loading.value = false
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

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

}