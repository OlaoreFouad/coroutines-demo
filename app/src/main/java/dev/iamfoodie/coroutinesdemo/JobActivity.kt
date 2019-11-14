package dev.iamfoodie.coroutinesdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import dev.iamfoodie.coroutinesdemo.R
import dev.iamfoodie.coroutinesdemo.databinding.ActivityJobBinding

class JobActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJobBinding
    var isExecuting: Boolean = false
    var executingString: String = ""
    private var jobIsComplete: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_job)

        // variable assignments.
        isExecuting = false
        jobIsComplete = false

        binding.jobIsComplete = jobIsComplete
    }
}
