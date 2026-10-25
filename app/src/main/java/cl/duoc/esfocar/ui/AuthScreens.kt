package cl.duoc.esfocar.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun LoginScreen(vm: AuthViewModel, onRegister: () -> Unit) {
    var email by rememberSaveable { mutableStateOf("") }
    var pass by rememberSaveable { mutableStateOf("") }
    val error by vm.error.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { vm.clearError() }

    val emailErr = if (email.isNotEmpty()) Validators.email(email) else null
    val valid = Validators.email(email) == null && pass.isNotEmpty()

    AuthLayout("Iniciar sesión", "Accede a tus documentos de estudio") {
        FormField(email, { email = it; vm.clearError() }, "Correo", emailErr, Icons.Default.Email, keyboard = KeyboardType.Email)
        FormField(pass, { pass = it; vm.clearError() }, "Contraseña", null, Icons.Default.Lock, password = true, imeAction = ImeAction.Done)
        ErrorBanner(error)
        Button(onClick = { vm.login(email, pass) }, enabled = valid, modifier = Modifier.fillMaxWidth()) {
            Text("Ingresar")
        }
        TextButton(onClick = onRegister) { Text("¿No tienes cuenta? Regístrate") }
    }
}

@Composable
fun RegisterScreen(vm: AuthViewModel, onBack: () -> Unit) {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var pass by rememberSaveable { mutableStateOf("") }
    var confirm by rememberSaveable { mutableStateOf("") }
    val error by vm.error.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { vm.clearError() }

    val nameErr = if (name.isNotEmpty()) Validators.name(name) else null
    val emailErr = if (email.isNotEmpty()) Validators.email(email) else null
    val passErr = if (pass.isNotEmpty()) Validators.password(pass) else null
    val confirmErr = if (confirm.isNotEmpty()) Validators.confirm(pass, confirm) else null
    val valid = Validators.name(name) == null && Validators.email(email) == null &&
        Validators.password(pass) == null && Validators.confirm(pass, confirm) == null

    AuthLayout("Crear cuenta", "Usa datos ficticios (MVP académico)") {
        FormField(name, { name = it }, "Nombre", nameErr, Icons.Default.Person)
        FormField(email, { email = it; vm.clearError() }, "Correo", emailErr, Icons.Default.Email, keyboard = KeyboardType.Email)
        FormField(pass, { pass = it }, "Contraseña", passErr, Icons.Default.Lock, password = true)
        FormField(confirm, { confirm = it }, "Repetir contraseña", confirmErr, Icons.Default.Lock, password = true, imeAction = ImeAction.Done)
        ErrorBanner(error)
        Button(onClick = { vm.register(name, email, pass) }, enabled = valid, modifier = Modifier.fillMaxWidth()) {
            Text("Registrarme")
        }
        TextButton(onClick = onBack) { Text("Ya tengo cuenta") }
    }
}
