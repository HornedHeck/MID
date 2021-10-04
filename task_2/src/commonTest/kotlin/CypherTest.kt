import kotlin.test.Test
import kotlin.test.assertEquals

class CypherTest {

    @Test
    fun `h is CORRECT`() {
        val src = 0xA2U
        val exp = 0x9B
        val res = h(src.toUByte())

        assertEquals(exp.toUByte(), res)
    }
}