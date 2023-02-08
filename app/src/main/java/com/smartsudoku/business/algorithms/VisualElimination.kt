package com.smartsudoku.business.algorithms

import com.smartsudoku.business.Cell
import com.smartsudoku.business.EnhancedCell
import com.smartsudoku.business.Grid
import com.smartsudoku.business.Selector

object VisualElimination : Algorithm {
    override val id = 1

    override fun where(grid: Grid<EnhancedCell>): Boolean {
        for (i in 0..8)
            for (j in 0..8)
                if (grid.get(i, j).isMark() and Selector.groupsCored(i, j)
                        .containsNote(grid, grid.get(i, j).getVal())
                ) {
                    grid.get(i, j).highlighted = true
                    return true
                }
        return false
    }

    override fun apply(grid: Grid<out Cell>): Boolean {
        var needed = false
        for (i in 0..8)
            for (j in 0..8)
                if (grid.get(i, j).isMark())
                    needed = needed or Algorithm.eraseNoteSel(
                        grid,
                        Selector.groupsCored(i, j),
                        grid.get(i, j).getVal()
                    )
        return needed
    }


}