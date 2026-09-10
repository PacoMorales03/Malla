package devall.malla

import android.app.Application
import devall.malla.data.AppDatabase
import devall.malla.data.MallaRepository

class MallaApplication : Application() {
    val repository: MallaRepository by lazy {
        MallaRepository(AppDatabase.obtener(this))
    }
}
