package com.example.esespi

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException

private lateinit var txtUsuario: EditText
private lateinit var txtContraseña: EditText
private lateinit var txtConfirmarContraseña: EditText
private lateinit var btnRegistrarse: Button

private lateinit var connection: Connection

private var DUI: String = "no hay dui XD"
private var numPlaca: String = "jaja placa"
var usuarioEncriptado: String = ""
var contraseñaEncriptada: String = ""

class RegistroUsuarioIngresoCredenciales : AppCompatActivity() {

    private lateinit var sharedPrefs: SharedPreferences
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_usuario_ingreso_credenciales)

        connection = conexionSQL().dbConn() ?: throw SQLException("No se pudo establecer la conexión a la base de datos")
        txtUsuario=findViewById(R.id.RegistroUsuarioIngresoCredencialestxtUsuario)
        txtContraseña=findViewById(R.id.RegistroUsuarioIngresoCredencialestxtContraseña)
        txtConfirmarContraseña=findViewById(R.id.RegistroUsuarioIngresoCredencialestxtConfirmacionContraseña)
        btnRegistrarse=findViewById(R.id.RegistroUsuarioIngresoCredencialesBtnRegistrarse)

        sharedPrefs = getSharedPreferences("datos_ingreso", Context.MODE_PRIVATE)


        btnRegistrarse.setOnClickListener {
            val usuario = txtUsuario.text.toString().trim()

            var v = Validaciones()

            if (usuarioExiste(usuario)) {
                // El usuario ya existe, muestra un mensaje de error o realiza alguna acción adecuada.
                Toast.makeText(this, "El nombre de usuario ya está en uso.", Toast.LENGTH_SHORT).show()
            }
            else {
                if (v.CharWritten(txtContraseña, "La Contraseña", 15, 8, this)) {
                    if (v.CharWritten(
                            txtConfirmarContraseña,
                            "La verificación de contraseña",
                            15,
                            8,
                            this
                        )
                    ) {
                        if (txtContraseña.text.toString() == txtConfirmarContraseña.text.toString()) {
                            val RegistroUsuarioValoresDeRegistro =
                                getSharedPreferences("datos_ingreso", Context.MODE_PRIVATE)
                            val valores = RegistroUsuarioValoresDeRegistro.all

                            DUI = sharedPrefs.getString("DUI", "").toString()
                            numPlaca = sharedPrefs.getString("NumeroPlaca", "").toString()
                            contraseñaEncriptada =
                                Encriptacion().convertirSHA256(txtConfirmarContraseña.text.toString())

                            for ((clave, valor) in valores) {
                                println("Clave: $clave - Valor: $valor")
                            }

                            insertarDatosEnBaseDeDatos()
                            InsertarIdiomasPorUsuario()
                            InsertarNacionalidadesPorUsuario()
                            generarConsultasSQLReferenciasPersonales(userDataList)
                            println(numPlaca)
                            val sharedPreferences =
                                getSharedPreferences("datos_ingreso", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.clear() // Elimina todos los datos almacenados en SharedPreferences
                            editor.apply()
                            val customDialog = ShowCustomDialogImage(this)
                            customDialog.showCustomDialog(
                                R.drawable.dialog_check,
                                "Registro exitoso",
                                "Aceptar",
                                MainActivity::class.java,
                                1
                            )
                        } else {
                            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    return@setOnClickListener
                }
            }




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

    fun insertarDatosEnBaseDeDatos() {
        try {

            val statement = connection.createStatement()

            val SQLgenero = sharedPrefs.getString("Genero", "")
            val SQLestadoCivil = sharedPrefs.getString("EstadoCivil", "")
            val SQLtipoSangre = sharedPrefs.getString("TipoSangre", "")

            var IdGenero = -1
            var IdEstadoCivil = -1
            var IdTipoSangre = -1

            val resultSet1 = statement.executeQuery("select IdGenero from tbGeneros where Genero='$SQLgenero'")
            if (resultSet1.next()) {
                IdGenero = resultSet1.getInt("IdGenero")
            }
            resultSet1.close()

            val resultSet2 = statement.executeQuery("select IdEstadoCivil from tbEstadosCivil where EstadoCivil='$SQLestadoCivil'")
            if (resultSet2.next()) {
                IdEstadoCivil = resultSet2.getInt("IdEstadoCivil")
            }
            resultSet2.close()

            val resultSet3 = statement.executeQuery("select IdTipoSangre from tbTiposSangre where TipoSangre='$SQLtipoSangre'")
            if (resultSet3.next()) {
                IdTipoSangre = resultSet3.getInt("IdTipoSangre")
            }
            resultSet3.close()



            // Preparar la sentencia SQL para la inserción de datos
            val sentencia = "insert into tbPersonas (Nombre, Apellido, FechaNacimiento, DireccionDomicilio, Dui, IdEstadoCivil, IdTipoSangre, IdGenero, CorreoElectronico, NumeroTel) " +
                    "VALUES (?,?,?,?,?,?,?,?,?,?);\n"+
                    // Insertar datos en tbTiposPersonas_Personas
                    "INSERT INTO tbTiposPersonas_Personas (IdTipoPersona, IdPersona) SELECT 1, IdPersona FROM dbo.getIdFromDUI(?);\n"+
                    // Insertar datos en tbUsuarios
                    "insert into tbUsuarios values(?, ?, 1);\n"+
                    // Insertar datos en tbPolicias
                    "DECLARE @usuario VARCHAR(200) = ?;\n" +
                    "DECLARE @contraseña VARCHAR(200) = ?;\n" +
                    "DECLARE @dui2 VARCHAR(50) = ?;\n" +
                    "DECLARE @tipoPersona VARCHAR(50) = ?;\n" +

                    "DECLARE @idUsuario INT;\n" +
                    "DECLARE @idTiposPersonas_Personas INT;\n" +

                    "SELECT @idUsuario = IdUsuario FROM dbo.getUsuarioId(@usuario, @contraseña);\n" +
                    "SELECT @idTiposPersonas_Personas = IdTiposPersonas_Personas FROM dbo.getTiposPersonas_PersonasId(@dui2, @tipoPersona);\n" +

                    "INSERT INTO tbPolicias(ONI, NumeroPlaca, IdUsuario, IdRangoTipoUsuario, IdTipoPersonas_Personas)\n" +
                    "VALUES (?, ?, 1, ?, @idTiposPersonas_Personas);"
            val preparedStatement: PreparedStatement = connection.prepareStatement(sentencia)

            // Asignar los valores a los parámetros de la sentencia preparada
            preparedStatement.setString(1, sharedPrefs.getString("Nombre", ""))
            preparedStatement.setString(2, sharedPrefs.getString("Apellido", ""))
            preparedStatement.setString(3, sharedPrefs.getString("FechaNacimiento", ""))
            preparedStatement.setString(4, sharedPrefs.getString("Direccion", ""))
            preparedStatement.setString(5, DUI)
            preparedStatement.setInt(6, IdEstadoCivil)
            preparedStatement.setInt(7, IdTipoSangre)
            preparedStatement.setInt(8, IdGenero)
            preparedStatement.setString(9, sharedPrefs.getString("Correo", ""))
            preparedStatement.setString(10, sharedPrefs.getString("Telefono", ""))
            // tbTiposPersonas_Personas
            preparedStatement.setString(11, DUI)
            // tbUsuarios
            preparedStatement.setString(12, usuarioEncriptado)
            preparedStatement.setString(13, contraseñaEncriptada)
            //tbPolicias
            preparedStatement.setString(14, usuarioEncriptado)
            preparedStatement.setString(15, contraseñaEncriptada)
            preparedStatement.setString(16, DUI)
            preparedStatement.setString(17, "Policia")
            preparedStatement.setString(18, sharedPrefs.getString("ONI", ""))
            preparedStatement.setString(19, numPlaca)
            preparedStatement.setInt(20, 1)
            // Ejecutar la sentencia de inserción
            preparedStatement.executeUpdate()

            println("Datos insertados correctamente en la base de datos.")


            try {
                // Preparar la sentencia SQL para la inserción de datos en tbPersonas
                val sentenciaPersonas = "INSERT INTO tbPersonas (Nombre, Apellido, FechaNacimiento, DireccionDomicilio, Dui, IdEstadoCivil, IdTipoSangre, IdGenero, CorreoElectronico, NumeroTel) " +
                        "VALUES (?,?,?,?,?,?,?,?,?,?);"
                val preparedStatementPersonas: PreparedStatement = connection.prepareStatement(sentenciaPersonas)

                preparedStatementPersonas.setString(1, sharedPrefs.getString("Nombre", ""))
                preparedStatementPersonas.setString(2, sharedPrefs.getString("Apellido", ""))
                preparedStatementPersonas.setString(3, sharedPrefs.getString("FechaNacimiento", ""))
                preparedStatementPersonas.setString(4, sharedPrefs.getString("Direccion", ""))
                preparedStatementPersonas.setString(5, DUI)
                preparedStatementPersonas.setInt(6, IdEstadoCivil)
                preparedStatementPersonas.setInt(7, IdTipoSangre)
                preparedStatementPersonas.setInt(8, IdGenero)
                preparedStatementPersonas.setString(9, sharedPrefs.getString("Correo", ""))
                preparedStatementPersonas.setString(10, sharedPrefs.getString("Telefono", ""))
                preparedStatementPersonas.executeUpdate()
            } catch (ex:SQLException){
                println("Error ocurrido en la insercion en tbPersonas")
            }

            try{
                // Insertar datos en tbTiposPersonas_Personas
                val sentenciaTiposPersonas = "INSERT INTO tbTiposPersonas_Personas (IdTipoPersona, IdPersona) SELECT 1, IdPersona FROM dbo.getIdFromDUI(?);"
                val preparedStatementTiposPersonas: PreparedStatement = connection.prepareStatement(sentenciaTiposPersonas)

                preparedStatementTiposPersonas.setString(1, DUI)
                preparedStatementTiposPersonas.executeUpdate()
            } catch (ex:SQLException){
                println("Error ocurrido en la insercion en tbPersonas")
            }



            try {
                // Insertar datos en tbUsuarios
                val sentenciaUsuarios = "INSERT INTO tbUsuarios VALUES (?, ?, 4);"
                val preparedStatementUsuarios: PreparedStatement = connection.prepareStatement(sentenciaUsuarios)

                preparedStatementUsuarios.setString(1, txtUsuario.text.toString())
                preparedStatementUsuarios.setString(2, contraseñaEncriptada)
                preparedStatementUsuarios.executeUpdate()
            } catch (ex:SQLException){
                println("Error ocurrido en la insercion en tbUsuarios")
            }

            try {
                // Insertar datos en tbPolicias
                val sentenciaPolicias =
                        "DECLARE @usuario VARCHAR(200) = ?;" +
                        "DECLARE @contraseña VARCHAR(200) = ?;" +
                        "DECLARE @dui VARCHAR(50) = ?;" +
                        "DECLARE @tipoPersona VARCHAR(50) = ?;" +

                        "DECLARE @idUsuario INT;" +
                        "DECLARE @idTiposPersonas_Personas INT;" +

                        "SELECT @idUsuario = IdUsuario FROM dbo.getUsuarioId(@usuario, @contraseña);" +
                        "SELECT @idTiposPersonas_Personas = IdTiposPersonas_Personas FROM dbo.getTiposPersonas_PersonasId(@dui, @tipoPersona);" +

                        "INSERT INTO tbPolicias(ONI, NumeroPlaca, IdUsuario, IdRangoTipoUsuario, IdTipoPersonas_Personas)" +
                        "VALUES (?, ?, @idUsuario, ?, @idTiposPersonas_Personas);"

                val preparedStatementPolicias: PreparedStatement = connection.prepareStatement(sentenciaPolicias)

                // Asignar los valores a los parámetros de la sentencia preparada para tbPolicias
                //preparedStatementPolicias.setString(1, usuarioEncriptado)
                preparedStatementPolicias.setString(1, txtUsuario.text.toString())
                preparedStatementPolicias.setString(2, contraseñaEncriptada)
                preparedStatementPolicias.setString(3, DUI)
                preparedStatementPolicias.setString(4, "Policia")
                preparedStatementPolicias.setString(5, sharedPrefs.getString("ONI", ""))
                preparedStatementPolicias.setString(6, numPlaca)
                preparedStatementPolicias.setInt(7, 3)

                println(txtUsuario.text.toString())
                println(contraseñaEncriptada)
                println(DUI)
                println(sharedPrefs.getString("ONI", ""))
                println(numPlaca)

                // Ejecutar la sentencia de inserción para tbPolicias
                preparedStatementPolicias.executeUpdate()
            } catch (ex:SQLException){
                println("Error ocurrido en la insercion en tbPolicias")
            }

            println("Datos insertados correctamente en la base de datos.")
        }
        catch (e: SQLException) {
            e.printStackTrace()
            println(e.toString())
            println("Error ocurrido en insercion de policia")
            val customDialog = ShowCustomDialogImage(this)
            customDialog.showCustomDialog(R.drawable.dialog_cross, "Ocurrió un error", "Aceptar", MainActivity::class.java, 3)
        }
    }

    fun InsertarIdiomasPorUsuario() {

        try {
            val idiomasString = sharedPrefs.getString("IdiomasSeleccionados", "")
            val idiomasSeleccionados: ArrayList<String> = ArrayList(idiomasString?.split(","))
            val sqlQueries = mutableListOf<String>()

            for (idioma in idiomasSeleccionados) {
                    val sqlQuery = "INSERT INTO tbPersonas_Idiomas (IdIdioma, IdPersona) SELECT IdIdioma, IdPersona FROM buscar_y_reemplazar_idioma_persona('$idioma', '$DUI');"
                sqlQueries.add(sqlQuery)
            }

            val statement = connection.createStatement()
            for (query in sqlQueries) {
                statement.executeUpdate(query)
            }
        }
        catch (e: SQLException){
            e.printStackTrace()
            println(e.toString())
            println("Error ocurrido en insercion de idiomas del policia")
            val customDialog = ShowCustomDialogImage(this)
            customDialog.showCustomDialog(R.drawable.dialog_cross, "Ocurrió un error", "Aceptar", MainActivity::class.java, 3)
        }
    }

    fun InsertarNacionalidadesPorUsuario() {

        try {
            val NacionalidadesString = sharedPrefs.getString("NacionalidadesSeleccionados", "")
            val NacionalidadesSeleccionadas: ArrayList<String> = ArrayList(NacionalidadesString?.split(","))
            val sqlQueries = mutableListOf<String>()

            for (nacionalidad in NacionalidadesSeleccionadas) {
                val sqlQuery = "INSERT INTO tbPersonas_Nacionalidades (IdNacionalidad, IdPersona) SELECT IdNacionalidad, IdPersona FROM buscar_y_reemplazar_nacionalidad_persona('$nacionalidad', '$DUI');"
                sqlQueries.add(sqlQuery)
            }

            val statement = connection.createStatement()
            for (query in sqlQueries) {
                statement.executeUpdate(query)
            }
        }
        catch (e: SQLException){
            e.printStackTrace()
            println(e.toString())
            println("Error ocurrido en insercion de nacionalidades del policia")
            val customDialog = ShowCustomDialogImage(this)
            customDialog.showCustomDialog(R.drawable.dialog_cross, "Ocurrió un error", "Aceptar", MainActivity::class.java, 3)
        }
    }

    fun generarConsultasSQLReferenciasPersonales(userDataList: ArrayList<HashMap<String, String>>) {

        try {
            for (userData in userDataList) {
                val statement = connection.createStatement()

                val SQLgenero = userData["genero"] ?: ""
                val SQLestadoCivil = userData["estadoCivil"] ?: ""
                val SQLtipoSangre = userData["tipoSangre"] ?: ""

                var IdGenero = -1
                var IdEstadoCivil = -1
                var IdTipoSangre = -1

                val resultSet1 = statement.executeQuery("select IdGenero from tbGeneros where Genero='$SQLgenero'")
                if (resultSet1.next()) {
                    IdGenero = resultSet1.getInt("IdGenero")
                }
                resultSet1.close()

                val resultSet2 = statement.executeQuery("select IdEstadoCivil from tbEstadosCivil where EstadoCivil='$SQLestadoCivil'")
                if (resultSet2.next()) {
                    IdEstadoCivil = resultSet2.getInt("IdEstadoCivil")
                }
                resultSet2.close()

                val resultSet3 = statement.executeQuery("select IdTipoSangre from tbTiposSangre where TipoSangre='$SQLtipoSangre'")
                if (resultSet3.next()) {
                    IdTipoSangre = resultSet3.getInt("IdTipoSangre")
                }
                resultSet3.close()

                val nombre = userData["nombre"] ?: ""
                val apellido = userData["apellido"] ?: ""
                val dui = userData["dui"] ?: ""
                val telefono = userData["telefono"] ?: ""
                val correo = userData["correo"] ?: ""
                val fechaNacimiento = userData["fechaNacimiento"] ?: ""
                val domicilio = userData["domicilio"] ?: ""


                val sqlQuery = "insert into tbPersonas (Nombre, Apellido, FechaNacimiento, DireccionDomicilio, Dui, IdEstadoCivil, IdTipoSangre, IdGenero, CorreoElectronico, NumeroTel) " +
                        "VALUES ('$nombre', '$apellido', '$fechaNacimiento', '$domicilio', '$dui', $IdEstadoCivil, '$IdTipoSangre', '$IdGenero', '$correo', '$telefono'); " +
                        "DECLARE @dui VARCHAR(100) = $dui; INSERT INTO tbReferenciasPersonales (IdPersona) SELECT IdPersona FROM dbo.getIdFromDUI(@dui); "+
                        "INSERT INTO tbPolicias_Referencias (IdReferenciaPersonal, IdPolicia) SELECT IdReferenciaPersonal, IdPolicia \n" +
                        "FROM dbo.PoliciasReferenciasQuery('$numPlaca', '$dui');"
                statement.executeUpdate(sqlQuery)

                val idiomas = userData["idiomas"] ?: ""
                val idiomasSeleccionados: ArrayList<String> = ArrayList(idiomas?.split(","))
                for (idioma in idiomasSeleccionados) {
                    val sqlQuery = "INSERT INTO tbPersonas_Idiomas (IdIdioma, IdPersona) SELECT IdIdioma, IdPersona FROM buscar_y_reemplazar_idioma_persona('$idioma', '$dui');"
                    val statement = connection.createStatement()
                    statement.executeUpdate(sqlQuery)
                }

                val nacionalidades = userData["nacionalidades"] ?: ""
                val NacionalidadesSeleccionadas: ArrayList<String> = ArrayList(nacionalidades?.split(","))
                for (nacionalidad in NacionalidadesSeleccionadas) {
                    val sqlQuery = "INSERT INTO tbPersonas_Nacionalidades (IdNacionalidad, IdPersona) SELECT IdNacionalidad, IdPersona FROM buscar_y_reemplazar_nacionalidad_persona('$nacionalidad', '$dui');"
                    val statement = connection.createStatement()
                    statement.executeUpdate(sqlQuery)
                }
            }
        } catch (e: SQLException){
            println(e.toString())
            println("Error ocurrido en insercion de referencias personales del policia")
            val customDialog = ShowCustomDialogImage(this)
            customDialog.showCustomDialog(R.drawable.dialog_cross, "Ocurrió un error", "Aceptar", MainActivity::class.java, 3)
        }
    }
}