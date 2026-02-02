package com.example.sababukia_tbc.presentation.screen.registration

import app.cash.turbine.test
import com.example.sababukia_tbc.MainDispatcherRule
import com.example.sababukia_tbc.domain.model.FieldType
import com.example.sababukia_tbc.domain.model.FormField
import com.example.sababukia_tbc.domain.model.FormSection
import com.example.sababukia_tbc.domain.model.KeyboardType
import com.example.sababukia_tbc.domain.model.RegistrationError
import com.example.sababukia_tbc.domain.model.Result
import com.example.sababukia_tbc.domain.usecase.GetFormConfigurationUseCase
import com.example.sababukia_tbc.domain.usecase.ValidateFormUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RegistrationViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(testDispatcher)

    private lateinit var getFormConfigurationUseCase: GetFormConfigurationUseCase
    private lateinit var validateFormUseCase: ValidateFormUseCase

    private val testSections = listOf(
        FormSection(
            id = 0,
            fields = listOf(
                FormField(
                    id = 1,
                    hint = "UserName",
                    fieldType = FieldType.INPUT,
                    keyboardType = KeyboardType.TEXT,
                    isRequired = true,
                    isActive = true,
                    iconUrl = null
                ),
                FormField(
                    id = 2,
                    hint = "Birthday",
                    fieldType = FieldType.CHOOSER,
                    keyboardType = KeyboardType.TEXT,
                    isRequired = false,
                    isActive = true,
                    iconUrl = null
                )
            )
        )
    )

    @Before
    fun setUp() {
        getFormConfigurationUseCase = mockk()
        validateFormUseCase = ValidateFormUseCase()
    }

    @Test
    fun `initial state shows loading and fetches form configuration`() = runTest {
        coEvery { getFormConfigurationUseCase() } returns Result.Success(testSections)

        val viewModel = RegistrationViewModel(getFormConfigurationUseCase, validateFormUseCase)

        viewModel.state.test {
            skipItems(1) // Skip initial state

            testDispatcher.scheduler.advanceUntilIdle()

            val loadedState = expectMostRecentItem()
            assertFalse(loadedState.isLoading)
            assertEquals(testSections, loadedState.sections)
            assertNull(loadedState.error)
        }
    }

    @Test
    fun `shows error when form configuration fetch fails`() = runTest {
        coEvery { getFormConfigurationUseCase() } returns Result.Error(RegistrationError.Network)

        val viewModel = RegistrationViewModel(getFormConfigurationUseCase, validateFormUseCase)

        viewModel.state.test {
            skipItems(1)
            testDispatcher.scheduler.advanceUntilIdle()

            val state = expectMostRecentItem()
            assertFalse(state.isLoading)
            assertEquals("Network error. Please check your connection.", state.error)
        }
    }

    @Test
    fun `field value changed event updates state`() = runTest {
        coEvery { getFormConfigurationUseCase() } returns Result.Success(testSections)

        val viewModel = RegistrationViewModel(getFormConfigurationUseCase, validateFormUseCase)

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val initialState = awaitItem()
            assertEquals(testSections, initialState.sections)

            viewModel.onEvent(RegistrationEvent.FieldValueChanged(1, "testuser"))

            val state = awaitItem()
            assertEquals("testuser", state.fieldValues[1])
        }
    }

    @Test
    fun `field value changed clears error for that field`() = runTest {
        coEvery { getFormConfigurationUseCase() } returns Result.Success(testSections)

        val viewModel = RegistrationViewModel(getFormConfigurationUseCase, validateFormUseCase)

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            awaitItem() // Initial loaded state

            viewModel.onEvent(RegistrationEvent.Submit)
            val stateWithError = awaitItem()
            assertTrue(stateWithError.fieldErrors.containsKey(1))

            viewModel.onEvent(RegistrationEvent.FieldValueChanged(1, "testuser"))
            val stateWithoutError = awaitItem()
            assertFalse(stateWithoutError.fieldErrors.containsKey(1))
        }
    }

    @Test
    fun `chooser field click with birthday hint shows date picker`() = runTest {
        coEvery { getFormConfigurationUseCase() } returns Result.Success(testSections)

        val viewModel = RegistrationViewModel(getFormConfigurationUseCase, validateFormUseCase)

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            awaitItem()

            viewModel.onEvent(RegistrationEvent.ChooserFieldClicked(2, "Birthday"))

            val state = awaitItem()
            assertTrue(state.showDatePicker)
            assertEquals(2, state.activeChooserFieldId)
        }
    }

    @Test
    fun `chooser field click with gender hint shows gender picker`() = runTest {
        val sectionsWithGender = listOf(
            FormSection(
                id = 0,
                fields = listOf(
                    FormField(
                        id = 3,
                        hint = "Gender",
                        fieldType = FieldType.CHOOSER,
                        keyboardType = KeyboardType.TEXT,
                        isRequired = false,
                        isActive = true,
                        iconUrl = null
                    )
                )
            )
        )
        coEvery { getFormConfigurationUseCase() } returns Result.Success(sectionsWithGender)

        val viewModel = RegistrationViewModel(getFormConfigurationUseCase, validateFormUseCase)

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            awaitItem()

            viewModel.onEvent(RegistrationEvent.ChooserFieldClicked(3, "Gender"))

            val state = awaitItem()
            assertTrue(state.showGenderPicker)
            assertEquals(3, state.activeChooserFieldId)
        }
    }

    @Test
    fun `dismiss date picker hides dialog`() = runTest {
        coEvery { getFormConfigurationUseCase() } returns Result.Success(testSections)

        val viewModel = RegistrationViewModel(getFormConfigurationUseCase, validateFormUseCase)

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            awaitItem()

            viewModel.onEvent(RegistrationEvent.ChooserFieldClicked(2, "Birthday"))
            awaitItem()

            viewModel.onEvent(RegistrationEvent.DismissDatePicker)
            val state = awaitItem()
            assertFalse(state.showDatePicker)
            assertNull(state.activeChooserFieldId)
        }
    }

    @Test
    fun `date selected updates field value and hides dialog`() = runTest {
        coEvery { getFormConfigurationUseCase() } returns Result.Success(testSections)

        val viewModel = RegistrationViewModel(getFormConfigurationUseCase, validateFormUseCase)

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            awaitItem()

            viewModel.onEvent(RegistrationEvent.ChooserFieldClicked(2, "Birthday"))
            awaitItem()

            viewModel.onEvent(RegistrationEvent.DateSelected("01/01/2000"))
            val state = awaitItem()
            assertEquals("01/01/2000", state.fieldValues[2])
            assertFalse(state.showDatePicker)
            assertNull(state.activeChooserFieldId)
        }
    }

    @Test
    fun `gender selected updates field value and hides dialog`() = runTest {
        val sectionsWithGender = listOf(
            FormSection(
                id = 0,
                fields = listOf(
                    FormField(
                        id = 3,
                        hint = "Gender",
                        fieldType = FieldType.CHOOSER,
                        keyboardType = KeyboardType.TEXT,
                        isRequired = false,
                        isActive = true,
                        iconUrl = null
                    )
                )
            )
        )
        coEvery { getFormConfigurationUseCase() } returns Result.Success(sectionsWithGender)

        val viewModel = RegistrationViewModel(getFormConfigurationUseCase, validateFormUseCase)

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            awaitItem()

            viewModel.onEvent(RegistrationEvent.ChooserFieldClicked(3, "Gender"))
            awaitItem()

            viewModel.onEvent(RegistrationEvent.GenderSelected("Male"))
            val state = awaitItem()
            assertEquals("Male", state.fieldValues[3])
            assertFalse(state.showGenderPicker)
            assertNull(state.activeChooserFieldId)
        }
    }

    @Test
    fun `submit with validation errors shows field errors`() = runTest {
        coEvery { getFormConfigurationUseCase() } returns Result.Success(testSections)

        val viewModel = RegistrationViewModel(getFormConfigurationUseCase, validateFormUseCase)

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            awaitItem()

            viewModel.onEvent(RegistrationEvent.Submit)

            val state = awaitItem()
            assertTrue(state.fieldErrors.isNotEmpty())
            assertTrue(state.fieldErrors.containsKey(1))
        }
    }

    @Test
    fun `submit with valid form emits success side effect`() = runTest {
        coEvery { getFormConfigurationUseCase() } returns Result.Success(testSections)

        val viewModel = RegistrationViewModel(getFormConfigurationUseCase, validateFormUseCase)

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onEvent(RegistrationEvent.FieldValueChanged(1, "testuser"))

        viewModel.sideEffect.test {
            viewModel.onEvent(RegistrationEvent.Submit)
            testDispatcher.scheduler.advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is RegistrationSideEffect.RegistrationSuccess)
        }
    }

    @Test
    fun `submit with validation errors emits error side effect`() = runTest {
        coEvery { getFormConfigurationUseCase() } returns Result.Success(testSections)

        val viewModel = RegistrationViewModel(getFormConfigurationUseCase, validateFormUseCase)

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.sideEffect.test {
            viewModel.onEvent(RegistrationEvent.Submit)
            testDispatcher.scheduler.advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is RegistrationSideEffect.ShowError)
            assertEquals("Please fill in all required fields", (effect as RegistrationSideEffect.ShowError).message)
        }
    }

    @Test
    fun `network error emits side effect`() = runTest {
        coEvery { getFormConfigurationUseCase() } returns Result.Error(RegistrationError.Network)

        val viewModel = RegistrationViewModel(getFormConfigurationUseCase, validateFormUseCase)

        viewModel.sideEffect.test {
            testDispatcher.scheduler.advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is RegistrationSideEffect.ShowError)
            assertEquals("Network error. Please check your connection.", (effect as RegistrationSideEffect.ShowError).message)
        }
    }

    @Test
    fun `load form event reloads form configuration`() = runTest {
        coEvery { getFormConfigurationUseCase() } returns Result.Error(RegistrationError.Network) andThen Result.Success(testSections)

        val viewModel = RegistrationViewModel(getFormConfigurationUseCase, validateFormUseCase)

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val errorState = awaitItem()
            assertNotNull(errorState.error)

            viewModel.onEvent(RegistrationEvent.LoadForm)

            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            testDispatcher.scheduler.advanceUntilIdle()

            val loadedState = expectMostRecentItem()
            assertFalse(loadedState.isLoading)
            assertEquals(testSections, loadedState.sections)
            assertNull(loadedState.error)
        }
    }
}
