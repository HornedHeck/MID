package rsa

import java.io.File
import java.nio.file.Files
import java.nio.file.StandardOpenOption
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

    val textFile = File(args[2])
    if (!textFile.exists()) {
        println("Text file not found.")
        return
    }

    val keys = keyFile.readLines().map { it.toBigInteger() }

    val res = if (direction) {
        val rsa = RSA(null, RSAKey(keys[1], keys[0]) , 3)
        rsa.encrypt(textFile.readBytes())
    } else {
        val rsa = RSA(RSAKey(keys[2], keys[0]), null , 3)
        rsa.decrypt(textFile.readBytes())
    }


    Files.write(
        Path(args.getOrElse(3) { "res.txt" }),
        res,
        StandardOpenOption.CREATE,
        StandardOpenOption.TRUNCATE_EXISTING
    )
}