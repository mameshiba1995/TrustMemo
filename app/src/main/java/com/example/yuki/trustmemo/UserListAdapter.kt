package com.example.yuki.trustmemo

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class UserListAdapter(val mCtx: Context, val layoutResId: Int, val userList: List<UserAccount>) : ArrayAdapter<UserAccount>(mCtx, layoutResId, userList){
    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater : LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(layoutResId, null)

        val nickName = view.findViewById<TextView>(R.id.nickName)

        val user = userList[position]

        nickName.text = user.name + "(" + user.area + ")"
        view.setBackgroundColor(
            if(position % 2 == 0){
                Color.LTGRAY
            } else {
                Color.WHITE
            }
        )

        return view
    }
}