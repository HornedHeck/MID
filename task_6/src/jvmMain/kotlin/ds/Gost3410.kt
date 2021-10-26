package ds

import java.math.BigInteger
import math.minus
import math.plus
import md5.Md5
import java.security.SecureRandom
import kotlin.random.asJavaRandom
import kotlin.random.asKotlinRandom

class Gost3410(
    private val p: BigInteger,
    private val q: BigInteger,
    private val x: BigInteger,
) {

    private val g = q.modInverse(p).modPow((p - 1) / q, p)

    private val y = g.modPow(x, p)
    private val random = SecureRandom.getInstanceStrong().asKotlinRandom().asJavaRandom()
    private val hasher = Gost3411()

    private fun getHash(data: ByteArray): BigInteger {
        return BigInteger(hasher.digest(data) , 16)
    }

    fun digest(data: ByteArray): Pair<BigInteger, BigInteger> {
        val k = BigInteger(q.bitLength() - 1, random)
        val r = g.modPow(k, p).mod(q)
        val s = (k * getHash(data) + x * r).mod(q)
        return r to s
    }

    fun checkDigest(data: ByteArray, r: BigInteger, s: BigInteger): Boolean {
        val w = getHash(data).modInverse(q)
        val u1 = w * s % q
        val u2 = (q - r) * w % q
        val v = g.modPow(u1, p) * y.modPow(u2, p) % p % q
        return v == r
    }
}

fun main() {
    val message = "Test".toByteArray()
    val gost = Gost3410(
        p = (4294977287L).toBigInteger(),
        q = (2147488643L).toBigInteger(),
        x = (1263113L).toBigInteger()
    )
    val (r, s) = gost.digest(message)
    println(gost.checkDigest(message, r, s))
    println(gost.checkDigest(message, r + 1127, s))
}