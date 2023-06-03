package com.example.skincerinapp.scanResult

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.skincerinapp.R
import com.example.skincerinapp.databinding.ActivityScanResultBinding

class ScanResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanResultBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setTitle(R.string.scan_result)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val imageUri = intent.getStringExtra("imageUri")

        if (imageUri != null) {
            val bitmap = BitmapFactory.decodeFile(imageUri)

            binding.imageResult.setImageBitmap(bitmap)
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }


}