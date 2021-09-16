const val DES_BLOCK_SIZE_BYTES = 8
const val DES_BLOCK_SIZE_BITS = 64
const val DES_CYCLE_SIZE_BITS = 32
const val DES_B_SIZE = 6
const val DES_BS_SIZE = 4
const val DES_CYCLE_COUNT = 16

class DES(
    keyRaw : ByteArray
) {

    private val key = BitSet(
        keyRaw
//            .encodeToByteArray()
            .chunkToBits()
    )

    private val k = List(DES_CYCLE_COUNT) {
        key.translation(CD[it]).translation(K_E)
    }

    fun encrypt(data : ByteArray) : ByteArray {
        return data.toBits(DES_BLOCK_SIZE_BYTES).flatMap {
            encryptBlock(BitSet(it)).toBytes()
        }.toByteArray()
    }

    private fun encryptBlock(data : BitSet) : BitSet {
        val data = data.translation(X)
        var l = data[0, DES_CYCLE_SIZE_BITS]
        var r = data[DES_CYCLE_SIZE_BITS, DES_BLOCK_SIZE_BITS]
        repeat(16) {
            l = r.apply { r = l xor feistel(r, k[it]) }
        }
        return (l + r).translation(Y)
    }

    fun decrypt(data : ByteArray) : ByteArray {
        return data.toBits(DES_BLOCK_SIZE_BYTES).flatMap {
            decryptBlock(BitSet(it)).toBytes()
        }.toByteArray()
    }

    private fun decryptBlock(data : BitSet) : BitSet {
        val data = data.translation(X)
        var l = data[0, DES_CYCLE_SIZE_BITS]
        var r = data[DES_CYCLE_SIZE_BITS, DES_BLOCK_SIZE_BITS]
        repeat(DES_CYCLE_COUNT) {
            r = l.apply { l = r xor feistel(l, k[DES_CYCLE_COUNT - it - 1]) }
        }
        return (l + r).translation(Y)
    }

    fun feistel(r : BitSet, k : BitSet) =
        r.translation(E)
            .xor(k)
            .chunked(DES_B_SIZE)
            .flatMapIndexed { i, it ->
                val a = it[0].bitValue() * 2 + it[5].bitValue()
                val b = it[1].bitValue() * 8 +
                        it[2].bitValue() * 4 +
                        it[3].bitValue() * 2 +
                        it[4].bitValue()
                S[i][a][b].formatS()
            }.toBooleanArray().let { BitSet(it) }
            .translation(P)

}

private fun Int.formatS() = List(DES_BS_SIZE) {
    this and (1).shl(it) != 0
}

fun BitSet.toBytes() = chunked(8) {
    it.mapIndexed { i, v -> v.bitValue().shl(7 - i) }.sum().toByte()
}