package com.example.yuki.trustmemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_add_memo.*
import java.util.Date
import java.text.SimpleDateFormat

class AddMemoActivity : AppCompatActivity() {

    //Firebase
    private lateinit var auth: FirebaseAuth

    lateinit var editTitleName: EditText
    lateinit var editMemo: EditText

    lateinit var  memoList: MutableList<Memo>
    lateinit var  ref: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_memo)

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        loginCheck(user)

        memoList = mutableListOf()
        ref = FirebaseDatabase.getInstance().getReference("memos")
        editTitleName = findViewById(R.id.titleEdit)
        editMemo = findViewById(R.id.memoEdit)

        saveBtn.setOnClickListener{
            //メモをRealtimeDBに保存
            saveMemo()
        }

        returnBtn.setOnClickListener{
            finish()
        }
    }

    private fun saveMemo(){

        val title = editTitleName.text.toString().trim()
        val memo = editMemo.text.toString().trim()
        val lat = intent.getDoubleExtra("lat", 0.0)
        val lng = intent.getDoubleExtra("lng", 0.0)
        val date = SimpleDateFormat("yyyy/MM/dd HH:mm").format(Date())

        if(title.isEmpty()){
            editTitleName.error = "タイトルを入力してください"
            return
        } else if(memo.isEmpty()){
            editMemo.error = "メモを入力してください"
            return
        } else if(lat == 0.0 || lng == 0.0){
            Toast.makeText(baseContext, "位置情報が取得できません", Toast.LENGTH_SHORT).show()
            return
        }

        val memoId = ref.push().key

        val addMemo = Memo(memoId.toString(), title, date, memo, lat, lng)
        ref.child(memoId.toString()).setValue(addMemo).addOnCanceledListener {

        }
        Toast.makeText(baseContext, "保存しました。", Toast.LENGTH_SHORT).show()
        finish()

    }

    //ログインしていない状態の場合ログイン画面へ
    private fun loginCheck(user: FirebaseUser?){

        if (user == null) {
            val login =  Intent(this, LoginActivity::class.java)
            startActivity(login)
        }
    }


}
