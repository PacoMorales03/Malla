package devall.malla.ui.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val nodosAcento = setOf(0 to 0, 1 to 0, 0 to 1)

/** El isotipo de Malla: cuadrícula 3x3, con el nodo aprobado marcado en dorado. */
@Composable
fun MallaLogo(modifier: Modifier = Modifier, size: Dp = 32.dp) {
    val acento = MaterialTheme.colorScheme.secondary
    val fondo = MaterialTheme.colorScheme.primary
    val onFondo = MaterialTheme.colorScheme.onPrimary

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.22f))
            .background(fondo)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(size * 0.18f)
        ) {
            val celda = this.size.minDimension / 3f
            val radio = celda * 0.34f
            for (fila in 0..2) {
                for (columna in 0..2) {
                    val color = if ((columna to fila) in nodosAcento) acento else onFondo.copy(alpha = 0.4f)
                    drawCircle(
                        color = color,
                        radius = radio,
                        center = Offset((columna + 0.5f) * celda, (fila + 0.5f) * celda)
                    )
                }
            }
        }
    }
}
