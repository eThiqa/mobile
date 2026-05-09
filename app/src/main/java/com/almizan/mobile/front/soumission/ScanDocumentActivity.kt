package com.almizan.mobile.front.soumission



import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.almizan.mobile.databinding.ActivityScanDocumentBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ScanDocumentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanDocumentBinding
    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) startCamera()
        else {
            binding.tvResult.text = "Permission caméra refusée"
            binding.tvResult.visibility = View.VISIBLE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanDocumentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Scanner un document"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        cameraExecutor = Executors.newSingleThreadExecutor()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            requestPermission.launch(Manifest.permission.CAMERA)
        }

        binding.btnCapture.setOnClickListener { takePhoto() }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageCapture
                )
            } catch (e: Exception) {
                Log.e("ScanDoc", "Erreur caméra: ${e.message}")
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val capture = imageCapture ?: return
        binding.btnCapture.isEnabled = false
        binding.progressBar.visibility = View.VISIBLE

        capture.takePicture(ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    analyzeImage(image)
                }
                override fun onError(exc: ImageCaptureException) {
                    binding.progressBar.visibility = View.GONE
                    binding.btnCapture.isEnabled = true
                    binding.tvResult.text = "Erreur de capture: ${exc.message}"
                    binding.tvResult.visibility = View.VISIBLE
                }
            })
    }

    @androidx.camera.core.ExperimentalGetImage
    private fun analyzeImage(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: run {
            imageProxy.close()
            return
        }
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        recognizer.process(image)
            .addOnSuccessListener { result ->
                imageProxy.close()
                val text = result.text
                binding.progressBar.visibility = View.GONE
                binding.btnCapture.isEnabled = true

                if (text.isNotBlank()) {
                    // Analyse des dates de validité (CNAS, CASNOS)
                    val datePattern = Regex("""(\d{2}/\d{2}/\d{4})""")
                    val dates = datePattern.findAll(text).map { it.value }.toList()

                    binding.tvResult.visibility = View.VISIBLE
                    if (dates.isNotEmpty()) {
                        binding.tvResult.text = "✅ Document analysé\nDates détectées : ${dates.joinToString(", ")}"
                        binding.btnConfirm.visibility = View.VISIBLE
                    } else {
                        binding.tvResult.text = "⚠️ Aucune date trouvée. Vérifiez la mise au point."
                    }
                } else {
                    binding.tvResult.text = "⚠️ Aucun texte détecté. Réessayez."
                    binding.tvResult.visibility = View.VISIBLE
                }
            }
            .addOnFailureListener { e ->
                imageProxy.close()
                binding.progressBar.visibility = View.GONE
                binding.btnCapture.isEnabled = true
                binding.tvResult.text = "Erreur OCR: ${e.message}"
                binding.tvResult.visibility = View.VISIBLE
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        recognizer.close()
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}