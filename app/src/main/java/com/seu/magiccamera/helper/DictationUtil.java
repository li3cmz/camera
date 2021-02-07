package com.seu.magiccamera.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;

import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.seu.magiccamera.AudioGlobalConfig;
import com.seu.magiccamera.R;

import java.util.ArrayList;

public class DictationUtil {

    private static final String DICTATION_APPID = AudioGlobalConfig.IFLY_VOICE_SDK_APP_ID;
    private static SpeechRecognizer speechRecognizer;
    private static String dictationResultStr;
    private static RecognizerListener mRecognizerListener;


    // 开始听写
    public static void startSpeechListener(final Context context, final AudioItemType audioItemType, final ArrayList<Boolean>audio_item_state, final DictationListener listener){
        initConfig(context);
        // 听写监听器
        mRecognizerListener = new RecognizerListener(){
            @Override
            public void onBeginOfSpeech() {
                // TODO Auto-generated method stub
            }

            @Override
            public void onEndOfSpeech() {
                // TODO Auto-generated method stub
            }

            @Override
            public void onError(SpeechError arg0) {
                // TODO Auto-generated method stub
                boolean curState = false;
                int curIndex = -1;
                switch (audioItemType){
                    case NONE:
                        curState = audio_item_state.get(0);
                        curIndex = 0;
                        break;
                    case CUSTOM:
                        curState = audio_item_state.get(1);
                        curIndex = 1;
                        break;
                    case SAYHI:
                        curState = audio_item_state.get(2);
                        break;
                    default:
                        break;
                }
                if (curState==true){
                    startSpeechListener(context, audioItemType,audio_item_state, listener);
                }
            }

            @Override
            public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onResult(RecognizerResult result, boolean isLast) {
                dictationResultStr = GsonUtil.parseJsonVoice(result.getResultString());
                listener.onDictationListener(dictationResultStr);
                boolean curState = false;
                int curIndex = -1;
                switch (audioItemType){
                    case NONE:
                        curState = audio_item_state.get(0);
                        curIndex = 0;
                        break;
                    case CUSTOM:
                        curState = audio_item_state.get(1);
                        curIndex = 1;
                        break;
                    case SAYHI:
                        curState = audio_item_state.get(2);
                        break;
                    default:
                        break;
                }
                if (curIndex == -1 && curState==true){
                    startSpeechListener(context, audioItemType, audio_item_state, listener);
                }
                else if (curIndex == 0){
                    //do nothing
                }
                else if (curIndex == 1){
                    //弹出给用户输入自定义字符的框框
                    startSpeechListener(context, audioItemType, audio_item_state, listener);
                }
            }

            @Override
            public void onVolumeChanged(int arg0, byte[] arg1) {
                // TODO Auto-generated method stub
            }
        };
    }


    private static void initConfig(Context context){
        dictationResultStr = "";
        // 初始化语音识别对象
        SpeechUtility.createUtility(context, SpeechConstant.APPID + "=" + DICTATION_APPID);//APPID？
        speechRecognizer = SpeechRecognizer.createRecognizer(context, null);
        speechRecognizer.setParameter(SpeechConstant.LANGUAGE, "zh_cn"); // 中文
        speechRecognizer.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8"); // 设置编码类型
        speechRecognizer.setParameter(SpeechConstant.ACCENT, "mandarin"); // 普通话
        speechRecognizer.startListening(mRecognizerListener);
    }

    public void showInputDialog(Context context){
        System.out.println("emmmmmmmmmmmmh");
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.ic_edit_audio);
        builder.setTitle("请输入唤醒语音");
        final EditText et=new EditText(context);
        et.setSingleLine();
        et.setHint("請輸入文本");
        builder.setView(et);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog=builder.create();
        dialog.show();
    }

}
