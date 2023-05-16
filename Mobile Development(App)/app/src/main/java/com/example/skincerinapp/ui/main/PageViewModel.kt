package com.example.skincerinapp.ui.main

import androidx.lifecycle.*

class PageViewModel : ViewModel() {

    private val _index = MutableLiveData<Int>()
    val text: LiveData<String> = _index.map() {
        "Hello world : $it"
    }

    fun setIndex(index: Int) {
        _index.value = index
    }
}