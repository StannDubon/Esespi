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

private lateinit var lblTipoDetencion : TextView
private lateinit var lblDescripcion : TextView
private lateinit var imgDecomiso : ImageView
private lateinit var llInvolucrados : LinearLayout

private lateinit var btnEliminar : LinearLayout
private lateinit var btnEditar : LinearLayout
private lateinit var btnRegresar : LinearLayout
private lateinit var btnSalir : ImageView

private val SelectedInfractores = ArrayList<Triple<String, String, ByteArray?>>()

class Decomisos_info : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_decomisos_info)

        lblTipoDetencion = findViewById(R.id.Decomisos_info_lblTipoDetencion)
        lblDescripcion = findViewById(R.id.Decomisos_info_lblDescripcion)
        imgDecomiso = findViewById(R.id.Decomisos_info_imgDecomiso)
        llInvolucrados = findViewById(R.id.Decomisos_info_llInvolucrados)

        btnEliminar = findViewById(R.id.Decomisos_info_btnEliminar)
        btnEditar = findViewById(R.id.Decomisos_info_btnEditar)
        btnRegresar = findViewById(R.id.Decomisos_info_btnRegresar)
        btnSalir = findViewById(R.id.Infractores_info_btnQuit)

        val Id = intent.getStringExtra("id")
        val detalles = intent.getStringExtra("det")
        val tipoDetencion = intent.getStringExtra("tip")
        val Foto = intent.getByteArrayExtra("img")

        if (Id != null) {
            Actualizar(Id){}
        }

        lblTipoDetencion.text = tipoDetencion
        lblDescripcion.text = detalles

        btnSalir.setOnClickListener {
            finish()
        }

        val bitmap = Foto?.let { BitmapFactory.decodeByteArray(Foto, 0, it.size) }
        imgDecomiso.setImageBitmap(bitmap)

        btnEliminar.setOnClickListener {
            var con = conexionSQL().dbConn()
            if (con!=null){
                try {
                    val addProducto: PreparedStatement =  con.prepareStatement("EXEC Eliminar01Decomiso\n" +
                            "\t@IdDecomiso = ?\n")!!
                    addProducto.setString(1, Id)
                    addProducto.executeUpdate()
                    Toast.makeText(this, "Eliminado correctamente", Toast.LENGTH_SHORT).show()
                    con.close()

                    onBackPressed()
                }
                catch (ex: SQLException){
                    Toast.makeText(this, "Ocurrio un error: "+ex, Toast.LENGTH_SHORT).show()
                    println(ex)
                    con.close()
                    onBackPressed()
                }
            }
        }

        btnRegresar.setOnClickListener {
            finish()
        }

        btnEditar.setOnClickListener {
            val intent = Intent(this, Decomisos_agregar::class.java)
            intent.putExtra("mode", "Editar")

            intent.putExtra("id", Id)
            intent.putExtra("det", detalles)
            intent.putExtra("tip", tipoDetencion)
            intent.putExtra("img", Foto)

            val selectedInfractoresParcelable = ArrayList<MyParcelableTriple>()
            for (triple in SelectedInfractores) {
                val myParcelableTriple = MyParcelableTriple(triple.first, triple.second, triple.third)
                selectedInfractoresParcelable.add(myParcelableTriple)
            }

            intent.putParcelableArrayListExtra("selectedDetenidos", selectedInfractoresParcelable)
            startActivity(intent)
            finish()
        }

    }

    override fun onBackPressed() {
        //setResult(Activity.RESULT_OK, Intent())
        super.onBackPressed()
        finish()
    }

    @SuppressLint("MissingInflatedId", "SuspiciousIndentation")
    private fun Actualizar(Id:String, callback: (result: Boolean) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            var conn: Connection? = null
            var xd = false
            SelectedInfractores.clear()
            try {
                conn = conexionSQL().dbConn() ?: throw SQLException("No se pudo establecer la conexión a la base de datos")
                //Sacamos los datos que mostraremos en la card
                val statement = conn.createStatement()
                val query = "EXEC  dbo.VerDetenidosANDDeco  @IdDecomiso = $Id"
                val resultSet = statement.executeQuery(query)
                val handler = Handler(Looper.getMainLooper())

                //Sacamos los datos que obtuvimos de la busqueda sql
                while (resultSet.next()) {
                    try {
                    //Vamo a sacar el id pq asi sabremos cual es la card que queremos eliminar, no se mostrara en la card, pero se guardara
                    val Id = resultSet.getString("IdDetenido")
                    val TipoDetecion = resultSet.getString("Tipo_Detencion")
                    val Nombre = resultSet.getString("Nombre")
                    val Fecha = resultSet.getString("Fecha_Detencion")
                    val Lugar = resultSet.getString("Lugar_Detencion")
                    val Foto: ByteArray? = resultSet.getBytes("Foto")
                    val Dui = resultSet.getString("Dui")
                    val infractor = Triple(Dui, Nombre, Foto)

                        handler.post {
                            val cardView =
                                layoutInflater.inflate(R.layout.detenidos_card_detenido, null)

                            val lblNombre =
                                cardView.findViewById<TextView>(R.id.Detenidos_card_detenido_lblNombre)
                            val lblDui =
                                cardView.findViewById<TextView>(R.id.Detenidos_card_detenido_lblDui)
                            val imgDetenidos =
                                cardView.findViewById<ImageView>(R.id.Detenidos_card_detenido_imgInfractor)
                            val btnInfo =
                                cardView.findViewById<LinearLayout>(R.id.Detenidos_card_detenido_btnInfo)

                            SelectedInfractores.add(infractor)

                            btnInfo.setOnClickListener {
                                val intent = Intent(this@Decomisos_info, Detenidos_info::class.java)
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
}