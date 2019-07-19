package com.intern.registrationformapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        buNewMember.setOnClickListener {
            val intent=Intent(this@MainActivity,register_form::class.java)
            startActivity(intent)
        }

    }
}

