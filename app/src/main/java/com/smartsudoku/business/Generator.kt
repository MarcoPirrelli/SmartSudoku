package com.smartsudoku.business

import com.smartsudoku.business.algorithms.AlgorithmManager
import java.util.*

class Generator(private val level: Int) {
    private val random = Random()
    var running = true

    fun run() {
        val rand = Random()
        var gen: Grid<Cell>
        var prev: Grid<Cell>
        var solution: Grid<Cell>
        do {
            solution = getFullGrid()
            shuffleNumbers(solution)
            shuffle(solution)
            shuffle3X9(solution)

            // remove numbers from the grid. Redo in case of a failure
            do {
                gen = solution.copy()
                for (i in 0..50) {
                    val r = rand.nextInt(9)
                    val c = rand.nextInt(9)
                    gen.get(r, c).reset()
                }
            } while (!AlgorithmManager.solve(level, gen.copy()))
            do {
                prev = gen.copy()
                var r: Int
                var c: Int
                do {
                    r = rand.nextInt(9)
                    c = rand.nextInt(9)
                } while (!gen.get(r, c).isMark())
                gen.get(r, c).reset()
            } while (AlgorithmManager.solve(level, gen.copy()))
            // when you come out of this loop, prev can be solved, but gen can't
        } while (AlgorithmManager.solve(level - 1, prev.copy()) && running)
        // accept prev only if it is not solvable at level - 1, else try again
        synchronized(Manager) {
            if (running && Manager.generatedGrid == null) {
                Manager.generatedGrid = prev
                Manager.generatedSolution = solution
                Manager.generatedLevel = level + 1
            }
        }
    }

    private fun getFullGrid(): Grid<Cell> {
        val grid = Grid(Cell())
        for (r in 0..8) {
            for (c in 0..8) {
                grid.get(r, c).setInit((3 * (c / 3 + r % 3) + (c + r / 3) % 3) % 9 + 1)
            }
        }
        return grid
    }

    private fun shuffleNumbers(grid: Grid<Cell>) {
        for (i in 1..9) swapNumbers(grid, i, random.nextInt(9) + 1)
    }

    private fun swapNumbers(grid: Grid<Cell>, n1: Int, n2: Int) {
        //swap numbers in the grid
        for (i in 0..8) {
            for (j in 0..8) {
                if (grid.get(i, j).getVal() == n1) {
                    grid.get(i, j).setInit(n2)
                } else if (grid.get(i, j).getVal() == n2) {
                    grid.get(i, j).setInit(n1)
                }
            }
        }
    }

    private fun shuffle(grid: Grid<Cell>) {
        var temp: Cell
        //shuffle rows in blocks
        for (b in 0..2) {
            val s1 = random.nextInt(3)
            val s2 = random.nextInt(2)
            for (c in 0..8) {
                temp = grid.get(b * 3 + 2, c)
                grid.set(b * 3 + 2, c, grid.get(b * 3 + s1, c))
                grid.set(b * 3 + s1, c, temp)
                temp = grid.get(b * 3 + 1, c)
                grid.set(b * 3 + 1, c, grid.get(b * 3 + s2, c))
                grid.set(b * 3 + s2, c, temp)
            }
        }
        // shuffle columns in blocks
        for (b in 0..2) {
            val s1 = random.nextInt(3)
            val s2 = random.nextInt(2)
            for (r in 0..8) {
                temp = grid.get(r, b * 3 + 2)
                grid.set(r, b * 3 + 2, grid.get(r, b * 3 + s1))
                grid.set(r, b * 3 + s1, temp)
                temp = grid.get(r, b * 3 + 1)
                grid.set(r, b * 3 + 1, grid.get(r, b * 3 + s2))
                grid.set(r, b * 3 + s2, temp)
            }
        }
    }

    private fun shuffle3X9(grid: Grid<Cell>) {
        //shuffle 3x9 blocks
        var s1 = random.nextInt(3)
        var s2 = random.nextInt(2)
        for (c in 0..8) {
            for (r in 0..2) {
                var temp: Cell = grid.get(6 + r, c)
                grid.set(6 + r, c, grid.get(3 * s1 + r, c))
                grid.set(3 * s1 + r, c, temp)
                temp = grid.get(3 + r, c)
                grid.set(3 + r, c, grid.get(3 * s2 + r, c))
                grid.set(3 * s2 + r, c, temp)
            }
        }
        s1 = random.nextInt(3)
        s2 = random.nextInt(2)
        for (r in 0..8) {
            for (c in 0..2) {
                var temp: Cell = grid.get(r, 6 + c)
                grid.set(r, 6 + c, grid.get(r, 3 * s1 + c))
                grid.set(r, 3 * s1 + c, temp)
                temp = grid.get(r, 3 + c)
                grid.set(r, 3 + c, grid.get(r, 3 * s2 + c))
                grid.set(r, 3 * s2 + c, temp)
            }
        }
    }

}