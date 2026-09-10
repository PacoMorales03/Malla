package devall.malla.ui.nav

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import devall.malla.ui.cursos.CursoDetalleScreen
import devall.malla.ui.cursos.CursosScreen
import devall.malla.ui.planificacion.PlanificacionScreen
import devall.malla.ui.update.ActualizacionBanner

private object Rutas {
    const val CURSOS = "cursos"
    const val PLANIFICACION = "planificacion"
    const val CURSO_DETALLE = "curso/{cursoId}"
    fun cursoDetalle(cursoId: Long) = "curso/$cursoId"
}

private data class Pestana(val ruta: String, val etiqueta: String, val icono: ImageVector)

private val pestanas = listOf(
    Pestana(Rutas.CURSOS, "Cursos", Icons.Filled.School),
    Pestana(Rutas.PLANIFICACION, "Planificación", Icons.Filled.CalendarMonth)
)

@Composable
fun MallaNavHost() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = backStackEntry?.destination
    val mostrarBarraInferior = pestanas.any { rutaActual?.hierarchy?.any { d -> d.route == it.ruta } == true }

    Scaffold(
        bottomBar = {
            if (mostrarBarraInferior) {
                NavigationBar {
                    pestanas.forEach { pestana ->
                        val seleccionada = rutaActual?.hierarchy?.any { it.route == pestana.ruta } == true
                        NavigationBarItem(
                            selected = seleccionada,
                            onClick = {
                                navController.navigate(pestana.ruta) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(pestana.icono, contentDescription = pestana.etiqueta) },
                            label = { Text(pestana.etiqueta) }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            ActualizacionBanner()
            NavHost(
                navController = navController,
                startDestination = Rutas.CURSOS
            ) {
                composable(Rutas.CURSOS) {
                    CursosScreen(onCursoClick = { navController.navigate(Rutas.cursoDetalle(it)) })
                }
                composable(Rutas.PLANIFICACION) {
                    PlanificacionScreen()
                }
                composable(
                    route = Rutas.CURSO_DETALLE,
                    arguments = listOf(navArgument("cursoId") { type = NavType.LongType })
                ) { entry ->
                    val cursoId = entry.arguments?.getLong("cursoId") ?: return@composable
                    CursoDetalleScreen(cursoId = cursoId, onVolver = { navController.popBackStack() })
                }
            }
        }
    }
}
