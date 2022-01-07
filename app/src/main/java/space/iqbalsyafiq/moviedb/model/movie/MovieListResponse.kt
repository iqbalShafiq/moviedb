package space.iqbalsyafiq.moviedb.model.movie

import com.google.gson.annotations.SerializedName

data class MovieListResponse(
    val page: Int,
    var results: List<Movie>,
    @SerializedName("total_pages")
    val totalPages: Int
)