package bastidas.felipe.mistareasapp

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource

@Composable
fun SortDropdown(
    currentSortCode: String,
    onSortSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val sortOptions = mapOf(
        "REC" to stringResource(R.string.recientes),
        "OLD" to stringResource(R.string.antiguo),
        "AZ" to stringResource(R.string.titulo_A_Z),
        "ZA" to stringResource(R.string.titulo_Z_A)
    )

    Box{
        OutlinedButton( onClick = {
            expanded = true
        }) {
            Text(
                text = "Ordenar: ${sortOptions[currentSortCode] ?: "Recientes"}"
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Opciones de ordenamiento"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            sortOptions.forEach { (code, label) ->
                DropdownMenuItem(
                    text = { Text(label)},
                    onClick = {
                        onSortSelected(code)
                        expanded = false
                    }
                )
            }
        }
    }
}