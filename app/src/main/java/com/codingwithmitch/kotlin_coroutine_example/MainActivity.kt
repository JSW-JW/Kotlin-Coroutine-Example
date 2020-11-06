package com.codingwithmitch.kotlin_coroutine_example

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class MainActivity : AppCompatActivity() {
    private val RESULT_1 = "RESULT #1"
    private val RESULT_2 = "RESULT #2"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
           CoroutineScope(IO).launch { // CoroutineScope extends CoroutineBuilder
               val response1 = fakeApiRequest1()
               executeFunctionOnMainThread { setTextView(response1) }

               val response2 = fakeApiRequest2(response1)
               executeFunctionOnMainThread { setTextView(response2) }
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

    private suspend fun fakeApiRequest1() : String {
        val result1 = getResult1FromApi()
        println("DEBUG : ${result1}")
        return result1
    }

    private suspend fun fakeApiRequest2(result1: String) : String {
        val result1 = getResult2FromApi(result1)
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

    private suspend fun getResult2FromApi(result1: String): String {
        logThread("getResult2FromApi")
        delay(1000)
        return RESULT_2
    }


    private fun logThread(methodName: String) {
        println("DEBUG: ${methodName}: ${Thread.currentThread().name}")
    }
}