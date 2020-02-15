package com.arlanov.nativeapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.io.PipedReader
import java.io.PipedWriter

class MainActivity : AppCompatActivity() {

    private lateinit var r: PipedReader
    private lateinit var w: PipedWriter

    private lateinit var workerThread: Thread

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
    }
    private fun init(){
        initEditText()
    }
    private fun createPipe(){
        r = PipedReader()
        w = PipedWriter()

        try {
            w.connect(r)
        }catch (io: IOException){
            io.printStackTrace()
        }
    }

    private fun initEditText(){
        createPipe()

        sample_text.addTextChangedListener (
            object : TextWatcher{
                override fun afterTextChanged(p0: Editable?) {
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    try {
                        if (p3 > p2){
                            w.write(p0?.subSequence(p2,p3).toString())
                        }
                    }catch (io: IOException){
                        io.printStackTrace()
                    }
                }
            }
        )

        createThread()
    }

    private fun createThread(){
        workerThread = Thread(TextHandlerTask(r))
        workerThread.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        workerThread.interrupt()
        try {
            r.close()
            w.close()
        }catch (io: IOException){
            io.printStackTrace()
        }
    }

    private inner class TextHandlerTask(private val reader: PipedReader) : Runnable{
        override fun run() {
            while (!Thread.currentThread().isInterrupted){
                try {
                    var i: Int
                    while ((i = reader.read()) != -1){
                        val c = i.toChar()
                        Log.d("currency","$c")
                    }
                }catch (io: IOException){
                    io.printStackTrace()
                }
            }
        }

    }

//    /**
//     * A native method that is implemented by the 'native-lib' native library,
//     * which is packaged with this application.
//     */
//    external fun stringFromJNI(): String
//
//    companion object {
//
//        // Used to load the 'native-lib' library on application startup.
//        init {
//            System.loadLibrary("native-lib")
//        }
//    }
}
