package com.neko.hiepdph.skibyditoiletvideocall.view.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.neko.hiepdph.skibyditoiletvideocall.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
    }

}