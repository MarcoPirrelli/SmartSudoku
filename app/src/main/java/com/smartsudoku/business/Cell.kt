package com.smartsudoku.business


open class Cell() : DeepCopy {
    var fixed: Boolean = false
    var value: Int = 0
    var values: BooleanArray = BooleanArray(9)

    constructor(cell: Cell) : this() {
        this.fixed = cell.fixed
        this.value = cell.value
        this.values = cell.values.clone()
    }

    fun reset() {
        fixed = false
        value = 0
        values.fill(false)
    }

    open fun erase() {
        if (fixed) return

        value = 0
        values.fill(false)
    }

    fun isFixed(): Boolean {
        return fixed
    }

    fun isMark(): Boolean {
        return value != 0
    }

    fun getVal(): Int {
        return value
    }

    fun countNotes(): Int {
        var count = 0
        for (i in values) if (i) count++
        return count
    }

    fun isEmpty(): Boolean {
        return countNotes() == 0
    }

    fun isSingle(): Boolean {
        return countNotes() == 1
    }

    operator fun contains(n: Int): Boolean {
        return values[n - 1]
    }

    fun unsetNote(n: Int) : Boolean {
        if (!fixed && value == 0 && values[n - 1]) {
            values[n - 1] = false
            return true
        }
        return false
    }

    fun toggleVal(n: Int) {
        if (fixed) return
        val ogVal = value
        erase()
        if (ogVal != n) {
            values[n - 1] = true
            value = n
        }
    }

    fun setInit(n: Int) {
        for (i in 0..8) values[i] = false
        values[n - 1] = true
        value = n
        fixed = true
    }

    fun toggleInit(n: Int) {
        for (i in 0..8) values[i] = false
        if (value == n) {
            fixed = false
            value = 0
        } else {
            fixed = true
            values[n - 1] = true
            value = n
        }
    }

    fun toggleNote(n: Int) {
        if (fixed) return
        if (value != 0) values[value - 1] = false
        value = 0
        values[n - 1] = !values[n - 1]
    }

    fun convertToPen() {
        for (i in 0..8) if (values[i]) {
            value = i + 1
            break
        }
    }

    open fun fillNotes() {
        if (fixed || value != 0) return
        for (i in 0..8) values[i] = true
    }

    operator fun plus(cell2: Cell): Cell {
        for (i in 0..8) {
            values[i] = values[i] || cell2.values[i]
        }
        return this
    }

    override fun copy(): Cell = Cell(this)

    override fun equals(other: Any?): Boolean {
        if (other !is Cell) return false
        for (i in 0..8) {
            if (values[i] != other.values[i]) return false
        }
        return true
    }
}