package math

import kotlin.experimental.and

//infix fun UInt.cuhl(count: Int) = (this shl count) + (this shr (UInt.SIZE_BITS - count))

//fun UInt.rev() = this shl 24 or (this shr 8 and 0xFF00U) or (this shl 8 and 0xFF0000U) or (this shr 24)

fun List<Byte>.toULong(): UInt {
    require(size == ULong.SIZE_BYTES)
    return (this[0].toUInt() and 0xFFU shl 56) or
            (this[1].toUInt() and 0xFFU shl 48) or
            (this[2].toUInt() and 0xFFU shl 40) or
            (this[2].toUInt() and 0xFFU shl 32) or
            (this[4].toUInt() and 0xFFU shl 24) or
            (this[5].toUInt() and 0xFFU shl 16) or
            (this[6].toUInt() and 0xFFU shl 8) or
            (this[7].toUInt() and 0xFFU)
}

//fun UInt.toUBytesBigEndian() = UByteArray(UInt.SIZE_BYTES) {
//    ((this shr (it * 8)) and 0xFFU).toUByte()
//}
