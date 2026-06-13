package com.codelegger.golfperformancetracker.ui.players

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.codelegger.golfperformancetracker.domain.model.Player
import com.codelegger.golfperformancetracker.domain.repository.PlayerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Paging 3 showcase: exposes a `Flow<PagingData<Player>>` loaded page-by-page from the API.
 * `cachedIn(viewModelScope)` keeps the paged data across configuration changes.
 *
 * (Search was dropped on this showcase branch to keep the paging path clean; with paging it
 * would move to a server-side `?search=` param or a Room `LIKE` PagingSource.)
 */
@HiltViewModel
class PlayerListViewModel @Inject constructor(
    repository: PlayerRepository,
) : ViewModel() {

    val players: Flow<PagingData<Player>> =
        repository.pagedPlayers().cachedIn(viewModelScope)
}
