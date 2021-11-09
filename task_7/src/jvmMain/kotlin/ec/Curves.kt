package ec

import java.math.BigInteger

val SECP256K1 = ECurve(
    p = BigInteger( "fffffffffffffffffffffffffffffffffffffffffffffffffffffffefffffc2f" , 16),
    a = BigInteger.ZERO,
    b = (7).toBigInteger(),
    base = EPoint(
        x = BigInteger("79be667ef9dcbbac55a06295ce870b07029bfcdb2dce28d959f2815b16f81798",16),
        y = BigInteger("483ada7726a3c4655da4fbfc0e1108a8fd17b448a68554199c47d08ffb10d4b8",16),
    ),
    n = BigInteger("fffffffffffffffffffffffffffffffebaaedce6af48a03bbfd25e8cd0364141" , 16),
    h = BigInteger.ONE
)