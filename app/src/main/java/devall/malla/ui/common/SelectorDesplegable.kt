package devall.malla.ui.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * Select desplegable de una sola opción, al estilo "spinner" nativo de Android
 * pero con Material3. Genérico para no repetir el mismo ExposedDropdownMenuBox
 * en estado, día, tipo de bloque, asignatura y hora.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SelectorDesplegable(
    etiqueta: String,
    opciones: List<T>,
    seleccionado: T,
    textoDe: (T) -> String,
    onSeleccionar: (T) -> Unit,
    modifier: Modifier = Modifier,
    colorDe: ((T) -> Color)? = null
) {
    var expandido by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expandido,
        onExpandedChange = { expandido = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = textoDe(seleccionado),
            onValueChange = {},
            readOnly = true,
            label = { Text(etiqueta) },
            leadingIcon = colorDe?.let { obtenerColor ->
                { ColorDot(obtenerColor(seleccionado)) }
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        DropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false },
            modifier = Modifier.exposedDropdownSize()
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(textoDe(opcion)) },
                    leadingIcon = colorDe?.let { obtenerColor ->
                        { ColorDot(obtenerColor(opcion)) }
                    },
                    onClick = {
                        onSeleccionar(opcion)
                        expandido = false
                    }
                )
            }
        }
    }
}
