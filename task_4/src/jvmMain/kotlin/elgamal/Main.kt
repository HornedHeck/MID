package elgamal

import java.io.File
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.security.interfaces.RSAKey
import kotlin.io.path.Path

fun main(args: Array<String>) {

    val ENCRYPT = "--encrypt"
    val DECRYPT = "--decrypt"

    if (args.size !in 4..5) {
        println(
            "Wrong usage.\n" +
                    "Synopsis:\n" +
                    "\ttask_3 DIRECTION k_file t_file\n" +
                    "Params:\n" +
                    "\t DIRECTION: $ENCRYPT or $DECRYPT\n" +
                    "\t k_file: path to the key file. 1st line - n, 2nd line e, 3rd line d\n" +
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

    val keyFile = File(args[1])
    if (!keyFile.exists()) {
        println("Key file not found.")
        return
    }

    val keys = keyFile.readLines().map { it.toBigInteger() }
    val elGamal = ElGamal(1, keys[0], keys[1], keys[2], keys[3])

    if (direction) {
        val textFile = File(args[2])
        if (!textFile.exists()) {
            println("Text file not found.")
            return
        }

        val data = elGamal.encrypt(textFile.readBytes())
        Files.write(
            Path("a-" + args.getOrElse(3) { "res.txt" }),
            data.first,
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING
        )

        Files.write(
            Path("b-" + args.getOrElse(3) { "res.txt" }),
            data.second,
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING
        )
    } else {
        val baseName = File(args[2])
        val a = File("a-$baseName").readBytes()
        val b = File("b-$baseName").readBytes()

        val data = elGamal.decrypt(a, b)

        Files.write(
            Path(args.getOrElse(3) { "res.txt" }),
            data,
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING
        )
    }
}