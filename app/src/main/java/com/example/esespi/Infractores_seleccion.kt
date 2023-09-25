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
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.sql.Connection
import java.sql.SQLException

private var mode:String="noResult"
private val SelectedInfractores = ArrayList<Triple<String, String, ByteArray?>>()
var selectedViewInfractores: LinearLayout? = null

private lateinit var conn: Connection
private lateinit var LlInfractores:LinearLayout
private lateinit var btnGuardar:TextView
private lateinit var txtBuscar:EditText
private lateinit var btnAgregar:LinearLayout

class Infractores_seleccion : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_infractores_seleccion)

        SelectedInfractores.clear()
        mode = intent.getStringExtra("mode").toString()

        txtBuscar = findViewById(R.id.Infractores_Seleccion_txtBusqueda)
        btnAgregar = findViewById(R.id.Infractores_Seleccion_btnAgregar)
        LlInfractores=findViewById(R.id.Infractores_Seleccion_LlInfractores)
        btnGuardar=findViewById(R.id.Infractores_Seleccion_btnGuardar)

        btnAgregar.setOnClickListener {
            val intent = Intent(this, Infractores_agregar::class.java)
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
            val intent = Intent()

            val selectedInfractoresParcelable = ArrayList<MyParcelableTriple>()
            for (triple in SelectedInfractores) {
                val myParcelableTriple = MyParcelableTriple(triple.first, triple.second, triple.third)
                selectedInfractoresParcelable.add(myParcelableTriple)
            }

            intent.putParcelableArrayListExtra("selectedInfractores", selectedInfractoresParcelable)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

    }

    @SuppressLint("MissingInflatedId")
    private fun Actualizar(callback: (result: Boolean) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            var conn: Connection? = null
            var xd = false
            try {
                conn = conexionSQL().dbConn()?: throw SQLException("No se pudo establecer la conexión a la base de datos")
                val statement = conn.createStatement()
                var query = ""
                val handler = Handler(Looper.getMainLooper())

                if(mode=="UnInfractor"){query = "exec dbo.Infractores_NoDetenidos_Visualizar;"}

                else{query = "exec dbo.Infractores_Visualizar;"}


                val resultSet = statement.executeQuery(query)

                //Sacamos los datos que obtuvimos de la busqueda sql
                while (resultSet.next()) {
                    //Vamo a sacar el id pq asi sabremos cual es la card que queremos eliminar, no se mostrara en la card, pero se guardara
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
                            layoutInflater.inflate(R.layout.card_infractores_infractor_select, null)

                        val lblNombre =
                            cardView.findViewById<TextView>(R.id.Infractores_card_infractor_seleccion_lblNombre)
                        val lblDui =
                            cardView.findViewById<TextView>(R.id.Infractores_card_infractor_seleccion_lblDui)
                        val imgInfractor =
                            cardView.findViewById<ImageView>(R.id.Infractores_card_infractor_seleccion_imgInfractor)
                        val btnInfo =
                            cardView.findViewById<LinearLayout>(R.id.Infractores_card_infractor_seleccion_info)

                        val select =
                            cardView.findViewById<LinearLayout>(R.id.Infractores_card_infractor_seleccion_llSelect)
                        select.backgroundTintList =
                            ColorStateList.valueOf(Color.parseColor("#FFFFFF"))

                        imgInfractor.setOnClickListener {
                            val i = Intent(this@Infractores_seleccion, Infractores_Info::class.java)
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

                        if (mode == "UnInfractor") {

                            btnInfo.setOnClickListener {
                                val infractor = Triple(DUI, Nombre, Foto)
                                val isSelected = SelectedInfractores.contains(infractor)

                                if (isSelected) {
                                    SelectedInfractores.remove(infractor)
                                    select.backgroundTintList =
                                        ColorStateList.valueOf(Color.parseColor("#FFFFFF"))
                                    if (selectedViewInfractores == select) {
                                        selectedViewInfractores = null
                                    }
                                } else {
                                    SelectedInfractores.clear()
                                    selectedViewInfractores?.backgroundTintList =
                                        ColorStateList.valueOf(Color.parseColor("#FFFFFF"))
                                    selectedViewInfractores = null

                                    SelectedInfractores.add(infractor)
                                    select.backgroundTintList =
                                        ColorStateList.valueOf(Color.parseColor("#3D96FF"))
                                    selectedViewInfractores = select
                                }
                            }

                        } else if (mode == "MultiInfractor") {

                            btnInfo.setOnClickListener {
                                // Si ya hay una vista seleccionada, restaura su color de fondo a blanco

                                val infractor = Triple(DUI, Nombre, Foto)
                                val isSelected = SelectedInfractores.contains(infractor)

                                if (isSelected) {
                                    // Si el infractor ya está seleccionado, deselecciónalo y establece selectedView en null
                                    SelectedInfractores.remove(infractor)
                                    select.backgroundTintList = ColorStateList.valueOf(
                                        ContextCompat.getColor(
                                            this@Infractores_seleccion,
                                            R.color.white
                                        )
                                    )
                                } else {
                                    // Si el infractor no está seleccionado, selecciónalo y establece el color de fondo
                                    SelectedInfractores.add(infractor)
                                    select.backgroundTintList = ColorStateList.valueOf(
                                        ContextCompat.getColor(
                                            this@Infractores_seleccion,
                                            R.color.colorPrimary
                                        )
                                    )
                                }
                            }
                        }

                        //Definir valores de las cards
                        lblNombre.text = Nombre
                        lblDui.text = DUI

                        if (Foto != null && Foto.isNotEmpty()) {
                            val bitmap = BitmapFactory.decodeByteArray(Foto, 0, Foto.size)
                            imgInfractor.setImageBitmap(bitmap)
                        } else {
                            // Si no hay imagen en la base de datos, mostrar una imagen por defecto
                            imgInfractor.setImageResource(R.drawable.void_image) // Cambia por el recurso de imagen por defecto
                        }

                        //Finalmente sampar la card a el LinearLayout
                        LlInfractores.addView(cardView)
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

    private fun buscarInfractores(query: String) {
        val queryLowerCase = query.toLowerCase()

        for (i in 0 until LlInfractores.childCount) {
            val cardView = LlInfractores.getChildAt(i) as ConstraintLayout
            val nombreView = cardView.findViewById<TextView>(R.id.Infractores_card_infractor_seleccion_lblNombre)
            val duiView = cardView.findViewById<TextView>(R.id.Infractores_card_infractor_seleccion_lblDui)

            val nombre = nombreView.text.toString().toLowerCase()
            val dui = duiView.text.toString().toLowerCase()

            if (nombre.contains(queryLowerCase) || dui.contains(queryLowerCase)) {
                cardView.visibility = View.VISIBLE
            } else {
                cardView.visibility = View.GONE
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                LlInfractores.removeAllViews()
                Actualizar{}
            }
        }
    }

    override fun onResume() {
        super.onResume()
        LlInfractores.removeAllViews()
        Actualizar{}
    }
}