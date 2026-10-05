package skateparks

import androidx.compose.ui.window.application
import skateparks.view.SkateparkAdministrationWindow
import skateparks.viewmodel.FederalAdministration

fun main() {
    val model = FederalAdministration()
    model.sortSkateparksByZIPCode()
    model.loadAllImageBitmaps()

    application {
        SkateparkAdministrationWindow(model)
    }
}