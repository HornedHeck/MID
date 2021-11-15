import java.awt.image.Raster
import java.awt.image.WritableRaster
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import javax.imageio.ImageIO
import kotlin.experimental.and
import kotlin.experimental.or
import kotlin.io.path.Path

private const val START_OFFSET = 100
private val SAVE_MASK = (-2).toByte()
private val LOAD_MASK = (1).toByte()

class PngSteg {

    fun save(message: ByteArray, img: ByteArray): List<Byte> {
        require(message.size * Byte.SIZE_BITS + START_OFFSET <= img.size)
        val skipped = img.take(START_OFFSET)
        val data = img
            .drop(START_OFFSET)
            .take(message.size * Byte.SIZE_BITS)
            .chunked(Byte.SIZE_BITS)
            .flatMapIndexed { i, bytes ->
                var byte = message[i]
                bytes.map {
                    val b = byte and 1
                    byte = byte.div(2).toByte()
                    it and SAVE_MASK or b
                }
            }
        val zeros = img
            .drop(START_OFFSET + data.size)
            .map { it and SAVE_MASK }
        return skipped + data + zeros
    }

    fun load(img: ByteArray): List<Byte> {
        val data = img
            .drop(START_OFFSET)
            .chunked(Byte.SIZE_BITS)
            .mapIndexed { i, bytes ->
                var byte = 0
                bytes.reversed().forEach {
                    byte = (byte shl 1) or (it and LOAD_MASK).toInt()
                }
                byte.toByte()
            }
        return data.dropLastWhile { it.toInt() == 0 }
    }
}

fun Raster.toByteArray(): ByteArray {
    val res = mutableListOf<Byte>()
    repeat(width) { i ->
        repeat(height) { j ->
            val a = IntArray(4)
            getPixel(i, j, a)
            res.addAll(a.map { it.toByte() })
        }
    }
    return res.toByteArray()
}

fun WritableRaster.applyBytes(bytes: ByteArray) {
    val pixels = bytes.map { it.toInt() }.chunked(4) { it.toIntArray() }
    repeat(width) { i ->
        repeat(height) { j ->
            setPixel(i, j, pixels[i * height + j])
        }
    }

}

fun main() {

    val imgRaw = ImageIO.read(File("/home/hornedheck/IdeaProjects/MID/img.png"))
    val message = "Hello world"
    val steg = PngSteg()
    val bytes = imgRaw.data.toByteArray()
    val saved = steg.save(message.encodeToByteArray() , bytes).toByteArray()
    imgRaw.raster.applyBytes(saved)
    ImageIO.write(imgRaw , "png" , File("/home/hornedheck/IdeaProjects/MID/img2.png") )
    val loaded = steg.load(saved).toByteArray()
    println(String(loaded))
}