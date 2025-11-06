package screen.card

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import data.RepositoryProvider

@Suppress("UNCHECKED_CAST")
class VmFactory(private val app: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repo = RepositoryProvider.cards(app)
        return when {
            modelClass.isAssignableFrom(CardViewModel::class.java) -> CardViewModel(repo) as T
            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}