package com.example.sababukia_tbc.domain.usecase

import com.example.sababukia_tbc.domain.model.Result
import com.example.sababukia_tbc.domain.model.WorkspaceError
import com.example.sababukia_tbc.domain.model.WorkspaceItem
import com.example.sababukia_tbc.domain.repository.WorkspaceRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetWorkspacesUseCaseTest {

    private val repository: WorkspaceRepository = mockk()
    private val useCase = GetWorkspacesUseCase(repository)

    private val sampleWorkspaces = listOf(
        WorkspaceItem(
            location = "Tbilisi",
            altitudeM = 500,
            title = "Office A",
            image = "https://example.com/a.jpg",
            stars = 4,
            price = 100
        )
    )

    @Test
    fun `success returns workspace list`() = runTest {
        coEvery { repository.getWorkspaces() } returns Result.Success(sampleWorkspaces)

        val result = useCase()

        assertTrue(result is Result.Success)
        assertEquals(sampleWorkspaces, (result as Result.Success).data)
    }

    @Test
    fun `error returns WorkspaceError`() = runTest {
        coEvery { repository.getWorkspaces() } returns Result.Error(WorkspaceError.Network)

        val result = useCase()

        assertTrue(result is Result.Error)
        assertEquals(WorkspaceError.Network, (result as Result.Error).error)
    }
}
