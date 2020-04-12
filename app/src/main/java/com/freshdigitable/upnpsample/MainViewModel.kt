package com.freshdigitable.upnpsample

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.freshdigitable.upnpsample.model.RecordScheduleItem
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val repository: RecordScheduleRepository
) : ViewModel() {
    val allRecordScheduleItems: LiveData<List<RecordScheduleItem>> by lazy {
        repository.getAllRecordScheduleSource()
    }
}
