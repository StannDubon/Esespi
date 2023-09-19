package com.example.esespi

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.sql.Connection
import java.sql.SQLException
import java.util.*

class RegistroUsuarioIngresoDatos : AppCompatActivity() {

    private val municipiosPorDepartamento = mapOf(
        "Ahuachapán" to arrayOf("Ahuachapán", "Atiquizaya", "Jujutla", "San Francisco Menéndez", "Turín"),
        "Cabañas" to arrayOf("Sensuntepeque", "Ilobasco", "Victoria", "San Isidro", "San Sebastián"),
        "Chalatenango" to arrayOf("Chalatenango", "Nueva Concepción", "La Palma", "San Ignacio", "Las Vueltas"),
        "Cuscatlán" to arrayOf("Cojutepeque", "Santiago de María", "San Pedro Perulapán", "San Rafael Cedros", "Victoria"),
        "La Libertad" to arrayOf("Santa Tecla", "Antiguo Cuscatlán", "La Libertad", "Colón", "San Juan Opico"),
        "La Paz" to arrayOf("Zacatecoluca", "San Luis Talpa", "Cuyultitán", "San Juan Nonualco", "San Pedro Masahuat"),
        "La Unión" to arrayOf("La Unión", "Conchagua", "El Carmen", "Pasaquina", "Santa Rosa de Lima"),
        "Morazán" to arrayOf("San Francisco Gotera", "Guatajiagua", "Perquín", "Yamabal", "Sociedad"),
        "San Miguel" to arrayOf("San Miguel", "Ciudad Barrios", "Carolina", "Chapeltique", "San Rafael Oriente"),
        "San Salvador" to arrayOf("San Salvador", "Soyapango", "Delgado", "Mejicanos", "Ayutuxtepeque"),
        "San Vicente" to arrayOf("San Vicente", "Apastepeque", "Guadalupe", "San Esteban Catarina", "San Cayetano Istepeque"),
        "Santa Ana" to arrayOf("Santa Ana", "Chalchuapa", "Metapán", "Coatepeque", "Atiquizaya"),
        "Sonsonate" to arrayOf("Sonsonate", "Sonzacate", "Acajutla", "Izalco", "Nahuizalco"),
        "Usulután" to arrayOf("Usulután", "Santiago de María", "Jiquilisco", "San Francisco Javier", "Santa Elena")
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
    private var genero: Int = 0
    private var generotxt: String? = null

    private lateinit var btnAñadirIdioma: Button
    private lateinit var btnAñadirNacionalidad: Button
    private lateinit var linearLayoutIdiomas: LinearLayout
    private lateinit var linearLayoutNacionalidad: LinearLayout

    private lateinit var connection: Connection

    private lateinit var txtNombre: EditText
    private lateinit var txtApellido:EditText
    private lateinit var txtDUI: EditText
    private lateinit var txtDomicilio: EditText

    private lateinit var btnHombre: Button
    private lateinit var btnMujer: Button

    private lateinit var dbDia: Spinner
    private lateinit var dbMes: Spinner
    private lateinit var dbAño: Spinner

    private lateinit var dbEstCivil: Spinner
    private lateinit var dbTipoSangre: Spinner
    private lateinit var dbDepartamento: Spinner
    private lateinit var dbMunicipio: Spinner

    private lateinit var btnSiguiente: Button

    private var IdiomasSeleccionados: ArrayList<String> = ArrayList()
    private var NacionalidadesSeleccionados: ArrayList<String> = ArrayList()

    @SuppressLint("MissingInflatedId", "InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_usuario_ingreso_datos)

        connection = conexionSQL().dbConn() ?: throw SQLException("No se pudo establecer la conexión a la base de datos")

        val RegistroUsuarioValoresDeRegistro = getSharedPreferences("datos_ingreso", Context.MODE_PRIVATE)


//TEXTBOX--------------------------------------------------------------------------------------------------------------------------------------------------

        txtNombre=findViewById(R.id.RegistroUsuarioTxtNombre)
        txtApellido=findViewById(R.id.RegistroUsuarioTxtApellido)
        txtDUI=findViewById(R.id.RegistroUsuarioTxtDUI)
        txtDomicilio=findViewById(R.id.RegistroUsuarioTxtDireccion)

//GENERO--------------------------------------------------------------------------------------------------------------------------------------------------

        btnHombre=findViewById(R.id.RegistroUsuarioBtnMasculino)
        btnMujer=findViewById(R.id.RegistroUsuarioBtnFemenino)

        btnHombre.setOnClickListener {
            genero = 1
            btnHombre.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#4399FF"))
            btnHombre.setTextColor(Color.parseColor("#FFFFFF"))

            btnMujer.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D8D8D8"))
            btnMujer.setTextColor(Color.parseColor("#686868"))
        }

        btnMujer.setOnClickListener {
            genero = 2
            btnMujer.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#4399FF"))
            btnMujer.setTextColor(Color.parseColor("#FFFFFF"))

            btnHombre.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D8D8D8"))
            btnHombre.setTextColor(Color.parseColor("#686868"))
        }

//ESTADO CIVIL Y TIPO DE SANGRE--------------------------------------------------------------------------------------------------------------------------------------------------

        dbEstCivil=findViewById(R.id.RegistroUsuarioDbEstadoCivil)
        dbTipoSangre=findViewById(R.id.RegistroUsuarioDbTipoSangre)

        val estCivilAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, obtenerDatosPorID("tbEstadosCivil", "EstadoCivil"))
        estCivilAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dbEstCivil.adapter = estCivilAdapter

        val tipoSangreAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, obtenerDatosPorID("tbTiposSangre", "TipoSangre"))
        tipoSangreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dbTipoSangre.adapter = tipoSangreAdapter


//FECHA DE NACIMIENTO--------------------------------------------------------------------------------------------------------------------------------------------------

        dbDia=findViewById(R.id.RegistroUsuarioDbDia)
        dbMes=findViewById(R.id.RegistroUsuarioDbMes)
        dbAño=findViewById(R.id.RegistroUsuarioDbAño)

        // Obtener la fecha actual
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH) + 1
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        // Configurar los Spinners de día, mes y año
        val diaAdapter = ArrayAdapter.createFromResource(this, R.array.dias, android.R.layout.simple_spinner_item)
        diaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dbDia.adapter = diaAdapter
        dbDia.setSelection(currentDay - 1)

        val mesAdapter = ArrayAdapter.createFromResource(this, R.array.meses, android.R.layout.simple_spinner_item)
        mesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dbMes.adapter = mesAdapter
        dbMes.setSelection(currentMonth - 1)

        val años = resources.getStringArray(R.array.años)
        val añoAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, años)
        añoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dbAño.adapter = añoAdapter

        // Encontrar el índice del año actual en el arreglo de años
        val currentYearIndex = años.indexOf(currentYear.toString())
        if (currentYearIndex != -1) {
            dbAño.setSelection(currentYearIndex)
        }


//DEPARTAMENTO Y MUNICIPIO--------------------------------------------------------------------------------------------------------------------------------------------------

        // Referencia a xml para direccion
        dbDepartamento=findViewById(R.id.RegistroUsuarioDbDepartamento)
        dbMunicipio=findViewById(R.id.RegistroUsuarioDbMunicipio)

        // Configurar el Spinner de Departamentos y Municipios
        val departamentoAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, departamentos)
        departamentoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dbDepartamento.adapter = departamentoAdapter
        //Añadir interaccion a departamentos y municipios
        dbDepartamento.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val departamento = parent.getItemAtPosition(position).toString()
                val municipios = municipiosPorDepartamento[departamento] ?: arrayOf()
                val municipioAdapter = ArrayAdapter(this@RegistroUsuarioIngresoDatos, android.R.layout.simple_spinner_item, municipios)
                municipioAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                dbMunicipio.adapter = municipioAdapter

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No se seleccionó ningún departamento
            }
        }


//IDIOMA Y NACIONALIDAD--------------------------------------------------------------------------------------------------------------------------------------------------

        // Referencia a xml para idioma y nacionalidad
        linearLayoutIdiomas = findViewById(R.id.LinearLayoutIdiomasSeleccionados)
        linearLayoutNacionalidad = findViewById(R.id.LinearLayoutNacionalidadSeleccionados)

        IdiomasSeleccionados.clear()
        NacionalidadesSeleccionados.clear()

        btnAñadirNacionalidad=findViewById(R.id.btnAñadirNacionalidad)
        btnAñadirNacionalidad.setOnClickListener {


            val REQUEST_CODE_SEGUNDA_ACTIVIDAD = 1
            val intent = Intent(this, RegistroUsuarioSeleccionNacionalidad::class.java)
            intent.putExtra("tarjetasSeleccionadasNacionalidades", NacionalidadesSeleccionados)
            startActivityForResult(intent, REQUEST_CODE_SEGUNDA_ACTIVIDAD)
        }


        btnAñadirIdioma=findViewById(R.id.btnAñadirIdioma)
        btnAñadirIdioma.setOnClickListener {

            val REQUEST_CODE_SEGUNDA_ACTIVIDAD = 2
            val intent = Intent(this, RegistroUsuarioSeleccionIdioma::class.java)
            intent.putExtra("tarjetasSeleccionadasBefore", IdiomasSeleccionados)
            startActivityForResult(intent, REQUEST_CODE_SEGUNDA_ACTIVIDAD)

        }

        btnSiguiente = findViewById(R.id.RegistroUsuarioBtnSiguiente)

        btnSiguiente.setOnClickListener {

            var v = Validaciones()
            val dia = dbDia.selectedItem.toString().toInt()
            val mes = v.obtenerNumeroMes(dbMes.selectedItem.toString())
            val año = dbAño.selectedItem.toString().toInt()

            if (
                v.CharWritten(txtNombre, "Nombre", 30, 3, this) &&
                v.CharWritten(txtApellido, "Apelido", 30, 3, this) &&
                v.validarDUI(txtDUI, this) &&
                v.CharWritten(txtDomicilio, "Dirección", 50, 1, this) &&
                v.GenderSelected(genero, this) &&
                v.FechaReal(dia, mes, año, this)
            ){

                if(validarCampos())
                {
                    val dia = dbDia.selectedItem.toString().toIntOrNull()
                    val mes = obtenerNumeroMes(dbMes.selectedItem.toString())
                    val año = dbAño.selectedItem.toString().toIntOrNull()

                    if (dia != null && mes != null && año != null) {
                        if (validarFechaNacimiento(dia, mes, año)) {
                            if(genero==1 || genero==2)
                            {
                                val Reg = RegistroUsuarioValoresDeRegistro.edit()
                                Reg.putString("Nombre", txtNombre.text.toString())
                                Reg.putString("Apellido", txtApellido.text.toString())
                                Reg.putString("DUI", txtDUI.text.toString())

                                var fechaNacimiento = "$año/$mes/$dia"
                                Reg.putString("FechaNacimiento", fechaNacimiento)

                                if (IdiomasSeleccionados != null) {
                                    val idiomasString = IdiomasSeleccionados.joinToString(",")
                                    Reg.putString("IdiomasSeleccionados", idiomasString)
                                }

                                if (NacionalidadesSeleccionados != null) {
                                    val nacionalidadesString = NacionalidadesSeleccionados.joinToString(",")
                                    Reg.putString("NacionalidadesSeleccionados", nacionalidadesString)
                                }

                                var dep = dbDepartamento.selectedItem.toString()
                                var mun = dbMunicipio.selectedItem.toString()
                                var di = txtDomicilio.text.toString()
                                Reg.putString("Direccion", "$dep, $mun, $di")

                                if(genero==1)
                                {
                                    generotxt="Masculino"
                                }
                                else if (genero==2)
                                {
                                    generotxt="Femenino"
                                }

                                Reg.putString("Genero", generotxt)

                                Reg.putString("EstadoCivil", dbEstCivil.selectedItem.toString())
                                Reg.putString("TipoSangre", dbTipoSangre.selectedItem.toString())

                                Reg.apply()

                                val intent = Intent(this, RegistroUsuarioDatosPolicia::class.java)
                                startActivity(intent)
                            }
                            else
                            {
                                Toast.makeText(this, "Por favor seleccione su genero", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this, "Por favor ingrese una fecha valida", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Error al convertir los valores seleccionados", Toast.LENGTH_SHORT).show()
                    }
                }
            }





        }
    }

    //Eliminar toas las activities anteriores y dejar solo la actual
/*
    val intent = Intent(this, MiActividadActual::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
    startActivity(intent)
    finish()

 */

    fun obtenerDatosPorID(tabla: String, columnaDato: String): ArrayList<String> {
        val datos: ArrayList<String> = ArrayList()

        try {
            val statement = connection.createStatement()

            val resultSet = statement.executeQuery("SELECT * FROM $tabla")

            while (resultSet.next()) {
                val dato = resultSet.getString(columnaDato)
                datos.add(dato)
            }

            resultSet.close()
            statement.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        }

        return datos
    }

    fun obtenerNumeroMes(nombreMes: String): Int {
        val meses = listOf(
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        )
        return meses.indexOfFirst { it.equals(nombreMes, ignoreCase = true) } + 1
    }

    fun validarFechaNacimiento(dia: Int, mes: Int, año: Int): Boolean {
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

        // Calcular la edad actual
        var edad = añoActual - año
        if (mesActual < mes || (mesActual == mes && diaActual < dia)) {
            edad--
        }

        // Verificar si la persona es mayor de 18 años
        if (edad < 18) {
            return false
        }

        return true
    }

    private fun validarCampos(): Boolean {
        val nombre = txtNombre.text.toString().trim()
        val apellido = txtApellido.text.toString().trim()
        val dui = txtDUI.text.toString().trim()
        val domicilio = txtDomicilio.text.toString().trim()

        if (nombre.isEmpty() || apellido.isEmpty() || dui.isEmpty() || domicilio.isEmpty()) {
            Toast.makeText(this, "Rellene todos los campos", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    fun ActualizarLinearLayout(LL: LinearLayout, coso: ArrayList<String>) {
        try {
            LL.removeAllViews() // Eliminar vistas anteriores

            for (dato in coso) {
                val inflater = LayoutInflater.from(this)
                val tarjeta = inflater.inflate(R.layout.registro_usuario_card_element_selected, LL, false)

                val textoTarjeta = tarjeta.findViewById<TextView>(R.id.lblCardTextElement)
                val quitar = tarjeta.findViewById<TextView>(R.id.lblMinus)

                textoTarjeta.text = dato

                quitar.setOnClickListener {
                    coso.remove(dato)
                    LL.removeView(tarjeta)
                }

                LL.addView(tarjeta)
            }
        } catch (e: Exception) {
            println(e.toString())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            val tarjetasSeleccionadas = data?.getStringArrayListExtra("tarjetasSeleccionadasIdiomas")
            // Realiza la acción adicional con los datos obtenidos
            if (tarjetasSeleccionadas != null) {
                IdiomasSeleccionados.clear() // Elimina todos los elementos actuales de IdiomasSeleccionados
                IdiomasSeleccionados.addAll(tarjetasSeleccionadas) // Agrega todos los elementos de tarjetasSeleccionadas a IdiomasSeleccionados
                ActualizarLinearLayout(linearLayoutIdiomas, IdiomasSeleccionados)
            }
        }

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val tarjetasSeleccionadas = data?.getStringArrayListExtra("tarjetasSeleccionadasNacionalidades")
            // Realiza la acción adicional con los datos obtenidos
            if (tarjetasSeleccionadas != null) {
                NacionalidadesSeleccionados.clear() // Elimina todos los elementos actuales de IdiomasSeleccionados
                NacionalidadesSeleccionados.addAll(tarjetasSeleccionadas) // Agrega todos los elementos de tarjetasSeleccionadas a IdiomasSeleccionados
                ActualizarLinearLayout(linearLayoutNacionalidad, NacionalidadesSeleccionados)
            }
        }
    }


}
