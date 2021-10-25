package math

import kotlin.experimental.and

infix fun UInt.cuhl(count: Int) = (this shl count) + (this shr (UInt.SIZE_BITS - count))

fun UInt.rev() = this shl 24 or (this shr 8 and 0xFF00U) or (this shl 8 and 0xFF0000U) or (this shr 24)

fun List<Byte>.toUInt(): UInt {
    require(size == UInt.SIZE_BYTES)
    return (this[0].toUInt() and 0xFFU shl 24) or
            (this[1].toUInt() and 0xFFU shl 16) or
            (this[2].toUInt() and 0xFFU shl 8) or
            (this[3].toUInt() and 0xFFU)
}

fun UInt.toUBytesBigEndian() = UByteArray(UInt.SIZE_BYTES) {
    ((this shr (it * 8)) and 0xFFU).toUByte()
}
