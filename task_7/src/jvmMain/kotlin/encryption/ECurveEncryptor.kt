package encryption

import ec.ECurve
import ec.EPoint
import ec.SECP256K1
import java.math.BigInteger
import java.security.SecureRandom
import kotlin.random.asJavaRandom
import kotlin.random.asKotlinRandom
import math.plus

private val random = SecureRandom.getInstanceStrong().asKotlinRandom().asJavaRandom()

class ECurveEncryptor(
    private val eCurve: ECurve,
    // public
    private val h: EPoint,
    // private
    private val d: BigInteger
) {

    private val dataKey = eCurve.mul(d, h)

    private val byteConverter: Map<Byte, EPoint>
    private val pointConverter: Map<EPoint, Byte>

    init {
        val byteConverterTemp: MutableMap<Byte, EPoint> = mutableMapOf()
        val pointConverterTemp: MutableMap<EPoint, Byte> = mutableMapOf()
        val multiplier = eCurve.n / (257).toBigInteger()
        (1..256).zip(-128..127).forEach { (k, b) ->
            val p = eCurve.mul(k.toBigInteger() * multiplier, eCurve.base)
            byteConverterTemp[b.toByte()] = p
            pointConverterTemp[p] = b.toByte()
        }
        byteConverter = byteConverterTemp.toMap()
        pointConverter = pointConverterTemp.toMap()
    }

    fun encrypt(data: ByteArray): Pair<EPoint, List<EPoint>> {
        val k = BigInteger(
            random.nextInt(eCurve.p.bitLength() - 1),
            random
        ) + 1
        val prefix = eCurve.mul(k, eCurve.base)
        val key = eCurve.mul(k, h)
        val encrypted = data.map {
            eCurve.sum(byteConverter[it]!!, key)
        }
        return prefix to encrypted
    }

    fun decrypt(prefix: EPoint, data: List<EPoint>): ByteArray {
        val dKey = eCurve.mul(d, prefix)
        return data.map {
            pointConverter[eCurve.minus(it, dKey)]!!
        }.toByteArray()
    }
}

fun main() {
    val curve = SECP256K1
    val (d, h) = curve.keygen()
    val cipher = ECurveEncryptor(curve, h, d)
    val text = "Hello world"
    val encrypted = cipher.encrypt(text.encodeToByteArray())
    val decrypted = cipher.decrypt(encrypted.first, encrypted.second)
    println(String(decrypted))
}