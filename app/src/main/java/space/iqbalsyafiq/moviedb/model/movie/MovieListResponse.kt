package space.iqbalsyafiq.moviedb.model.movie

data class MovieListResponse(
    val page: Int,
    var results: List<Movie>
)