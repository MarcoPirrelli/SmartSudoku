package com.smartsudoku.business.algorithms

import com.smartsudoku.business.Cell
import com.smartsudoku.business.EnhancedCell
import com.smartsudoku.business.Grid
import com.smartsudoku.business.Selector

object Omission : Algorithm {
    override val id = 5

    override fun where(grid: Grid<EnhancedCell>): Boolean {
        for (i in 0..8)
            for (j in 0..6 step 3) {
                val row = Selector.row(i, 0)
                val block = Selector.block(i, j)
                if (findOmission(grid, row, block))
                    return true
                if (findOmission(grid, block, row))
                    return true
            }
        for (i in 0..6 step 3)
            for (j in 0..8) {
                val col = Selector.col(0, j)
                val block = Selector.block(i, j)
                if (findOmission(grid, col, block))
                    return true
                if (findOmission(grid, block, col))
                    return true
            }
        return false
    }

    override fun apply(grid: Grid<out Cell>): Boolean {
        var needed = false
        for (i in 0..8)
            for (j in 0..6 step 3) {
                val row = Selector.row(i, 0)
                val block = Selector.block(i, j)
                needed = needed or applyOmission(grid, row, block)
                needed = needed or applyOmission(grid, block, row)
            }
        for (i in 0..6 step 3)
            for (j in 0..8) {
                val col = Selector.col(0, j)
                val block = Selector.block(i, j)
                needed = needed or applyOmission(grid, col, block)
                needed = needed or applyOmission(grid, block, col)
            }
        return needed
    }

    private fun findOmission(
        grid: Grid<EnhancedCell>,
        firstGroup: Selector,
        secondGroup: Selector
    ): Boolean {
        val intersect = firstGroup.intersect(secondGroup)
        val firstSide = secondGroup - intersect
        val secondSide = firstGroup - intersect
        for (n in 1..9) {
            if (intersect.containsNote(grid, n) && !firstSide.containsNote(
                    grid,
                    n
                ) && secondSide.containsNote(grid, n)
            ) {
                for (cell in intersect)
                    grid.get(cell).highlighted = true
                return true
            }
        }
        return false
    }

    private fun applyOmission(grid: Grid<out Cell>, firstGroup: Selector, secondGroup: Selector): Boolean {
        var needed = false
        val intersect = firstGroup.intersect(secondGroup)
        val firstSide = secondGroup - intersect
        val secondSide = firstGroup - intersect
        for (n in 1..9) {
            if (intersect.containsNote(grid, n) && !firstSide.containsNote(grid, n)) {
                needed = needed or Algorithm.eraseNoteSel(grid, secondSide, n)
            }
        }
        return needed
    }
}