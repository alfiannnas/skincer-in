package com.example.skincerinapp.scanResult

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.skincerinapp.R
import com.example.skincerinapp.databinding.ActivityScanResultBinding
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.IntBuffer
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class ScanResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanResultBinding
    private val modelPath = "tf_lite_model.tflite"
    private val tfliteModel: MappedByteBuffer by lazy { loadModel(modelPath) }
    private lateinit var interpreter: Interpreter

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

            // Preprocess the image if required
            val processedBitmap = preprocessImage(bitmap)

            // Perform inference
            val result = classifyImage(processedBitmap)

            // Process the result as per your requirements
            processResult(result)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadModel(modelPath: String): MappedByteBuffer {
        val assetManager = assets
        val fileDescriptor = assetManager.openFd(modelPath)
        val fileChannel = FileInputStream(fileDescriptor.fileDescriptor).channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun preprocessImage(bitmap: Bitmap): Bitmap {
        val targetSize = 150
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, targetSize, targetSize, false)
        return scaledBitmap
    }

    private fun classifyImage(bitmap: Bitmap): String {
        if (!::interpreter.isInitialized) {
            interpreter = Interpreter(tfliteModel)
        }

        val inputShape = interpreter.getInputTensor(0).shape()
        val inputSize = inputShape[1] * inputShape[2] * inputShape[3]

        val inputBuffer = ByteBuffer.allocateDirect(inputSize * 4) // 4 bytes for each float
        inputBuffer.order(ByteOrder.nativeOrder())
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        // Iterate over each pixel and extract the color components
        for (pixel in pixels) {
            val red = (pixel shr 16) and 0xFF
            val green = (pixel shr 8) and 0xFF
            val blue = pixel and 0xFF

            inputBuffer.putFloat(red.toFloat())
            inputBuffer.putFloat(green.toFloat())
            inputBuffer.putFloat(blue.toFloat())
        }

        val outputSize = 150 * 150 * 3 * 4 // 4 bytes for each float
        val outputBuffer = ByteBuffer.allocateDirect(outputSize)
        interpreter.run(inputBuffer, outputBuffer)

        val outputArray = FloatArray(outputBuffer.remaining() / 4)
        outputBuffer.order(ByteOrder.nativeOrder()).asFloatBuffer().get(outputArray)

        var maxConfidence = -1.0f
        var maxIndex = -1
        for (i in outputArray.indices) {
            if (outputArray[i] > maxConfidence) {
                maxConfidence = outputArray[i]
                maxIndex = i
            }
        }

        val result = "Class: $maxIndex, Confidence: $maxConfidence"
        return result
    }



    private fun processResult(result: String) {
        binding.indicator.text = result
    }
}