package com.example.esespi

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
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

class Detenidos_main : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detenidos_main)

        LlDetenidos=findViewById(R.id.Detenidos_Main_LlAcercamiento)
        btnAgregar=findViewById(R.id.Detenidos_Main_btnAgregar)
        LlDetenidos.removeAllViews()

        btnAgregar.setOnClickListener {
            val intent = Intent(this, Detenidos_agregar::class.java)
            intent.putExtra("mode", "Agregar")
            startActivityForResult(intent, 1)
        }

        var btnSalir = findViewById<ImageView>(R.id.Detenidos_Main_btnSalir)
        btnSalir.setOnClickListener {
            finish()
        }
    }

    @SuppressLint("MissingInflatedId")
    private fun Actualizar(callback: (result: Boolean) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            var conn: Connection? = null
            var xd = false
            try {
                conn = conexionSQL().dbConn() ?: throw SQLException("No se pudo establecer la conexión a la base de datos")
                val statement = conn.createStatement()
                val query = "EXEC dbo.VerDetenidos;"
                val resultSet = statement.executeQuery(query)
                val handler = Handler(Looper.getMainLooper())

                //Sacamos los datos que obtuvimos de la busqueda sql
                while (resultSet.next()) {
                    //Vamo a sacar el id pq asi sabremos cual es la card que queremos eliminar, no se mostrara en la card, pero se guardara
                    val Id = resultSet.getString("IdDetenido")
                    val TipoDetecion = resultSet.getString("Tipo_Detencion")
                    val Nombre = resultSet.getString("Nombre")
                    val Fecha = resultSet.getString("Fecha_Detencion")
                    val Lugar = resultSet.getString("Lugar_Detencion")
                    val Foto: ByteArray? = resultSet.getBytes("Foto")
                    val Dui = resultSet.getString("Dui")

                    handler.post {
                        val cardView =
                            layoutInflater.inflate(R.layout.card_detenidos_detenido, null)

                        val lblNombre =
                            cardView.findViewById<TextView>(R.id.Detenidos_card_detenido_lblNombre)
                        val lblDui =
                            cardView.findViewById<TextView>(R.id.Detenidos_card_detenido_lblDui)
                        val imgDetenidos =
                            cardView.findViewById<ImageView>(R.id.Detenidos_card_detenido_imgInfractor)
                        val btnInfo =
                            cardView.findViewById<LinearLayout>(R.id.Detenidos_card_detenido_btnInfo)

                        btnInfo.setOnClickListener {
                            val intent = Intent(this@Detenidos_main, Detenidos_info::class.java)
                            intent.putExtra("IdDetenido", Id)
                            intent.putExtra("TipoDetencion", TipoDetecion)
                            intent.putExtra("Nombre", Nombre)
                            intent.putExtra("Fecha", Fecha)
                            intent.putExtra("LugarDetencion", Lugar)
                            intent.putExtra("Foto", Foto)
                            intent.putExtra("Dui", Dui)
                            startActivityForResult(intent, 1)
                        }

                        //Definir valores de las cards
                        lblNombre.text = Nombre
                        lblDui.text = Dui

                        if (Foto != null && Foto.isNotEmpty()) {
                            val bitmap = BitmapFactory.decodeByteArray(Foto, 0, Foto.size)
                            imgDetenidos.setImageBitmap(bitmap)
                        } else {
                            // Si no hay imagen en la base de datos, mostrar una imagen por defecto
                            imgDetenidos.setImageResource(R.drawable.void_image) // Cambia por el recurso de imagen por defecto
                        }


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