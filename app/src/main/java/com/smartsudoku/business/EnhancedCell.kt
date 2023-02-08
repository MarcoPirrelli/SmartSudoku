package com.smartsudoku.business

open class EnhancedCell : Cell {
    var red = 255
    var green = 255
    var blue = 255

    var highlighted = false

    constructor()

    constructor(cell: Cell) : super(cell)

    constructor(enhancedCell: EnhancedCell) : this(enhancedCell as Cell) {
        red = enhancedCell.red
        green = enhancedCell.green
        blue = enhancedCell.blue
        highlighted = enhancedCell.highlighted
    }

    fun setColor(red: Int, green: Int, blue: Int) {
        this.red = red
        this.green = green
        this.blue = blue
    }

    fun getARGB(): Int {
        return (0xff shl 24) or (red and 0xff shl 16) or (green and 0xff shl 8) or (blue and 0xff)
    }

    override fun copy(): EnhancedCell = EnhancedCell(this)
}