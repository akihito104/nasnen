package com.freshdigitable.upnpsample

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.freshdigitable.upnpsample.model.RecordScheduleResult

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
        fun logd(msg: String, res: RecordScheduleResult<*>) {
            Log.d(TAG, "$msg: num>${res.numberReturned}, total>${res.totalMatches}")
            res.result.forEach {
                Log.d(TAG, "$it")
            }
        }
    }
}
