package com.example.esespi

import android.os.AsyncTask
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class SendMailTask(private val destinatario: String,
                   private val asunto: String,
                   private val mensaje: String) :
    AsyncTask<Void?, Void?, Void?>() {

    override fun doInBackground(vararg params: Void?): Void? {

        // Configuración del servidor SMTP
        val props = Properties()
        props["mail.smtp.host"] = "smtp.gmail.com"
        props["mail.smtp.socketFactory.port"] = "465"
        props["mail.smtp.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"
        props["mail.smtp.auth"] = "true"
        props["mail.smtp.port"] = "465"

        //Iniciamos Sesion
        val session = Session.getDefaultInstance(props,
            object : javax.mail.Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {

                    //Colocamos el correo y contraseña desde donde enviaremos el mensaje
                    //La contraseña debe ser generada en "Contraseñas de aplicaciones"
                    return PasswordAuthentication("esespi.org@gmail.com", "udnhlrbsfhxmlofk")
                }
            })

        //Hacemos el envio
        try {
            val message = MimeMessage(session)

            //Cambiamos el valor por el correo desde donde enviaremos el mensaje
            message.setFrom(InternetAddress("esespi.org@gmail.com"))
            message.addRecipient(Message.RecipientType.TO, InternetAddress(destinatario))
            message.subject = asunto
            message.setText(this.mensaje)
            Transport.send(message)

            println("Correo enviado satisfactoriamente")
        } catch (e: MessagingException) {
            e.printStackTrace()

            println("Correo no enviado")
        }
        return null
    }
}
