package com.example.esespi

import android.app.Application

class MyApp : Application() {
    companion object {
        var selectedDetenidos: ArrayList<MyParcelableTriple>? = null
    }

    override fun onCreate() {
        super.onCreate()
        // Puedes realizar cualquier inicialización adicional aquí si es necesario.
    }
}