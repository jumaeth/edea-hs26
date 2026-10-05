package skateparks.view

import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import skateparks.viewmodel.FederalAdministration
import skateparks.viewmodel.Skatepark


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

//todo: Vervollständigen Sie das UI

@Composable
private fun Toolbar(institution: FederalAdministration){
    Text("Toolbar mit Icons für 'Save', 'Create', 'Delete'")
}

@Composable
private fun Explorer(institution: FederalAdministration){
    Text("""
        Hier kommt der Explorer hin.
        """.trimIndent())
}

@Composable
private fun Editor(skatepark: Skatepark?){
    Text("""
        Hier kommt der Editor hin
        Mit Header und Formular
        Header enthält mindestens 'name', 'fulladdress', 'imageBitmap'
        Formular enthält mindestens 'status', 'name', 'street', 'zipPlace', 'latitude', 'longitude'
        """.trimIndent())
}