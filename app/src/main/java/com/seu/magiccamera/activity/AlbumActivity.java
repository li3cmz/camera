package com.seu.magiccamera.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.media.FaceDetector;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.seu.magiccamera.R;
import com.seu.magiccamera.adapter.FilterAdapter;
import com.seu.magiccamera.adapter.MenuAdapter1;
import com.seu.magiccamera.adapter.MenuImgAdapter;
import com.seu.magiccamera.adapter.StickerAdapter;
import com.seu.magiccamera.helper.ClickUtils;
import com.seu.magiccamera.helper.ConbindSaveImage;
import com.seu.magiccamera.helper.HttpAssist;
import com.seu.magiccamera.helper.MSTType;
import com.seu.magiccamera.helper.MenuBean;
import com.seu.magiccamera.helper.ShareToolUtil;
import com.seu.magiccamera.helper.Sticker;
import com.seu.magiccamera.helper.StickerItemType;
import com.seu.magicfilter.MagicEngine;
import com.seu.magicfilter.filter.helper.MagicFilterType;
import com.seu.magicfilter.utils.MagicParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by why8222 on 2016/3/18.
 */
public class AlbumActivity extends Activity{
    static final String tag = "li3";
    private Bitmap bitmap;
    Bitmap sharebmp;


    private ImageView btn_sticker;
    private ImageView btn_return;
    private ImageView btn_filter;
    private ImageView  btn_download;

    private boolean filter_closedOrNot;
    private boolean sticker_closedOrNot;

    private LinearLayout mFilterLayout;
    private RecyclerView mFilterListView;
    private LinearLayout mStickerLayout;
    private RecyclerView mAllStickerListView;

    private FilterAdapter mAdapter;
    private MagicEngine magicEngine;

    FaceDetector faceDetector = null;
    FaceDetector.Face[] face;
    final int N_MAX = 2;
    boolean hasDetected = false;
    private ImageView image2Photo;
    Bitmap srcImg = null;
    Bitmap srcFace = null;

    // 人脸识别得到的几个重要的全局坐标数据
    ArrayList<Integer> eyeLeftX = new ArrayList<>();
    ArrayList<Integer> eyeLeftY = new ArrayList<>();
    ArrayList<Integer> eyeRightX = new ArrayList<>();
    ArrayList<Integer> eyeRightY = new ArrayList<>();
    ArrayList<Integer> rectX1 = new ArrayList<>();
    ArrayList<Integer> rectY1 = new ArrayList<>();
    ArrayList<Integer> rectX2 = new ArrayList<>();
    ArrayList<Integer> rectY2 = new ArrayList<>();
    ArrayList<Float> eyesDistance = new ArrayList<>();
    ArrayList<Float> dis = new ArrayList<>();


    int  nFace = 0;// 实际检测到的人脸数

    private ArrayList<ImageView> glassesStickersType = new ArrayList<>();
    private ArrayList<ImageView> catearsStickersType = new ArrayList<>();
    private ArrayList<ImageView> biziStickersType = new ArrayList<>();
    private ArrayList<ImageView> ferStcikerType = new ArrayList<>();
    private ArrayList<ImageView> saihongStickersType = new ArrayList<>();
    Canvas stickerCanvas;
    private int picWidth;
    private int picHeight;


    private ArrayList<Boolean> audio_item_state = new ArrayList<>();

    private boolean NONE_sticker_state = true;
    private boolean GLASSES_sticker_state = false;
    private boolean CATEAR_sticker_state = false;
    private boolean BI_sticker_state = false;
    private boolean SAIHONG_sticker_state = false;

    private boolean NONE_audio_state = true;
    private boolean CUSTOM_audio_state = false;
    private boolean SAYHI_audio_state = false;
    private boolean JIAYOU_audio_state = false;
    private boolean QIEZI_audio_state = false;
    private boolean BANG_audio_state = false;

    private boolean hasCroped = false;
    private int faceCount = 0;
    //private ImageView  curSticker;

    private ArrayList<Bitmap> bitmaps = new ArrayList<>();
    private Canvas canvas;
    private ArrayList<Sticker>stickers = new ArrayList<>();

    private String picturePath = "";


    // private GLSurfaceView glSurfaceView;
    private ArrayList<MenuBean> mFerData = new ArrayList<>();
    private ArrayList<MSTType> mStData = new ArrayList<>();
    private MenuAdapter1 ferAdapter;
    private MenuImgAdapter stAdapter;


    private File filePic;
    private static final int SHOW_RESPONSE = 11;
    private static String picPathh;
    String ferresponse;
    Bitmap stres_img;
    private int recordHeight;


    private final StickerItemType[] sticker_List = new StickerItemType[]{
            StickerItemType.NONE, StickerItemType.GLASSES, StickerItemType.CATEAR, StickerItemType.BI, StickerItemType.SAIHONG, StickerItemType.MAOZI, StickerItemType.BAHENG, StickerItemType.LUJIAO, StickerItemType.FACE, StickerItemType.RING, StickerItemType.ERDUO, StickerItemType.ERDUO1,
            StickerItemType.BOZI, StickerItemType.XIN, StickerItemType.NONE, StickerItemType.NONE, StickerItemType.NONE, StickerItemType.NONE, StickerItemType.NONE, StickerItemType.NONE, StickerItemType.NONE, StickerItemType.NONE, StickerItemType.NONE, StickerItemType.NONE, StickerItemType.NONE,
            StickerItemType.NONE, StickerItemType.NONE, StickerItemType.NONE, StickerItemType.NONE, StickerItemType.NONE, StickerItemType.NONE, StickerItemType.NONE, StickerItemType.NONE, StickerItemType.NONE, StickerItemType.NONE, StickerItemType.NONE, StickerItemType.NONE,
            StickerItemType.NONE, StickerItemType.NONE, StickerItemType.NONE, StickerItemType.NONE, StickerItemType.NONE,
    };

    private final MagicFilterType[] types = new MagicFilterType[]{
            MagicFilterType.NONE, //原图
            MagicFilterType.FAIRYTALE, //童话
            //MagicFilterType.SUNRISE, //日出
            MagicFilterType.SUNSET, //日落
            MagicFilterType.WHITECAT, //白猫
            MagicFilterType.BLACKCAT, //黑猫
            //MagicFilterType.SKINWHITEN, //美白
            MagicFilterType.HEALTHY, //健康
            //MagicFilterType.SWEETS, //甜品
            MagicFilterType.ROMANCE, //浪漫
            MagicFilterType.SAKURA, //樱花
            //MagicFilterType.WARM, //温暖
            MagicFilterType.ANTIQUE, //复古
            //MagicFilterType.NOSTALGIA, //怀旧
            //MagicFilterType.CALM, //平静
            MagicFilterType.LATTE, //拿铁
            MagicFilterType.TENDER, //温柔
            MagicFilterType.COOL, //冰冷
            MagicFilterType.EMERALD, //祖母绿
            MagicFilterType.EVERGREEN, //常青
            //MagicFilterType.CRAYON, //蜡笔
            //MagicFilterType.SKETCH, //素描
            MagicFilterType.AMARO, //Amaro
            //MagicFilterType.BRANNAN, //Brannan
            MagicFilterType.BROOKLYN, //Brooklyn
            //MagicFilterType.EARLYBIRD, //EarlyBird
            MagicFilterType.FREUD, //Freud
            MagicFilterType.HEFE, //Hefe
            MagicFilterType.HUDSON, //Hudson
            MagicFilterType.INKWELL, //Inkwell
            MagicFilterType.KEVIN, //Kevin
            //MagicFilterType.LOMO, //Lomo
            MagicFilterType.N1977, //1977
            MagicFilterType.NASHVILLE, //Nashville
            MagicFilterType.PIXAR, //Pixar
            MagicFilterType.RISE, //Rise
            MagicFilterType.SIERRA, //Sierra
            //MagicFilterType.SUTRO, //Sutro
            //MagicFilterType.TOASTER2, //Toaster2
            MagicFilterType.VALENCIA, //Valencia
            MagicFilterType.WALDEN, //Walden
            MagicFilterType.XPROII //Xproll
    };

    Thread checkFaceThread = new Thread(){ // 用来检测人脸

        @Override
        public void run() {
            // TODO Auto-generated method stub
            Bitmap faceBitmap = detectFace();
          //  image2Photo.setImageBitmap(faceBitmap);// 这张bitmap单独传?????????????
            image2Photo.setImageBitmap(faceBitmap);
            hasDetected = true;
            mainHandler.sendEmptyMessage(2);
            Message m = new Message();
            m.what = 0;
            m.obj = faceBitmap;
            mainHandler.sendMessage(m);
        }
    };

    Handler mainHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            //super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    //Bitmap b = (Bitmap) msg.obj;
                    //image2Photo.setImageBitmap(bitmap);
                    for (int i=0;i < nFace;i++){
                        if (eyesDistance.get(i) != 0){
                            setStickers(i);
                        }
                    }

                    System.out.println("检测完毕");
                    break;
                case 1:
                    //showProcessBar();
                    break;
                case 2:
                    //progressBar.setVisibility(View.GONE);
                    //detectFaceBtn.setClickable(false);
                    break;
                case SHOW_RESPONSE:
                    String response = (String) msg.obj;
                    Log.d("reponse",response);
                    // 在这里进行UI操作，将结果显示到界面上
                    //text = (TextView)findViewById(R.id.text);
                    //text.setText(response);
                    if (ferresponse.equals("0")){
                        //happiness
                        setFerSticker(0);
                    }
                    else if (ferresponse.equals("1")){
                        //calm
                        setFerSticker(1);
                    }
                    else if (ferresponse.equals("2")){
                        //angry
                        setFerSticker(2);
                    }
                    break;
                default:
                    break;
            }
        }

    };

    public void setFerSticker(int i){
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float screenSizeWidth = dm.widthPixels;
        float screenSizeHeight = dm.heightPixels;
        if (i == 0){
            //开心贴纸
            ferStcikerType.get(0).setVisibility(View.VISIBLE);

            int curx = 0;
            int cury = 0;

            TranslateAnimation anim = new TranslateAnimation(Animation.ABSOLUTE, curx,Animation.ABSOLUTE,screenSizeWidth/2-300,Animation.ABSOLUTE,cury,Animation.ABSOLUTE,recordHeight/2-300);
            anim.setFillAfter(true);
            ferStcikerType.get(0).setBackgroundResource(R.drawable.gif4);
            ferStcikerType.get(0).startAnimation(anim);


            final AnimationDrawable curStickerAnimation = (AnimationDrawable) ferStcikerType.get(0).getBackground();
            curStickerAnimation.setOneShot(false);
            curStickerAnimation.start();

        }
        else if(i == 1){
            //冷静贴纸
            ferStcikerType.get(2).setVisibility(View.VISIBLE);

            int curx = 0;
            int cury = 0;

            TranslateAnimation anim = new TranslateAnimation(Animation.ABSOLUTE, curx,Animation.ABSOLUTE,screenSizeWidth/2-300,Animation.ABSOLUTE,cury,Animation.ABSOLUTE,recordHeight/2-300);
            anim.setFillAfter(true);
            ferStcikerType.get(1).setBackgroundResource(R.drawable.gif5);
            ferStcikerType.get(1).startAnimation(anim);
            final AnimationDrawable curStickerAnimation = (AnimationDrawable) ferStcikerType.get(2).getBackground();
            curStickerAnimation.setOneShot(false);
            curStickerAnimation.start();
        }
        else if (i==2){
            //生气贴纸
            ferStcikerType.get(2).setVisibility(View.VISIBLE);
            int curx = 0;
            int cury = 0;

            TranslateAnimation anim = new TranslateAnimation(Animation.ABSOLUTE, curx,Animation.ABSOLUTE,screenSizeWidth/2-300,Animation.ABSOLUTE,cury,Animation.ABSOLUTE,recordHeight/2-300);
            anim.setFillAfter(true);
            ferStcikerType.get(2).setBackgroundResource(R.drawable.gif6);
            ferStcikerType.get(2).startAnimation(anim);
            final AnimationDrawable curStickerAnimation = (AnimationDrawable) ferStcikerType.get(2).getBackground();
            curStickerAnimation.setOneShot(false);
            curStickerAnimation.start();
        }
    }

    public void setStickers(int i){
        if (eyesDistance.get(i)!=0){
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            float screenSizeWidth = dm.widthPixels;
            float screenSizeHeight = dm.heightPixels;

            if (GLASSES_sticker_state == true){
                if (faceCount == 0){
                    for (int k=0; k < catearsStickersType.size();k++){
                        catearsStickersType.get(k).clearAnimation();
                        catearsStickersType.get(k).setVisibility(View.INVISIBLE);
                        biziStickersType.get(k).clearAnimation();
                        biziStickersType.get(k).setVisibility(View.INVISIBLE);
                        saihongStickersType.get(k).clearAnimation();
                        saihongStickersType.get(k).setVisibility(View.INVISIBLE);
                    }
                    if (stickers.size()!=0){
                        stickers.clear();
                    }
                }
                Resources res = getResources();
                Bitmap bitmap = BitmapFactory.decodeResource(res,R.drawable.glasses_00);

                int rawGlassesWidth = bitmap.getWidth();
                int rawGlassesHeight = bitmap.getHeight();//转为pixel

                float rawPicToScreenLeftX = (screenSizeWidth/picWidth)*eyeLeftX.get(i);
                float rawPicToScreenRightX = (screenSizeWidth/picWidth)*eyeRightX.get(i);
                float rawPicToScreenLeftY = 0;
                float rawPicToScreenRightY = 0;

                float realPicHeight = (screenSizeWidth/picWidth)*picHeight;
                rawPicToScreenLeftY = (realPicHeight/picHeight)*eyeLeftY.get(i);
                rawPicToScreenRightY = (realPicHeight/picHeight)*eyeRightY.get(i);

                // 有空再来设置旋转角
                // float K1 = (rawPicToScreenRightY - rawPicToScreenLeftY)/(rawPicToScreenRightX - rawPicToScreenLeftX);
                // float K2 = 0;
                // double angle =  Math.atan(K2-K1);

                float rawEyesDis = eyesDistance.get(i);
                eyesDistance.set(i,( rawPicToScreenRightX - rawPicToScreenLeftX));
                // 求缩放比例
                float rate = (15f*eyesDistance.get(i))/rawGlassesWidth;

               // glassesStickersType.get(faceCount).setImageBitmap(bitmap);
                int[] location = new int[2];
                glassesStickersType.get(faceCount).getLocationOnScreen(location);
                int curx = location[0];
                int cury = location[1];

                TranslateAnimation anim = new TranslateAnimation(Animation.ABSOLUTE, curx,Animation.ABSOLUTE,(rawPicToScreenLeftX+rawPicToScreenRightX)/2-90,Animation.ABSOLUTE,cury,Animation.ABSOLUTE,(rawPicToScreenLeftY+rawPicToScreenRightY)/2-100);
                // anim.setStartOffset(1000);
                // anim.setDuration(2000);
                anim.setFillAfter(true);
                glassesStickersType.get(faceCount).setBackgroundResource(R.drawable.gif1);
                glassesStickersType.get(faceCount).startAnimation(anim);
                glassesStickersType.get(faceCount).setScaleX(rate);
                glassesStickersType.get(faceCount).setScaleY(rate*0.6f);
                //curSticker.setRotation((float)angle);
                glassesStickersType.get(faceCount).setVisibility(View.VISIBLE);
                final AnimationDrawable curStickerAnimation = (AnimationDrawable) glassesStickersType.get(faceCount).getBackground();
                curStickerAnimation.setOneShot(false);
                curStickerAnimation.start();

                float rawRate = 0.9f*rawEyesDis/rawGlassesWidth;
                int bitmapWidth = ((int)(bitmap.getWidth()*rawRate*1.8));
                int bitmapHeight = ((int)(bitmap.getHeight()*rawRate*1.2));
                Rect dst = new Rect();
                dst.left = eyeLeftX.get(i)-(int)(rawEyesDis*0.55);
                dst.top = eyeLeftY.get(i)-(int)(rawEyesDis*0.55);
                dst.right = eyeLeftX.get(i) + bitmapWidth;
                dst.bottom = eyeLeftY.get(i) + bitmapHeight;
                Sticker tmp = new Sticker();
                tmp.bitmap = bitmap;
                tmp.left = dst.left;
                tmp.top = dst.top;
                tmp.right = dst.right;
                tmp.bottom = dst.bottom;
                tmp.stickerType = 1;
                stickers.add(tmp);
               // canvas.drawBitmap(bitmap, null, dst,null);

                faceCount++;
            }
            else if (CATEAR_sticker_state == true){
                if (faceCount == 0){
                    for (int k=0;k < glassesStickersType.size();k++){
                        glassesStickersType.get(k).clearAnimation();
                        glassesStickersType.get(k).setVisibility(View.INVISIBLE);
                        biziStickersType.get(k).clearAnimation();
                        biziStickersType.get(k).setVisibility(View.INVISIBLE);
                        saihongStickersType.get(k).clearAnimation();
                        saihongStickersType.get(k).setVisibility(View.INVISIBLE);
                    }
                    if (stickers.size()!=0){
                        stickers.clear();
                    }
                }
                Resources res = getResources();
                Bitmap bitmap = BitmapFactory.decodeResource(res,R.drawable.catear);
                int rawCatEarWidth = bitmap.getWidth();
                int rawCatEarHeight = bitmap.getHeight();
                float recX1ToScreen = (screenSizeWidth/picWidth)*rectX1.get(i);
                float recX2ToScreen = (screenSizeWidth/picWidth)*rectX2.get(i);
                float realPicHeight = (screenSizeWidth/picWidth)*picHeight;
                float rectY1ToScreen = (realPicHeight/picHeight)*rectY1.get(i);
                float rawPicToScreenLeftX = (screenSizeWidth/picWidth)*eyeLeftX.get(i);
                float rawPicToScreenRightX = (screenSizeWidth/picWidth)*eyeRightX.get(i);

                float rawEyesDis = eyesDistance.get(i);
                eyesDistance.set(i,( rawPicToScreenRightX - rawPicToScreenLeftX));
                // 求缩放比例
                float rate = (2.5f*eyesDistance.get(i))/rawCatEarWidth;

                // catearsStickersType.get(faceCount).setImageBitmap(bitmap);
                int[] location = new int[2];
                catearsStickersType.get(faceCount).getLocationOnScreen(location);
                int curx = location[0];
                int cury = location[1];

                TranslateAnimation anim = new TranslateAnimation(Animation.ABSOLUTE, curx,Animation.ABSOLUTE,(recX1ToScreen+recX2ToScreen)/2-110,Animation.ABSOLUTE,cury,Animation.ABSOLUTE,rectY1ToScreen-120);
                // anim.setStartOffset(1000);
                // anim.setDuration(2000);
                anim.setFillAfter(true);
                catearsStickersType.get(faceCount).setBackgroundResource(R.drawable.gif2);
                catearsStickersType.get(faceCount).startAnimation(anim);

                catearsStickersType.get(faceCount).setScaleX(rate);
                catearsStickersType.get(faceCount).setScaleY(rate);
                //curSticker.setRotation((float)angle);
                catearsStickersType.get(faceCount).setVisibility(View.VISIBLE);
                final AnimationDrawable curStickerAnimation = (AnimationDrawable) catearsStickersType.get(faceCount).getBackground();
                curStickerAnimation.setOneShot(false);
                curStickerAnimation.start();


                float rawRate = 0.009f*rawEyesDis/rawCatEarWidth;
               // int bitmapWidth = ((int)(bitmap.getWidth()*rawRate*1.6));
               // int bitmapHeight = ((int)(bitmap.getHeight()*rawRate*0.001));
                Rect dst = new Rect();
                dst.left = rectX1.get(i)-(int)(rawEyesDis*0.3);
                dst.top = rectY1.get(i)-(int)(rawEyesDis*1.7);
                dst.right = rectX2.get(i)+(int)(rawRate*10);
                dst.bottom = rectY2.get(i)-(int)(rawRate*0.01);
                Sticker tmp = new Sticker();
                tmp.bitmap = bitmap;
                tmp.left = dst.left;
                tmp.top = dst.top;
                tmp.right = dst.right;
                tmp.bottom = dst.bottom;
                tmp.stickerType = 2;
               // canvas.drawBitmap(bitmap, null, dst,null);
                stickers.add(tmp);
                faceCount ++;
            }
            else if (BI_sticker_state == true){
                if (faceCount == 0){
                    for (int k=0;k < glassesStickersType.size();k++){
                        glassesStickersType.get(k).clearAnimation();
                        glassesStickersType.get(k).setVisibility(View.INVISIBLE);
                        catearsStickersType.get(k).clearAnimation();
                        catearsStickersType.get(k).setVisibility(View.INVISIBLE);
                        saihongStickersType.get(k).clearAnimation();
                        saihongStickersType.get(k).setVisibility(View.INVISIBLE);
                    }
                    if (stickers.size()!=0){
                        stickers.clear();
                    }
                }
                Resources res = getResources();
                Bitmap bitmap = BitmapFactory.decodeResource(res,R.drawable.bizi_000);
                int rawCatEarWidth = bitmap.getWidth();
                int rawCatEarHeight = bitmap.getHeight();
                float recX1ToScreen = (screenSizeWidth/picWidth)*rectX1.get(i);
                float recX2ToScreen = (screenSizeWidth/picWidth)*rectX2.get(i);
                float realPicHeight = (screenSizeWidth/picWidth)*picHeight;
                float rectY1ToScreen = (realPicHeight/picHeight)*rectY1.get(i);
                float rawPicToScreenLeftX = (screenSizeWidth/picWidth)*eyeLeftX.get(i);
                float rawPicToScreenRightX = (screenSizeWidth/picWidth)*eyeRightX.get(i);

                float rawEyesDis = eyesDistance.get(i);
                eyesDistance.set(i,( rawPicToScreenRightX - rawPicToScreenLeftX));
                // 求缩放比例
                float rate = (16f*eyesDistance.get(i))/rawCatEarWidth;

                // catearsStickersType.get(faceCount).setImageBitmap(bitmap);
                int[] location = new int[2];
                biziStickersType.get(faceCount).getLocationOnScreen(location);
                int curx = location[0];
                int cury = location[1];

                TranslateAnimation anim = new TranslateAnimation(Animation.ABSOLUTE, curx,Animation.ABSOLUTE,(recX1ToScreen+recX2ToScreen)/2-90,Animation.ABSOLUTE,cury,Animation.ABSOLUTE,rectY1ToScreen+eyesDistance.get(i)*1.03f);
                // anim.setStartOffset(1000);
                // anim.setDuration(2000);
                anim.setFillAfter(true);
                biziStickersType.get(faceCount).setBackgroundResource(R.drawable.gif3);
                biziStickersType.get(faceCount).startAnimation(anim);

                biziStickersType.get(faceCount).setScaleX(rate);
                biziStickersType.get(faceCount).setScaleY(rate);
                //curSticker.setRotation((float)angle);
                biziStickersType.get(faceCount).setVisibility(View.VISIBLE);
                final AnimationDrawable curStickerAnimation = (AnimationDrawable) biziStickersType.get(faceCount).getBackground();
                curStickerAnimation.setOneShot(false);
                curStickerAnimation.start();


                float rawRate = 0.009f*rawEyesDis/rawCatEarWidth;
                // int bitmapWidth = ((int)(bitmap.getWidth()*rawRate*1.6));
                // int bitmapHeight = ((int)(bitmap.getHeight()*rawRate*0.001));
                Rect dst = new Rect();
                dst.left = rectX1.get(i);
                dst.top = rectY1.get(i)+(int)(rawEyesDis*1.3);
                dst.right = rectX2.get(i)+(int)(rawRate*4);
                dst.bottom = rectY2.get(i)+(int)(rawRate*50);
                Sticker tmp = new Sticker();
                tmp.bitmap = bitmap;
                tmp.left = dst.left;
                tmp.top = dst.top;
                tmp.right = dst.right;
                tmp.bottom = dst.bottom;
                tmp.stickerType = 2;
                // canvas.drawBitmap(bitmap, null, dst,null);
                stickers.add(tmp);
                faceCount ++;
            }
            else if (SAIHONG_sticker_state == true){
                if (faceCount == 0){
                    for (int k=0;k < glassesStickersType.size();k++){
                        glassesStickersType.get(k).clearAnimation();
                        glassesStickersType.get(k).setVisibility(View.INVISIBLE);
                        catearsStickersType.get(k).clearAnimation();
                        catearsStickersType.get(k).setVisibility(View.INVISIBLE);
                        biziStickersType.get(k).clearAnimation();
                        biziStickersType.get(k).setVisibility(View.INVISIBLE);
                    }
                    if (stickers.size()!=0){
                        stickers.clear();
                    }
                }
                Resources res = getResources();
                Bitmap bitmap = BitmapFactory.decodeResource(res,R.drawable.saihong_000);
                int rawCatEarWidth = bitmap.getWidth();
                int rawCatEarHeight = bitmap.getHeight();
                float recX1ToScreen = (screenSizeWidth/picWidth)*rectX1.get(i);
                float recX2ToScreen = (screenSizeWidth/picWidth)*rectX2.get(i);
                float realPicHeight = (screenSizeWidth/picWidth)*picHeight;
                float rectY1ToScreen = (realPicHeight/picHeight)*rectY1.get(i);
                float rawPicToScreenLeftX = (screenSizeWidth/picWidth)*eyeLeftX.get(i);
                float rawPicToScreenRightX = (screenSizeWidth/picWidth)*eyeRightX.get(i);

                float rawEyesDis = eyesDistance.get(i);
                eyesDistance.set(i,( rawPicToScreenRightX - rawPicToScreenLeftX));
                // 求缩放比例
                float rate = (19f*eyesDistance.get(i))/rawCatEarWidth;

                // catearsStickersType.get(faceCount).setImageBitmap(bitmap);
                int[] location = new int[2];
                saihongStickersType.get(faceCount).getLocationOnScreen(location);
                int curx = location[0];
                int cury = location[1];

                TranslateAnimation anim = new TranslateAnimation(Animation.ABSOLUTE, curx,Animation.ABSOLUTE,(recX1ToScreen+recX2ToScreen)/2-90,Animation.ABSOLUTE,cury,Animation.ABSOLUTE,rectY1ToScreen+eyesDistance.get(i)*1.03f);
                // anim.setStartOffset(1000);
                // anim.setDuration(2000);
                anim.setFillAfter(true);
                saihongStickersType.get(faceCount).setBackgroundResource(R.drawable.gif7);
                saihongStickersType.get(faceCount).startAnimation(anim);
                saihongStickersType.get(faceCount).setScaleX(rate*1.6f);
                saihongStickersType.get(faceCount).setScaleY(rate);
                //curSticker.setRotation((float)angle);
                saihongStickersType.get(faceCount).setVisibility(View.VISIBLE);
                final AnimationDrawable curStickerAnimation = (AnimationDrawable) saihongStickersType.get(faceCount).getBackground();
                curStickerAnimation.setOneShot(false);
                curStickerAnimation.start();


                float rawRate = 0.009f*rawEyesDis/rawCatEarWidth;
                // int bitmapWidth = ((int)(bitmap.getWidth()*rawRate*1.6));
                // int bitmapHeight = ((int)(bitmap.getHeight()*rawRate*0.001));
                Rect dst = new Rect();
                dst.left = rectX1.get(i);
                dst.top = rectY1.get(i)+(int)(rawEyesDis*1.3);
                dst.right = rectX2.get(i)+(int)(rawRate*4);
                dst.bottom = rectY2.get(i)+(int)(rawRate*50);
                Sticker tmp = new Sticker();
                tmp.bitmap = bitmap;
                tmp.left = dst.left;
                tmp.top = dst.top;
                tmp.right = dst.right;
                tmp.bottom = dst.bottom;
                tmp.stickerType = 2;
                // canvas.drawBitmap(bitmap, null, dst,null);
                stickers.add(tmp);
                faceCount ++;
            }
            else if (NONE_sticker_state == true){
                if (stickers.size()!=0){
                    stickers.clear();
                }
                for (int k=0; k < catearsStickersType.size();k++){
                    catearsStickersType.get(k).clearAnimation();
                    catearsStickersType.get(k).setVisibility(View.GONE);
                }
                for (int k=0;k < glassesStickersType.size();k++){
                    glassesStickersType.get(k).clearAnimation();
                    glassesStickersType.get(k).setVisibility(View.GONE);
                }
                for (int k=0;k < biziStickersType.size();k++){
                    biziStickersType.get(k).clearAnimation();
                    biziStickersType.get(k).setVisibility(View.GONE);
                }
                for (int k=0;k < saihongStickersType.size();k++){
                    saihongStickersType.get(k).clearAnimation();
                    saihongStickersType.get(k).setVisibility(View.GONE);
                }
            }
        }
    }

    public void initFaceDetect(){
        this.srcFace = srcImg.copy(Bitmap.Config.RGB_565, true);
        int w = srcFace.getWidth();
        int h = srcFace.getHeight();
        Log.i(tag, "待检测图像: w = " + w + "h = " + h);
        faceDetector = new FaceDetector(w, h, N_MAX);
        face = new FaceDetector.Face[N_MAX];
    }

    public boolean checkFace(Rect rect ,int i){ // 人脸识别会发生误判。所以增加函数checkFace(Rect rect)来判断，
        // 当人脸Rect的面积像素点太小时则视为无效人脸。这里阈值设为10000，实际上这个值可以通过整个图片的大小进行粗略估计到。
        int w = rect.width();
        int h = rect.height();
        int s = w*h;
        Log.i(tag, "人脸 宽w = " + w + "高h = " + h + "人脸面积 s = " + s);
        if(s < 10000){
            eyesDistance.set(i,0f);
            Log.i(tag, "无效人脸，舍弃.");
            return false;
        }
        else{
            //eyesDistance = dis;
            Log.i(tag, "有效人脸，保存.");
            return true;
        }
    }

    public Bitmap detectFace() {
        nFace = faceDetector.findFaces(srcFace, face);
        Log.i(tag, "检测到人脸：n = " + nFace);
        for (int i = 0; i < nFace; i++) {
            FaceDetector.Face f = face[i];
            PointF midPoint = new PointF();
            dis.add(f.eyesDistance());
            eyesDistance.add(dis.get(i)); // 暂时的存储
            f.getMidPoint(midPoint);
            int dd = (int)f.eyesDistance();
            // 得到两个眼睛之间的距离
            Point eyeLeft = new Point((int) (midPoint.x - dis.get(i) / 2), (int) midPoint.y);
            Point eyeRight = new Point((int) (midPoint.x + dis.get(i) / 2), (int) midPoint.y);

            // 得到人脸的矩形
            Rect faceRect = new Rect((int) (midPoint.x - dd), (int) (midPoint.y - dd), (int) (midPoint.x + dd), (int) (midPoint.y + dd));
            Log.i(tag, "左眼坐标 x = " + eyeLeft.x + "y = " + eyeLeft.y);
            if (checkFace(faceRect,i)){
                canvas = new Canvas(srcFace);
                Paint p = new Paint();
                p.setAntiAlias(true);
                p.setStrokeWidth(8);
                p.setStyle(Paint.Style.STROKE);
                p.setColor(Color.GREEN);
                //canvas.drawCircle(eyeLeft.x, eyeLeft.y, 20, p);
                //canvas.drawCircle(eyeRight.x, eyeRight.y, 20, p);
                //canvas.drawRect(faceRect, p);

                eyeLeftX.add(eyeLeft.x);
                eyeLeftY.add(eyeLeft.y);
                eyeRightX.add(eyeRight.x);
                eyeRightY.add(eyeRight.y);
                rectX1.add((int) (midPoint.x - dd));
                rectY1.add((int) (midPoint.y - dd));
                rectX2.add((int) (midPoint.x + dd));
                rectY2.add((int) (midPoint.y + dd));
            }
            else{
                eyeLeftX.add(0);
                eyeLeftY.add(0);
                eyeRightX.add(0);
                eyeRightY.add(0);
                rectX1.add(0);
                rectY1.add(0);
                rectX2.add(0);
                rectY2.add(0);
            }
        }
        // ImageUtil.saveJpeg(srcFace);
        Log.i(tag, "保存完毕");

        //将绘制完成后的faceBitmap返回
        return srcFace;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_album);


        /*
        glSurfaceView = (GLSurfaceView) findViewById(R.id.glsurfaceview_camera_album);
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setRenderer(myRender);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        MagicParams.context = glSurfaceView.getContext();
        */

        // 左右两大面板的上滑与否
        filter_closedOrNot = false;
        sticker_closedOrNot = false;


        // 初始化挂件组件
        glassesStickersType.add((ImageView)findViewById(R.id.glasses));
        glassesStickersType.add((ImageView)findViewById(R.id.glasses1));

        catearsStickersType.add((ImageView)findViewById(R.id.catear));
        catearsStickersType.add((ImageView)findViewById(R.id.catear1));

        biziStickersType.add((ImageView)findViewById(R.id.bizi1));
        biziStickersType.add((ImageView)findViewById(R.id.bizi));

        ferStcikerType.add((ImageView)findViewById(R.id.ferSticker));
        ferStcikerType.add((ImageView)findViewById(R.id.ferSticker1));

        saihongStickersType.add((ImageView)findViewById(R.id.saihong));
        saihongStickersType.add((ImageView)findViewById(R.id.saihong1));


        //glSurfaceView = (GLSurfaceView) findViewById(R.id.glsurfaceview_camera_album);
        //glSurfaceView.setEGLContextClientVersion(2);
        //glSurfaceView.setRenderer(myRender);
        //MagicParams.context = glSurfaceView.getContext();

        // 获取屏幕信息
        Point screenSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);
        int screenWidth = screenSize.x;
        int screeHeight = screenSize.y;


        Intent intent = getIntent();//getIntent将该项目中包含的原始intent检索出来，将检索出来的intent赋值给一个Intent类型的变量intent
        if (intent != null) {
            String picPath = intent.getStringExtra("picturePath");
           // String strUri = intent.getStringExtra("picturePath");
            // Uri uri = Uri.parse((String) strUri);
            Uri uri = getUri(picPath);
            Log.e("dataUri",uri.toString());
            String uriString = convertUri(uri);
            filePic = new File(uriString);
            if (filePic.exists()) {
                Log.e("FileExist","Yes");
            }
            Bitmap bitmap = BitmapFactory.decodeFile(picPath);
            srcImg = bitmap;
            picWidth = bitmap.getWidth();
            picHeight = bitmap.getHeight();

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) ((ImageView) findViewById(R.id.image2Photo)).getLayoutParams(); //获取当前控件的布局对象
            if ((screenWidth * picHeight) / picWidth > screeHeight * 10 / 11) {
                params.height = screeHeight * 10 / 11;//设置当前控件布局的高度
                findViewById(R.id.image2Photo).setLayoutParams(params);//将设置好的布局参数应用到控件中
                hasCroped = true;
            } else {
                params.height = (screenWidth * picHeight) / picWidth;//设置当前控件布局的高度
                findViewById(R.id.image2Photo).setLayoutParams(params);//将设置好的布局参数应用到控件中
                hasCroped = false;
            }
            recordHeight = params.height;

            ((ImageView) findViewById(R.id.image2Photo)).setImageBitmap(bitmap);

            //下面判断固定位置的组件的颜色是否需要改变
            if ((screenWidth * picHeight) / picWidth > screeHeight * 8 / 11) {
                findViewById(R.id.btn_camera_sticker_album).setBackgroundResource(R.drawable.ic_sticker);
                findViewById(R.id.btn_camera_filter_album).setBackgroundResource(R.drawable.ic_camera_filter);
                findViewById(R.id.btn_return).setBackgroundResource(R.drawable.ic_return);
            } else {
                findViewById(R.id.btn_camera_sticker_album).setBackgroundResource(R.drawable.ic_sticker_black);
                findViewById(R.id.btn_camera_filter_album).setBackgroundResource(R.drawable.ic_camera_filter_black);
                findViewById(R.id.btn_return).setBackgroundResource(R.drawable.ic_return_black);
            }
            //初始化交互事件的一些变量
            initView();
            initFaceDetect();

            // 把选择的图片作为背景图片保存下来
           // imageViews.add((ImageView)findViewById(R.id.image2Photo));

        }
    }


    /**
     * path转uri
     */
    private Uri getUri(String path){
        Uri uri = null;
        if (path != null) {
            path = Uri.decode(path);
            Log.d("TAG", "path2 is " + path);
            ContentResolver cr = this.getContentResolver();
            StringBuffer buff = new StringBuffer();
            buff.append("(")
                    .append(MediaStore.Images.ImageColumns.DATA)
                    .append("=")
                    .append("'" + path + "'")
                    .append(")");
            Cursor cur = cr.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[] { MediaStore.Images.ImageColumns._ID },
                    buff.toString(), null, null);
            int index = 0;
            for (cur.moveToFirst(); !cur.isAfterLast(); cur
                    .moveToNext()) {
                index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
// set _id value
                index = cur.getInt(index);
            }
            if (index == 0) {
//do nothing
            } else {
                Uri uri_temp = Uri.parse("content://media/external/images/media/" + index);
                Log.d("TAG", "uri_temp is " + uri_temp);
                if (uri_temp != null) {
                    uri = uri_temp;
                }
            }
        }
        return uri;
    }

    void startToSavePic(){

        //ConbindSaveImage.saveImage(image2Photo, stickers);
        if (stickers.size()!=0){
            for (int i=0;i<stickers.size();i++){
                Rect dst = new Rect();
                dst.left = stickers.get(i).left;
                dst.top = stickers.get(i).top;
                dst.right = stickers.get(i).right;
                dst.bottom = stickers.get(i).bottom;
                canvas.drawBitmap(stickers.get(i).bitmap, null, dst,null);
            }
        }

        picturePath = ConbindSaveImage.saveImageToGallery(this,image2Photo);
        sharebmp = createViewBitmap(image2Photo);
        //save成功后要调用展示成果图片的界面，然后有个返回相机的按钮
    }
    public static Bitmap createViewBitmap(View v) {
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }

    void initView(){
        btn_sticker = (ImageView) findViewById(R.id.btn_camera_sticker_album);
        btn_filter = (ImageView)findViewById(R.id.btn_camera_filter_album);
        btn_return = (ImageView)findViewById(R.id.btn_return);
        btn_download = (ImageView)findViewById(R.id.btn_download);
        mFilterLayout = (LinearLayout)findViewById(R.id.layout_filter);
        mStickerLayout = (LinearLayout)findViewById(R.id.layout_sticker);
        mFilterListView = (RecyclerView) findViewById(R.id.filter_listView);
        mAllStickerListView = (RecyclerView)findViewById(R.id.allsticker_listView1);
        image2Photo = (ImageView)findViewById(R.id.image2Photo);


        findViewById(R.id.btn_camera_filter_album).setOnClickListener(btn_listener);
        findViewById(R.id.btn_return).setOnClickListener(btn_listener);
        findViewById(R.id.btn_camera_beauty).setOnClickListener(btn_listener);
        findViewById(R.id.btn_camera_sticker_album).setOnClickListener(btn_listener);
        findViewById(R.id.btn_camera_filter1).setOnClickListener(btn_listener);
        findViewById(R.id.btn_camera_lip).setOnClickListener(btn_listener);
        findViewById(R.id.btn_download).setOnClickListener( btn_listener);
        findViewById(R.id.btn_sticker1).setOnClickListener(btn_listener);
        findViewById(R.id.btn_sticker2).setOnClickListener(btn_listener);
        findViewById(R.id.btn_sticker3).setOnClickListener(btn_listener);
        findViewById(R.id.btn_share).setOnClickListener(btn_listener);
        findViewById(R.id.btn_share1).setOnClickListener(btn_listener);



        // 下面开始stickerLayout最新部分list布局的设置

        // 初始化为语音的界面
        //AudioAdapter adapter1 = new AudioAdapter(AlbumActivity.this, audio_List); // 这里用于切换
        // mAllStickerListView.setAdapter(adapter1);
        //adapter1.setOnAudioChangeListener(onAudioChaneListener);

        MenuBean bean=new MenuBean();
        bean.name="开始检测";
        mFerData.add(bean);

        mStData.add(MSTType.ST1);
        mStData.add(MSTType.ST2);
        mStData.add(MSTType.ST3);
        mStData.add(MSTType.ST4);
        mStData.add(MSTType.ST5);
        mStData.add(MSTType.ST6);
        mStData.add(MSTType.ST7);

    }

    //设置filter样式
    /*
    public void setFilter(final MagicFilterType type){

        glSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                if (filter != null)
                    filter.destroy();
                filter = null;
                filter = MagicFilterFactory.initFilters(type);
                if (filter != null)
                    filter.init();
            }

        });
        glSurfaceView.requestRender();
    }*/
    private FilterAdapter.onFilterChangeListener onFilterChangeListener = new FilterAdapter.onFilterChangeListener() {
        @Override
        public void onFilterChanged(MagicFilterType filterType)  {
            //magicEngine.setFilter(filterType);
            //setFilter(filterType);
            // reDrawSrcImg();
        }
    };

    View.OnClickListener btn_listener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_camera_sticker_album:
                    LinearLayoutManager linearLayoutManager11 = new LinearLayoutManager(AlbumActivity.this);
                    linearLayoutManager11.setOrientation(LinearLayoutManager.HORIZONTAL);
                    mAllStickerListView.setLayoutManager(linearLayoutManager11);
                    stAdapter = new MenuImgAdapter(AlbumActivity.this, mStData);
                    mAllStickerListView.setAdapter(stAdapter);
                    stAdapter.setOnMstChangeListener(mstListener);
                    showStickers();
                    break;
                case R.id.btn_camera_filter_album:
                    // 右边滤镜模块的上滑面板
                    LinearLayoutManager linearLayoutManager00 = new LinearLayoutManager(AlbumActivity.this);
                    linearLayoutManager00.setOrientation(LinearLayoutManager.HORIZONTAL);
                    mFilterListView.setLayoutManager(linearLayoutManager00);
                    mAdapter = new FilterAdapter(AlbumActivity.this, types);
                    mFilterListView.setAdapter(mAdapter);
                    mAdapter.setOnFilterChangeListener(onFilterChangeListener);
                    showFilters();
                    break;
                case R.id.btn_return:
                    Intent intent = new Intent(AlbumActivity.this, CameraActivity.class);
                    startActivity(intent);
                    break;
                case R.id.btn_sticker2:
                    findViewById(R.id.btn_sticker2).setBackgroundResource(R.drawable.ic_sticker_latest_pressed);
                    findViewById(R.id.btn_sticker3).setBackgroundResource(R.drawable.ic_sticker_fer);
                    findViewById(R.id.btn_sticker1).setBackgroundResource(R.drawable.ic_sticker_st);
                    GridLayoutManager sticker_gridLayoutMag = new GridLayoutManager(AlbumActivity.this,5);
                    sticker_gridLayoutMag.setOrientation(GridLayoutManager.VERTICAL);
                    mAllStickerListView.setLayoutManager(sticker_gridLayoutMag);
                    mAllStickerListView.setHasFixedSize(true);
                    StickerAdapter adapter = new StickerAdapter(AlbumActivity.this, sticker_List); // 这里用于切换
                    mAllStickerListView.setAdapter(adapter);
                    adapter.setOnStickerChangeListener(onStickerChaneListener);
                    break;
                case R.id.btn_sticker1:
                    findViewById(R.id.btn_sticker1).setBackgroundResource(R.drawable.ic_sticker_st_pressed);
                    findViewById(R.id.btn_sticker3).setBackgroundResource(R.drawable.ic_sticker_fer);
                    findViewById(R.id.btn_sticker2).setBackgroundResource(R.drawable.ic_sticker_latest);
                    findViewById(R.id.btn_camera_beauty).setBackgroundResource(R.drawable.btn_camera_beauty_pressed);
                    findViewById(R.id.btn_camera_filter1).setBackgroundResource(R.drawable.btn_camera_filter_small);
                    //AudioAdapter adapter1 = new AudioAdapter(AlbumActivity.this, audio_List); // 这里用于切换
                    //mAllStickerListView.setAdapter(adapter1);
                    //adapter1.setOnAudioChangeListener(onAudioChaneListener);
                    LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(AlbumActivity.this);
                    linearLayoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);
                    mAllStickerListView.setLayoutManager(linearLayoutManager1);
                    stAdapter = new MenuImgAdapter(AlbumActivity.this, mStData);
                    mAllStickerListView.setAdapter(stAdapter);
                    stAdapter.setOnMstChangeListener(mstListener);
                    break;
                case R.id.btn_sticker3:
                    findViewById(R.id.btn_sticker3).setBackgroundResource(R.drawable.ic_sticker_fer_pressed);
                    findViewById(R.id.btn_sticker1).setBackgroundResource(R.drawable.ic_sticker_st);
                    findViewById(R.id.btn_sticker2).setBackgroundResource(R.drawable.ic_sticker_latest);
                    // 开始表情识别
                    //先展示选择面板
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AlbumActivity.this);
                    linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                    mAllStickerListView.setLayoutManager(linearLayoutManager);
                    ferAdapter = new MenuAdapter1(AlbumActivity.this, mFerData);
                    mAllStickerListView.setAdapter(ferAdapter);
                    ferAdapter.setOnClickListener(btn_fer_listener);

                    break;
                case R.id.btn_download:
                    startToSavePic();
                    Intent intente = new Intent(AlbumActivity.this, ShowPhoActivity.class);
                    intente.putExtra("picPath",picturePath);
                    startActivity(intente);
                    Toast.makeText(getApplicationContext(), "保存成功", Toast.LENGTH_LONG).show();
                    break;
                case R.id.btn_camera_filter1:
                    findViewById(R.id.btn_camera_beauty).setBackgroundResource(R.drawable.btn_camera_beauty);
                    findViewById(R.id.btn_camera_filter1).setBackgroundResource(R.drawable.btn_camera_filter_small_pressed);
                    break;
                case R.id.btn_camera_beauty:
                    /*
                    findViewById(R.id.btn_camera_beauty).setBackgroundResource(R.drawable.btn_camera_beauty_pressed);
                    findViewById(R.id.btn_camera_filter1).setBackgroundResource(R.drawable.btn_camera_filter_small);
                    new AlertDialog.Builder(AlbumActivity.this)
                            .setSingleChoiceItems(new String[] { "关闭", "1", "2", "3", "4", "5"}, MagicParams.beautyLevel,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
//                                            cameraView2.setBeautyLevel(which); //设置美颜级别
                                            setBeautyLevel(which);
//                                            myRender.setBeautyLevel(which);
                                            //reDrawSrcImg();
                                            dialog.dismiss();
                                        }
                                    })
                            .setNegativeButton("取消", null)
                            .show();*/
                    break;
                case R.id.btn_share:
                    sharebmp = createViewBitmap(image2Photo);
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.angry0);
                    ShareToolUtil.shareWechatMoment(AlbumActivity.this,"分享到朋友圈",sharebmp);

                    break;
                case R.id.btn_share1:
                    sharebmp = createViewBitmap(image2Photo);
                    Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.angry0);
                    ShareToolUtil.shareWechatFriend(AlbumActivity.this,"分享给好友",sharebmp);
                    break;
                default:
                    break;
            }
        }
    };

    //美颜滤镜级别
    public void setBeautyLevel(int _level){
        MagicParams.beautyLevel = _level;

    }

    private ClickUtils.OnClickListener btn_fer_listener = new ClickUtils.OnClickListener(){
        @Override
        public void onClick(View v, int type, int pos, int child) {
            MenuBean m=mFerData.get(pos);
            String name=m.name;
            ferAdapter.checkPos=pos;
            v.setSelected(true);
            ferAdapter.notifyDataSetChanged();
            if (pos == 0){
                Log.d("FER","开始识别表情");
                sendRequestWithHttpURLConnection("0","0");
            }
        }
    };

    private ClickUtils.OnClickListener btn_st_listener = new ClickUtils.OnClickListener(){
        @Override
        public void onClick(View v, int type, int pos, int child) {
            MSTType m=mStData.get(pos);
            stAdapter.checkPos=pos;
            v.setSelected(true);
            stAdapter.notifyDataSetChanged();
        }
    };



    private MenuImgAdapter.onMstChangeListener mstListener = new MenuImgAdapter.onMstChangeListener() {
        @Override
        public void onMstChanged(MSTType mstData) {
            switch(mstData){
                case NONE:
                    break;
                case ST1:
                   //这里是原图
                    break;
                case ST2:
                    Toast.makeText(getApplicationContext(), "开始转换", Toast.LENGTH_LONG).show();
                    sendRequestWithHttpURLConnection("1","1");
                    Toast.makeText(getApplicationContext(), "结束转换", Toast.LENGTH_LONG).show();
                    break;
                case ST3:
                    Toast.makeText(getApplicationContext(), "开始转换", Toast.LENGTH_LONG).show();
                    sendRequestWithHttpURLConnection("1","2");
                    Toast.makeText(getApplicationContext(), "结束转换", Toast.LENGTH_LONG).show();
                    break;
                case ST4:
                    Toast.makeText(getApplicationContext(), "开始转换", Toast.LENGTH_LONG).show();
                    sendRequestWithHttpURLConnection("1","3");
                    Toast.makeText(getApplicationContext(), "结束转换", Toast.LENGTH_LONG).show();
                    break;
                case ST5:
                    Toast.makeText(getApplicationContext(), "开始转换", Toast.LENGTH_LONG).show();
                    sendRequestWithHttpURLConnection("1","4");
                    Toast.makeText(getApplicationContext(), "结束转换", Toast.LENGTH_LONG).show();
                    break;
                case ST6:
                    Toast.makeText(getApplicationContext(), "开始转换", Toast.LENGTH_LONG).show();
                    sendRequestWithHttpURLConnection("1","5");
                    Toast.makeText(getApplicationContext(), "结束转换", Toast.LENGTH_LONG).show();
                    break;
                case ST7:
                    Toast.makeText(getApplicationContext(), "开始转换", Toast.LENGTH_LONG).show();
                    sendRequestWithHttpURLConnection("1","6");
                    Toast.makeText(getApplicationContext(), "结束转换", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
    };
    private StickerAdapter.onStickerChangeListener onStickerChaneListener = new StickerAdapter.onStickerChangeListener() {
        @Override
        public void onStickerChanged(StickerItemType stickerItemType) {
            switch (stickerItemType){
                case NONE:
                    NONE_sticker_state = true;
                    GLASSES_sticker_state = false;
                    CATEAR_sticker_state = false;
                    BI_sticker_state = false;
                    SAIHONG_sticker_state = false;
                    break;
                case CATEAR:
                    CATEAR_sticker_state = true;
                    GLASSES_sticker_state = false;
                    NONE_sticker_state = false;
                    BI_sticker_state = false;
                    SAIHONG_sticker_state = false;
                    break;
                case GLASSES:
                    GLASSES_sticker_state = true;//后面引入别的state再考虑切换的问题？？？？？
                    NONE_sticker_state = false;
                    CATEAR_sticker_state = false;
                    BI_sticker_state = false;
                    SAIHONG_sticker_state = false;
                    break;
                case BI:
                    BI_sticker_state = true;
                    GLASSES_sticker_state = false;//后面引入别的state再考虑切换的问题？？？？？
                    NONE_sticker_state = false;
                    CATEAR_sticker_state = false;
                    SAIHONG_sticker_state = false;
                    break;
                case SAIHONG:
                    BI_sticker_state = false;
                    GLASSES_sticker_state = false;//后面引入别的state再考虑切换的问题？？？？？
                    NONE_sticker_state = false;
                    CATEAR_sticker_state = false;
                    SAIHONG_sticker_state = true;
                    break;
                default:
                    break;
            }

            if (GLASSES_sticker_state == true){
                faceCount = 0;
                //启动人脸识别
                //下面开始对获得的这张Bitmap-> bitmap进行人脸检测
                if(hasDetected == false){
                    mainHandler.sendEmptyMessage(1);
                    //Toast.makeText(getApplicationContext(), "开始检测", Toast.LENGTH_LONG).show();
                    checkFaceThread.start();
                }
                else{ // 直接在识别好的进行贴图
                    for (int i=0;i < nFace;i++){
                        if (eyesDistance.get(i) != 0){
                            setStickers(i);
                        }
                    }
                }
            }
            else if(CATEAR_sticker_state == true){
                faceCount = 0;
                if(hasDetected == false){
                    mainHandler.sendEmptyMessage(1);
                    checkFaceThread.start();
                }
                else{ // 直接在识别好的进行贴图
                    for (int i=0;i < nFace;i++){
                        if (eyesDistance.get(i) != 0){
                            setStickers(i);
                        }
                    }
                }
            }
            else if (BI_sticker_state == true){
                faceCount = 0;
                if(hasDetected == false){
                    mainHandler.sendEmptyMessage(1);
                    checkFaceThread.start();
                }
                else{ // 直接在识别好的进行贴图
                    for (int i=0;i < nFace;i++){
                        if (eyesDistance.get(i) != 0){
                            setStickers(i);
                        }
                    }
                }
            }
            else if (SAIHONG_sticker_state == true){
                faceCount = 0;
                if(hasDetected == false){
                    mainHandler.sendEmptyMessage(1);
                    checkFaceThread.start();
                }
                else{ // 直接在识别好的进行贴图
                    for (int i=0;i < nFace;i++){
                        if (eyesDistance.get(i) != 0){
                            setStickers(i);
                        }
                    }
                }
            }
            else if (NONE_sticker_state == true){
                for (int i=0;i < nFace;i++){
                    if (eyesDistance.get(i) != 0){
                        setStickers(i);
                    }
                }
                faceCount = 0;
            }

        }
    };




    // 开启美型滤镜面板上的东西的显示
    public void showFilters(){

        //表示当前有上滑面板
        filter_closedOrNot = true;

        findViewById(R.id.btn_camera_beauty).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_camera_filter1).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_camera_lip).setVisibility(View.VISIBLE);


        ObjectAnimator animator = ObjectAnimator.ofFloat(mFilterLayout, "translationY", mFilterLayout.getHeight()+1000, 1280);
        animator.setDuration(200);
        animator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                mFilterLayout.setVisibility(View.VISIBLE);
                findViewById(R.id.btn_camera_sticker_album).setVisibility(View.GONE);
                findViewById(R.id.btn_camera_filter_album).setVisibility(View.GONE);
                findViewById(R.id.btn_return).setVisibility(View.GONE);

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });
        animator.start();

    }

    public void hideFilters(){
        if(filter_closedOrNot == true){
            ObjectAnimator animator = ObjectAnimator.ofFloat(mFilterLayout, "translationY",  1280,mFilterLayout.getHeight()+1000);
            animator.setDuration(100);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    // TODO Auto-generated method stub
                    findViewById(R.id.btn_camera_sticker_album).setVisibility(View.VISIBLE);
                    findViewById(R.id.btn_camera_filter_album).setVisibility(View.VISIBLE);
                    findViewById(R.id.btn_return).setVisibility(View.VISIBLE);

                    //瞬间隐藏掉面板上的东西
                    findViewById(R.id.btn_camera_beauty).setVisibility(View.GONE);
                    findViewById(R.id.btn_camera_filter1).setVisibility(View.GONE);
                    findViewById(R.id.btn_camera_lip).setVisibility(View.GONE);
                    mFilterLayout.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    // TODO Auto-generated method stub
                    findViewById(R.id.btn_download).setClickable(true);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    // TODO Auto-generated method stub
                    findViewById(R.id.btn_download).setClickable(true);
                }
            });
            animator.start();
        }

        //表示当前没有上滑面板
        filter_closedOrNot = false;

    }

    // 开启挂件面板上的东西的显示
    public void showStickers(){
        sticker_closedOrNot = true;

        findViewById(R.id.btn_sticker1).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_sticker2).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_sticker3).setVisibility(View.VISIBLE);
        ObjectAnimator animator = ObjectAnimator.ofFloat(mStickerLayout, "translationY",mStickerLayout.getHeight()+1000, 1280);
        animator.setDuration(200);
        animator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                mStickerLayout.setVisibility(View.VISIBLE);
                findViewById(R.id.btn_camera_sticker_album).setVisibility(View.GONE);
                findViewById(R.id.btn_camera_filter_album).setVisibility(View.GONE);
                findViewById(R.id.btn_return).setVisibility(View.GONE);
                findViewById(R.id.btn_download).setVisibility(View.VISIBLE);


            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }
        });
        animator.start();

    }

    public void hideStickers(){
        if(sticker_closedOrNot == true){

            ObjectAnimator animator = ObjectAnimator.ofFloat(mStickerLayout, "translationY",  1280,mStickerLayout.getHeight()+1000);
            animator.setDuration(100);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    // TODO Auto-generated method stub
                    findViewById(R.id.btn_sticker1).setVisibility(View.GONE);
                    findViewById(R.id.btn_sticker2).setVisibility(View.GONE);
                    findViewById(R.id.btn_sticker3).setVisibility(View.GONE);
                    findViewById(R.id.btn_camera_sticker_album).setVisibility(View.VISIBLE);
                    findViewById(R.id.btn_camera_filter_album).setVisibility(View.VISIBLE);
                    findViewById(R.id.btn_return).setVisibility(View.VISIBLE);



                    //瞬间隐藏掉面板上的东西
                    mStickerLayout.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    // TODO Auto-generated method stub
                    findViewById(R.id.btn_download).setClickable(true);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    // TODO Auto-generated method stub
                    findViewById(R.id.btn_download).setClickable(true);
                }
            });
            animator.start();
        }
        sticker_closedOrNot = false;

    }

    // 检测在上滑动板出现的情况下的屏幕点击事件
    public boolean onTouchEvent(MotionEvent event){
        if (event.getAction() != MotionEvent.ACTION_DOWN) {//MotionEvent.ACTION_MOVE
            return false;
        }
        hideFilters();
        hideStickers();

        return true;
    }

    //*****************************************下面是加入的*********************************************//
    /*
    @Override
    protected void onPause() {
        super.onPause();
        glSurfaceView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        glSurfaceView.onResume();
    }*/
    /*
    private void reDrawSrcImg() {
//        new Thread() {
//            @Override
//            public void run() {
//                myRender.filterIsChanged = true;
        //Bitmap tmp = srcImg.copy(Bitmap.Config.RGB_565, true);

        glSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
//                        myRender.drawPhotoExtenal();
                final Bitmap newBitmap = filteringPhoto().copy(Bitmap.Config.RGB_565, true);
                Log.d("queueEvent", "run: filtering Photo");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("runOnUiThread", "run: updateImage");
                        ((ImageView) findViewById(R.id.image2Photo)).setImageBitmap(newBitmap);
                    }
                });
            }
        });*/
        //myRender.onDrawFrame();
//                while(myRender.filterIsChanged) {
//                    Log.d("AlbumActivity reDrawSrc", "waiting for MyRender");
//                }
//
//                newBitmap = myRender.filterBitmap.copy(Bitmap.Config.RGB_565, true);
//                ((ImageView) findViewById(R.id.image2Photo)).setImageBitmap(newBitmap);
//            }
//        }.start();

    // }


    /**
     * 异步线程请求到的图片数据，利用Handler，在主线程中显示
     */
    class ImageHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 0:
                    stres_img = (Bitmap)msg.obj;
                    if(stres_img != null){
                        image2Photo.setImageBitmap(stres_img);
                    }
                    break;

                default:
                    break;
            }
        }
    }

    private ImageHandler imgHandler = new ImageHandler();


    // 开启线程来发起网络请求
    private void sendRequestWithHttpURLConnection(final String opra,final String style) {
        //匿名类,并重写run这个方法。此为开启新的进程的方法。
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("HttpURLConnection","Success");
                Log.e("fileURI",filePic.getPath());
                HttpAssist httpAssist = new HttpAssist();
                if (opra == "0"){
                    ferresponse = httpAssist.uploadFile(filePic,opra,style);//1表示需要进行表情检测
                    Log.e("Debug",ferresponse);
                    if (!ferresponse.isEmpty()) {
                        //在子线程中将Message对象发出去
                        Message message = new Message();
                        message.what = SHOW_RESPONSE;
                        message.obj = ferresponse;
                        mainHandler.sendMessage(message);
                    }
                }
                else{

                    stres_img =  httpAssist.getImg(filePic,opra,style);
                    Message msg = imgHandler.obtainMessage();
                    msg.what = 0;
                    msg.obj = stres_img;
                    imgHandler.sendMessage(msg);
                }

            }
        }).start(); // Don't forget about it!!
    }

    /**
     * 将content类型的Uri转化为文件类型的Uri
     * @param uri
     * @return
     */
    private String convertUri(Uri uri){
        InputStream is;
        try {
            //Uri ----> InputStream
            is = getContentResolver().openInputStream(uri);
            //InputStream ----> Bitmap
            Bitmap bm = BitmapFactory.decodeStream(is);
            is.close();
            return saveBitmap(bm, "temp");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将Bitmap写入SD卡中的一个文件中,并返回写入文件的Uri
     * @param bm
     * @param dirPath
     * @return
     */
    private String saveBitmap(Bitmap bm, String dirPath) {
        //新建文件夹用于存放裁剪后的图片
        File tmpDir = new File(Environment.getExternalStorageDirectory() + "/" + dirPath);
        // 其实就在temp这个文件夹下，而不是/storage/temp,前面的/storage可能指的是内部存储，而非storage这个文件夹
        if (!tmpDir.exists()) {
            tmpDir.mkdir();
        }

        //新建文件存储裁剪后的图片
        final long time = System.currentTimeMillis();
        String fileName = Long.toString(time);
        //picPath = "/temp/" + fileName;
        picPathh = tmpDir.getAbsolutePath() + "/" + fileName + ".jpg";
        //Toast.makeText(getApplicationContext(), picPathh, Toast.LENGTH_SHORT).show();
        File img = new File(picPathh);
        try {
            //打开文件输出流
            FileOutputStream fos = new FileOutputStream(img);
            //将bitmap压缩后写入输出流(参数依次为图片格式、图片质量和输出流)
            bm.compress(Bitmap.CompressFormat.JPEG, 85, fos);
            //刷新输出流
            fos.flush();
            //关闭输出流
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //返回File类型的path
        return picPathh;
    }

    //传入图片的bitmap, isRotated应该是手机是否横屏，调用的时候就传入false

    /*private Bitmap filteringPhoto(){
        Bitmap _bitmap = srcImg.copy(Bitmap.Config.RGB_565, true);
        int width = _bitmap.getWidth();
        int height = _bitmap.getHeight();
        int[] mFrameBuffers = new int[1];
        int[] mFrameBufferTextures = new int[1];
        if(beautyFilter == null)
            beautyFilter = new MagicBeautyFilter();
        beautyFilter.init();
        beautyFilter.onDisplaySizeChanged(width, height);
        beautyFilter.onInputSizeChanged(width, height);
        beautyFilter.setBeautyLevel(level);

        if(filter != null) {
            filter.onInputSizeChanged(width, height);
            filter.onDisplaySizeChanged(width, height);
        }
        GLES20.glGenFramebuffers(1, mFrameBuffers, 0);
        GLES20.glGenTextures(1, mFrameBufferTextures, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFrameBufferTextures[0]);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, mFrameBufferTextures[0], 0);

        GLES20.glViewport(0, 0, width, height);
        int textureId = OpenGlUtils.loadTexture(_bitmap, OpenGlUtils.NO_TEXTURE, true);

        FloatBuffer gLCubeBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.CUBE.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        FloatBuffer gLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        gLCubeBuffer.put(TextureRotationUtil.CUBE).position(0);
//        if(isRotated)
//            gLTextureBuffer.put(TextureRotationUtil.getRotation(Rotation.NORMAL, false, false)).position(0);
//        else
        gLTextureBuffer.put(TextureRotationUtil.getRotation(Rotation.NORMAL, false, true)).position(0);

        Log.d("albumAct", "isDrawing");
        if(filter == null){
            beautyFilter.onDrawFrame(textureId, gLCubeBuffer, gLTextureBuffer);
            Log.d("albumAct", "filter is null onDrawFrame");
        }else{
            beautyFilter.onDrawFrame(textureId);
            filter.onDrawFrame(mFrameBufferTextures[0], gLCubeBuffer, gLTextureBuffer);
            Log.d("albumAct", "filtering onDrawFrame");
        }
        IntBuffer ib = IntBuffer.allocate(width * height);
        GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, ib);
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        result.copyPixelsFromBuffer(ib);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glDeleteTextures(1, new int[]{textureId}, 0);
        GLES20.glDeleteFramebuffers(mFrameBuffers.length, mFrameBuffers, 0);
        GLES20.glDeleteTextures(mFrameBufferTextures.length, mFrameBufferTextures, 0);

        beautyFilter.destroy();
        beautyFilter = null;
        return result;
    }*/



}
