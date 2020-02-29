package com.marikhapp.marikhcoupon

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.vision.barcode.Barcode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_confirm_coupon.*

@Suppress("NAME_SHADOWING")
class ConfirmCoupon : AppCompatActivity() {
    private val requestcode = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
        window.setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH)
        setContentView(R.layout.activity_confirm_coupon)

        cancel.setOnClickListener {
            this.finish()
            overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out)
        }
        buttonYakin.setOnClickListener {
            layoutCon.visibility = LinearLayout.GONE
            val intent = Intent(this@ConfirmCoupon, ActivityScanner::class.java)
            startActivityForResult(intent, requestcode)
            overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val user = FirebaseAuth.getInstance().currentUser
        val namaKupon = intent.getStringExtra("namaKupon")
        val noKupon = intent.getStringExtra("noKupon")
        val arrayNamaKupon : ArrayList<String> = ArrayList()
        val arrayNoKupon : ArrayList<String> = ArrayList()
        user.let {
            val email = user!!.email
            val db = FirebaseFirestore.getInstance()
            if (requestCode == requestcode && resultCode == Activity.RESULT_OK) {
                val barcode = data!!.getParcelableExtra<Barcode>("barcode")
                val scan = barcode!!.displayValue.toString()
                Log.d("testM", scan)
                db.collection("Random").get().addOnSuccessListener { Document->
                    if(Document != null){
                        for(doc in Document){
                            val randomString = doc.getString("RandomString")
                            if(scan == randomString){
                                db.collection("ListUser/$email/DaftarKupon").get().addOnSuccessListener { Document->
                                    if(Document != null){
                                        for(doc in Document){
                                            val namaC = doc.getString("Nama Kupon")
                                            val noC = doc.getString("No Kupon")
                                            arrayNamaKupon.add(namaC!!)
                                            arrayNoKupon.add(noC!!)
                                            Log.d("testM","Daftar Kupon : $namaC")
                                            if(namaKupon == namaC && noKupon == noC){
                                                Log.d("testM","Kupon Yang DiPilih : $namaC--/NomorKuponNya:$noC/--"+doc.id)
                                                db.collection("ListUser/$email/DaftarKupon").document(doc.id).update("Status","Used")
                                                    .addOnSuccessListener {
                                                        //popUP()
                                                       /* val intent = Intent(this,CouponList::class.java)
                                                        startActivity(intent)
                                                        overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out)*/
                                                        Handler().postDelayed({
                                                            val intent = Intent(this,PopUp03::class.java)
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                                            startActivity(intent)
                                                            this.finish()
                                                        },500)
                                                    }
                                            }
                                        }
                                    }
                                }
                            }else{
                                //popUPP()
                                val intent = Intent(this,PopUp02::class.java)
                                startActivity(intent)
                              /*  Handler().postDelayed({
                                    this.finish()
                                },5000)*/
                            }
                        }
                    }
                }
            }
        }
    }
    private fun popUPP(){
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(true)
        builder.setTitle("")
        builder.setMessage("Eiiitz, jangan coba-coba gitu.. Scan QRcode-nya di Marikh ya :D")
        val dialog = builder.create()
        dialog.show()
        val textView = dialog.findViewById(android.R.id.message) as TextView
        textView.textSize = 20f
    }
    private fun popUP(){
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(true)
        builder.setTitle("")
        builder.setMessage("Kuponmu berhasil digunakan. Besok datang lagi ke Marikh ya..")
        val dialog = builder.create()
        dialog.show()
        val textView = dialog.findViewById(android.R.id.message) as TextView
        textView.textSize = 20f
    }


    override fun onBackPressed() {
        this.finish()
        this.overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out)
    }
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (MotionEvent.ACTION_OUTSIDE == event.action) {
            Log.d("testM","Touch")
            this.finish()
            this.overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out)
            return true
        }
        return super.onTouchEvent(event)
    }
}
