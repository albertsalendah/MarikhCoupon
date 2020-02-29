package com.marikhapp.marikhcoupon

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_game.*


class Game : AppCompatActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        val game = intent.getStringExtra("namagame")
        val view = findViewById<WebView>(R.id.dinogame)
        view.settings.javaScriptEnabled = true
        val res = getScreenResolution(this)
        if(game == "pinball"){
            if (android.os.Build.VERSION.SDK_INT <= 19) {
                this.finish()
            } else {
                view.loadUrl("file:///android_asset/pinball/pinball.html",null)
                view.settings.javaScriptEnabled = true
            }
        }else if(game == "dino"){
            if (android.os.Build.VERSION.SDK_INT <= 19) {
                this.finish()
            } else {
                if(res.toInt() <= 720){
                    gamelayout.layoutParams.height = 900
                    gamelayout.layoutParams.width  = 650
                    Log.d("testM","Resolution = $res <= 720")
                }else {
                    gamelayout.layoutParams.height = 1180
                    gamelayout.layoutParams.width  = 800
                    Log.d("testM","Resolution = $res > 720")
                }
                gamelayout.requestLayout()
                view.loadUrl("file:///android_asset/dino/index.html",null)
                view.settings.javaScriptEnabled = true
            }
        } else if(game == "tetris"){
            if (android.os.Build.VERSION.SDK_INT <= 19) {
                this.finish()
            } else {
                if(res.toInt() <= 720){
                    gamelayout.layoutParams.height = 1050
                    gamelayout.layoutParams.width  = 650
                    Log.d("testM","Resolution = $res <= 720")
                }else {
                    gamelayout.layoutParams.height = 1400
                    gamelayout.layoutParams.width  = 900
                    Log.d("testM","Resolution = $res > 720")
                }
                gamelayout.requestLayout()
                view.loadUrl("file:///android_asset/tetris/index.html",null)
                view.settings.javaScriptEnabled = true
            }
        }
        Log.d("testM",  getScreenResolution(this))
        exit.setOnClickListener {
            this.finish()
            this.overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out)
        }

    }
    private fun getScreenResolution(context: Context): String {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)
        val width = metrics.widthPixels
        //val height = metrics.heightPixels

        return "$width"
    }
    override fun onBackPressed() {
        this.finish()
        this.overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out)
    }
}
