package ds

import Gost28147
import math.toUShort
import math.trailingZerosPad
import math.xor
import java.math.BigInteger
import kotlin.experimental.xor

private val C2 = ByteArray(32)
private val C3 = BigInteger("ff00ffff000000ffff0000ff00ffff0000ff00ff00ff00ffff00ff00ff00ff00", 16).toByteArray()
private val C4 = ByteArray(32)
private fun phi(i: Int, k: Int) = 8 * i + k
private val phiMap = List(4) { i ->
    List(8) { k ->
        phi(i, k)
    }
}.flatten().reversed()

class Gost3411(
    private val h0: ByteArray = BigInteger("FAFF37A615A816691CFF3EF8B68CA247E09525F39F8119832EB81975D366C4B1", 16)
        .toByteArray()
        .takeLast(32)
        .toByteArray()
) {

    //    private var h = ByteArray(32)
    private var h = h0


    private fun a(msg: ByteArray): ByteArray {
        val (y1, y2, y3, y4) = msg.asList().chunked(8)
        return (y1 xor y2).toByteArray() + y4 + y3 + y2
    }

    private fun p(msg: ByteArray): ByteArray {
        return phiMap.map { msg[it] }.toByteArray()
    }

    private fun keygen(msg: ByteArray): List<ByteArray> {
        val keys = mutableListOf<ByteArray>()
        var u = h
        var v = msg
        var w = u xor v
        keys.add(p(w))

        u = a(u) xor C2
        v = a(a(v))
        w = u xor v
        keys.add(p(w))


        u = a(u) xor C3
        v = a(a(v))
        w = u xor v
        keys.add(p(w))


        u = a(u) xor C4
        v = a(a(v))
        w = u xor v
        keys.add(p(w))
        return keys
    }

    private fun encrypt(msg: ByteArray, keys: List<ByteArray>) =
        msg.asList().chunked(8).mapIndexed { i, h ->
            val e = Gost28147(keys[i])
            e.encrypt(h.toByteArray()).asList()
        }.reversed().flatten()


    private fun gamma(msg: List<Byte>): List<Byte> {
        val y = msg.chunked(2)
        return (y[0] xor y[1] xor y[2] xor y[3] xor y[12] xor y[15]) + y.drop(1).flatten()
    }

    private fun digestInternal(msg: ByteArray) {
        val keys = keygen(msg)
        var s = encrypt(msg, keys)
        repeat(12) {
            s = gamma(s)
        }
        s = gamma(s xor msg.asList())
        s = s xor h.asList()
        repeat(61) {
            s = gamma(s)
        }
        h = s.toByteArray()
    }

    fun digest(msg: ByteArray): String {
        val leftover = msg.size % 32
        val padded = if (leftover == 0) {
            msg
        } else {
            msg.trailingZerosPad(msg.size + 32 - leftover)
        }
        padded.asList().chunked(32).forEach {
            digestInternal(it.toByteArray())
        }
        val digest = h.toHexString()
        h = h0
        return digest
    }

}

fun ByteArray.toHexString(): String {
    return joinToString("") { it.toUByte().toString(16).uppercase() }
}