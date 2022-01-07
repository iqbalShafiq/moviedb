package space.iqbalsyafiq.moviedb.model.movie

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Movie(
    val adult: Boolean?,
    val backdrop_path: String?,
    val budget: Int?,
    @SerializedName("genre_ids")
    val genreIds: List<Int>?,
    val id: Int,
    @SerializedName("imdb_id")
    val imdbId: String?,
    @SerializedName("original_language")
    val language: String?,
    @SerializedName("original_title")
    val title: String?,
    val overview: String?,
    val popularity: Double?,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("release_date")
    val releaseDate: String?,
    val runtime: Int?,
    val status: String?,
    val tagline: String?,
    @SerializedName("vote_average")
    val voteAverage: Double?,
    @SerializedName("vote_count")
    val voteCount: Int?
) {
    @PrimaryKey(autoGenerate = true)
    var uuid: Int = 0
    var category: String? = null
    var isWatchListed: Boolean = false
    var rating: Double = 0.0
}