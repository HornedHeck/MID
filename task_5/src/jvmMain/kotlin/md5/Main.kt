package md5

import java.io.File

fun main(args: Array<String>) {

    if (args.size != 1) {
        println(
            "Wrong usage.\n" +
                    "Synopsis:\n" +
                    "\ttask_5 t_file\n" +
                    "Params:\n" +
                    "\t t_file: path to the target file"
        )
        return
    }

    val textFile = File(args[0])
    if (!textFile.exists()) {
        println("Target file not found.")
        return
    }

    val md5 = Md5()
    println(md5.stringDigest(textFile.readBytes()))

}