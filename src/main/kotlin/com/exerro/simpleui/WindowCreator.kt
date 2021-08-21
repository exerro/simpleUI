package com.exerro.simpleui

/** An object capable of creating and managing windows. */
interface WindowCreator {
    /** Create a window with the given [title]. */
    fun createWindow(
        title: String
    ): Window

    /** Handle any updates or events for all open windows from this
     *  [WindowCreator]. */
    fun update()
}
