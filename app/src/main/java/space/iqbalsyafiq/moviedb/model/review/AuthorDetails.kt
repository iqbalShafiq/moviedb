package space.iqbalsyafiq.moviedb.model.review

import com.google.gson.annotations.SerializedName

data class AuthorDetails(
    @SerializedName("avatar_path")
    val avatarPath: String?,
    val name: String?,
    val rating: Double?,
    val username: String?
)