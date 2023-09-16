package com.example.esespi

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.ByteArrayOutputStream
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException
import java.text.SimpleDateFormat
import java.util.Calendar

private lateinit var imgInfractor:ImageView
private lateinit var btnQuitarFoto:LinearLayout
private lateinit var btnTomarFoto:LinearLayout
private lateinit var btnSubirFoto:LinearLayout

private lateinit var txtNombre:EditText
private lateinit var txtApellido:EditText
private lateinit var txtDUI:EditText

private lateinit var btnMasculino:LinearLayout
private lateinit var imgMasculino:ImageView
private lateinit var lblMasculino:TextView

private lateinit var btnFemenino:LinearLayout
private lateinit var imgFemenino:ImageView
private lateinit var lblFemenino:TextView

private lateinit var dbDepartamento:Spinner
private lateinit var dbMunicipio:Spinner

private lateinit var txtDireccion:EditText
private lateinit var txtDescripcion:EditText

private lateinit var dbDia:Spinner
private lateinit var dbMes:Spinner
private lateinit var dbAño:Spinner

private lateinit var btnGuardar:LinearLayout
private lateinit var btnDescartar:LinearLayout

private var genero: Int = 0
private var foto: ByteArray? = null

private lateinit var conn: Connection

private val PICK_IMAGE_REQUEST = 1
private val REQUEST_IMAGE_CAPTURE = 2

private var ActivityMode: String? = null

private val municipiosPorDepartamento = mapOf(
    "Ahuachapán" to arrayOf("Atiquizaya", "Jujutla", "San Francisco Menéndez", "Turín"),
    "Cabañas" to arrayOf("Sensuntepeque", "Ilobasco", "Victoria", "San Isidro", "San Sebastián"),
    "Chalatenango" to arrayOf("Nueva Concepción", "La Palma", "San Ignacio", "Las Vueltas"),
    "Cuscatlán" to arrayOf("Cojutepeque", "Santiago de María", "San Pedro Perulapán", "San Rafael Cedros", "Victoria"),
    "La Libertad" to arrayOf("Santa Tecla", "Antiguo Cuscatlán", "La Libertad", "Colón", "San Juan Opico"),
    "La Paz" to arrayOf("Zacatecoluca", "San Luis Talpa", "Cuyultitán", "San Juan Nonualco", "San Pedro Masahuat"),
    "La Unión" to arrayOf("Conchagua", "El Carmen", "Pasaquina", "Santa Rosa de Lima"),
    "Morazán" to arrayOf("San Francisco Gotera", "Guatajiagua", "Perquín", "Yamabal", "Sociedad"),
    "San Miguel" to arrayOf("Ciudad Barrios", "Carolina", "Chapeltique", "San Rafael Oriente"),
    "San Salvador" to arrayOf("Soyapango", "Delgado", "Mejicanos", "Ayutuxtepeque"),
    "San Vicente" to arrayOf("Apastepeque", "Guadalupe", "San Esteban Catarina", "San Cayetano Istepeque"),
    "Santa Ana" to arrayOf("Chalchuapa", "Metapán", "Coatepeque", "Atiquizaya"),
    "Sonsonate" to arrayOf("Sonzacate", "Acajutla", "Izalco", "Nahuizalco"),
    "Usulután" to arrayOf("Santiago de María", "Jiquilisco", "San Francisco Javier", "Santa Elena")
)
private val departamentos = arrayOf(
    "Ahuachapán",
    "Cabañas",
    "Chalatenango",
    "Cuscatlán",
    "La Libertad",
    "La Paz",
    "La Unión",
    "Morazán",
    "San Miguel",
    "San Salvador",
    "San Vicente",
    "Santa Ana",
    "Sonsonate",
    "Usulután"
)

class Infractores_agregar : AppCompatActivity() {
    @SuppressLint("MissingInflatedId", "WrongThread", "ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_infractores_agregar)

        conn = conexionSQL().dbConn() ?: throw SQLException("No se pudo establecer la conexión a la base de datos")

        var btnSalir = findViewById<ImageView>(R.id.Infractores_Agregar_btnQuit)
        btnSalir.setOnClickListener {
            finish()
        }

        ActivityMode = intent.getStringExtra("mode")

        imgInfractor = findViewById(R.id.Infractores_Agregar_imgInfractor)
        btnQuitarFoto = findViewById(R.id.Infractores_Agregar_btnQuitarFoto)
        btnTomarFoto = findViewById(R.id.Infractores_Agregar_btnTomarFoto)
        btnSubirFoto = findViewById(R.id.Infractores_Agregar_btnSubirFoto)

        txtNombre = findViewById(R.id.Infractores_Agregar_txtNombre)
        txtApellido = findViewById(R.id.Infractores_Agregar_txtApellido)
        txtDUI = findViewById(R.id.Infractores_Agregar_txtDUI)

        btnMasculino = findViewById(R.id.Infractores_Agregar_btnMasculino)
        imgMasculino = findViewById(R.id.Infractores_Agregar_btnMasculino_vec)
        lblMasculino = findViewById(R.id.Infractores_Agregar_btnMasculino_txt)

        btnFemenino = findViewById(R.id.Infractores_Agregar_btnFemenino)
        imgFemenino = findViewById(R.id.Infractores_Agregar_btnFemenino_vec)
        lblFemenino = findViewById(R.id.Infractores_Agregar_btnFemenino_txt)

        dbDepartamento = findViewById(R.id.Infractores_Agregar_DbDepartamento)
        dbMunicipio = findViewById(R.id.Infractores_Agregar_DbMunicipio)

        txtDireccion = findViewById(R.id.Infractores_Agregar_txtDireccion)
        txtDescripcion = findViewById(R.id.infractores_Agregar_txtDescripcion)

        dbDia = findViewById(R.id.Infractores_Agregar_DbDia)
        dbMes = findViewById(R.id.Infractores_Agregar_DbMes)
        dbAño = findViewById(R.id.Infractores_Agregar_DbAño)

        btnGuardar = findViewById(R.id.Infractores_Agregar_btnAgregar)
        btnDescartar = findViewById(R.id.Infractores_Agregar_btnDescartar)

        btnDescartar.setOnClickListener {
            finish()
        }

//ULTIMA VEZ VISTO ---------------------------------------------------------------------------------

        // Obtener la fecha actual
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH) + 1
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        // Configurar los Spinners de día, mes y año
        val diaAdapter = ArrayAdapter.createFromResource(this, R.array.dias, R.drawable.custom_spinner_adapter)
        diaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dbDia.adapter = diaAdapter
        dbDia.setSelection(currentDay - 1)

        val mesAdapter = ArrayAdapter.createFromResource(this, R.array.meses, R.drawable.custom_spinner_adapter)
        mesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dbMes.adapter = mesAdapter
        dbMes.setSelection(currentMonth - 1)

        val años = resources.getStringArray(R.array.años)
        val añoAdapter = ArrayAdapter(this, R.drawable.custom_spinner_adapter, años)
        añoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dbAño.adapter = añoAdapter

        // Encontrar el índice del año actual en el arreglo de años
        val currentYearIndex = años.indexOf(currentYear.toString())
        if (currentYearIndex != -1) {
            dbAño.setSelection(currentYearIndex)
        }

//MASCULINO Y FEMENINO -----------------------------------------------------------------------------

        btnMasculino.setOnClickListener {

            imgMasculino.setImageResource(R.drawable.vec_masculino_selected)
            lblMasculino.setTextColor(Color.parseColor("#FFFFFF"))
            btnMasculino.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#4399FF"))

            imgFemenino.setImageResource(R.drawable.vec_femenino_unselected)
            lblFemenino.setTextColor(Color.parseColor("#686868"))
            btnFemenino.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D8D8D8"))
            genero=1
        }

        btnFemenino.setOnClickListener {

            imgFemenino.setImageResource(R.drawable.vec_femenino_selected)
            lblFemenino.setTextColor(Color.parseColor("#FFFFFF"))
            btnFemenino.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#4399FF"))

            imgMasculino.setImageResource(R.drawable.vec_masculino_unselected)
            lblMasculino.setTextColor(Color.parseColor("#686868"))
            btnMasculino.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D8D8D8"))
            genero=2
        }

//MUNICIPIO Y DEPARTAMENTO -------------------------------------------------------------------------

        // Configurar el Spinner de Departamentos y Municipios
        val departamentoAdapter = ArrayAdapter(this, R.drawable.custom_spinner_adapter, departamentos)
        departamentoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dbDepartamento.adapter = departamentoAdapter
        //Añadir interaccion a departamentos y municipios
        dbDepartamento.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val departamento = parent.getItemAtPosition(position).toString()
                val municipios = municipiosPorDepartamento[departamento] ?: arrayOf()
                val municipioAdapter = ArrayAdapter(this@Infractores_agregar, R.drawable.custom_spinner_adapter, municipios)
                municipioAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                dbMunicipio.adapter = municipioAdapter

            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // No se seleccionó ningún departamento
            }
        }

// FOTO --------------------------------------------------------------------------------------------

        btnSubirFoto.setOnClickListener {
            getIMG(this, false)
        }
        btnTomarFoto.setOnClickListener {
            getIMG(this, true)
        }
        btnQuitarFoto.setOnClickListener {
            imgInfractor.setImageResource(R.drawable.void_image)
            foto=null
        }

// SUBIR A LA BASE ---------------------------------------------------------------------------------

        if(ActivityMode=="Agregar"){
            btnGuardar.setOnClickListener {

                var v = Validaciones()
                val dia = dbDia.selectedItem.toString().toInt()
                val mes = v.obtenerNumeroMes(dbMes.selectedItem.toString())
                val año = dbAño.selectedItem.toString().toInt()

                if (
                    v.CharWritten(txtNombre, "Nombre", 30, 3, this) &&
                    v.CharWritten(txtApellido, "Apelido", 30, 3, this) &&
                    v.CharWritten(txtDUI, "Dui", 9, 9, this) &&
                    v.CharWritten(txtDireccion, "Dirección", 50, 1, this) &&
                    v.CharWritten(txtDescripcion, "Descripción", 50, 1, this) &&
                    v.GenderSelected(genero, this) &&
                    v.FechaReal(dia, mes, año, this)
                ){

                    try {
                        val addProducto: PreparedStatement =  conn.prepareStatement(
                            "EXEC dbo.InsertarInfractores \n" +
                                    "    @NombrePersona = ?,\n" +
                                    "    @ApellidoPersona = ?,\n" +
                                    "    @DuiPersona = ?,\n" +
                                    "    @DireccionPersona = ?,\n" +
                                    "    @IdGenero = ?,\n" +
                                    "    @DescripcionInfractor = ?,\n" +
                                    "    @UltimaVezVisto = ?,\n" +
                                    "    @Foto = ?;"
                        )!!

                        addProducto.setString(1, txtNombre.text.toString())
                        addProducto.setString(2, txtApellido.text.toString())
                        addProducto.setString(3, txtDUI.text.toString())
                        addProducto.setString(4, dbDepartamento.selectedItem.toString() +", "+ dbMunicipio.selectedItem.toString() +", "+ txtDireccion.text.toString())
                        addProducto.setInt(5, genero)
                        addProducto.setString(6, txtDescripcion.text.toString())
                        addProducto.setString(7, "$año/$mes/$dia")
                        addProducto.setBytes(8, foto)
                        addProducto.executeUpdate()

                        Toast.makeText(this, "Se ha registrado correctamente", Toast.LENGTH_SHORT).show()

                        setResult(RESULT_OK, Intent())
                        conn.close()
                        finish()
                    }
                    catch (ex: SQLException){
                        Toast.makeText(this, "Error al ingresar: "+ex, Toast.LENGTH_SHORT).show()
                        println(ex)
                        setResult(RESULT_OK, Intent())
                    }
                } else{

                }
            }
        }

        else if (ActivityMode=="Editar"){

            val GotId = intent.getStringExtra("id")
            val GotNombre = intent.getStringExtra("nom")
            val GotApellido = intent.getStringExtra("ape")
            val GotDui = intent.getStringExtra("dui")
            val GotDireccion = intent.getStringExtra("dir")
            val GotIdGenero = intent.getIntExtra("gen", 0)
            val GotDescripcion = intent.getStringExtra("des")
            val GotUltimaVezVisto = intent.getStringExtra("las")
            val GotFoto = intent.getByteArrayExtra("img")

            val GotDireccionFull = GotDireccion?.split(", ")

            val SplitDepartamento = GotDireccionFull?.get(0)
            val SplitMunicipio = GotDireccionFull?.get(1)
            val SplitDireccion = GotDireccionFull?.drop(2)?.joinToString(", ")

            txtNombre.setText(GotNombre)
            txtApellido.setText(GotApellido)
            txtDUI.setText(GotDui)
            txtDireccion.setText(SplitDireccion)
            txtDescripcion.setText(GotDescripcion)

            val bitmap = GotFoto?.let { BitmapFactory.decodeByteArray(GotFoto, 0, it.size) }
            imgInfractor.setImageBitmap(bitmap)
            foto = GotFoto

            //VOLVER A PONER VALORES DE FECHA -------------------------------------------------------------------------------------

            val fecha = SimpleDateFormat("yyyy-MM-dd").parse(GotUltimaVezVisto) // Convertir la cadena en un objeto Date
            val calendar = Calendar.getInstance()
            calendar.time = fecha

            val año = calendar.get(Calendar.YEAR)
            val mes = calendar.get(Calendar.MONTH) + 1 // Los meses en Calendar van de 0 a 11
            val día = calendar.get(Calendar.DAY_OF_MONTH)

            // Configurar los adaptadores para los spinners
            val diaAdapter = ArrayAdapter.createFromResource(this, R.array.dias, R.drawable.custom_spinner_adapter)
            diaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            dbDia.adapter = diaAdapter

            val mesAdapter = ArrayAdapter.createFromResource(this, R.array.meses, R.drawable.custom_spinner_adapter)
            mesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            dbMes.adapter = mesAdapter

            val años = resources.getStringArray(R.array.años)
            val añoAdapter = ArrayAdapter(this, R.drawable.custom_spinner_adapter, años)
            añoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            dbAño.adapter = añoAdapter

            // Encontrar el índice del año actual en el arreglo de años
            val currentYearIndex = años.indexOf(año.toString())
            if (currentYearIndex != -1) {
                dbAño.setSelection(currentYearIndex)
            }

            // Configurar la selección de los spinners de día y mes
            dbDia.setSelection(día - 1) // El índice del spinner comienza en 0
            dbMes.setSelection(mes - 1) // El índice del spinner comienza en 0

            //VOLVER A PONER LOS VALORES DE GENERO -----------------------------------------------------------------------

            if(GotIdGenero==1){
                imgMasculino.setImageResource(R.drawable.vec_masculino_selected)
                lblMasculino.setTextColor(Color.parseColor("#FFFFFF"))
                btnMasculino.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#4399FF"))

                imgFemenino.setImageResource(R.drawable.vec_femenino_unselected)
                lblFemenino.setTextColor(Color.parseColor("#686868"))
                btnFemenino.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D8D8D8"))
                genero=1
            }

            if(GotIdGenero==2){
                imgFemenino.setImageResource(R.drawable.vec_femenino_selected)
                lblFemenino.setTextColor(Color.parseColor("#FFFFFF"))
                btnFemenino.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#4399FF"))

                imgMasculino.setImageResource(R.drawable.vec_masculino_unselected)
                lblMasculino.setTextColor(Color.parseColor("#686868"))
                btnMasculino.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D8D8D8"))
                genero=2
            }

            //VOLVER A PONER LOS VALORES DE DEPARTAMENTO Y MUNICIPIO --------------------------------------------------------------------------

            // Configurar el Spinner de Departamentos y Municipios
            val departamentoAdapter = ArrayAdapter(this, R.drawable.custom_spinner_adapter, departamentos)
            departamentoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            dbDepartamento.adapter = departamentoAdapter

            // Añadir interacción a departamentos y municipios
            dbDepartamento.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    val departamento = parent.getItemAtPosition(position).toString()
                    val municipios = municipiosPorDepartamento[departamento] ?: arrayOf()
                    val municipioAdapter = ArrayAdapter(this@Infractores_agregar, R.drawable.custom_spinner_adapter, municipios)
                    municipioAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    dbMunicipio.adapter = municipioAdapter

                    // Encontrar el índice del municipio actual en el arreglo de municipios
                    val municipioIndex = municipios.indexOf(SplitMunicipio)
                    if (municipioIndex != -1) {
                        dbMunicipio.setSelection(municipioIndex)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // No se seleccionó ningún departamento
                }
            }

            // Suponiendo que SplitDepartamento y SplitMunicipio tienen los valores correctos
            val departamentoIndex = departamentos.indexOf(SplitDepartamento)
            if (departamentoIndex != -1) {
                dbDepartamento.setSelection(departamentoIndex)
            }

            btnGuardar.setOnClickListener {
                val dia = dbDia.selectedItem.toString().toInt()
                val mes = obtenerNumeroMes(dbMes.selectedItem.toString())
                val año = dbAño.selectedItem.toString().toInt()

                if (
                    validarFecha(dia, mes, año) &&
                    genero!=0 &&
                    txtNombre.text.toString()!=null &&
                    txtApellido.text.toString()!=null &&
                    txtDUI.text.toString()!=null &&
                    txtDUI.text.toString().length==9 &&
                    txtDireccion.text.toString()!=null &&
                    txtDescripcion.text.toString()!=null
                ){

                    try {
                        val addProducto: PreparedStatement =  conn.prepareStatement(
                            "EXEC dbo.ActualizarInfractores \n" +
                                    "    @IdPersona = ?,\n" +
                                    "    @NombrePersona = ?,\n" +
                                    "    @ApellidoPersona = ?,\n" +
                                    "    @DuiPersona = ?,\n" +
                                    "    @DireccionPersona = ?,\n" +
                                    "    @IdGenero = ?,\n" +
                                    "    @DescripcionInfractor = ?,\n" +
                                    "    @UltimaVezVisto = ?,\n" +
                                    "    @Foto = ?;"
                        )!!

                        addProducto.setString(1, GotId)
                        addProducto.setString(2, txtNombre.text.toString())
                        addProducto.setString(3, txtApellido.text.toString())
                        addProducto.setString(4, txtDUI.text.toString())
                        addProducto.setString(5, dbDepartamento.selectedItem.toString() +", "+ dbMunicipio.selectedItem.toString() +", "+ txtDireccion.text.toString())
                        addProducto.setInt(6, genero)
                        addProducto.setString(7, txtDescripcion.text.toString())
                        addProducto.setString(8, "$año/$mes/$dia")
                        addProducto.setBytes(9, foto)
                        addProducto.executeUpdate()

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
            imgInfractor.setImageBitmap(imageBitmap)
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
            imgInfractor.setImageURI(imageUri)
        }
    }

    fun validarFecha(dia: Int, mes: Int, año: Int): Boolean {
        // Verificar si el año es válido
        if (año <= 0) {
            return false
        }

        // Verificar si el mes es válido
        if (mes < 1 || mes > 12) {
            return false
        }

        // Verificar si el día es válido
        if (dia < 1 || dia > 31) {
            return false
        }

        // Verificar si el día es válido para el mes específico
        when (mes) {
            2 -> {  // Febrero
                // Verificar si es año bisiesto
                if ((año % 4 == 0 && año % 100 != 0) || (año % 400 == 0)) {
                    if (dia > 29) {
                        return false
                    }
                } else {
                    if (dia > 28) {
                        return false
                    }
                }
            }
            4, 6, 9, 11 -> {  // Meses con 30 días
                if (dia > 30) {
                    return false
                }
            }
        }

        // Obtener la fecha actual
        val calendarActual = Calendar.getInstance()
        val añoActual = calendarActual.get(Calendar.YEAR)
        val mesActual = calendarActual.get(Calendar.MONTH) + 1
        val diaActual = calendarActual.get(Calendar.DAY_OF_MONTH)

        // Comparar las fechas
        if (año > añoActual) {
            return false
        } else if (año == añoActual && mes > mesActual) {
            return false
        } else if (año == añoActual && mes == mesActual && dia > diaActual) {
            return false
        }

        return true
    }

    fun obtenerNumeroMes(nombreMes: String): Int {
        val meses = listOf(
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        )
        return meses.indexOfFirst { it.equals(nombreMes, ignoreCase = true) } + 1
    }
}