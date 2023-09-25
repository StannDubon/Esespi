package com.example.esespi

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
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

private lateinit var txtNombre: EditText
private lateinit var txtMotivo: EditText
private lateinit var txtDireccion:EditText

private lateinit var txtHora:TextView
private lateinit var lblHora:TextView

private lateinit var dbDepartamento:Spinner
private lateinit var dbMunicipio:Spinner

private lateinit var dbDia:Spinner
private lateinit var dbMes:Spinner
private lateinit var dbAño:Spinner

private lateinit var btnGuardar: LinearLayout
private lateinit var btnDescartar: LinearLayout

private lateinit var conn: Connection
private var hora:String="noSelected"

private var ActivityMode: String? = null

class Acercamientos_agregar : AppCompatActivity() {
    @SuppressLint("ResourceType", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acercamientos_agregar)

        conn = conexionSQL().dbConn() ?: throw SQLException("No se pudo establecer la conexión a la base de datos")

        txtNombre = findViewById(R.id.Acercamientos_Agregar_txtNombre)

        txtMotivo = findViewById(R.id.Acercamientos_Agregar_txtMotivo)
        txtDireccion = findViewById(R.id.Acercamienos_Agregar_txtDireccion)

        dbDepartamento = findViewById(R.id.Acercamientos_Agregar_DbDepartamento)
        dbMunicipio = findViewById(R.id.Acercamientos_Agregar_DbMunicipio)

        txtHora = findViewById(R.id.Acercamientos_Agregar_txtHora)
        lblHora = findViewById(R.id.Acercamientos_Agregar_lblHora)

        dbDia = findViewById(R.id.Acercamientos_Agregar_DbDia)
        dbMes = findViewById(R.id.Acercamientos_Agregar_DbMes)
        dbAño = findViewById(R.id.Acercamientos_Agregar_DbAño)

        btnGuardar = findViewById(R.id.Acercamientos_Agregar_btnAgregar)
        btnDescartar = findViewById(R.id.Acercamientos_Agregar_btnDescartar)

        btnDescartar.setOnClickListener {
            finish()
        }

        ActivityMode = intent.getStringExtra("mode")

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
                val municipioAdapter = ArrayAdapter(this@Acercamientos_agregar, R.drawable.custom_spinner_adapter, municipios)
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

        val horaActual = obtenerHoraActual()
        val hora_parsed = SimpleDateFormat("HH:mm:ss").parse(horaActual)
        val hora_formateada = SimpleDateFormat("hh:mm a").format(hora_parsed)
        lblHora.text = hora_formateada
        hora = horaActual

// SUBIR A LA BASE ---------------------------------------------------------------------------------

        if(ActivityMode=="Agregar"){
            btnGuardar.setOnClickListener {

                val dia = dbDia.selectedItem.toString().toInt()
                val mes = obtenerNumeroMes(dbMes.selectedItem.toString())
                val año = dbAño.selectedItem.toString().toInt()

                if (
                    true
                ){

                    try {
                        val addProducto: PreparedStatement =  conn.prepareStatement(
                            "EXEC dbo.InsertarAcercamiento \n" +
                                    "   @Lugar = ?,\n" +
                                    "   @Fecha = ?,\n" +
                                    "   @NombrePersona = ?,\n" +
                                    "   @Acercamiento = ?,\n" +
                                    "   @IdGrupoPatrullaje = ?;"
                        )!!

                        println(IdGrupoGot)
                        addProducto.setString(1, dbDepartamento.selectedItem.toString() +", "+ dbMunicipio.selectedItem.toString() +", "+ txtDireccion.text.toString())
                        addProducto.setString(2, "$año/$mes/$dia $hora")
                        addProducto.setString(3, txtNombre.text.toString())
                        addProducto.setString(4, txtMotivo.text.toString())
                        addProducto.setInt(5, IdGrupoGot)
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

            val GotId = intent.getStringExtra("id")
            val GotIdInforme = intent.getStringExtra("inf")
            val GotLugar = intent.getStringExtra("lug")
            val GotFecha = intent.getStringExtra("fec")
            val GotNombrePersona = intent.getStringExtra("nomp")
            val GotAcercamiento = intent.getStringExtra("ace")

            val GotDireccionFull = GotLugar?.split(", ")

            val SplitDepartamento = GotDireccionFull?.get(0)
            val SplitMunicipio = GotDireccionFull?.get(1)
            val SplitDireccion = GotDireccionFull?.drop(2)?.joinToString(", ")

            txtNombre.setText(GotNombrePersona)
            txtMotivo.setText(GotAcercamiento)
            txtDireccion.setText(SplitDireccion)

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
                    val municipioAdapter = ArrayAdapter(this@Acercamientos_agregar, R.drawable.custom_spinner_adapter, municipios)
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
                            "EXEC dbo.ActualizarAcercamiento \n" +
                                    "   @IdAcercamiento = ?,\n" +
                                    "   @Lugar = ?,\n" +
                                    "   @Fecha = ?,\n" +
                                    "   @NombrePersona = ?,\n" +
                                    "   @Acercamiento = ?;"
                        )!!

                        addProducto.setString(1, GotId)
                        addProducto.setString(2, dbDepartamento.selectedItem.toString() +", "+ dbMunicipio.selectedItem.toString() +", "+ txtDireccion.text.toString())
                        addProducto.setString(3, "$año/$mes/$dia $hora")
                        addProducto.setString(4, txtNombre.text.toString())
                        addProducto.setString(5, txtMotivo.text.toString())
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
}