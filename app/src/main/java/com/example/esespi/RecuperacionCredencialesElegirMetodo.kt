package com.example.esespi

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

private lateinit var BtnEmail: Button
private lateinit var BtnPhone: Button


class RecuperacionCredencialesElegirMetodo : AppCompatActivity() {
    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperacion_credenciales_elegir_metodo)

        BtnEmail=findViewById(R.id.RecuperacionCredencialesElegirMetodoBtnEmail)


        BtnEmail.setOnClickListener{
            val intent = Intent(this, RecuperacionCredencialesMailVerificacion::class.java)
            startActivity(intent)
        }
    }
}