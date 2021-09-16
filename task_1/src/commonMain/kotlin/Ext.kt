fun ByteArray.toBits(groupSize : Int) = asSequence()
    .chunked(groupSize) {
        it.flatMap(Byte::toBits).toBooleanArray()
    }
    .toList()

fun ByteArray.chunkToBits() : BooleanArray {
    require(size == Long.SIZE_BYTES)
    return flatMap { it.toBits() }.toBooleanArray()
}

fun Byte.toBits() = List(Byte.SIZE_BITS) {
    this.toInt() and (1).shl(Byte.SIZE_BITS - 1 - it) != 0
}

fun Boolean.bitValue() = if (this) {
    1
} else {
    0
}