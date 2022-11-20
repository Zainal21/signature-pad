package com.muhamadzain.signature_pad.ui

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import com.github.gcacace.signaturepad.views.SignaturePad
import com.muhamadzain.signature_pad.R
import com.muhamadzain.signature_pad.base.BaseActivity
import java.io.*


class MainActivity : BaseActivity() {

    private lateinit var mSignaturePad : SignaturePad
    private lateinit var bSaveSignature : AppCompatButton
    private lateinit var bClearSignature : AppCompatButton

    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override fun getLayoutResource(): Int = R.layout.activity_main

    private fun initComponents() {
        bSaveSignature = findViewById(R.id.save_signature)
        bClearSignature = findViewById(R.id.clear_button)
        mSignaturePad = findViewById(R.id.signature_pad)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_EXTERNAL_STORAGE -> {
                if (grantResults.size!! <= 0
                    || grantResults[0] !== PackageManager.PERMISSION_GRANTED
                ) {
                    Toast.makeText(
                        this@MainActivity,
                        "Cannot write images to external storage",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
    private fun initEventListener(){
        bSaveSignature.setOnClickListener {
            val signatureBitmap = mSignaturePad.signatureBitmap
            if (addJpgSignatureToGallery(signatureBitmap)) {
                Toast.makeText(
                    this@MainActivity,
                    "Signature saved into the Gallery",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this@MainActivity,
                    "Unable to store the signature",
                    Toast.LENGTH_SHORT
                ).show()
            }
            if (addSvgSignatureToGallery(mSignaturePad.signatureSvg)) {
                Toast.makeText(
                    this@MainActivity,
                    "SVG Signature saved into the Gallery",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this@MainActivity,
                    "Unable to store the SVG signature",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        bClearSignature.setOnClickListener {
            val builder = AlertDialog.Builder(this@MainActivity)
            builder.setMessage("Are you sure you want to Delete this signature?")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, id ->
                    mSignaturePad.clear()
                }
                .setNegativeButton("No") { dialog, id ->
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }

        mSignaturePad.setOnSignedListener(object : SignaturePad.OnSignedListener {
            override fun onStartSigning() { }

            override fun onSigned() {}

            override fun onClear() {
                Toast.makeText(this@MainActivity, "Signature was clear", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        verifyStoragePermissions(this)
        setContentView(R.layout.activity_main)
        initComponents()
        initEventListener()
    }

    private fun verifyStoragePermissions(activity: Activity?) {
        val permission = ActivityCompat.checkSelfPermission(
            activity!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            )
        }
    }


    private fun getAlbumStorageDir(albumName: String?): File {
        val file = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            ), albumName
        )
        if (!file.mkdirs()) {
            Log.e("SignaturePad", "Directory not created")
        }
        return file
    }

    private fun saveBitmapToJPG(bitmap: Bitmap, photo: File?) {
        val newBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(newBitmap)
        canvas.drawColor(Color.WHITE)
        val stream: OutputStream = FileOutputStream(photo)
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        stream.close()
    }

    private fun addJpgSignatureToGallery(signature: Bitmap): Boolean {
        var result = false
        try {
            val photo = File(
                getAlbumStorageDir("SignaturePad"),
                String.format("Signature_%d.jpg", System.currentTimeMillis())
            )
            saveBitmapToJPG(signature, photo)
            scanMediaFile(photo)
            result = true
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return result
    }

    private fun scanMediaFile(photo: File) {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val contentUri: Uri = Uri.fromFile(photo)
        mediaScanIntent.data = contentUri
        this@MainActivity.sendBroadcast(mediaScanIntent)
    }

    fun addSvgSignatureToGallery(signatureSvg: String?): Boolean {
        var result = false
        try {
            val svgFile = File(
                getAlbumStorageDir("SignaturePad"),
                String.format("Signature_%d.svg", System.currentTimeMillis())
            )
            val stream: OutputStream = FileOutputStream(svgFile)
            val writer = OutputStreamWriter(stream)
            writer.write(signatureSvg)
            writer.close()
            stream.flush()
            stream.close()
            scanMediaFile(svgFile)
            result = true
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return result
    }

}