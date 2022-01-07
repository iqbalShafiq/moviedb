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
import space.iqbalsyafiq.moviedb.model.rating.GuestSessionResponse
import space.iqbalsyafiq.moviedb.utils.SharedPreferencesHelper

class MovieListViewModel(application: Application) :
    BaseViewModel(application) {

    val TAG = "MovieListViewModel"

    private val movieService = MovieApiService()
    private val disposable = CompositeDisposable()
    private val prefHelper = SharedPreferencesHelper(getApplication())
    private val refreshTime = 5 * 60 * 1000 * 1000 * 1000L
    private val sessionTime = 30 * 60 * 1000 * 1000 * 1000L
    private var queryTitle = ""

    // Live Data
    private val sessionId = MutableLiveData<String>()
    val movies = MutableLiveData<List<Movie>>()
    val loadError = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()
    val searching = MutableLiveData<Boolean>()

    fun getGuestSession() {
        val updateTime = prefHelper.getListUpdateTime("Session Time")

        if (updateTime != null && updateTime != 0L && System.nanoTime() - updateTime < sessionTime) {
            getSessionLocally()
        } else {
            getNewSession()
        }
    }

    private fun getNewSession() {
        disposable.add(
            movieService.getGuestSession()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<GuestSessionResponse>() {
                    override fun onSuccess(response: GuestSessionResponse) {
                        Log.d(TAG, "onSuccess Guest Session: $response")
                        storeSessionLocally(response)
                    }

                    override fun onError(e: Throwable) {
                        Log.d(TAG, "onError: $e")
                    }
                })
        )
    }

    private fun getSessionLocally() {
        launch {
            val dao = MovieDatabase(getApplication()).movieDao()
            val response = dao.getGuestSession()
            Log.d(TAG, "onSuccess Guest Session: $response")
            retrieveSession(response.guestSessionId)
        }
    }

    private fun storeSessionLocally(guestSessionResponse: GuestSessionResponse) {
        launch {
            val dao = MovieDatabase(getApplication()).movieDao()
            dao.deleteAllSession()

            dao.insertGuestSession(guestSessionResponse)
            retrieveSession(guestSessionResponse.guestSessionId)
        }

        prefHelper.saveListUpdateTime(System.nanoTime(), "Session Time")
    }

    private fun retrieveSession(guestId: String) {
        sessionId.value = guestId
    }

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
        queryTitle = title

        disposable.add(
            movieService.getSearchResult(title)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<MovieListResponse>() {
                    override fun onSuccess(listMovie: MovieListResponse) {
//                        launch {
//                            val dao = MovieDatabase(getApplication()).movieDao()
//
//                            // get watch list
//                            val watchList = dao.getMoviesByCategory("Watch List")
//
//                            for (movie in listMovie.results) {
//                                if (watchList.contains(movie)) movie.isWatchListed = true
//
//                                // set rate
//                                val ratedMovie = dao.getRatingById(movie.id)
//                                if (ratedMovie != null) {
//                                    Log.d(TAG, "onSuccess: ${ratedMovie.title}")
//                                    movie.rating = ratedMovie.rating
//                                }
//                            }
//
//                            movies.value = listMovie.results
//                        }
//
//                        loading.value = false
//                        loadError.value = false
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

    private fun refreshSearchResult(title: String) {
        loading.value = true
        searching.value = true
        queryTitle = title

        disposable.add(
            movieService.getSearchResult(title)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<MovieListResponse>() {
                    override fun onSuccess(listMovie: MovieListResponse) {
                        movies.value = listMovie.results
                        loading.value = false
                        loadError.value = false
                        Log.d(TAG, "onSuccess: ${movies.value}")
                    }

                    override fun onError(e: Throwable) {
                        loadError.value = true
                        loading.value = false
                        Log.d(TAG, "onError: $e")
                    }

                })
        )
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

            // get rated movie
            val ratedMovies = dao.getMoviesByCategory("Rated")

            // set the category of movie
            for (movie in listMovie) {
                // set category and check does it listed on watchlist
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

        prefHelper.saveListUpdateTime(System.nanoTime(), "List Time $category")
    }

    fun storeWatchList(movie: Movie, category: String) {
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

    fun rateMovie(rating: String, movie: Movie, queryTitle: String = "") {
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