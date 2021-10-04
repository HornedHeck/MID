private const val BYTE_MULTIPLIER = 256
private val UBYTE_MULTIPLIER = 256U

private fun UInt.power(p: Int) = if (p == 0) {
    1U
} else {
    var r = 1U
    repeat(p) {
        r *= this
    }
    r
}

fun ByteArray.pad(size: Int): ByteArray {
    if (this.size % size == 0) return this
    return this + ByteArray(size - this.size % size)
}

fun Iterable<Byte>.toInts() = this
    .asSequence()
    .chunked(Int.SIZE_BYTES)
    .map {
        it.mapIndexed { i, v -> v * BYTE_MULTIPLIER.times(Int.SIZE_BYTES - 1 - i) }.sum()
    }
    .toList()

fun List<UByte>.toUint() =
    this[0] * UBYTE_MULTIPLIER.power(3) +
            this[1] * UBYTE_MULTIPLIER.power(2) +
            this[2] * UBYTE_MULTIPLIER.power(1) +
            this[3]

fun Iterable<Byte>.toLongs() = this
    .asSequence()
    .chunked(Long.SIZE_BYTES)
    .map {
        it.mapIndexed { i, v -> v.toLong() * BYTE_MULTIPLIER.times(Long.SIZE_BYTES - 1 - i) }.sum()
    }
    .toList()

fun UInt.toUBytes() = List(UInt.SIZE_BYTES) {
    this.div(UBYTE_MULTIPLIER.power(it))
        .mod(UBYTE_MULTIPLIER)
        .toUByte()
}.reversed()