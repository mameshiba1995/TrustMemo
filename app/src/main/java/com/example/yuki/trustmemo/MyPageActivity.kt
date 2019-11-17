package com.example.yuki.trustmemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_my_page.*


class MyPageActivity : AppCompatActivity() {

    //Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var  ref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        //アカウントが存在しない：false する:true
        var isUserFlg = false
        //ログイン状態確認
        loginCheck(user)

        ref = FirebaseDatabase.getInstance().getReference("users")
        ref.addValueEventListener(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                if (p0!!.exists()) {
                    for (h in p0.children) {
                        val account = h.getValue(UserAccount::class.java)
                        if(user!!.email.toString().equals(account!!.email)) {
                            isUserFlg = true
                        }
                    }
                }
            }
        })
        regiBtn.setOnClickListener{
            if(!isUserFlg) {
                //公開アカウントをRealtimeDBに保存
                saveUser(user)
            } else {
                Toast.makeText(baseContext, "あなたは既にアカウントを持っています。", Toast.LENGTH_SHORT).show()
                Toast.makeText(baseContext, "一覧から他のユーザのメモを見てみましょう！", Toast.LENGTH_SHORT).show()
            }
        }
        returnBtn.setOnClickListener{
            finish()
        }
    }

    //公開アカウント作成
    private fun saveUser(user: FirebaseUser?) {
        //ニックネーム
        val nickname = nicknameEdit.text.toString().trim()
        //居住地
        val area = areaEdit.text.toString().trim()
        //ログインアカウントのemail
        val email = user!!.email.toString()

        if (nickname.isEmpty()) {
            nicknameEdit.error = "ニックネームを入力してください"
            return
        } else if (area.isEmpty()) {
            areaEdit.error = "居住地を入力してください"
            return
        }
            val userId = ref.push().key
            val addUser = UserAccount(userId.toString(), nickname, email, area)
            ref.child(userId.toString()).setValue(addUser).addOnCanceledListener {
            }
            Toast.makeText(baseContext, "保存しました。", Toast.LENGTH_SHORT).show()
            finish()
    }
        //ログインしていない状態の場合ログイン画面へ
        private fun loginCheck(user: FirebaseUser?) {
            if (user == null) {
                val login = Intent(this, LoginActivity::class.java)
                startActivity(login)
            }
        }




}
