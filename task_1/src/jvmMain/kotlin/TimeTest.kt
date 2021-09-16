import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec
import kotlin.system.measureTimeMillis

infix fun ByteArray.feq(b : ByteArray) : Boolean {
    return this[0] == b[0]
            && this[1] == b[1]
            && this[2] == b[2]
            && this[3] == b[3]
}

fun main() {
    val key = "12345678".toByteArray()
    val t = "Lorem ip"
    val kf = SecretKeyFactory.getInstance("DES")
    val des = Cipher.getInstance("DES")
    des.init(Cipher.ENCRYPT_MODE, kf.generateSecret(DESKeySpec(key)))
    val ct = des.doFinal(t.toByteArray())

    var wkey = 1
    val step = 10

    val ciphers = Array(100000) {
        Cipher.getInstance("DES").apply {
            init(Cipher.ENCRYPT_MODE, kf.generateSecret(DESKeySpec((1 + 10 * it).toBigInteger().bytes())))
        }
    }
    val tb = t.toByteArray()

    val wkeys = List(100000) {
        1 + 10 * it to ciphers[it]
    }

    println(measureTimeMillis {
        wkeys.forEach { (k, c) ->
            if (ct feq c.doFinal(tb)) {
                k.toBigInteger().bytes()
            }
        }
    } / 10)

}