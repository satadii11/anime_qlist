package io.github.satadii11.animeqlist.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.coroutines.await
import io.github.satadii11.GetAnimesQuery
import io.github.satadii11.type.MediaSort
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnimeRepository @Inject constructor(private val apolloClient: ApolloClient) {
    fun getAnimes(query: String?, sortBy: MediaSort) = Pager(
        config = PagingConfig(15),
        initialKey = 1,
        pagingSourceFactory = { GetAnimeSource(apolloClient, query, sortBy) }
    )
}

class GetAnimeSource @Inject constructor(
    private val apolloClient: ApolloClient,
    private val query: String?,
    private val sortBy: MediaSort
) : PagingSource<Int, GetAnimesQuery.Medium>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GetAnimesQuery.Medium> {
        val pageNumber = params.key ?: 1
        return try {
            val query = GetAnimesQuery(
                pageNumber,
                Input.optional(query),
                Input.optional(listOf(sortBy))
            )
            val result = apolloClient.query(query).await()

            var nextPageNumber: Int? = null
            if (result.data?.page?.pageInfo?.hasNextPage == true) {
                nextPageNumber = pageNumber + 1
            }

            val resultData = result.data?.page?.media?.filterNotNull().orEmpty()
            LoadResult.Page(resultData, null, nextPageNumber)
        } catch (ex: Exception) {
            LoadResult.Error(ex)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, GetAnimesQuery.Medium>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
