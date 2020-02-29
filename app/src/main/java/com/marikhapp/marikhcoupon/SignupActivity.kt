package com.marikhapp.marikhcoupon

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog.THEME_HOLO_LIGHT
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.signup_activity.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class SignupActivity : AppCompatActivity(),DatePickerDialog.OnDateSetListener {
    private val permissionRequest = 200
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_activity)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), permissionRequest)
        }
        val signupbutton = findViewById<Button>(R.id.signupbutton)
        tgl_lahir.setOnClickListener { showDatePickerDialog() }
        signupbutton.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            mendaftar()
        }
            loginbutton.setOnClickListener {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
        @Suppress("DEPRECATION")
        private fun showDatePickerDialog() {
            val datePickerDialog = DatePickerDialog(
                this,
                THEME_HOLO_LIGHT,
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
                tgl_lahir.text  =  formatter.format(LocalDate.of(year,month+1,dayOfMonth))
            }else{
                val date = GregorianCalendar(year, month, dayOfMonth)
                val formatter = SimpleDateFormat("MM/dd/yyyy")
                tgl_lahir.text  = formatter.format(date.time)
            }
        }
        private fun mendaftar() {
            val nama = nama.text.toString()
            val nomorhp = nomorhandphone.text.toString()
            val email = email.text.toString()
            val tgl = tgl_lahir.text.toString()
            val add = HashMap<String, Any>()
            val db = FirebaseFirestore.getInstance()
            add["Nama"] = nama
            add["Nomor Handphone"] = nomorhp
            add["Email"] = email
            add["Tanggal Lahir"] = tgl
            if (nama.isEmpty() || nomorhp.isEmpty() || email.isEmpty() || tgl.isEmpty()) {
                progressBar.visibility = View.INVISIBLE
                val toast = Toast.makeText(this, "Isi Semua Kolom", Toast.LENGTH_LONG)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            } else {
                db.collection("ListUser").document(email).set(add)
                    .addOnSuccessListener {
                        createUser(email,tgl)
                    }
                    .addOnFailureListener {
                        progressBar.visibility = View.INVISIBLE
                        val toast = Toast.makeText(this, "Anda Gagal Mendaftar", Toast.LENGTH_LONG)
                        toast.setGravity(Gravity.CENTER, 0, 0)
                        toast.show()
                    }
            }
        }
    private fun createUser(email: String, pass: String){

        if(email.isEmpty() || pass.isEmpty()){
            progressBar.visibility = View.INVISIBLE
            val toast = Toast.makeText(this, "Isi Semua Kolom", Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
            return
        }else {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pass)
                .addOnSuccessListener {
                    val toast = Toast.makeText(this, "Anda Berhasil Mendaftar", Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                   validasilogin(email,pass)
                }
                .addOnFailureListener {
                    progressBar.visibility = View.INVISIBLE
                    val toast = Toast.makeText(this, "Email Sudah Terdaftar", Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                }
        }
    }
    private fun validasilogin(email: String, pass: String){

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,pass)
            .addOnSuccessListener {
                progressBar.visibility = View.INVISIBLE
                val intent = Intent(this,HomePage::class.java)
                startActivity(intent)
                this.finish()
            }
            .addOnFailureListener {
                progressBar.visibility = View.INVISIBLE
                val toast = Toast.makeText(this, "Email/Password Salah", Toast.LENGTH_LONG)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }
    }
    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}