package space.iqbalsyafiq.moviedb.model.rating

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class GuestSessionResponse(
    @SerializedName("expires_at")
    val expiresAt: String?,
    @SerializedName("guest_session_id")
    @PrimaryKey
    val guestSessionId: String,
    val success: Boolean?
)