package com.example.esespi

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.sql.Connection
import java.sql.SQLException



private lateinit var btnQuit:ImageView

private lateinit var btnTomarFoto:LinearLayout
private lateinit var btnSubirFoto:LinearLayout
private lateinit var btnQuitarFoto:LinearLayout

private lateinit var imgDecomiso:ImageView

private lateinit var txtDescripcion:EditText
private lateinit var dbTipo:Spinner

private lateinit var LlInfractoresSeleccionados:LinearLayout
private lateinit var LlAgregarInvolucrado:LinearLayout

private lateinit var btnDescartar:LinearLayout
private lateinit var btnGuardar:LinearLayout

private var foto: ByteArray? = null

private lateinit var conn: Connection

private val PICK_IMAGE_REQUEST = 1
private val REQUEST_IMAGE_CAPTURE = 2

private var INFRACTORES_REQUEST = 3
private var SelectedDetenidos: ArrayList<Triple<String, String, ByteArray?>> = ArrayList()

class Decomisos_agregar : AppCompatActivity() {
    @SuppressLint("MissingInflatedId", "ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_decomisos_agregar)

        conn = conexionSQL().dbConn() ?: throw SQLException("No se pudo establecer la conexión a la base de datos")

        btnQuit = findViewById(R.id.Decomisos_agregar_btnQuitTop)
        dbTipo=findViewById(R.id.Decomisos_agregar_spDecomiso)

        btnTomarFoto = findViewById(R.id.Decomisos_agregar_btnTomarFoto)
        btnSubirFoto = findViewById(R.id.Decomisos_agregar_btnSubirFoto)
        imgDecomiso = findViewById(R.id.Decomisos_agregar_imgDecomiso)

        txtDescripcion = findViewById(R.id.Decomisos_agregar_txtDescripcion)

        LlInfractoresSeleccionados = findViewById(R.id.Decomisos_agregar_LlInfractoresSelected)
        LlAgregarInvolucrado = findViewById(R.id.Decomisos_agregar_btnSeleccionarDetenido)

        btnDescartar = findViewById(R.id.Decomisos_agregar_btnDescartar)
        btnGuardar = findViewById(R.id.Decomisos_agregar_btnGuardar)
        btnQuitarFoto = findViewById(R.id.Decomisos_agregar_btnQuitarFoto)

// FOTO --------------------------------------------------------------------------------------------

        btnSubirFoto.setOnClickListener {
            getIMG(this, false)
        }
        btnTomarFoto.setOnClickListener {
            getIMG(this, true)
        }
        btnQuitarFoto.setOnClickListener {
            imgDecomiso.setImageResource(R.drawable.void_image)
            foto=null
        }

// SPINNER -----------------------------------------------------------------------------------------

        val clasificaciones = ArrayList<String>()
        try {
            val statement = conn.createStatement()
            val query = "SELECT * FROM tbTiposDecomiso"
            val resultSet = statement?.executeQuery(query)

            clasificaciones.add("Seleccione una opcion...")

            while (resultSet?.next() == true) {
                val clasificacion = resultSet.getString("TipoDecomiso")
                clasificaciones.add(clasificacion)
            }

            val adapter = ArrayAdapter(this, R.drawable.custom_spinner_adapter, clasificaciones)

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            dbTipo.adapter = adapter
            resultSet?.close()
            statement?.close()
        } catch (ex: SQLException) {
            println(ex.message)
        }

// AÑADIR INVOLUCRADOS -----------------------------------------------------------------------------

        LlAgregarInvolucrado.setOnClickListener{
            val intent = Intent(this, Detenidos_seleccion::class.java)
            intent.putExtra("mode", "MasDeUno")
            startActivityForResult(intent, INFRACTORES_REQUEST)
        }

    }

    private fun getIMG(activity: Activity, capturarFoto: Boolean) {
        val intent: Intent
        if (capturarFoto) {
            intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        } else {
            //Y en este caso, se abrira el explorador de archivos
            intent = Intent(Intent.ACTION_GET_CONTENT)
            //En el cual solo podra seleccionar imagenes
            intent.type = "image/*"
        }
        //Luego, se ejecuta un /Intent for activity result/ El cual como su nombre indica
        //Esque va a abrir un intent el cual si o si va a tener que devolver algo, en este caso, le pasamos de parametro tambien un if
        //Que indica que si escogio capturar la foto, va a tomar como parametro de recuperacion del dato como un /1/ ya que antes defnimos que "REQUEST_IMAGE_CAPTURE" = /1/
        //Y si escogio NO capturar la foto, sino que tomara de archvos, pasara como parametro de recuperacion un /2/ ya que antes definimos que "PICK_IMAGE_REQUEST" = /2/
        activity.startActivityForResult(Intent.createChooser(intent, "Seleccionar imagen"), if (capturarFoto) REQUEST_IMAGE_CAPTURE else PICK_IMAGE_REQUEST)
    }

    @SuppressLint("Recycle")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //Si el parametro de recuperacion es el mismo que el de tomar foto, y se haya detectado que se volvio de una activity siguiente, a
        //La activity acual, se ejecutara el siguiente cogigo
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //Se obtiente la imagen que se tomo como un parametro extra con el nombre de referencia "data" y se convierte a bitmaps
            val imageBitmap = data?.extras?.get("data") as Bitmap
            // Obtener el arreglo de bytes de la imagen
            val byteArrayOutputStream = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val imageByteArray = byteArrayOutputStream.toByteArray()
            // Asignar el arreglo de bytes a la variable 'foto'
            foto = imageByteArray
            // Mostrar la imagen en el ImageView
            imgDecomiso.setImageBitmap(imageBitmap)
        }

        else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            //se obtiene e Uri de la imagen que escogimos en los archivos
            val imageUri = data.data
            // Obtener el arreglo de bytes de la imagen seleccionada
            val inputStream = contentResolver.openInputStream(imageUri!!)
            val imageByteArray = inputStream?.readBytes()
            // Asignar el arreglo de bytes a la variable 'foto'
            if (imageByteArray != null) {
                foto = imageByteArray
            }
            // Mostrar la imagen en el ImageView
            imgDecomiso.setImageURI(imageUri)
        }

        else if (requestCode == INFRACTORES_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null && data.hasExtra("selectedDetenidos")) {

                val selectedDetenidos = data.getParcelableArrayListExtra<MyParcelableTriple>("selectedDetenidos")
                MyApp.selectedDetenidos = selectedDetenidos
                LlInfractoresSeleccionados.removeAllViews()

                GlobalScope.launch(Dispatchers.IO) {
                    selectedDetenidos?.forEach { item ->
                        val DUI = item.first
                        val Nombre = item.second
                        val Foto = item.third

                        val handler = Handler(Looper.getMainLooper())
                        handler.post {
                            // Infla una CardView
                            val cardView = layoutInflater.inflate(R.layout.detenidos_card_detenido_select, null)

                            val lblNombre = cardView.findViewById<TextView>(R.id.Detenidos_card_detenido_seleccion_lblNombre)
                            val lblDui = cardView.findViewById<TextView>(R.id.Detenidos_card_detenido_seleccion_lblDui)
                            val imgInfractor = cardView.findViewById<ImageView>(R.id.Detenidos_card_detenido_seleccion_imgInfractor)

                            // Definir valores de las cards
                            lblNombre.text = Nombre
                            lblDui.text = DUI

                            if (Foto != null && Foto.isNotEmpty()) {
                                val bitmap = BitmapFactory.decodeByteArray(Foto, 0, Foto.size)
                                imgInfractor.setImageBitmap(bitmap)
                            } else {
                                // Si no hay imagen en el array, mostrar una imagen por defecto
                                imgInfractor.setImageResource(R.drawable.void_image) // Cambia por el recurso de imagen por defecto
                            }

                            // Finalmente añadir la card al LinearLayout
                            LlInfractoresSeleccionados.addView(cardView)
                        }
                    }
                }
            }
        }
    }
}