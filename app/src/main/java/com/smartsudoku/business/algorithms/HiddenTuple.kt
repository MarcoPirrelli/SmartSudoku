package com.smartsudoku.business.algorithms

import com.smartsudoku.business.Cell
import com.smartsudoku.business.EnhancedCell
import com.smartsudoku.business.Grid
import com.smartsudoku.business.Selector

interface HiddenTuple : Algorithm {
    val tupleSize: Int

    override fun where(grid: Grid<EnhancedCell>): Boolean {
        for (i in 0..8) {
            val a = whereGroup(grid, Selector.row(i, 0))
            if (a) return true
            val b = whereGroup(grid, Selector.col(0, i))
            if (b) return true
            val c = whereGroup(grid, Selector.block(i / 3, i % 3))
            if (c) return true
        }
        return false
    }

    override fun apply(grid: Grid<out Cell>): Boolean {
        var needed = false
        for (i in 0..8) {
            needed = needed or applyGroup(grid, Selector.row(i, 0))
            needed = needed or applyGroup(grid, Selector.col(0, i))
            needed = needed or applyGroup(grid, Selector.block(i / 3 * 3, i % 3 * 3))
        }
        return needed
    }

    fun applyGroup(grid: Grid<out Cell>, group: Selector): Boolean {
        val selectorArray = Array(9) { Selector() }
        for (n in 1..9)
            for (cell in group)
                if (!grid.get(cell).isMark() && grid.get(cell).contains(n))
                    selectorArray[n - 1].add(cell)

        val notesInPile = arrayOfNulls<Int>(tupleSize)
        var start = 1
        var t = 0
        while (t < tupleSize) {
            for (n in start..9) {
                if ((t == 0 || notesInPile[t - 1]!! < n) && selectorArray[n - 1].size in 1..tupleSize) {
                    var pile = Selector()
                    for (i in 0 until t)
                        pile = pile + selectorArray[notesInPile[i]!! - 1]
                    pile = pile + selectorArray[n - 1]
                    if (pile.size <= tupleSize) {
                        notesInPile[t] = n
                        break
                    }
                }
            }
            if (notesInPile[t] == null) {
                if (t == 0 || start + tupleSize >= 9)
                    return false
                t--
                start++
            } else
                t++
        }

        var pile = Selector()
        for (i in notesInPile)
            pile = pile + selectorArray[i!! - 1]

        var needed = false

        for (n in 1..9)
            if (!notesInPile.contains(n))
                needed = needed or Algorithm.eraseNoteSel(grid, pile, n)
        return needed
    }

    fun whereGroup(grid: Grid<EnhancedCell>, group: Selector): Boolean {
        val selectorArray = Array(9) { Selector() }
        for (n in 1..9)
            for (cell in group)
                if (!grid.get(cell).isMark() && grid.get(cell).contains(n))
                    selectorArray[n - 1].add(cell)

        val notesInPile = arrayOfNulls<Int>(tupleSize)
        var start = 1
        var t = 0
        while (t < tupleSize) {
            for (n in start..9) {
                if ((t == 0 || notesInPile[t - 1]!! < n) && selectorArray[n - 1].size in 1..tupleSize) {
                    var pile = Selector()
                    for (i in 0 until t)
                        pile = pile + selectorArray[notesInPile[i]!! - 1]
                    pile = pile + selectorArray[n - 1]
                    if (pile.size <= tupleSize) {
                        notesInPile[t] = n
                        break
                    }
                }
            }
            if (notesInPile[t] == null) {
                if (t == 0 || start + tupleSize >= 9)
                    return false
                t--
                start++
            } else
                t++
        }

        var pile = Selector()
        for (i in notesInPile)
            pile = pile + selectorArray[i!! - 1]

        var needed = false
        for (n in 1..9)
            if (!notesInPile.contains(n))
                needed = needed || pile.containsNote(grid, n)

        if (needed)
            for (cell in pile)
                grid.get(cell).highlighted = true

        return needed
    }
}