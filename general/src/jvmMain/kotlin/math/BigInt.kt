package math

import java.math.BigInteger

operator fun BigInteger.minus(int: Int): BigInteger = when (int) {
    0 -> this
    1 -> this - BigInteger.ONE
    -1 -> this + BigInteger.ONE
    2 -> this - BigInteger.TWO
    -2 -> this + BigInteger.TWO
    10 -> this - BigInteger.TEN
    -10 -> this + BigInteger.TEN
    else -> this - int.toBigInteger()
}

operator fun BigInteger.plus(int: Int): BigInteger = when (int) {
    0 -> this
    1 -> this + BigInteger.ONE
    -1 -> this - BigInteger.ONE
    2 -> this + BigInteger.TWO
    -2 -> this - BigInteger.TWO
    10 -> this + BigInteger.TEN
    -10 -> this - BigInteger.TEN
    else -> this + int.toBigInteger()
}

fun ByteArray.leadingZerosPad(size: Int): ByteArray {
    return if (this.size >= size) {
        this
    } else {
        ByteArray(size - this.size) + this
    }
}


fun ByteArray.trailingZerosPad(size: Int): ByteArray {
    return if (this.size >= size) {
        this
    } else {
        this + ByteArray(size - this.size)
    }
}


fun ByteArray.rsaJoin(size: Int) = this.asList()
    .chunked(size)
    .map {
        BigInteger(1, it.toByteArray())
    }

fun ByteArray.join(size: Int) = this.asList()
    .chunked(size)
    .map {
        BigInteger(it.toByteArray())
    }

fun List<BigInteger>.splitLeftPadded(size: Int) = this
    .flatMap {
        it.toByteArray()
            .leadingZerosPad(size)
            .asIterable()
    }
    .toByteArray()

fun List<BigInteger>.split(size: Int) = this
    .flatMap {
        it.toByteArray()
            .let { bytes ->
                if (bytes.size > size) {
                    bytes.takeLast(size)
                } else {
                    bytes.asIterable()
                }
            }
    }
    .toByteArray()