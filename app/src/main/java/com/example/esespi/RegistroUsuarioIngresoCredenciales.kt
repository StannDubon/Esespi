package com.example.esespi
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException
import java.sql.Types

class RegistroUsuarioIngresoCredenciales : AppCompatActivity() {

    private lateinit var txtUsuario: EditText
    private lateinit var txtContraseña: EditText
    private lateinit var txtConfirmarContraseña: EditText
    private lateinit var btnRegistrarse: Button
    private var numPlaca: String = "jaja placa"

    private lateinit var sharedPrefs: SharedPreferences

    private lateinit var connection: Connection

    private var DUI: String = "no hay DUI"

    private var IdRangoTipoUsuario: Int =4 // Debes establecer el valor correcto
    private var contraseñaEncriptada: String = ""

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_usuario_ingreso_credenciales)

        txtUsuario = findViewById(R.id.txtUsuario)
        txtContraseña = findViewById(R.id.RegistroUsuarioIngresoCredencialestxtContraseña)
        txtConfirmarContraseña = findViewById(R.id.RegistroUsuarioIngresoCredencialestxtConfirmacionContraseña)
        btnRegistrarse = findViewById(R.id.RegistroUsuarioIngresoCredencialesBtnRegistrarse)


        sharedPrefs = getSharedPreferences("datos_ingreso", Context.MODE_PRIVATE)

        connection = conexionSQL().dbConn()
            ?: throw SQLException("No se pudo establecer la conexión a la base de datos")

        btnRegistrarse.setOnClickListener {

            var v = Validaciones()

            val usuario = txtUsuario.text.toString().trim()
            if (usuarioExiste(usuario)) {
                // El usuario ya existe en la base de datos, muestra un mensaje de error
                Toast.makeText(this, "El usuario ya está registrado.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else
            {
                if (v.CharWritten(txtContraseña, "La Contraseña", 15, 8, this)&&
                    v.CharWritten(txtConfirmarContraseña, "La confirmacion de contraseña", 15, 8, this)){
                    if (txtContraseña.text.toString() == txtConfirmarContraseña.text.toString()) {

                        try {
                            val RegistroUsuarioValoresDeRegistro =
                                getSharedPreferences("datos_ingreso", Context.MODE_PRIVATE)
                            val valores = RegistroUsuarioValoresDeRegistro.all

                            DUI = sharedPrefs.getString("DUI", "").toString()
                            numPlaca = sharedPrefs.getString("NumeroPlaca", "").toString()


                            for ((clave, valor) in valores) {
                                println("Clave: $clave - Valor: $valor")


                            }

                            android.util.Log.d("Depuración", "Referencias")
                            val userData =
                                getSharedPreferences("datos_ingreso", Context.MODE_PRIVATE)
                            val valor = userData.all


                            for ((clave, valor) in valor) {
                                println("Clave: $clave - Valor: $valor")


                            }

                            contraseñaEncriptada = Encriptacion().convertirSHA256(txtConfirmarContraseña.text.toString())

                            insertarDatosEnBaseDeDatos()
                            insertarReferenciasDatosEnBaseDeDatos()

                            android.util.Log.d("Depuración", "Ingresa datos")



                            //InsertarIdiomasPorUsuario()
                            //InsertarNacionalidadesPorUsuario()

                            val sharedPreferences =
                                getSharedPreferences("datos_ingreso", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.clear() // Elimina todos los datos almacenados en SharedPreferences
                            editor.apply()
                            if (!isFinishing) {
                                runOnUiThread {
                                    val customDialog = ShowCustomDialogImage(this@RegistroUsuarioIngresoCredenciales)
                                    customDialog.showCustomDialog(R.drawable.dialog_check, "Registro exitoso", "Aceptar", MainActivity::class.java, 1)
                                }
                            }

                        } catch (e: SQLException) {
                            /* if (!isFinishing) {
                                 runOnUiThread {
                                     val customDialog =
                                         ShowCustomDialogImage(this@RegistroUsuarioIngresoCredenciales)
                                     customDialog.showCustomDialog(
                                         R.drawable.dialog_cross,
                                         "Ocurrió un error",
                                         "Aceptar",
                                         MainActivity::class.java,
                                         3
                                     )
                                 }
                             }*/
                        }
                    }
                }

            }


        }
    }

    fun insertarDatosEnBaseDeDatos() {

        val statement = connection.createStatement()

        val SQLgenero = sharedPrefs.getString("Genero", "")
        val SQLestadoCivil = sharedPrefs.getString("EstadoCivil", "")
        val SQLtipoSangre = sharedPrefs.getString("TipoSangre", "")

        var IdGenero = -1
        var IdEstadoCivil = -1
        var IdTipoSangre = -1

        val resultSet1 =
            statement.executeQuery("select IdGenero from tbGeneros where Genero='$SQLgenero'")
        if (resultSet1.next()) {
            IdGenero = resultSet1.getInt("IdGenero")
        }
        resultSet1.close()

        val resultSet2 =
            statement.executeQuery("select IdEstadoCivil from tbEstadosCivil where EstadoCivil='$SQLestadoCivil'")
        if (resultSet2.next()) {
            IdEstadoCivil = resultSet2.getInt("IdEstadoCivil")
        }
        resultSet2.close()

        val resultSet3 =
            statement.executeQuery("select IdTipoSangre from tbTiposSangre where TipoSangre='$SQLtipoSangre'")
        if (resultSet3.next()) {
            IdTipoSangre = resultSet3.getInt("IdTipoSangre")
        }
        resultSet3.close()

        android.util.Log.d("Depuración", "Todos los id que manda")

        try {
            val addPolicia: PreparedStatement = connection.prepareStatement(
                "EXEC dbo.InsertarPoliciasAndroid " +
                        "@Nombre = ?, " +
                        "@Apellido = ?, " +
                        "@FechaNacimiento = ?, " +
                        "@Direccion = ?, " +
                        "@Dui = ?, " +
                        "@IdEstadoCivil = ?, " +
                        "@IdTipoSangre = ?, " +
                        "@IdGenero = ?, " +
                        "@CorreoElectronico = ?, " +
                        "@NumeroTel = ?, " +
                        "@ONI = ?, " +
                        "@NumeroPlaca = ?, " +
                        "@Foto = ?, " +
                        "@Usuario = ?, " +
                        "@Contrasena = ?, " +
                        "@IdRangoTipoUsuario = ?"
            )

            addPolicia.setString(1, sharedPrefs.getString("Nombre", ""))
            addPolicia.setString(2, sharedPrefs.getString("Apellido", ""))
            addPolicia.setString(3, sharedPrefs.getString("FechaNacimiento", ""))
            addPolicia.setString(4, sharedPrefs.getString("Direccion", ""))
            addPolicia.setString(5, DUI)
            addPolicia.setInt(6, IdEstadoCivil)
            addPolicia.setInt(7, IdTipoSangre)
            addPolicia.setInt(8, IdGenero)
            addPolicia.setString(9, sharedPrefs.getString("Correo", ""))
            addPolicia.setString(10, sharedPrefs.getString("Telefono", ""))
            addPolicia.setString(11, sharedPrefs.getString("ONI", ""))
            addPolicia.setString(12, sharedPrefs.getString("NumeroPlaca", ""))
            addPolicia.setNull(13, Types.VARBINARY)
            addPolicia.setString(14, txtUsuario.text.toString())
            addPolicia.setString(15, contraseñaEncriptada)
            addPolicia.setInt(16, 7)

            addPolicia.executeUpdate()

            // Log success or handle it as needed.
            android.util.Log.d("Depuración", "Datos ingresados correctamente")


        } catch (ex: SQLException) {
            // Log the error for debugging purposes.
            android.util.Log.e("Error", "Error al ingresar datos", ex)

            // Display a user-friendly message.
            Toast.makeText(
                this,
                "Error al ingresar datos. Por favor, inténtelo de nuevo.",
                Toast.LENGTH_SHORT
            ).show()

            // Handle the error gracefully.
            ex.printStackTrace()
            setResult(RESULT_OK, Intent())
        }
    }

    fun usuarioExiste(usuario: String): Boolean {
        try {
            val statement = connection.createStatement()
            val query = "SELECT COUNT(*) AS count FROM tbUsuarios WHERE Usuario = ?"
            val preparedStatement = connection.prepareStatement(query)
            preparedStatement.setString(1, usuario)
            val resultSet = preparedStatement.executeQuery()

            if (resultSet.next()) {
                val count = resultSet.getInt("count")
                return count > 0 // Si count es mayor que 0, significa que el usuario ya existe.
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return false // Si ocurre una excepción o no se encuentra el usuario, asumimos que no existe.
    }

    fun InsertarIdiomasPorUsuario(){
        try {
            val idiomasString = sharedPrefs.getString("IdiomasSeleccionados", "")
            val idiomasSeleccionados: ArrayList<String> = ArrayList(idiomasString?.split(","))
            val sqlQueries = mutableListOf<String>()

            for (idioma in idiomasSeleccionados) {
                val sqlQuery = "EXEC dbo.InsertarPoliciasIDIOMASAndroid @IdIdioma = ?"
                sqlQueries.add(sqlQuery)
            }

            val statement = connection.createStatement()
            for (query in sqlQueries) {
                statement.executeUpdate(query)
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            println(e.toString())
            println("Error ocurrido en insercion de idiomas del policía")
        }
    }

    fun InsertarNacionalidadesPorUsuario() {
        try {
            val nacionalidadesString = sharedPrefs.getString("NacionalidadesSeleccionados", "")
            val nacionalidadesSeleccionadas: ArrayList<String> =
                ArrayList(nacionalidadesString?.split(","))
            val sqlQueries = mutableListOf<String>()

            for (nacionalidad in nacionalidadesSeleccionadas) {
                val sqlQuery = "EXEC dbo.InsertarPoliciasNACIONALIDADAndroid @IdNacionalidad = ?"
                sqlQueries.add(sqlQuery)
            }

            val statement = connection.createStatement()
            for (query in sqlQueries) {
                statement.executeUpdate(query)
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            println(e.toString())
            println("Error ocurrido en insercion de nacionalidades del policía")
        }
    }

    fun insertarReferenciasDatosEnBaseDeDatos() {

        val statement = connection.createStatement()

        val SQLgenero = sharedPrefs.getString("Genero", "")
        val SQLestadoCivil = sharedPrefs.getString("EstadoCivil", "")
        val SQLtipoSangre = sharedPrefs.getString("TipoSangre", "")

        var IdGenero = -1
        var IdEstadoCivil = -1
        var IdTipoSangre = -1

        val resultSet1 =
            statement.executeQuery("select IdGenero from tbGeneros where Genero='$SQLgenero'")
        if (resultSet1.next()) {
            IdGenero = resultSet1.getInt("IdGenero")
        }
        resultSet1.close()

        val resultSet2 =
            statement.executeQuery("select IdEstadoCivil from tbEstadosCivil where EstadoCivil='$SQLestadoCivil'")
        if (resultSet2.next()) {
            IdEstadoCivil = resultSet2.getInt("IdEstadoCivil")
        }
        resultSet2.close()

        val resultSet3 =
            statement.executeQuery("select IdTipoSangre from tbTiposSangre where TipoSangre='$SQLtipoSangre'")
        if (resultSet3.next()) {
            IdTipoSangre = resultSet3.getInt("IdTipoSangre")
        }
        resultSet3.close()

        android.util.Log.d("Depuración", "Todos los id que manda")

        try {
            val addPolicia: PreparedStatement = connection.prepareStatement(
                "EXEC dbo.InsertarReferenciasPoliciasAndroid " +
                        "@Nombre = ?, " +
                        "@Apellido = ?, " +
                        "@FechaNacimiento = ?, " +
                        "@Direccion = ?, " +
                        "@Dui = ?, " +
                        "@IdEstadoCivil = ?, " +
                        "@IdTipoSangre = ?, " +
                        "@IdGenero = ?, " +
                        "@CorreoElectronico = ?, " +
                        "@NumeroTel = ?"

            )

            addPolicia.setString(1, sharedPrefs.getString("Nombre", ""))
            addPolicia.setString(2, sharedPrefs.getString("Apellido", ""))
            addPolicia.setString(3, sharedPrefs.getString("FechaNacimiento", ""))
            addPolicia.setString(4, sharedPrefs.getString("Direccion", ""))
            addPolicia.setString(5, DUI)
            addPolicia.setInt(6, IdEstadoCivil)
            addPolicia.setInt(7, IdTipoSangre)
            addPolicia.setInt(8, IdGenero)
            addPolicia.setString(9, sharedPrefs.getString("Correo", ""))
            addPolicia.setString(10, sharedPrefs.getString("Telefono", ""))

            addPolicia.executeUpdate()

            // Log success or handle it as needed.
            android.util.Log.d("Depuración", "Datos de referencias ingresados correctamente")


        } catch (ex: SQLException) {
            // Log the error for debugging purposes.
            android.util.Log.e("Error", "Error al ingresar datos", ex)

            // Display a user-friendly message.
            Toast.makeText(
                this,
                "Error al ingresar datos. Por favor, inténtelo de nuevo.",
                Toast.LENGTH_SHORT
            ).show()

            // Handle the error gracefully.
            ex.printStackTrace()
            setResult(RESULT_OK, Intent())
        }
    }



}
