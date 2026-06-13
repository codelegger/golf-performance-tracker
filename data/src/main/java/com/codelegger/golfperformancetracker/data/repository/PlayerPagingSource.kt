package com.codelegger.golfperformancetracker.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.codelegger.golfperformancetracker.data.mapper.toDomain
import com.codelegger.golfperformancetracker.data.mapper.toEntity
import com.codelegger.golfperformancetracker.data.remote.GolfApi
import com.codelegger.golfperformancetracker.domain.model.Player
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Paging 3 [PagingSource] that loads players page-by-page from the API.
 *
 * MockAPI is 1-indexed (`?page=1&limit=…`). A short page (fewer items than requested) means
 * we've reached the end, so [nextKey] becomes null.
 *
 * Note: this network-backed source demonstrates Paging 3 end-to-end. A production offline-first
 * version would use a `RemoteMediator` writing pages into Room (Room as the PagingSource) — see
 * the PR description.
 */
class PlayerPagingSource(
    private val api: GolfApi,
    private val ioDispatcher: CoroutineDispatcher,
) : PagingSource<Int, Player>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Player> =
        withContext(ioDispatcher) {
            val page = params.key ?: STARTING_PAGE
            try {
                val dtos = api.getPlayersPaged(page = page, limit = params.loadSize)
                val players = dtos.map { it.toEntity().toDomain() }
                LoadResult.Page(
                    data = players,
                    prevKey = if (page == STARTING_PAGE) null else page - 1,
                    nextKey = if (dtos.size < params.loadSize) null else page + 1,
                )
            } catch (e: Exception) {
                LoadResult.Error(e)
            }
        }

    override fun getRefreshKey(state: PagingState<Int, Player>): Int? =
        state.anchorPosition?.let { anchor ->
            val closest = state.closestPageToPosition(anchor)
            closest?.prevKey?.plus(1) ?: closest?.nextKey?.minus(1)
        }

    private companion object {
        const val STARTING_PAGE = 1
    }
}
