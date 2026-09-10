package devall.malla

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import devall.malla.ui.nav.MallaNavHost
import devall.malla.ui.theme.MallaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MallaTheme {
                MallaNavHost()
            }
        }
    }
}
