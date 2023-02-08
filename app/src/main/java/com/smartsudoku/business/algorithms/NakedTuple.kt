package com.smartsudoku.business.algorithms

import com.smartsudoku.business.Cell
import com.smartsudoku.business.EnhancedCell
import com.smartsudoku.business.Grid
import com.smartsudoku.business.Selector

interface NakedTuple : Algorithm {
    var tupleSize: Int
    override fun where(grid: Grid<EnhancedCell>): Boolean {
        for (i in 0..8) {
            val nakedTuple = getNakedTuple(grid, Selector.row(i, 0))
            if (nakedTuple != null) {
                for(cell in nakedTuple){
                    grid.get(cell).highlighted=true
                }
                return true
            }
        }
        for (j in 0..8) {
            val nakedTuple = getNakedTuple(grid, Selector.col(0, j))
            if (nakedTuple != null) {
                for(cell in nakedTuple){
                    grid.get(cell).highlighted=true
                }
                return true
            }
        }
        for (i in 0..6 step 3)
            for (j in 0..6 step 3) {
                val nakedTuple = getNakedTuple(grid, Selector.block(i, j))
                if (nakedTuple != null) {
                    for(cell in nakedTuple){
                        grid.get(cell).highlighted=true
                    }
                    return true
                }
            }
        return false
    }

    override fun apply(grid: Grid<out Cell>): Boolean {
        var useful = false
        for (i in 0..8) {
            val nakedTuple = getNakedTuple(grid, Selector.row(i, 0))
            if (nakedTuple != null) {
                useful = true
                applyNakedTuple(grid, Selector.row(i, 0), nakedTuple)
            }
        }
        for (j in 0..8) {
            val nakedTuple = getNakedTuple(grid, Selector.col(0, j))
            if (nakedTuple != null) {
                useful = true
                applyNakedTuple(grid, Selector.col(0, j), nakedTuple)
            }
        }
        for (i in 0..6 step 3)
            for (j in 0..6 step 3) {
                val nakedTuple = getNakedTuple(grid, Selector.block(i, j))
                if (nakedTuple != null) {
                    useful = true
                    applyNakedTuple(grid, Selector.block(i, j), nakedTuple)
                }
            }
        return useful
    }

    fun getNakedTuple(
        grid: Grid<out Cell>,
        group: Selector
    ): Selector? {
        val eligible: Selector = group.copy()
        val iterator: MutableIterator<Int> = eligible.iterator()
        while (iterator.hasNext()) {
            val i = iterator.next()
            if (grid.get(i).isMark() || grid.get(i).isEmpty() || grid.get(i)
                    .countNotes() > tupleSize
            )
                iterator.remove()
        }
        if (eligible.size < tupleSize)
            return null
        return testTuples(grid, group, eligible)
    }

    fun testTuples(
        grid: Grid<out Cell>,
        group: Selector,
        eligible: Selector,
        tuple: Selector = Selector()
    ): Selector? {
        if (tuple.size == tupleSize) {
            if (isUsefulNakedTuple(grid, group, tuple))
                return tuple
            else
                return null
        }
        for (p in eligible) {
            if (tuple.isEmpty() || p > tuple.last()) {
                tuple.add(p)
                val found = testTuples(grid, group, eligible, tuple)
                if (found != null) return found
                tuple.remove(p)
            }
        }
        return null
    }

    fun isUsefulNakedTuple(grid: Grid<out Cell>, group: Selector, tuple: Selector): Boolean {
        var cell = Cell()
        for (p in tuple) {
            cell += grid.get(p)
        }
        if (cell.countNotes() <= tupleSize) {
            val others: Selector = group.copy()
            others.removeAll(tuple)
            for (i in 1..9) {
                if (cell.contains(i) && others.containsNote(grid, i)) {
                    return true
                }
            }
        }
        return false
    }

    fun applyNakedTuple(grid: Grid<out Cell>, group: Selector, tuple: Selector) {
        val others: Selector = group.copy()
        others.removeAll(tuple)
        for (n in 1..9) {
            if (tuple.containsNote(grid, n)) {
                Algorithm.eraseNoteSel(grid, others, n)
            }
        }
    }
}