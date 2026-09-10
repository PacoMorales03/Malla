package devall.malla.ui.planificacion

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import devall.malla.data.BloqueHorario
import devall.malla.data.OcurrenciaBloque
import devall.malla.data.TipoBloque
import devall.malla.ui.theme.BloqueColores
import devall.malla.ui.theme.LocalBloqueColores
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

private val ALTURA_HORA = 52.dp
private val ANCHO_ETIQUETA_HORA = 34.dp
private val ANCHO_DIA_MINIMO = 36.dp
private val ANCHO_DIA_MAXIMO = 96.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanificacionScreen(viewModel: PlanificacionViewModel = viewModel()) {
    val bloques by viewModel.bloques.collectAsState()
    val asignaturasEstudiando by viewModel.asignaturasEstudiando.collectAsState()
    val todasAsignaturas by viewModel.todasAsignaturas.collectAsState()
    val bloqueColores = LocalBloqueColores.current
    val lunes = viewModel.lunesMostrado

    var bloqueEnEdicion by remember { mutableStateOf<BloqueHorario?>(null) }
    var nuevoBloqueFecha by remember { mutableStateOf<LocalDate?>(null) }
    var nuevoBloqueMinutos by remember { mutableStateOf(HORA_INICIO_AGENDA + 60) }
    var mostrarSelectorSemana by remember { mutableStateOf(false) }

    val ocurrencias = remember(bloques, lunes) { viewModel.ocurrenciasDeLaSemana(bloques) }

    val mapaNombresAsignatura = remember(todasAsignaturas) {
        todasAsignaturas.associate { it.id to it.nombre }
    }
    val nombreDeAsignatura: (Long) -> String = { id -> mapaNombresAsignatura[id] ?: "" }

    val minutosPlanificados = ocurrencias
        .filter { it.bloque.tipo == TipoBloque.ESTUDIO_PLANIFICADO }
        .sumOf { it.bloque.horaFinMinutos - it.bloque.horaInicioMinutos }
    val minutosReales = ocurrencias
        .filter { it.bloque.tipo == TipoBloque.ESTUDIO_REAL }
        .sumOf { it.bloque.horaFinMinutos - it.bloque.horaInicioMinutos }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Planificación", style = MaterialTheme.typography.headlineSmall)

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.semanaAnterior() }) {
                        Icon(Icons.Filled.ChevronLeft, contentDescription = "Semana anterior")
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { mostrarSelectorSemana = true }
                    ) {
                        Text(formatoRangoSemana(lunes), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        if (lunes != lunesDeSemanaDeHoy()) {
                            TextButton(onClick = { viewModel.irAHoy() }) { Text("Volver a hoy") }
                        }
                    }
                    IconButton(onClick = { viewModel.semanaSiguiente() }) {
                        Icon(Icons.Filled.ChevronRight, contentDescription = "Semana siguiente")
                    }
                }

                OutlinedCard(
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(18.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ResumenHoras("Planificadas", minutosPlanificados, bloqueColores.estudio.copy(alpha = 0.6f))
                        ResumenHoras("Reales", minutosReales, bloqueColores.estudio)
                    }
                }
            }

            BoxWithConstraints(modifier = Modifier.weight(1f).fillMaxWidth()) {
                val anchoDia = ((maxWidth - ANCHO_ETIQUETA_HORA) / 7)
                    .coerceIn(ANCHO_DIA_MINIMO, ANCHO_DIA_MAXIMO)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 96.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.width(ANCHO_ETIQUETA_HORA))
                        for (offset in 0..6) {
                            val fecha = lunes.plusDays(offset.toLong())
                            EncabezadoDia(fecha = fecha, ancho = anchoDia)
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth()) {
                        ColumnaHoras()
                        for (offset in 0..6) {
                            val fecha = lunes.plusDays(offset.toLong())
                            ColumnaDia(
                                ancho = anchoDia,
                                ocurrenciasDelDia = ocurrencias.filter { it.fecha == fecha },
                                bloqueColores = bloqueColores,
                                nombreDeAsignatura = nombreDeAsignatura,
                                onTapVacio = { minutos ->
                                    nuevoBloqueFecha = fecha
                                    nuevoBloqueMinutos = minutos
                                },
                                onTapBloque = { bloqueEnEdicion = it }
                            )
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = {
                nuevoBloqueFecha = LocalDate.now()
                nuevoBloqueMinutos = HORA_INICIO_AGENDA + 60
            },
            modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Nuevo bloque")
        }
    }

    if (mostrarSelectorSemana) {
        val estado = rememberDatePickerState(
            initialSelectedDateMillis = lunes.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { mostrarSelectorSemana = false },
            confirmButton = {
                TextButton(onClick = {
                    estado.selectedDateMillis?.let { millis ->
                        viewModel.irASemanaDe(Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate())
                    }
                    mostrarSelectorSemana = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarSelectorSemana = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = estado)
        }
    }

    if (bloqueEnEdicion != null || nuevoBloqueFecha != null) {
        DialogoBloque(
            fechaInicial = bloqueEnEdicion?.fechaInicio ?: nuevoBloqueFecha!!,
            horaInicialMinutos = bloqueEnEdicion?.horaInicioMinutos ?: nuevoBloqueMinutos,
            bloqueExistente = bloqueEnEdicion,
            asignaturasEstudiando = asignaturasEstudiando,
            onConfirmar = { bloque ->
                if (bloqueEnEdicion != null) viewModel.actualizarBloque(bloque) else viewModel.crearBloque(bloque)
                bloqueEnEdicion = null
                nuevoBloqueFecha = null
            },
            onEliminar = bloqueEnEdicion?.let { b -> ({ viewModel.eliminarBloque(b); bloqueEnEdicion = null }) },
            onCancelar = {
                bloqueEnEdicion = null
                nuevoBloqueFecha = null
            }
        )
    }
}

private fun lunesDeSemanaDeHoy(): LocalDate {
    val hoy = LocalDate.now()
    return hoy.minusDays((hoy.dayOfWeek.value - 1).toLong())
}

@Composable
private fun ResumenHoras(etiqueta: String, minutos: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "%.1f h".format(minutos / 60.0),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
        Text(
            etiqueta,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun EncabezadoDia(fecha: LocalDate, ancho: Dp) {
    val esHoy = fecha == LocalDate.now()
    Box(
        modifier = Modifier.width(ancho).padding(bottom = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                diasSemanaCortos[fecha.dayOfWeek.value - 1].second,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Box(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .size(24.dp)
                    .clip(CircleShape)
                    .then(if (esHoy) Modifier.background(MaterialTheme.colorScheme.primary) else Modifier),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    fecha.dayOfMonth.toString(),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = if (esHoy) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun ColumnaHoras() {
    val horas = (HORA_FIN_AGENDA - HORA_INICIO_AGENDA) / 60
    Column(modifier = Modifier.width(ANCHO_ETIQUETA_HORA)) {
        for (h in 0..horas) {
            Box(modifier = Modifier.height(ALTURA_HORA)) {
                Text(
                    formatoHora(HORA_INICIO_AGENDA + h * 60),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun ColumnaDia(
    ancho: Dp,
    ocurrenciasDelDia: List<OcurrenciaBloque>,
    bloqueColores: BloqueColores,
    nombreDeAsignatura: (Long) -> String,
    onTapVacio: (minutos: Int) -> Unit,
    onTapBloque: (BloqueHorario) -> Unit
) {
    val density = LocalDensity.current
    val horas = (HORA_FIN_AGENDA - HORA_INICIO_AGENDA) / 60
    val alturaTotal = ALTURA_HORA * horas

    Box(
        modifier = Modifier
            .width(ancho)
            .height(alturaTotal)
            .pointerInputTap(density) { minutos -> onTapVacio(minutos) }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val alturaHoraPx = size.height / horas
            val colorLinea = Color(0x1F000000)
            for (h in 0..horas) {
                drawLine(
                    color = colorLinea,
                    start = Offset(0f, h * alturaHoraPx),
                    end = Offset(size.width, h * alturaHoraPx),
                    strokeWidth = 1f
                )
            }
        }
        ocurrenciasDelDia.forEach { ocurrencia ->
            val bloque = ocurrencia.bloque
            val offsetY = ALTURA_HORA * ((bloque.horaInicioMinutos - HORA_INICIO_AGENDA) / 60f)
            val alturaBloque = ALTURA_HORA * ((bloque.horaFinMinutos - bloque.horaInicioMinutos) / 60f)
            val esSolido = bloque.tipo != TipoBloque.ESTUDIO_PLANIFICADO
            val color = bloqueColores.de(bloque.tipo)

            Box(
                modifier = Modifier
                    .offset(y = offsetY)
                    .fillMaxWidth()
                    .height(alturaBloque)
                    .padding(1.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .then(
                        if (esSolido) Modifier.background(color)
                        else Modifier.background(color.copy(alpha = 0.22f)).border(1.dp, color, RoundedCornerShape(4.dp))
                    )
                    .clickable { onTapBloque(bloque) }
                    .padding(2.dp)
            ) {
                Text(
                    text = bloque.asignaturaId?.let(nombreDeAsignatura) ?: bloque.nombre.orEmpty(),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (esSolido) Color.White else color,
                    maxLines = 3
                )
            }
        }
    }
}

private fun Modifier.pointerInputTap(
    density: Density,
    onMinuto: (Int) -> Unit
): Modifier = this.then(
    Modifier.pointerInput(Unit) {
        detectTapGestures { offset ->
            val alturaHoraPx = with(density) { ALTURA_HORA.toPx() }
            val minutosDesdeInicio = ((offset.y / alturaHoraPx) * 60).toInt()
            val redondeado = HORA_INICIO_AGENDA + (minutosDesdeInicio / 30) * 30
            onMinuto(redondeado.coerceIn(HORA_INICIO_AGENDA, HORA_FIN_AGENDA - 30))
        }
    }
)
