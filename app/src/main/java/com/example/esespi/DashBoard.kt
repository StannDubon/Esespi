package com.example.esespi

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

private lateinit var AcercamientoCiudadano: LinearLayout
private lateinit var DenunciaCiudadana: LinearLayout
private lateinit var Decomiso: LinearLayout
private lateinit var Infractores: LinearLayout
private lateinit var Detenidos: LinearLayout

var IdGrupoGot:Int=0

val municipiosPorDepartamento = mapOf(
    "Ahuachapán" to arrayOf("Atiquizaya", "El Refugio", "San Lorenzo", "Turín", "Ahuachapán", "Apaneca", "Concepción de Ataco", "Tacuba", "Guaymango", "Jujutla", "San Francisco Menéndez", "San Pedro Puxtla"),
    "Cabañas" to arrayOf("Sensuntepeque", "Ilobasco", "Victoria", "San Isidro", "San Sebastián"),
    "Chalatenango" to arrayOf("Nueva Concepción", "La Palma", "San Ignacio", "Las Vueltas"),
    "Cuscatlán" to arrayOf("Cojutepeque", "Santiago de María", "San Pedro Perulapán", "San Rafael Cedros", "Victoria"),
    "La Libertad" to arrayOf("Santa Tecla", "Antiguo Cuscatlán", "La Libertad", "Colón", "San Juan Opico"),
    "La Paz" to arrayOf("Zacatecoluca", "San Luis Talpa", "Cuyultitán", "San Juan Nonualco", "San Pedro Masahuat"),
    "La Unión" to arrayOf("Conchagua", "El Carmen", "Pasaquina", "Santa Rosa de Lima"),
    "Morazán" to arrayOf("San Francisco Gotera", "Guatajiagua", "Perquín", "Yamabal", "Sociedad"),
    "San Miguel" to arrayOf("Ciudad Barrios", "Carolina", "Chapeltique", "San Rafael Oriente"),
    "San Salvador" to arrayOf("Soyapango", "Delgado", "Mejicanos", "Ayutuxtepeque"),
    "San Vicente" to arrayOf("Apastepeque", "Guadalupe", "San Esteban Catarina", "San Cayetano Istepeque"),
    "Santa Ana" to arrayOf("Chalchuapa", "Metapán", "Coatepeque", "Atiquizaya"),
    "Sonsonate" to arrayOf("Sonzacate", "Acajutla", "Izalco", "Nahuizalco"),
    "Usulután" to arrayOf("Santiago de María", "Jiquilisco", "San Francisco Javier", "Santa Elena")
)
val departamentos = arrayOf(
    "Ahuachapán",
    "Cabañas",
    "Chalatenango",
    "Cuscatlán",
    "La Libertad",
    "La Paz",
    "La Unión",
    "Morazán",
    "San Miguel",
    "San Salvador",
    "San Vicente",
    "Santa Ana",
    "Sonsonate",
    "Usulután"
)

class DashBoard : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash_board)

        AcercamientoCiudadano=findViewById(R.id.DashBoard_btnAcercamientoCiudadano)
        DenunciaCiudadana=findViewById(R.id.DashBoard_btnDenunciaCiudadana)
        Decomiso=findViewById(R.id.DashBoard_btnDecomiso)
        Infractores=findViewById(R.id.DashBoard_btnInfractores)
        Detenidos=findViewById(R.id.DashBoard_btnDetenidos)

        IdGrupoGot = intent.getIntExtra("grupoPatrullaje", 0)

        AcercamientoCiudadano.setOnClickListener {
            val intent = Intent(this, Acercamientos_main::class.java)
            startActivity(intent)
        }

        DenunciaCiudadana.setOnClickListener {
            val intent = Intent(this, Denuncias_main::class.java)
            startActivity(intent)
        }

        Decomiso.setOnClickListener {
            val intent = Intent(this, Decomisos_main::class.java)
            startActivity(intent)
        }

        Infractores.setOnClickListener {
            val intent = Intent(this, Infractores_main::class.java)
            startActivity(intent)
        }

        Detenidos.setOnClickListener {
            val intent = Intent(this, Detenidos_main::class.java)
            startActivity(intent)
        }

    }
}