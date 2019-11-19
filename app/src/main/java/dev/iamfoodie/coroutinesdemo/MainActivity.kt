package dev.iamfoodie.coroutinesdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

const val RESULT_ONE = "Result One"
const val RESULT_TWO = "Result Two"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fetch_button.setOnClickListener {
            val ioScope = CoroutineScope(Dispatchers.IO)
            ioScope.launch {
//                fakeApiCall()
                fakeApiRequest()
            }
        }

        job_button.setOnClickListener { startActivity(Intent(this@MainActivity, JobActivity::class.java)) }
        parallel_button.setOnClickListener { startActivity(Intent(this@MainActivity, ParallelActivity::class.java)) }

    }

    private suspend fun fakeApiRequest() {
        val job = withTimeoutOrNull(2900L) {
            var result_one = getFirstResult()
            setTextOnMainThread(result_one)

            var result_two = getSecondResult()
            setTextOnMainThread(result_two)
        }

        if (job == null) {
            setTextOnMainThread("Cancelling job... Timeout exceedeed!!")
        }

    }

    private suspend fun fakeApiCall() {
        var result_one = getFirstResult()
        setTextOnMainThread(result_one)
    }

    private suspend fun setTextOnMainThread(text: String) {
        withContext(Dispatchers.Main) {
            setText(text)
        }
    }

    private fun setText(text: String) {
        val newText = "${ result.text }\n$text"
        result.text = newText
    }

    private suspend fun getFirstResult(): String {
        logThread("getFirstResult")
        delay(1000L)
        return RESULT_ONE
    }

    private suspend fun getSecondResult(): String {
        logThread("getSecondResult")
        delay(1000L)
        return RESULT_TWO
    }

    private fun logThread(methodName: String) {
        println("debug: from $methodName: thread: ${ Thread.currentThread().name }")
    }

}
