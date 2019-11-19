package dev.iamfoodie.coroutinesdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_parallel.*
import kotlinx.coroutines.*
import kotlinx.coroutines.CoroutineScope.*;
import kotlin.system.measureTimeMillis

class ParallelActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parallel)

        run_button.setOnClickListener {
//            firstJob()
            asyncAwait()
        }

    }

    private fun asyncAwait() {
        CoroutineScope(Dispatchers.IO).launch {
            val result: Deferred<String> = CoroutineScope(Dispatchers.IO).async {
                Log.d("MainActivity","debug: launching async job 1")
                getFirstResult()
            }

            val result2: Deferred<String> = CoroutineScope(Dispatchers.IO).async {
                Log.d("MainActivity", "debug: launching async job 2")
                getSecondResult()
            }

            setTextOnMainThread("Got ${ result.await() }")
            setTextOnMainThread("Got ${ result2.await() }")
        }
    }

    private fun firstJob() {
        // creating two long variables to denote the time taken to execute the coroutines.
        var time1: Long = 0
        var time2: Long = 0

        // launching a parent couroutine in the background thread
        val parentJob = CoroutineScope(Dispatchers.IO).launch {
            // second job that is supposed to take 1700ms.
            val job2 = launch {
                // the measureTimeMillis is supposed to measure the entire time it took to execute the function in ms. The value is assigned to time2
                // variable.
                time2 = measureTimeMillis {
                    Log.d("debug", "debug: launching job2 in thread: ${ Thread.currentThread().name }")
                    val result2 = getSecondResult()
                    setTextOnMainThread(result2)
                }
                Log.d("debug", "debug: completed job2 in $time2 ms")
            }

            // calling job.join() instructs the enclosing coroutine to wait until this job finishes until it continues exceution. This forces it to
            // sequential.
            job2.join()

            val job = launch {
                time1 = measureTimeMillis {
                    Log.d("debug", "debug: launching job1 in thread: ${ Thread.currentThread().name }")
                    val result1 = getFirstResult()
                    setTextOnMainThread(result1)
                }
                Log.d("debug", "debug: completed job in $time1 ms")
            }
        }
        // when the parent coroutine completes, we set the text of a textview to the sum of the time variants. The internal jobs would have to get
        // completed before the parent job completes. Roughly 2700ms, since we are suspending exec by calling .join()
        parentJob.invokeOnCompletion {
            setText("Jobs completed in ${ time1 + time2 }")
        }
    }

    private fun setText(text: String) {
        val newText = "${ run_details.text }\n$text"
        run_details.text = newText
    }

    private suspend fun setTextOnMainThread(text: String) {
        withContext(Dispatchers.Main) {
            setText(text)
        }
    }

    private suspend fun getFirstResult(append: String = ""): String {
        delay(1000L)
        return "Result #1$append"
    }

    private suspend fun getSecondResult(): String {
        delay(1700L)
        return "Result #2"
    }

}
