package com.seu.magiccamera.helper;

import com.seu.magiccamera.R;

public class StickerItemHelper {
    public static int StickerType2Type(StickerItemType stickerType){
        switch (stickerType) {
            case NONE:
                return R.drawable.sticker_none;
            case GLASSES:
                return R.drawable.nsticker_glasses;
            case CATEAR:
                return R.drawable.nsticker_catear;
            case RABIT:
                return R.drawable.rabit;
            case HAT:
                return R.drawable.hat;
            case BI:
                return R.drawable.nsticker_bizi;
            case SAIHONG:
                return R.drawable.nsticker_saihong;
            case BAHENG:
                return R.drawable.bahen;
            case MAOZI:
                return R.drawable.baozi;
            case LUJIAO:
                return R.drawable.lujiao;
            case FACE:
                return R.drawable.face;
            case RING:
                return R.drawable.ring;
            case ERDUO:
                return R.drawable.erduo;
            case ERDUO1:
                return R.drawable.erduo1;
            case BOZI:
                return R.drawable.bozi;
            case XIN:
                return R.drawable.xin;
            default:
                return R.drawable.sticker_none;
        }
    }
}
