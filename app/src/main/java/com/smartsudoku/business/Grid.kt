package com.smartsudoku.business

class Grid<E : DeepCopy> : ArrayList<E>, DeepCopy {
    constructor(specimen: E) {
        for(i in 0..80)
            add(specimen.copy() as E)
    }

    constructor(grid : Grid<out E>){
        for(i in 0..80)
            add(grid[i].copy() as E)
    }

    fun set(row: Int, col: Int, element: E) {
        set(row * 9 + col, element)
    }

    fun get(row: Int, col: Int): E {
        return get(row * 9 + col)
    }

    override fun copy(): Grid<E> = Grid<E>(this)

}