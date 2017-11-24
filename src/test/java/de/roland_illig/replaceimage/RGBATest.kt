package de.roland_illig.replaceimage

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class RGBATest {

    private fun RGBA.str(): String {
        val sb = StringBuilder()
        for (j in 0 until height)
            for (i in 0 until width)
                sb.append(get(i, j)).append(if (i == width - 1) "" else " ")
        return sb.toString()
    }

    @Test
    fun draw() {
        val img100 = RGBA(3, 3)
        for (i in 0 until 3)
            for (j in 0 until 3)
                img100[i, j] = 100 + 10 * i + j

        assertThat(img100.str()).isEqualTo("" +
                "100 110 120" +
                "101 111 121" +
                "102 112 122")

        val img200 = RGBA(3, 3)
        for (i in 0 until 3)
            for (j in 0 until 3)
                img200[i, j] = 200 + 10 * i + j

        assertThat(img200.str()).isEqualTo("" +
                "200 210 220" +
                "201 211 221" +
                "202 212 222")

        img100.draw(1, 1, img200)

        assertThat(img100.str()).isEqualTo("" +
                "100 110 120" +
                "101 200 210" +
                "102 201 211")

        img100.replace(
                RGBA(1, 1).also { it[0, 0] = 200 },
                RGBA(3, 1).also { it[0, 0] = 300; it[1, 0] = 301; it[2, 0] = 302 })

        assertThat(img100.str()).isEqualTo("" +
                "100 110 120" +
                "101 300 301" +
                "102 201 211")
    }

    @Test
    fun `replace 1×1`() {
        val img = RGBA(5, 5)
        val with = RGBA(1, 1).also { it[0, 0] = 3 }
        img.replace(RGBA(1, 1), with)

        assertThat(img.str()).isEqualTo("" +
                "3 3 3 3 3" +
                "3 3 3 3 3" +
                "3 3 3 3 3" +
                "3 3 3 3 3" +
                "3 3 3 3 3")
    }

    @Test
    fun `replace 2×2`() {
        val img = RGBA(5, 5)
        val with = RGBA(2, 2).also { it[0, 0] = 1; it[1, 0] = 2; it[0, 1] = 3; }
        img.replace(RGBA(2, 2), with)

        assertThat(img.str()).isEqualTo("" +
                "1 2 1 2 0" +
                "3 0 3 0 0" +
                "1 2 1 2 0" +
                "3 0 3 0 0" +
                "0 0 0 0 0")
    }
}
