package com.app.ecarepro.core.domain.model

enum class HomeScreenType(
    val id: Int,
) {
    DASHBOARD(0),
    FEED_OR_TIMELINE(1),
    BOOKMARK(2);

    companion object {
        fun getHomeScreenTypeById(id: Int): HomeScreenType {
            return entries.find { it.id == id } ?: DASHBOARD
        }
    }
}