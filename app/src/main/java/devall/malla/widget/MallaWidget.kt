package devall.malla.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.action.clickable
import androidx.glance.unit.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import devall.malla.MainActivity
import devall.malla.data.AppDatabase
import devall.malla.data.MallaRepository
import devall.malla.data.TipoBloque
import devall.malla.data.lunesDeSemana
import devall.malla.data.ocurrenciasEnSemana
import kotlinx.coroutines.flow.first
import java.time.LocalDate

private val ColorPaper = Color(0xFFF1EFE6)
private val ColorInk = Color(0xFF1B2A34)
private val ColorMuted = Color(0xFF5F6B64)
private val ColorAccent = Color(0xFF2A6B6A)
private val ColorAccentSoft = Color(0xFFDCEBE7)

private val diasCortos = listOf("L", "M", "X", "J", "V", "S", "D")

class MallaWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = MallaRepository(AppDatabase.obtener(context))
        val bloques = repository.bloques.first()
        val hoy = LocalDate.now().dayOfWeek.value
        val ocurrencias = ocurrenciasEnSemana(bloques, lunesDeSemana(LocalDate.now()))

        val minutosPorDia = (1..7).associateWith { dia ->
            ocurrencias
                .filter { it.bloque.diaSemana == dia && it.bloque.tipo == TipoBloque.ESTUDIO_PLANIFICADO }
                .sumOf { it.bloque.horaFinMinutos - it.bloque.horaInicioMinutos }
        }

        provideContent {
            ContenidoWidget(context = context, minutosPorDia = minutosPorDia, diaDeHoy = hoy)
        }
    }
}

@Composable
private fun ContenidoWidget(context: Context, minutosPorDia: Map<Int, Int>, diaDeHoy: Int) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .appWidgetBackground()
            .background(ColorProvider(ColorPaper))
            .cornerRadius(16.dp)
            .padding(10.dp)
            .clickable(actionStartActivity(Intent(context, MainActivity::class.java)))
    ) {
        Text(
            "Malla · esta semana",
            style = TextStyle(color = ColorProvider(ColorInk), fontWeight = FontWeight.Medium, fontSize = 12.sp)
        )
        Spacer(modifier = GlanceModifier.height(8.dp))
        Row(modifier = GlanceModifier.fillMaxWidth()) {
            for (dia in 1..4) {
                CeldaDia(dia, minutosPorDia[dia] ?: 0, dia == diaDeHoy, GlanceModifier.defaultWeight())
            }
        }
        Spacer(modifier = GlanceModifier.height(6.dp))
        Row(modifier = GlanceModifier.fillMaxWidth()) {
            for (dia in 5..7) {
                CeldaDia(dia, minutosPorDia[dia] ?: 0, dia == diaDeHoy, GlanceModifier.defaultWeight())
            }
            Spacer(modifier = GlanceModifier.defaultWeight())
        }
    }
}

@Composable
private fun CeldaDia(dia: Int, minutos: Int, esHoy: Boolean, modifier: GlanceModifier) {
    Box(
        modifier = modifier
            .padding(2.dp)
            .background(ColorProvider(if (esHoy) ColorAccent else ColorAccentSoft))
            .cornerRadius(8.dp)
            .padding(vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                diasCortos[dia - 1],
                style = TextStyle(
                    color = ColorProvider(if (esHoy) Color.White else ColorInk),
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp
                )
            )
            Text(
                "${minutos / 60}h",
                style = TextStyle(
                    color = ColorProvider(if (esHoy) Color.White else ColorMuted),
                    fontSize = 10.sp
                )
            )
        }
    }
}

class MallaWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MallaWidget()
}
