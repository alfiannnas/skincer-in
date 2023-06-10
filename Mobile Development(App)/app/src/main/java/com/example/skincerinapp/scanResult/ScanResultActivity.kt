package com.example.skincerinapp.scanResult

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.example.skincerinapp.R
import com.example.skincerinapp.databinding.ActivityScanResultBinding
import com.example.skincerinapp.model.Cancer
import com.example.skincerinapp.model.CancerData
import com.itextpdf.text.Document
import com.itextpdf.text.PageSize
import com.itextpdf.text.pdf.PdfWriter
import org.tensorflow.lite.Interpreter
import java.io.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class ScanResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanResultBinding
    private val modelPath = "tf_lite_model.tflite"
    private val tfliteModel: MappedByteBuffer by lazy { loadModel(modelPath) }
    private lateinit var interpreter: Interpreter
    private val SAVE_PDF_REQUEST_CODE = 2

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

            val processedBitmap = preprocessImage(bitmap)
            val result = classifyImage(processedBitmap)

            processResult(result)
        }

        binding.button.setOnClickListener {
            convertToPdf()
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

            inputBuffer.putFloat(red.toFloat() )
            inputBuffer.putFloat(green.toFloat() )
            inputBuffer.putFloat(blue.toFloat() )
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

        val confidenceThreshold = 0.5

        val result = if (maxIndex != -1 && outputArray[maxIndex] >= confidenceThreshold) {
            ClassificationResult(labels[maxIndex], outputArray[maxIndex])
        } else {
            ClassificationResult("Unable to classify", 0f)
        }

        outputBuffer.rewind()
        while (outputBuffer.hasRemaining()) {
            val value = outputBuffer.float
            Log.d("OutputBuffer", value.toString())
        }

        return result
    }

    private data class ClassificationResult(val className: String, val confidence: Float)

    private fun loadLabels(labelPath: String): List<String> {
        val labels = mutableListOf<String>()
        try {
            val inputStream = assets.open(labelPath)
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


    private fun getCancer(): List<Cancer> {
        return CancerData.cancer
    }

    @SuppressLint("StringFormatInvalid")
    private fun processResult(result: ClassificationResult) {

        val className = result.className
        val confidence = result.confidence.toString()

        val resultText = getString(R.string.result_scan, className)
        val acurationText = getString(R.string.acuration, confidence)

        binding.indicator.text = resultText
        binding.confidence.text = acurationText


        val cancerList = getCancer()
        val resultCancer = cancerList.find { it.name == result.className }

        if (resultCancer != null) {
            binding.patient.text = resultCancer.patient
            binding.danger.text = resultCancer.Dangerous
            binding.descriptionText.text = resultCancer.desc
            binding.causeText.text = resultCancer.causes
            binding.symptomsText.text = resultCancer.symptoms
            binding.treatmentText.text = resultCancer.treatment
            binding.preventionText.text = resultCancer.prevention
            binding.source.text =resultCancer.source
        } else {
            binding.patient.text = "-"
            binding.danger.text = "-"
            binding.descriptionText.text = getString(R.string.healthy_skin)
            binding.causeText.text = getString(R.string.healthy_skin)
            binding.symptomsText.text = getString(R.string.healthy_skin)
            binding.treatmentText.text = getString(R.string.healthy_skin)
            binding.preventionText.text = getString(R.string.healthy_skin)

        }
    }

    private fun convertToPdf() {
        val directory = getExternalFilesDir(null)
        val pdfFilePath = "$directory/scan_result.pdf"

        try {
            val document = Document()
            val outputStream = FileOutputStream(pdfFilePath)
            PdfWriter.getInstance(document, outputStream)
            document.open()

            val bitmap = Bitmap.createBitmap(binding.root.width, binding.root.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            binding.root.draw(canvas)

            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArray = stream.toByteArray()

            val image = com.itextpdf.text.Image.getInstance(byteArray)
            image.scaleToFit(PageSize.A4.width, PageSize.A4.height)
            image.setAbsolutePosition(0f, 0f)
            document.add(image)

            document.close()

            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/pdf"
                putExtra(Intent.EXTRA_TITLE, "scan_result.pdf")
            }

            startActivityForResult(intent, SAVE_PDF_REQUEST_CODE)

            Toast.makeText(this, "Activity converted to PDF", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SAVE_PDF_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                val inputStream = FileInputStream(File(getExternalFilesDir(null), "scan_result.pdf"))
                val outputStream = contentResolver.openOutputStream(uri)
                outputStream?.let { inputStream.copyTo(it) }
                inputStream.close()
                outputStream?.close()
                Toast.makeText(this, "PDF file saved", Toast.LENGTH_SHORT).show()
            }
        }
    }

}