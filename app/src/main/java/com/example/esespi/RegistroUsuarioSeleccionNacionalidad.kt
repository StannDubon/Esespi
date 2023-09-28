package com.example.esespi

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.sql.Connection

private lateinit var connSQL: conexionSQL
private lateinit var btnIdiomaAceptar: Button
private lateinit var linearLayout: LinearLayout
private lateinit var Busqueda: EditText
private lateinit var Buscar: ImageView

class RegistroUsuarioSeleccionNacionalidad : AppCompatActivity() {


    var tarjetasSeleccionadasOnNacionalidad = ArrayList<String>()


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_usuario_seleccion_nacionalidad)


        val tarjetasSeleccionadasBefore = intent.getSerializableExtra("tarjetasSeleccionadasNacionalidades") as? ArrayList<String>

        if (tarjetasSeleccionadasBefore != null) {
            tarjetasSeleccionadasOnNacionalidad.addAll(tarjetasSeleccionadasBefore)
        }


        linearLayout =findViewById(R.id.linearLayoutNacionalidadPicker) // Reemplaza "linearLayout" con el ID de tu LinearLayout
        connSQL = conexionSQL()
        Busqueda = findViewById(R.id.txtBusqudaNacionalidadSelector)
        Buscar = findViewById(R.id.btnBuscarNacionalidad)
        btnIdiomaAceptar = findViewById(R.id.btnNacionalidadSelectorAceptar)


        val conn = connSQL.dbConn()
        if (conn != null) {

            cargarTarjetasDesdeBD(conn, "select * from tbNacionalidades")


            Buscar.setOnClickListener {
                val searchTerm = Busqueda.text.toString()

                if (searchTerm == null) {
                    cargarTarjetasDesdeBD(conn, "select * from tbNacionalidades")

                } else {
                    cargarTarjetasDesdeBD(
                        conn,
                        "SELECT * FROM tbNacionalidades WHERE Nacionalidad COLLATE Latin1_General_CI_AI LIKE '$searchTerm%';"
                    )
                }

            }

            btnIdiomaAceptar.setOnClickListener {

                val title = "¿Tiene estas nacionalidades?"
                val content = "¿Esta seguro que cuenta las nacionalidades seleccionadas con documentación al respecto?"
                val option1 = "Si, estoy seguro"
                val option2 = "Cancelar"

                showCustomDialog(title, content, option1, option2, conn)

            }
        }
        
        else{
            Toast.makeText(this, "Error de conexion", Toast.LENGTH_SHORT).show()
        }
    }

    fun cargarTarjetasDesdeBD(conn: Connection?, query: String) {
        linearLayout.removeAllViews() // Elimina todas las tarjetas previas del LinearLayout

        val statement = conn?.createStatement()
        val resultSet = statement?.executeQuery(query)
        while (resultSet?.next() == true) {
            val lblNacionalidad = resultSet.getString("Nacionalidad")

            // Crea la tarjeta (card) para cada elemento
            val card = layoutInflater.inflate(R.layout.card_registro_usuario_idioma, null)
            val selector = card.findViewById<View>(R.id.selectedBooleanCircle)
            val textView = card.findViewById<TextView>(R.id.lblIdiomaOnCard)
            textView.text = lblNacionalidad

            if (tarjetasSeleccionadasOnNacionalidad.contains(lblNacionalidad)) {
                selector.setBackgroundResource(R.drawable.registro_ususario_circulo_seleccion_verdadero)
            }

            card.setOnClickListener {

                if (tarjetasSeleccionadasOnNacionalidad?.contains(lblNacionalidad) == true) {
                    selector.setBackgroundResource(R.drawable.registro_ususario_circulo_seleccion_falso) // Establece el fondo deseado cuando no está seleccionada
                    tarjetasSeleccionadasOnNacionalidad?.remove(lblNacionalidad) // Elimina lblIdioma de la lista de seleccionadas
                } else {
                    selector.setBackgroundResource(R.drawable.registro_ususario_circulo_seleccion_verdadero) // Establece el fondo deseado cuando está seleccionada
                    tarjetasSeleccionadasOnNacionalidad?.add(lblNacionalidad) // Agrega lblIdioma a la lista de seleccionadas
                }
            }

            // Agrega la tarjeta al contenedor
            linearLayout.addView(card)
        }
    }

    private fun showCustomDialog(
        title: String,
        content: String,
        option1: String,
        option2: String,
        conn: Connection?
    ) {
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

            /*
            val texto = NombrestarjetasSeleccionadas.joinToString(", ")
            Toast.makeText(applicationContext, texto, Toast.LENGTH_SHORT).show()

             */

            val intent = Intent(this, RegistroUsuarioIngresoDatos::class.java)
            intent.putExtra("tarjetasSeleccionadasNacionalidades", ArrayList(tarjetasSeleccionadasOnNacionalidad))
            setResult(Activity.RESULT_OK, intent)

            alertDialog.dismiss()
            conn?.close()
            finish()
        }

        // clic en el botón de opción 2
        option2TextView.setOnClickListener {
            alertDialog.dismiss()
            println("No esta seguro")
        }
    }
}