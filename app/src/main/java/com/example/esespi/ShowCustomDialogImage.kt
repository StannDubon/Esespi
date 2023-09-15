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
        alertDialog.show()

        // clic en el botón
        if(Resultado==1){
            buttonTextView.setOnClickListener {
                val intent = Intent(context, activityToOpen)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
                alertDialog.dismiss()
            }
        }
        if(Resultado==2){
            buttonTextView.setOnClickListener {
                val intent = Intent(context, activityToOpen)
                context.startActivity(intent)
                alertDialog.dismiss()
            }
        }
        if(Resultado==3){
            buttonTextView.setOnClickListener {
                alertDialog.dismiss()
            }
        }
    }
}