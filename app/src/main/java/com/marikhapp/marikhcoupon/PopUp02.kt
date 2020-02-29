package com.marikhapp.marikhcoupon

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

class PopUp02 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
        window.setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH)
        setContentView(R.layout.activity_pop_up02)
        //------------------------------------------
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
