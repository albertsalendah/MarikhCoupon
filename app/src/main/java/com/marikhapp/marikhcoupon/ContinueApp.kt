package com.marikhapp.marikhcoupon

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


class ContinueApp : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            val intent = Intent(this, HomePage::class.java)
            startActivity(intent)

        }else{
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
            this.finish()
        }
    }
}
