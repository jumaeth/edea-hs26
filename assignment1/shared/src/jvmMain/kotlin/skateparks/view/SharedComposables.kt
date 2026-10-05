package skateparks.view

import java.awt.Cursor
import java.util.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


/**
 * Stellt eine Liste als scrollbare Liste dar.
 *
 * Der Explorer ist generisch und unabhängig von einer konkreten Model-Klasse. Wie ein einzelnes Item
 * aussieht bestimmt der Aufrufer mit [listItem].
 * Der [scrollState] kommt aus dem Model, damit auch das Model die Liste scrollen kann.
 *
 * @param data Die Liste der Daten-Items, die angezeigt werden sollen.
 * @param key Lambda, das für ein Daten-Item einen eindeutigen Key liefert (z.B. die 'id').
 * @param scrollState Der Zustand der Liste, mit dem die Scroll-Position gesteuert wird.
 * @param modifier Der Modifier für den gesamten Explorer.
 * @param listItem Composable Function, die ein einzelnes Daten-Item visualisiert.
 */
@Composable
fun <T> GenericExplorer(data:        List<T>,
                        key:         (T) -> Any,
                        scrollState: LazyListState,
                        modifier:    Modifier = Modifier,
                        listItem:    @Composable (T) -> Unit) {
    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(state    = scrollState,
                   modifier = Modifier.padding(end = 12.dp)) {
            items(items = data,
                  key   = { key(it) }) {
                listItem(it)
            }
        }

        VerticalScrollbar(adapter  = rememberScrollbarAdapter(scrollState),
                          modifier = Modifier.align(Alignment.CenterEnd)
                                             .fillMaxHeight())
    }
}


/**
 * Funktion, die das Grundlayout einer Master-Detail-Applikation darstellt.
 *
 * Oben wird eine graue Toolbar angezeigt, darunter nebeneinander zwei Cards: links der Explorer
 * und rechts der breitere Editor (Detail). Die Inhalte werden vom Aufrufer als Composable Functions
 * übergeben, dadurch kann das Layout für beliebige Applikationen verwendet werden.
 *
 * @param toolbar Composable Function für den Inhalt der Toolbar, z.B. Icons für 'Save', 'Create', 'Delete'. Standardmässig leer.
 * @param explorer Composable Function für den Explorer, der alle Daten-Items anzeigt.
 * @param editor Composable Function für den Editor, der das selektierte Daten-Item anzeigt.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MasterDetail(toolbar:  @Composable () -> Unit = {},
                 explorer: @Composable () -> Unit,
                 editor:   @Composable () -> Unit){
    val padding   = 20.dp
    val elevation = 2.dp

    Column {
        TopAppBar(title = { toolbar() },
                  colors = TopAppBarDefaults.topAppBarColors(Color.LightGray))

        Row(modifier = Modifier.fillMaxSize()
            .padding(padding)) {

            // see https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary#Card(androidx.compose.ui.Modifier,androidx.compose.ui.graphics.Shape,androidx.compose.material3.CardColors,androidx.compose.material3.CardElevation,androidx.compose.foundation.BorderStroke,kotlin.Function1)
            Card(elevation = CardDefaults.cardElevation(defaultElevation = elevation),
                 colors = CardDefaults.cardColors(containerColor = Color.White),
                 modifier = Modifier.weight(0.4f)
                     .fillMaxSize()) {
                explorer()
            }

            Spacer(Modifier.width(padding))

            Card(elevation = CardDefaults.cardElevation(defaultElevation = elevation),
                 colors = CardDefaults.cardColors(containerColor = Color.White),
                 modifier = Modifier.weight(1.0f)
                     .fillMaxSize()) {
                editor()
            }
        }
    }
}



/**
 * Funktion, die einen Platzhalter anzeigt, wenn nichts selektiert ist.
 *
 * Zeigt den [text] zentriert in grauer Schrift über einem stark abgeschwächten Hintergrundbild.
 * Wird im Editor anstelle des Formulars verwendet, solange im Explorer kein Daten-Item selektiert ist.
 *
 * @param text Der Hinweistext, z.B. "Skatepark auswählen".
 * @param backgroundImage Das Bild, das den gesamten Bereich als Hintergrund füllt.
 */
@Composable
fun NothingSelected(text: String, backgroundImage: ImageBitmap){
    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center) {
        Text(text = text,
             color = Color.Gray,
             fontSize = 32.sp)

        Image(bitmap = backgroundImage,
              contentDescription = "Default skatepark image",
              contentScale = ContentScale.Crop,
              alpha = 0.15f,
              modifier = Modifier.fillMaxSize()
             )
    }
}


/**
 * Funktion, die zwei Elemente gleich breit nebeneinander anordnet.
 *
 * Beide Spalten erhalten über den übergebenen Modifier die gleiche Breite, dazwischen liegt ein fester Abstand.
 * Wird z.B. im Formular verwendet, um zwei kurze Eingabefelder (Längengrad und Breitengrad) in einer Zeile anzuzeigen.
 *
 * @param left Composable Function für die linke Spalte. Der übergebene Modifier legt die Breite fest und muss verwendet werden.
 * @param right Composable Function für die rechte Spalte. Der übergebene Modifier legt die Breite fest und muss verwendet werden.
 */
@Composable
fun TwoColumnRow(left:  @Composable (Modifier) -> Unit, right:  @Composable (Modifier) -> Unit){
    Row(modifier = Modifier.fillMaxWidth()) {
        left(Modifier.weight(1.0f))
        Spacer(modifier = Modifier.width(60.dp))
        right(Modifier.weight(1.0f))
    }
}


/**
 * Funktion, die ein einzeiliges Eingabefeld mit Label anzeigt.
 *
 * Basiert auf [FormField] und verwendet ein OutlinedTextField als Control. Der angezeigte Wert kommt
 * aus dem Model, jede Eingabe wird über [onValueChange] an das Model weitergegeben.
 *
 * @param label Der Text, der links vom Eingabefeld angezeigt wird.
 * @param labelWidth Die Breite des Labels, damit die Eingabefelder untereinander bündig sind. Standardmässig 100.dp.
 * @param modifier Der Modifier für die gesamte Zeile.
 * @param value Der Wert, der im Eingabefeld angezeigt wird.
 * @param onValueChange Der Callback, der bei jeder Eingabe mit dem neuen Text aufgerufen wird, z.B. eine update-Funktion des Models.
 */
@Composable
fun FormTextField(label: String, labelWidth: Dp = 100.dp, modifier: Modifier = Modifier, value: String, onValueChange : (String) -> Unit){
    FormField(label      = label,
              labelWidth = labelWidth,
              modifier   = modifier,
              control    = { m ->
                  OutlinedTextField(value = value,
                                    onValueChange = { onValueChange(it) },
                                    singleLine = true,
                                    modifier = m)
              })
}


/**
 * Funktion, die eine Formularzeile mit Label und einem beliebigen Control anzeigt.
 *
 * Links steht das Label mit fester Breite, rechts das Control, das die restliche Breite einnimmt.
 * Kann für jede Art von Eingabe verwendet werden, z.B. Textfelder oder RadioButtons.
 *
 * @param label Der Text, der links vom Control angezeigt wird.
 * @param labelWidth Die Breite des Labels, damit die Controls untereinander bündig sind. Standardmässig 100.dp.
 * @param modifier Der Modifier für die gesamte Zeile.
 * @param control Composable Function für das Control. Der übergebene Modifier sorgt dafür, dass das Control die restliche Breite einnimmt.
 */
@Composable
fun FormField(label: String, labelWidth: Dp = 100.dp, modifier: Modifier = Modifier, control: @Composable (modifier: Modifier) -> Unit){
    Row(modifier = modifier.padding(vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically) {

        Text(text = label,
             modifier = Modifier.width(labelWidth))

        control(Modifier.weight(1.0f))
    }
}

/**
 * Schweizer Locale
 */
val CH: Locale = Locale.of("de", "CH")

// this is an Extension Function for any kind of Number.
// see https://kotlinlang.org/docs/extensions.html
/**
 * Formatiert eine Zahl für die Anzeige im UI.
 *
 * Die Formatierung verwendet den Schweizer Locale [CH]. Da im Model 'null' für 'Wert unbekannt' steht,
 * wird in diesem Fall stattdessen [nullFormat] angezeigt.
 *
 * @param pattern Das Format-Pattern, z.B. "%.1f" für eine Nachkommastelle.
 * @param nullFormat Der Text, der angezeigt wird, wenn die Zahl 'null' ist. Standardmässig ein leerer String.
 * @return Die formatierte Zahl oder [nullFormat].
 */
fun Number?.format(pattern: String, nullFormat: String = ""): String {
    return if (null == this) nullFormat else pattern.format(CH, this)
}

//other extension functions

/**
 * Bereitet einen nullable String für die Anzeige im UI vor.
 *
 * Wird z.B. für Eingabefelder verwendet, die keinen 'null'-Wert anzeigen können.
 *
 * @param nullFormat Der Text, der angezeigt wird, wenn der String 'null' ist. Standardmässig ein leerer String.
 * @return Der String selbst oder [nullFormat].
 */
fun String?.format(nullFormat: String = "") = this ?: nullFormat

/**
 * Zeigt über dem Element den Hand-Cursor an.
 *
 * Signalisiert dem Benutzer, dass ein Element anklickbar ist.
 *
 * @return Der Modifier mit dem Hand-Cursor.
 */
fun Modifier.handCursor() = cursor(Cursor.HAND_CURSOR)

/**
 * Legt fest, welcher Cursor angezeigt wird, wenn die Maus über dem Element ist.
 *
 * Extension Function von Modifier, es ist keine Subklasse von Modifier notwendig.
 *
 * @param cursorId Die Id des Cursors aus java.awt.Cursor, z.B. Cursor.HAND_CURSOR oder Cursor.DEFAULT_CURSOR.
 * @return Der Modifier mit dem gewünschten Cursor.
 */
fun Modifier.cursor(cursorId: Int) : Modifier = pointerHoverIcon(PointerIcon(Cursor(cursorId)))

