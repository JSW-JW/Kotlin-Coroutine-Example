package com.codingwithmitch.kotlin_coroutine_example

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    private val PROGRESS_MAX = 100
    private val PROGRESS_START = 0
    private val JOB_TIME = 4000 // ms

    private lateinit var job: CompletableJob

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        job_button.setOnClickListener {
            if (!::job.isInitialized) {
                initJob()
            }
            job_progress_bar.startJobOrCancel(job)
        }

    }

    private fun initJob() {
        job_button.text = "Start Job #1"
        job_progress_bar.progress = 0
        updateJobCompleteTextView("")
        job = Job()
        job.invokeOnCompletion {
            it?.message.let {
                var msg = it
                if (it.isNullOrBlank()) {
                    msg = "Unknown cancellation error."
                }
                println("${job} was cancelled. Reason: $msg")
                showToast(msg)
            }
        }
        job_progress_bar.max = PROGRESS_MAX
        job_progress_bar.progress = PROGRESS_START

    }

    private fun ProgressBar.startJobOrCancel(aJob: CompletableJob) {
        if (this.progress > 0) {
            resetJob(aJob)
            Log.d("here", "first")
        } else {
            job_button.text = "Cancel Job #1"
            CoroutineScope(IO + aJob).launch {
                for (i in PROGRESS_START..PROGRESS_MAX) {
                    delay(JOB_TIME / PROGRESS_MAX.toLong())
                    this@startJobOrCancel.progress = i
                }
                updateJobCompleteTextView("Job is complete!")
                /* why job.complete() not make 'isCompleted' true inside coroutine scope? */

                aJob.complete()
                Log.d(TAG, "startJobOrCancel: " + aJob.isCompleted)

            }


        }
    }

    private fun resetJob(job: CompletableJob) {
        if (job.isActive || job.isCompleted) {
            job.cancel(CancellationException("Resetting job"))
/*            Log.d(TAG, "resetJob: isComplete: " + job.isCompleted)
            Log.d(TAG, "resetJob: isComplete: " + job.isActive)
            Log.d(TAG, "resetJob: isComplete: " + job.isCancelled)*/
        }
        initJob()
    }

    private fun updateJobCompleteTextView(text: String) {
        GlobalScope.launch(Main) {
            job_complete_text.text = text
        }
    }

    private fun showToast(text: String?) {
        GlobalScope.launch(Main) {
            Toast.makeText(this@MainActivity, text, Toast.LENGTH_SHORT).show()
        }
    }

}