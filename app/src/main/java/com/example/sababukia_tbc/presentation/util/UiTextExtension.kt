package com.example.sababukia_tbc.presentation.util

import androidx.fragment.app.Fragment

fun UiText.asString(fragment: Fragment): String {
    return asString(fragment.requireContext())
}
