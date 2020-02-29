package com.marikhapp.marikhcoupon

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.io.IOException
import java.util.HashMap
import kotlin.random.Random


@Suppress("NAME_SHADOWING", "ControlFlowWithEmptyBody", "SameParameterValue")
class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        /* TODO(developer): Handle FCM messages here. */
        Log.d("testM", "From: " + remoteMessage.from!!)

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d("testM", "Message data payload: " + remoteMessage.data)

            val judul = remoteMessage.data["title"].toString()
            val pesan = remoteMessage.data["body"].toString()
            val channel = remoteMessage.data["channel_id"].toString()

            if(channel == "MarikhNotifKupon"){
                Log.d("testM","Jududl : $channel")
                createNotificationChannels(channel)
                    sendOnChannel3(judul,pesan,channel)
                    notifkupon()
            }else{
                Log.d("testM","Jududl : $channel")
                createNotificationChannels(channel)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    sendOnChannel1(judul,pesan,channel)
                }else{
                    sendOnChannel2(judul,pesan,channel)
                }
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {

        }
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.d("testM","Token Baru = $p0")
        updateToken(p0)
    }

    private fun updateToken(token:String){
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            val email = user.email
            Log.d("testM","User : $email")
            val db = FirebaseFirestore.getInstance()
            db.collection("ListUser").get().addOnSuccessListener {Document->
                if(Document != null){
                    for (doc in Document){
                        val uEmail = doc.getString("Email")
                        if(uEmail == email){
                            Log.d("testM","Email User : $uEmail")
                            val docRef = db.collection("ListUser/$uEmail/TokenNotifikasi")
                            docRef.get().addOnSuccessListener { Document ->
                                if (Document != null) {
                                    db.collection("ListUser/$email/TokenNotifikasi").document("Token")
                                        .update(mapOf("TokenNotifikasi" to token)).addOnSuccessListener {
                                            Log.d("testM","Token Baru Berhasil DiUpdate Dari Service")
                                        }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun sendOnChannel1(judul: String, pesan: String, channel: String) {
        val pendingIntent: PendingIntent = Intent(this, HomePage::class.java).let { notificationIntent ->
            PendingIntent.getActivity(this, 0, notificationIntent, 0)
        }
        var picture: Bitmap? = null
        try {
            picture = BitmapFactory.decodeResource(this.resources,R.drawable.logomarikh)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val notificationManager = NotificationManagerCompat.from(this)
        val notification = NotificationCompat.Builder(this, channel)
            .setSmallIcon(R.drawable.ic_marikhlogo)
            .setContentTitle(judul)
            .setContentText(pesan)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setLargeIcon(picture)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(1, notification)
    }

    private fun sendOnChannel2(judul: String, pesan: String, channel: String) {
        val pendingIntent: PendingIntent = Intent(this, HomePage::class.java).let { notificationIntent ->
            PendingIntent.getActivity(this, 0, notificationIntent, 0)
        }
        var picture: Bitmap? = null
        try {
            picture = BitmapFactory.decodeResource(this.resources,R.drawable.logomarikh)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationManager = NotificationManagerCompat.from(this)
        val notification = NotificationCompat.Builder(this, channel)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(judul)
            .setContentText(pesan)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(defaultSoundUri)
            .setLargeIcon(picture)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(2, notification)
    }

    private fun sendOnChannel3(judul: String, pesan: String, channel: String) {
        val pendingIntent: PendingIntent = Intent(this, CouponList::class.java).let { notificationIntent ->
            PendingIntent.getActivity(this, 0, notificationIntent, 0)
        }
        var picture: Bitmap? = null
        try {
            picture = BitmapFactory.decodeResource(this.resources,R.drawable.logomarikh)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val notificationManager = NotificationManagerCompat.from(this)
        val notification = NotificationCompat.Builder(this, channel)
            .setSmallIcon(R.drawable.ic_marikhlogo)
            .setContentTitle(judul)
            .setContentText(pesan)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setLargeIcon(picture)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(1, notification)
    }

    private fun createNotificationChannels(channel: String) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()
            val channel1 = NotificationChannel(
                channel,
                channel,
                NotificationManager.IMPORTANCE_HIGH
            )
            channel1.enableLights(true)
            channel1.lightColor = Color.RED
            channel1.enableVibration(true)
            channel1.vibrationPattern = longArrayOf(100, 200, 300, 400, 500)
            channel1.setSound(defaultSoundUri,audioAttributes)
            channel1.description = "MarikhCoupon"

            val channel2 = NotificationChannel(
                channel,
                channel,
                NotificationManager.IMPORTANCE_HIGH
            )
            channel2.enableLights(true)
            channel2.lightColor = Color.RED
            channel2.enableVibration(true)
            channel2.vibrationPattern = longArrayOf(100, 200, 300, 400, 500)
            channel2.description = "MarikhCoupon"

            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(channel1)
            manager.createNotificationChannel(channel2)
        }
    }

    private fun notifkupon(){
        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            val email = user.email
            db.collection("Notifikasi/pesan/Kupon").get().addOnSuccessListener {Document->
                if(Document != null) {
                    for (doc in Document) {
                        val namaKupon = doc.getString("Nama Kupon").toString()
                        val picture = doc.getString("Picture")
                        val tglExp = doc.getString("Tanggal Exp").toString()
                        val nokupon = doc.getString("No Kupon").toString()
                        tambahlistkupon(namaKupon,tglExp,picture!!,email!!,nokupon)
                    }
                }
            }
        }
    }

    private fun tambahlistkupon(namaKupon:String,tgl:String,pic:String,namaUser:String,nokupon:String){
        val db = FirebaseFirestore.getInstance()
        val add = HashMap<String, Any>()
        val sts = "UnUsed"
        val rsg = nokupon + randomStringGenerator(10)
        add["Nama Kupon"] = namaKupon
        add["Tanggal Exp"] = tgl
        add["Picture"] = pic
        add["No Kupon"] = rsg
        add["Status"] = sts
        Log.d("testM","NamaUser : $namaUser")
        db.collection("ListUser/$namaUser/DaftarKupon/").document().set(add)
            .addOnSuccessListener {
                Log.d("testM","Kupon DiTambah")
            }
    }

    private fun randomStringGenerator(length:Int):String{
        val sb = StringBuilder(length)
        val alpha = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!@#$%^&*()_+~`[]{}|?/;:<>,."
        val rand = Random
        for (i in 0 until sb.capacity()){
            val index = rand.nextInt(alpha.length)
            sb.append(alpha[index])
        }
        return  sb.toString()
    }
}