package io.github.satadii11.animeqlist.ui.animelist

import AnimeListState
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.view.WindowInsetsControllerCompat
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.google.android.material.composethemeadapter.MdcTheme
import com.skydoves.landscapist.coil.CoilImage
import dagger.hilt.android.AndroidEntryPoint
import io.github.satadii11.animeqlist.R
import io.github.satadii11.type.MediaSort
import rememberAnimeListState

@AndroidEntryPoint
class AnimeListActivity : AppCompatActivity() {
    private val viewModel by viewModels<AnimeListViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowInsetsControllerCompat(window, window.decorView)
            .isAppearanceLightStatusBars = true
        setContent {
            MdcTheme {
                val state = rememberAnimeListState()
                val animeModels = viewModel.animes.collectAsLazyPagingItems()
                val requestOption by viewModel.requestOption.collectAsState()
                val shouldShowQuery by viewModel.shouldShowDialog.collectAsState()
                AnimeList(
                    state,
                    animeModels,
                    requestOption,
                    shouldShowQuery,
                    viewModel::onQueryChange,
                    viewModel::showDialog,
                    viewModel::onDialogShown,
                    viewModel::onSortSelected
                )
            }
        }
    }
}

private fun MediaSort.toResourceId(): Int = when (this) {
    MediaSort.SCORE_DESC -> R.string.sort_score_dsc
    MediaSort.SCORE -> R.string.sort_score
    MediaSort.START_DATE_DESC -> R.string.sort_start_dsc
    MediaSort.START_DATE -> R.string.sort_start
    else -> R.string.sort_unknown
}

@Composable
private fun AnimeList(
    state: AnimeListState,
    animeModels: LazyPagingItems<AnimeModel>,
    requestOption: RequestOption,
    shouldShowDialog: Boolean,
    onQueryChange: (String) -> Unit,
    showDialog: () -> Unit,
    onDialogShown: () -> Unit,
    onSortSelected: (MediaSort) -> Unit
) {
    Column {
        AnimeSearchBox(requestOption.query, onQueryChange)
        AnimeSortSwitch(requestOption.sortBy, showDialog)

        EmptyStateIfNeeded(animeModels, requestOption.query)
        ErrorStateIfNeeded(animeModels)
        SortDialogIfShown(shouldShowDialog, onDialogShown, onSortSelected)

        LazyColumn(state = state.lazyListState) {
            if (animeModels.loadState.refresh is LoadState.Loading) {
                item { Loading(Modifier.fillParentMaxSize()) }
            }

            items(animeModels) {
                it?.let { anime -> AnimeCard(animeModel = anime) }
            }

            if (animeModels.loadState.append == LoadState.Loading) {
                item { Loading(Modifier.fillParentMaxWidth()) }
            }
        }
    }
}

@Composable
private fun AnimeSearchBox(query: String?, onQueryChange: (String) -> Unit) {
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        value = query.orEmpty(),
        onValueChange = onQueryChange,
        label = { Text(stringResource(id = R.string.search_placeholder)) }
    )
}

@Composable
private fun AnimeSortSwitch(sortBy: MediaSort, showDialog: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = showDialog)
    ) {
        Icon(
            ImageVector.vectorResource(id = R.drawable.ic_baseline_sort_24),
            "Urutkan",
            tint = MaterialTheme.colors.primary
        )
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = stringResource(
                id = R.string.sort_by,
                stringResource(sortBy.toResourceId())
            ),
            color = MaterialTheme.colors.primary
        )
    }
}

@Composable
private fun EmptyStateIfNeeded(animeModels: LazyPagingItems<AnimeModel>, query: String?) {
    val refreshState = animeModels.loadState.refresh

    val isNotLoading = refreshState is LoadState.NotLoading
    val itemIsEmpty = animeModels.itemCount == 0
    val queryIsChanged = !query.isNullOrEmpty()
    val shouldShowEmptyState = isNotLoading && itemIsEmpty && queryIsChanged
    if (shouldShowEmptyState) {
        TextInfo(
            modifier = Modifier.fillMaxSize(),
            message = stringResource(id = R.string.anime_not_found, query.orEmpty())
        )
    }
}

@Composable
private fun ErrorStateIfNeeded(animeModels: LazyPagingItems<AnimeModel>) {
    val refreshState = animeModels.loadState.refresh
    if (refreshState is LoadState.Error) {
        TextInfo(modifier = Modifier.fillMaxSize(), message = refreshState.error.message.orEmpty())
    }
}

@Composable
private fun SortDialogIfShown(
    shouldShowDialog: Boolean,
    onDialogShown: () -> Unit,
    onSortSelected: (MediaSort) -> Unit
) {
    if (shouldShowDialog) {
        Dialog(onDismissRequest = onDialogShown) {
            Column(modifier = Modifier.background(Color.White, RoundedCornerShape(16.dp))) {
                SortOption(R.string.sort_score_dsc) {
                    onSortSelected(MediaSort.SCORE_DESC)
                    onDialogShown()
                }
                SortOption(R.string.sort_score) {
                    onSortSelected(MediaSort.SCORE)
                    onDialogShown()
                }
                SortOption(R.string.sort_start_dsc) {
                    onSortSelected(MediaSort.START_DATE_DESC)
                    onDialogShown()
                }
                SortOption(R.string.sort_start) {
                    onSortSelected(MediaSort.START_DATE)
                    onDialogShown()
                }
            }
        }
    }
}

@Composable
private fun SortOption(optionId: Int, onClickListener: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClickListener)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = stringResource(
                id = R.string.sort_with,
                stringResource(optionId)
            )
        )
    }
}

@Composable
private fun AnimeCard(animeModel: AnimeModel) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp, 4.dp),
        elevation = 8.dp,
        backgroundColor = if (animeModel.majorColor.isEmpty()) MaterialTheme.colors.primary
        else Color(android.graphics.Color.parseColor(animeModel.majorColor))
    ) {
        Row {
            CoilImage(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .width(100.dp)
                    .height(100.dp),
                imageModel = animeModel.coverImage
            )
            Column(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .align(Alignment.CenterVertically),
                verticalArrangement = Arrangement.Center
            ) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(animeModel.genres, key = { it }) {
                        Text(
                            color = Color.White,
                            text = it,
                            fontSize = 10.sp,
                            modifier = Modifier
                                .background(Color.Gray, CircleShape)
                                .padding(8.dp, 4.dp)
                        )
                    }
                }

                Text(
                    text = animeModel.title,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.onPrimary,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Average Score: ${animeModel.averageScore}%",
                    color = MaterialTheme.colors.onPrimary
                )
            }
        }
    }
}

@Composable
private fun Loading(modifier: Modifier) {
    Box(modifier) {
        CircularProgressIndicator(
            Modifier
                .align(Alignment.Center)
                .padding(top = 8.dp, bottom = 8.dp)
        )
    }
}

@Composable
private fun TextInfo(modifier: Modifier, message: String) {
    Box(modifier) {
        Text(
            text = message,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 16.dp),
            fontSize = 18.sp,
            color = MaterialTheme.colors.error
        )
    }
}

@Composable
@Preview
fun LoadingMatchParent() {
    Loading(Modifier.fillMaxSize())
}

@Composable
@Preview
fun LoadingWrapContent() {
    Loading(Modifier.fillMaxWidth())
}

@Composable
@Preview
fun TextInfoMatchParent() {
    TextInfo(Modifier.fillMaxSize(), "Anime \"One Piece Naruto Bleach\" gak ketemu nih.")
}

@Preview
@Composable
fun AnimeCardPreview() {
    Column {
        AnimeCard(
            AnimeModel(
                id = 1,
                title = "Ore No Youth",
                coverImage = "https://s4.anilist.co/file/anilistcdn/media/anime/cover/medium/bx1-CXtrrkMpJ8Zq.png",
                majorColor = "#f1785d",
                genres = listOf("Action", "Adventure", "Drama"),
                averageScore = 78
            )
        )

        AnimeCard(
            AnimeModel(
                id = 1,
                title = "Oregairu",
                coverImage = "https://s4.anilist.co/file/anilistcdn/media/anime/cover/medium/bx1-CXtrrkMpJ8Zq.png",
                majorColor = "#67f15d",
                genres = listOf("Action", "Adventure", "Drama"),
                averageScore = 78
            )
        )
    }
}
