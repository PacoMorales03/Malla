package devall.malla.ui.planificacion

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

private val formateadorFechaLarga = DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", Locale("es", "ES"))

fun formatoFechaLarga(fecha: LocalDate): String =
    formateadorFechaLarga.format(fecha).replaceFirstChar { it.uppercase() }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoFecha(
    etiqueta: String,
    fecha: LocalDate,
    onFechaElegida: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    var mostrarSelector by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = formatoFechaLarga(fecha),
            onValueChange = {},
            readOnly = true,
            label = { Text(etiqueta) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        // Capa transparente encima: el TextField es de solo lectura, así que
        // el toque abre siempre el selector en vez de intentar editar el texto.
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { mostrarSelector = true }
        )
    }

    if (mostrarSelector) {
        val estado = rememberDatePickerState(
            initialSelectedDateMillis = fecha.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { mostrarSelector = false },
            confirmButton = {
                TextButton(onClick = {
                    estado.selectedDateMillis?.let { millis ->
                        onFechaElegida(Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate())
                    }
                    mostrarSelector = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarSelector = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = estado)
        }
    }
}
