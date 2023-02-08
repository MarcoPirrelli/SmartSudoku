package com.smartsudoku.business.algorithms

import com.smartsudoku.business.*

object AlgorithmManager {
    val maxAlgs: Int
        get() = algorithms.size

    val maxLevels: Int
        get() = levels.size

    private val algorithms: HashMap<Int, Algorithm> = HashMap()
    private val levels: ArrayList<Array<Algorithm>> = ArrayList()

    init {
        algorithms[VisualElimination.id] = VisualElimination
        algorithms[LoneSingle.id] = LoneSingle

        algorithms[HiddenSingle.id] = HiddenSingle
        algorithms[NakedCouple.id] = NakedCouple

        algorithms[Omission.id] = Omission
        algorithms[NakedTriplet.id] = NakedTriplet
        algorithms[NakedQuatrain.id] = NakedQuatrain
        algorithms[HiddenCouple.id] = HiddenCouple
        algorithms[HiddenTriplet.id] = HiddenTriplet

        val level0 = arrayOf(VisualElimination, LoneSingle)
        val level1 = arrayOf(HiddenSingle, NakedCouple)
        val level2 = arrayOf(Omission, NakedTriplet, NakedQuatrain, HiddenCouple, HiddenTriplet)

        levels.add(level0)
        levels.add(level1)
        levels.add(level2)
    }


    fun where(id: Int, grid: Grid<EnhancedCell>): Boolean {
        if (id == 0) return false
        return algorithms[id]!!.where(grid)
    }

    fun apply(id: Int, grid: Grid<out Cell>): Boolean {
        if (id == 0) {
            fillAllNotes(grid)
            return true
        } else
            return algorithms[id]!!.apply(grid)
    }

    fun fillAllNotes(grid: Grid<out Cell>) {
        for (c in grid) c.fillNotes()
    }


    /**
     * Checks if a grid is complete.
     *
     * @return True if all cells have a pen mark.
     */
    fun isComplete(grid: Grid<out Cell>): Boolean {
        for (i in 0..8) {
            for (j in 0..8) {
                if (!grid.get(i, j).isMark()) return false
            }
        }
        return true
    }

    /**
     * Attempts to solve the grid based on the level of techniques available.
     *
     * @param level Level of techniques available for solving.
     * @return True if the Sudoku can be solved with the given techniques.
     */
    fun solve(level: Int, grid: Grid<out Cell>): Boolean {
        fillAllNotes(grid)
        solveRec(level, grid)
        return isComplete(grid)
    }

    fun solveRec(level: Int, grid: Grid<out Cell>): Boolean {
        if (level < 0) {
            return false
        }
        var useful = false
        var changed: Boolean
        do {
            do {
                changed = solveRec(level - 1, grid)
                useful = useful || changed
            } while (changed)

            for (algorithm in levels[level]) {
                changed = changed or algorithm.apply(grid)
                useful = useful || changed
            }
        } while (changed)
        return useful
    }
}