package com.example.skincerinapp.onBoarding

import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.skincerinapp.R
import com.example.skincerinapp.databinding.ActivityOnBoardingBinding
import com.example.skincerinapp.ui.main.MainActivity

class OnBoardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnBoardingBinding
    private lateinit var sliderAdapter: SliderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Initialize slider adapter with string resources
        sliderAdapter = SliderAdapter(
            listOf(
                Slider(
                    R.drawable.onboarding1,
                    getString(R.string.slider_title_1),
                    getString(R.string.slider_description_1)
                ),
                Slider(
                    R.drawable.onboarding2,
                    getString(R.string.slider_title_2),
                    getString(R.string.slider_description_2)
                ),
                Slider(
                    R.drawable.onboarding3,
                    getString(R.string.slider_title_3),
                    getString(R.string.slider_description_3)
                )
            )
        )

        supportActionBar?.hide()
        binding.slider.adapter = sliderAdapter
        getIndicator()
        currentIndicator(0)
        binding.slider.registerOnPageChangeCallback(object :ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentIndicator(position)
            }
        })

        binding.nextButton.setOnClickListener{
            if(binding.slider.currentItem +1 < sliderAdapter.itemCount){
                binding.slider.currentItem += 1
            }else{
                Intent(applicationContext, MainActivity::class.java).also{
                    startActivity(it)
                }
            }
        }
        binding.skipButton.setOnClickListener {
            Intent(applicationContext, MainActivity::class.java).also{
                startActivity(it)
            }
        }
    }

    private fun getIndicator() {
        val indicator = arrayOfNulls<ImageView>(sliderAdapter.itemCount)
        val layoutParams: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(8, 0, 8, 0)
        for (i in indicator.indices) {
            indicator[i] = ImageView(applicationContext)
            indicator[i].apply {
                this?.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive
                    )
                )
                this?.layoutParams = layoutParams
            }
            binding.indicator.addView(indicator[i])
        }
    }

    private fun currentIndicator(index:Int){
        val childCount = binding.indicator.childCount
        for(i in 0 until childCount){
            val imageView = binding.indicator.getChildAt(i) as ImageView
            if(i ==index){
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive
                    )
                )
            }else{
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_active
                    )
                )
            }
        }
    }
}