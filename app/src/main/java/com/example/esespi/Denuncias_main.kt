package com.example.esespi

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.sql.Connection
import java.sql.SQLException

private lateinit var LlDetenidos:LinearLayout
private lateinit var btnAgregar:LinearLayout
private lateinit var conn: Connection

class Denuncias_main : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_denuncias_main)
        LlDetenidos=findViewById(R.id.Denuncias_main_denucias_cards)
        btnAgregar=findViewById(R.id.Denuncias_main_denuncias_btnAgregar)
        LlDetenidos.removeAllViews()

        btnAgregar.setOnClickListener {
            val intent = Intent(this, Denuncias_agregar::class.java)
            intent.putExtra("mode", "Agregar")
            startActivityForResult(intent, 1)
        }

        var btnSalir = findViewById<ImageView>(R.id.Denuncias_main_btnQuit)
        btnSalir.setOnClickListener {
            finish()
        }
    }

    private fun Actualizar(callback: (result: Boolean) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            var conn: Connection? = null
            var xd = false
            try {
                //Sacamos los datos que mostraremos en la card
                conn = conexionSQL().dbConn() ?: throw SQLException("No se pudo establecer la conexión a la base de datos")
                val statement = conn.createStatement()
                val query = "EXEC dbo.VerDenuncia;"
                val resultSet = statement.executeQuery(query)
                val handler = Handler(Looper.getMainLooper())

                //Sacamos los datos que obtuvimos de la busqueda sql
                while (resultSet.next()) {
                    //Vamo a sacar el id pq asi sabremos cual es la card que queremos eliminar, no se mostrara en la card, pero se guardara
                    val Id = resultSet.getString("IdDenuncias")
                    val Fecha = resultSet.getString("Fecha")
                    val Denunciante = resultSet.getString("Dui_Denunciante")
                    val Nombre = resultSet.getString("Nombre")
                    val Apellido = resultSet.getString("Apellido")

                    handler.post {
                        val cardView =
                            layoutInflater.inflate(R.layout.card_denuncias_denuncia, null)

                        val lblDenunciante =
                            cardView.findViewById<TextView>(R.id.denuncias_card_denuncia_lblDenunciante)
                        val lblFecha =
                            cardView.findViewById<TextView>(R.id.denuncias_card_denuncia_lblFecha)
                        val lblNombre =
                            cardView.findViewById<TextView>(R.id.denuncias_card_denuncia_lblNombre)
                        val btnInfo =
                            cardView.findViewById<LinearLayout>(R.id.denuncias_card_denuncia_btnInfo)

                        btnInfo.setOnClickListener {
                            val intent = Intent(this@Denuncias_main, Denuncias_info::class.java)
                            intent.putExtra("IdDetenido", Id)
                            startActivityForResult(intent, 1)
                        }

                        /*
                        //Definir valores de las cards
                        lblDenunciante.text = "DUI: "+Denunciante
                        lblFecha.text = Validaciones().parsearFecha(Fecha)
                        lblNombre.text = "$Nombre $Apellido"

                         */

                        //Definir valores de las cards
                        lblDenunciante.text = "DUI: "+Denunciante
                        lblFecha.text = Validaciones().parsearFecha(Fecha)
                        lblNombre.text = "$Nombre $Apellido"


                        //Finalmente sampar la card a el LinearLayout
                        LlDetenidos.addView(cardView)
                    }
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

    override fun onResume() {
        super.onResume()
        LlDetenidos.removeAllViews()
        Actualizar{}
    }

}