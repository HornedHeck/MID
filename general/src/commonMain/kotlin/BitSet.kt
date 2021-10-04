private fun Long.toBits(): BooleanArray {
    return BooleanArray(Long.SIZE_BITS) {
        this and (1L).shl(it) != 0L
    }.reversed().toBooleanArray()
}

private fun Int.toBits(): BooleanArray {
    return BooleanArray(Int.SIZE_BITS) {
        this and (1).shl(Int.SIZE_BITS - 1 - it) != 0
    }
}

class BitSet(private val bits: BooleanArray) : List<Boolean> {

    constructor(value: Long) : this(value.toBits())
    constructor(value: Int) : this(value.toBits())

    override fun toString() = "${bits.size} : " + bits.joinToString("") {
        if (it) {
            "1"
        } else {
            "0"
        }
    }

    fun translation(t: Array<Int>) = BitSet(t.map { bits[it] }.toBooleanArray())

    fun reversed() = BitSet(bits.reversedArray())

    infix fun xor(b: BitSet): BitSet {
        require(b.size == size)
        return BitSet(BooleanArray(size) {
            bits[it] != b.bits[it]
        })
    }

    operator fun get(start: Int, end: Int) = BitSet(
        subList(start, end).toBooleanArray()
    )

    operator fun plus(b: BitSet) = BitSet(bits + b.bits)

//    List methods

    override val size = bits.size

    override fun contains(element: Boolean) = bits.contains(element)

    override fun containsAll(elements: Collection<Boolean>) = elements.all { bits.contains(it) }

    override fun get(index: Int) = bits[index]

    override fun indexOf(element: Boolean) = bits.indexOf(element)

    override fun isEmpty() = size == 0

    override fun iterator() = bits.iterator()

    override fun lastIndexOf(element: Boolean) = bits.lastIndexOf(element)

    override fun listIterator() = bits.asList().listIterator()

    override fun listIterator(index: Int) = bits.asList().listIterator(index)

    override fun subList(fromIndex: Int, toIndex: Int) = bits.asList().subList(fromIndex, toIndex)
}