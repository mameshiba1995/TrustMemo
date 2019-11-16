package com.example.yuki.trustmemo

import java.util.*

class Memo(val id: String, val title: String, val date: String, val memo: String, val lat: Double, val lng: Double, val email: String) {

    constructor() : this("","", "","",0.0, 0.0,"")

}