package cl.duoc.esfocar.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.esfocar.data.AppDatabase
import cl.duoc.esfocar.data.DocumentEntity
import cl.duoc.esfocar.data.Session
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class DocumentsViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = AppDatabase.get(app).documentDao()

    val docs: StateFlow<List<DocumentEntity>> = Session.userId
        .flatMapLatest { id -> if (id == null) flowOf(emptyList()) else dao.observeAll(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _saving = MutableStateFlow(false)
    val saving: StateFlow<Boolean> = _saving.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun clearMessage() { _message.value = null }

    /** Copia el archivo elegido al almacenamiento interno de la app (funciona sin internet). */
    fun upload(uri: Uri, title: String, onDone: () -> Unit) {
        val uid = Session.userId.value ?: return
        viewModelScope.launch {
            _saving.value = true
            try {
                val ctx = getApplication<Application>()
                val doc = withContext(Dispatchers.IO) {
                    val cr = ctx.contentResolver
                    val (name, _) = cr.fileInfo(uri)
                    val mime = cr.getType(uri) ?: "application/octet-stream"
                    val dir = File(ctx.filesDir, "docs").apply { mkdirs() }
                    val safe = name.replace(Regex("[^A-Za-z0-9._-]"), "_")
                    val dest = File(dir, "${UUID.randomUUID()}_$safe")
                    cr.openInputStream(uri)!!.use { input ->
                        dest.outputStream().use { out -> input.copyTo(out) }
                    }
                    DocumentEntity(
                        userId = uid, title = title.trim(), fileName = name,
                        path = dest.absolutePath, mimeType = mime,
                        sizeBytes = dest.length(), createdAt = System.currentTimeMillis()
                    )
                }
                dao.insert(doc)
                _message.value = "Documento guardado"
                onDone()
            } catch (e: Exception) {
                _message.value = "No se pudo guardar el archivo"
            } finally {
                _saving.value = false
            }
        }
    }

    fun delete(doc: DocumentEntity) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { File(doc.path).delete() }
            dao.delete(doc)
            _message.value = "Documento eliminado"
        }
    }
}
