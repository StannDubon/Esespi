package com.example.esespi

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException

private lateinit var txtAgregar_Nombre_Infractor:TextView
private lateinit var txtAgregar_Apellido_Infractor:TextView
private lateinit var txtAgregar_DUI_infractor:TextView
private lateinit var btnInfractor_Masculino:Button
private lateinit var btnInfractor_Femenino:Button
private lateinit var btncancelar_Infractor:Button
private lateinit var btnguardar_Infractor:Button

private lateinit var dbdepartamentos:Spinner

private lateinit var spinner1:Spinner
private lateinit var spinner2:Spinner

private var genero: Int = 0

private  var RegistroDepartamentoInfractor  = mapOf(
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
private var RegistroMunicipioInfractor = arrayOf(
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



class InfractoresAgregarInfractor : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_infractores_agregar_infractor)


        txtAgregar_Nombre_Infractor = findViewById(R.id.txtAgregar_Nombre_Infractor)
        txtAgregar_Apellido_Infractor = findViewById(R.id.txtAgregar_Apellido_Infractor)
        txtAgregar_DUI_infractor = findViewById(R.id.txtAgregar_DUI_infractor)
        btnInfractor_Masculino = findViewById(R.id.btnInfractor_Masculino)
        btnInfractor_Femenino = findViewById(R.id.btnInfractor_Femenino)
        btncancelar_Infractor = findViewById(R.id.btncancelar_Infractor)
        btnguardar_Infractor = findViewById(R.id.btnguardar_Infractor)




        btnInfractor_Masculino.setOnClickListener {
            genero = 1
            btnInfractor_Masculino.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#4399FF"))
            btnInfractor_Masculino.setTextColor(Color.parseColor("#FFFFFF"))

            btnInfractor_Femenino.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D8D8D8"))
            btnInfractor_Femenino.setTextColor(Color.parseColor("#686868"))
        }

        btnInfractor_Femenino.setOnClickListener {
            genero = 2
            btnInfractor_Femenino.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#4399FF"))
            btnInfractor_Femenino.setTextColor(Color.parseColor("#FFFFFF"))

            btnInfractor_Masculino.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D8D8D8"))
            btnInfractor_Masculino.setTextColor(Color.parseColor("#686868"))
        }



        val connection = conexionSQL().dbConn()

        // Referencia a xml para direccion
        spinner1 = findViewById(R.id.RegistroDepartamentoInfractor)
        spinner2 = findViewById(R.id.RegistroMunicipioInfractor)


        // Configurar el Spinner de Departamentos y Municipios
        val departamentoAdapter =
            ArrayAdapter(this@InfractoresAgregarInfractor, android.R.layout.simple_spinner_item, RegistroMunicipioInfractor)
        departamentoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner1.adapter = departamentoAdapter

        //Añadir interaccion a departamentos y municipios
        spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val departamento = parent.getItemAtPosition(position).toString()
                val municipios = RegistroDepartamentoInfractor[departamento] ?: arrayOf()
                val municipioAdapter = ArrayAdapter(
                    this@InfractoresAgregarInfractor,
                    android.R.layout.simple_spinner_item,
                    municipios
                )
                municipioAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner2.adapter = municipioAdapter

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No se seleccionó ningún departamento
            }
        }

        btncancelar_Infractor.setOnClickListener {
            showCustomDialog(
                "¿Descartar cambios?",
                "Al hacer esto, se eliminaran los cambios en el formulario actual. ¿Desea descartar los cambios?",
                "Continuar",
                "Cancelar",
                "CancelarInsercion",
                connection
            )
        }

        btnguardar_Infractor.setOnClickListener {
            showCustomDialog(
                "¿Generar Infractor?",
                "¿Esta seguro que desea guardar este infractor en la base de datos?",
                "Agregar",
                "Cancelar",
                "IngresarDatos",
                connection
            )
        }
    }

    private fun showCustomDialog(title: String, content: String, option1: String, option2: String, Accion: String, conn: Connection?) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_layout, null)

        val titleTextView = dialogView.findViewById<TextView>(R.id.title)
        val contentTextView = dialogView.findViewById<TextView>(R.id.content)
        val option1TextView = dialogView.findViewById<TextView>(R.id.op1)
        val option2TextView = dialogView.findViewById<TextView>(R.id.op2)

        titleTextView.text = title
        contentTextView.text = content
        option1TextView.text = option1
        option2TextView.text = option2



        option1TextView.setBackgroundColor(Color.parseColor("#FFC634"))
        option1TextView.setTextColor(Color.parseColor("#FFFFFF"))

        val alertDialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()


        // clic en el botón de opción 1
        option1TextView.setOnClickListener {
            if(Accion=="CancelarInsercion"){
                finish()
            }
            if(Accion=="IngresarDatos")
            {
                AgregarInfractorADB()
            }
        }

        // clic en el botón de opción 2
        option2TextView.setOnClickListener {
            alertDialog.dismiss()
            println("No esta seguro")
        }
    }


    private fun AgregarInfractorADB(){

        try {
            val connection = conexionSQL().dbConn() ?: throw SQLException("No se pudo establecer la conexión a la base de datos")
            // Preparar la sentencia SQL para la inserción de datos en tbPersonas
            val sentenciaPersonas = "DECLARE @dui VARCHAR(10) = ?;\n" +
                    "DECLARE @idTipopersona_persona INT;\n" +
                    "DECLARE @idTipopersona INT;\n" +
                    "\n" +
                    "-- Get the IdTipoPersona using the function getIdTipoPersona1\n" +
                    "SELECT @idTipopersona = dbo.getIdTipoPersona1('Infractor');\n" +
                    "\n" +
                    "-- Insert data into tbPersonas\n" +
                    "INSERT INTO tbPersonas (Nombre, Apellido, DireccionDomicilio, Dui, idGenero) \n" +
                    "VALUES (?, ?, ?, @dui, ?);\n" +
                    "\n" +
                    "-- Insert data into tbTiposPersonas_Personas\n" +
                    "INSERT INTO tbTiposPersonas_Personas (IdTipoPersona, IdPersona)\n" +
                    "VALUES (@idTipopersona, SCOPE_IDENTITY());\n" +
                    "\n" +
                    "-- Get the inserted IdTipoPersonas_Personas\n" +
                    "SELECT @idTipopersona_persona = SCOPE_IDENTITY();\n" +
                    "\n" +
                    "-- Insert data into tbInfractores\n" +
                    "INSERT INTO tbInfractores (IdEstadoInfractor, IdTipoPersonas_Personas)\n" +
                    "VALUES (1, @idTipopersona_persona);"
            val preparedStatementPersonas: PreparedStatement = connection.prepareStatement(sentenciaPersonas)

            preparedStatementPersonas.setString(1, txtAgregar_DUI_infractor.text.toString())
            preparedStatementPersonas.setString(2, txtAgregar_Nombre_Infractor.text.toString())
            preparedStatementPersonas.setString(3, txtAgregar_Apellido_Infractor.text.toString())
            preparedStatementPersonas.setString(4, spinner1.selectedItem.toString()+", "+ spinner2.selectedItem.toString())
            preparedStatementPersonas.setInt(5, genero)
            preparedStatementPersonas.executeUpdate()
        } catch (ex:SQLException){
            println("Error ocurrido en la insercion en tbPersonas")
        }
    }

}