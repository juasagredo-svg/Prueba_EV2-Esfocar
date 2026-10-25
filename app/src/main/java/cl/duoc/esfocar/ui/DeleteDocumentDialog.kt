package cl.duoc.esfocar.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import cl.duoc.esfocar.data.DocumentEntity

@Composable
fun DeleteDocumentDialog(doc: DocumentEntity, vm: DocumentsViewModel, onDismiss: () -> Unit) {
    val haptic = LocalHapticFeedback.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Eliminar documento") },
        text = { Text("¿Eliminar \"${doc.title}\"? Esta acción no se puede deshacer.") },
        confirmButton = {
            TextButton(onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                vm.delete(doc)
                onDismiss()
            }) { Text("Eliminar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
