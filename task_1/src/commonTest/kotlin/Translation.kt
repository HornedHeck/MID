import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class Translation {

    @Test
    fun `X translation is CORRECT`() {
        assertEquals(X.size, X.distinct().size)
    }

    @Test
    fun `Y translation is CORRECT`() {
        val data = List(X.size) { it }

        var converted = X.map { data[it] }
        converted = Y.map { converted[it] }

        assertContentEquals(data, converted)
    }

    @Test
    fun `K_C translation is CORRECT`() {
        assertEquals(K_C.size, K_C.distinct().size)
    }

}