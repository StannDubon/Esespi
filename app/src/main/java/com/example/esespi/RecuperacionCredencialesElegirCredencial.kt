package com.example.esespi

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

private lateinit var BtnUsuario:Button
private lateinit var BtnContraseña:Button

class RecuperacionCredencialesElegirCredencial : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperacion_credenciales_elegir_credencial)

        BtnUsuario=findViewById(R.id.RecuperacionCredencialesElegirCredencialesBtnUsuario)
        BtnContraseña=findViewById(R.id.RecuperacionCredencialesElegirCredencialesBtnContraseña)

        BtnUsuario.setOnClickListener {
            val intent = Intent(this, RecuperacionCredencialesRecuperarUsuario::class.java)
            startActivity(intent)
            finish()
        }

        BtnContraseña.setOnClickListener {
            val intent = Intent(this, RecuperacionCredencialesRecuperarContrasena::class.java)
            startActivity(intent)
            finish()
        }
    }
}