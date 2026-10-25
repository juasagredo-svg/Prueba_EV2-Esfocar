package cl.duoc.esfocar.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.MessageDigest
import java.util.UUID

/** Sesión del usuario guardada localmente (SharedPreferences) y expuesta como estado. */
object Session {
    private lateinit var prefs: SharedPreferences
    private val _userId = MutableStateFlow<Long?>(null)
    private val _userName = MutableStateFlow("")
    val userId: StateFlow<Long?> = _userId.asStateFlow()
    val userName: StateFlow<String> = _userName.asStateFlow()

    fun init(context: Context) {
        if (::prefs.isInitialized) return
        prefs = context.applicationContext.getSharedPreferences("session", Context.MODE_PRIVATE)
        val id = prefs.getLong("uid", -1L)
        _userId.value = if (id >= 0) id else null
        _userName.value = prefs.getString("name", "") ?: ""
    }

    fun login(id: Long, name: String) {
        prefs.edit().putLong("uid", id).putString("name", name).apply()
        _userName.value = name
        _userId.value = id
    }

    fun logout() {
        prefs.edit().clear().apply()
        _userId.value = null
        _userName.value = ""
    }
}

object Crypto {
    fun newSalt(): String = UUID.randomUUID().toString()
    fun hash(password: String, salt: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest((salt + password).toByteArray())
            .joinToString("") { "%02x".format(it) }
}
