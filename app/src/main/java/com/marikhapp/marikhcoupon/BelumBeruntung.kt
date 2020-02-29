package com.marikhapp.marikhcoupon

import android.annotation.SuppressLint
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

@Suppress("DEPRECATION")
class BelumBeruntung : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
        window.setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH)
        setContentView(R.layout.activity_belum_beruntung)

        val audio = MediaPlayer.create(this, R.raw.awwww)
        audio.setAudioStreamType(AudioManager.STREAM_MUSIC)
        audio.isLooping = false
        audio.start()

        /*progressBar04.visibility = View.VISIBLE
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            val email = user.email
            Log.d("testM","User : $email")
            val db = FirebaseFirestore.getInstance()
            val docRef = db.collection("ListUser")
            docRef.get().addOnSuccessListener { Document ->
                if (Document != null) {
                    for (doc in Document) {
                        val emaildb = doc.getString("Email").toString()
                        if (email == emaildb) {
                            //locatecoupon(email)
                        }
                    }
                }
            }
        }*/
    }
    @SuppressLint("SetTextI18n")
   /* private fun locatecoupon2(namaUser:String){
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("Coupon")

        docRef.get().addOnSuccessListener { Document ->
            if (Document != null) {
                for (doc in Document) {
                    Log.d("testM","So sampeDisini$namaUser")
                    val num = doc.getString("No Kupon")
                    val n100 = "999"
                    if(n100 == num){
                        textView000.visibility = View.INVISIBLE
                        textView6.text = "Tapi Jangan Sedih,Marikh Kasih Diskon 10% deh"
                        textView6.textSize = 18f
                        val namaKupon = doc.getString("Nama Coupon").toString()
                        val tglExp = doc.getString("Tanggal Exp").toString()
                        val picKupon = doc.getString("Picture").toString()
                        val nokupon = doc.getString("No Kupon").toString()
                        Log.d("testM","No = $num -/- Nama Kupon = $namaKupon")
                        //tambahlistkupon2(namaKupon,tglExp,picKupon,namaUser,nokupon)
                    }
                }
            }
        }
    }*/
   /* private fun tambahlistkupon2(namaKupon:String,tgl:String,pic:String,namaUser:String,nokupon:String){
        val db = FirebaseFirestore.getInstance()
        val add = HashMap<String, Any>()
        val sts = "UnUsed"
        add["Nama Kupon"] = namaKupon
        add["Tanggal Exp"] = tgl
        add["Picture"] = pic
        add["No Kupon"] = nokupon
        add["Status"] = sts
        Log.d("testM","NamaUser : $namaUser")
        db.collection("ListUser/$namaUser/DaftarKupon/").document(namaKupon).set(add)
            .addOnSuccessListener {
                progressBar04.visibility = View.INVISIBLE
                Log.d("testM","Kupon DiTambah")
                val intent = Intent(this,CouponList::class.java)
                startActivity(intent)
            }
    }*/

    override fun onBackPressed() {
        val intent = Intent(this,CouponList::class.java)
        startActivity(intent)
        this.finish()
        this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (MotionEvent.ACTION_OUTSIDE == event.action) {
            Log.d("testM","Touch")
            val intent = Intent(this,CouponList::class.java)
            startActivity(intent)
            this.finish()
            this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            return true
        }
        return super.onTouchEvent(event)
    }
}
