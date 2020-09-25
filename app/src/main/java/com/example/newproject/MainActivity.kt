package com.example.newproject

import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.newproject.voice.OperationUtil
import com.example.newproject.voice.WavFileReader
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        main_voice4.setSize(32, 20)
        main_voice4.setOnClickListener {
//            thread {
//                var i = 0;
//                while (true) {
//                    if (i > 100) {
//                        return@thread
//                    }
//                    Thread.sleep(300)
//                    i++;
//                    main_voice4.playVoice(i)
//                }
//            }
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

        val reader = WavFileReader()
        reader.openFile(
            Environment.getExternalStorageDirectory().toString() + "/OBB/test.wav")

        thread {
            val buffer = ByteArray(1024 * 2)
            while (reader.readData(buffer, 0, buffer.size) > 0) {
                Log.e(WavFileReader::class.java.simpleName, "onCreate: 读取中")
                val tempVolume: Double = OperationUtil.getO(buffer)
                if (tempVolume > mPeakVolume) {
                    mPeakVolume = tempVolume
                }
            }
            Log.e(WavFileReader::class.java.simpleName, "mPeakVolume: "+mPeakVolume)
        }

    }

    var mPeakVolume: Double = 0.0

}