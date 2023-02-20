package com.example.barcodescanscan

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode

private const val CAMERA_REQUEST_CODE = 101

class MainActivity : AppCompatActivity() {

    private lateinit var codeScanner: CodeScanner
    private lateinit var scanner_view: CodeScannerView
    private lateinit var tv_textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        scanner_view = findViewById(R.id.scanner_view)
        tv_textView = findViewById(R.id.tv_textView)
        setupPermissions()
        codeScanner()
    }

    private fun codeScanner(){
        codeScanner = CodeScanner(this, scanner_view)

        codeScanner.apply{
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS

            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.CONTINUOUS
            isAutoFocusEnabled = true
            isFlashEnabled = false

            decodeCallback = DecodeCallback {
                runOnUiThread{
                    tv_textView.text = it.text
                }
            }

            errorCallback = ErrorCallback {
                runOnUiThread {
                    Log.d("Main", "Camera Initliazation error: ${it.message}")
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        codeScanner.startPreview() //try to fetch new QR code after coming back to the app
    }

    override fun onPause() {
        codeScanner.releaseResources() //to release resources and prevent memory leaks
        super.onPause()
    }

    private fun setupPermissions(){
        val permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
        if(permission != PackageManager.PERMISSION_GRANTED){
            makeRequest()
        }
    }

    private fun makeRequest(){
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            CAMERA_REQUEST_CODE ->{
                if(grantResults.isEmpty() || grantResults[0]!= PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "You need the camera permission to be able to use this feature",Toast.LENGTH_SHORT).show()
                }else{
                    //sucessful
                }
            }
        }
    }
}