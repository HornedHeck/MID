package ecdsa

import ec.ECurve
import ec.EPoint
import ec.SECP256K1
import java.math.BigInteger
import java.math.BigInteger.ZERO
import java.security.SecureRandom
import kotlin.random.asJavaRandom
import kotlin.random.asKotlinRandom
import math.plus

private val random = SecureRandom.getInstanceStrong().asKotlinRandom().asJavaRandom()

class ECDSA(
    private val curve: ECurve,
    private val h: EPoint,
    private val d: BigInteger?
) {

    fun digest(data: ByteArray): Pair<BigInteger, BigInteger> {
        requireNotNull(d)
        val hash = data.sha256()
        var s = ZERO
        var r = ZERO
        while (s == ZERO) {
            val k = BigInteger(curve.n.bitLength() - 1, random) + 1
            val p = curve.mul(k, curve.base)
            r = p.x % curve.n
            if (r == ZERO) continue
            s = k.modInverse(curve.n) * (hash + r * d) % curve.n
        }
        return r to s
    }

    fun checkDigest(r: BigInteger, s: BigInteger, data: ByteArray): Boolean {
        val sInv = s.modInverse(curve.n)
        val u1 = sInv * data.sha256() % curve.n
        val u2 = sInv * r % curve.n
        val p = curve.sum(
            curve.mul(u1, curve.base),
            curve.mul(u2, h)
        )
        return r == p.x % curve.n
    }
}

fun main() {
    val text = "Hello world"
    val bytes = text.encodeToByteArray()
    val curve = SECP256K1
    val (d, h) = curve.keygen()
    val ecdsa = ECDSA(curve, h, d)
    val digest = ecdsa.digest(bytes)
    println(ecdsa.checkDigest(digest.first, digest.second, bytes))
}