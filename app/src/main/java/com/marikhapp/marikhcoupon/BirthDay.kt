package com.marikhapp.marikhcoupon

import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_birth_day.*


@Suppress("NAME_SHADOWING", "DEPRECATION")
class BirthDay : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
        window.setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH)
        setContentView(R.layout.activity_birth_day)

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
                        val nama = doc.getString("Nama").toString()
                        if (email == emaildb) {
                            val nama = nama.split(" ")[0]
                            namaUS.text = nama
                            val audio = MediaPlayer.create(this, R.raw.partypopper)
                            audio.setAudioStreamType(AudioManager.STREAM_MUSIC)
                            audio.isLooping = false
                            audio.start()
                        }
                    }
                }
            }
        }

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
