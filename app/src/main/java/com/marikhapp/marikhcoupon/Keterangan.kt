package com.marikhapp.marikhcoupon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_keterangan.*

class Keterangan : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_keterangan)

        buttonexit.setOnClickListener {
            this.finish()
            this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
    }
    override fun onBackPressed() {
        this.finish()
        this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
}
