package cl.duoc.esfocar.ui

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.duoc.esfocar.data.Session

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val UPLOAD = "upload"
}

@Composable
fun AppNav() {
    val nav = rememberNavController()
    val authVm: AuthViewModel = viewModel()
    val docsVm: DocumentsViewModel = viewModel()
    val userId by Session.userId.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val start = remember { if (Session.userId.value != null) Routes.UPLOAD else Routes.REGISTER }

    // Al registrarse o iniciar sesión con una cuenta existente, pasa a la pantalla de carga
    LaunchedEffect(userId) {
        val current = nav.currentDestination?.route
        if (userId != null && (current == Routes.LOGIN || current == Routes.REGISTER)) {
            nav.navigate(Routes.UPLOAD) { popUpTo(0) { inclusive = true } }
        } else if (userId == null && current != Routes.LOGIN && current != Routes.REGISTER) {
            // Cierre de sesión: vuelve al registro (primera pantalla)
            nav.navigate(Routes.REGISTER) { popUpTo(0) { inclusive = true } }
        }
    }

    val msg by docsVm.message.collectAsStateWithLifecycle()
    LaunchedEffect(msg) {
        msg?.let { snackbar.showSnackbar(it); docsVm.clearMessage() }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbar) }) { padding ->
        NavHost(
            navController = nav,
            startDestination = start,
            modifier = Modifier.padding(padding),
            enterTransition = { fadeIn() + slideInHorizontally { it / 10 } },
            exitTransition = { fadeOut() },
            popEnterTransition = { fadeIn() },
            popExitTransition = { fadeOut() }
        ) {
            composable(Routes.REGISTER) {
                RegisterScreen(authVm) {
                    nav.navigate(Routes.LOGIN) { popUpTo(Routes.REGISTER) { inclusive = true } }
                }
            }
            composable(Routes.LOGIN) {
                LoginScreen(authVm) {
                    nav.navigate(Routes.REGISTER) { popUpTo(Routes.LOGIN) { inclusive = true } }
                }
            }
            composable(Routes.UPLOAD) { UploadScreen(docsVm, onLogout = { authVm.logout() }) { } }
        }
    }
}
