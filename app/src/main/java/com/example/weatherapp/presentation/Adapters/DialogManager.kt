package com.example.weatherapp.presentation.Adapters

import android.app.AlertDialog
import android.content.Context
import android.widget.EditText

object DialogManager {
    fun locationSettingsDialog(context: Context, listener:Listener){
        val builder = AlertDialog.Builder(context)
        val dialog = builder.create()
        dialog.setTitle("Ennable Location")
        dialog.setMessage("Location disabled, do you wont enable GPS?")
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok"){ _,_->
            listener.onClick(null)
            dialog.dismiss()
        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel"){_,_->
            dialog.dismiss()
        }
        dialog.show()
    }
    fun searchByNameDialog(context: Context, listener:Listener){
        val builder = AlertDialog.Builder(context)
        val etName = EditText(context)
        builder.setView(etName)
        val dialog = builder.create()
        dialog.setTitle("City name:")


        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok"){ _,_->
            listener.onClick(etName.text.toString())
            dialog.dismiss()
        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel"){_,_->
            dialog.dismiss()
        }
        dialog.show()
    }

    interface Listener{
        fun onClick(name:String?)
    }
}