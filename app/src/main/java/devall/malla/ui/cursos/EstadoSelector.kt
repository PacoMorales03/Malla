package devall.malla.ui.cursos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import devall.malla.data.EstadoAsignatura
import devall.malla.ui.common.SelectorDesplegable
import devall.malla.ui.theme.LocalEstadoColores

private val estados = listOf(
    EstadoAsignatura.VACIO,
    EstadoAsignatura.MATRICULADA,
    EstadoAsignatura.ESTUDIANDO,
    EstadoAsignatura.APROBADA
)

fun etiquetaDeEstado(estado: EstadoAsignatura): String = when (estado) {
    EstadoAsignatura.VACIO -> "Vacío"
    EstadoAsignatura.MATRICULADA -> "Matriculada"
    EstadoAsignatura.ESTUDIANDO -> "Estudiando"
    EstadoAsignatura.APROBADA -> "Aprobada"
}

@Composable
fun EstadoSelector(
    estadoActual: EstadoAsignatura,
    onEstadoElegido: (EstadoAsignatura) -> Unit,
    modifier: Modifier = Modifier
) {
    val colores = LocalEstadoColores.current
    SelectorDesplegable(
        etiqueta = "Estado",
        opciones = estados,
        seleccionado = estadoActual,
        textoDe = ::etiquetaDeEstado,
        colorDe = { colores.de(it) },
        onSeleccionar = onEstadoElegido,
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun ConvocatoriasStepper(
    convocatorias: Int,
    onCambiar: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Convocatorias gastadas",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OutlinedIconButton(
                onClick = { if (convocatorias > 0) onCambiar(convocatorias - 1) },
                modifier = Modifier.size(30.dp)
            ) {
                Icon(Icons.Filled.Remove, contentDescription = "Restar convocatoria")
            }
            Text(
                text = convocatorias.toString(),
                style = MaterialTheme.typography.titleMedium
            )
            OutlinedIconButton(
                onClick = { onCambiar(convocatorias + 1) },
                modifier = Modifier.size(30.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Sumar convocatoria")
            }
        }
    }
}
