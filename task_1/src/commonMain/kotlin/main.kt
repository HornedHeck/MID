fun main() {

    val text =
        "avada ke"

    require(text.encodeToByteArray().size % Long.SIZE_BYTES == 0)

    val key = "arvadek adava arvadek adava arva"

    val des = GOST(key.encodeToByteArray())

    val encrypted = des.encrypt(text.encodeToByteArray())

    val decrypted = des.decrypt(encrypted).decodeToString()

    println(decrypted)

    println(text == decrypted)
}