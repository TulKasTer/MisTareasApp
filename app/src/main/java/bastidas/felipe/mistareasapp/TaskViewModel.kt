package bastidas.felipe.mistareasapp

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.room.util.query
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel (private val dao: TaskDao): ViewModel() {

    private val _searchInput = MutableStateFlow("")
    val searchInput: StateFlow<String> = _searchInput.asStateFlow()

    private val _activeQuery = MutableStateFlow("")

    private val _sortBy = MutableStateFlow("REC")

    val currentSort: StateFlow<String> = _sortBy.asStateFlow()


    @OptIn(ExperimentalCoroutinesApi::class)
    val tareas: StateFlow<List<TaskEntity>> = combine(_activeQuery, _sortBy){
        query, sortBy ->
        Pair(query, sortBy)
    }
        .flatMapLatest { (query, sortBy) ->
            dao.searchTasks(query, sortBy)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun addTask(title: String){
        if(title.isBlank()) return
        viewModelScope.launch {
            dao.insert(TaskEntity(titulo = title.trim()))
        }
    }

    fun toggleCompleted(task: TaskEntity){
        viewModelScope.launch {
            dao.update((task.copy(completado = !task.completado)))
        }
    }

    fun deleteTask(task: TaskEntity){
        viewModelScope.launch {
            dao.delete(task)
        }
    }

    fun onSearchInputChanged(text: String) {
        _searchInput.value = text
    }

    fun executeSearch() {
        _activeQuery.value = _searchInput.value.trim()
    }

    fun updateSortBy(newSort: String){
        _sortBy.value = newSort
    }

    companion object{
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as Application
                val dao = AppDatabase
                    .getInstance(application)
                    .taskDao()
                TaskViewModel(dao)
            }
        }
    }
}