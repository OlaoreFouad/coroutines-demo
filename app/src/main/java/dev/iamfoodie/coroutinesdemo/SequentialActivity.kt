package dev.iamfoodie.coroutinesdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_sequential.*
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

class SequentialActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sequential)

        run_button_seq.setOnClickListener {
            setText("Clicked!")
            fakeApiCall()
        }

    }

    private fun fakeApiCall() {
        CoroutineScope(Dispatchers.IO).launch {
            val elapsedTime = measureTimeMillis {

                val result1 = CoroutineScope(Dispatchers.IO).async {
                    println("getting the first data")
                    getFirstResult()
                }.await()

                val result2 = CoroutineScope(Dispatchers.IO).async {
                    println("getting the second data")
                    getSecondResult(result1)
                }.await()

                setTextOnMainThread(result2)

            }

            println("debug: Completed the task in: $elapsedTime ms")

        }
    }

    private suspend fun getFirstResult(): String {
        println("debug: getting the first result")
        delay(1000L)
        return "Result #1"
    }

    private suspend fun getSecondResult(firstResult: String): String {
        println("debug: getting the second result")
        delay(1700L)
        if (firstResult == "Result #1") {
            return "Result #2: $firstResult"
        }
        return "First result was incorrect"
    }

    private fun setText(input: String) {
        val newText = "${run_seq_text.text}\n$input"
        run_seq_text.text = newText
    }

    private suspend fun setTextOnMainThread(text: String) {
        withContext(Dispatchers.Main) {
            setText(text)
        }
    }

}
