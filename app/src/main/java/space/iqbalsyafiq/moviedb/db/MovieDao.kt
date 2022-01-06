package space.iqbalsyafiq.moviedb.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import space.iqbalsyafiq.moviedb.model.movie.Movie

@Dao
interface MovieDao {
    // Insert movie
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg movies: Movie): List<Long>

    // Get list of movie by category
    @Query("SELECT * FROM movie WHERE category = :movieCategory")
    suspend fun getMoviesByCategory(movieCategory: String): List<Movie>

    // Get movie by Id
    @Query("SELECT * FROM movie WHERE id = :movieId")
    suspend fun getMovie(movieId: Int): Movie

    // Delete all movie
    @Query("DELETE FROM movie")
    suspend fun deleteAllMovie()

    // Delete movies by category
    @Query("DELETE FROM movie WHERE category = :movieCategory")
    suspend fun deleteMoviesByCategory(movieCategory: String)
}