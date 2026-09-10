package devall.malla.ui.planificacion

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import devall.malla.data.Asignatura
import devall.malla.data.BloqueHorario
import devall.malla.data.TipoBloque
import devall.malla.ui.common.SelectorDesplegable
import devall.malla.ui.theme.LocalBloqueColores
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogoBloque(
    fechaInicial: LocalDate,
    horaInicialMinutos: Int,
    bloqueExistente: BloqueHorario?,
    asignaturasEstudiando: List<Asignatura>,
    onConfirmar: (BloqueHorario) -> Unit,
    onEliminar: (() -> Unit)?,
    onCancelar: () -> Unit
) {
    val bloqueColores = LocalBloqueColores.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var tipo by remember { mutableStateOf(bloqueExistente?.tipo ?: TipoBloque.ESTUDIO_PLANIFICADO) }
    var fecha by remember { mutableStateOf(bloqueExistente?.fechaInicio ?: fechaInicial) }
    var asignaturaId by remember {
        mutableStateOf(bloqueExistente?.asignaturaId ?: asignaturasEstudiando.firstOrNull()?.id)
    }
    var nombreLibre by remember { mutableStateOf(bloqueExistente?.nombre ?: "") }
    var horaInicio by remember { mutableStateOf(bloqueExistente?.horaInicioMinutos ?: horaInicialMinutos) }
    var horaFin by remember {
        mutableStateOf(bloqueExistente?.horaFinMinutos ?: (horaInicialMinutos + 60).coerceAtMost(HORA_FIN_AGENDA))
    }
    var repetir by remember { mutableStateOf(bloqueExistente?.let { it.fechaFin != it.fechaInicio } ?: false) }
    var fechaFinRepeticion by remember {
        mutableStateOf(
            if (bloqueExistente != null && bloqueExistente.fechaFin != bloqueExistente.fechaInicio) {
                bloqueExistente.fechaFin
            } else {
                fecha.plusWeeks(8)
            }
        )
    }
    var confirmarEliminar by remember { mutableStateOf(false) }

    val esEstudio = tipo == TipoBloque.ESTUDIO_PLANIFICADO || tipo == TipoBloque.ESTUDIO_REAL
    val esPersonal = tipo == TipoBloque.PERSONAL
    val asignaturaSeleccionada = asignaturasEstudiando.firstOrNull { it.id == asignaturaId }
    val opcionesHoraInicio = opcionesHora().dropLast(1)
    val opcionesHoraFin = opcionesHora().filter { it > horaInicio }

    val valido = horaFin > horaInicio &&
        (!repetir || !fechaFinRepeticion.isBefore(fecha)) &&
        when {
            esEstudio -> asignaturaSeleccionada != null
            esPersonal -> nombreLibre.isNotBlank()
            else -> true
        }

    ModalBottomSheet(onDismissRequest = onCancelar, sheetState = sheetState) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                if (bloqueExistente == null) "Nuevo bloque" else "Editar bloque",
                style = MaterialTheme.typography.titleLarge
            )

            SelectorDesplegable(
                etiqueta = "Tipo",
                opciones = TipoBloque.entries,
                seleccionado = tipo,
                textoDe = ::etiquetaTipoBloque,
                colorDe = { bloqueColores.de(it) },
                onSeleccionar = { tipo = it }
            )

            when {
                esEstudio -> {
                    if (asignaturasEstudiando.isEmpty()) {
                        Text(
                            "No tienes ninguna asignatura en estado \"Estudiando\". Márcala así desde Cursos primero.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        SelectorDesplegable(
                            etiqueta = "Asignatura",
                            opciones = asignaturasEstudiando,
                            seleccionado = asignaturaSeleccionada ?: asignaturasEstudiando.first(),
                            textoDe = { it.nombre },
                            onSeleccionar = { asignaturaId = it.id }
                        )
                    }
                }
                esPersonal -> {
                    OutlinedTextField(
                        value = nombreLibre,
                        onValueChange = { nombreLibre = it },
                        label = { Text("Nombre, p. ej. Gimnasio") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                else -> Unit // Trabajo no necesita más datos, se etiqueta siempre igual
            }

            CampoFecha(
                etiqueta = "Fecha",
                fecha = fecha,
                onFechaElegida = {
                    fecha = it
                    if (fechaFinRepeticion.isBefore(it)) fechaFinRepeticion = it.plusWeeks(8)
                }
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SelectorDesplegable(
                    etiqueta = "Inicio",
                    opciones = opcionesHoraInicio,
                    seleccionado = horaInicio,
                    textoDe = ::formatoHora,
                    onSeleccionar = {
                        horaInicio = it
                        if (horaFin <= it) horaFin = (it + 30).coerceAtMost(HORA_FIN_AGENDA)
                    },
                    modifier = Modifier.weight(1f)
                )
                SelectorDesplegable(
                    etiqueta = "Fin",
                    opciones = opcionesHoraFin,
                    seleccionado = horaFin.coerceAtLeast(opcionesHoraFin.firstOrNull() ?: horaFin),
                    textoDe = ::formatoHora,
                    onSeleccionar = { horaFin = it },
                    modifier = Modifier.weight(1f)
                )
            }

            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Repetir semanalmente", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Todos los ${nombreDia(fecha.dayOfWeek.value).lowercase()} a la misma hora",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(checked = repetir, onCheckedChange = { repetir = it })
            }

            if (repetir) {
                CampoFecha(
                    etiqueta = "Repetir hasta",
                    fecha = fechaFinRepeticion,
                    onFechaElegida = { fechaFinRepeticion = it }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (onEliminar != null) {
                    TextButton(onClick = { confirmarEliminar = true }) { Text("Eliminar") }
                } else {
                    Row {}
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    TextButton(onClick = onCancelar) { Text("Cancelar") }
                    TextButton(
                        enabled = valido,
                        onClick = {
                            onConfirmar(
                                BloqueHorario(
                                    id = bloqueExistente?.id ?: 0,
                                    tipo = tipo,
                                    asignaturaId = if (esEstudio) asignaturaId else null,
                                    nombre = when {
                                        esPersonal -> nombreLibre.trim()
                                        tipo == TipoBloque.TRABAJO -> "Trabajo"
                                        else -> null
                                    },
                                    diaSemana = fecha.dayOfWeek.value,
                                    horaInicioMinutos = horaInicio,
                                    horaFinMinutos = horaFin,
                                    fechaInicio = fecha,
                                    fechaFin = if (repetir) fechaFinRepeticion else fecha
                                )
                            )
                        }
                    ) { Text("Guardar") }
                }
            }
        }
    }

    if (confirmarEliminar) {
        AlertDialog(
            onDismissRequest = { confirmarEliminar = false },
            title = { Text("Eliminar bloque") },
            text = { Text("Si se repite semanalmente, se eliminan todas sus ocurrencias.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmarEliminar = false
                    onEliminar?.invoke()
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { confirmarEliminar = false }) { Text("Cancelar") }
            }
        )
    }
}
