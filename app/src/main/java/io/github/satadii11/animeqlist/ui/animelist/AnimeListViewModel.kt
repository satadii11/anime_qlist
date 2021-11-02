package io.github.satadii11.animeqlist.ui.animelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.satadii11.animeqlist.data.repository.AnimeRepository
import io.github.satadii11.type.MediaSort
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import javax.inject.Inject

data class RequestOption(
    val query: String? = null,
    val sortBy: MediaSort = MediaSort.SCORE_DESC
)

@HiltViewModel
class AnimeListViewModel @Inject constructor(
    private val animeRepository: AnimeRepository,
    private val animeModelMapper: AnimeModelMapper
) : ViewModel() {
    private val _shouldShowDialog = MutableStateFlow(false)
    val shouldShowDialog: StateFlow<Boolean>
        get() = _shouldShowDialog

    private val _requestOption = MutableStateFlow(RequestOption())
    val requestOption: StateFlow<RequestOption>
        get() = _requestOption

    val animes = requestOption.flatMapLatest {
        animeRepository.getAnimes(it.query, it.sortBy)
            .flow
            .map { pagingData -> pagingData.map { data -> animeModelMapper.map(data)!! } }
    }.cachedIn(viewModelScope)

    fun onQueryChange(newQuery: String) {
        _requestOption.value = requestOption.value.copy(
            query = if (newQuery.isEmpty()) null else newQuery
        )
    }

    fun onSortSelected(mediaSort: MediaSort) {
        _requestOption.value = requestOption.value.copy(sortBy = mediaSort)
    }

    fun showDialog() {
        _shouldShowDialog.value = true
    }

    fun onDialogShown() {
        _shouldShowDialog.value = false
    }
}

