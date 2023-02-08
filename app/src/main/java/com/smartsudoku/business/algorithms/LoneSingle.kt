package com.smartsudoku.business.algorithms

import com.smartsudoku.business.Cell
import com.smartsudoku.business.EnhancedCell
import com.smartsudoku.business.Grid
import com.smartsudoku.business.Selector

object LoneSingle : Algorithm {
    override val id = 2

    override fun where(grid: Grid<EnhancedCell>): Boolean {
        for (i in 0..8)
            for (j in 0..8)
                if (!grid.get(i, j).isMark() && grid.get(i, j).isSingle()) {
                    grid.get(i, j).highlighted = true
                    return true
                }
        return false
    }

    override fun apply(grid: Grid<out Cell>): Boolean {
        var needed = false
        var more = true
        while (more) {
            more = false
            for (i in 0..8)
                for (j in 0..8)
                    if (!grid.get(i, j).isMark() && grid.get(i, j).isSingle()) {
                        more = true
                        needed = true
                        Algorithm.smartConvertToPen(grid, i, j)
                    }
        }
        return needed
    }
}