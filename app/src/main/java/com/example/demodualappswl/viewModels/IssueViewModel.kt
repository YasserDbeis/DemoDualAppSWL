package com.example.demodualappswl.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.demodualappswl.models.Issue

class IssueViewModel : ViewModel() {


    private val issueLiveData: MutableLiveData<MutableList<Issue>> = MutableLiveData()
    val issues: LiveData<MutableList<Issue>> = issueLiveData

    private val issueSelectionLiveData = MutableLiveData<Int>() // observe the image selection change

    fun getIssueSelectionLiveData(): LiveData<Int> {
        return this.issueSelectionLiveData
    }

    fun setIssueSelectionLiveData(selectedIssue: Int) {
        issueSelectionLiveData.value = selectedIssue
    }

    fun setIssues(newIssues : MutableList<Issue>) {
        issueLiveData.value = newIssues
    }
}