package devall.malla.ui.cursos

import android.app.Application
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import devall.malla.data.Asignatura
import devall.malla.data.EstadoAsignatura
import devall.malla.data.formatCreditos
import devall.malla.data.ordenadasAprobadas
import devall.malla.data.ordenadasNoAprobadas
import kotlin.math.roundToInt

@Composable
fun CursoDetalleScreen(
    cursoId: Long,
    onVolver: () -> Unit,
    viewModel: CursoDetalleViewModel = viewModel(
        factory = CursoDetalleViewModel.Factory(
            LocalContext.current.applicationContext as Application,
            cursoId
        )
    )
) {
    val curso by viewModel.curso.collectAsState()
    val asignaturas by viewModel.asignaturas.collectAsState()

    var mostrarNuevaAsignatura by remember { mutableStateOf(false) }
    var asignaturaEnEdicion by remember { mutableStateOf<Asignatura?>(null) }
    var asignaturaAEliminar by remember { mutableStateOf<Asignatura?>(null) }
    var asignaturaPendienteNota by remember { mutableStateOf<Asignatura?>(null) }
    var asignaturaNotaAEditar by remember { mutableStateOf<Asignatura?>(null) }
    var mostrarEditarCurso by remember { mutableStateOf(false) }
    var menuCursoAbierto by remember { mutableStateOf(false) }
    var confirmarEliminarCurso by remember { mutableStateOf(false) }
    var pestanaSeleccionada by remember { mutableStateOf(0) }

    val cursoActual = curso ?: return

    val noAprobadas = remember(asignaturas) {
        asignaturas.filter { it.estado != EstadoAsignatura.APROBADA }.ordenadasNoAprobadas()
    }
    val aprobadas = remember(asignaturas) {
        asignaturas.filter { it.estado == EstadoAsignatura.APROBADA }.ordenadasAprobadas()
    }
    val mostrarPestanas = noAprobadas.isNotEmpty() && aprobadas.isNotEmpty()
    val pestanaEfectiva = if (mostrarPestanas) pestanaSeleccionada else if (aprobadas.isNotEmpty()) 1 else 0
    val listaVisible = if (pestanaEfectiva == 1) aprobadas else noAprobadas

    val creditosAprobados = asignaturas
        .filter { it.estado == EstadoAsignatura.APROBADA }
        .sumOf { it.creditos }
    val porcentaje = if (cursoActual.creditosCurso <= 0.0) 0f
        else (creditosAprobados / cursoActual.creditosCurso).toFloat().coerceIn(0f, 1f)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onVolver) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                        }
                        Text(cursoActual.nombre, style = MaterialTheme.typography.headlineSmall)
                    }
                    Box {
                        IconButton(onClick = { menuCursoAbierto = true }) {
                            Icon(Icons.Filled.MoreVert, contentDescription = "Más opciones")
                        }
                        DropdownMenu(expanded = menuCursoAbierto, onDismissRequest = { menuCursoAbierto = false }) {
                            DropdownMenuItem(text = { Text("Editar curso") }, onClick = {
                                menuCursoAbierto = false
                                mostrarEditarCurso = true
                            })
                            DropdownMenuItem(text = { Text("Eliminar curso") }, onClick = {
                                menuCursoAbierto = false
                                confirmarEliminarCurso = true
                            })
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "${formatCreditos(creditosAprobados)} / ${formatCreditos(cursoActual.creditosCurso)} créditos",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "${(porcentaje * 100).roundToInt()}%",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                LinearProgressIndicator(
                    progress = { porcentaje },
                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp)
                )
            }

            if (mostrarPestanas) {
                TabRow(selectedTabIndex = pestanaSeleccionada) {
                    Tab(
                        selected = pestanaSeleccionada == 0,
                        onClick = { pestanaSeleccionada = 0 },
                        text = { Text("No aprobadas (${noAprobadas.size})") }
                    )
                    Tab(
                        selected = pestanaSeleccionada == 1,
                        onClick = { pestanaSeleccionada = 1 },
                        text = { Text("Aprobadas (${aprobadas.size})") }
                    )
                }
            }

            if (asignaturas.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text(
                        "Este curso no tiene asignaturas todavía.\nToca + para añadir la primera.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(listaVisible, key = { it.id }) { asignatura ->
                        AsignaturaCard(
                            asignatura = asignatura,
                            onCambiarEstado = { nuevoEstado ->
                                if (nuevoEstado == EstadoAsignatura.APROBADA) {
                                    asignaturaPendienteNota = asignatura
                                } else {
                                    viewModel.actualizarAsignatura(
                                        asignatura.copy(estado = nuevoEstado, nota = null)
                                    )
                                }
                            },
                            onCambiarConvocatorias = { nuevoValor ->
                                viewModel.actualizarAsignatura(asignatura.copy(convocatoriasGastadas = nuevoValor))
                            },
                            onEditar = { asignaturaEnEdicion = asignatura },
                            onEliminar = { asignaturaAEliminar = asignatura },
                            onEditarNota = { asignaturaNotaAEditar = asignatura }
                        )
                    }
                    item { Box(modifier = Modifier.padding(bottom = 88.dp)) }
                }
            }
        }

        FloatingActionButton(
            onClick = { mostrarNuevaAsignatura = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Nueva asignatura")
        }
    }

    if (mostrarNuevaAsignatura) {
        DialogoAsignatura(
            onConfirmar = { nombre, creditos ->
                viewModel.crearAsignatura(nombre, creditos)
                mostrarNuevaAsignatura = false
            },
            onCancelar = { mostrarNuevaAsignatura = false }
        )
    }

    asignaturaEnEdicion?.let { asignatura ->
        DialogoAsignatura(
            nombreInicial = asignatura.nombre,
            creditosInicial = asignatura.creditos.toString(),
            onConfirmar = { nombre, creditos ->
                viewModel.actualizarAsignatura(asignatura.copy(nombre = nombre, creditos = creditos))
                asignaturaEnEdicion = null
            },
            onCancelar = { asignaturaEnEdicion = null }
        )
    }

    asignaturaAEliminar?.let { asignatura ->
        AlertDialog(
            onDismissRequest = { asignaturaAEliminar = null },
            title = { Text("Eliminar ${asignatura.nombre}") },
            text = { Text("Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.eliminarAsignatura(asignatura)
                    asignaturaAEliminar = null
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { asignaturaAEliminar = null }) { Text("Cancelar") }
            }
        )
    }

    asignaturaPendienteNota?.let { asignatura ->
        DialogoNota(
            nombreAsignatura = asignatura.nombre,
            onConfirmar = { nota ->
                viewModel.actualizarAsignatura(
                    asignatura.copy(estado = EstadoAsignatura.APROBADA, nota = nota)
                )
                asignaturaPendienteNota = null
            },
            onCancelar = { asignaturaPendienteNota = null }
        )
    }

    asignaturaNotaAEditar?.let { asignatura ->
        DialogoNota(
            nombreAsignatura = asignatura.nombre,
            notaInicial = asignatura.nota?.toString() ?: "",
            onConfirmar = { nota ->
                viewModel.actualizarAsignatura(asignatura.copy(nota = nota))
                asignaturaNotaAEditar = null
            },
            onCancelar = { asignaturaNotaAEditar = null }
        )
    }

    if (mostrarEditarCurso) {
        DialogoCurso(
            nombreInicial = cursoActual.nombre,
            creditosInicial = cursoActual.creditosCurso.toString(),
            onConfirmar = { nombre, creditos ->
                viewModel.actualizarCurso(cursoActual.copy(nombre = nombre, creditosCurso = creditos))
                mostrarEditarCurso = false
            },
            onCancelar = { mostrarEditarCurso = false }
        )
    }

    if (confirmarEliminarCurso) {
        AlertDialog(
            onDismissRequest = { confirmarEliminarCurso = false },
            title = { Text("Eliminar ${cursoActual.nombre}") },
            text = { Text("Se eliminarán también todas sus asignaturas.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.eliminarCurso(cursoActual, alTerminar = onVolver)
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { confirmarEliminarCurso = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun AsignaturaCard(
    asignatura: Asignatura,
    onCambiarEstado: (EstadoAsignatura) -> Unit,
    onCambiarConvocatorias: (Int) -> Unit,
    onEditar: () -> Unit,
    onEliminar: () -> Unit,
    onEditarNota: () -> Unit
) {
    var menuAbierto by remember { mutableStateOf(false) }

    OutlinedCard(
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(asignatura.nombre, style = MaterialTheme.typography.titleMedium)
                    Text(
                        "${formatCreditos(asignatura.creditos)} créditos",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (asignatura.estado == EstadoAsignatura.APROBADA && asignatura.nota != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable(onClick = onEditarNota)
                        ) {
                            Text(
                                "%.1f".format(asignatura.nota),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Icon(
                                Icons.Filled.Edit,
                                contentDescription = "Editar nota",
                                modifier = Modifier.padding(start = 4.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Box {
                        IconButton(onClick = { menuAbierto = true }) {
                            Icon(Icons.Filled.MoreVert, contentDescription = "Más opciones")
                        }
                        DropdownMenu(expanded = menuAbierto, onDismissRequest = { menuAbierto = false }) {
                            DropdownMenuItem(text = { Text("Editar") }, onClick = {
                                menuAbierto = false
                                onEditar()
                            })
                            DropdownMenuItem(text = { Text("Eliminar") }, onClick = {
                                menuAbierto = false
                                onEliminar()
                            })
                        }
                    }
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            EstadoSelector(estadoActual = asignatura.estado, onEstadoElegido = onCambiarEstado)
            if (asignatura.estado != EstadoAsignatura.APROBADA) {
                ConvocatoriasStepper(
                    convocatorias = asignatura.convocatoriasGastadas,
                    onCambiar = onCambiarConvocatorias,
                    modifier = Modifier.padding(top = 14.dp)
                )
            }
        }
    }
}
