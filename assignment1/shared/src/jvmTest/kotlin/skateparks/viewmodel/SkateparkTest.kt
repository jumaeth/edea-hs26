package skateparks.viewmodel

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SkateparkTest {

    private lateinit var skatepark: Skatepark

    @BeforeEach
    fun setup(){
        skatepark = Skatepark(id          = 999,
                              name        = "Skater's Paradise",
                              status      = "in Betrieb",
                              claimed     = "YES",
                              fulladdress = "Bahnhofstr. 6b, 5210 Windisch",
                              street      = "Bahnhofstr. 6b",
                              zipPlace    = "5210 Windisch")
    }

    @Test
    fun testConstructor(){
        //then
        assertEquals(SkateparkState.OPERATIONAL, skatepark.status)
        assertEquals(SkateparkClaimed.YES, skatepark.claimed)
        assertEquals("Bahnhofstr. 6b, 5210 Windisch", skatepark.fulladdress)
        assertEquals("Bahnhofstr. 6b", skatepark.street)
        assertEquals("5210 Windisch", skatepark.zipPlace)
    }

    @Test
    fun testIfInt(){
        //given
        var value : Int? = null

        //when
        "1".ifInt { value = it }

        //then
        assertEquals(1, value)

        //when
        "+2".ifInt { value = it }

        //then
        assertEquals(2, value)

        //when
        "-2".ifInt { value = it }

        //then
        assertEquals(-2, value)

        //when
        "-2’000".ifInt { value = it }

        //then
        assertEquals(-2000, value)

        //when
        "invalid input".ifInt { value = it }

        //then
        assertEquals(-2000, value)

        //when
        "   ".ifInt { value = it }

        //then
        assertNull(value)
    }

    @Test
    fun testIfDouble(){
        //given
        var value : Double? = null

        //when
        "1".ifDouble { value = it }

        //then
        assertEquals(1.0, value)

        //when
        "+2.5".ifDouble { value = it }

        //then
        assertEquals(2.5, value)

        //when
        "-2.5".ifDouble { value = it }

        //then
        assertEquals(-2.5, value)

        //when
        "2.".ifDouble { value = it }

        //then
        assertEquals(2.0, value)

        //when
        ".5".ifDouble { value = it }

        //then
        assertEquals(0.5, value)

        //when
        "-2’000.25".ifDouble { value = it }

        //then: swiss grouping separator
        assertEquals(-2000.25, value)

        //when
        "invalid input".ifDouble { value = it }

        //then: value doesn't change on invalid input
        assertEquals(-2000.25, value)

        //when
        "   ".ifDouble { value = it }

        //then: blank input means 'Wert unbekannt'
        assertNull(value)
    }


    @Test
    fun testUpdateZipPlace(){
        //given
        val newZipPlace = "9999 Himmelsdorf"

        //when
        skatepark.updateZipPlace(newZipPlace)

        //then
        assertEquals(newZipPlace, skatepark.zipPlace)
        assertEquals("${skatepark.street}, $newZipPlace", skatepark.fulladdress)
    }

    @Test
    fun testUpdateStreet(){
        //given
        val newStreet = "Pump Street 1"

        //when
        skatepark.updateStreet(newStreet)

        //then
        assertEquals(newStreet, skatepark.street)
        assertEquals("$newStreet, ${skatepark.zipPlace}", skatepark.fulladdress)
    }

}