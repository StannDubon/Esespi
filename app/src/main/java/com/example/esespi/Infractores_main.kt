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

private lateinit var conn: Connection
private lateinit var cardsLayout:LinearLayout

class Infractores_main : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_infractores_main)


        var agregar = findViewById<LinearLayout>(R.id.infractores_main_infractores_btnAgregar)
        agregar.setOnClickListener {
            val intent = Intent(this, Infractores_agregar::class.java)
            intent.putExtra("mode", "Agregar")
            startActivityForResult(intent, 1)
        }

        var btnSalir = findViewById<ImageView>(R.id.infractores_main_btnQuit)
        btnSalir.setOnClickListener {
            finish()
        }

        cardsLayout = findViewById(R.id.infractores_main_infractores_cards)
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
                val query = "exec dbo.Infractores_Visualizar;"
                val resultSet = statement.executeQuery(query)
                val handler = Handler(Looper.getMainLooper())

                // Sacamos los datos que obtuvimos de la búsqueda SQL
                while (resultSet.next()) {
                    try {
                        // Obtenemos los valores de la base de datos
                        val Id = resultSet.getString("IdPersona")
                        val Nombre = resultSet.getString("Nombre")
                        val Apellido = resultSet.getString("Apellido")
                        val DUI = resultSet.getString("Dui")
                        val Direccion = resultSet.getString("DireccionDomicilio")
                        val IdGenero = resultSet.getInt("IdGenero")
                        val Descripcion = resultSet.getString("Descripcion")
                        val UltimaVezVisto = resultSet.getString("UltimaVezVisto")
                        val Foto: ByteArray? = resultSet.getBytes("Foto")

                        handler.post {
                            val cardView =
                                layoutInflater.inflate(R.layout.infractores_card_infractor, null)

                            val lblNombre =
                                cardView.findViewById<TextView>(R.id.Infractores_card_infractor_lblNombre)
                            val lblDui =
                                cardView.findViewById<TextView>(R.id.Infractores_card_infractor_lblDui)
                            val imgInfractor =
                                cardView.findViewById<ImageView>(R.id.Infractores_card_infractor_imgInfractor)
                            val btnInfo =
                                cardView.findViewById<LinearLayout>(R.id.Infractores_card_infractor_info)

                            btnInfo.setOnClickListener {
                                val i = Intent(this@Infractores_main, Infractores_Info::class.java)
                                i.putExtra("id", Id)
                                i.putExtra("nom", Nombre)
                                i.putExtra("ape", Apellido)
                                i.putExtra("dui", DUI)
                                i.putExtra("dir", Direccion)
                                i.putExtra("gen", IdGenero)
                                i.putExtra("des", Descripcion)
                                i.putExtra("las", UltimaVezVisto)
                                if (Foto != null && Foto.isNotEmpty()) {
                                    i.putExtra("img", Foto) // Pasar el ByteArray directamente
                                }
                                startActivityForResult(i, 1)
                            }

                            // Definir valores de las cards
                            lblNombre.text = Nombre
                            lblDui.text = DUI

                            if (Foto != null && Foto.isNotEmpty()) {
                                val bitmap = BitmapFactory.decodeByteArray(Foto, 0, Foto.size)
                                imgInfractor.setImageBitmap(bitmap)
                            } else {
                                // Si no hay imagen en la base de datos, mostrar una imagen por defecto
                                imgInfractor.setImageResource(R.drawable.void_image) // Cambia por el recurso de imagen por defecto
                            }

                            // Finalmente añadir la card al LinearLayout
                            cardsLayout.addView(cardView)
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
            cardsLayout.removeAllViews()
            Actualizar { result -> }
        }
    }
