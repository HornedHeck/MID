class DESOpt(
    keyRaw : ByteArray
) {

    private val key = BitSet(
        keyRaw.chunkToBits()
    )

    private val k = List(DES_CYCLE_COUNT) {
        key.translation(CD[it]).translation(K_E)
    }

    fun encrypt(data : ByteArray) : ByteArray {
        return data.toBits().flatMap {
            encryptBlock(BitSet(it)).toBytes()
        }.toByteArray()
    }

    private fun encryptBlock(data : BitSet) : BitSet {

        var (l, r) = data.translation(X).let {
            it[0, DES_CYCLE_SIZE_BITS] to it[DES_CYCLE_SIZE_BITS, DES_BLOCK_SIZE_BITS]
        }
        repeat(16) {
            l = r.apply { r = l xor feistel(r, k[it]) }
        }
        return (l + r).translation(Y)
    }

    fun decrypt(data : ByteArray) : ByteArray {
        return data.toBits().flatMap {
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
                val a = it[0].bitValue().shl(1) + it[5].bitValue()
                val b = it[1].bitValue().shl(3) +
                        it[2].bitValue().shl(2) +
                        it[3].bitValue().shl(1) +
                        it[4].bitValue()
                S[i][a][b].formatS()
            }.toBooleanArray().let { BitSet(it) }
            .translation(P)

}

private fun Int.formatS() = List(DES_BS_SIZE) {
    this and (1).shl(it) != 0
}

private fun BitSet.toBytes() = chunked(8) {
    it.mapIndexed { i, v -> v.bitValue().shl(7 - i) }.sum().toByte()
}