package com.example.esespi

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
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
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException
import java.text.SimpleDateFormat
import java.util.Calendar

private lateinit var txtDireccion: EditText
private lateinit var txtHora: TextView
private lateinit var lblHora: TextView

private lateinit var dbDepartamento: Spinner
private lateinit var dbMunicipio: Spinner
private lateinit var dbTipo: Spinner

private lateinit var dbDia: Spinner
private lateinit var dbMes: Spinner
private lateinit var dbAño: Spinner

private lateinit var btnAgregarInfractor: LinearLayout
private lateinit var llAgregarInfractor: LinearLayout

private lateinit var btnGuardar: LinearLayout
private lateinit var btnDescartar: LinearLayout

private lateinit var conn: Connection
private var hora:String="noSelected"

private var GrupoPatrullaje:Int=0
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

private var UN_INFRACTOR_REQUEST = 1
private var SelectedInfractores: ArrayList<Triple<String, String, ByteArray?>> = ArrayList()


class Detenidos_agregar : AppCompatActivity() {
    @SuppressLint("MissingInflatedId", "ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detenidos_agregar)

        conn = conexionSQL().dbConn() ?: throw SQLException("No se pudo establecer la conexión a la base de datos")

        txtDireccion = findViewById(R.id.Detenidos_agregar_txtDireccion)
        dbDepartamento = findViewById(R.id.Detenidos_agregar_DbDepartamento)
        dbMunicipio = findViewById(R.id.Detenidos_agregar_DbMunicipio)
        dbTipo = findViewById(R.id.Detenidos_agregar_DbTipoDetenido)

        txtHora = findViewById(R.id.Detenidos_agregar_txtHora)
        lblHora = findViewById(R.id.Detenidos_agregar_lblHora)

        dbDia = findViewById(R.id.Detenidos_agregar_DbDia)
        dbMes = findViewById(R.id.Detenidos_agregar_DbMes)
        dbAño = findViewById(R.id.Detenidos_agregar_DbAño)

        btnAgregarInfractor = findViewById(R.id.Detenidos_agregar_btnSeleccionarInfractor)
        llAgregarInfractor = findViewById(R.id.Detenidos_agregar_LlInfractoresSelected)

        btnGuardar = findViewById(R.id.Detenidos_agregar_btnAgregar)
        btnDescartar = findViewById(R.id.Detenidos_agregar_btnDescartar)

        btnDescartar.setOnClickListener {
            finish()
        }

        ActivityMode = intent.getStringExtra("mode")


// SPINNER -----------------------------------------------------------------------------------------

        val clasificaciones = ArrayList<String>()
        try {
            val statement = conn.createStatement()
            val query = "SELECT * FROM tbTiposDetencion"
            val resultSet = statement?.executeQuery(query)

            clasificaciones.add("Seleccione una opcion...")

            while (resultSet?.next() == true) {
                val clasificacion = resultSet.getString("TipoDetension")
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

        btnAgregarInfractor.setOnClickListener{
            val intent = Intent(this, Infractores_seleccion::class.java)
            intent.putExtra("mode", "UnInfractor")
            startActivityForResult(intent, UN_INFRACTOR_REQUEST)
        }






// FECHA ------------------------------------------------------------------------------------------

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
                val municipioAdapter = ArrayAdapter(this@Detenidos_agregar, R.drawable.custom_spinner_adapter, municipios)
                municipioAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                dbMunicipio.adapter = municipioAdapter

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No se seleccionó ningún departamento
            }
        }
//HORA ---------------------------------------------------------------------------------------------
        txtHora.setOnClickListener{
            val timePicker = TimePickerFragment{onTimeSelected(it)}
            timePicker.show(supportFragmentManager, "time")
        }

// SUBIR A LA BASE ---------------------------------------------------------------------------------

        if(ActivityMode=="Agregar"){
            btnGuardar.setOnClickListener {

                val dia = dbDia.selectedItem.toString().toInt()
                val mes = obtenerNumeroMes(dbMes.selectedItem.toString())
                val año = dbAño.selectedItem.toString().toInt()

                if (
                    SelectedInfractores.isNotEmpty()
                ){

                    val DUIS = SelectedInfractores[0]
                    val DuiPersona = DUIS.first

                    try {
                        val addProducto: PreparedStatement =  conn.prepareStatement(
                            "EXEC InsertarDetenido4\n" +
                                    "\t@DuiInfractor = ?,\n" +
                                    "\t@IdGrupoPatrullaje = ?,\n" +
                                    "\t@LugarDetencion = ?,\n" +
                                    "\t@Fecha_Detencion = ?,\n" +
                                    "\t@TipoDetencion = ?;"
                        )

                        GrupoPatrullaje = intent.getIntExtra("grupoPatrullaje", 0)
                        println(GrupoPatrullaje)
                        addProducto.setString(1, DuiPersona)
                        addProducto.setInt(2, IdGrupoGot)
                        addProducto.setString(3, dbDepartamento.selectedItem.toString() +", "+ dbMunicipio.selectedItem.toString() +", "+ txtDireccion.text.toString())
                        addProducto.setString(4, "$año/$mes/$dia $hora")
                        addProducto.setString(5, dbTipo.selectedItem.toString())
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
                    Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
                }
            }
        }


        else if (ActivityMode=="Editar"){

            val GotId = intent.getStringExtra("IdDetenido")
            val GotTipoDetencion = intent.getStringExtra("TipoDetencion")
            val GotNombre = intent.getStringExtra("Nombre")
            val GotFecha = intent.getStringExtra("Fecha")
            val GotLugar = intent.getStringExtra("LugarDetencion")
            val GotFoto = intent.getByteArrayExtra("Foto")
            val GotDui = intent.getStringExtra("Dui")

            findViewById<LinearLayout>(R.id.Detenidos_agregar_LlAgregarInvolucradoContainer).visibility = View.GONE

            val GotDireccionFull = GotLugar?.split(", ")

            val SplitDepartamento = GotDireccionFull?.get(0)
            val SplitMunicipio = GotDireccionFull?.get(1)
            val SplitDireccion = GotDireccionFull?.drop(2)?.joinToString(", ")

            txtDireccion.setText(SplitDireccion)

            //VOLVER A PONER VALORES DE TIPO DETENIDO -------------------------------------------------------------------------------

            val indiceSeleccion = clasificaciones.indexOf(GotTipoDetencion)

            if (indiceSeleccion != -1) {
                // Si se encontró el texto, establecerlo como la selección actual del Spinner
                dbTipo.setSelection(indiceSeleccion)
            }

            //VOLVER A PONER VALORES DE FECHA -------------------------------------------------------------------------------------

            val fecha = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(GotFecha) // Analizar la cadena en formato con milisegundos
            val calendar = Calendar.getInstance()
            calendar.time = fecha

            val año = calendar.get(Calendar.YEAR)
            val mes = calendar.get(Calendar.MONTH) + 1 // Los meses en Calendar van de 0 a 11
            val día = calendar.get(Calendar.DAY_OF_MONTH)

            val GotHora = calendar.get(Calendar.HOUR)
            val minutos = calendar.get(Calendar.MINUTE)
            val segundos = calendar.get(Calendar.SECOND)
            val amPm = calendar.get(Calendar.AM_PM)

            val amPmStr = if (amPm == Calendar.AM) "AM" else "PM"

            hora = "$GotHora:$minutos:$segundos $amPmStr"
            lblHora.text = "$GotHora:$minutos $amPmStr"

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
                    val municipioAdapter = ArrayAdapter(this@Detenidos_agregar, R.drawable.custom_spinner_adapter, municipios)
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
                    true
                ){

                    try {
                        val addProducto: PreparedStatement =  conn.prepareStatement(
                            "EXEC dbo.ActualizarDetenido \n" +
                                    "   @IdDetenido = ?,\n" +
                                    "   @LugarDetencion = ?,\n" +
                                    "   @Fecha_Detencion = ?,\n" +
                                    "   @TipoDetencion = ?,\n" +
                                    "   @TipoDetencionOriginal = ?;"
                        )!!

                        addProducto.setString(1, GotId)
                        addProducto.setString(2, dbDepartamento.selectedItem.toString() +", "+ dbMunicipio.selectedItem.toString() +", "+ txtDireccion.text.toString())
                        addProducto.setString(3, "$año/$mes/$dia $hora")
                        addProducto.setString(4, dbTipo.selectedItem.toString())
                        addProducto.setString(5, GotTipoDetencion)
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
                    }
                } else{
                    Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun onTimeSelected(time:String){
        val hora_parsed = SimpleDateFormat("HH:mm:ss").parse(time) // Ajusta el formato al que realmente tienes
        val hora_formateada = SimpleDateFormat("hh:mm a").format(hora_parsed) // Formatea la hora en formato "hh:mm AM/PM"

        lblHora.text = hora_formateada
        hora = time
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == UN_INFRACTOR_REQUEST && resultCode == Activity.RESULT_OK) {
            // Comprueba si el extra "selectedInfractores" está presente en el Intent resultante
            if (data != null && data.hasExtra("selectedInfractores")) {
                val selectedInfractores = data.getParcelableArrayListExtra<MyParcelableTriple>("selectedInfractores")
                llAgregarInfractor.removeAllViews()
                // Realiza la conversión de tipos
                val convertedList = ArrayList<Triple<String, String, ByteArray?>>()
                selectedInfractores?.forEach { item ->
                    val triple = Triple(item.first, item.second, item.third)
                    convertedList.add(triple as Triple<String, String, ByteArray?>)
                }

                // Asigna la lista convertida a la variable global
                SelectedInfractores = convertedList

                for (data in SelectedInfractores) {
                    // Desempaqueta los datos del array
                    val DUI = data.first
                    val Nombre = data.second
                    val Foto = data.third

                    // Infla una CardView
                    val cardView = layoutInflater.inflate(R.layout.infractores_card_infractor_select, null)

                    val lblNombre = cardView.findViewById<TextView>(R.id.Infractores_card_infractor_seleccion_lblNombre)
                    val lblDui = cardView.findViewById<TextView>(R.id.Infractores_card_infractor_seleccion_lblDui)
                    val imgInfractor = cardView.findViewById<ImageView>(R.id.Infractores_card_infractor_seleccion_imgInfractor)

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
                    llAgregarInfractor.addView(cardView)
                }
            }
        }
    }
}