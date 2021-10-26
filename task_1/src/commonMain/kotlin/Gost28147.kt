private const val KEYS_SIZE_BYTE = 32
private const val KEY_SIZE_BYTE = 4
private val encryptKeysOrder = arrayOf(
    0, 1, 2, 3, 4, 5, 6, 7,
    0, 1, 2, 3, 4, 5, 6, 7,
    0, 1, 2, 3, 4, 5, 6, 7,
    7, 6, 5, 4, 3, 2, 1, 0
)

private val decryptKeysOrder = arrayOf(
    0, 1, 2, 3, 4, 5, 6, 7,
    7, 6, 5, 4, 3, 2, 1, 0,
    7, 6, 5, 4, 3, 2, 1, 0,
    7, 6, 5, 4, 3, 2, 1, 0,
)

val S_GOST = arrayOf(
    arrayOf(4, 10, 9, 2, 13, 8, 0, 14, 6, 11, 1, 12, 7, 15, 5, 3),
    arrayOf(14, 11, 4, 12, 6, 13, 15, 10, 2, 3, 8, 1, 0, 7, 5, 9),
    arrayOf(5, 8, 1, 13, 10, 3, 4, 2, 14, 15, 12, 7, 6, 0, 9, 11),
    arrayOf(7, 13, 10, 1, 0, 8, 9, 15, 14, 4, 6, 12, 11, 2, 5, 3),
    arrayOf(6, 12, 7, 1, 5, 15, 13, 8, 4, 10, 9, 14, 0, 3, 11, 2),
    arrayOf(4, 11, 10, 0, 7, 2, 1, 13, 3, 6, 8, 5, 9, 12, 15, 14),
    arrayOf(13, 11, 4, 1, 3, 15, 5, 9, 0, 10, 14, 7, 6, 8, 2, 12),
    arrayOf(1, 15, 13, 0, 5, 7, 10, 4, 9, 2, 3, 14, 6, 11, 8, 12)
)


fun BitSet.mod2p32(key: BitSet) = BitSet(
    (toUInt().toULong() + key.toUInt())
        .mod(1UL shl 32)
        .toInt()
)

fun BitSet.toUInt() = this.toBytes().let {
    ((it[0].toUByte().toUInt() * 256U + it[1].toUByte().toUInt()) * 256U + it[2].toUByte()
        .toUInt()) * 256U + it[3].toUByte().toUInt()
}

class Gost28147(keyRaw: ByteArray) {

    init {
        require(keyRaw.size == KEYS_SIZE_BYTE)
    }

    private val keys = keyRaw.toBits(KEY_SIZE_BYTE).map {
        BitSet(it)
    }

    fun encrypt(data: ByteArray): ByteArray {
        return data.toBits(DES_BLOCK_SIZE_BYTES).flatMap {
            encryptBlock(BitSet(it)).toBytes()
        }.toByteArray()
    }

    private fun encryptBlock(data: BitSet): BitSet {
        var r = data[0, DES_CYCLE_SIZE_BITS]
        var l = data[DES_CYCLE_SIZE_BITS, DES_BLOCK_SIZE_BITS]
        for (i in 0 until 32) {
            r = l.apply {
                l = r xor applyKey(
                    keys[encryptKeysOrder[i]],
                    l
                )
            }
        }
        return (l + r)
    }

    fun decrypt(data: ByteArray): ByteArray {
        return data.toBits(DES_BLOCK_SIZE_BYTES).flatMap {
            decryptBlock(BitSet(it)).toBytes()
        }.toByteArray()
    }

    private fun decryptBlock(data: BitSet): BitSet {
        var r = data[0, DES_CYCLE_SIZE_BITS]
        var l = data[DES_CYCLE_SIZE_BITS, DES_BLOCK_SIZE_BITS]
        repeat(32) {
            r = l.apply {
                l = r xor applyKey(
                    keys[decryptKeysOrder[it]],
                    l
                )
            }
        }
        return (l + r)
    }


    private fun applyKey(key: BitSet, data: BitSet): BitSet {
        return BitSet(
            data
                .mod2p32(key)
                .chunked(4)
                .flatMapIndexed { i, bits ->
                    val index = bits[0].bitValue() * 8 +
                            bits[1].bitValue() * 4 +
                            bits[2].bitValue() * 2 +
                            bits[3].bitValue()
                    S_GOST[i][index].formatS()
                }.let {
                    it.takeLast(11) + it.dropLast(11)
                }.toBooleanArray()
        )
    }


    private fun Int.formatS() = List(4) {
        this and (1).shl(it) != 0
    }

}