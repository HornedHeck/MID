package rsa

import java.math.BigInteger

internal operator fun BigInteger.minus(int: Int): BigInteger = when (int) {
    0 -> this
    1 -> this - BigInteger.ONE
    -1 -> this + BigInteger.ONE
    2 -> this - BigInteger.TWO
    -2 -> this + BigInteger.TWO
    10 -> this - BigInteger.TEN
    -10 -> this + BigInteger.TEN
    else -> this - int.toBigInteger()

}

fun ByteArray.leadingZerosPad(size: Int) : ByteArray{
    return if (this.size >= size){
        this
    }else{
        ByteArray(size - this.size) + this
    }


}