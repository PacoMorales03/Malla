package devall.malla.ui.cursos

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun DialogoCurso(
    nombreInicial: String = "",
    creditosInicial: String = "",
    onConfirmar: (nombre: String, creditos: Double) -> Unit,
    onCancelar: () -> Unit
) {
    var nombre by remember { mutableStateOf(nombreInicial) }
    var creditos by remember { mutableStateOf(creditosInicial) }
    val creditosValidos = creditos.toDoubleOrNull() != null
    val titulo = if (nombreInicial.isEmpty()) "Nuevo curso" else "Editar curso"

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(titulo) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre, p. ej. 1º Curso") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = creditos,
                    onValueChange = { creditos = it },
                    label = { Text("Créditos del curso") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = nombre.isNotBlank() && creditosValidos,
                onClick = { onConfirmar(nombre.trim(), creditos.toDouble()) }
            ) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancelar") }
        }
    )
}

@Composable
fun DialogoAsignatura(
    nombreInicial: String = "",
    creditosInicial: String = "",
    onConfirmar: (nombre: String, creditos: Double) -> Unit,
    onCancelar: () -> Unit
) {
    var nombre by remember { mutableStateOf(nombreInicial) }
    var creditos by remember { mutableStateOf(creditosInicial) }
    val creditosValidos = creditos.toDoubleOrNull() != null
    val titulo = if (nombreInicial.isEmpty()) "Nueva asignatura" else "Editar asignatura"

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(titulo) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre de la asignatura") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = creditos,
                    onValueChange = { creditos = it },
                    label = { Text("Créditos") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = nombre.isNotBlank() && creditosValidos,
                onClick = { onConfirmar(nombre.trim(), creditos.toDouble()) }
            ) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancelar") }
        }
    )
}

@Composable
fun DialogoCreditosCarrera(
    valorInicial: String = "",
    onConfirmar: (Double) -> Unit,
    onCancelar: (() -> Unit)?
) {
    var creditos by remember { mutableStateOf(valorInicial) }
    val valido = creditos.toDoubleOrNull() != null

    AlertDialog(
        onDismissRequest = { onCancelar?.invoke() },
        title = { Text("Créditos totales de la carrera") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Se usan para calcular tu % de progreso total. Puedes cambiarlos más adelante.")
                OutlinedTextField(
                    value = creditos,
                    onValueChange = { creditos = it },
                    label = { Text("Créditos totales") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = valido,
                onClick = { onConfirmar(creditos.toDouble()) }
            ) { Text("Guardar") }
        },
        dismissButton = {
            if (onCancelar != null) {
                TextButton(onClick = onCancelar) { Text("Cancelar") }
            }
        }
    )
}
