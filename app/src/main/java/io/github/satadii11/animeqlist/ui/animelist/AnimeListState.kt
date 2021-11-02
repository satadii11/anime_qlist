import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

class AnimeListState(val lazyListState: LazyListState)

@Composable
fun rememberAnimeListState(
    lazyListState: LazyListState = rememberLazyListState()
) = remember(lazyListState) {
    AnimeListState(lazyListState)
}