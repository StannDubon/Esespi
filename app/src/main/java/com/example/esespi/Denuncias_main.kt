package com.example.esespi

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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
        Actualizar()

        btnAgregar.setOnClickListener {
            val intent = Intent(this, Acercamientos_agregar::class.java)
            intent.putExtra("mode", "Agregar")
            startActivityForResult(intent, 1)
        }

        var btnSalir = findViewById<ImageView>(R.id.Denuncias_main_btnQuit)
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
                val query = "EXEC dbo.VerDenuncia;"
                val resultSet = statement.executeQuery(query)

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

                    val cardView = layoutInflater.inflate(R.layout.detenidos_card_detenido, null)

                    val lblNombre = cardView.findViewById<TextView>(R.id.Detenidos_card_detenido_lblNombre)
                    val lblDui = cardView.findViewById<TextView>(R.id.Detenidos_card_detenido_lblDui)
                    val imgDetenidos = cardView.findViewById<ImageView>(R.id.Detenidos_card_detenido_imgInfractor)
                    val btnInfo = cardView.findViewById<LinearLayout>(R.id.Detenidos_card_detenido_btnInfo)

                    btnInfo.setOnClickListener {
                        val intent = Intent(this, Detenidos_info::class.java)
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
                LlDetenidos.removeAllViews()
                Actualizar()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        LlDetenidos.removeAllViews()
        Actualizar()
    }

}