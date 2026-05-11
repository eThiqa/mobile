package com.almizan.mobile.front.marches

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.almizan.mobile.data.api.ApiClient
import com.almizan.mobile.data.models.Question
import com.almizan.mobile.utils.Resource
import kotlinx.coroutines.launch

class QuestionsViewModel(app: Application) : AndroidViewModel(app) {

    private val api = ApiClient.create(app)

    private val _questions = MutableLiveData<Resource<List<Question>>>()
    val questions: LiveData<Resource<List<Question>>> = _questions

    private val _postState = MutableLiveData<Resource<Question>>()
    val postState: LiveData<Resource<Question>> = _postState

    fun loadQuestions(marcheId: String) {
        _questions.value = Resource.Loading

        viewModelScope.launch {
            try {
                val response = api.getQuestions(marcheId)

                if (response.isSuccessful) {
                    // getQuestions returns Response<List<Question>>, so we use the body directly
                    val list: List<Question> = response.body()?.data ?: emptyList()
                    _questions.value = Resource.Success(list)
                } else {
                    _questions.value = Resource.Error("Erreur ${response.code()}")
                }
            } catch (e: Exception) {
                _questions.value = Resource.Error("Connexion impossible: ${e.message}")
            }
        }
    }

    fun poserQuestion(marcheId: String, contenu: String) {
        _postState.value = Resource.Loading

        viewModelScope.launch {
            try {
                val response = api.poserQuestion(
                    marcheId,
                    mapOf("question_text" to contenu)
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    // poserQuestion returns Response<ApiResponse<Question>>, so .data is required
                    val question = body?.data

                    if (question != null) {
                        _postState.value = Resource.Success(question)
                    } else {
                        _postState.value = Resource.Error("Réponse invalide")
                    }
                } else {
                    _postState.value = Resource.Error("Erreur ${response.code()}")
                }
            } catch (e: Exception) {
                _postState.value = Resource.Error("Connexion impossible: ${e.message}")
            }
        }
    }
}
