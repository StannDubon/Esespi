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

private lateinit var btnAgregar:LinearLayout
private lateinit var LlDecomisos:LinearLayout
private lateinit var btnQuit:LinearLayout

class Decomisos_main : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_decomisos_main)

        btnAgregar = findViewById(R.id.Decomisos_main_btnAgregar)
        LlDecomisos = findViewById(R.id.Decomisos_main_LlDecomisos)

        btnAgregar.setOnClickListener{
            val intent = Intent(this, Decomisos_agregar::class.java)
            intent.putExtra("mode", "Agregar")
            startActivity(intent)
        }
    }

    @SuppressLint("MissingInflatedId")
    private fun Actualizar(callback: (result: Boolean) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            var conn: Connection? = null
            var xd = false
            try {
                // Sacamos los datos que mostraremos en la card
                conn = conexionSQL().dbConn()
                    ?: throw SQLException("No se pudo establecer la conexión a la base de datos")
                val statement = conn.createStatement()
                val query = "exec dbo.VerDecomisos;"
                val resultSet = statement.executeQuery(query)
                val handler = Handler(Looper.getMainLooper())

                // Sacamos los datos que obtuvimos de la búsqueda SQL
                while (resultSet.next()) {
                    try {
                        // Obtenemos los valores de la base de datos
                        val Id = resultSet.getString("IdDecomiso")
                        val Detalles = resultSet.getString("Detalles")
                        val Foto: ByteArray? = resultSet.getBytes("FOTO")
                        val TipoDecomiso = resultSet.getString("TipoDecomiso")

                        handler.post {
                            val cardView = layoutInflater.inflate(R.layout.card_decomisos_decomiso, null)

                            val imgDecomiso =
                                cardView.findViewById<ImageView>(R.id.Decomisos_card_decomisos_foto)
                            val lblTipoDecomiso =
                                cardView.findViewById<TextView>(R.id.Decomisos_card_decomisos_lblTipoDecomiso)
                            val btnInfo =
                                cardView.findViewById<LinearLayout>(R.id.Decomisos_card_decomisos_btnInfo)

                            btnInfo.setOnClickListener {
                                val i = Intent(this@Decomisos_main, Decomisos_info::class.java)
                                i.putExtra("id", Id)
                                i.putExtra("det", Detalles)
                                i.putExtra("tip", TipoDecomiso)
                                if (Foto != null && Foto.isNotEmpty()) {
                                    i.putExtra("img", Foto) // Pasar el ByteArray directamente
                                }
                                startActivityForResult(i, 1)
                            }

                            // Definir valores de las cards
                            lblTipoDecomiso.text = Detalles

                            if (Foto != null && Foto.isNotEmpty()) {
                                val bitmap = BitmapFactory.decodeByteArray(Foto, 0, Foto.size)
                                imgDecomiso.setImageBitmap(bitmap)
                            } else {
                                // Si no hay imagen en la base de datos, mostrar una imagen por defecto
                                imgDecomiso.setImageResource(R.drawable.void_image) // Cambia por el recurso de imagen por defecto
                            }

                            // Finalmente añadir la card al LinearLayout
                            LlDecomisos.addView(cardView)
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
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
        LlDecomisos.removeAllViews()
        Actualizar { result -> }
    }
}