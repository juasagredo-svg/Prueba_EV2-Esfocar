package cl.duoc.esfocar.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cl.duoc.esfocar.data.DocumentEntity


@Composable
fun UploadScreen(vm: DocumentsViewModel, onLogout: () -> Unit, onDone: () -> Unit) {
    val ctx = LocalContext.current
    var title by rememberSaveable { mutableStateOf("") }
    var uri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var fileName by rememberSaveable { mutableStateOf("") }
    var fileSize by rememberSaveable { mutableStateOf(0L) }
    var toDelete by remember { mutableStateOf<DocumentEntity?>(null) }
    var confirmLogout by remember { mutableStateOf(false) }
    val saving by vm.saving.collectAsStateWithLifecycle()
    val docs by vm.docs.collectAsStateWithLifecycle()

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { picked ->
        if (picked != null) {
            val (n, s) = ctx.contentResolver.fileInfo(picked)
            uri = picked; fileName = n; fileSize = s
        }
    }

    val titleErr = if (title.isNotEmpty()) Validators.title(title) else null
    val fileErr = if (uri != null && fileSize > MAX_FILE_BYTES) "El archivo supera 20 MB" else null
    val valid = Validators.title(title) == null && uri != null && fileErr == null

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Cargar documento", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
                    TextButton(onClick = { confirmLogout = true }) { Text("Cerrar sesión") }
                }
                Text("Agrega apuntes, manuales o material de estudio.", style = MaterialTheme.typography.bodyLarge)
                FormField(title, { title = it }, "Título del documento", titleErr, Icons.Default.Add, imeAction = ImeAction.Done)

                OutlinedButton(
                    onClick = { picker.launch(arrayOf("application/pdf", "image/*", "text/plain")) },
                    modifier = Modifier.fillMaxWidth()
                ) { Text(if (uri == null) "Seleccionar archivo (PDF, imagen o texto)" else "Cambiar archivo") }

                AnimatedVisibility(visible = uri != null) {
                    Text("Archivo: $fileName (${formatSize(fileSize)})", style = MaterialTheme.typography.bodyMedium)
                }
                ErrorBanner(fileErr)

                Button(
                    onClick = {
                        uri?.let {
                            vm.upload(it, title) {
                                title = ""; uri = null; fileName = ""; fileSize = 0; onDone()
                            }
                        }
                    },
                    enabled = valid && !saving,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (saving) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp) else Text("Guardar documento")
                }

                Text("Mis documentos cargados", style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 16.dp))
                AnimatedVisibility(visible = docs.isEmpty()) {
                    Text("Aún no has cargado documentos.", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        items(docs, key = { it.id }) { doc ->
            ElevatedCard(Modifier.fillMaxWidth().animateItem().animateContentSize()) {
                Column(Modifier.padding(12.dp)) {
                    Text(doc.title, style = MaterialTheme.typography.titleMedium)
                    Text("${doc.fileName} · ${formatSize(doc.sizeBytes)}", style = MaterialTheme.typography.bodySmall)
                    Text(formatDate(doc.createdAt), style = MaterialTheme.typography.bodySmall)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { openDocument(ctx, doc) }) { Text("Abrir") }
                        TextButton(onClick = { toDelete = doc }) {
                            Text("Eliminar", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }

    toDelete?.let { d -> DeleteDocumentDialog(d, vm) { toDelete = null } }

    if (confirmLogout) AlertDialog(
        onDismissRequest = { confirmLogout = false },
        title = { Text("Cerrar sesión") },
        text = { Text("¿Quieres salir de tu cuenta?") },
        confirmButton = { TextButton(onClick = { confirmLogout = false; onLogout() }) { Text("Salir") } },
        dismissButton = { TextButton(onClick = { confirmLogout = false }) { Text("Cancelar") } }
    )
}
