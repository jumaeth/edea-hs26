package skateparks.view

import java.awt.Cursor
import java.util.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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


// todo: Implement GenericExplorer
// eine generische Funktion, die eine Liste von 'items' als Parameter entgegennimmt und als Tabelle darstellt.
// sinnvolle weitere Parameter:
// - lambda, das den Key eines Daten-Items zurückgibt
// - composable function, die ein Daten-Item geeignet visualisiert
// - den ScrollState
// - sind noch weitere Parameter sinnvoll?

/*
 @Composable
fun<T> GenericExplorer(data: ..,
                       key: ..,
                       scrollState: LazyListState,
                       listItem: @Composable (T) -> Unit) {
                       ....
                       }

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


@Composable
fun TwoColumnRow(left:  @Composable (Modifier) -> Unit, right:  @Composable (Modifier) -> Unit){
    Row(modifier = Modifier.fillMaxWidth()) {
        left(Modifier.weight(1.0f))
        Spacer(modifier = Modifier.width(60.dp))
        right(Modifier.weight(1.0f))
    }
}


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


@Composable
fun FormField(label: String, labelWidth: Dp = 100.dp, modifier: Modifier = Modifier, control: @Composable (modifier: Modifier) -> Unit){
    Row(modifier = modifier.padding(vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically) {

        Text(text = label,
             modifier = Modifier.width(labelWidth))

        control(Modifier.weight(1.0f))
    }
}

val CH: Locale = Locale.of("de", "CH")

// this is an Extension Function for any kind of Number.
// see https://kotlinlang.org/docs/extensions.html
fun Number?.format(pattern: String, nullFormat: String = ""): String {
    return if (null == this) nullFormat else pattern.format(CH, this)
}

//other extension functions

fun String?.format(nullFormat: String = "") = this ?: nullFormat

fun Modifier.handCursor() = cursor(Cursor.HAND_CURSOR)

fun Modifier.cursor(cursorId: Int) : Modifier = pointerHoverIcon(PointerIcon(Cursor(cursorId)))

