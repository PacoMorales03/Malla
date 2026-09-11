package devall.malla.ui.cursos

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import devall.malla.data.Curso
import devall.malla.data.CursoConProgreso
import devall.malla.data.formatCreditos
import devall.malla.ui.common.MallaLogo
import kotlin.math.roundToInt

@Composable
fun CursosScreen(
    onCursoClick: (Long) -> Unit,
    viewModel: CursosViewModel = viewModel()
) {
    val cursos by viewModel.cursos.collectAsState()
    val progresoTotal by viewModel.progresoTotal.collectAsState()
    val creditosTotales by viewModel.creditosTotalesConfigurados.collectAsState()
    val notaMedia by viewModel.notaMedia.collectAsState()

    var mostrarNuevoCurso by remember { mutableStateOf(false) }
    var mostrarEditarCreditos by remember { mutableStateOf(false) }
    var cursoEnEdicion by remember { mutableStateOf<Curso?>(null) }
    var cursoAEliminar by remember { mutableStateOf<Curso?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MallaLogo()
                    Text(
                        "Cursos",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = { mostrarEditarCreditos = true }) {
                        Text("Créditos")
                    }
                }

                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            "Progreso total de la carrera",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(
                                "${(progresoTotal.porcentaje * 100).roundToInt()}%",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                "${formatCreditos(progresoTotal.creditosAprobados)} / ${formatCreditos(progresoTotal.creditosTotales)} créditos",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 3.dp)
                            )
                        }
                        LinearProgressIndicator(
                            progress = { progresoTotal.porcentaje },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                        )
                        notaMedia?.let { nota ->
                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Nota media",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    "%.2f".format(nota),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            if (cursos.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text(
                        "Todavía no tienes cursos.\nToca + para crear el primero.",
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
                    items(cursos, key = { it.curso.id }) { item ->
                        CursoCard(
                            item = item,
                            onClick = { onCursoClick(item.curso.id) },
                            onEditar = { cursoEnEdicion = item.curso },
                            onEliminar = { cursoAEliminar = item.curso }
                        )
                    }
                    item { Box(modifier = Modifier.padding(bottom = 88.dp)) }
                }
            }
        }

        FloatingActionButton(
            onClick = { mostrarNuevoCurso = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Nuevo curso")
        }
    }

    if (creditosTotales == null) {
        DialogoCreditosCarrera(
            onConfirmar = { viewModel.guardarCreditosTotales(it) },
            onCancelar = null
        )
    }

    if (mostrarNuevoCurso) {
        DialogoCurso(
            onConfirmar = { nombre, creditos ->
                viewModel.crearCurso(nombre, creditos)
                mostrarNuevoCurso = false
            },
            onCancelar = { mostrarNuevoCurso = false }
        )
    }

    if (mostrarEditarCreditos) {
        DialogoCreditosCarrera(
            valorInicial = creditosTotales?.toString() ?: "",
            onConfirmar = {
                viewModel.guardarCreditosTotales(it)
                mostrarEditarCreditos = false
            },
            onCancelar = { mostrarEditarCreditos = false }
        )
    }

    cursoEnEdicion?.let { curso ->
        DialogoCurso(
            nombreInicial = curso.nombre,
            creditosInicial = curso.creditosCurso.toString(),
            onConfirmar = { nombre, creditos ->
                viewModel.actualizarCurso(curso.copy(nombre = nombre, creditosCurso = creditos))
                cursoEnEdicion = null
            },
            onCancelar = { cursoEnEdicion = null }
        )
    }

    cursoAEliminar?.let { curso ->
        AlertDialog(
            onDismissRequest = { cursoAEliminar = null },
            title = { Text("Eliminar ${curso.nombre}") },
            text = { Text("Se eliminarán también todas sus asignaturas.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.eliminarCurso(curso)
                    cursoAEliminar = null
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { cursoAEliminar = null }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun CursoCard(
    item: CursoConProgreso,
    onClick: () -> Unit,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    var menuAbierto by remember { mutableStateOf(false) }

    OutlinedCard(
        onClick = onClick,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(item.curso.nombre, style = MaterialTheme.typography.titleMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "${(item.porcentaje * 100).roundToInt()}%",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
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
            LinearProgressIndicator(
                progress = { item.porcentaje },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
            HorizontalDivider(modifier = Modifier.padding(top = 12.dp, bottom = 8.dp))
            Text(
                "${formatCreditos(item.creditosAprobados)} / ${formatCreditos(item.curso.creditosCurso)} créditos",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
