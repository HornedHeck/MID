private val H = arrayOf(
    arrayOf(0xB1, 0x94, 0xBA, 0xC8, 0x0A, 0x08, 0xF5, 0x3B, 0x36, 0x6D, 0x00, 0x8E, 0x58, 0x4A, 0x5D, 0xE4),
    arrayOf(0x85, 0x04, 0xFA, 0x9D, 0x1B, 0xB6, 0xC7, 0xAC, 0x25, 0x2E, 0x72, 0xC2, 0x02, 0xFD, 0xCE, 0x0D),
    arrayOf(0x5B, 0xE3, 0xD6, 0x12, 0x17, 0xB9, 0x61, 0x81, 0xFE, 0x67, 0x86, 0xAD, 0x71, 0x6B, 0x89, 0x0B),
    arrayOf(0x5C, 0xB0, 0xC0, 0xFF, 0x33, 0xC3, 0x56, 0xB8, 0x35, 0xC4, 0x05, 0xAE, 0xD8, 0xE0, 0x7F, 0x99),
    arrayOf(0xE1, 0x2B, 0xDC, 0x1A, 0xE2, 0x82, 0x57, 0xEC, 0x70, 0x3F, 0xCC, 0xF0, 0x95, 0xEE, 0x8D, 0xF1),
    arrayOf(0xC1, 0xAB, 0x76, 0x38, 0x9F, 0xE6, 0x78, 0xCA, 0xF7, 0xC6, 0xF8, 0x60, 0xD5, 0xBB, 0x9C, 0x4F),
    arrayOf(0xF3, 0x3C, 0x65, 0x7B, 0x63, 0x7C, 0x30, 0x6A, 0xDD, 0x4E, 0xA7, 0x79, 0x9E, 0xB2, 0x3D, 0x31),
    arrayOf(0x3E, 0x98, 0xB5, 0x6E, 0x27, 0xD3, 0xBC, 0xCF, 0x59, 0x1E, 0x18, 0x1F, 0x4C, 0x5A, 0xB7, 0x93),
    arrayOf(0xE9, 0xDE, 0xE7, 0x2C, 0x8F, 0x0C, 0x0F, 0xA6, 0x2D, 0xDB, 0x49, 0xF4, 0x6F, 0x73, 0x96, 0x47),
    arrayOf(0x06, 0x07, 0x53, 0x16, 0xED, 0x24, 0x7A, 0x37, 0x39, 0xCB, 0xA3, 0x83, 0x03, 0xA9, 0x8B, 0xF6),
    arrayOf(0x92, 0xBD, 0x9B, 0x1C, 0xE5, 0xD1, 0x41, 0x01, 0x54, 0x45, 0xFB, 0xC9, 0x5E, 0x4D, 0x0E, 0xF2),
    arrayOf(0x68, 0x20, 0x80, 0xAA, 0x22, 0x7D, 0x64, 0x2F, 0x26, 0x87, 0xF9, 0x34, 0x90, 0x40, 0x55, 0x11),
    arrayOf(0xBE, 0x32, 0x97, 0x13, 0x43, 0xFC, 0x9A, 0x48, 0xA0, 0x2A, 0x88, 0x5F, 0x19, 0x4B, 0x09, 0xA1),
    arrayOf(0x7E, 0xCD, 0xA4, 0xD0, 0x15, 0x44, 0xAF, 0x8C, 0xA5, 0x84, 0x50, 0xBF, 0x66, 0xD2, 0xE8, 0x8A),
    arrayOf(0xA2, 0xD7, 0x46, 0x52, 0x42, 0xA8, 0xDF, 0xB3, 0x69, 0x74, 0xC5, 0x51, 0xEB, 0x23, 0x29, 0x21),
    arrayOf(0xD4, 0xEF, 0xD9, 0xB4, 0x3A, 0x62, 0x28, 0x75, 0x91, 0x14, 0x10, 0xEA, 0x77, 0x6C, 0xDA, 0x1D),
)

private val LAMBDA_THRESHOLD = 1U shl 31
private val LAMBDA_MOD = 1UL shl 32

internal fun h(it: UByte) = H[it.div(16U).toInt()][it.mod(16U).toInt()].toUByte()

internal infix fun UInt.sum32(v: UInt) = (toULong() + v).mod(LAMBDA_MOD).toUInt()
internal infix fun UInt.sub32(v: UInt) = this - v

internal class Cypher(
    rawKey: ByteArray,
) {

    private val keys = rawKey.asList().chunked(4).map {
        it.map(Byte::toUByte).toUint()
    }.let { it + it + it + it + it + it + it }

    private fun lambda(u: ULong): ULong {
        return if (u < LAMBDA_THRESHOLD) {
            2UL * u
        } else {
            2UL * u + 1UL
        }.mod(LAMBDA_MOD)
    }

    private fun lambda(u: UInt, r: Int): UInt {
        var l = u.toULong()
        repeat(r) {
            l = lambda(l)
        }
        return l.toUInt()
    }

    private fun g(u: UInt, r: Int): UInt {
        val h = u.toUBytes().map(::h).toUint()

        return lambda(h, r)
    }

    fun encryptBlock(x: List<UInt>): List<UInt> {
        require(x.size == 4)
        var (a, b, c, d) = x
        for (i in 0..7) {
            b = b xor g(a sum32 keys[7 * i], 5)
            c = c xor g(d sum32 keys[7 * i + 1], 21)
            a = a sub32 g(b sum32 keys[7 * i + 2], 13)
            val e = g(b sum32 c sum32 keys[7 * i + 3], 21) xor (i + 1).toUInt()
            b = b sum32 e
            c = c sub32 e
            d = d sum32 g(c sum32 keys[7 * i + 4], 13)
            b = b xor g(a sum32 keys[7 * i + 5], 21)
            c = c xor g(d sum32 keys[7 * i + 6], 5)
            a = b.also { b = a }
            c = d.also { d = c }
            b = c.also { c = b }
        }
        return listOf(b, d, a, c)
    }

    fun decryptBlock(x: List<UInt>): List<UInt> {
        require(x.size == 4)
        var (a, b, c, d) = x
        for (i in 0..7) {
            b = b xor g(a sum32 keys[7 * i + 6], 5)
            c = c xor g(d sum32 keys[7 * i + 5], 21)
            a = a sub32 g(b sum32 keys[7 * i + 4], 13)
            val e = g(b sum32 c sum32 keys[7 * i + 3], 21) xor (i + 1).toUInt()
            b = b sum32 e
            c = c sub32 e
            d = d sum32 g(c sum32 keys[7 * i + 2], 13)
            b = b xor g(a sum32 keys[7 * i + 1], 21)
            c = c xor g(d sum32 keys[7 * i], 5)
            a = b.also { b = a }
            c = d.also { d = c }
            a = d.also { d = a }
        }
        return listOf(c, a, d, b)
    }


}
