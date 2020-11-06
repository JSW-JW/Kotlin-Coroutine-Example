package com.codingwithmitch.kotlin_coroutine_example

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class MainActivity : AppCompatActivity() {
    private val RESULT_1 = "RESULT #1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
           CoroutineScope(IO).launch { // CoroutineScope extends CoroutineBuilder
               val response = fakeApiRequest()
               executeFunctionOnMainThread { setTextView(response) }
           }
        }
    }

    private suspend fun executeFunctionOnMainThread(myFunction : () -> Unit) {
        withContext(Main) {
            myFunction()
        }
    }

    private fun setTextView(newText: String) {
        text.text = newText
    }

    private suspend fun fakeApiRequest() : String {
        val result1 = getResult1FromApi()
        println("DEBUG : ${result1}")
        return result1
    }

    private suspend fun getResult1FromApi(): String {
        logThread("getResult1FromApi")
        delay(1000)
        /* This is very different from 'Thread.sleep(1000)'. It makes the Thread itself sleep, so that it will make all coroutines in the thread also sleep.
        But, delay() makes only the coroutine sleep. */
        return RESULT_1
    }


    private fun logThread(methodName: String) {
        println("DEBUG: ${methodName}: ${Thread.currentThread().name}")
    }
}