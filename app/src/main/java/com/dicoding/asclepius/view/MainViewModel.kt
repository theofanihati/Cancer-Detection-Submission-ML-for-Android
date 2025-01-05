package com.dicoding.asclepius.view

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.asclepius.helper.ImageClassifierHelper
import org.tensorflow.lite.task.vision.classifier.Classifications

class MainViewModel : ViewModel() {

    private val _ImageUri = MutableLiveData<Uri?>()
    val currentImageUri: LiveData<Uri?> = _ImageUri

    private val _classificationResult = MutableLiveData<String>()
    val classificationResult: LiveData<String> = _classificationResult

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: MutableLiveData<String?> get() = _errorMessage

    fun setCurrentImageUri(uri: Uri) {
        _ImageUri.value = uri
    }

    fun analyzeImage(context: Context, uri: Uri) {
        val classifierHelper = ImageClassifierHelper(
            context = context,
            classifierListener = object : ImageClassifierHelper.ClassifierListener {
                override fun onError(error: String) {
                    _errorMessage.value = error
                }

                override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                    val resultText = results?.joinToString("\n") { classification ->
                        classification.categories.joinToString("\n") { category ->
                            "${category.label}: ${"%.2f".format(category.score * 100)}%"
                        }
                    } ?: "Tidak ada hasil klasifikasi"
                    _classificationResult.postValue(resultText)
                }
            }
        )
        classifierHelper.classifyStaticImage(uri)
    }
}
