package skateparks.viewmodel

import java.awt.Toolkit
import java.net.URI
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.random.Random
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.runBlocking
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import org.jetbrains.compose.resources.decodeToImageBitmap
import skateparks.shared.generated.resources.Res

enum class SkateparkState( val description: String) {
    PROJECTED("geplant"),
    OPERATIONAL("in Betrieb"),
    UNDER_CONSTRUCTION("in Bau"),
}

enum class SkateparkClaimed( val description: String) {
    YES("ja"),
    NO("nein"),
}

private fun String?.asSkateparkState() : SkateparkState? = SkateparkState.entries.find { it.description == this }
private fun String?.asSkateparkClaimed() : SkateparkClaimed? = SkateparkClaimed.entries.find { it.description == this }


// TODO: Gegebenen Code nachvollziehen und verstehen.
// Die meisten Felder sind als nullable definiert. 'null' bedeutet 'Wert unbekannt'.
// Wie ist das genau gemacht? Wie sehen die Update-Methoden aus? Was muss im UI gemacht werden?

class Skatepark(
    name: String? = null,
    status: String? = null,
    fulladdress: String? = null,
    street: String? = null,
    zipPlace: String? = null,
    categories: String? = null,
    timezone: String? = null,
    amenities: String? = null,
    phone: String? = null,
    phones: String? = null,
    claimed: String? = null,
    count: Int? = null,
    avgRating: Double? = null,
    reviewUrl: String? = null,
    mapsUrl: String? = null,
    latitude: Double? = null,
    longitude: Double? = null,
    website: String? = null,
    domain: String? = null,
    openingHours: String? = null,
    imageUrl: String? = null,
    cid: Double? = null,
    fid: String? = null,
    googleId: String? = null,
    val id: Int = Random.nextInt()) {

    private val modelScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // all the attributes we need to display
    var name : String? by mutableStateOf(name)
        private set
    var status : SkateparkState? by mutableStateOf(status.asSkateparkState())
        private set
    var fulladdress : String? by mutableStateOf(fulladdress)
        private set
    var street : String? by mutableStateOf(street)
        private set
    var zipPlace : String? by mutableStateOf(zipPlace)
        private set
    var categories  : String?by mutableStateOf(categories)
        private set
    var timezone : String? by mutableStateOf(timezone)
        private set
    var amenities : String? by mutableStateOf(amenities)
        private set
    var phone : String? by mutableStateOf(phone)
        private set
    var phones : String? by mutableStateOf(phones)
        private set
    var claimed : SkateparkClaimed? by mutableStateOf(null)
        private set
    var count : Int? by mutableStateOf(count)
        private set
    var avgRating : Double? by mutableStateOf(avgRating)
        private set
    var reviewUrl : String? by mutableStateOf(reviewUrl)
        private set
    var mapsUrl : String? by mutableStateOf(mapsUrl)
        private set
    var longitude : Double? by mutableStateOf(longitude)
        private set
    var latitude : Double? by mutableStateOf(latitude)
        private set
    var website : String? by mutableStateOf(website)
        private set
    var domain : String? by mutableStateOf(domain)
        private set
    var openingHours : String? by mutableStateOf(openingHours)
        private set
    var googleId : String? by mutableStateOf(googleId)
        private set
    var imageUrl : String? by mutableStateOf(imageUrl)
        private set
    var imageBitmap : ImageBitmap  by mutableStateOf(defaultImageBitmap)
        private set
    var bitmapLoaded : Boolean by mutableStateOf(false)
        private set
    var cid : Double? by mutableStateOf(cid)
        private set
    var fid : String? by mutableStateOf(fid)
        private set

    // todo: wenn diese beiden Attribute verändert werden muss das Attribut 'fullAddress' ebenfalls verändert werden
    // implementieren Sie dies gemäss dem TestCase
    fun updateStreet(valueAsText: String) { street = valueAsText.ifBlank { null } }
    fun updateZipPlace(valueAsText: String) { zipPlace = valueAsText.ifBlank { null } }


    // hier die notwendigen update-Funktionen. Sie benutzen eine primitive Inputvalidierung.
    // Wenn etwas syntaktisch falsch ist, ertönt ein 'Beep'
    // todo: warum fehlt hier 'updateFullAddress' ?
    fun updateCount(valueAsText: String)     = valueAsText.ifInt { count = it }
    fun updateAvgRating(valueAsText: String) = valueAsText.ifDouble { avgRating = it }
    fun updateLongitude(valueAsText: String) = valueAsText.ifDouble { longitude = it }
    fun updateLatitude(valueAsText: String)  = valueAsText.ifDouble { latitude = it }

    fun updateImageURL(newValue: String){
        imageUrl = newValue
        imageBitmap = defaultImageBitmap
        bitmapLoaded = false
        loadImageBitmap()
    }

    fun updateName(newValue: String) { name = newValue.ifBlank { null } }
    fun updateStatus(valueAsText: String) { status = valueAsText.asSkateparkState() ?: SkateparkState.PROJECTED }
    fun updateCategories(valueAsText: String) { categories = valueAsText.ifBlank { null } }
    fun updateTimezone(valueAsText: String) { timezone = valueAsText.ifBlank { null } }
    fun updateAmenities(valueAsText: String) { amenities = valueAsText.ifBlank { null } }
    fun updatePhone(valueAsText: String) { phone = valueAsText.ifBlank { null } }
    fun updatePhones(valueAsText: String) { phones = valueAsText.ifBlank { null } }
    fun updateClaimed(valueAsText: String) { claimed = valueAsText.asSkateparkClaimed() }
    fun updateReviewUrl(valueAsText: String) { reviewUrl = valueAsText.ifBlank { null } }
    fun updateMapsUrl(valueAsText: String) { mapsUrl = valueAsText.ifBlank { null } }
    fun updateWebsite(valueAsText: String) { website = valueAsText.ifBlank { null } }
    fun updateDomain(valueAsText: String) { domain = valueAsText.ifBlank { null } }
    fun updateOpeningHours(valueAsText: String) { openingHours = valueAsText.ifBlank { null } }
    fun updateCid(valueAsText: String) = valueAsText.ifDouble { cid = it }
    fun updateFid(valueAsText: String) { fid = valueAsText.ifBlank { null } }
    fun updateGoogleId(valueAsText: String) { googleId = valueAsText.ifBlank { null } }

    fun loadImageBitmap(){
         //TODO: image asynchron laden
        // Hinweis: verwenden Sie die bestehende Funktion für synchrones Laden (s.u.)
    }

    // see: https://kotlinlang.org/docs/object-declarations.html#companion-objects
    companion object {
        val defaultImageBitmap = getImageBitmapFromResources("skatepark-default.jpg")
    }

}

/**
 * Reads an image file from the resources directory and converts it to an ImageBitmap.
 *
 * @param fileName The name of the file located in the "files" directory within the resources.
 * @return The decoded ImageBitmap from the specified file.
 */
private fun getImageBitmapFromResources(fileName: String) : ImageBitmap {
    var img: ImageBitmap?
    runBlocking {
        img =  Res.readBytes("files/$fileName")
            .decodeToImageBitmap()
    }
    return img!!
}

/**
 * Fetches and decodes an image bitmap from the provided URL.
 *
 * @param url The string URL from which the image bitmap is to be retrieved.
 * @return The decoded ImageBitmap if the operation is successful,
 *         or a default ImageBitmap in case of an exception.
 */
private fun getImageBitmapFromUrl(url: String): ImageBitmap {
    return try {
        URI(url).toURL().openStream().readAllBytes().decodeToImageBitmap()
    } catch (e: Exception) {
        Skatepark.defaultImageBitmap
    }
}


// einige hilfreiche Funktionen

private val ch = Locale.of("de", "CH")
private val chGroupingSeparator = DecimalFormatSymbols(ch).groupingSeparator

private val intCHRegex = Regex(pattern = """^\s*[+-]?[\d$chGroupingSeparator]{1,9}\s*$""")
private val floatCHRegex = Regex(pattern = """^\s*[+-]?[\d$chGroupingSeparator]{0,9}\.?\d*\s*$""")


private fun String.asDouble() = trim().replace("$chGroupingSeparator", "").toDoubleOrNull()
private fun String.asInt() = trim().replace("$chGroupingSeparator", "").toIntOrNull()


fun String.ifDouble(update: (Double?) -> Unit){
    if (this.isBlank() || floatCHRegex.matches(this)) {
        update(this.asDouble())
    } else {
        Toolkit.getDefaultToolkit().beep()
    }
}

fun String.ifInt(update: (Int?) -> Unit){
    if (this.isBlank() || intCHRegex.matches(this)) {
        update(this.asInt())
    } else {
        Toolkit.getDefaultToolkit().beep()
    }
}