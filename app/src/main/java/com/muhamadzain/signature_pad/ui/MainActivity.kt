package com.muhamadzain.signature_pad.ui

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.github.gcacace.signaturepad.views.SignaturePad
import com.muhamadzain.signature_pad.R
import com.muhamadzain.signature_pad.base.BaseActivity

class MainActivity : BaseActivity() {

    private lateinit var mSignaturePad : SignaturePad
    private lateinit var bSaveSignature : Button
    private lateinit var bClearSIgnature : Button

    private fun initComponents() {
        bSaveSignature = findViewById(R.id.save_signature)
        bClearSIgnature = findViewById(R.id.clear_button)
        mSignaturePad = findViewById(R.id.signature_pad)
    }

    private fun initEventListener(){
        bSaveSignature.setOnClickListener {
            Toast.makeText(this@MainActivity, "This is work", Toast.LENGTH_SHORT).show()
        }

        bClearSIgnature.setOnClickListener {
            Toast.makeText(this@MainActivity, "This is work", Toast.LENGTH_SHORT).show()
        }

        mSignaturePad.setOnSignedListener(object : SignaturePad.OnSignedListener {
            override fun onStartSigning() { }

            override fun onSigned() {}

            override fun onClear() {}
        })
    }

    override fun getLayoutResource(): Int = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initComponents()
        initEventListener()
        setContentView(R.layout.activity_main)
    }
}