package com.smartsudoku.business

import java.util.TreeSet

class Selector : TreeSet<Int>(), DeepCopy {
    companion object {
        fun row(r: Int, c: Int): Selector {
            val s = Selector()
            s.addRow(r)
            return s
        }

        fun col(r: Int, c: Int): Selector {
            val s = Selector()
            s.addCol(c)
            return s
        }

        fun block(r: Int, c: Int): Selector {
            val s = Selector()
            s.addBlock(r, c)
            return s
        }

        fun groups(r: Int, c: Int): Selector {
            val s = Selector()
            s.addGroups(r, c)
            return s
        }

        fun rowCored(r: Int, c: Int): Selector {
            val s = Selector()
            s.addRow(r)
            s.remove(r * 9 + c)
            return s
        }

        fun colCored(r: Int, c: Int): Selector {
            val s =
                Selector()
            s.addCol(c)
            s.remove(r * 9 + c)
            return s
        }

        fun blockCored(r: Int, c: Int): Selector {
            val s = Selector()
            s.addBlock(r, c)
            s.remove(r * 9 + c)
            return s
        }

        fun groupsCored(r: Int, c: Int): Selector {
            val s = Selector()
            s.addGroups(r, c)
            s.remove(r * 9 + c)
            return s
        }
    }

    fun add(r: Int, c: Int) {
        add(r * 9 + c)
    }

    fun remove(r: Int, c: Int) {
        remove(r * 9 + c)
    }

    fun addRow(r: Int) {
        for (j in 0..8) {
            add(r * 9 + j)
        }
    }

    fun addCol(c: Int) {
        for (i in 0..8) {
            add(i * 9 + c)
        }
    }

    fun addBlock(r: Int, c: Int) {
        for (i in 0..2) {
            for (j in 0..2) {
                add((i + 3 * (r / 3)) * 9 + j + 3 * (c / 3))
            }
        }
    }

    fun addGroups(r: Int, c: Int) {
        addRow(r)
        addCol(c)
        addBlock(r, c)
    }

    operator fun plus(selector2: Selector) : Selector {
        val ret = copy()
        ret.addAll(selector2)
        return ret
    }

    operator fun minus(selector2: Selector) : Selector {
        val ret = copy()
        ret.removeAll(selector2)
        return ret
    }

    fun intersect(selector2: Selector): Selector {
        val ret = Selector()
        for (i in this) {
            if (i in selector2)
                ret.add(i)
        }
        return ret
    }

    fun containsNote(grid: Grid<out Cell>, n: Int): Boolean {
        for (p in this) {
            if (grid.get(p).contains(n)) {
                return true
            }
        }
        return false
    }

    override fun copy(): Selector {
        val c = Selector()
        c.addAll(this)
        return c
    }
}