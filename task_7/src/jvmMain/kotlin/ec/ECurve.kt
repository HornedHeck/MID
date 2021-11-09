package ec

import java.math.BigInteger
import math.plus
import math.times
import java.math.BigInteger.ZERO
import java.security.SecureRandom
import kotlin.random.asJavaRandom
import kotlin.random.asKotlinRandom

private val random = SecureRandom.getInstanceStrong().asKotlinRandom().asJavaRandom()

class ECurve(
    val p: BigInteger,
    private val a: BigInteger,
    private val b: BigInteger,
    val base: EPoint,
    val n: BigInteger,
    private val h: BigInteger
) {

    fun sum(p: EPoint, q: EPoint): EPoint {
        if (p == EPoint.ZERO) {
            return q
        }
        if (q == EPoint.ZERO) {
            return p
        }
        if (p.x == q.x && p.y == this.p - q.y) {
            return EPoint.ZERO
        }
        val m = if (p == q) {
            ((p.x * p.x * 3 + a) * (p.y * 2).modInverse(this.p)).mod(this.p)
        } else {
            ((p.y - q.y) * (p.x - q.x).modInverse(this.p)).mod(this.p)
        }
        val x = (m * m - p.x - q.x).mod(this.p)
        val y = (p.y + m * (x - p.x)).mod(this.p)
        return EPoint(x, this.p - y)
    }

    fun minus(p: EPoint, q: EPoint) = sum(
        p, q.copy(y = this.p - q.y)
    )

    private fun bits(src: BigInteger) = sequence {
        var a = src
        while (a > ZERO) {
            yield(a.and(BigInteger.ONE).toInt())
            a = a shr 1
        }
    }

    fun mul(k: BigInteger, p: EPoint): EPoint {
        var r = EPoint(ZERO, ZERO)
        var inc = p
        for (b in bits(k.mod(this.p))) {
            if (b == 1) {
                r = sum(r, inc)
            }
            inc = sum(inc, inc)
        }
        return r
    }

    fun keygen(): Pair<BigInteger, EPoint> {
        val d = BigInteger(n.bitLength() - 1, random) + 1
        val h = mul(d, base)
        return d to h
    }

}

fun main() {
    val curve = SECP256K1
    val (dA, hA) = curve.keygen()
    println(curve.minus(hA, hA))
}

