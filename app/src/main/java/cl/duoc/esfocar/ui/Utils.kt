package cl.duoc.esfocar.ui

import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Patterns
import android.widget.Toast
import androidx.core.content.FileProvider
import cl.duoc.esfocar.data.DocumentEntity
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


object Validators {
    fun name(v: String) = if (v.trim().length < 3) "Mínimo 3 caracteres" else null
    fun email(v: String) =
        if (!Patterns.EMAIL_ADDRESS.matcher(v.trim()).matches()) "Correo no válido" else null
    fun password(v: String) = when {
        v.length < 6 -> "Mínimo 6 caracteres"
        v.none { it.isLetter() } -> "Debe incluir una letra"
        v.none { it.isDigit() } -> "Debe incluir un número"
        else -> null
    }
    fun confirm(p: String, c: String) = if (p != c) "Las contraseñas no coinciden" else null
    fun title(v: String) = when {
        v.trim().length < 3 -> "Mínimo 3 caracteres"
        v.trim().length > 60 -> "Máximo 60 caracteres"
        else -> null
    }
}

const val MAX_FILE_BYTES = 20L * 1024 * 1024

fun ContentResolver.fileInfo(uri: Uri): Pair<String, Long> {
    var name = "documento"
    var size = 0L
    query(uri, null, null, null, null)?.use { c ->
        if (c.moveToFirst()) {
            val n = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val s = c.getColumnIndex(OpenableColumns.SIZE)
            if (n >= 0) name = c.getString(n) ?: name
            if (s >= 0 && !c.isNull(s)) size = c.getLong(s)
        }
    }
    return name to size
}

fun formatSize(bytes: Long): String = when {
    bytes < 1024 -> "$bytes B"
    bytes < 1024 * 1024 -> "%.1f KB".format(bytes / 1024.0)
    else -> "%.1f MB".format(bytes / (1024.0 * 1024.0))
}

fun formatDate(ms: Long): String =
    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(ms))


fun openDocument(ctx: Context, doc: DocumentEntity) {
    val uri = FileProvider.getUriForFile(ctx, "${ctx.packageName}.fileprovider", File(doc.path))
    val intent = Intent(Intent.ACTION_VIEW)
        .setDataAndType(uri, doc.mimeType)
        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    try {
        ctx.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(ctx, "No hay una app para abrir este archivo", Toast.LENGTH_SHORT).show()
    }
}
