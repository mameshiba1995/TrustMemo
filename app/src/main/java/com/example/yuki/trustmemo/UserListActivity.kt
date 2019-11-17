package com.example.yuki.trustmemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class UserListActivity : AppCompatActivity() {

    lateinit var  userList: MutableList<UserAccount>
    lateinit var  ref: DatabaseReference
    lateinit var  listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)

        userList = mutableListOf()
        ref = FirebaseDatabase.getInstance().getReference("users")
        listView = findViewById(R.id.listView)

        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                if(p0!!.exists()){

                    userList.clear()

                    for(h in p0.children){
                        val user = h.getValue(UserAccount::class.java)
                        userList.add(user!!)
                    }

                    val adapter = UserListAdapter(applicationContext, R.layout.users, userList)
                    listView.adapter = adapter
                }

            }

        })
    }

    //ログインしていない状態の場合ログイン画面へ
    private fun loginCheck(user: FirebaseUser?){
        if (user == null) {
            val login =  Intent(this, LoginActivity::class.java)
            startActivity(login)
        }
    }
}
