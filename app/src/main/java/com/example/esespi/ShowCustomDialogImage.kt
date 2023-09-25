package com.example.esespi

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
class ShowCustomDialogImage(private val context: Context) {

    fun showCustomDialog(imageId: Int, content: String, button: String, activityToOpen: Class<out Activity>, Resultado: Int) {
        val activity = context as Activity

        // Verificar si la actividad aún está en curso
        if (activity.isFinishing) {
            return
        }

        try {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_layout_img, null)

            val imageView = dialogView.findViewById<ImageView>(R.id.DialogLayoutImg_img)
            val contentTextView = dialogView.findViewById<TextView>(R.id.DialogLayoutImg_content)
            val buttonTextView = dialogView.findViewById<TextView>(R.id.DialogLayoutImg_Btn)

            imageView.setImageResource(imageId)
            contentTextView.text = content
            buttonTextView.text = button

            buttonTextView.setBackgroundColor(Color.parseColor("#52A1FF"))

            val alertDialogBuilder = AlertDialog.Builder(context)
                .setView(dialogView)

            val alertDialog = alertDialogBuilder.create()

            // Clic en el botón
            when (Resultado) {
                1 -> {
                    buttonTextView.setOnClickListener {
                        alertDialog.dismiss() // Cierra el diálogo antes de iniciar la actividad
                        val intent = Intent(context, activityToOpen)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        context.startActivity(intent)
                    }
                }
                2 -> {
                    buttonTextView.setOnClickListener {
                        alertDialog.dismiss() // Cierra el diálogo antes de iniciar la actividad
                        val intent = Intent(context, activityToOpen)
                        context.startActivity(intent)
                    }
                }
                3 -> {
                    buttonTextView.setOnClickListener {
                        alertDialog.dismiss() // Cierra el diálogo sin iniciar una actividad
                    }
                }
            }

            // Mostrar el diálogo en el subproceso principal de la interfaz de usuario
            activity.runOnUiThread {
                alertDialog.show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Manejar la excepción de manera adecuada si es necesario
        }
    }
}
