package com.example.newproject.voice;

import android.util.Log;

import com.example.annotation.Post;


public class OperationUtil {


    @Post(url = "/v1/user_page")
    public void getUserPage() {


    }

    @Post(url = "/v1/banner")
    public void getBanner() {


    }


    public static double getO(byte[] buffer) {

        double sumVolume = 0.0;
        double avgVolume = 0.0;
        double volume = 0.0;
        for (int i = 0; i < buffer.length; i += 2) {
            int v1 = buffer[i] & 0xFF;
            int v2 = buffer[i + 1];
            int temp = v1 | (v2 << 8);  // 小端
            if (temp >= 0x8000) {
                temp = 0xffff - temp;
            }
            Log.e(WavFileReader.class.getSimpleName(), "byte:" + temp);
            sumVolume += Math.abs(temp);
        }
        avgVolume = sumVolume / buffer.length / 2;
        volume = Math.log10(1 + avgVolume) * 10;
        return volume;

    }


}
