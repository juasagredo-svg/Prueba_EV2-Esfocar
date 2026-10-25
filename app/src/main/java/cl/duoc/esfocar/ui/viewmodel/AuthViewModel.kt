package cl.duoc.esfocar.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.esfocar.data.AppDatabase
import cl.duoc.esfocar.data.Crypto
import cl.duoc.esfocar.data.Session
import cl.duoc.esfocar.data.UserEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = AppDatabase.get(app).userDao()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun clearError() { _error.value = null }

    fun logout() = Session.logout()

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            val e = email.trim().lowercase()
            if (dao.findByEmail(e) != null) {
                _error.value = "Ese correo ya está registrado"
                return@launch
            }
            val salt = Crypto.newSalt()
            val id = dao.insert(
                UserEntity(name = name.trim(), email = e,
                    passwordHash = Crypto.hash(password, salt), salt = salt)
            )
            _error.value = null
            Session.login(id, name.trim())
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val user = dao.findByEmail(email.trim().lowercase())
            if (user == null || user.passwordHash != Crypto.hash(password, user.salt)) {
                _error.value = "Correo o contraseña incorrectos"
            } else {
                _error.value = null
                Session.login(user.id, user.name)
            }
        }
    }
}
