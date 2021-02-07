package com.seu.magiccamera.helper;

import com.seu.magiccamera.R;

public class AudioItemHelper {
    public static int AudioType2Type(AudioItemType audioType){
        switch (audioType) {
            case NONE:
                return R.drawable.sticker_none;
            case CUSTOM:
                return R.drawable.sticker_custom;
            case SAYHI:
                return R.drawable.sticker_say_hi;
            default:
                return R.drawable.sticker_none;
        }

    }
}
