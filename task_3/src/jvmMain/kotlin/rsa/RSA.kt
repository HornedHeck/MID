package rsa


import math.*
import java.math.BigInteger
import kotlin.random.Random

private val E = (257).toBigInteger()


/** @return RSA keypair: [Pair.first] - public, [Pair.second] - private */
fun generateKeys(p: BigInteger, q: BigInteger): Pair<RSAKey, RSAKey> {

    val n = p * q
    val sigma = (p - 1) * (q - 1)
    val d = E.modInverse(sigma)
    return RSAKey(E, n) to RSAKey(d, n)
}

class RSA(
    private val private: RSAKey?,
    private val public: RSAKey?,
    private val size: Int = (private ?: public!!).n.toByteArray().size - 1
) {


    fun decrypt(data: ByteArray): ByteArray {
        require(private != null) { "Can't decrypt without private key" }
        return data
            .join(size + 1)
            .map { it.modPow(private.v, private.n) }
            .split(size)
    }

    fun encrypt(data: ByteArray): ByteArray {
        require(public != null) { "Can't encrypt without public key" }
        return data
            .rsaJoin(size)
            .map {
                it.modPow(public.v, public.n)
            }
            .splitLeftPadded(size + 1)
    }


}

fun main() {
    val (public, private) = generateKeys((3557).toBigInteger(), (2579).toBigInteger())

    val rsa = RSA(private, public, 3)

    val data = "Hello world"
    val encrypted = rsa.encrypt(data.toByteArray())
    val decrypred = rsa.decrypt(encrypted)
    val decryptedStr = String(decrypred)

    println(public)
    println(private)
    println(data == decryptedStr)
    println(data)
    println(encrypted.contentToString())
    println(decryptedStr)
}

