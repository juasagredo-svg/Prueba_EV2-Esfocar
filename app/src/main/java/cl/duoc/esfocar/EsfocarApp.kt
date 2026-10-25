package cl.duoc.esfocar

import android.app.Application
import cl.duoc.esfocar.data.Session

class EsfocarApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Session.init(this)
    }
}
