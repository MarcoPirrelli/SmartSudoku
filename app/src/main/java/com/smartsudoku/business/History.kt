package com.smartsudoku.business

class History<E : DeepCopy> {
    private val MAX_SIZE = 50
    val deque = ArrayDeque<E>(MAX_SIZE)
    var current = 0

    fun isEmpty():Boolean{
        return deque.isEmpty()
    }

    fun isCurrent(): Boolean {
        return current == 0
    }

    operator fun hasNext(): Boolean {
        return current + 1 < deque.size
    }

    fun hasPrevious(): Boolean {
        return current > 0
    }

    fun addFirst(e: E) {
        for (i in 0 until current) deque.removeFirst()
        current = 0
        deque.addFirst(e)
        if (deque.size > MAX_SIZE) deque.removeLast()
    }

    fun getCurrent(): E {
        current++
        return deque[current].copy() as E
    }

    fun getPrevious(): E? {
        if (hasPrevious()) {
            current--
            return deque[current].copy() as E
        }
        return null
    }

    fun clear(){
        deque.clear()
        current = 0
    }
}