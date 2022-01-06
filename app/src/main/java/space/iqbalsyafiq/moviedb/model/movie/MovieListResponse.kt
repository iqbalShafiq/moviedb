package space.iqbalsyafiq.moviedb.model.movie

data class MovieListResponse(
    val page: Int,
    val results: List<MovieResponse>
)