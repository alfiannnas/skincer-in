package com.example.skincerinapp.scanResult

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.skincerinapp.R
import com.example.skincerinapp.databinding.ActivityScanResultBinding
import com.example.skincerinapp.model.Cancer
import com.example.skincerinapp.model.CancerData
import com.itextpdf.text.Document
import com.itextpdf.text.PageSize
import com.itextpdf.text.pdf.PdfWriter
import java.io.*

class ScanResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScanResultBinding
    private lateinit var viewModel: ScanResultViewModel
    private val SAVE_PDF_REQUEST_CODE = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setTitle(R.string.scan_result)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val imageUri = intent.getStringExtra("imageUri")
        val bitmap = BitmapFactory.decodeFile(imageUri)

        binding.imageResult.setImageBitmap(bitmap)

        viewModel = ViewModelProvider(this)[ScanResultViewModel::class.java]

        viewModel.errorMessage.observe(this) { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        }

        viewModel.scanResult.observe(this) { result ->
            processResult(result)
        }

        imageUri?.let {
            viewModel.processImage(it)
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

    private fun getCancer(): List<Cancer> {
        return CancerData.cancer
    }

    @SuppressLint("StringFormatInvalid")
    private fun processResult(result: ScanResultViewModel.ClassificationResult) {

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
            binding.source.text ="-"
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