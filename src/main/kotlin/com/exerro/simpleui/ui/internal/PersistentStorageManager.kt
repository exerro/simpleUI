package com.exerro.simpleui.ui.internal

import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.ui.PersistentStorageCell

@UndocumentedExperimentalUI
internal class PersistentStorageManager {
    @UndocumentedExperimentalUI
    fun resetIndex() {
        index = 0
    }

    @Suppress("UNCHECKED_CAST")
    @UndocumentedExperimentalUI
    fun <T> getNth(getInitialValue: () -> T) = getCell {
        object: PersistentStorageCell<T> {
            var currentValue = getInitialValue()
            override fun set(value: T) { currentValue = value }
            override fun get() = this.currentValue
        }
    }

    @UndocumentedExperimentalUI
    fun <T: Any> getNthLateInit() = getCell {
        object: PersistentStorageCell<T> {
            lateinit var currentValue: T
            override fun set(value: T) { currentValue = value }
            override fun get() = this.currentValue
        }
    }

    @Suppress("UNCHECKED_CAST")
    @UndocumentedExperimentalUI
    private fun <T> getCell(produceCell: () -> PersistentStorageCell<T>): PersistentStorageCell<T> {
        if (index <= cells.lastIndex)
            return cells[index++] as PersistentStorageCell<T>

        val cell = produceCell()

        cells.add(cell)
        ++index

        return cell
    }

    private var index = 0
    private val cells = mutableListOf<PersistentStorageCell<*>>()
}
