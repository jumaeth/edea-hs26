package skateparks.viewmodel

import kotlinx.coroutines.joinAll
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FederalAdministrationTest {
    private lateinit var administration: FederalAdministration

    // ein skatepark ohne 'imageUrl', damit beim auswählen kein Bild aus dem internet geladen wird
    private lateinit var parkWithoutImage: Skatepark

    @BeforeEach
    fun setup(){
        administration   = FederalAdministration()
        parkWithoutImage = Skatepark(name     = "Skater's Paradise",
                                     street   = "Bahnhofstr. 6b",
                                     zipPlace = "5210 Windisch")
    }

    @Test
    fun testConstructor(){
        //then
        assertEquals("Skatepark Admin Board", administration.title)
        assertFalse(administration.allSkateparks.isEmpty())
        assertNull(administration.skateparkUnderControl)
    }

    @Test
    fun testSortSkateparksByZIPCode(){
        //when
        administration.sortSkateparksByZIPCode()

        //then: zip codes sort asc
        val zipCodes = administration.allSkateparks.map { it.zipPlace!!.take(4).toInt() }
        assertEquals(zipCodes.sorted(), zipCodes)
    }

    @Test
    fun testSortWithInvalidZipCode(){
        //given
        val parkWithInvalidZip = Skatepark(zipPlace = "Bern")
        val parkWithoutZip     = Skatepark()
        administration.allSkateparks.add(parkWithInvalidZip)
        administration.allSkateparks.add(parkWithoutZip)

        //when
        administration.sortSkateparksByZIPCode()

        //then: no crash, parks without a valid zip code are sorted to the top
        assertTrue(administration.allSkateparks.indexOf(parkWithInvalidZip) < 2)
        assertTrue(administration.allSkateparks.indexOf(parkWithoutZip) < 2)
    }

    @Test
    fun testUpdateSkateparkUnderControl(){
        //when
        administration.updateSkateparkUnderControl(parkWithoutImage)

        //then
        assertEquals(parkWithoutImage, administration.skateparkUnderControl)

        //when
        administration.updateSkateparkUnderControl(null)

        //then: nothing is selected
        assertNull(administration.skateparkUnderControl)
    }

    @Test
    fun testIsSelected(){
        //given
        val otherPark = administration.allSkateparks[0]

        //when
        administration.updateSkateparkUnderControl(parkWithoutImage)

        //then
        assertTrue(administration.isSelected(parkWithoutImage))
        assertFalse(administration.isSelected(otherPark))

        //when
        administration.updateSkateparkUnderControl(null)

        //then: nothing is selected
        assertFalse(administration.isSelected(parkWithoutImage))
    }

    @Test
    fun testDeleteEnabled(){
        //then: nothing is selected, nothing can be deleted
        assertFalse(administration.deleteEnabled)

        //when
        administration.updateSkateparkUnderControl(parkWithoutImage)

        //then
        assertTrue(administration.deleteEnabled)
    }

    @Test
    fun testLoadAllImageBitmaps(){
        //given: invalid url, so the test doesn't depend on the internet
        administration.allSkateparks.clear()
        administration.allSkateparks.add(Skatepark(imageUrl = "not a valid url"))
        administration.allSkateparks.add(Skatepark(imageUrl = "also not a valid url"))
        administration.allSkateparks.add(parkWithoutImage)

        //when
        val jobs = administration.loadAllImageBitmaps()
        runBlocking { jobs.joinAll() }  // wait until all jobs are finished

        //then: url loaded, a failed load shows the default image
        assertEquals(2, jobs.size)
        assertTrue(administration.allSkateparks[0].bitmapLoaded)
        assertTrue(administration.allSkateparks[1].bitmapLoaded)
        assertEquals(Skatepark.defaultImageBitmap, administration.allSkateparks[0].imageBitmap)
        assertFalse(parkWithoutImage.bitmapLoaded)
    }

    @Test
    fun testCreate(){
        //given
        val sizeBefore = administration.allSkateparks.size

        //when
        administration.create()

        //then: new and empty skatepark is added at the end and selected
        assertEquals(sizeBefore + 1, administration.allSkateparks.size)
        assertEquals(administration.allSkateparks.last(), administration.skateparkUnderControl)
        assertNull(administration.skateparkUnderControl!!.name)
    }

    @Test
    fun testCreateUniqueIds(){
        //when: many skateparks are created quickly one after the other
        repeat(100) {
            administration.create()
        }

        //then: every skatepark has its own id (the id is the key in the explorer)
        val ids = administration.allSkateparks.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun testDelete(){
        //given
        administration.allSkateparks.add(0, parkWithoutImage)
        administration.updateSkateparkUnderControl(parkWithoutImage)
        val sizeBefore = administration.allSkateparks.size

        //when
        administration.delete()

        //then: the skatepark is removed and the next one is selected
        assertEquals(sizeBefore - 1, administration.allSkateparks.size)
        assertFalse(administration.allSkateparks.contains(parkWithoutImage))
        assertEquals(administration.allSkateparks[0], administration.skateparkUnderControl)
    }

    @Test
    fun testDeleteLastSkatepark(){
        //given
        administration.create()
        val newSkatepark = administration.skateparkUnderControl

        //when
        administration.delete()

        //then: the skatepark before the deleted one is selected
        assertFalse(administration.allSkateparks.contains(newSkatepark))
        assertEquals(administration.allSkateparks.last(), administration.skateparkUnderControl)
    }

    @Test
    fun testDeleteAll(){
        //given
        administration.create()

        //when
        while (administration.allSkateparks.isNotEmpty()) {
            administration.delete()
        }

        //then: nothing is left to select
        assertNull(administration.skateparkUnderControl)
    }

    @Test
    fun testDeleteWithoutSelection(){
        //given
        val sizeBefore = administration.allSkateparks.size

        //when
        administration.delete()

        //then: nothing is deleted
        assertEquals(sizeBefore, administration.allSkateparks.size)
        assertNull(administration.skateparkUnderControl)
    }

    @Test
    fun testSave(){
        //given
        administration.updateSkateparkUnderControl(parkWithoutImage)
        val sizeBefore = administration.allSkateparks.size

        //when
        administration.save()

        //then: save only works in memory, the data and the selection dont change
        assertEquals(sizeBefore, administration.allSkateparks.size)
        assertEquals(parkWithoutImage, administration.skateparkUnderControl)
    }

    @Test
    fun testSaveScrollsToSelection(){
        //given
        administration.updateSkateparkUnderControl(parkWithoutImage)

        //when
        val job = administration.save()

        //then: scrolling to the selected skatepark is started
        assertNotNull(job)
    }

    @Test
    fun testSaveWithoutSelection(){
        //when
        val job = administration.save()

        //then: without a selection there is nothing to scroll to
        assertNull(job)
    }
}
