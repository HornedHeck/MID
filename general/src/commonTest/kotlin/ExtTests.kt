import kotlin.test.Test
import kotlin.test.assertContentEquals

class ExtTests {

    @Test
    fun `UInt to UBytes conversion CORRECT`() {
        val src = 1208488192U

        val res = src.toUBytes()

        assertContentEquals(
            listOf(72, 8, 17, 0).map { it.toUByte() },
            res
        )
    }

}