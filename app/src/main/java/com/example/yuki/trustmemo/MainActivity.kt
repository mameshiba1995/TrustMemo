package com.example.yuki.trustmemo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    //Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var  ref: DatabaseReference
    //GoogleMap
    private lateinit var gMap: GoogleMap
    //現在地定数
    private val MY_PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 1

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    //位置情報取得オブジェクト
    private lateinit var lastLocation: Location
    //更新内容取得リスナー
    private var locationCallback : LocationCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        var user = auth.currentUser
        loginCheck(user)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        //メモボタン押下
        memoBtn.setOnClickListener{
            val addMemo = Intent(this, AddMemoActivity::class.java)
            addMemo.putExtra("lat", lastLocation.latitude)
            addMemo.putExtra("lng", lastLocation.longitude)
            startActivity(addMemo)
        }
    }

    override fun onStart() {
        super.onStart()
        if(::gMap.isInitialized){
            putMemos()
        }
    }

    //ログインしていない状態の場合ログイン画面へ
    private fun loginCheck(user: FirebaseUser?){

        if (user == null) {
            val login =  Intent(this, LoginActivity::class.java)
            startActivity(login)
        }
    }

    //パーミッション判断
    override fun onMapReady(googleMap: GoogleMap) {

        gMap = googleMap
        checkPermission()
    }

    private fun checkPermission(){

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            myLocationEnable()
        } else {
            //位置情報許可を確認
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission(){

        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            //既に拒否されている場合
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISSION_REQUEST_ACCESS_FINE_LOCATION)
        } else {
            //初めて許可を求める時
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISSION_REQUEST_ACCESS_FINE_LOCATION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            MY_PERMISSION_REQUEST_ACCESS_FINE_LOCATION -> {
                if(permissions.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //許可
                    myLocationEnable()
                } else {
                    //拒否
                    Toast.makeText(baseContext, "現在地を取得できません", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun myLocationEnable(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            gMap.isMyLocationEnabled = true
            val locationRequest = LocationRequest().apply {
                interval = 10000
                fastestInterval = 5000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
            locationCallback = object : LocationCallback(){
                override fun onLocationResult(locationResult: LocationResult?) {
                    if(locationResult?.lastLocation != null){
                        lastLocation = locationResult.lastLocation
                        val currentLatLng = LatLng(lastLocation.latitude,lastLocation.longitude)
                        gMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng))
                        positionView.text = "緯度：${lastLocation.latitude},経度：${lastLocation.longitude}"
                    }
                }
            }
            fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,null)
            putMemos()
        }
    }

    //メモをRealtimeDBから呼び出す
    private fun putMemos() {
        ref = FirebaseDatabase.getInstance().getReference("memos")
        gMap.clear()

        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0!!.exists()) {

                    for (h in p0.children) {
                        val memo = h.getValue(Memo::class.java)
                        val latLng = LatLng(memo!!.lat, memo!!.lng)
                        val marker = MarkerOptions().position(latLng)
                            .title(memo!!.title)
                            .snippet(memo!!.memo)
                            .snippet(memo!!.date)
                            .draggable(false)

                        val descriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
                        marker.icon(descriptor)

                        gMap.addMarker(marker)
                    }
                }

            }

        })
    }
}
