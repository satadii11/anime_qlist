package io.github.satadii11.animeqlist.ui.animelist

import dagger.hilt.android.scopes.ViewModelScoped
import io.github.satadii11.GetAnimesQuery
import io.github.satadii11.animeqlist.map.Mapper
import javax.inject.Inject

data class AnimeModel(
    val id: Int,
    val title: String,
    val coverImage: String,
    val majorColor: String,
    val genres: List<String>,
    val averageScore: Int
)

@ViewModelScoped
class AnimeModelMapper @Inject constructor() : Mapper<GetAnimesQuery.Medium?, AnimeModel?> {
    override fun map(from: GetAnimesQuery.Medium?): AnimeModel? {
        if (from == null) return null
        return AnimeModel(
            id = from.id,
            title = from.title?.userPreferred.orEmpty(),
            coverImage = from.coverImage?.large.orEmpty(),
            majorColor = from.coverImage?.color.orEmpty(),
            genres = from.genres?.filterNotNull().orEmpty(),
            averageScore = from.averageScore ?: 0
        )
    }
}
