import kotlinx.coroutines.*
import java.lang.Exception
import java.math.BigInteger

private const val COUNT = 12
private val WORKER_KEYS = ULong.MAX_VALUE.div(COUNT.toUInt())
private val LOG_INTERVAL = ULong.MAX_VALUE.div(COUNT.toUInt()).div(1_000_000_000_000_00UL).toLong()

suspend fun main() {
//    val key = "12345678".toByteArray()
    val key = byteArrayOf(0, 0, 0, 0, 0, 10, 10, 10)
    val text = "Lorem ip"
    val des = DES(key)
    val cypher = des.encrypt(text.toByteArray())

    CoroutineScope(Dispatchers.Default).launch {
        val keys = List(COUNT) {
            async { Worker(it, COUNT).launch(text, cypher) }
        }

        keys.forEach {
            it.invokeOnCompletion {
                keys.forEach { k -> k.cancel() }
            }
        }
        keys.joinAll()
    }.join()
}

private val MAX_VALUE = BigInteger(ULong.MAX_VALUE.toString())
private val TARGET_VALUE = (657930).toBigInteger()


fun BigInteger.bytes() : ByteArray {
    val bytes = toByteArray()
    return if (bytes.size < 8) {
        ByteArray(8 - bytes.size) + bytes
    } else {
        bytes.take(8).toByteArray()
    }
}

class Worker(val initialValue : Int, step : Int) {

    private var key = initialValue.toBigInteger()
    private val step = step.toBigInteger()

    fun launch(t : String, ct : ByteArray) : ByteArray? {
        var counter = 0L
        while (key < MAX_VALUE) {
            val des = DES(key.bytes())
            if (ct.contentEquals(des.encrypt(t.toByteArray()))) {
                return key.bytes()
            } else {
                if (key == TARGET_VALUE){
                    throw Exception("Key Skipped")
                }
                key += step
            }
            if ((counter + 1) % LOG_INTERVAL == 0L) {
                println(
                    "$ Worker-${initialValue + 1}: ${counter + 1}/${WORKER_KEYS} (${
                        "%.2f".format(
                            (counter + 1).div(
                                WORKER_KEYS.toFloat()
                            )
                        )
                    })"
                )
            }
            counter++
        }
        return null
    }
}

