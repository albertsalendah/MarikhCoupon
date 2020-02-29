package com.marikhapp.marikhcoupon

import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_anda_beruntung.*


@Suppress("DEPRECATION")
class AndaBeruntung : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
        window.setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH)
        setContentView(R.layout.activity_anda_beruntung)
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            val email = user.email
            Log.d("testM","User : $email")
            val db = FirebaseFirestore.getInstance()
            val docRef = db.collection("ListUser")
            val nomorkupon = intent.getStringExtra("NomorKupon")
            docRef.get().addOnSuccessListener { Document ->
                if (Document != null) {
                    for (doc in Document) {
                        val emaildb = doc.getString("Email").toString()
                        if (email == emaildb) {
                            callsplash(email,nomorkupon!!)
                        }
                    }
                }
            }
        }
    }
    private fun callsplash(namaU:String,nomork:String){
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("ListUser/$namaU/DaftarKupon")
        docRef.get().addOnSuccessListener {Document ->
            for(doc in Document){
                val nomor = doc.getString("No Kupon")
                val namaCoupon = doc.getString("Nama Kupon")
                if(nomork == nomor){
                    Log.d("testM","Nama Kupon Yang Didapat : $namaCoupon")
                    kuponygdidpt.text = namaCoupon
                    //kuponygdidpt.isSelected = true
                   // val marquee = AnimationUtils.loadAnimation(this,R.anim.scrollanim)
                   // kuponygdidpt.startAnimation(marquee)

                    val audio = MediaPlayer.create(this, R.raw.yayyy)
                    audio.setAudioStreamType(AudioManager.STREAM_MUSIC)
                    audio.isLooping = false
                    audio.start()

                }
            }
        }
    }

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
