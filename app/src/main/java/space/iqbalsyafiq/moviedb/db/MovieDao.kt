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

    // Get watch list by id
    @Query("SELECT * FROM movie WHERE id = :movieId")
    suspend fun getWatchListById(movieId: Int): Movie

    // Delete watch list by id
    @Query("DELETE FROM movie WHERE (category = 'Watch List' AND id = :movieId)")
    suspend fun deleteWatchListById(movieId: Int)

    // Set watch list by id
    @Query("UPDATE movie SET isWatchListed = :isWatchListed WHERE id = :movieId")
    suspend fun setWatchListById(movieId: Int, isWatchListed: Boolean)
}