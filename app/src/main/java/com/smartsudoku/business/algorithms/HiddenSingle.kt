package com.smartsudoku.business.algorithms

import com.smartsudoku.business.Cell
import com.smartsudoku.business.EnhancedCell
import com.smartsudoku.business.Grid
import com.smartsudoku.business.Selector

object HiddenSingle : Algorithm {
    override val id = 3

    override fun where(grid: Grid<EnhancedCell>): Boolean {
        for (i in 0..8)
            for (j in 0..8)
                if (!grid.get(i, j).isMark()) {
                    for (n in 1..9)
                        if ((grid.get(i, j).contains(n)) && ((!Selector.rowCored(i, j)
                                .containsNote(grid, n) || !Selector.colCored(i, j)
                                .containsNote(grid, n) || !Selector.blockCored(i, j).containsNote(grid, n)))
                        ) {
                            grid.get(i, j).highlighted = true
                            return true
                        }
                }
        return false
    }

    override fun apply(grid: Grid<out Cell>): Boolean {
        var needed = false
        var more = true
        while (more) {
            more = false
            for (i in 0..8)
                for (j in 0..8) {
                    if (!grid.get(i, j).isMark()) {
                        for (n in 1..9)
                            if ((grid.get(i, j).contains(n)) && ((!Selector.rowCored(i, j)
                                    .containsNote(grid, n) || !Selector.colCored(i, j)
                                    .containsNote(grid, n) || !Selector.blockCored(i, j)
                                    .containsNote(grid, n)))
                            ) {
                                Algorithm.smartToggleVal(grid, i, j, n)
                                more = true
                                needed = true
                            }

                    }
                }
        }
        return needed
    }
}