package math

fun List<Byte>.toUShort(): UShort {
    require(size == UInt.SIZE_BYTES)
    return (this[0].toUInt() and 0xFFU shl 8).toUShort() or
            (this[1].toUShort() and 0xFFU)
}