package com.akame.jetpack.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.akame.jetpack.R
import kotlinx.coroutines.*

class ScrollingActivity : AppCompatActivity(),CoroutineScope by MainScope(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
         GlobalScope.launch {

        }



       val resutl =  GlobalScope.async {

        }

        launch() {
            resutl.await()
        }

        async {

        }





    }
}
