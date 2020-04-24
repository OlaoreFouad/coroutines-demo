package dev.iamfoodie.coroutinesdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import dev.iamfoodie.coroutinesdemo.R
import dev.iamfoodie.coroutinesdemo.databinding.ActivityJobBinding
import kotlinx.android.synthetic.main.activity_job.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.coroutines.*

class JobActivity : AppCompatActivity() {

    private val PROGRESS_MAX = 100
    private val PROGRESS_START = 0
    private val JOB_TIME = 4000L

    private lateinit var binding: ActivityJobBinding
    var isExecuting: Boolean = false
    private var jobIsComplete: Boolean? = null
    private lateinit var job: CompletableJob

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_job)

        // variable assignments.
        isExecuting = false
        jobIsComplete = false

        binding.jobIsComplete = jobIsComplete
        binding.startJobButton.setOnClickListener {
            if (!::job.isInitialized) {
                initJob()
            }
            binding.progressBar.startJobOrCancel(job)
        }
    }

    private fun ProgressBar.startJobOrCancel(job: Job) {
        if (this.progress > 0) {
            println("$job is already running!!")
            resetJob()
        } else {
            binding.isExecuting = true
            binding.jobIsComplete = false

            CoroutineScope(Dispatchers.IO + job).launch {
                for (i in PROGRESS_START..PROGRESS_MAX) {
                    delay((JOB_TIME/PROGRESS_MAX))
                    this@startJobOrCancel.progress = i
                }

                updateTextOnMainThread()
            }
        }
    }

    private fun resetJob() {
        if (job.isActive || job.isCompleted) {
            job.cancel(CancellationException("Job is cancelled!"))
            progress_bar.progress = 0
        }
        showToast("Resetting Job...")
        initJob()
    }

    private fun updateTextOnMainThread() {
        GlobalScope.launch(Dispatchers.Main) {
            binding.jobIsComplete = true
            binding.isExecuting = false
        }
    }

    private fun initJob() {
        binding.isExecuting = false
        binding.jobIsComplete = false
        job = Job()
        job.invokeOnCompletion {
            it?.message.let {
                var msg = it
                if (msg.isNullOrBlank()) {
                    msg = "Unknown cancellation exception"
                }
                println("$job was cancelled for reason: $msg")
                showToast(msg)
                binding.progressBar.progress = PROGRESS_START
                binding.progressBar.max = PROGRESS_MAX
            }
        }
    }

    private fun showToast(message: String) {
        GlobalScope.launch(Dispatchers.Main) {
            Toast.makeText(this@JobActivity, message, Toast.LENGTH_SHORT).show()
        }
    }


}
