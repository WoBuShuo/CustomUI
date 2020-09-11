package com.example.newproject

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        main_voice4.setSize(32, 20)
        main_voice4.setOnClickListener {
            thread {
                var i = 0;
                while (true) {
                    if (i > 100) {
                        return@thread
                    }
                    Thread.sleep(300)
                    i++;
                    main_voice4.playVoice(i)
                }
            }
        }
        main_voice4.setOnLongClickListener {
            Log.e("----------", "setOnLongClickListener: ")
            false
        }


        val titleList = listOf<String>("第一", "第二", "第三")
        main_tab.setTitleList(titleList);//添加tab标题集合
        main_tab.setDefaultSelectPosition(0);
//        main_tab.addOnScaleTabSelectedListener(new ScaleTabLayout . OnScaleTabSelectedListener () {
//            @Override
//            public void onScaleTabSelected(int lastPosition, int currentPosition) {
//                //这个回调可以和viewpage联动
//                viewPager.setCurrentItem(currentPosition, true);
//            }
//        });

    }


}