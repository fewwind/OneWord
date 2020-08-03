package com.fewwind.word

import android.app.Application
import jackmego.com.jieba_android.JiebaSegmenter

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        JiebaSegmenter.init(this)
    }
}