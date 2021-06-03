package com.example.demodualappswl.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PagerViewModel: ViewModel() {

    private val pageSelectionLiveData: MutableLiveData<Int> = MutableLiveData()

    fun getPageSelectionLiveData(): MutableLiveData<Int> {
        return this.pageSelectionLiveData
    }

    fun setPageSelectionLiveData(pageSelection: Int) {
        this.pageSelectionLiveData.value = pageSelection
    }
}