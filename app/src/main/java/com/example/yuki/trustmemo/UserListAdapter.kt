package com.example.yuki.trustmemo

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class UserListAdapter(val mCtx: Context, val layoutResId: Int, val userList: List<UserAccount>) : ArrayAdapter<UserAccount>(mCtx, layoutResId, userList){

    private lateinit var pref: SharedPreferences

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater : LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(layoutResId, null)

        val nickName = view.findViewById<TextView>(R.id.nickName)
        val area = view.findViewById<TextView>(R.id.area)

        val user = userList[position]

        nickName.text = user.name
        area.text = user.area
        view.setBackgroundColor(
            if(position % 2 == 0){
                Color.LTGRAY
            } else {
                Color.WHITE
            }
        )
        nickName.setOnClickListener{
            saveData(user.email)
        }

        return view
    }

    private fun saveData(email: String){

//        val editor = pref.edit()
//        editor.putString("listUser", email).apply()
    }
}