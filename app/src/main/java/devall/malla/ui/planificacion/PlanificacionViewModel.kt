package devall.malla.ui.planificacion

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import devall.malla.MallaApplication
import devall.malla.data.Asignatura
import devall.malla.data.BloqueHorario
import devall.malla.data.OcurrenciaBloque
import devall.malla.data.lunesDeSemana
import devall.malla.data.ocurrenciasEnSemana
import devall.malla.widget.MallaWidget
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class PlanificacionViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = (application as MallaApplication).repository

    val bloques: StateFlow<List<BloqueHorario>> = repository.bloques
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val asignaturasEstudiando: StateFlow<List<Asignatura>> = repository.asignaturasEstudiando
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val todasAsignaturas: StateFlow<List<Asignatura>> = repository.todasAsignaturas
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    var lunesMostrado by mutableStateOf(lunesDeSemana(LocalDate.now()))
        private set

    fun semanaAnterior() {
        lunesMostrado = lunesMostrado.minusWeeks(1)
    }

    fun semanaSiguiente() {
        lunesMostrado = lunesMostrado.plusWeeks(1)
    }

    fun irAHoy() {
        lunesMostrado = lunesDeSemana(LocalDate.now())
    }

    fun irASemanaDe(fecha: LocalDate) {
        lunesMostrado = lunesDeSemana(fecha)
    }

    fun ocurrenciasDeLaSemana(bloques: List<BloqueHorario>): List<OcurrenciaBloque> =
        ocurrenciasEnSemana(bloques, lunesMostrado)

    fun crearBloque(bloque: BloqueHorario) = viewModelScope.launch {
        repository.crearBloque(bloque)
        actualizarWidget()
    }

    fun actualizarBloque(bloque: BloqueHorario) = viewModelScope.launch {
        repository.actualizarBloque(bloque)
        actualizarWidget()
    }

    fun eliminarBloque(bloque: BloqueHorario) = viewModelScope.launch {
        repository.eliminarBloque(bloque)
        actualizarWidget()
    }

    private suspend fun actualizarWidget() {
        MallaWidget().updateAll(getApplication())
    }
}
