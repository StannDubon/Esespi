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

private lateinit var txtDui_Denunciante: EditText

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

        if(ActivityMode=="Agregar" || true){
            btnGuardar.setOnClickListener {
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
                                        "\t@IdGrupoPatrullaje = ?;"
                            )

                            addProducto.setString(1, dbDepartamento.selectedItem.toString() + ", " + dbMunicipio.selectedItem.toString() + ", " + txtDireccion.text.toString())
                            addProducto.setString(2, "$año/$mes/$dia $hora")
                            addProducto.setString(3, txtDui_Denunciante.text.toString())
                            addProducto.setString(4, triple.first)
                            addProducto.setString(5, txtDetalles.text.toString())
                            addProducto.setString(6, dbTipo.selectedItem.toString())
                            addProducto.setInt(7, IdGrupoGot)
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