package bastidas.felipe.mistareasapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    viewModel: TaskViewModel = viewModel(
        factory = TaskViewModel.Factory
    )
) {

    val tasks by viewModel.tareas.collectAsStateWithLifecycle()

    val searchInput by viewModel.searchInput
        .collectAsStateWithLifecycle()

    var nuevaTareaTexto by remember { mutableStateOf("") }

    // Tarea pendiente de confirmar eliminación
    var tareaAEliminar by remember {
        mutableStateOf<TaskEntity?>(null)
    }

    val currentSort by viewModel.currentSort.collectAsState()

    Scaffold { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {

            Text(
                text = stringResource(R.string.app_title),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            SearchBar(
                searchInput = searchInput,
                onSearchInputChanged = { texto ->
                    viewModel.onSearchInputChanged(texto)
                },
                onSearchClicked = {
                    viewModel.executeSearch()
                },
                modifier = Modifier.padding(bottom = 8.dp)
            )

            SortDropdown(
                currentSortCode = currentSort,
                onSortSelected = { newSortCode ->
                    viewModel.updateSortBy(newSortCode)
                }
            )

            Box(modifier = Modifier.weight(1f)) {

                if (tasks.isEmpty()) {

                    Text(
                        text = stringResource(
                            R.string.empty_list_message
                        ),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        textAlign = TextAlign.Center
                    )

                } else {

                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {

                        items(
                            items = tasks,
                            key = { task -> task.id }
                        ) { task ->

                            val dismissState =
                                rememberSwipeToDismissBoxState(
                                    confirmValueChange = { value ->

                                        if (
                                            value ==
                                            SwipeToDismissBoxValue.EndToStart
                                        ) {
                                            tareaAEliminar = task
                                            false
                                        } else {
                                            false
                                        }
                                    }
                                )

                            SwipeToDismissBox(
                                state = dismissState,

                                enableDismissFromStartToEnd = false,

                                backgroundContent = {

                                    Row(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 16.dp),

                                        horizontalArrangement =
                                            Arrangement.End,

                                        verticalAlignment =
                                            Alignment.CenterVertically
                                    ) {

                                        Icon(
                                            imageVector = Icons.Default.Delete,

                                            contentDescription = null,

                                            tint = MaterialTheme
                                                    .colorScheme
                                                    .error
                                        )
                                    }
                                }
                            ) {

                                TaskItem(
                                    task = task,

                                    onToggleCompleted = {
                                        viewModel
                                            .toggleCompleted(task)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),

                verticalAlignment = Alignment.CenterVertically,

                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                OutlinedTextField(
                    value = nuevaTareaTexto,

                    onValueChange = {
                        nuevaTareaTexto = it
                    },

                    placeholder = {
                        Text(
                            text = stringResource(
                                R.string.new_task_placeholder
                            )
                        )
                    },

                    modifier = Modifier.weight(1f)
                )

                Button(
                    onClick = {
                        viewModel.addTask(nuevaTareaTexto)
                        nuevaTareaTexto = ""
                    }
                ) {

                    Text(
                        text = stringResource(
                            R.string.add_button
                        )
                    )
                }
            }
        }

        // -------- Dialogo de confirmación --------

        tareaAEliminar?.let { task ->

            AlertDialog(
                onDismissRequest = {
                    tareaAEliminar = null
                },

                title = {
                    Text(stringResource(R.string.confirmar_eliminación))
                },

                text = {
                    Text(
                        stringResource(R.string.seguro_eliminar)
                    )
                },

                confirmButton = {

                    TextButton(
                        onClick = {

                            viewModel.deleteTask(task)

                            tareaAEliminar = null
                        }
                    ) {
                        Text(stringResource(R.string.eliminar))
                    }
                },

                dismissButton = {

                    TextButton(
                        onClick = {
                            tareaAEliminar = null
                        }
                    ) {
                        Text(stringResource(R.string.cancelar))
                    }
                }
            )
        }
    }
}