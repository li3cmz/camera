package com.seu.magiccamera.helper;

import com.seu.magiccamera.R;

public class MSTHelper {
    public static int MSTType2Type(MSTType mstType){
        switch (mstType) {
            case NONE:
                return R.drawable.ic_st_none;
            case ST1:
                return R.drawable.ic_st1;
            case ST2:
                return R.drawable.ic_st2;
            case ST3:
                return R.drawable.ic_st3;
            case ST4:
                return R.drawable.ic_st4;
            case ST5:
                return R.drawable.ic_st5;
            case ST6:
                return R.drawable.ic_st6;
            case ST7:
                return R.drawable.ic_st7;
            default:
                return R.drawable.ic_st_none;
        }

    }
}
