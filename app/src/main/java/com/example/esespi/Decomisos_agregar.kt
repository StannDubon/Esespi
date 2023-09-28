package com.example.esespi

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException

private lateinit var btnQuit: ImageView

private lateinit var btnTomarFoto: LinearLayout
private lateinit var btnSubirFoto: LinearLayout
private lateinit var btnQuitarFoto: LinearLayout

private lateinit var imgDecomiso: ImageView

private lateinit var txtDescripcion: EditText
private lateinit var dbTipo: Spinner

private lateinit var LlInfractoresSeleccionados: LinearLayout
private lateinit var LlAgregarInvolucrado: LinearLayout

private lateinit var btnDescartar: LinearLayout
private lateinit var btnGuardar: LinearLayout

private var foto: ByteArray? = null

private lateinit var conn: Connection

private var ActivityMode: String? = null

private val PICK_IMAGE_REQUEST = 1
private val REQUEST_IMAGE_CAPTURE = 2

private var INFRACTORES_REQUEST = 3
private var SelectedDetenidos: ArrayList<Triple<String, String, ByteArray?>> = ArrayList()

class Decomisos_agregar : AppCompatActivity() {
    @SuppressLint("MissingInflatedId", "ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_decomisos_agregar)

        ActivityMode = intent.getStringExtra("mode")

        conn = conexionSQL().dbConn() ?: throw SQLException("No se pudo establecer la conexión a la base de datos")

        btnQuit = findViewById(R.id.Decomisos_agregar_btnQuitTop)
        dbTipo = findViewById(R.id.Decomisos_agregar_spDecomiso)

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
            getIMG(false)
        }
        btnTomarFoto.setOnClickListener {
            getIMG(true)
        }
        btnQuitarFoto.setOnClickListener {
            imgDecomiso.setImageResource(R.drawable.void_image)
            foto = null
        }

        findViewById<ImageView>(R.id.Decomisos_agregar_btnQuitTop).setOnClickListener {
            finish()
        }

// SPINNER -----------------------------------------------------------------------------------------

        val clasificaciones = ArrayList<String>()
        try {
            val statement = conn.createStatement()
            val query = "SELECT * FROM tbTiposDecomiso"
            val resultSet = statement?.executeQuery(query)

            clasificaciones.add("Seleccione una opción...")

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

        LlAgregarInvolucrado.setOnClickListener {
            val intent = Intent(this, Detenidos_seleccion::class.java)
            intent.putExtra("mode", "MasDeUno")

            // Verifica si hay elementos en SelectedDetenidos antes de pasarlo como un extra
            if (SelectedDetenidos.isNotEmpty()) {
                // Convierte SelectedDetenidos a una lista de Parcelable si es necesario
                val selectedDetenidosParcelable = ArrayList<MyParcelableTriple>()
                for (triple in SelectedDetenidos) {
                    selectedDetenidosParcelable.add(MyParcelableTriple(triple.first, triple.second, triple.third))
                }
                intent.putParcelableArrayListExtra("selectedDetenidos", selectedDetenidosParcelable)
            }

            startActivityForResult(intent, INFRACTORES_REQUEST)
        }

//SUBIR A LA BASE DE DATOS ---------------------------------------------------------------------------

        if(ActivityMode=="Agregar") {
            btnGuardar.setOnClickListener {
                for (triple in SelectedDetenidos) {
                    if (
                        dbTipo.selectedItemPosition != 0
                    ) {
                        try {
                            println("No")
                            val addProducto: PreparedStatement = conn.prepareStatement(
                                "EXEC dbo.InsertarDecomisos1\n" +
                                        "\t@DetallesDecomiso = ?,\n" +
                                        "\t@TipoDecomiso = ?,\n" +
                                        "\t@DuiInfractor_Detenido = ?," +
                                        "\t@FOTO = ?\n"
                            )

                            addProducto.setString(1, txtDescripcion.text.toString())
                            addProducto.setString(2, dbTipo.selectedItem.toString())
                            addProducto.setString(3, triple.first)
                            addProducto.setBytes(4, foto)
                            addProducto.executeUpdate()

                            Toast.makeText(
                                this,
                                "Se ha registrado correctamente",
                                Toast.LENGTH_SHORT
                            ).show()

                            setResult(RESULT_OK, Intent())
                            finish()
                        } catch (ex: SQLException) {
                            Toast.makeText(this, "Error al ingresar: " + ex, Toast.LENGTH_SHORT)
                                .show()
                            println(ex)
                            setResult(RESULT_OK, Intent())
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "Por favor complete todos los campos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                conn.close()
            }
        }

        else if (ActivityMode=="Editar"){

            val GotId = intent.getStringExtra("id")
            val GotDetalles = intent.getStringExtra("det")
            val GotTipoDecomiso = intent.getStringExtra("tip")
            val GotFoto = intent.getByteArrayExtra("img")

            txtDescripcion.setText(GotDetalles)

            val indiceSeleccion = clasificaciones.indexOf(GotTipoDecomiso)

            if (indiceSeleccion != -1) {
                // Si se encontró el texto, establecerlo como la selección actual del Spinner
                dbTipo.setSelection(indiceSeleccion)
            }

            val bitmap = GotFoto?.let { BitmapFactory.decodeByteArray(GotFoto, 0, it.size) }
            imgDecomiso.setImageBitmap(bitmap)
            foto = GotFoto

            val selectedInfractores = intent.getParcelableArrayListExtra<MyParcelableTriple>("selectedDetenidos")
            LlInfractoresSeleccionados.removeAllViews()

            val convertedList = ArrayList<Triple<String, String, ByteArray?>>()

            selectedInfractores?.forEach { item ->
                val triple = Triple(item.first, item.second, item.third)
                convertedList.add(triple as Triple<String, String, ByteArray?>)
            }


            // Asigna la lista convertida a la variable global
            SelectedDetenidos = convertedList

            for (item in SelectedDetenidos) {
                val DUI = item.first
                val Nombre = item.second
                val Foto = item.third

                // Crear una vista para cada detenido seleccionado
                val cardView = layoutInflater.inflate(R.layout.card_detenidos_detenido_select, null)

                val lblNombre = cardView.findViewById<TextView>(R.id.Detenidos_card_detenido_seleccion_lblNombre)
                val lblDui = cardView.findViewById<TextView>(R.id.Detenidos_card_detenido_seleccion_lblDui)
                val imgInfractor = cardView.findViewById<ImageView>(R.id.Detenidos_card_detenido_seleccion_imgInfractor)
                val btnDel = cardView.findViewById<LinearLayout>(R.id.Detenidos_card_detenido_seleccion_info)

                cardView.findViewById<LinearLayout>(R.id.Detenidos_card_detenido_seleccion_llSelect).backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(this@Decomisos_agregar, R.color.DetenidosSelected))

                btnDel.setOnClickListener{
                    LlInfractoresSeleccionados.removeView(cardView)
                    SelectedDetenidos.remove(item)
                }

                lblNombre.text = Nombre
                lblDui.text = DUI

                if (Foto != null && Foto.isNotEmpty()) {
                    val bitmap = BitmapFactory.decodeByteArray(Foto, 0, Foto.size)
                    imgInfractor.setImageBitmap(bitmap)
                } else {
                    imgInfractor.setImageResource(R.drawable.void_image)
                }

                // Agregar la vista al contenedor
                LlInfractoresSeleccionados.addView(cardView)
            }

            btnGuardar.setOnClickListener {
                if (
                    true
                ){

                    try {
                        val addProducto: PreparedStatement =  conn.prepareStatement(
                            "EXEC dbo.ActualizarYEliminarInvolucradosDecomiso\n" +
                                    "@IdDecomiso = ?,\n" +
                                    "@Detalles = ?,\n" +
                                    "@Foto = ?,\n" +
                                    "@TipoDecomiso = ?"
                        )!!

                        addProducto.setString(1, GotId)
                        addProducto.setString(2, txtDescripcion.text.toString())
                        addProducto.setBytes(3, foto)
                        addProducto.setString(4, dbTipo.selectedItem.toString())
                        addProducto.executeUpdate()

                        for (triple in SelectedDetenidos){
                            val addProductoxd: PreparedStatement =  conn.prepareStatement(
                                "EXEC dbo.ActualizarInvolucradosDecomiso \n" +
                                        "@IdDecomiso = ?,\n" +
                                        "@DuiDetendio = ?"
                            )!!

                            addProductoxd.setString(1, GotId)
                            addProductoxd.setString(2, triple.first)
                            addProductoxd.executeUpdate()
                        }

                        Toast.makeText(this, "Se ha actualizado correctamente", Toast.LENGTH_SHORT).show()

                        setResult(RESULT_OK, Intent())
                        conn.close()
                        finish()
                    }
                    catch (ex: SQLException){
                        Toast.makeText(this, "Error al ingresar: "+ex, Toast.LENGTH_SHORT).show()
                        println(ex)
                        setResult(RESULT_OK, Intent())
                        finish()
                    }
                } else{
                    Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun getIMG(capturarFoto: Boolean) {
        val intent: Intent
        if (capturarFoto) {
            intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        } else {
            intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
        }
        startActivityForResult(Intent.createChooser(intent, "Seleccionar imagen"), if (capturarFoto) REQUEST_IMAGE_CAPTURE else PICK_IMAGE_REQUEST)
    }

    @SuppressLint("Recycle")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            val byteArrayOutputStream = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val imageByteArray = byteArrayOutputStream.toByteArray()
            foto = imageByteArray
            imgDecomiso.setImageBitmap(imageBitmap)
        } else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            val imageUri = data.data
            val inputStream = contentResolver.openInputStream(imageUri!!)
            val imageByteArray = inputStream?.readBytes()
            if (imageByteArray != null) {
                foto = imageByteArray
            }
            imgDecomiso.setImageURI(imageUri)
        }

        else if (requestCode == INFRACTORES_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null && data.hasExtra("selectedDetenidos")) {
                val selectedDetenidos = data.getSerializableExtra("selectedDetenidos") as? ArrayList<Triple<String, String, ByteArray?>>

                if (selectedDetenidos != null) {

                    val uniqueMap = mutableMapOf<String, Triple<String, String, ByteArray?>>()

                    selectedDetenidos.forEach { triple ->
                        val key = triple.first
                        if (!uniqueMap.containsKey(key)) {
                            uniqueMap[key] = triple
                        }
                    }

                    LlInfractoresSeleccionados.removeAllViews()
                    SelectedDetenidos.clear()
                    SelectedDetenidos = ArrayList(uniqueMap.values)
                    println("No me voy a suicidar")

                    for (item in SelectedDetenidos) {
                        val DUI = item.first
                        val Nombre = item.second
                        val Foto = item.third

                        // Crear una vista para cada detenido seleccionado
                        val cardView = layoutInflater.inflate(R.layout.card_detenidos_detenido_select, null)

                        val lblNombre = cardView.findViewById<TextView>(R.id.Detenidos_card_detenido_seleccion_lblNombre)
                        val lblDui = cardView.findViewById<TextView>(R.id.Detenidos_card_detenido_seleccion_lblDui)
                        val imgInfractor = cardView.findViewById<ImageView>(R.id.Detenidos_card_detenido_seleccion_imgInfractor)
                        val btnDel = cardView.findViewById<LinearLayout>(R.id.Detenidos_card_detenido_seleccion_info)

                        cardView.findViewById<LinearLayout>(R.id.Detenidos_card_detenido_seleccion_llSelect).backgroundTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(this@Decomisos_agregar, R.color.DetenidosSelected))

                        btnDel.setOnClickListener{
                            LlInfractoresSeleccionados.removeView(cardView)
                            SelectedDetenidos.remove(item)
                        }

                        lblNombre.text = Nombre
                        lblDui.text = DUI

                        if (Foto != null && Foto.isNotEmpty()) {
                            val bitmap = BitmapFactory.decodeByteArray(Foto, 0, Foto.size)
                            imgInfractor.setImageBitmap(bitmap)
                        } else {
                            imgInfractor.setImageResource(R.drawable.void_image)
                        }

                        // Agregar la vista al contenedor
                        LlInfractoresSeleccionados.addView(cardView)
                    }
                }
                else{
                    println("Me voy a suicidar")
                }
            }
        }
    }
}