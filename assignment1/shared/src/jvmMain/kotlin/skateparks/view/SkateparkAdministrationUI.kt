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

@Composable
private fun TheUI(institution: FederalAdministration) {
    MasterDetail(toolbar  = { Toolbar(institution) },
                 explorer = { Explorer(institution) },
                 editor   = { Editor(institution.skateparkUnderControl) }
                )

}

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

@Composable
private fun ToolbarIcon(image: ImageVector, description: String, enabled: Boolean = true, onClick: () -> Unit){
    IconButton(onClick  = onClick,
               enabled  = enabled,
               modifier = Modifier.cursor(if (enabled) Cursor.HAND_CURSOR else Cursor.DEFAULT_CURSOR)) {
        Icon(imageVector        = image,
             contentDescription = description)
    }
}

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

@Composable
private fun Thumbnail(image: ImageBitmap, description: String){
    Image(bitmap             = image,
          contentDescription = description,
          contentScale       = ContentScale.Crop,
          modifier           = Modifier.size(56.dp)
                                       .clip(CircleShape))
}

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

@Composable
private fun HSpace(width: Int) = Spacer(modifier = Modifier.width(width.dp))

@Composable
private fun VSpace(height: Int) = Spacer(modifier = Modifier.height(height = height.dp))