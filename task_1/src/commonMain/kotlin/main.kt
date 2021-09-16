fun main(args : Array<String>) {

    val text =
        "Lorem ipLorem ipLorem ipLorem ipLorem ipLorem ip"

    require(text.encodeToByteArray().size % Long.SIZE_BYTES == 0)

    val key = "12345678"

    val des = DES(key.encodeToByteArray())

    val encrypted = des.encrypt(text.encodeToByteArray())

    val decrypted = des.decrypt(encrypted).decodeToString()

    println(decrypted)

    println(text == decrypted)
}