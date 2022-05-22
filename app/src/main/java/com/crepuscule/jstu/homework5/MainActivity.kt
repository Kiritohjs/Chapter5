package com.crepuscule.jstu.homework5

import okhttp3.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.text.TextUtils
import android.view.KeyEvent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.crepuscule.jstu.homework5.jsonClass.generalClass
import com.crepuscule.jstu.homework5.jsonClass.singleWordClass
import com.google.gson.GsonBuilder
import java.io.IOException
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    private lateinit var translateBtn : Button
    private lateinit var editText: EditText
    private lateinit var resultText: TextView
    private val client: OkHttpClient = OkHttpClient.Builder().build()
    private val gson = GsonBuilder().create()       // 用于解析 json 数据

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        translateBtn = findViewById(R.id.translate)
        resultText = findViewById(R.id.translate_result)
        editText = findViewById(R.id.editText)
        // 等候用户输入内容
        // 用户可以在点击翻译或者按下 enter 时翻译内容
        editText.setOnKeyListener { view, keycode, event -> run {
            if(event.keyCode == KeyEvent.KEYCODE_ENTER) {
                translate()
                true
            } else {
                false
            }}
        }
        translateBtn.setOnClickListener {translate()}

        /*
        * 如果该 lambda 表达式是外层调用时唯一的参数，那么圆括号也可以省略：
        * {} 括起来为 lambda 表达式
        */
    }

    private fun translate() {
        if (TextUtils.isEmpty(editText.text.toString())) {
            return updateResult("请在输入框输入您要查询的单词")
        }
        val url = "https://dict.youdao.com/jsonapi?q=${editText.text}"
        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue (object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                updateResult("查询错误")
            }
            override fun onResponse(call: Call, response: Response) {
                val jsonStr = response.body?.string()
                var result = "未查询到结果"
                if (response.isSuccessful) {
                    val resolve_1 = gson.fromJson(jsonStr, generalClass::class.java)
                    if("blng_sents_part" in resolve_1.meta.dicts) {
                        try {
                            val resolve_2 = gson.fromJson(jsonStr, singleWordClass::class.java)
                            result = resolve_2.blng_sents_part.trs[1].tr
                        } catch (exception:Exception) {}
                    }
                }
                updateResult(result)
            }
        }
        )
    }
    
    // 在显示框中更新结果
    private fun updateResult(text: String) {
        // 非主线程则需提交结果
        if (Looper.getMainLooper() !== Looper.myLooper()) {
            runOnUiThread { updateResult(text) }
        } else {
            resultText.text = text
        }
    }
}