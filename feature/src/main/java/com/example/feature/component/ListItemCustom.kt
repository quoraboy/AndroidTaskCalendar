package com.example.feature.component

import com.example.core.home.model.HeaderViewState
import com.example.core.home.model.TaskViewState

sealed class ListItemCustom {
    object CalendarViewItemCustom : ListItemCustom()
    data class HeaderItemCustom(val headerViewState: HeaderViewState) : ListItemCustom()
    data class TaskItemCustom(val taskViewState: TaskViewState) : ListItemCustom()
    object FooterItemCustom : ListItemCustom()

}