package com.seu.magiccamera.helper;

//科大讯飞语音解析结果返回监听接口
public interface DictationListener {
    public abstract void onDictationListener(String dictationResultStr);
}
