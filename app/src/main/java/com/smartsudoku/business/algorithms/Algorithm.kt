package com.smartsudoku.business.algorithms

import com.smartsudoku.business.*

interface Algorithm {
    val id: Int
    fun where(grid: Grid<EnhancedCell>): Boolean
    fun apply(grid: Grid<out Cell>): Boolean

    companion object {
        fun eraseNoteSel(grid: Grid<out Cell>, s: Selector, n: Int): Boolean {
            var ret = false
            for (p in s)
                ret = ret or grid.get(p).unsetNote(n)
            return ret
        }

        fun smartToggleVal(grid: Grid<out Cell>, row: Int, col: Int, n: Int) {
            if (Manager.autoUpdateNotes && !Manager.wouldBeWrong(row, col, n) && grid.get(row, col)
                    .getVal() != n
            )
                eraseNoteSel(grid, Selector.groupsCored(row, col), n)
            grid.get(row, col).toggleVal(n)
        }

        fun smartConvertToPen(grid: Grid<out Cell>, row: Int, col: Int) {
            grid.get(row, col).convertToPen()
            if (Manager.autoUpdateNotes)
                eraseNoteSel(grid, Selector.groupsCored(row, col), grid.get(row, col).getVal())
        }
    }
}
