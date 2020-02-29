package com.marikhapp.marikhcoupon

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_profile.*

@Suppress("NAME_SHADOWING")
class Profile : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        buttonLogOut.setOnClickListener {
         progressBarProfil.visibility = View.VISIBLE
            clearToken()
        }
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
                            val namaU = doc.getString("Nama").toString()
                            val emailU = doc.getString("Email").toString()
                            val nohpU = doc.getString("Nomor Handphone").toString()
                            val tglLU = doc.getString("Tanggal Lahir").toString()
                            textnamauser.text = namaU
                            textemailuser.text = emailU
                                textnouser.text = nohpU
                                texttgluser.text = tglLU
                                Log.d("testM","Nama : $namaU")
                        }
                    }
                }
            }
        }

        imgho.setOnClickListener {
            val intent = Intent(this,HomePage::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out)
            //this.finish()
        }
        imgcoup.setOnClickListener {
            val intent = Intent(this,CouponList::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out)
            //this.finish()
        }
    }
   private fun clearToken(){
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
                                   for (doc in Document){
                                       val id = doc.id
                                       db.collection("ListUser/$email/TokenNotifikasi").document(id)
                                           .delete().addOnSuccessListener {
                                               Log.d("testM","Token Berhasil Hapus")
                                               progressBarProfil.visibility = View.INVISIBLE
                                               FirebaseAuth.getInstance().signOut()
                                               val intent = Intent(this,SignupActivity::class.java)
                                               intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                               intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                               startActivity(intent)
                                           }
                                   }
                               }
                               if(Document.isEmpty){
                                   Log.d("testM","Tidak ada token")
                                   progressBarProfil.visibility = View.INVISIBLE
                                   FirebaseAuth.getInstance().signOut()
                                   val intent = Intent(this,SignupActivity::class.java)
                                   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                   startActivity(intent)
                               }
                           }
                       }
                   }
               }
           }
       }
   }
    override fun onBackPressed() {
        this.finish()
    }
}
