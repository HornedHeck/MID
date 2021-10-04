import java.io.File
import java.nio.file.Files
import java.nio.file.OpenOption
import java.nio.file.StandardOpenOption
import kotlin.io.path.Path

enum class Mode(val param: String) {
    DES2("-D2"),
    DES3("-D3"),
    GOST("-G")
}

private const val ENCRYPT = "-e"
private const val DECRYPT = "-d"

fun main(args: Array<String>) {

    if (args.size !in 4..5) {
        println(
            "Wrong usage.\n" +
                    "Synopsis:\n" +
                    "\ttask_1 DIRECTION MODE k_file t_file\n" +
                    "Params:\n" +
                    "\t DIRECTION: -e or -d\n" +
                    "\t MODE: ${Mode.values().joinToString(", ") { it.param }}\n" +
                    "\t k_file: path to the key file\n" +
                    "\t t_file: path to the text file"
        )
        return
    }

    val direction = when (args[0]) {
        ENCRYPT -> {
            true
        }
        DECRYPT -> {
            false
        }
        else -> {
            println("Wrong usage. Consider using -e or -d")
            return
        }
    }

    val mode = Mode.values().firstOrNull { it.param == args[1] } ?: run {
        println(
            "Wrong usage. Consider using of of the following modes: ${
                Mode.values().joinToString(", ") { it.param }
            }"
        )
        return
    }

    val keyFile = File(args[2])
    if (!keyFile.exists()) {
        println("Key file not found.")
        return
    }

    val textFile = File(args[3])
    if (!textFile.exists()) {
        println("Text file not found.")
        return
    }

    val data = textFile.readBytes().pad(8)

    val res = when (mode) {
        Mode.DES2 -> run2Des(data, keyFile.readLines(), direction)
        Mode.DES3 -> run3Des(data, keyFile.readLines(), direction)
        Mode.GOST -> runGost(data, keyFile.readText(), direction)
    }

    Files.write(
        Path(args.getOrElse(4) { "res.txt" }),
        res,
        StandardOpenOption.CREATE,
        StandardOpenOption.TRUNCATE_EXISTING
    )
}

fun run2Des(data: ByteArray, keys: List<String>, direction: Boolean): ByteArray {
    val des1 = DES(keys[0].encodeToByteArray())
    val des2 = DES(keys[1].encodeToByteArray())

    return if (direction) {
        des2.encrypt(des1.encrypt(data))
    } else {
        des1.decrypt(des2.decrypt(data))
    }
}

fun run3Des(data: ByteArray, keys: List<String>, direction: Boolean): ByteArray {
    val des1 = DES(keys[0].encodeToByteArray())
    val des2 = DES(keys[1].encodeToByteArray())
    val des3 = DES(keys[2].encodeToByteArray())

    return if (direction) {
        des3.encrypt(des2.decrypt(des1.encrypt(data)))
    } else {
        des1.decrypt(des2.encrypt(des3.decrypt(data)))
    }
}

fun runGost(data: ByteArray, key: String, direction: Boolean): ByteArray {
    val gost = GOST(key.encodeToByteArray())

    return if (direction) {
        gost.encrypt(data)
    } else {
        gost.decrypt(data)
    }
}