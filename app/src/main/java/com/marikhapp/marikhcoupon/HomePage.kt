package com.marikhapp.marikhcoupon

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.vision.barcode.Barcode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_home_page.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

@Suppress("NAME_SHADOWING", "SameParameterValue")
class HomePage : AppCompatActivity() {
    private val requestcode = 100
    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)
        buttondino.setOnClickListener {
            val intent = Intent(this,Game::class.java)
            intent.putExtra("namagame","dino")
            startActivity(intent)
        }
        logg.setOnLongClickListener {
            val intent = Intent(this,Game::class.java)
            intent.putExtra("namagame","pinball")
            startActivity(intent)
            return@setOnLongClickListener true
        }
        cardT.setOnLongClickListener {
            val intent = Intent(this,Game::class.java)
            intent.putExtra("namagame","tetris")
            startActivity(intent)
            return@setOnLongClickListener true
        }
        //-------------------Untuk Notifikasi------------------------
        FirebaseMessaging.getInstance().subscribeToTopic("marikh")
            .addOnCompleteListener { task ->
                var msg = getString(R.string.whaaa_selamaaat)
                if (!task.isSuccessful) {
                    msg = getString(R.string.besok_datang_lagi_ya)
                }
                Log.d("testM",msg)
            }
        //----------------Get Token Untuk Notifikasi
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("testM", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }
                // Get new Instance ID token
                val token = task.result?.token
                val user = FirebaseAuth.getInstance().currentUser
                user?.let {
                    val email = user.email
                    val db = FirebaseFirestore.getInstance()
                    val docRef = db.collection("ListUser/$email/TokenNotifikasi")
                    docRef.get().addOnSuccessListener { Document ->
                        if (Document.isEmpty) {
                            val add = HashMap<String, Any>()
                            add["TokenNotifikasi"] = token.toString()
                            docRef.document("Token").set(add).addOnSuccessListener {
                               Log.d("testM","Token Ditambahkan")
                            }
                        }else{
                            db.collection("ListUser/$email/TokenNotifikasi").get().addOnSuccessListener {Document->
                                if (Document != null){
                                    for (doc in Document){
                                        val tokenlama = doc.getString("TokenNotifikasi")
                                        if(tokenlama != token){
                                            db.collection("ListUser/$email/TokenNotifikasi").document("Token")
                                                .update(mapOf("TokenNotifikasi" to token)).addOnSuccessListener {
                                                    Log.d("testM","Token Baru Berhasil DiUpdate")
                                                }
                                        }else{
                                            Log.d("testM","Token Masih Sama Belum diUpdate")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                // Log and toast
                Log.d("testM", "Token Skarang = $token")
            })

        //------------------------------------------------------------
        marikhnotif()
        //notifkupon()
        //------------------------------------------------------------
        val s= "Ketuk, Yuk!"
        val ss = "dan temukan keberuntunganmu"
        val ss1=   SpannableString(ss)
        ss1.setSpan( RelativeSizeSpan(0.3f), 0,27, 0)
        val finalText = TextUtils.concat(s, "\n", ss1)
        buttonScann.text = finalText

        birthdaycard.visibility = View.GONE
        cardbirth.visibility = View.GONE
        buttonScann.visibility = View.INVISIBLE
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
                        val tanggalLahir = doc.getString("Tanggal Lahir").toString()
                        if (email == emaildb) {
                            val namaU = doc.getString("Nama").toString()
                            texthi.text = "Hi, $namaU"
                            Log.d("testM","Nama : $namaU")
                            val sdf = SimpleDateFormat("MM/dd/")
                            val date = Date()
                            val date1 = sdf.parse(tanggalLahir)
                            val tanggalDataBase= sdf.format(date1!!)
                            val tanggalHariIni = sdf.format(date)
                            if(tanggalHariIni == tanggalDataBase){
                                namaUSs.text = namaU.split(" ")[0]
                                birthdaycard.visibility = View.VISIBLE
                                cardbirth.visibility = View.VISIBLE
                                overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out)
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    val current = LocalDateTime.now()
                                    val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
                                    val tanggal =  current.format(formatter)
                                    db.collection("ListUser/$email/HistoryUlangTahun").get().addOnSuccessListener { Document->
                                        if(Document != null){
                                            for(doc in Document){
                                                val tglscan = doc.getString("Tanggal Scann").toString()
                                                if(tanggal == tglscan){
                                                    imgs.setImageResource(R.drawable.gift)
                                                }
                                            }
                                        }
                                    }
                                }else{
                                    db.collection("ListUser/$email/HistoryUlangTahun").get().addOnSuccessListener { Document->
                                        val date = Date()
                                        val formatter = SimpleDateFormat("MM/dd/yyyy")
                                        val tanggal = formatter.format(date)
                                        if(Document != null){
                                            for(doc in Document){
                                                val tglscan = doc.getString("Tanggal Scann")
                                                val tgl = formatter.parse(tglscan!!)
                                                val newtgl = formatter.format(tgl!!)
                                                Log.d("testM","--/$tanggal--//Sama--//$newtgl")
                                                if(tanggal == newtgl){
                                                    imgs.setImageResource(R.drawable.gift)
                                                }
                                            }
                                        }
                                    }
                                }
                                    birthdaycard.setOnClickListener {
                                        progressBar2.visibility = View.VISIBLE
                                        birthdaycard.visibility = View.INVISIBLE
                                        cardbirth.visibility = View.INVISIBLE
                                        val user = FirebaseAuth.getInstance().currentUser
                                        user?.let {
                                            val email = user.email
                                            val db = FirebaseFirestore.getInstance()
                                            val docRef = db.collection("ListUser/$email/HistoryUlangTahun")
                                            docRef.get().addOnSuccessListener { Document ->
                                                if (Document.isEmpty) {
                                                    Log.d("testM","Is Empty")

                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                        val current =  LocalDateTime.now()
                                                        val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
                                                        val tanggal =  current.format(formatter)
                                                        val add = HashMap<String, Any>()
                                                        add["Tanggal Scann"] = tanggal
                                                        Log.d("testM","I am Here $tanggal")
                                                        docRef.document("UlangTahun").set(add).addOnSuccessListener {
                                                            locatecouponUlangTahun(emaildb)
                                                        }
                                                    }else{
                                                        val date = Date()
                                                        val sdf = SimpleDateFormat("MM/dd/yyyy")
                                                        val tanggal = sdf.format(date)
                                                        val add = HashMap<String, Any>()
                                                        add["Tanggal Scann"] = tanggal
                                                        db.collection("ListUser/$email/HistoryUlangTahun").document("UlangTahun").set(add).addOnSuccessListener {
                                                            locatecouponUlangTahun(emaildb)
                                                        }
                                                    }
                                                }else{
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                        val current = LocalDateTime.now()
                                                        val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
                                                        val tanggal =  current.format(formatter)
                                                        db.collection("ListUser/$email/HistoryUlangTahun").get().addOnSuccessListener { Document->
                                                            if(Document != null){
                                                                for(doc in Document){
                                                                    val tglscan = doc.getString("Tanggal Scann").toString()
                                                                    if(tanggal == tglscan){
                                                                        progressBar2.visibility = View.INVISIBLE
                                                                        birthdaycard.visibility = View.VISIBLE
                                                                        cardbirth.visibility = View.VISIBLE
                                                                        val toast = Toast.makeText(this, "Kado sudah dibuka, cek di daftar kupon ya", Toast.LENGTH_LONG)
                                                                        toast.setGravity(Gravity.CENTER, 0, 0)
                                                                        toast.show()
                                                                    }else{
                                                                        db.collection("ListUser/$email/HistoryUlangTahun").document("UlangTahun")
                                                                            .update(mapOf("Tanggal Scann" to tanggal))
                                                                        locatecouponUlangTahun(emaildb)
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }else{
                                                        db.collection("ListUser/$email/HistoryUlangTahun").get().addOnSuccessListener { Document->
                                                            val date = Date()
                                                            val formatter = SimpleDateFormat("MM/dd/yyyy")
                                                            val tanggal = formatter.format(date)
                                                            if(Document != null){
                                                                for(doc in Document){
                                                                    val tglscan = doc.getString("Tanggal Scann")
                                                                    val tgl = formatter.parse(tglscan!!)
                                                                    val newtgl = formatter.format(tgl!!)
                                                                    Log.d("testM","--/$tanggal--//Sama--//$newtgl")
                                                                    if(tanggal == newtgl){
                                                                        progressBar2.visibility = View.INVISIBLE
                                                                        birthdaycard.visibility = View.VISIBLE
                                                                        cardbirth.visibility = View.VISIBLE
                                                                        val toast = Toast.makeText(this, "Kado sudah dibuka, cek di daftar kupon ya", Toast.LENGTH_LONG)
                                                                        toast.setGravity(Gravity.CENTER, 0, 0)
                                                                        toast.show()
                                                                    }else{
                                                                        db.collection("ListUser/$email/HistoryUlangTahun").document("UlangTahun")
                                                                            .update(mapOf("Tanggal Scann" to tanggal))
                                                                        locatecouponUlangTahun(emaildb)
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                val isFirstRun = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).getBoolean("isFirstRun", true)
                                if (isFirstRun) {
                                    //show start activity
                                    startActivity(Intent(this@HomePage, BirthDay::class.java))
                                    getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).edit().putBoolean("isFirstRun", false).apply()
                                }
                            }else{
                                getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).edit().putBoolean("isFirstRun", true).apply()
                            }
                            buttonScann.visibility = View.VISIBLE
                            progressBar2.visibility = View.INVISIBLE
                        }
                    }
                }
            }
        }

       // daftarnotif()

        buttonScann.setOnClickListener {
            progressBar2.visibility = View.VISIBLE
            buttonScann.visibility = View.INVISIBLE
            val user = FirebaseAuth.getInstance().currentUser
            user?.let {
                val email = user.email
                val db = FirebaseFirestore.getInstance()
                val docRef = db.collection("ListUser/$email/HistoryScann")
                docRef.get().addOnSuccessListener { Document ->
                    if (Document.isEmpty) {
                        Log.d("testM","Is Empty")
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                val current = "01/01/1111"
                                val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
                                val tanggal =  current.format(formatter)
                                val add = HashMap<String, Any>()
                                add["Tanggal Scann"] = tanggal
                                Log.d("testM","I am Here $tanggal")
                                docRef.document("Scann").set(add).addOnSuccessListener {
                                    buttonScann.visibility = View.VISIBLE
                                    progressBar2.visibility = View.INVISIBLE
                                    val intent = Intent(this@HomePage, ActivityScanner::class.java)
                                    startActivityForResult(intent, requestcode)
                                }
                            }else{
                                val date = "01/01/1111"
                                val sdf = SimpleDateFormat("MM/dd/yyyy")
                                val tanggal = sdf.parse(date)
                                val newtgl = sdf.format(tanggal!!)
                                val add = HashMap<String, Any>()
                                add["Tanggal Scann"] = newtgl
                                Log.d("testM","I am Here $newtgl")
                                db.collection("ListUser/$email/HistoryScann").document("Scann").set(add).addOnSuccessListener {
                                    buttonScann.visibility = View.VISIBLE
                                    progressBar2.visibility = View.INVISIBLE
                                    val intent = Intent(this@HomePage, ActivityScanner::class.java)
                                    startActivityForResult(intent, requestcode)
                                }
                            }
                    }else{
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            val current = LocalDateTime.now()
                            val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
                            val tanggal =  current.format(formatter)
                           db.collection("ListUser/$email/HistoryScann").get().addOnSuccessListener { Document->
                                if(Document != null){
                                    for(doc in Document){
                                        val tglscan = doc.getString("Tanggal Scann").toString()
                                        if(tanggal == tglscan){
                                            Log.d("testM","Sama")
                                            buttonScann.visibility = View.VISIBLE
                                            progressBar2.visibility = View.INVISIBLE
                                            val intent = Intent(this,PopUp01::class.java)
                                            startActivity(intent)
                                            overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out)
                                        }else{
                                            Log.d("testM","Tidak Sama")
                                            buttonScann.visibility = View.VISIBLE
                                            progressBar2.visibility = View.INVISIBLE
                                            val intent = Intent(this@HomePage, ActivityScanner::class.java)
                                            startActivityForResult(intent, requestcode)
                                        }
                                    }
                                }
                            }
                        }else{
                            db.collection("ListUser/$email/HistoryScann").get().addOnSuccessListener { Document->
                                val date = Date()
                                val formatter = SimpleDateFormat("MM/dd/yyyy")
                                val tanggal = formatter.format(date)
                                if(Document != null){
                                    for(doc in Document){
                                        val tglscan = doc.getString("Tanggal Scann")
                                        val tgl = formatter.parse(tglscan!!)
                                        val newtgl = formatter.format(tgl!!)
                                        Log.d("testM","--/$tanggal--//Sama--//$newtgl")
                                        if(tanggal == newtgl){
                                            Log.d("testM","Sama")
                                            buttonScann.visibility = View.VISIBLE
                                            progressBar2.visibility = View.INVISIBLE
                                            val intent = Intent(this,PopUp01::class.java)
                                            startActivity(intent)
                                            overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out)
                                        }else{
                                            Log.d("testM","Tidak Sama")
                                            buttonScann.visibility = View.VISIBLE
                                            progressBar2.visibility = View.INVISIBLE
                                            val intent = Intent(this@HomePage, ActivityScanner::class.java)
                                            startActivityForResult(intent, requestcode)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        imgcoup.setOnClickListener {
            val intent = Intent(this,CouponList::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out)
        }
        imgprop.setOnClickListener {
            val intent = Intent(this,Profile::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out)
        }
    }

    @SuppressLint("SimpleDateFormat")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
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
                             try{
                            if (requestCode == requestcode && resultCode == Activity.RESULT_OK) {
                                if (data != null) {
                                    val barcode = data.getParcelableExtra<Barcode>("barcode")
                                    val scan = barcode?.displayValue.toString()
                                    Log.d("testM", scan)
                                    randomKupon(scan,emaildb)
                                db.collection("Random").get().addOnSuccessListener {Document->
                                    if(Document != null){
                                        for(doc in Document){
                                            val scanstring = doc.getString("RandomString").toString()
                                            if(scan == scanstring){
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                    val current = LocalDateTime.now()
                                                    val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
                                                    val tanggal: String = current.format(formatter)
                                                    db.collection("ListUser/$email/HistoryScann").document("Scann")
                                                        .update(mapOf("Tanggal Scann" to tanggal))
                                                }else{
                                                    val date = Date()
                                                    val formatter = SimpleDateFormat("MM/dd/yyyy")
                                                    val tanggal: String = formatter.format(date)
                                                    db.collection("ListUser/$email/HistoryScann").document("Scann")
                                                        .update(mapOf("Tanggal Scann" to tanggal))
                                                }
                                            }else{
                                                val intent = Intent(this,PopUp02::class.java)
                                                startActivity(intent)
                                                overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out)
                                                buttonScann.visibility = View.VISIBLE
                                            }
                                        }
                                    }
                                }
                                }
                            }
                        }catch (e:Exception){
                                 e.printStackTrace()
                             }
                        }
                    }
                }
            }
        }
    }

    private fun randomKupon(scan :String,namaUser:String){
        val tt : ArrayList<String> = ArrayList()
        val db = FirebaseFirestore.getInstance()
        val db99 = FirebaseFirestore.getInstance()
        val docRef99 = db99.collection("Coupon")
        docRef99.get().addOnSuccessListener {Document ->
            for(doc in Document){
                val nomorkupon = doc.getString("No Kupon")
                tt.add(nomorkupon!!)
            }
            db.collection("Random").get().addOnSuccessListener {Document->
                if(Document != null){
                    for(doc in Document){
                        val randomString = doc.getString("RandomString").toString()
                        if(scan == randomString){
                            try {
                                val size = tt.size
                                val rand = (1 until size).random()
                                tt.shuffle()
                                for(i in 1..rand){
                                    if(tt[i] == rand.toString()){
                                        Log.d("testM","Sama : "+tt[i]+"--//--$rand--//--")
                                        locatecoupon(rand.toString(),namaUser)
                                        break
                                    }else{
                                        Log.d("testM","Tidak Sama : "+tt[i]+"--//--$rand")
                                        if(i == rand && tt[i] != rand.toString()){
                                            locatecoupon2(namaUser)
                                        }
                                    }

                                }
                            }catch (e : java.lang.Exception){}
                        }
                    }
                }
            }

        }
    }
    private fun locatecouponUlangTahun(namaUser:String){
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("KuponUlangTahun")
        docRef.get().addOnSuccessListener { Document ->
            if (Document != null) {
                for (doc in Document) {
                    val num = doc.getString("No Kupon")
                        val namaKupon = doc.getString("Nama Coupon").toString()
                        val tglExp = doc.getString("Tanggal Exp").toString()
                        val picKupon = doc.getString("Picture").toString()
                        val nokupon = doc.getString("No Kupon").toString()
                        Log.d("testM", "No = $num -/- Nama Kupon = $namaKupon")
                        tambahkuponUlangTahun(namaKupon, tglExp, picKupon, namaUser, nokupon)
                }
            }
        }
    }

    private fun tambahkuponUlangTahun(namaKupon:String,tgl:String,pic:String,namaUser:String,nokupon:String){
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
                val intent = Intent(this,CouponList::class.java)
                progressBar2.visibility = View.INVISIBLE
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }
    }

    private fun locatecoupon(randonnumber : String,namaUser:String){
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("Coupon")

        docRef.get().addOnSuccessListener { Document ->
            if (Document != null) {
                for (doc in Document) {
                    val num = doc.getString("No Kupon")
                    if(randonnumber == num){
                        val namaKupon = doc.getString("Nama Coupon").toString()
                        val tglExp = doc.getString("Tanggal Exp").toString()
                        val picKupon = doc.getString("Picture").toString()
                        val nokupon = doc.getString("No Kupon").toString()
                        Log.d("testM","No = $num -/- Nama Kupon = $namaKupon")
                        tambahlistkupon(namaKupon,tglExp,picKupon,namaUser,nokupon)
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
                    val intent = Intent(this,AndaBeruntung::class.java)
                    intent.putExtra("NomorKupon",rsg)
                    startActivity(intent)
                    overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out)
                }
    }
    //------------------------------------------------
    private fun locatecoupon2(namaUser:String){
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("Coupon")
        docRef.get().addOnSuccessListener { Document ->
            if (Document != null) {
                for (doc in Document) {
                    Log.d("testM","So sampeDisini$namaUser")
                    val num = doc.getString("No Kupon")
                    val n100 = "999"
                    if(n100 == num){
                        val namaKupon = doc.getString("Nama Coupon").toString()
                        val tglExp = doc.getString("Tanggal Exp").toString()
                        val picKupon = doc.getString("Picture").toString()
                        val nokupon = doc.getString("No Kupon").toString()
                        Log.d("testM","No = $num -/- Nama Kupon = $namaKupon")
                        tambahlistkupon2(namaKupon,tglExp,picKupon,namaUser,nokupon)
                    }
                }
            }
        }
    }
    private fun tambahlistkupon2(namaKupon:String,tgl:String,pic:String,namaUser:String,nokupon:String){
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
                val intent = Intent(this,BelumBeruntung::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out)
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

    @SuppressLint("SetTextI18n")
    private fun marikhnotif(){
        val db = FirebaseFirestore.getInstance()
        db.collection("Notifikasi").get().addOnSuccessListener {Document->
            if(Document != null){
                for (doc in Document){
                    val judul = doc.getString("Judul")
                    val isipesan = doc.getString("Pesan")
                    if (doc.id == "pesan"){
                        val notif = "$isipesan \n\n\n "
                        author.text = judul
                        pesan.text = notif
                    }
                }
            }
            if (Document.isEmpty){
                val notif = "Selamat datang \n\n\n "
                author.text = "Marikhh"
                pesan.text = notif
            }
        }
    }

    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}