package com.smartsudoku.business

import com.smartsudoku.business.algorithms.Algorithm

class Sudoku : DeepCopy {
    var cellGrid: Grid<EnhancedCell> = Grid(EnhancedCell())

    /**
     * Constructor with an empty grid.
     */
    constructor() {
        for (i in 0..8)
            for (j in 0..8)
                cellGrid.set(i, j, EnhancedCell())
    }

    /**
     * Sudoku with a prebuilt grid.
     *
     * @param grid The grid to use.
     */
    constructor(grid: Grid<Cell>) {
        for (i in 0..8)
            for (j in 0..8)
                cellGrid.set(i, j, EnhancedCell(grid.get(i, j)))

    }

    /**
     * Returns the cell at the given coordinates.
     *
     * @param row The row of the cell.
     * @param col The column of the cell.
     * @return The cell at the given coordinates.
     */
    fun getCell(row: Int, col: Int): EnhancedCell {
        return cellGrid.get(row, col)
    }

    fun toggleInit(row: Int, col: Int, n: Int) {
        getCell(row, col).toggleInit(n)
    }

    fun toggleVal(row: Int, col: Int, n: Int) {
        Algorithm.smartToggleVal(cellGrid, row, col, n)
    }

    fun toggleNote(row: Int, col: Int, n: Int) {
        getCell(row, col).toggleNote(n)
    }

    fun getColor(row: Int, col: Int): Int {
        return getCell(row, col).getARGB()
    }

    fun setColor(row: Int, col: Int, red: Int, green: Int, blue: Int) {
        getCell(row, col).setColor(red, green, blue)
    }

    fun clearHighlights() {
        for (c in cellGrid)
            c.highlighted = false
    }

    override fun copy(): Sudoku {
        val copy = Sudoku()
        copy.cellGrid = cellGrid.copy()
        return copy
    }
}