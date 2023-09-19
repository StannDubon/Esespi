package com.example.esespi

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import java.sql.Connection
import java.sql.SQLException

private var mode:String="noResult"
private val SelectedInfractores = ArrayList<Triple<String, String, ByteArray?>>()
var selectedViewDetenidos: LinearLayout? = null

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
        Actualizar()

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

            intent.putParcelableArrayListExtra("selectedDetenidos", selectedInfractoresParcelable)
            setResult(Activity.RESULT_OK, intent)
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
                val query = "exec dbo.VerDetenidos;"
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

                    val cardView = layoutInflater.inflate(R.layout.detenidos_card_detenido_select, null)

                    val lblNombre = cardView.findViewById<TextView>(R.id.Detenidos_card_detenido_seleccion_lblNombre)
                    val lblDui = cardView.findViewById<TextView>(R.id.Detenidos_card_detenido_seleccion_lblDui)
                    val imgDetenido = cardView.findViewById<ImageView>(R.id.Detenidos_card_detenido_seleccion_imgInfractor)
                    val btnInfo = cardView.findViewById<LinearLayout>(R.id.Detenidos_card_detenido_seleccion_info)

                    val select = cardView.findViewById<LinearLayout>(R.id.Detenidos_card_detenido_seleccion_llSelect)
                    select.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFFFFF"))


                    imgDetenido.setOnClickListener {
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

                    //Finalmente sampar la card a el LinearLayout
                    LlInfractores.addView(cardView)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                LlInfractores.removeAllViews()
                Actualizar()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        LlInfractores.removeAllViews()
        Actualizar()
    }
}