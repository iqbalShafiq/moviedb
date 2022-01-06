package space.iqbalsyafiq.moviedb.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import space.iqbalsyafiq.moviedb.api.MovieApiService
import space.iqbalsyafiq.moviedb.model.movie.MovieListResponse

class MovieListViewModel(application: Application) :
    BaseViewModel(application) {

    val TAG = "MovieListViewModel"

    private val movieService = MovieApiService()
    private val disposable = CompositeDisposable()

    val movies = MutableLiveData<MovieListResponse>()
    val loadError = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()

    fun refresh(category: String) {
        when (category) {
            "Now Playing" -> fetchNowPlayingRemotely()
            "Top Rated" -> fetchTopRatedRemotely()
            "Popular" -> fetchPopularRemotely()
            "Upcoming" -> fetchUpcomingRemotely()
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
                        movies.value = listMovie
                        loadError.value = false
                        loading.value = false
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

    private fun fetchNowPlayingRemotely() {
        loading.value = true

        disposable.add(
            movieService.getNowPlayingMovies()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<MovieListResponse>() {
                    override fun onSuccess(listMovie: MovieListResponse) {
                        movies.value = listMovie
                        loadError.value = false
                        loading.value = false
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

    private fun fetchTopRatedRemotely() {
        loading.value = true

        disposable.add(
            movieService.getTopRatedMovies()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<MovieListResponse>() {
                    override fun onSuccess(listMovie: MovieListResponse) {
                        movies.value = listMovie
                        loadError.value = false
                        loading.value = false
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

    private fun fetchPopularRemotely() {
        loading.value = true

        disposable.add(
            movieService.getPopularMovies()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<MovieListResponse>() {
                    override fun onSuccess(listMovie: MovieListResponse) {
                        movies.value = listMovie
                        loadError.value = false
                        loading.value = false
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

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

}