package bastidas.felipe.mistareasapp

import android.content.Context
import android.view.KeyEvent
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.concurrent.Volatile

@Database(
    entities = [TaskEntity::class],
    version = 1
)
abstract class AppDatabase: RoomDatabase(){

    abstract fun taskDao(): TaskDao

    companion object{

        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val TAREAS_INICIALES = listOf(
            TaskEntity(
                titulo = "Configurar repositorio en GitHub",
                completado = true
            ),
            TaskEntity(
                titulo = "Implementar base de datos con Room",
                completado = true
            ),
            TaskEntity(
                titulo = "Construir UI con Jetpack Compose",
                completado = true
            ),
            TaskEntity(
                titulo = "Crear proyecto de Android",
                completado = true
            ),
            TaskEntity(
                titulo = "Crear Firebase",
                completado = true
            ),
            TaskEntity(
                titulo = "Configurar el Firebase en el Proyecto de Android",
                completado = true
            ),
            TaskEntity(
                titulo = "Hacer logica de Login",
                completado = true
            ),
            TaskEntity(
                titulo = "Hacer logica de SignUp",
                completado = true
            ),
            TaskEntity(
                titulo = "Manejar errores de firebase en las capas de datos",
                completado = true
            ),
            TaskEntity(
                titulo = "Manejar errores de firebase en las capas de dominio",
                completado = true
            ),
            TaskEntity(
                titulo = "Revisar PRs",
                completado = false
            ),
            TaskEntity(
                titulo = "Hacer logica de Restablecer contraseña",
                completado = false
            ),
            TaskEntity(
                titulo = "Hacer pantalla de Restablecer contraseña",
                completado = false
            ),
            TaskEntity(
                titulo = "Estudiar Clean Architecture de Android Developers",
                completado = true
            ),
            TaskEntity(
                titulo = "Estudiar MVVM de Android Developers",
                completado = true
            ),
            TaskEntity(
                titulo = "Estudiar convenciones de Android Developers",
                completado = true
            ),
        )

        fun getInstance(
            context: Context
        ): AppDatabase {
            return INSTANCE ?: synchronized(
                this
            ){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tasks_db"
                )
                    .addCallback(object: RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            val dao = getInstance(context).taskDao()
                            TAREAS_INICIALES.forEach { tarea ->
                                dao.insert(tarea)
                            }
                        }
                    }
                }).build()

                instance
            }
        }
    }
}