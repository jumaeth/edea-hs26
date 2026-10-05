package skateparks.view

import java.awt.Cursor
import androidx.compose.foundation.Image
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import skateparks.viewmodel.FederalAdministration
import skateparks.viewmodel.Skatepark
import skateparks.viewmodel.SkateparkState


/**
 * Funktion, die das Hauptfenster der Skatepark-Administration anzeigt.
 *
 * Öffnet ein zentriertes Fenster mit dem Titel der [institution] und setzt ein graues
 * Farbschema. Beim Schliessen des Fensters wird die gesamte Applikation beendet.
 * Wird in 'main' innerhalb von 'application' aufgerufen.
 *
 * @receiver Der ApplicationScope, über den die Applikation beim Schliessen beendet wird.
 * @param institution Das Model mit allen Skateparks, das im Fenster angezeigt und bearbeitet wird.
 */
@Composable
fun ApplicationScope.SkateparkAdministrationWindow(institution: FederalAdministration){
    Window(title          = institution.title,
           onCloseRequest = ::exitApplication,
           state          =  rememberWindowState(width = 1000.dp,
                                                 height = 700.dp,
                                                 position = WindowPosition(Alignment.Center))){
        MaterialTheme(colorScheme = MaterialTheme.colorScheme.copy(primary                 = Color(150, 150, 150),
                                                                   surface                 = Color(248, 248, 248),
                                                                   surfaceContainerHighest = Color(230, 230, 230)
                                                                  )) {
            TheUI(institution)
        }

    }
}

/**
 * Funktion, die den gesamten Inhalt des Fensters anzeigt.
 *
 * Verwendet das [MasterDetail]-Layout mit der Toolbar, dem Explorer aller Skateparks und dem Editor
 * für den aktuell selektierten Skatepark.
 *
 * @param institution Das Model, aus dem die Daten für Toolbar, Explorer und Editor kommen.
 */
@Composable
private fun TheUI(institution: FederalAdministration) {
    MasterDetail(toolbar  = { Toolbar(institution) },
                 explorer = { Explorer(institution) },
                 editor   = { Editor(institution.skateparkUnderControl) }
                )

}

/**
 * Funktion, die die Toolbar mit den Icons 'Save', 'Create' und 'Delete' anzeigt.
 *
 * Jedes Icon ruft die entsprechende Funktion des Models auf. 'Delete' ist nur aktiv,
 * wenn ein Skatepark selektiert ist.
 *
 * @param institution Das Model, dessen Funktionen 'save', 'create' und 'delete' aufgerufen werden.
 */
@Composable
private fun Toolbar(institution: FederalAdministration){
    with(institution){
        Row(verticalAlignment = Alignment.CenterVertically) {
            ToolbarIcon(image       = Icons.Filled.Save,
                        description = "Save",
                        onClick     = { save() })

            ToolbarIcon(image       = Icons.Filled.AddCircle,
                        description = "Create",
                        onClick     = { create() })

            ToolbarIcon(image       = Icons.Filled.RemoveCircle,
                        description = "Delete",
                        enabled     = deleteEnabled,
                        onClick     = { delete() })
        }
    }
}

/**
 * Funktion, die ein einzelnes anklickbares Icon in der Toolbar anzeigt.
 *
 * Ist das Icon aktiv, wird beim Überfahren mit der Maus der Hand-Cursor angezeigt, sonst der normale Cursor.
 *
 * @param image Das Icon, das angezeigt wird, z.B. Icons.Filled.Save.
 * @param description Die Beschreibung des Icons für die Barrierefreiheit, z.B. "Save".
 * @param enabled Gibt an, ob das Icon anklickbar ist. Standardmässig true.
 * @param onClick Der Callback, der beim Klick auf das Icon aufgerufen wird.
 */
@Composable
private fun ToolbarIcon(image: ImageVector, description: String, enabled: Boolean = true, onClick: () -> Unit){
    IconButton(onClick  = onClick,
               enabled  = enabled,
               modifier = Modifier.cursor(if (enabled) Cursor.HAND_CURSOR else Cursor.DEFAULT_CURSOR)) {
        Icon(imageVector        = image,
             contentDescription = description)
    }
}

/**
 * Funktion, die alle Skateparks als scrollbare Liste anzeigt.
 *
 * Verwendet den [GenericExplorer] mit der 'id' als Key und dem ScrollState aus dem Model.
 * Jeder Skatepark wird als [SkateparkItem] dargestellt, ein Klick darauf selektiert ihn.
 *
 * @param institution Das Model mit der Liste aller Skateparks und dem selektierten Skatepark.
 */
@Composable
private fun Explorer(institution: FederalAdministration){
    with(institution){
        GenericExplorer(data        = allSkateparks,
                        key         = { it.id },
                        scrollState = explorerScrollState,
                        listItem    = { skatepark -> SkateparkItem(skatepark  = skatepark,
                                                                   isSelected = isSelected(skatepark),
                                                                   onClick    = { updateSkateparkUnderControl(skatepark) })
                                      })
    }
}

/**
 * Funktion, die einen einzelnen Skatepark als Eintrag im Explorer anzeigt.
 *
 * Zeigt das Rating, den Namen, PLZ und Ort sowie ein rundes Thumbnail. Der selektierte Eintrag wird
 * grau hinterlegt, unter jedem Eintrag steht eine Trennlinie.
 *
 * @param skatepark Der Skatepark, der angezeigt wird.
 * @param isSelected Gibt an, ob der Skatepark aktuell selektiert ist.
 * @param onClick Der Callback, der beim Klick auf den Eintrag aufgerufen wird, z.B. um ihn zu selektieren.
 */
@Composable
private fun SkateparkItem(skatepark: Skatepark, isSelected: Boolean, onClick: () -> Unit){
    with(skatepark){
        ListItem(overlineContent   = { Text(text = "Rating: ${avgRating.format("%.1f", "-")}") },
                 headlineContent   = { Text(text     = name.format(),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis) },
                 supportingContent = { Text(text = zipPlace.format()) },
                 leadingContent    = { Thumbnail(image = imageBitmap, description = name.format()) },
                 colors            = ListItemDefaults.colors(containerColor = if (isSelected) Color.LightGray else Color.White),
                 modifier          = Modifier.clickable(onClick = onClick)
                                             .handCursor())
        HorizontalDivider()
    }
}

/**
 * Funktion, die ein Bild als kleines, rundes Thumbnail anzeigt.
 *
 * Das Bild wird auf 56 dp zugeschnitten und kreisförmig dargestellt. Wird im Explorer verwendet.
 *
 * @param image Das Bild, das angezeigt wird.
 * @param description Die Beschreibung des Bildes für die Barrierefreiheit, z.B. der Name des Skateparks.
 */
@Composable
private fun Thumbnail(image: ImageBitmap, description: String){
    Image(bitmap             = image,
          contentDescription = description,
          contentScale       = ContentScale.Crop,
          modifier           = Modifier.size(56.dp)
                                       .clip(CircleShape))
}

/**
 * Funktion, die den Editor für den selektierten Skatepark anzeigt.
 *
 * Ist kein Skatepark selektiert, wird [NothingSelected] angezeigt. Sonst werden der [Header]
 * und darunter das scrollbare Formular angezeigt.
 *
 * @param skatepark Der selektierte Skatepark oder 'null', wenn nichts selektiert ist.
 */
@Composable
private fun Editor(skatepark: Skatepark?){
    if (null == skatepark) {
        NothingSelected(text            = "Skatepark auswählen",
                        backgroundImage = Skatepark.defaultImageBitmap)
    } else {
        Column(modifier = Modifier.fillMaxSize()
                                  .padding(20.dp)) {
            Header(skatepark)
            VSpace(height = 20)
            DetailsBox(skatepark)
        }
    }
}

/**
 * Funktion, die den Kopfbereich des Editors anzeigt.
 *
 * Links stehen der Name, die vollständige Adresse und das Rating, rechts das Bild des Skateparks.
 * Der Bereich hat einen grauen Hintergrund mit abgerundeten Ecken.
 *
 * @param skatepark Der Skatepark, dessen Daten angezeigt werden.
 */
@Composable
private fun Header(skatepark: Skatepark){
    with(skatepark){
        Row(modifier          = Modifier.fillMaxWidth()
                                        .background(color = Color(240, 240, 240),
                                                    shape = RoundedCornerShape(12.dp))
                                        .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically) {

            Column(modifier = Modifier.weight(1.0f)) {
                Text(text     = name.format(),
                     fontSize = 28.sp,
                     maxLines = 1,
                     overflow = TextOverflow.Ellipsis)
                Text(text     = fulladdress.format(),
                     fontSize = 18.sp)
                Text(text     = "Rating: ${avgRating.format("%.1f", "-")}",
                     fontSize = 18.sp)
            }

            HSpace(width = 20)

            Image(bitmap             = imageBitmap,
                  contentDescription = name.format(),
                  contentScale       = ContentScale.Crop,
                  modifier           = Modifier.size(width = 220.dp, height = 130.dp)
                                               .clip(RoundedCornerShape(8.dp)))
        }
    }
}

/**
 * Funktion, die das Formular des Editors scrollbar macht.
 *
 * Zeigt das [DetailsForm] mit einer vertikalen Scrollbar am rechten Rand, damit das Formular
 * auch bei kleinem Fenster vollständig erreichbar ist.
 *
 * @param skatepark Der Skatepark, der im Formular bearbeitet wird.
 */
@Composable
private fun DetailsBox(skatepark: Skatepark){
    Box(modifier = Modifier.fillMaxSize()) {
        val verticalScrollState = rememberScrollState()

        DetailsForm(skatepark = skatepark,
                    modifier  = Modifier.verticalScroll(verticalScrollState)
                                        .padding(end = 20.dp))

        VerticalScrollbar(adapter  = rememberScrollbarAdapter(verticalScrollState),
                          modifier = Modifier.align(Alignment.CenterEnd)
                                             .fillMaxHeight())
    }
}

/**
 * Funktion, die das Formular zum Bearbeiten eines Skateparks anzeigt.
 *
 * Enthält den Status sowie Eingabefelder für Name, Strasse, PLZ Ort, Längen- und Breitengrad.
 * Jede Eingabe wird über die update-Funktionen direkt an den Skatepark weitergegeben.
 *
 * @param skatepark Der Skatepark, der bearbeitet wird.
 * @param modifier Der Modifier für das gesamte Formular, z.B. um es scrollbar zu machen.
 */
@Composable
private fun DetailsForm(skatepark: Skatepark, modifier: Modifier){
    with(skatepark){
        Column(modifier = modifier) {
            StatusField(skatepark)

            FormTextField(label         = "Name",
                          value         = name.format(),
                          onValueChange = { updateName(it) })

            FormTextField(label         = "Strasse",
                          value         = street.format(),
                          onValueChange = { updateStreet(it) })

            FormTextField(label         = "PLZ Ort",
                          value         = zipPlace.format(),
                          onValueChange = { updateZipPlace(it) })

            TwoColumnRow(left  = { m -> FormTextField(label         = "Längengrad",
                                                      modifier      = m,
                                                      value         = longitude.format("%s"),
                                                      onValueChange = { updateLongitude(it) })
                                 },
                         right = { m -> FormTextField(label         = "Breitengrad",
                                                      modifier      = m,
                                                      value         = latitude.format("%s"),
                                                      onValueChange = { updateLatitude(it) })
                                 })
        }
    }
}

/**
 * Funktion, die den Status des Skateparks als Gruppe von RadioButtons anzeigt.
 *
 * Für jeden möglichen [SkateparkState] wird eine [StatusOption] angezeigt. Beim Klick auf eine
 * Option wird der Status des Skateparks aktualisiert.
 *
 * @param skatepark Der Skatepark, dessen Status angezeigt und geändert wird.
 */
@Composable
private fun StatusField(skatepark: Skatepark){
    with(skatepark){
        FormField(label   = "Status",
                  control = { m ->
                      Row(modifier              = m,
                          horizontalArrangement = Arrangement.SpaceBetween) {
                          SkateparkState.entries.forEach { state ->
                              StatusOption(text       = state.description,
                                           isSelected = status == state,
                                           onClick    = { updateStatus(state.description) })
                          }
                      }
                  })
    }
}

/**
 * Funktion, die eine einzelne Option als RadioButton mit Text anzeigt.
 *
 * Sowohl der RadioButton als auch der Text sind anklickbar, beim Überfahren wird der Hand-Cursor angezeigt.
 *
 * @param text Der Text, der neben dem RadioButton angezeigt wird.
 * @param isSelected Gibt an, ob die Option aktuell ausgewählt ist.
 * @param onClick Der Callback, der beim Klick auf die Option aufgerufen wird.
 */
@Composable
private fun StatusOption(text: String, isSelected: Boolean, onClick: () -> Unit){
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier          = Modifier.clickable(onClick = onClick)
                                    .handCursor()) {
        RadioButton(selected = isSelected,
                    onClick  = onClick)
        Text(text = text)
    }
}

/**
 * Funktion, die einen horizontalen Abstand einfügt.
 *
 * @param width Die Breite des Abstands in dp.
 */
@Composable
private fun HSpace(width: Int) = Spacer(modifier = Modifier.width(width.dp))

/**
 * Funktion, die einen vertikalen Abstand einfügt.
 *
 * @param height Die Höhe des Abstands in dp.
 */
@Composable
private fun VSpace(height: Int) = Spacer(modifier = Modifier.height(height = height.dp))