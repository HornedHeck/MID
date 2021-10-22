package elgamal

import math.*
import java.math.BigInteger
import java.math.BigInteger.ONE
import java.security.SecureRandom
import kotlin.random.Random
import kotlin.random.asJavaRandom
import kotlin.random.asKotlinRandom

private const val PUBLIC_KEY_REQUIRED = "public key required for encryption"

class ElGamal(
    private val size: Int,
    private val p: BigInteger,
    private val x: BigInteger?,
    private val y: BigInteger?,
    private val g: BigInteger?
) {

    init {
//        require((256).toBigInteger().pow(size) < p) {
//            "Message must be shorter than p"
//        }
    }

    fun encrypt(data: ByteArray): Pair<ByteArray, ByteArray> {
        requireNotNull(y) { PUBLIC_KEY_REQUIRED }
        requireNotNull(g) { PUBLIC_KEY_REQUIRED }

        val random = SecureRandom.getInstanceStrong().asKotlinRandom().asJavaRandom()

        var k = BigInteger(p.bitLength(), random)
        while (k >= p - 1 || k.gcd(p - 1) != ONE) {
            k = BigInteger(p.bitLength(), random)
        }
//        println("k = $k")
        val a = g.modPow(k, p).toByteArray()
        val yK = y.modPow(k, p)
        val b = data.join(size)
            .map { it * yK % p }
            .splitLeftPadded(size + 1)
        return a to b
    }

    fun decrypt(a: ByteArray, b: ByteArray): ByteArray {
        requireNotNull(x) { "private key required for decryption" }
        val apx = BigInteger(a).modPow(p - 1 - x, p)
        return b.join(size + 1)
            .map {
                it * apx % p
            }
            .split(size)
    }
}

fun main() {

    val key = elgamalKeygen(15)
    println(key)

    val elGamal = ElGamal(1, key.p, key.x, key.y, key.g)

    val data = "Hello world"
    val encrypted = elGamal.encrypt(data.toByteArray())
    val decrypted = elGamal.decrypt(encrypted.first, encrypted.second)
    val decryptedStr = String(decrypted)
    println(data == decryptedStr)
    println(data)
    println(encrypted.first.contentToString() to encrypted.second.contentToString())
    println(decryptedStr)
}
