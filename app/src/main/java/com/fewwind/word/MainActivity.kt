package com.fewwind.word

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import jackmego.com.jieba_android.JiebaSegmenter
import jackmego.com.jieba_android.RequestCallback
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.File
import java.util.*


class MainActivity : AppCompatActivity() {
    var mData = mutableListOf<WordInfo>()
    lateinit var mAdapter: CommonAdapterRV<WordInfo>
    lateinit var cm: ClipboardManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        fab.setOnClickListener { view ->
            var s = ""
            if (!et.text.toString().isNullOrEmpty()) {
                s = et.text.toString()
            } else {
                Toast.makeText(this@MainActivity, "不能为空", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            JiebaSegmenter.getJiebaSegmenterSingleton()
                .getDividedStringAsync(s, object : RequestCallback<ArrayList<String>> {
                    override fun onSuccess(list: ArrayList<String>?) {
                        mData.clear()
                        var map = hashMapOf<String, Int>()
                        list?.forEach {
                            if (!it.isNullOrBlank()) {
                                if (map.containsKey(it)) {
                                    map.put(it, map.get(it)!!.plus(1))
                                } else {
                                    map.put(it, 1)
                                }
                            }
                        }
                        for ((k, v) in map) {
                            mData.add(WordInfo(k, v))
                        }
                        mData.sortWith(Comparator { p0, p1 ->
                            if (p1.count - p0.count == 0) p0.word.compareTo(p1.word) else p1.count - p0.count
                        })
                        mAdapter.notifyDataSetChanged()
                    }

                    override fun onError(errorMsg: String?) {
                    }

                })
        }
        copy.setOnClickListener {
            cm.primaryClip?.getItemAt(0)?.apply {
                if (!toString().isNullOrEmpty()) {
                    var s = text.toString()
                    et.setText(s)
                }
            }
        }
        clear.setOnClickListener {
            et.setText("")
        }
        save.setOnClickListener {
            if (mData.isEmpty()) {
                Toast.makeText(this@MainActivity, "请先点击分词", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            var path = File(externalCacheDir!!.absolutePath + File.separator + "word.txt")
            if (!path.exists()) path.createNewFile()
            else {
                path.delete()
                path.createNewFile()
            }
            FileUtils.wirteFile(mData, path.absolutePath)
        }
        var layoutManager = GridLayoutManager(this, 4)
//        var layoutManager = FlexboxLayoutManager(this, FlexDirection.COLUMN, FlexWrap.WRAP)
        rv.layoutManager = layoutManager
        mAdapter = object : CommonAdapterRV<WordInfo>(this, mData, R.layout.item_word) {
            override fun convert(holder: ViewHolderRV, bean: WordInfo) {
                val name: TextView = holder?.getView(R.id.word)
                val item: TextView = holder?.getView(R.id.count)
                name.text = bean.word
                item.text = bean.count.toString()
                holder.setOnClickListener(R.id.card) {
                    // 创建普通字符型ClipData
                    val mClipData = ClipData.newPlainText("Label", bean.word)
                    // 将ClipData内容放到系统剪贴板里。
                    cm.setPrimaryClip(mClipData)
                    Toast.makeText(this@MainActivity, "复制成功", Toast.LENGTH_LONG).show()
                }
            }
        }
        rv.adapter = mAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
