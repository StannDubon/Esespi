package com.example.esespi

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.graphics.Color
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

private var mode:String="noResult"
private val SelectedInfractores = ArrayList<Triple<String, String, ByteArray?>>()

private lateinit var conn: Connection
private lateinit var LlInfractores:LinearLayout
private lateinit var btnGuardar:TextView
private lateinit var txtBuscar:EditText
private lateinit var btnAgregar:LinearLayout


class Detenidos_seleccion : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detenidos_seleccion)

        SelectedInfractores.clear()
        mode = intent.getStringExtra("mode").toString()

        txtBuscar = findViewById(R.id.Detenidos_Seleccion_txtBusqueda)
        btnAgregar = findViewById(R.id.Detenidos_Seleccion_btnAgregar)
        LlInfractores=findViewById(R.id.Detenidos_Seleccion_LlInfractores)
        btnGuardar=findViewById(R.id.Detenidos_Seleccion_btnGuardar)

        if (intent.hasExtra("selectedDetenidos")) {
            val selectedDetenidos = intent.getParcelableArrayListExtra<MyParcelableTriple>("selectedDetenidos")

            // Ahora, convierte los objetos MyParcelableTriple en Triples y agrégalos a SelectedInfractores
            if (selectedDetenidos != null) {
                for (detenido in selectedDetenidos) {
                    val triple = Triple(detenido.first, detenido.second, detenido.third)
                    SelectedInfractores.add(triple as Triple<String, String, ByteArray?>)
                }
            }
        }

        btnAgregar.setOnClickListener {
            val intent = Intent(this, Detenidos_agregar::class.java)
            intent.putExtra("mode", "Agregar")
            startActivityForResult(intent, 1)
        }

        txtBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                buscarInfractores(query)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        btnGuardar.setOnClickListener {
            val selectedDetenidosTyped = ArrayList<Triple<String, String, ByteArray?>>()
            for (myParcelableTriple in SelectedInfractores) {
                val triple = Triple(myParcelableTriple.first, myParcelableTriple.second, myParcelableTriple.third)
                selectedDetenidosTyped.add(triple)
            }

            val intent = Intent()
            intent.putExtra("selectedDetenidos", selectedDetenidosTyped)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

    }


    @SuppressLint("MissingInflatedId")
    private fun Actualizar(callback: () -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            var conn: Connection? = null
            try {
                conn = conexionSQL().dbConn() ?: throw SQLException("No se pudo establecer la conexión a la base de datos")
                val statement = conn.createStatement()
                val query = "exec dbo.VerDetenidos;"
                val resultSet = statement.executeQuery(query)

                while (resultSet.next()) {
                    val Id = resultSet.getString("IdDetenido")
                    val TipoDetecion = resultSet.getString("Tipo_Detencion")
                    val Nombre = resultSet.getString("Nombre")
                    val Fecha = resultSet.getString("Fecha_Detencion")
                    val Lugar = resultSet.getString("Lugar_Detencion")
                    val Foto: ByteArray? = resultSet.getBytes("Foto")
                    val Dui = resultSet.getString("Dui")

                    val handler = Handler(Looper.getMainLooper())
                    handler.post {
                        val cardView = layoutInflater.inflate(R.layout.detenidos_card_detenido_select, null)

                        val lblNombre = cardView.findViewById<TextView>(R.id.Detenidos_card_detenido_seleccion_lblNombre)
                        val lblDui = cardView.findViewById<TextView>(R.id.Detenidos_card_detenido_seleccion_lblDui)
                        val imgDetenido = cardView.findViewById<ImageView>(R.id.Detenidos_card_detenido_seleccion_imgInfractor)
                        val btnInfo = cardView.findViewById<LinearLayout>(R.id.Detenidos_card_detenido_seleccion_info)
                        val select = cardView.findViewById<LinearLayout>(R.id.Detenidos_card_detenido_seleccion_llSelect)

                        lblNombre.text = Nombre
                        lblDui.text = Dui

                        if (Foto != null && Foto.isNotEmpty()) {
                            val bitmap = BitmapFactory.decodeByteArray(Foto, 0, Foto.size)
                            imgDetenido.setImageBitmap(bitmap)
                        } else {
                            imgDetenido.setImageResource(R.drawable.void_image) // Cambia por el recurso de imagen por defecto
                        }

                        imgDetenido.setOnClickListener {
                            val intent = Intent(this@Detenidos_seleccion, Detenidos_info::class.java)
                            intent.putExtra("IdDetenido", Id)
                            intent.putExtra("TipoDetencion", TipoDetecion)
                            intent.putExtra("Nombre", Nombre)
                            intent.putExtra("Fecha", Fecha)
                            intent.putExtra("LugarDetencion", Lugar)
                            intent.putExtra("Foto", Foto)
                            intent.putExtra("Dui", Dui)
                            startActivityForResult(intent, 1)
                        }


                        /*
                        val isSelected = SelectedInfractores.contains(Triple(Dui, Nombre, Foto))

                        if (isSelected) {
                            select.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#6C4FFF"))
                        }

                         */

                        btnInfo.setOnClickListener {
                            val infractor = Triple(Dui, Nombre, Foto)
                            val isSelected = SelectedInfractores.contains(infractor)

                            if (isSelected) {
                                // Si el infractor ya está seleccionado, deselecciónalo y establece selectedView en null
                                SelectedInfractores.remove(infractor)
                                select.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFFFFF"))
                            } else {
                                // Si el infractor no está seleccionado, selecciónalo y establece el color de fondo
                                SelectedInfractores.add(infractor)
                                select.setBackgroundColor(resources.getColor(R.color.DetenidosSelected))
                            }
                        }

                        //Definir valores de las cards
                        lblNombre.text = Nombre
                        lblDui.text = Dui

                        if (Foto != null && Foto.isNotEmpty()) {
                            val bitmap = BitmapFactory.decodeByteArray(Foto, 0, Foto.size)
                            imgDetenido.setImageBitmap(bitmap)
                        }else {
                            // Si no hay imagen en la base de datos, mostrar una imagen por defecto
                            imgDetenido.setImageResource(R.drawable.void_image) // Cambia por el recurso de imagen por defecto
                        }

                        LlInfractores.addView(cardView)
                    }
                }

                resultSet.close()
                statement.close()
            } catch (ex: SQLException) {
                // Manejo de excepciones en caso de error en la consulta
                ex.printStackTrace()
            } finally {
                conn?.close()
                callback()
            }
        }
    }



    private fun buscarInfractores(query: String) {
        val queryLowerCase = query.toLowerCase()

        for (i in 0 until LlInfractores.childCount) {
            val cardView = LlInfractores.getChildAt(i) as ConstraintLayout
            val nombreView = cardView.findViewById<TextView>(R.id.Detenidos_card_detenido_seleccion_lblNombre)
            val duiView = cardView.findViewById<TextView>(R.id.Detenidos_card_detenido_seleccion_lblDui)

            val nombre = nombreView.text.toString().toLowerCase()
            val dui = duiView.text.toString().toLowerCase()

            if (nombre.contains(queryLowerCase) || dui.contains(queryLowerCase)) {
                cardView.visibility = View.VISIBLE
            } else {
                cardView.visibility = View.GONE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        LlInfractores.removeAllViews()
        Actualizar{}
    }
}




/*
                    if(mode=="UnInfractor" && false){

                        btnInfo.setOnClickListener {
                            val infractor = Triple(Dui, Nombre, Foto)
                            val isSelected = SelectedInfractores.contains(infractor)

                            if (isSelected) {
                                SelectedInfractores.remove(infractor)
                                select.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFFFFF"))
                                if (selectedViewInfractores == select) {
                                    selectedViewInfractores = null
                                }
                            } else {
                                SelectedInfractores.clear()
                                selectedViewInfractores?.backgroundTintList = ColorStateList.valueOf(
                                    Color.parseColor("#FFFFFF"))
                                selectedViewInfractores = null

                                SelectedInfractores.add(infractor)
                                select.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#5FE35B"))
                                selectedViewInfractores = select
                            }
                        }

                    }
                    else{

                        btnInfo.setOnClickListener {
                            val infractor = Triple(Dui, Nombre, Foto)
                            val isSelected = SelectedInfractores.contains(infractor)

                            if (isSelected) {
                                // Si el infractor ya está seleccionado, deselecciónalo y establece selectedView en null
                                SelectedInfractores.remove(infractor)
                                select.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFFFFF"))
                            } else {
                                // Si el infractor no está seleccionado, selecciónalo y establece el color de fondo
                                SelectedInfractores.add(infractor)
                                select.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#5FE35B"))
                            }
                        }
                    }
 */