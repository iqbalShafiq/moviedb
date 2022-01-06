package space.iqbalsyafiq.moviedb.api

import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import space.iqbalsyafiq.moviedb.constants.ApiConstants
import space.iqbalsyafiq.moviedb.model.movie.MovieListResponse

class MovieApiService {
    private val api = Retrofit.Builder()
        .baseUrl(ApiConstants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(MovieAPI::class.java)

    fun getTopRatedMovies(page: Int = 1): Single<MovieListResponse> {
        return api.getTopRatedMovies(page = page)
    }

    fun getPopularMovies(page: Int = 1): Single<MovieListResponse> {
        return api.getPopularMovies(page = page)
    }

    fun getNowPlayingMovies(page: Int = 1): Single<MovieListResponse> {
        return api.getNowPlayingMovies(page = page)
    }

    fun getUpcomingMovies(page: Int = 1): Single<MovieListResponse> {
        return api.getUpcomingMovies(page = page)
    }
}