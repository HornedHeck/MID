package ds

import java.io.File

fun main(args: Array<String>) {

    if (args.size !in 1..2) {
        println(
            "Wrong usage.\n" +
                    "Synopsis:\n" +
                    "\ttask_6 t_file [eds_file]\n" +
                    "Params:\n" +
                    "\t t_file: path to the text file\n" +
                    "\t eds_file: optional, path to the expected signature file"
        )
        return
    }

    val direction = args.size == 2

    val textFile = File(args[0])
    if (!textFile.exists()) {
        println("Text file not found.")
        return
    }
    val data = textFile.readBytes()

    val eds = Gost3410(
        p = (4294977287L).toBigInteger(),
        q = (2147488643L).toBigInteger(),
        x = (1263113L).toBigInteger()
    )
    if (direction) {
        val edsFile = File(args[1])
        if (!edsFile.exists()) {
            println("EDS file not found")
        }
        val params = edsFile.readLines().map { it.toBigInteger(16) }
        println(eds.checkDigest(data, params[0], params[1]))
    }else{
        val signature = eds.digest(data)
        println(signature.first.toString(16).uppercase())
        println(signature.second.toString(16).uppercase())
    }
}