package com.example.esespi

import android.os.StrictMode
import android.util.Log
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class conexionSQL {

    // Variables para la cadena de conexión
    private val endpoint = "database-1.cqdnfs7kilaa.us-east-1.rds.amazonaws.com"
    private val port = "1433"
    private val dbName = "ESESPI_Expo1"
    private val username = "admin"
    private val password = "#Dbnlemus2006"

    // Función de conexión
    fun dbConn(): Connection? {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        var conn: Connection? = null
        val connString: String
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance()
            connString = "jdbc:jtds:sqlserver://$endpoint:$port/$dbName;user=$username;password=$password"
            conn = DriverManager.getConnection(connString)
        } catch (ex: SQLException) {
            Log.e("Error: ", ex.message!!)
        } catch (ex1: ClassNotFoundException) {
            Log.e("Error: ", ex1.message!!)
        } catch (ex2: Exception) {
            Log.e("Error: ", ex2.message!!)
        }
        println(conn)
        return conn
    }
    
}