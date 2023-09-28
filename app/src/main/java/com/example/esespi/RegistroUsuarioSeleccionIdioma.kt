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


class RegistroUsuarioSeleccionIdioma : AppCompatActivity() {


    var tarjetasSeleccionadasOnIdiomas = ArrayList<String>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_usuario_seleccion_idioma)


        val tarjetasSeleccionadasBefore = intent.getSerializableExtra("tarjetasSeleccionadasBefore") as? ArrayList<String>

        if (tarjetasSeleccionadasBefore != null) {
            tarjetasSeleccionadasOnIdiomas.addAll(tarjetasSeleccionadasBefore)
        }

        linearLayout = findViewById(R.id.linearLayoutIdiomaPicker) // Reemplaza "linearLayout" con el ID de tu LinearLayout
        connSQL = conexionSQL()
        Busqueda = findViewById(R.id.txtBusqudaIdiomaSelector)
        Buscar = findViewById(R.id.btnBuscar)
        btnIdiomaAceptar = findViewById(R.id.btnIdiomaSelectorAceptar)


        val conn = connSQL.dbConn()
        if (conn != null) {

            cargarTarjetasDesdeBD(conn, "select * from tbIdiomas")


            Buscar.setOnClickListener {
                val searchTerm = Busqueda.text.toString()

                if (searchTerm==null)
                {
                    cargarTarjetasDesdeBD(conn, "select * from tbIdiomas")
                    Toast.makeText(applicationContext, tarjetasSeleccionadasOnIdiomas.joinToString(", "), Toast.LENGTH_SHORT).show()
                }

                else
                {
                    cargarTarjetasDesdeBD(conn, "SELECT * FROM tbIdiomas WHERE Idioma COLLATE Latin1_General_CI_AI LIKE '$searchTerm%';")
                }

            }

            btnIdiomaAceptar.setOnClickListener {

                val title = "¿Sabe estos idiomas?"
                val content = "¿Esta seguro que sabe hablar de una manera fluida los idiomas que ha seleccioando?"
                val option1 = "Si, estoy seguro"
                val option2 = "Cancelar"

                showCustomDialog(title, content, option1, option2, conn)

            }
        }
    }

    fun cargarTarjetasDesdeBD(conn: Connection?, query: String) {

        linearLayout.removeAllViews() // Elimina todas las tarjetas previas del LinearLayout

        val statement = conn?.createStatement()
        val resultSet = statement?.executeQuery(query)
        while (resultSet?.next() == true) {
            val lblIdioma = resultSet.getString("Idioma")

            // Crea la tarjeta (card) para cada elemento
            val card = layoutInflater.inflate(R.layout.card_registro_usuario_idioma, null)
            val selector = card.findViewById<View>(R.id.selectedBooleanCircle)
            val textView = card.findViewById<TextView>(R.id.lblIdiomaOnCard)
            textView.text = lblIdioma

            if (tarjetasSeleccionadasOnIdiomas.contains(lblIdioma)) {
                selector.setBackgroundResource(R.drawable.registro_ususario_circulo_seleccion_verdadero)
            }

            card.setOnClickListener {

                if (tarjetasSeleccionadasOnIdiomas.contains(lblIdioma) == true) {
                    selector.setBackgroundResource(R.drawable.registro_ususario_circulo_seleccion_falso) // Establece el fondo deseado cuando no está seleccionada
                    tarjetasSeleccionadasOnIdiomas.remove(lblIdioma) // Elimina lblIdioma de la lista de seleccionadas
                } else {
                    selector.setBackgroundResource(R.drawable.registro_ususario_circulo_seleccion_verdadero) // Establece el fondo deseado cuando está seleccionada
                    tarjetasSeleccionadasOnIdiomas.add(lblIdioma) // Agrega lblIdioma a la lista de seleccionadas
                }
            }

            // Agrega la tarjeta al contenedor
            linearLayout.addView(card)
        }
    }

    private fun showCustomDialog(title: String, content: String, option1: String, option2: String, conn: Connection?) {
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
            intent.putExtra("tarjetasSeleccionadasIdiomas", ArrayList(tarjetasSeleccionadasOnIdiomas))
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