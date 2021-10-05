package rsa


import java.math.BigInteger
import kotlin.random.Random

private val E = (3).toBigInteger()
//private val E = (257).toBigInteger()


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
    private val size: Int = (private ?: public!!).n.toByteArray().size
) {


    private fun ByteArray.join() = this.asList()
        .chunked(this@RSA.size)
        .map {
            BigInteger(it.toByteArray())
        }

    private fun List<BigInteger>.split() = this
        .flatMap {
            it.toByteArray()
                .leadingZerosPad(this@RSA.size)
                .asIterable()
        }
        .toByteArray()

    fun decrypt(data: ByteArray): ByteArray {
        require(private != null) { "Can't decrypt without private key" }
        return data
            .join()
            .map { it.modPow(private.v, private.n) }
            .split()
    }

    fun encrypt(data: ByteArray): ByteArray {
        require(public != null) { "Can't encrypt without public key" }
        return data
            .join()
            .map { it.modPow(public.v, public.n) }
            .split()
    }


}

fun main() {
    val (public, private) = generateKeys((3557).toBigInteger(), (2579).toBigInteger())

    val rsa = RSA(private, public)

    val data = BigInteger.valueOf(111111L).toByteArray()
    val encrypted = rsa.encrypt(data)
    val decrypred = rsa.decrypt(encrypted)

    println(encrypted)
    println(public.n.toByteArray().size)
}

