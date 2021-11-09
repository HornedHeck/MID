package ec

import java.math.BigInteger

data class EPoint(
    val x : BigInteger,
    val y : BigInteger,
){

    companion object{
        val ZERO = EPoint(BigInteger.ZERO , BigInteger.ZERO)
    }

}

