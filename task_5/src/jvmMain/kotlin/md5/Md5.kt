package md5

import math.*
import java.security.MessageDigest
import kotlin.math.abs
import kotlin.math.sin

class Md5 {

    private var regs = uintArrayOf(A_0, B_0, C_0, D_0)
    private var pRegs = uintArrayOf(A_0, B_0, C_0, D_0)
    private var a: UInt
        get() = regs[0]
        set(value) {
            regs[0] = value
        }
    private var b: UInt
        get() = regs[1]
        set(value) {
            regs[1] = value
        }
    private var c: UInt
        get() = regs[2]
        set(value) {
            regs[2] = value
        }
    private var d: UInt
        get() = regs[3]
        set(value) {
            regs[3] = value
        }

    private fun extendMessageToSize(msg: ByteArray): ByteArray {
        val size = msg.size * Byte.SIZE_BITS % MESSAGE_SIZE_MOD
        return when {
            size == MESSAGE_REQUIRED_SIZE -> msg
            size < MESSAGE_REQUIRED_SIZE -> msg + byteArrayOf((0x80).toByte()) + ByteArray((MESSAGE_REQUIRED_SIZE - size - 1) / Byte.SIZE_BITS)
            else -> msg + byteArrayOf((0x80).toByte()) + ByteArray(MESSAGE_SIZE_MOD - size - 1 + MESSAGE_REQUIRED_SIZE)
        }
    }

    private fun extendMessageWithLength(msg: ByteArray, originalSize: Int): ByteArray {
        return msg + (originalSize * Byte.SIZE_BITS)
            .toBigInteger()
            .toByteArray()
            .leadingZerosPad(Long.SIZE_BYTES)
            .reversed()
    }

    private fun initBuffer() {
        a = A_0
        b = B_0
        c = C_0
        d = D_0
    }

    /** (x * y) + (!x * z) */
    private fun funF(x: UInt, y: UInt, z: UInt): UInt {
        return (x and y) or (x.inv() and z)
    }

    /** (x * z) + (!z * y) */
    private fun funG(x: UInt, y: UInt, z: UInt): UInt {
        return (x and z) or (z.inv() and y)
    }

    /** x^y^z */
    private fun funH(x: UInt, y: UInt, z: UInt): UInt {
        return x xor y xor z
    }

    /** y^(!z+x) */
    private fun funI(x: UInt, y: UInt, z: UInt): UInt {
        return y xor (z.inv() or x)
    }

    private fun runBlock(block: ByteArray) {
        pRegs = regs.copyOf()
        val ints = block.asList().chunked(UInt.SIZE_BYTES) { it.toUInt().rev() }
        // Псевдокод: https://ru.wikipedia.org/wiki/MD5
        var f: UInt
        var g: Int
        repeat(64) { i ->
            when (i) {
                in 0..15 -> {
                    f = funF(b, c, d)
                    g = i
                }
                in 16..31 -> {
                    f = funG(b, c, d)
                    g = (5 * i + 1) % 16
                }
                in 31..47 -> {
                    f = funH(b, c, d)
                    g = (3 * i + 5) % 16
                }
                else -> {
                    f = funI(b, c, d)
                    g = (7 * i) % 16
                }
            }
            f += a + t[i] + ints[g]
            a = d
            d = c
            c = b
            b += (f) cuhl s[i]
        }
        repeat(pRegs.size) {
            regs[it] += pRegs[it]
        }
    }

    fun digest(msg: ByteArray): List<UByte> {
        initBuffer()
        val eMsg = extendMessageToSize(msg)
        extendMessageWithLength(eMsg, msg.size)
            .asList()
            .chunked(64)
            .forEach { runBlock(it.toByteArray()) }
        return regs
            .flatMap(UInt::toUBytesBigEndian)
    }

    fun stringDigest(msg: ByteArray) = digest(msg)
        .joinToString("") {
            it.toString(16)
                .uppercase()
                .padStart(2, '0')
        }

}

fun main() {
    val md5 = Md5()
    val trueMd5 = MessageDigest.getInstance("MD5")
    val text = "md5".toByteArray()
    println(md5.digest(text))
    trueMd5.update(text)
    println(trueMd5.digest().joinToString("") {
        it.toUByte().toString(16).uppercase().padStart(2, '0')
    })
}