package com.smartsudoku.business

import com.smartsudoku.business.algorithms.AlgorithmManager
import kotlinx.coroutines.*

object Manager {
    @kotlin.jvm.JvmField
    var currentSudoku: Sudoku? = null
    var solvedGrid: Grid<Cell>? = null
    private var history: History<Sudoku> = History()

    var time: Long = 0
    var level = 0

    var generating = false
    var generatedGrid : Grid<Cell>? = null
    var generatedSolution : Grid<Cell>? = null
    var generatedLevel : Int? = null

    var generatorWaiter: GeneratorWaiter? = null

    @kotlin.jvm.JvmField
    var inputNotes = false

    @kotlin.jvm.JvmField
    var inputColors = false

    @kotlin.jvm.JvmField
    var customInit = false

    @kotlin.jvm.JvmField
    var autoUpdateNotes = true

    @kotlin.jvm.JvmField
    var checkForMistakes = true

    init {
        currentSudoku = Sudoku()
        save()
    }

    fun hasSolution(): Boolean {
        return solvedGrid != null
    }

    private fun save() {
        history.addFirst(currentSudoku!!.copy())
    }

    fun undo() {
        if (history.isEmpty() || !history.hasNext()) return
        currentSudoku = history.getCurrent()
    }

    fun redo() {
        if (history.hasPrevious()) currentSudoku = history.getPrevious()
    }

    fun wouldBeWrong(row: Int, col: Int, n: Int): Boolean {
        if (solvedGrid != null) {
            return n != solvedGrid!!.get(row, col).getVal()
        }
        return false
    }

    fun isWrong(row: Int, col: Int): Boolean {
        if (solvedGrid != null && currentSudoku!!.getCell(row, col).getVal() != 0) {
            return currentSudoku!!.getCell(row, col).getVal() != solvedGrid!!.get(row, col).getVal()
        }
        return false
    }

    fun hasMistake(): Boolean {
        for (i in 0..8)
            for (j in 0..8)
                if (isWrong(i, j)) return true

        return false
    }

    fun isFinished() = AlgorithmManager.isComplete(currentSudoku!!.cellGrid) && !hasMistake()

    fun write(row: Int, col: Int, n: Int) {
        if (customInit) {
            time = 0
            currentSudoku!!.toggleInit(row, col, n)
            save()
        } else {
            if (inputNotes) {
                currentSudoku!!.toggleNote(row, col, n)
                save()
            } else mark(row, col, n)
        }
    }

    private fun mark(row: Int, col: Int, n: Int) {
        if (checkForMistakes) {
            if (isWrong(row, col) || !hasMistake()) {
                currentSudoku!!.toggleVal(row, col, n)
                save()
            }
        } else {
            currentSudoku!!.toggleVal(row, col, n)
            save()
        }
    }

    fun erase(row: Int, col: Int) {
        if (customInit) {
            currentSudoku!!.getCell(row, col).reset()
            save()
        } else if (!currentSudoku!!.getCell(row, col).isFixed()) {
            currentSudoku!!.getCell(row, col).erase()
            save()
        }
    }

    fun setColor(row: Int, col: Int, red: Int, green: Int, blue: Int) {
        currentSudoku!!.setColor(row, col, red, green, blue)
        save()
    }

    fun clearHighlights() {
        currentSudoku!!.clearHighlights()
    }

    fun where(id: Int): Boolean {
        return AlgorithmManager.where(id, currentSudoku!!.cellGrid)
    }

    fun apply(id: Int) {
        if (AlgorithmManager.apply(id, currentSudoku!!.cellGrid))
            save()
    }

    fun clearSudoku() {
        history.clear()
        currentSudoku = Sudoku()
        solvedGrid = null
        time = 0
        level = 0
        save()
    }

    fun findSolvedGrid() {
        if (solvedGrid == null) {
            val solution: Grid<Cell> = Grid(currentSudoku!!.cellGrid)
            for (cell in solution)
                cell.erase()
            val ret = AlgorithmManager.solve(AlgorithmManager.maxLevels - 1, solution)
            if (ret)
                solvedGrid = solution
        }
    }

    fun generateSudoku(level: Int) {
        if(level<0)
            return

        generating = true
        val generator = Generator(level)
        val cores = Runtime.getRuntime().availableProcessors()
        val jobArray = ArrayList<Job>()
        runBlocking {
            for (i in 0 until cores)
                jobArray.add(GlobalScope.launch {
                    generator.run()
                })

            launch {
                waitForCondition(10000, 100)
            }
        }
        for (i in jobArray)
            i.cancel()
        generator.running = false
        if (generatedGrid == null) {
            generateSudoku(level-1)
        }else{
            generating = false
            if(generatorWaiter != null)
                generatorWaiter!!.generationCompleted()
        }
    }

    private tailrec suspend fun waitForCondition(maxDelay: Long, checkPeriod: Long): Boolean {
        if (maxDelay < 0) return false
        synchronized(this) {
            if (generatedGrid != null) return true
        }
        delay(checkPeriod)
        return waitForCondition(maxDelay - checkPeriod, checkPeriod)
    }

    fun applyGenerated() {
        if(generatedGrid == null || generatedSolution == null)
            return

        history.clear()
        currentSudoku = Sudoku(generatedGrid!!)
        solvedGrid = generatedSolution
        time = System.currentTimeMillis()
        level = generatedLevel!!

        save()

        generatedGrid = null
        generatedSolution = null
        generatedLevel = null
    }
}