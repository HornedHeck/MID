package rsa

import java.math.BigInteger

data class RSAKey(
    val v: BigInteger,
    val n: BigInteger
)
