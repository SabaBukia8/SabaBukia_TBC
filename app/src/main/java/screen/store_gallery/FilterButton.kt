package screen.store_gallery

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import model.CategoryType

@Parcelize
data class FilterButton(
    val text: String,
    val isSelected: Boolean = false,
    val category: CategoryType? = null
) : Parcelable