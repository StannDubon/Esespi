package com.example.esespi

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.sql.Connection
import java.sql.SQLException

private lateinit var LlAcercamientos:LinearLayout
private lateinit var btnAgregar:LinearLayout
private lateinit var conn: Connection


class Acercamientos_main : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acercamientos_main)

        LlAcercamientos=findViewById(R.id.Acercamientos_Main_LlAcercamiento)
        btnAgregar=findViewById(R.id.Acercamientos_Main_btnAgregar)
        LlAcercamientos.removeAllViews()
        Actualizar()

        btnAgregar.setOnClickListener {
            val intent = Intent(this, Acercamientos_agregar::class.java)
            intent.putExtra("mode", "Agregar")
            startActivityForResult(intent, 1)
        }

        var btnSalir = findViewById<ImageView>(R.id.Acercamientos_Main_btnSalir)
        btnSalir.setOnClickListener {
            finish()
        }
    }

    @SuppressLint("MissingInflatedId")
    private fun Actualizar(){
        conn = conexionSQL().dbConn() ?: throw SQLException("No se pudo establecer la conexión a la base de datos")
        if (conn != null) {
            try {
                //Sacamos los datos que mostraremos en la card
                val statement = conn.createStatement()
                val query = "SELECT * FROM tbAcercamientos ORDER BY IdAcercamiento DESC;"
                val resultSet = statement.executeQuery(query)

                //Sacamos los datos que obtuvimos de la busqueda sql
                while (resultSet.next()) {
                    //Vamo a sacar el id pq asi sabremos cual es la card que queremos eliminar, no se mostrara en la card, pero se guardara
                    val Id = resultSet.getString("IdAcercamiento")
                    val IdInforme = resultSet.getString("IdInforme")
                    val Lugar = resultSet.getString("Lugar")
                    val Fecha = resultSet.getString("Fecha")
                    val NombrePersona = resultSet.getString("NombrePersona")
                    val Acercamiento = resultSet.getString("Acercamiento")

                    val cardView = layoutInflater.inflate(R.layout.acercamientos_card_acercamiento, null)

                    val lblNombre = cardView.findViewById<TextView>(R.id.Acercamientos_Card_Acercamiento_lblNombre)
                    val lblId = cardView.findViewById<TextView>(R.id.Acercamientos_Card_Acercamiento_lblID)
                    val btnInfo = cardView.findViewById<LinearLayout>(R.id.Acercamientos_Card_Acercamiento_btnInfo)

                    btnInfo.setOnClickListener {
                        val i = Intent(this, Acercamientos_info::class.java)
                        i.putExtra("id", Id)
                        i.putExtra("inf", IdInforme)
                        i.putExtra("lug", Lugar)
                        i.putExtra("fec", Fecha)
                        i.putExtra("nomp", NombrePersona)
                        i.putExtra("ace", Acercamiento)
                        startActivityForResult(i, 1)
                    }

                    //Definir valores de las cards
                    lblNombre.text = NombrePersona
                    lblId.text = Id

                    //Finalmente sampar la card a el LinearLayout
                    LlAcercamientos.addView(cardView)
                }

                resultSet.close()
                statement.close()
                conn.close()
            } catch (ex: SQLException) {
                // Manejo de excepciones en caso de error en la consulta
                ex.printStackTrace()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                LlAcercamientos.removeAllViews()
                Actualizar()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        LlAcercamientos.removeAllViews()
        Actualizar()
    }

}