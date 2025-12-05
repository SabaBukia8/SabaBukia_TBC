package com.example.sababukia_tbc.presentation.mapper

import com.example.sababukia_tbc.domain.model.WorkspaceItem
import com.example.sababukia_tbc.presentation.model.WorkspaceUiModel

fun WorkspaceItem.toUi(): WorkspaceUiModel = WorkspaceUiModel(
    location = location,
    altitudeM = altitudeM,
    title = title,
    image = image,
    stars = stars,
    price = price
)
