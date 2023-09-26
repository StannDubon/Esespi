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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException

private lateinit var lblNombre:TextView
private lateinit var lblFecha:TextView
private lateinit var lblDui:TextView
private lateinit var lblIdInforme:TextView
private lateinit var lblDepartamento:TextView
private lateinit var lblMunicipio:TextView
private lateinit var lblDireccion:TextView

private lateinit var lblTipoDelito:TextView
private lateinit var lblDelito:TextView

private lateinit var llInvolucrados : LinearLayout

private lateinit var btnEliminar : LinearLayout
private lateinit var btnEditar : LinearLayout
private lateinit var btnRegresar : LinearLayout
private lateinit var btnSalir : ImageView

private val SelectedInfractores = ArrayList<Triple<String, String, ByteArray?>>()

class Denuncias_info : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_denuncias_info)

        llInvolucrados = findViewById(R.id.Denuncias_info_llInvolucrados)
    }

    @SuppressLint("MissingInflatedId", "SuspiciousIndentation")
    private fun Actualizar( callback: (result: Boolean) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            var conn: Connection? = null
            var xd = false
            SelectedInfractores.clear()
            try {
                conn = conexionSQL().dbConn() ?: throw SQLException("No se pudo establecer la conexión a la base de datos")
                //Sacamos los datos que mostraremos en la card
                val statement = conn.createStatement()
                val query = "EXEC dbo.VerInfractoresDenuncias @IdDenuncia = ${intent.getStringExtra("IdDetenido").toString()}"
                val resultSet = statement.executeQuery(query)
                val handler = Handler(Looper.getMainLooper())

                //Sacamos los datos que obtuvimos de la busqueda sql
                // Sacamos los datos que obtuvimos de la búsqueda SQL
                while (resultSet.next()) {
                    try {
                        // Obtenemos los valores de la base de datos
                        val Id = resultSet.getString("IdPersona")
                        val Nombre = resultSet.getString("Nombre")
                        val Apellido = resultSet.getString("Apellido")
                        val DUI = resultSet.getString("Dui_Infractor")
                        val Direccion = resultSet.getString("DireccionDomicilio")
                        val IdGenero = resultSet.getInt("IdGenero")
                        val Descripcion = resultSet.getString("Descripcion")
                        val UltimaVezVisto = resultSet.getString("UltimaVezVisto")
                        val Foto: ByteArray? = resultSet.getBytes("Foto")
                        val infractor = Triple(DUI, Nombre, Foto)

                        handler.post {
                            val cardView =
                                layoutInflater.inflate(R.layout.card_infractores_infractor, null)

                            val lblNombre =
                                cardView.findViewById<TextView>(R.id.Infractores_card_infractor_lblNombre)
                            val lblDui =
                                cardView.findViewById<TextView>(R.id.Infractores_card_infractor_lblDui)
                            val imgInfractor =
                                cardView.findViewById<ImageView>(R.id.Infractores_card_infractor_imgInfractor)
                            val btnInfo =
                                cardView.findViewById<LinearLayout>(R.id.Infractores_card_infractor_info)

                            SelectedInfractores.add(infractor)

                            btnInfo.setOnClickListener {
                                val i = Intent(this@Denuncias_info, Infractores_Info::class.java)
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
                            llInvolucrados.addView(cardView)
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

    @SuppressLint("MissingInflatedId", "SuspiciousIndentation")
    private fun PonerDatos( callback: (result: Boolean) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            var conn: Connection? = null
            var xd = false
            SelectedInfractores.clear()
            try {
                conn = conexionSQL().dbConn() ?: throw SQLException("No se pudo establecer la conexión a la base de datos")
                //Sacamos los datos que mostraremos en la card
                val statement = conn.createStatement()
                val query = "EXEC dbo.VerAllOf1Denuncia @IdDenuncia = ${intent.getStringExtra("IdDetenido").toString()}"
                val resultSet = statement.executeQuery(query)
                val handler = Handler(Looper.getMainLooper())

                //Sacamos los datos que obtuvimos de la busqueda sql
                while (resultSet.next()) {
                    try {
                        // Obtenemos los valores de la base de datos
                        val GotId = resultSet.getInt("IdDenuncias")
                        val GotDireccion = resultSet.getString("Lugar")
                        val GotFecha = resultSet.getString("Fecha")
                        val GotDuiDenunciante = resultSet.getString("Dui_Denunciante")
                        val GotIdInforme = resultSet.getString("IdInforme")
                        val GotNombre = resultSet.getString("Nombre")
                        val GotApellido = resultSet.getString("Apellido")

                        val GotTipoDelito = resultSet.getString("Categoria")
                        val GotDelito = resultSet.getString("Delito")

                        handler.post {

                            lblNombre=findViewById(R.id.Denuncias_info_lblNombre)
                            lblFecha=findViewById(R.id.Denuncias_info_lblFecha)
                            lblDui=findViewById(R.id.Denuncias_info_lblDui)
                            lblIdInforme=findViewById(R.id.Denuncias_info_lblIdInforme)
                            lblDepartamento=findViewById(R.id.Denuncias_info_lblDepartamento)
                            lblMunicipio=findViewById(R.id.Denuncias_info_lblMunicipio)
                            lblDireccion=findViewById(R.id.Denuncias_info_lblDireccion)

                            lblTipoDelito=findViewById(R.id.Denuncias_info_lblTipoDelito)
                            lblDelito=findViewById(R.id.Denuncias_info_lblDelito)

                            btnEliminar = findViewById(R.id.Denuncias_info_btnEliminar)
                            btnEditar = findViewById(R.id.Denuncias_info_btnEditar)
                            btnRegresar = findViewById(R.id.Denuncias_info_btnRegresar)
                            btnSalir = findViewById(R.id.Denuncias_info_btnQuit)

                            btnSalir.setOnClickListener {
                                finish()
                            }

                            val DireccionFull = GotDireccion?.split(", ")

                            val departamento = DireccionFull?.get(0)
                            val municipio = DireccionFull?.get(1)
                            val direccion = DireccionFull?.drop(2)?.joinToString(", ")

                            lblNombre.text = "$GotNombre $GotApellido"
                            lblFecha.text = Validaciones().parsearFecha(GotFecha)
                            lblDui.text = GotDuiDenunciante
                            lblIdInforme.text = GotIdInforme
                            lblDepartamento.text = departamento
                            lblMunicipio.text = municipio
                            lblDireccion.text = direccion

                            lblTipoDelito.text = GotTipoDelito
                            lblDelito.text = GotDelito

                            btnEditar.setOnClickListener {
                                val intent = Intent(this@Denuncias_info, Denuncias_agregar::class.java)
                                intent.putExtra("mode", "Editar")

                                intent.putExtra("id", GotId)
                                intent.putExtra("dir", GotDireccion)
                                intent.putExtra("fec", GotFecha)
                                intent.putExtra("dui_denunciante", GotDuiDenunciante)
                                intent.putExtra("infor", GotIdInforme)
                                intent.putExtra("nom", GotNombre)
                                intent.putExtra("ape", GotApellido)

                                intent.putExtra("tipdel", GotTipoDelito)
                                intent.putExtra("delit", GotDelito)

                                val selectedInfractoresParcelable = ArrayList<MyParcelableTriple>()
                                for (triple in SelectedInfractores) {
                                    val myParcelableTriple = MyParcelableTriple(triple.first, triple.second, triple.third)
                                    selectedInfractoresParcelable.add(myParcelableTriple)
                                }

                                intent.putParcelableArrayListExtra("selectedInfractores", selectedInfractoresParcelable)
                                startActivity(intent)
                                finish()
                            }

                            btnEliminar.setOnClickListener {
                                var con = conexionSQL().dbConn()
                                if (con!=null){
                                    try {
                                        val addProducto: PreparedStatement =  con.prepareStatement("EXEC EliminarDenuncia\n" +
                                                "\t@IdDenuncia = ?\n")!!
                                        addProducto.setString(1, intent.getStringExtra("IdDetenido").toString())
                                        addProducto.executeUpdate()
                                        Toast.makeText(this@Denuncias_info, "Eliminado correctamente", Toast.LENGTH_SHORT).show()
                                        con.close()

                                        onBackPressed()
                                    }
                                    catch (ex: SQLException){
                                        Toast.makeText(this@Denuncias_info, "Ocurrio un error: "+ex, Toast.LENGTH_SHORT).show()
                                        println(ex)
                                        con.close()
                                        onBackPressed()
                                    }
                                }
                            }

                            btnRegresar.setOnClickListener {
                                finish()
                            }
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
        llInvolucrados.removeAllViews()
        PonerDatos{}
        Actualizar{}
    }
}