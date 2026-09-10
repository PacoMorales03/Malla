package devall.malla.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import devall.malla.data.TipoBloque

data class BloqueColores(
    val estudio: Color,
    val trabajo: Color,
    val personal: Color
) {
    fun de(tipo: TipoBloque): Color = when (tipo) {
        TipoBloque.ESTUDIO_PLANIFICADO, TipoBloque.ESTUDIO_REAL -> estudio
        TipoBloque.TRABAJO -> trabajo
        TipoBloque.PERSONAL -> personal
    }
}

val BloqueColoresClaro = BloqueColores(estudio = EstadoEstudiando, trabajo = BloqueTrabajo, personal = BloquePersonal)
val BloqueColoresOscuro = BloqueColores(estudio = EstadoEstudiandoDark, trabajo = BloqueTrabajoDark, personal = BloquePersonalDark)

val LocalBloqueColores = staticCompositionLocalOf { BloqueColoresClaro }
