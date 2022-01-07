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

    // Get top rated movies
    fun getTopRatedMovies(page: Int = 1): Single<MovieListResponse> {
        return api.getTopRatedMovies(page = page)
    }

    // Get popular movies
    fun getPopularMovies(page: Int = 1): Single<MovieListResponse> {
        return api.getPopularMovies(page = page)
    }

    // Get now playing movies
    fun getNowPlayingMovies(page: Int = 1): Single<MovieListResponse> {
        return api.getNowPlayingMovies(page = page)
    }

    // Get upcoming movies
    fun getUpcomingMovies(page: Int = 1): Single<MovieListResponse> {
        return api.getUpcomingMovies(page = page)
    }

    // Get search result
    fun getSearchResult(title: String = ""): Single<MovieListResponse> {
        return api.getSearchResult(query = title)
    }
}