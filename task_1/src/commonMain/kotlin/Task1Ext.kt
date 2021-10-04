internal fun ByteArray.toBits(groupSize : Int) = asSequence()
    .chunked(groupSize) {
        it.flatMap(Byte::toBits).toBooleanArray()
    }
    .toList()

internal fun ByteArray.chunkToBits() : BooleanArray {
    require(size == Long.SIZE_BYTES)
    return flatMap { it.toBits() }.toBooleanArray()
}

internal fun Byte.toBits() = List(Byte.SIZE_BITS) {
    this.toInt() and (1).shl(Byte.SIZE_BITS - 1 - it) != 0
}

internal fun Boolean.bitValue() = if (this) {
    1
} else {
    0
}