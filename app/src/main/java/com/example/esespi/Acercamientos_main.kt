package com.example.esespi

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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

        btnAgregar.setOnClickListener {
            val intent = Intent(this, Acercamientos_agregar::class.java)
            intent.putExtra("mode", "Agregar")
            startActivityForResult(intent, 1)
        }

        var btnSalir = findViewById<ImageView>(R.id.Acercamientos_Main_btnSalir)
        btnSalir.setOnClickListener {
            finish()
        }

        findViewById<EditText>(R.id.Acercamientos_Main_txtBuscar).addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                buscarInfractores(query)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    @SuppressLint("MissingInflatedId")
    private fun Actualizar(callback: (result: Boolean) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            var conn: Connection? = null
            var xd = false
            try {
                conn = conexionSQL().dbConn() ?: throw SQLException("No se pudo establecer la conexión a la base de datos")
                val statement = conn.createStatement()
                val query = "SELECT * FROM tbAcercamientos ORDER BY IdAcercamiento DESC;"
                val resultSet = statement.executeQuery(query)

                val handler = Handler(Looper.getMainLooper())

                while (resultSet.next()) {
                    val Id = resultSet.getString("IdAcercamiento")
                    val IdInforme = resultSet.getString("IdInforme")
                    val Lugar = resultSet.getString("Lugar")
                    val Fecha = resultSet.getString("Fecha")
                    val NombrePersona = resultSet.getString("NombrePersona")
                    val Acercamiento = resultSet.getString("Acercamiento")

                    handler.post {
                        val cardView = layoutInflater.inflate(R.layout.card_acercamientos_acercamiento, null)

                        val lblNombre = cardView.findViewById<TextView>(R.id.Acercamientos_Card_Acercamiento_lblNombre)
                        val lblId = cardView.findViewById<TextView>(R.id.Acercamientos_Card_Acercamiento_lblID)
                        val btnInfo = cardView.findViewById<LinearLayout>(R.id.Acercamientos_Card_Acercamiento_btnInfo)

                        btnInfo.setOnClickListener {
                            val i = Intent(this@Acercamientos_main, Acercamientos_info::class.java)
                            i.putExtra("id", Id)
                            i.putExtra("inf", IdInforme)
                            i.putExtra("lug", Lugar)
                            i.putExtra("fec", Fecha)
                            i.putExtra("nomp", NombrePersona)
                            i.putExtra("ace", Acercamiento)
                            startActivityForResult(i, 1)
                        }

                        lblNombre.text = NombrePersona
                        lblId.text = Id

                        LlAcercamientos.addView(cardView)
                    }
                }
                xd=true
                resultSet.close()
                statement.close()
            } catch (ex: SQLException) {
                xd=false
                ex.printStackTrace()
            } finally {
                conn?.close()
                callback(xd)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        LlAcercamientos.removeAllViews()
        Actualizar { result ->}
    }

    private fun buscarInfractores(query: String) {
        val queryLowerCase = query.toLowerCase()

        for (i in 0 until LlAcercamientos.childCount) {
            val cardView = LlAcercamientos.getChildAt(i) as ConstraintLayout
            val ID = cardView.findViewById<TextView>(R.id.Acercamientos_Card_Acercamiento_lblID)
            val Nombre = cardView.findViewById<TextView>(R.id.Acercamientos_Card_Acercamiento_lblNombre)

            val id = ID.text.toString().toLowerCase()
            val nombre = Nombre.text.toString().toLowerCase()

            if (id.contains(queryLowerCase) || nombre.contains(queryLowerCase)) {
                cardView.visibility = View.VISIBLE
            } else {
                cardView.visibility = View.GONE
            }
        }
    }

}