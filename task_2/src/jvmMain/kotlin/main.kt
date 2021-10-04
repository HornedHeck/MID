fun main() {

    val key = ByteArray(32) { it.toByte() }

    val data = List(4){it.toUInt() * 256U}

    val cypher = Cypher(key)

    val e = cypher.encryptBlock(data)
    val d = cypher.decryptBlock(e)

    println(d == data)

}