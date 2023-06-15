package com.example.skincerinapp.scanResult


import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class ScanResultViewModel(application: Application) : AndroidViewModel(application) {
    private val modelPath = "tf_lite_model.tflite"
    private val tfliteModel: ByteBuffer by lazy { loadModel(modelPath) }
    private lateinit var interpreter: Interpreter

    private val _scanResult = MutableLiveData<ClassificationResult>()
    val scanResult: LiveData<ClassificationResult> = _scanResult

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun processImage(imagePath: String) {
        viewModelScope.launch {
            try {
                val bitmap = withContext(Dispatchers.IO) {
                    BitmapFactory.decodeFile(imagePath)
                }
                val processedBitmap = preprocessImage(bitmap)
                val result = classifyImage(processedBitmap)
                _scanResult.value = result
            } catch (e: Exception) {
                _errorMessage.value = "Failed to process image"
            }
        }
    }

    private fun loadModel(modelPath: String): ByteBuffer {
        val assetManager = getApplication<Application>().assets
        val fileDescriptor = assetManager.openFd(modelPath)
        val fileChannel = fileDescriptor.createInputStream().channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength

        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun preprocessImage(bitmap: Bitmap): Bitmap {
        val targetSize = 150
        return Bitmap.createScaledBitmap(bitmap, targetSize, targetSize, false)
    }

    private fun classifyImage(bitmap: Bitmap): ClassificationResult {
        if (!::interpreter.isInitialized) {
            interpreter = Interpreter(tfliteModel)
        }

        val inputShape = interpreter.getInputTensor(0).shape()
        val inputSize = inputShape[1] * inputShape[2] * inputShape[3]

        val inputBuffer = ByteBuffer.allocateDirect(inputSize * 4)
        inputBuffer.order(ByteOrder.nativeOrder())
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        for (pixel in pixels) {
            val red = (pixel shr 16) and 0xFF
            val green = (pixel shr 8) and 0xFF
            val blue = pixel and 0xFF

            inputBuffer.putFloat(red.toFloat())
            inputBuffer.putFloat(green.toFloat())
            inputBuffer.putFloat(blue.toFloat())
        }

        val outputShape = interpreter.getOutputTensor(0).shape()
        val outputSize = outputShape[1]

        val outputBuffer = ByteBuffer.allocateDirect(outputSize * 4)
        outputBuffer.order(ByteOrder.nativeOrder())
        interpreter.run(inputBuffer, outputBuffer)

        val outputArray = FloatArray(outputSize)
        outputBuffer.rewind()
        outputBuffer.asFloatBuffer().get(outputArray)

        val labels = loadLabels("label.txt")
        val maxIndex = outputArray.indices.maxByOrNull { outputArray[it] } ?: -1
        val confidenceThreshold = 0.5f

        val result = if (maxIndex != -1 && outputArray[maxIndex] >= confidenceThreshold) {
            ClassificationResult(labels[maxIndex], outputArray[maxIndex])
        } else {
            ClassificationResult("Normal Skin", 0f)
        }

        outputBuffer.rewind()
        while (outputBuffer.hasRemaining()) {
            val value = outputBuffer.float
            Log.d("OutputBuffer", value.toString())
        }

        return result
    }

    data class ClassificationResult(val className: String, val confidence: Float)

    private fun loadLabels(labelPath: String): List<String> {
        val labels = mutableListOf<String>()
        try {
            val inputStream = getApplication<Application>().assets.open(labelPath)
            val reader = BufferedReader(InputStreamReader(inputStream))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                labels.add(line.orEmpty())
            }
            reader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return labels
    }


}
