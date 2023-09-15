package com.example.esespi

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

private lateinit var BtnEmail: Button
private lateinit var BtnPhone: Button

class RecuperacionCredencialesElegirMetodo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperacion_credenciales_elegir_metodo)

        BtnEmail=findViewById(R.id.RecuperacionCredencialesElegirMetodoBtnEmail)
        BtnPhone=findViewById(R.id.RecuperacionCredencialesElegirMetodoBtnPhone)

        BtnEmail.setOnClickListener{
            val intent = Intent(this@RecuperacionCredencialesElegirMetodo, RecuperacionCredencialesMailVerificacion::class.java)
            startActivity(intent)
            finish()
        }
    }
}