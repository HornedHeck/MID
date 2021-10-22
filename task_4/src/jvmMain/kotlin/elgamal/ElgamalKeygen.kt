package elgamal

import math.plus
import math.minus
import java.math.BigInteger
import java.math.BigInteger.*
import java.security.SecureRandom
import kotlin.random.asJavaRandom
import kotlin.random.asKotlinRandom

data class ElgamalKey(
    val p: BigInteger,
    val x: BigInteger,
    val y: BigInteger,
    val g: BigInteger
)

internal fun elgamalKeygen(bitSize: Int): ElgamalKey {
    val random = SecureRandom.getInstanceStrong().asKotlinRandom().asJavaRandom()
    val p = probablePrime(
        bitSize,
        random
    )
    require(p.isPrime())

    val x = BigInteger(p.bitLength() - 2, random) + 1

    var g = BigInteger(p.bitLength() / 3, random)
    while (!isPrimitiveRoot(g, p)) {
        g += 1
    }

    val y = g.modPow(x, p)

    return ElgamalKey(p, x, y, g)
}

private fun isPrimitiveRoot(a: BigInteger, m: BigInteger): Boolean {
    if (a.gcd(m) != ONE) return false
    if (a.modPow((m - 1) / TWO, m) == ONE) return false
    if (getPrimeDividers(m - 1).any { a.modPow((m - 1) / it, m) == ONE }) return false
    return true
}

private fun getPrimeDividers(a: BigInteger): List<BigInteger> {
    val res = mutableListOf<BigInteger>()
    var z = TWO
    val end = a.sqrt()
    while (z <= end) {
        if (a % z == ZERO && z.isPrime()) {
            res.add(z)
        }
        z += 1
    }
    return res
}

private fun BigInteger.isPrime(): Boolean {
    val end = sqrt()
    var z = TWO
    while (z <= end) {
        if (this % z == ZERO) {
            return false
        }
        z += 1
    }
    return true
}