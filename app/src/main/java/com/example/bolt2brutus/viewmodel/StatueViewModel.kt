package com.example.bolt2brutus.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.bolt2brutus.data.repository.StatueRepository
import com.example.bolt2brutus.model.Statue
import kotlinx.coroutines.flow.catch

/** Presenter for the statue map. */
class StatueViewModel(
    statueRepository: StatueRepository = StatueRepository(),
) : ViewModel() {

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    val statues: LiveData<List<Statue>> = statueRepository.observeAll()
        .catch { _message.value = it.message }
        .asLiveData()

    fun messageShown() {
        _message.value = null
    }
}
