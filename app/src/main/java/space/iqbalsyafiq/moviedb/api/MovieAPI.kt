package space.iqbalsyafiq.moviedb.api

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query
import space.iqbalsyafiq.moviedb.constants.ApiConstants
import space.iqbalsyafiq.moviedb.model.movie.MovieListResponse

interface MovieAPI {

    @GET("movie/top_rated")
    fun getTopRatedMovies(
        @Query("api_key") apiKey: String = ApiConstants.API_KEY,
        @Query("page") page: Int = 1
    ): Single<MovieListResponse>

    @GET("movie/popular")
    fun getPopularMovies(
        @Query("api_key") apiKey: String = ApiConstants.API_KEY,
        @Query("page") page: Int = 1
    ): Single<MovieListResponse>

    @GET("movie/now_playing")
    fun getNowPlayingMovies(
        @Query("api_key") apiKey: String = ApiConstants.API_KEY,
        @Query("page") page: Int = 1
    ): Single<MovieListResponse>

    @GET("movie/upcoming")
    fun getUpcomingMovies(
        @Query("api_key") apiKey: String = ApiConstants.API_KEY,
        @Query("page") page: Int = 1
    ): Single<MovieListResponse>
}