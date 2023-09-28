package com.example.esespi

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
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
import androidx.core.content.ContextCompat
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException
import java.text.SimpleDateFormat
import java.util.Calendar

private lateinit var txtDui_Denunciante: EditText
private lateinit var txtNombre_Denunciante: EditText
private lateinit var txtApellido_Denunciante: EditText

private lateinit var txtDireccion: EditText
private lateinit var txtDetalles: EditText
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

private var ActivityMode: String? = null
private var SelectedInfractores: ArrayList<Triple<String, String, ByteArray?>> = ArrayList()
private var MULTI_INFRACTOR_REQUEST = 1

class Denuncias_agregar : AppCompatActivity() {
    @SuppressLint("ResourceType", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_denuncias_agregar)

        conn = conexionSQL().dbConn() ?: throw SQLException("No se pudo establecer la conexión a la base de datos")

        txtDui_Denunciante = findViewById(R.id.Denuncias_agregar_txtDUI_Denunciante)
        txtNombre_Denunciante = findViewById(R.id.Denuncias_agregar_txtNombre_Denunciante)
        txtApellido_Denunciante = findViewById(R.id.Denuncias_agregar_txtApellido_Denunciante)

        txtDetalles = findViewById(R.id.Denuncias_agregar_txtDetalles)
        txtDireccion = findViewById(R.id.Denuncias_agregar_txtDireccion)
        dbDepartamento = findViewById(R.id.Denuncias_agregar_DbDepartamento)
        dbMunicipio = findViewById(R.id.Denuncias_agregar_DbMunicipio)

        dbTipo = findViewById(R.id.Denuncias_agregar_DbTipoDenuncia)

        txtHora = findViewById(R.id.Denuncias_agregar_txtHora)
        lblHora = findViewById(R.id.Denuncias_agregar_lblHora)

        dbDia = findViewById(R.id.Denuncias_agregar_DbDia)
        dbMes = findViewById(R.id.Denuncias_agregar_DbMes)
        dbAño = findViewById(R.id.Denuncias_agregar_DbAño)

        btnAgregarInfractor = findViewById(R.id.Denuncias_agregar_btnSeleccionarInfractor)
        llAgregarInfractor = findViewById(R.id.Denuncias_agregar_LlInfractoresSelected)

        btnGuardar = findViewById(R.id.Denuncias_agregar_btnAgregar)
        btnDescartar = findViewById(R.id.Denuncias_agregar_btnDescartar)

        ActivityMode = intent.getStringExtra("mode")

        findViewById<ImageView>(R.id.Denuncias_agregar_btnQuit).setOnClickListener {
            finish()
        }

        // SPINNER -----------------------------------------------------------------------------------------

        val clasificaciones = ArrayList<String>()
        try {
            val statement = conn.createStatement()
            val query = "SELECT * FROM tbCategoriaDelito"
            val resultSet = statement?.executeQuery(query)

            clasificaciones.add("Seleccione una opcion...")

            while (resultSet?.next() == true) {
                val clasificacion = resultSet.getString("Categoria")
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
            intent.putExtra("mode", "MultiInfractor")
            startActivityForResult(intent, MULTI_INFRACTOR_REQUEST)
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
        val departamentoAdapter = ArrayAdapter(this@Denuncias_agregar, R.drawable.custom_spinner_adapter, departamentos)
        departamentoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dbDepartamento.adapter = departamentoAdapter
        //Añadir interaccion a departamentos y municipios
        dbDepartamento.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val departamento = parent.getItemAtPosition(position).toString()
                val municipios = municipiosPorDepartamento[departamento] ?: arrayOf()
                val municipioAdapter = ArrayAdapter(this@Denuncias_agregar, R.drawable.custom_spinner_adapter, municipios)
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


//SUBIR A LA BASE DE DATOS -------------------------------------------------------------------------

        if(ActivityMode=="Agregar"){
            btnGuardar.setOnClickListener {

                var v = Validaciones()
                val dui = txtDui_Denunciante.text.toString().trim()
                if (

                    v.validarDUI(txtDui_Denunciante, this)

                ) {
                    if(duiExiste(dui))
                    {
                        Toast.makeText(this, "El DUI ya existe en la base de datos.", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener // Salir de la función si el DUI existe}

                    }
                    else{
                        for (triple in SelectedInfractores) {

                            val dia = dbDia.selectedItem.toString().toInt()
                            val mes = Validaciones().obtenerNumeroMes(dbMes.selectedItem.toString())
                            val año = dbAño.selectedItem.toString().toInt()

                            if (
                                dbTipo.selectedItemPosition != 0
                            ) {
                                try {
                                    val addProducto: PreparedStatement = conn.prepareStatement(
                                        "EXEC dbo.InsertarDenuncias\n" +
                                                "\t@Lugar = ?,\n" +
                                                "\t@Fecha = ?,\n" +
                                                "\t@DuiDenunciante = ?,\n" +
                                                "\t@DuiInfractor = ?,\n" +
                                                "\t@Delito = ?,\n" +
                                                "\t@CategoriaDelito = ?,\n" +
                                                "\t@NombreDenun = ?,\n" +
                                                "\t@ApellidoDenun = ?,\n" +
                                                "\t@IdGrupoPatrullaje = ?;"
                                    )

                                    addProducto.setString(
                                        1,
                                        dbDepartamento.selectedItem.toString() + ", " + dbMunicipio.selectedItem.toString() + ", " + txtDireccion.text.toString()
                                    )
                                    addProducto.setString(2, "$año/$mes/$dia $hora")
                                    addProducto.setString(3, txtDui_Denunciante.text.toString())
                                    addProducto.setString(4, triple.first)
                                    addProducto.setString(5, txtDetalles.text.toString())
                                    addProducto.setString(6, dbTipo.selectedItem.toString())
                                    addProducto.setString(7, txtNombre_Denunciante.text.toString())
                                    addProducto.setString(8, txtApellido_Denunciante.text.toString())
                                    addProducto.setInt(9, IdGrupoGot)
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
                    }



                }
                conn.close()
            }
        }

        else if (ActivityMode=="Editar"){

            val GotId = intent.getIntExtra("id", 0)
            val GotDireccion = intent.getStringExtra("dir")
            val GotFecha = intent.getStringExtra("fec")
            val GotDuiDenunciante = intent.getStringExtra("dui_denunciante")
            val GotIdInforme = intent.getStringExtra("infor")
            val GotNombre = intent.getStringExtra("nom")
            val GotApellido = intent.getStringExtra("ape")

            val GotTipoDelito = intent.getStringExtra("tipdel")
            val GotDelito = intent.getStringExtra("delit")

            val DireccionFull = GotDireccion?.split(", ")

            val departamento = DireccionFull?.get(0)
            val municipio = DireccionFull?.get(1)
            val direccion = DireccionFull?.drop(2)?.joinToString(", ")

            txtDui_Denunciante.setText(GotDuiDenunciante)
            txtNombre_Denunciante.setText(GotNombre)
            txtApellido_Denunciante.setText(GotApellido)

            txtDetalles.setText(GotDelito)
            txtDireccion.setText(direccion)

            val indiceSeleccion = clasificaciones.indexOf(GotTipoDelito)

            if (indiceSeleccion != -1) {
                dbTipo.setSelection(indiceSeleccion)
            }

            val selectedInfractores = intent.getParcelableArrayListExtra<MyParcelableTriple>("selectedInfractores")
            llAgregarInfractor.removeAllViews()

            val convertedList = ArrayList<Triple<String, String, ByteArray?>>()

            selectedInfractores?.forEach { item ->
                val triple = Triple(item.first, item.second, item.third)
                convertedList.add(triple as Triple<String, String, ByteArray?>)
            }

            // Asigna la lista convertida a la variable global
            SelectedInfractores = convertedList

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
                    val municipioAdapter = ArrayAdapter(this@Denuncias_agregar, R.drawable.custom_spinner_adapter, municipios)
                    municipioAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    dbMunicipio.adapter = municipioAdapter

                    // Encontrar el índice del municipio actual en el arreglo de municipios
                    val municipioIndex = municipios.indexOf(municipio)
                    if (municipioIndex != -1) {
                        dbMunicipio.setSelection(municipioIndex)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // No se seleccionó ningún departamento
                }
            }

            // Suponiendo que SplitDepartamento y SplitMunicipio tienen los valores correctos
            val departamentoIndex = departamentos.indexOf(departamento)
            if (departamentoIndex != -1) {
                dbDepartamento.setSelection(departamentoIndex)
            }


            for (item in SelectedInfractores) {
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
                    ContextCompat.getColor(this@Denuncias_agregar, R.color.DetenidosSelected))

                btnDel.setOnClickListener{
                    //LlInfractoresSeleccionados.removeView(cardView)
                    //SelectedDetenidos.remove(item)
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
                llAgregarInfractor.addView(cardView)
            }

            btnGuardar.setOnClickListener {

                val dia = dbDia.selectedItem.toString().toInt()
                val mes = Validaciones().obtenerNumeroMes(dbMes.selectedItem.toString())
                val año = dbAño.selectedItem.toString().toInt()

                if (
                    true
                ){

                    try {
                        val addProducto: PreparedStatement =  conn.prepareStatement(
                            "EXEC dbo.ActualizarDenuncia\n" +
                                    "@Lugar = ?,\n" +
                                    "@Fecha = ?,\n" +
                                    "@DuiDenunciante = ?,\n" +
                                    "@NombreDenunciante = ?,\n" +
                                    "@ApellidoDenunciante = ?,\n" +
                                    "@IdDenuncia = ?;"
                        )!!

                        addProducto.setString(1, dbDepartamento.selectedItem.toString() + ", " + dbMunicipio.selectedItem.toString() + ", " + txtDireccion.text.toString())
                        addProducto.setString(2, "$año/$mes/$dia $hora")
                        addProducto.setString(3, txtDui_Denunciante.text.toString())
                        addProducto.setString(4, txtNombre_Denunciante.text.toString())
                        addProducto.setString(5, txtApellido_Denunciante.text.toString())
                        addProducto.setInt(6, GotId)
                        addProducto.executeUpdate()

                        println(
                            "EXEC dbo.ActualizarDenuncia\n" +
                                    "@Lugar = ${dbDepartamento.selectedItem.toString() + ", " + dbMunicipio.selectedItem.toString() + ", " + txtDireccion.text.toString()},\n" +
                                    "@Fecha = ${"$año/$mes/$dia $hora"},\n" +
                                    "@DuiDenunciante = ${txtDui_Denunciante.text.toString()},\n" +
                                    "@NombreDenunciante = ${txtNombre_Denunciante.text.toString()},\n" +
                                    "@ApellidoDenunciante = ${txtApellido_Denunciante.text.toString()},\n" +
                                    "@IdDenuncia = ${GotId};"
                        )

                        for (triple in SelectedInfractores){
                            val addProductoxd: PreparedStatement =  conn.prepareStatement(
                                "EXEC dbo.ActualizarDenunciaAddInvlDeli \n" +
                                        "@DuiInfr = ?,\n" +
                                        "@IdDenuncia = ?,\n" +
                                        "@Delito = ?,\n" +
                                        "@CategoriaDelito = ?"
                            )!!

                            addProductoxd.setString(1, triple.first)
                            addProductoxd.setInt(2, GotId)
                            addProductoxd.setString(3, txtDetalles.text.toString())
                            addProductoxd.setString(4, dbTipo.selectedItem.toString())
                            addProductoxd.executeUpdate()

                            println(
                                "EXEC dbo.ActualizarDenunciaAddInvlDeli \n" +
                                        "@DuiInfr = ${triple.first},\n" +
                                        "@IdDenuncia = ${GotId},\n" +
                                        "@Delito = ${txtDetalles.text.toString()},\n" +
                                        "@CategoriaDelito = ${dbTipo.selectedItem.toString()}"
                            )
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

    fun duiExiste(dui: String): Boolean {
        try {
            val statement = conn.createStatement()
            val query = "SELECT COUNT(*) AS count FROM tbPersonas WHERE Dui = ?"
            val preparedStatement = conn.prepareStatement(query)
            preparedStatement.setString(1, dui)
            val resultSet = preparedStatement.executeQuery()

            if (resultSet.next()) {
                val count = resultSet.getInt("count")
                return count > 0 // Si count es mayor que 0, significa que el DUI ya existe.
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return false // Si ocurre una excepción o no se encuentra el DUI, asumimos que no existe.
    }

    private fun onTimeSelected(time:String){
        val hora_parsed = SimpleDateFormat("HH:mm:ss").parse(time)
        val hora_formateada = SimpleDateFormat("hh:mm a").format(hora_parsed)

        lblHora.text = hora_formateada
        hora = time
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == MULTI_INFRACTOR_REQUEST && resultCode == Activity.RESULT_OK) {
            // Comprueba si el extra "selectedInfractores" está presente en el Intent resultante
            if (data != null && data.hasExtra("selectedInfractores")) {

                val selectedInfractores = data.getParcelableArrayListExtra<MyParcelableTriple>("selectedInfractores")

                val selectedInfractoresUnique = selectedInfractores?.mapNotNull {
                    Triple(it.first ?: "", it.second ?: "", it.third)
                }

                llAgregarInfractor.removeAllViews()

                if (selectedInfractoresUnique != null) {
                    SelectedInfractores.addAll(selectedInfractoresUnique)
                }
                val uniqueInfractores = SelectedInfractores.distinctBy { it.first }

                SelectedInfractores.clear()
                SelectedInfractores.addAll(uniqueInfractores)

                for (data in SelectedInfractores) {
                    // Desempaqueta los datos del array
                    val DUI = data.first
                    val Nombre = data.second
                    val Foto = data.third

                    // Infla una CardView
                    val cardView = layoutInflater.inflate(R.layout.card_infractores_infractor_select, null)

                    val lblNombre = cardView.findViewById<TextView>(R.id.Infractores_card_infractor_seleccion_lblNombre)
                    val lblDui = cardView.findViewById<TextView>(R.id.Infractores_card_infractor_seleccion_lblDui)
                    val imgInfractor = cardView.findViewById<ImageView>(R.id.Infractores_card_infractor_seleccion_imgInfractor)
                    val btnInfo = cardView.findViewById<LinearLayout>(R.id.Infractores_card_infractor_seleccion_info)

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

                    btnInfo.setOnClickListener{
                        llAgregarInfractor.removeView(cardView)
                        SelectedInfractores.remove(data)
                    }



                    // Finalmente añadir la card al LinearLayout
                    llAgregarInfractor.addView(cardView)
                }
            }
        }
    }
}