package com.example.skincerinapp.list

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.skincerinapp.databinding.ActivityDetailBinding
import com.example.skincerinapp.model.Cancer
import com.example.skincerinapp.model.CancerData

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val cancerId = intent.getStringExtra("cancerId")
        val cancer = cancerId?.let { getCancerById(it) }

        if (cancer != null) {
            binding.detailImage.setImageResource(cancer.photo)
        }

        binding.detailTitle.text = cancer?.name
        binding.descriptionText.text = cancer?.desc
        binding.causeText.text = cancer?.causes
        binding.symptomsText.text = cancer?.symptoms
        binding.treatmentText.text = cancer?.treatment
        binding.preventionText.text = cancer?.prevention
        binding.source.text = cancer?.source

        binding.backButton.setOnClickListener{
            super.onBackPressed()
            finish()
        }
    }

    private fun getCancerById(cancerId: String): Cancer? {
        return getCancer().find { it.id == cancerId }
    }

    private fun getCancer(): List<Cancer> {
        return CancerData.cancer
    }
}