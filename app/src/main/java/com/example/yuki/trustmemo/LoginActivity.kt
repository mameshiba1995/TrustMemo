package com.example.yuki.trustmemo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    //Firebase
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        loginBtn.setOnClickListener {

            val mail = loginMailEdit.text.toString()
            val pass = loginPassEdit.text.toString()

            if(mail.isNullOrBlank()){
                Toast.makeText(baseContext, "メールアドレスを入力してください", Toast.LENGTH_SHORT).show()

            }else if(pass.isNullOrBlank()) {
                Toast.makeText(baseContext, "パスワードを入力してください", Toast.LENGTH_SHORT).show()

            } else {

                auth.signInWithEmailAndPassword(mail, pass).addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(baseContext, "ログインしました。", Toast.LENGTH_SHORT).show()

                            val main =  Intent(this, MainActivity::class.java)
                            startActivity(main)

                        } else {
                            Toast.makeText(baseContext, "メールアドレスまたはパスワードが違います。", Toast.LENGTH_SHORT).show()

                        }
                    }
            }
        }

        registerBtn.setOnClickListener {

            val mail = loginMailEdit.text.toString()
            val pass = loginPassEdit.text.toString()

            if(mail.isNullOrBlank()){
                Toast.makeText(baseContext, "メールアドレスを入力してください", Toast.LENGTH_SHORT).show()

            }else if(pass.isNullOrBlank()) {
                Toast.makeText(baseContext, "パスワードを入力してください", Toast.LENGTH_SHORT).show()

            }else if(pass.length < 6) {
                Toast.makeText(baseContext, "パスワードは6文字以上で入力してください", Toast.LENGTH_SHORT).show()

            } else {
                auth.createUserWithEmailAndPassword(mail, pass).addOnCompleteListener(this) { task ->

                        if (task.isSuccessful) {
                            Toast.makeText(baseContext, "登録しました。", Toast.LENGTH_SHORT).show()

                        } else {
                            Toast.makeText(baseContext, "登録できませんでした。", Toast.LENGTH_SHORT).show()

                        }
                    }
            }
        }

        etcSignInBtn.setOnClickListener{

            val gooLogin =  Intent(this, GoogleLoginActivity::class.java)
            startActivity(gooLogin)
        }

    }
}