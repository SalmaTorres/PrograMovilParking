import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.easypark.app.registerparking.presentation.state.RegisterParkingUIState

@Composable
actual fun ParkingMapSection(
    state: RegisterParkingUIState,
    onLocationChanged: (Double, Double) -> Unit) {
    Box(Modifier.fillMaxWidth().height(240.dp)) {
        Text("Mapa no disponible en iOS todavía", Modifier.align(Alignment.Center))
    }
}