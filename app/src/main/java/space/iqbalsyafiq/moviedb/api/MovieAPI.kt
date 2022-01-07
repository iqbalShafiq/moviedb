package space.iqbalsyafiq.moviedb.api

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query
import space.iqbalsyafiq.moviedb.constants.ApiConstants
import space.iqbalsyafiq.moviedb.model.movie.MovieListResponse
import space.iqbalsyafiq.moviedb.model.rating.GuestSessionResponse

interface MovieAPI {

    // Get top rated movies
    @GET("movie/top_rated")
    fun getTopRatedMovies(
        @Query("api_key") apiKey: String = ApiConstants.API_KEY,
        @Query("page") page: Int = 1
    ): Single<MovieListResponse>

    // Get popular movies
    @GET("movie/popular")
    fun getPopularMovies(
        @Query("api_key") apiKey: String = ApiConstants.API_KEY,
        @Query("page") page: Int = 1
    ): Single<MovieListResponse>

    // Get now playing movies
    @GET("movie/now_playing")
    fun getNowPlayingMovies(
        @Query("api_key") apiKey: String = ApiConstants.API_KEY,
        @Query("page") page: Int = 1
    ): Single<MovieListResponse>

    // Get upcoming movies
    @GET("movie/upcoming")
    fun getUpcomingMovies(
        @Query("api_key") apiKey: String = ApiConstants.API_KEY,
        @Query("page") page: Int = 1
    ): Single<MovieListResponse>

    // Get guest session
    @GET("authentication/guest_session/new")
    fun getGuestSession(
        @Query("api_key") apiKey: String = ApiConstants.API_KEY
    ): Single<GuestSessionResponse>

    // Search movies
    @GET("search/movie")
    fun getSearchResult(
        @Query("api_key") apiKey: String = ApiConstants.API_KEY,
        @Query("query") query: String = ""
    ): Single<MovieListResponse>
}