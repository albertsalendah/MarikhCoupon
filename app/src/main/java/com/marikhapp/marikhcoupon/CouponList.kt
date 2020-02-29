package com.marikhapp.marikhcoupon

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_coupon_list.*


class CouponList : AppCompatActivity() {
    private var kuponList = ArrayList<Kupon>()
    private var container = ArrayList<Kupon>()
    private lateinit var mAdapter: ListkuponAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coupon_list)
        val user = FirebaseAuth.getInstance().currentUser

        val mRecyclerView = findViewById<RecyclerView>(R.id.listkupon)
        mRecyclerView.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(this)
        mAdapter = ListkuponAdapter(kuponList)

        mRecyclerView.layoutManager = mLayoutManager
        mRecyclerView.adapter = mAdapter

        floatingActionButton.setOnClickListener {
            val intent = Intent(this,Keterangan::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        searchV.setOnSearchClickListener {
            textView13.visibility = View.INVISIBLE
            textView22.visibility = View.INVISIBLE
        }
        searchV.setOnCloseListener {
            textView13.visibility = View.VISIBLE
            textView22.visibility = View.VISIBLE
            false
        }
        searchV.imeOptions = EditorInfo.IME_ACTION_DONE
        val s= "Ketik nama kupon"
        searchV.queryHint = s
        searchV.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchV.clearFocus()
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText.toString())
                return true
            }
        })

        listkupon.setOnTouchListener { view, _ ->
            val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(searchV.windowToken, 0)

        }

        user?.let {
            val namaU = user.email
            Log.d("testM","User : $namaU")
            val db = FirebaseFirestore.getInstance()
            val docRef = db.collection("ListUser")

            docRef.get().addOnSuccessListener { Document ->
                if (Document != null) {
                    for (doc in Document) {
                        val emaildb = doc.getString("Email").toString()
                        if (namaU == emaildb) {
                            progressBar3.visibility = View.VISIBLE
                            calllistkupon(namaU)
                            mAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        }

        homeicon.setOnClickListener {
            val intent = Intent(this,HomePage::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out)
        }

        imgpr.setOnClickListener {
            val intent = Intent(this,Profile::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out)
        }

    }

    @SuppressLint("DefaultLocale")
    private fun filter(text: String) {
        kuponList.clear()
        if(text.isNotEmpty()){
            for (item in container) {
                if (item.namakupon!!.toLowerCase().contains(text.toLowerCase())) {
                    kuponList.add(item)
                }
            }
        }else{
            kuponList.addAll(container)
        }
        kuponList.sortWith(Comparator { o1, o2 -> o1.sts.compareTo(o2.sts) })
       mAdapter.filterList(kuponList)
        mAdapter.notifyDataSetChanged()
    }

    @SuppressLint("SimpleDateFormat")
    private fun calllistkupon(namaU:String){

        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("ListUser/$namaU/DaftarKupon")
        docRef.addSnapshotListener{ documentSnapshots: QuerySnapshot?, _: FirebaseFirestoreException?->
            progressBar3.visibility = View.INVISIBLE

            for (doc in documentSnapshots!!.documentChanges) {
                if(doc.type == DocumentChange.Type.ADDED){
                    val namaCoupon = doc.document.getString("Nama Kupon")
                    val picture = doc.document.getString("Picture")!!.toUri()
                    val tanggal = doc.document.getString("Tanggal Exp")
                    val no = doc.document.getString("No Kupon")
                    val sts = doc.document.getString("Status")
                    kuponList.add(Kupon("$namaCoupon", "Berlaku sampai : $tanggal", "Gunakan",picture,no!!,sts!!))
                    container.add(Kupon("$namaCoupon", "Berlaku sampai : $tanggal", "Gunakan",picture,no,sts))
                }
                if(doc.type == DocumentChange.Type.REMOVED){
                    val namaCoupon = doc.document.getString("Nama Kupon")
                    val picture = doc.document.getString("Picture")!!.toUri()
                    val tanggal = doc.document.getString("Tanggal Exp")
                    val no = doc.document.getString("No Kupon")
                    val sts = doc.document.getString("Status")
                    kuponList.remove(Kupon("$namaCoupon", "Berlaku sampai : $tanggal", "Gunakan",picture,no!!,sts!!))
                    container.remove(Kupon("$namaCoupon", "Berlaku sampai : $tanggal", "Gunakan",picture,no,sts))
                }
                if(doc.type == DocumentChange.Type.MODIFIED){
                    mAdapter.notifyDataSetChanged()
                }
                kuponList.sortWith(Comparator { o1, o2 -> o1.tanggal!!.compareTo(o2.tanggal!!) })
                kuponList.sortWith(Comparator { o1, o2 -> o1.sts.compareTo(o2.sts) })
                mAdapter.notifyDataSetChanged()
            }
        }
    }
}
