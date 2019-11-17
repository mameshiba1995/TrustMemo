package com.example.yuki.trustmemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_add_memo.*
import kotlinx.android.synthetic.main.activity_add_memo.returnBtn
import kotlinx.android.synthetic.main.activity_my_page.*

class MyPageActivity : AppCompatActivity() {

    //Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var  ref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        ref = FirebaseDatabase.getInstance().getReference("users")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                var isLayoutFlg = false
                if (p0!!.exists()) {
                    for (h in p0.children) {
                        val account = h.getValue(UserAccount::class.java)
                        if(user!!.email.toString().equals(account!!.email)) {
                            isLayoutFlg = true
                        }
                    }
                }
                if(isLayoutFlg) {
                    setContentView(R.layout.activity_user_list)
                } else {
                    setContentView(R.layout.activity_my_page)
                }
            }
        })

        returnBtn.setOnClickListener{
            finish()
        }
        regiBtn.setOnClickListener{
            //メモをRealtimeDBに保存
          //  saveUser(user)
        }

    }



}
