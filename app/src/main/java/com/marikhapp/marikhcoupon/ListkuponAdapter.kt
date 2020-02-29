package com.marikhapp.marikhcoupon

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils.loadAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*


class ViewHolderKupon(itemView: View) : RecyclerView.ViewHolder(itemView){
    val button = itemView.findViewById(R.id.buttonUseIt) as TextView
    var namakupon : TextView = itemView.findViewById<View>(R.id.namakupon) as TextView
    var tanggal : TextView = itemView.findViewById<View>(R.id.expdate) as TextView
    var img : ImageView = itemView.findViewById(R.id.gbrKupon) as ImageView
    val nok : TextView = itemView.findViewById(R.id.nnn) as TextView
    val sts : TextView = itemView.findViewById(R.id.sts) as TextView

    }

@Suppress("NAME_SHADOWING")
class ListkuponAdapter(private var userList: ArrayList<Kupon>) : RecyclerView.Adapter<ViewHolderKupon>()  {
    @SuppressLint("SetTextI18n", "SimpleDateFormat")

    private lateinit var mContext: Context
    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onBindViewHolder(p0: ViewHolderKupon, p1: Int) {
        val kupon: Kupon = userList[p1]
        p0.button.text = kupon.btn
        p0.namakupon.text = kupon.namakupon
        p0.tanggal.text = kupon.tanggal
        p0.nok.text = kupon.nok
        p0.sts.text = kupon.sts
        Picasso.get().load(kupon.img).into(p0.img)

        setAnimation(p0.itemView, p1)

        val user = FirebaseAuth.getInstance().currentUser
        val nk = p0.namakupon.text
        val nok = p0.nok.text
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
                            val db = FirebaseFirestore.getInstance()
                            val docRef = db.collection("ListUser/$email/DaftarKupon")
                            docRef.get().addOnSuccessListener { Document ->

                                for (doc in Document) {
                                    val status = doc.getString("Status")
                                    val namaCoupon = doc.getString("Nama Kupon")
                                    val nomorKK = doc.getString("No Kupon")
                                    val tanggExp = doc.getString("Tanggal Exp")
                                    val sdf = SimpleDateFormat("MM/dd/yyyy")
                                    val date = Date()
                                    val date1 = sdf.parse(tanggExp!!)
                                    val tanggalDataBase= sdf.format(date1!!)
                                    val tanggalHariIni = sdf.format(date)
                                    if(nk == namaCoupon && nok == nomorKK){
                                    //-------------------Cek Tanggal Kadaluarsa-------------------------\\
                                        if(tanggalHariIni > tanggalDataBase){
                                             Log.d("testM", "$tanggalHariIni after $tanggalDataBase")
                                            p0.button.text = "Gunakan"
                                            p0.button.setBackgroundResource(R.drawable.button_custom2)
                                            db.collection("ListUser/$email/DaftarKupon").document(doc.id)
                                                .update(mapOf("Status" to "Used"))
                                        }else {
                                    //-------------------Jika Belum Kadaluarsa--------------------------------\\
                                            Log.d("testM", "$tanggalHariIni before $tanggalDataBase")
                                    //------------------Jika Kupon Sudah Digunakan----------------------------\\
                                            if(status == "Used"){
                                                p0.button.text = "Gunakan"
                                                p0.button.setBackgroundResource(R.drawable.button_custom2)
                                                Log.d("testM",status)
                                            }else if(status == "UnUsed"){
                                    //-----------------Jika Kupon Belum Digunakan----------------------------\\
                                                p0.button.setBackgroundResource(R.drawable.button_custom)
                                                //------------ButtonClick--------------------------\\
                                                p0.button.setOnClickListener {view ->
                                                    val nk = p0.namakupon.text
                                                    val nok = p0.nok.text
                                                    user.let {
                                                        val email = user.email
                                                        Log.d("testM","User : $email")
                                                        val db = FirebaseFirestore.getInstance()
                                                        val docRef = db.collection("ListUser")

                                                        docRef.get().addOnSuccessListener { Document ->
                                                            if (Document != null) {
                                                                for (doc in Document) {
                                                                    val emaildb = doc.getString("Email").toString()
                                                                    if (email == emaildb) {
                                                                        val db = FirebaseFirestore.getInstance()
                                                                        val docRef = db.collection("ListUser/$email/DaftarKupon")
                                                                        docRef.get().addOnSuccessListener { Document ->
                                                                            for (doc in Document) {
                                                                                val status = doc.getString("Status")
                                                                                val namaKoupon = doc.getString("Nama Kupon")
                                                                                val noKupon = doc.getString("No Kupon")
                                                                                if(nk == namaKoupon && nok == noKupon){
                                                                                    if(status == "UnUsed"){
                                                                                        //---------------------------------------------------------
                                                                                        val intent = Intent(view.context,ConfirmCoupon::class.java)
                                                                                        intent.putExtra("namaKupon",nk)
                                                                                        intent.putExtra("noKupon",noKupon)
                                                                                        view.context.startActivity(intent)
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolderKupon {
        mContext = p0.context
        val v = LayoutInflater.from(p0.context).inflate(R.layout.container2, p0, false)
        return ViewHolderKupon(v)
    }

    override fun getItemCount(): Int {
        return userList.size
    }
    fun filterList(filteredList: ArrayList<Kupon>) {
        userList = filteredList
        notifyDataSetChanged()
    }
    private fun setAnimation(viewToAnimate: View, position: Int) {
        val lastPosition : Int = -1
        if (position > lastPosition) {
            val animation =loadAnimation(mContext, R.anim.recyclerview_slide_anim)
            viewToAnimate.startAnimation(animation)
        }else if(position < lastPosition){
            val animation =loadAnimation(mContext, R.anim.recyclerview_slide_anim2)
            viewToAnimate.startAnimation(animation)
        }
    }



}
