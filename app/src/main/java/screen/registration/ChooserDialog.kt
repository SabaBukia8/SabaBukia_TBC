package screen.registration

sealed class ChooserDialog {
    data class Show(val fieldId: Int, val title: String, val options: List<String>) : ChooserDialog()
    object Hide : ChooserDialog()
}