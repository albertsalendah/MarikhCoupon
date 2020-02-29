package com.marikhapp.marikhcoupon

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import android.view.Gravity



class LoginActivity : AppCompatActivity(),DatePickerDialog.OnDateSetListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val login = findViewById<Button>(R.id.loginbutton)
        tgl_L.setOnClickListener {
            showDatePickerDialog()
        }
        login.setOnClickListener {
            if(emailaddres.text.isEmpty() || tgl_L.text.isEmpty()){
               val toast = Toast.makeText(this, "Isi Semua Kolom", Toast.LENGTH_LONG)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }else{validasilogin()}
        }
    }
    @Suppress("DEPRECATION")
    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(
            this,
            AlertDialog.THEME_HOLO_LIGHT,
            this,
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    @SuppressLint("SimpleDateFormat")
    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
            tgl_L.text  =  formatter.format(LocalDate.of(year,month+1,dayOfMonth))
        }else{
            val date = GregorianCalendar(year, month, dayOfMonth)
            val formatter = SimpleDateFormat("MM/dd/yyyy")
            tgl_L.text  = formatter.format(date.time)
        }
    }
    private fun validasilogin(){
        val email = emailaddres.text.toString()
        val tgl  = tgl_L.text.toString()
        progressBar1.visibility = View.VISIBLE
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,tgl)
            .addOnSuccessListener {
                progressBar1.visibility = View.INVISIBLE
                val intent = Intent(this,HomePage::class.java)
                startActivity(intent)
            }
            .addOnFailureListener {
                progressBar1.visibility = View.INVISIBLE
                val toast = Toast.makeText(this, "Email/Tanggal Lahir Salah", Toast.LENGTH_LONG)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }
    }
}
