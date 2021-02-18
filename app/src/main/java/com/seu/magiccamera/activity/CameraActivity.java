package com.seu.magiccamera.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.seu.magiccamera.R;
import com.seu.magiccamera.Utils;
import com.seu.magiccamera.adapter.AudioAdapter;
import com.seu.magiccamera.adapter.FilterAdapter;
import com.seu.magiccamera.adapter.MenuAdapter;
import com.seu.magiccamera.adapter.ModeAdapter;
import com.seu.magiccamera.adapter.StickerAdapter;
import com.seu.magiccamera.helper.ApplyForPermission;
import com.seu.magiccamera.helper.AudioItemType;
import com.seu.magiccamera.helper.ClickUtils;
import com.seu.magiccamera.helper.DictationListener;
import com.seu.magiccamera.helper.DictationUtil;
import com.seu.magiccamera.helper.GetAlbumPic;
import com.seu.magiccamera.helper.MenuBean;
import com.seu.magiccamera.helper.StickerItemType;
import com.seu.magiccamera.helper.VibratorUtil;
import com.seu.magiccamera.widget.EndlessRecyclerOnScrollListener;
import com.seu.magicfilter.MagicEngine;
import com.seu.magicfilter.camera.StickerView;
import com.seu.magicfilter.filter.helper.MagicFilterType;
import com.seu.magicfilter.widget.MagicCameraView;
import com.xiaojigou.luo.xjgarsdk.XJGArSdkApi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by why8222 on 2016/3/17.
 */
public class CameraActivity extends Activity{
    private LinearLayout mFilterLayout;
    private RecyclerView mFilterListView;
    private LinearLayout mStickerLayout;
    private RecyclerView mAllStickerListView;
    private RecyclerView mModeListView;

    private FilterAdapter mAdapter;
    private ModeAdapter modeAdapter;
    private MenuAdapter beautyAdapter;

    private LinearLayoutManager manager;
    private MagicEngine magicEngine;
    private ObjectAnimator animator;

    private final int MODE_PIC = 1;
    private final int MODE_VIDEO = 2;
    private int mode = MODE_PIC;

    private ImageView btn_shutter;
    private ImageView btn_sticker;
    private ImageView btn_album;

    private boolean filter_closedOrNot;
    private boolean sticker_closedOrNot;
    private boolean isRecording = false;


    private boolean NONE_audio_state = true;
    private boolean CUSTOM_audio_state = false;
    private boolean SAYHI_audio_state = false;
    private boolean JIAYOU_audio_state = false;
    private boolean QIEZI_audio_state = false;
    private boolean BANG_audio_state = false;


    private ArrayList<Boolean>audio_item_state = new ArrayList<>();
    private List<String> mList = new ArrayList<>();


    private Bitmap bitmap;
    private ImageView circle;

    /*
     * 四咸
     * */

    static boolean bShowFaceSurgery = false;
    private ArrayList<MenuBean> mSurgeryData = new ArrayList<>();
    private LinearLayout mFaceSurgery;
    protected SeekBar mFaceSurgeryFaceShapeSeek;
    protected SeekBar mFaceSurgeryBigEyeSeek;
    protected SeekBar mSkinSmoothSeek;
    protected SeekBar mSkinWihtenSeek;
    protected SeekBar mRedFaceSeek;


    /*
     * 四咸
     * */


    /*
    * 国锐
    * */
    /*##############*/
    private Handler mHandler;
    private StickerView stickerView;
    private Button hat,cat,rabit;
    private ImageView iv_show;
    private RelativeLayout group;
    /*##############*/
    private boolean NONE_sticker_state = true;
    private boolean CATEAR_sticker_state = false;
    private boolean HAT_sticker_state = false;
    private boolean RABIT_sticker_state = false;



    /*
     * 国锐
     * */


    /*
     *丽3
     * */

    private final AudioItemType[] audio_sticker_List = new AudioItemType[]{
            AudioItemType.NONE, AudioItemType.CUSTOM, AudioItemType.SAYHI, AudioItemType.QIEZI, AudioItemType.JIAYOU, AudioItemType.BANG, AudioItemType.NONE, AudioItemType.NONE, AudioItemType.NONE, AudioItemType.NONE, AudioItemType.NONE,
            AudioItemType.NONE, AudioItemType.NONE, AudioItemType.NONE, AudioItemType.NONE, AudioItemType.NONE, AudioItemType.NONE, AudioItemType.NONE, AudioItemType.NONE, AudioItemType.NONE, AudioItemType.NONE, AudioItemType.NONE,
            AudioItemType.NONE, AudioItemType.NONE, AudioItemType.NONE, AudioItemType.NONE, AudioItemType.NONE, AudioItemType.NONE, AudioItemType.NONE, AudioItemType.NONE, AudioItemType.NONE, AudioItemType.NONE, AudioItemType.NONE, AudioItemType.NONE, AudioItemType.NONE, AudioItemType.NONE, AudioItemType.NONE, AudioItemType.NONE,
    };
    private final MagicFilterType[] types = new MagicFilterType[]{
            MagicFilterType.NONE, MagicFilterType.FAIRYTALE, MagicFilterType.SUNRISE, MagicFilterType.SUNSET, MagicFilterType.WHITECAT, MagicFilterType.BLACKCAT, MagicFilterType.SKINWHITEN, MagicFilterType.HEALTHY, MagicFilterType.SWEETS, MagicFilterType.ROMANCE, MagicFilterType.SAKURA,
            MagicFilterType.WARM, MagicFilterType.ANTIQUE, MagicFilterType.NOSTALGIA, MagicFilterType.CALM, MagicFilterType.LATTE, MagicFilterType.TENDER, MagicFilterType.COOL, MagicFilterType.EMERALD, MagicFilterType.EVERGREEN, MagicFilterType.CRAYON, MagicFilterType.SKETCH, MagicFilterType.AMARO, MagicFilterType.BRANNAN, MagicFilterType.BROOKLYN,
            MagicFilterType.EARLYBIRD, MagicFilterType.FREUD, MagicFilterType.HEFE, MagicFilterType.HUDSON, MagicFilterType.INKWELL, MagicFilterType.KEVIN, MagicFilterType.LOMO, MagicFilterType.N1977, MagicFilterType.NASHVILLE, MagicFilterType.PIXAR, MagicFilterType.RISE, MagicFilterType.SIERRA, MagicFilterType.SUTRO, MagicFilterType.TOASTER2, MagicFilterType.VALENCIA, MagicFilterType.WALDEN, MagicFilterType.XPROII
    };

    private final StickerItemType[] sticker_List = new StickerItemType[]{
            StickerItemType.NONE, StickerItemType.CATEAR, StickerItemType.HAT, StickerItemType.RABIT,StickerItemType.BI, StickerItemType.SAIHONG, StickerItemType.MAOZI, StickerItemType.BAHENG, StickerItemType.LUJIAO, StickerItemType.FACE, StickerItemType.RING, StickerItemType.ERDUO, StickerItemType.ERDUO1,
            StickerItemType.BOZI, StickerItemType.XIN, StickerItemType.NONE, StickerItemType.NONE, StickerItemType.NONE, StickerItemType.NONE, StickerItemType.NONE, StickerItemType.NONE, StickerItemType.NONE, StickerItemType.NONE, StickerItemType.NONE, StickerItemType.NONE, StickerItemType.NONE,
            StickerItemType.NONE, StickerItemType.NONE, StickerItemType.NONE, StickerItemType.NONE, StickerItemType.NONE, StickerItemType.NONE, StickerItemType.NONE,
            StickerItemType.NONE, StickerItemType.NONE, StickerItemType.NONE, StickerItemType.NONE,
    };


    private void getPermissions() {
        String[] permissionLists = {Manifest.permission.CAMERA,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ApplyForPermission.applyPermission(this, permissionLists, 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        getPermissions();

        String licenseText = "hMPthC0oBIbtMp515TWb9jZvrLAKWIMvA4Dhf03n51QvnJr7jZowVe86d0WwU0NK9QGRFaXQn628fRu941qyr3FtsI5R7Y6v1XEpL6YvQNWQCkFEt1SAb0hyawimOYf1tfG2lIaNE63c5e+OxXssOVUWvw8tOr2glVwWVzh79NmZMahrnS8l69SoeoXLMKCYlvAt/qJFFk4+6Aq3QvOv3o72fq5p90yty+YWg7o0HirZpMSP9P5/DHYPFqR/ud7twTJ+Yo2+ZzYvodqRQbGG0HseZn8Xpt7fZdFuZbc2HGRMVk56vNDMRlcGZZXAjENk7m2UMhi1ohhuSf4WmIgXCZFiJXvYFByaY625gXKtEI7+b7t81nWQYHP9BEbzURwL";
        XJGArSdkApi.XJGARSDKInitialization(this,licenseText,"DoctorLuoInvitedUser:teacherluo", "LuoInvitedCompany:www.xiaojigou.cn");


        XJGArSdkApi.XJGARSDKSetOptimizationMode(2);
        XJGArSdkApi.XJGARSDKSetShowStickerPapers(false);

        MagicEngine.Builder builder = new MagicEngine.Builder();
        magicEngine = builder.build((MagicCameraView)findViewById(R.id.glsurfaceview_camera));

        //mode_select  recyclerView
        mModeListView = (RecyclerView)findViewById(R.id.mode_select_listView);
        mList.add("");
        mList.add("拍照");
        mList.add("长视频");
        mList.add("");

        manager = new LinearLayoutManager(this);
        //设置为横向滑动
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mModeListView.setLayoutManager(manager);
        modeAdapter = new ModeAdapter(mList);

        mModeListView.setAdapter(modeAdapter);
        initView();

        //左右两大面板的上滑与否
        filter_closedOrNot = false;
        sticker_closedOrNot = false;


        //存储语音挂件的状态（语音识别）
        audio_item_state.add(NONE_audio_state);
        audio_item_state.add(CUSTOM_audio_state);
        audio_item_state.add(SAYHI_audio_state);
        audio_item_state.add(JIAYOU_audio_state);
        audio_item_state.add(QIEZI_audio_state);
        audio_item_state.add(BANG_audio_state);
    }

    private void initView(){
        /*
         * 国锐
         * */
        /*##############*/
        stickerView = (StickerView) findViewById(R.id.sticker);

        group = (RelativeLayout) findViewById(R.id.group);
        group.setDrawingCacheEnabled(true);
        group.buildDrawingCache();
        iv_show = (ImageView) findViewById(R.id.iv_show);

        stickerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() != MotionEvent.ACTION_DOWN) {//MotionEvent.ACTION_MOVE
                    return false;
                }
                System.out.println("你个大笨蛋！！！！！！！！！！！！");
                hideFilters();
                hideStickers();
                findViewById(R.id.mode_select_listView).setVisibility(View.VISIBLE);
                return true;
            }
        });
        // cat = (Button) findViewById(R.id.cat);
        // hat = (Button) findViewById(R.id.hat);
        // rabit = (Button) findViewById(R.id.rabit);
        /*
        hat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                        R.drawable.hat);
                stickerView.setWaterMark(bitmap,"hat");
            }
        });
        cat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                        R.drawable.cat);
                stickerView.setWaterMark(bitmap,"cat");
            }
        });
        rabit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                        R.drawable.rabit);
                stickerView.setWaterMark(bitmap,"rabit");
            }
        });*/
        /*##############*/


        /*
         * 国锐
         * */

        /*
         * 四咸
         * */
        MenuBean bean=new MenuBean();
        bean.name="红润";
        mSurgeryData.add(bean);

        bean=new MenuBean();
        bean.name="美白";
        mSurgeryData.add(bean);

        bean=new MenuBean();
        bean.name="磨皮";
        mSurgeryData.add(bean);

        bean=new MenuBean();
        bean.name="瘦脸";
        mSurgeryData.add(bean);

        bean=new MenuBean();
        bean.name="大眼";
        mSurgeryData.add(bean);

        mFaceSurgery = (LinearLayout)findViewById(R.id.faceSurgery);
        mFaceSurgeryFaceShapeSeek = (SeekBar)findViewById(R.id.faceShapeValueBar);
        mFaceSurgeryFaceShapeSeek.setProgress(0);
        mFaceSurgeryBigEyeSeek = (SeekBar)findViewById(R.id.bigeyeValueBar);
        mFaceSurgeryBigEyeSeek.setProgress(0);

        mSkinSmoothSeek = (SeekBar)findViewById(R.id.skinSmoothValueBar);
        mSkinSmoothSeek.setProgress(0);
        mSkinWihtenSeek = (SeekBar)findViewById(R.id.skinWhitenValueBar);
        mSkinWihtenSeek.setProgress(0);
        mRedFaceSeek = (SeekBar)findViewById(R.id.redFaceValueBar);
        mRedFaceSeek.setProgress(0);


        XJGArSdkApi.XJGARSDKSetSkinSmoothParam(0);
        XJGArSdkApi.XJGARSDKSetWhiteSkinParam(0);
        XJGArSdkApi.XJGARSDKSetRedFaceParam(0);

        //seekBar滑动事件监听
        mFaceSurgeryFaceShapeSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public int value;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                value = i;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int strength = value;//(int)(value*(float)1.0/100);
                XJGArSdkApi.XJGARSDKSetThinChinParam(strength);
            }
        });
        mFaceSurgeryBigEyeSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public int value;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                value = i;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int strength = value;//(int)(value*(float)1.0/100);
                XJGArSdkApi.XJGARSDKSetBigEyeParam(strength);
            }
        });
        mSkinSmoothSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public int value;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                value = i;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int level = value;//(int)(value/18);
                XJGArSdkApi.XJGARSDKSetSkinSmoothParam(level);
//                GPUCamImgOperator.setBeautyLevel(level);
            }
        });
        mSkinWihtenSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public int value;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                value = i;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int level = value;//(int)(value/18);
                XJGArSdkApi.XJGARSDKSetWhiteSkinParam(level);
//                GPUCamImgOperator.setBeautyLevel(level);
            }
        });
        mRedFaceSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public int value;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                value = i;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int level = value;//(int)(value/18);
                XJGArSdkApi.XJGARSDKSetRedFaceParam(level);
//                GPUCamImgOperator.setBeautyLevel(level);
            }
        });

        /*
         * 四咸
         * */

        mFilterLayout = (LinearLayout)findViewById(R.id.layout_filter);
        mStickerLayout = (LinearLayout)findViewById(R.id.layout_sticker);
        mFilterListView = (RecyclerView) findViewById(R.id.filter_listView);
        mAllStickerListView = (RecyclerView)findViewById(R.id.allsticker_listView);
        btn_shutter = (ImageView)findViewById(R.id.btn_camera_shutter);
        btn_sticker = (ImageView)findViewById(R.id.btn_camera_sticker);
        btn_album = (ImageView)findViewById(R.id.btn_album);

        findViewById(R.id.btn_camera_filter).setOnClickListener(btn_listener);
        findViewById(R.id.btn_camera_shutter).setOnClickListener(btn_listener);
        findViewById(R.id.btn_camera_switch).setOnClickListener(btn_listener);
        findViewById(R.id.btn_camera_beauty).setOnClickListener(btn_listener);
        findViewById(R.id.btn_camera_audio).setOnClickListener(btn_listener);
        findViewById(R.id.btn_sticker_latest).setOnClickListener(btn_listener);
        findViewById(R.id.btn_camera_sticker).setOnClickListener(btn_listener);
        findViewById(R.id.btn_camera_filter1).setOnClickListener(btn_listener);
        findViewById(R.id.btn_camera_lip).setOnClickListener(btn_listener);
        findViewById(R.id.btn_album).setOnClickListener(btn_listener);
        findViewById(R.id.btn_camera_lip1).setOnClickListener(btn_listener);

        mFaceSurgery = (LinearLayout)findViewById(R.id.faceSurgery);

        // 右边滤镜模块的上滑面板
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mFilterListView.setLayoutManager(linearLayoutManager);
        mAdapter = new FilterAdapter(this, types);
        mFilterListView.setAdapter(mAdapter);
        mAdapter.setOnFilterChangeListener(onFilterChangeListener);

        // 左边语音识别挂件模块的上滑面板
        GridLayoutManager audio_linearLayoutMag = new GridLayoutManager(this,5);
        audio_linearLayoutMag.setOrientation(GridLayoutManager.VERTICAL);
        mAllStickerListView.setLayoutManager(audio_linearLayoutMag);
        mAllStickerListView.setHasFixedSize(true);
        AudioAdapter adapter = new AudioAdapter(this, audio_sticker_List);
        mAllStickerListView.setAdapter(adapter);
        adapter.setOnAudioChangeListener(onAudioChaneListener);


        // 这是相机界面
        Point screenSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);
        MagicCameraView cameraView = (MagicCameraView)findViewById(R.id.glsurfaceview_camera);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) cameraView.getLayoutParams();
        params.width = screenSize.x;
        params.height = screenSize.y;
        cameraView.setLayoutParams(params);

        /*###########*/
        mHandler = new Handler(){
            public void handleMessage (Message msg) {//此方法在ui线程运行
                switch (msg.what){
                    case 1:
                        iv_show.setImageBitmap((Bitmap) msg.obj);
                        iv_show.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        iv_show.setVisibility(View.INVISIBLE);
                }
            }
        };
        cameraView.setHandler(mHandler);
        cameraView.setStickerView(stickerView);
        cameraView.setIv_show(iv_show);
        cameraView.setGroup(group);
        /*###########*/




        // 下面是在判断模式滑动切换事件
        mModeListView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void changeModeToVideo() {
                VibratorUtil.Vibrate(CameraActivity.this, 100);   //震动100ms
                mode = MODE_VIDEO;
                ((ImageView) findViewById(R.id.btn_camera_shutter)).setImageResource(R.drawable.btn_camera_video_shutter);
            }
            @Override
            public void changeModeToPhoto() {
                VibratorUtil.Vibrate(CameraActivity.this, 100);   //震动100ms
                mode = MODE_PIC;
                ((ImageView) findViewById(R.id.btn_camera_shutter)).setImageResource(R.drawable.btn_camera_shutter);
            }
        });


        //这是录制按钮的动画
        animator = ObjectAnimator.ofFloat(btn_shutter,"rotation",0,360);
        animator.setDuration(500);
        animator.setRepeatCount(ValueAnimator.INFINITE);
    }

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
                findViewById(R.id.btn_camera_sticker).setVisibility(View.GONE);
                findViewById(R.id.btn_camera_filter).setVisibility(View.GONE);
                findViewById(R.id.point_show).setVisibility(View.GONE);
                ObjectAnimator animator_shutter_scalex = ObjectAnimator.ofFloat(findViewById(R.id.btn_camera_shutter),"scaleX", 1.0f, 0.7f);
                ObjectAnimator animator_shutter_scaley = ObjectAnimator.ofFloat(findViewById(R.id.btn_camera_shutter),"scaleY", 1.0f, 0.7f);
                animator_shutter_scaley.setDuration(200);
                animator_shutter_scalex.setDuration(200);
                animator_shutter_scalex.start();
                animator_shutter_scaley.start();
                ObjectAnimator animator_shutter_trany = ObjectAnimator.ofFloat(findViewById(R.id.btn_camera_shutter), "translationY", 150, 100);
                animator_shutter_trany.setDuration(200);
                animator_shutter_trany.start();
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
                    findViewById(R.id.btn_camera_sticker).setVisibility(View.VISIBLE);
                    findViewById(R.id.btn_camera_filter).setVisibility(View.VISIBLE);
                    findViewById(R.id.point_show).setVisibility(View.VISIBLE);
                    ObjectAnimator animator_shutter_scalex = ObjectAnimator.ofFloat(findViewById(R.id.btn_camera_shutter),"scaleX", 0.7f, 1.0f);
                    ObjectAnimator animator_shutter_scaley = ObjectAnimator.ofFloat(findViewById(R.id.btn_camera_shutter),"scaleY", 0.7f, 1.0f);
                    animator_shutter_scaley.setDuration(100);
                    animator_shutter_scalex.setDuration(100);
                    animator_shutter_scalex.start();
                    animator_shutter_scaley.start();
                    ObjectAnimator animator_shutter_trany = ObjectAnimator.ofFloat(findViewById(R.id.btn_camera_shutter), "translationY", 80, 0);
                    animator_shutter_trany.setDuration(100);
                    animator_shutter_trany.start();

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
                    findViewById(R.id.btn_camera_shutter).setClickable(true);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    // TODO Auto-generated method stub
                    findViewById(R.id.btn_camera_shutter).setClickable(true);
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
        mStickerLayout.setVisibility(View.VISIBLE);
        ObjectAnimator animator = ObjectAnimator.ofFloat(mStickerLayout, "translationY",mStickerLayout.getHeight()+1000, 1280);
        animator.setDuration(200);
        animator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                mStickerLayout.setVisibility(View.VISIBLE);
                findViewById(R.id.btn_camera_sticker).setVisibility(View.GONE);
                findViewById(R.id.btn_camera_filter).setVisibility(View.GONE);
                findViewById(R.id.point_show).setVisibility(View.GONE);
                ObjectAnimator animator_shutter_scalex = ObjectAnimator.ofFloat(btn_shutter,"scaleX", 1.0f, 0.7f);
                ObjectAnimator animator_shutter_scaley = ObjectAnimator.ofFloat(btn_shutter,"scaleY", 1.0f, 0.7f);
                animator_shutter_scaley.setDuration(200);
                animator_shutter_scalex.setDuration(200);
                animator_shutter_scalex.start();
                animator_shutter_scaley.start();
                ObjectAnimator animator_shutter_trany = ObjectAnimator.ofFloat(btn_shutter, "translationY", 150, 100);
                animator_shutter_trany.setDuration(200);
                animator_shutter_trany.start();
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
                    findViewById(R.id.btn_camera_sticker).setVisibility(View.VISIBLE);
                    findViewById(R.id.btn_camera_filter).setVisibility(View.VISIBLE);
                    findViewById(R.id.point_show).setVisibility(View.VISIBLE);
                    ObjectAnimator animator_shutter_scalex = ObjectAnimator.ofFloat(btn_shutter,"scaleX", 0.7f, 1.0f);
                    ObjectAnimator animator_shutter_scaley = ObjectAnimator.ofFloat(btn_shutter,"scaleY", 0.7f, 1.0f);
                    animator_shutter_scaley.setDuration(100);
                    animator_shutter_scalex.setDuration(100);
                    animator_shutter_scalex.start();
                    animator_shutter_scaley.start();
                    ObjectAnimator animator_shutter_trany = ObjectAnimator.ofFloat(btn_shutter, "translationY", 80, 0);
                    animator_shutter_trany.setDuration(100);
                    animator_shutter_trany.start();

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
                    findViewById(R.id.btn_camera_shutter).setClickable(true);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    // TODO Auto-generated method stub
                    findViewById(R.id.btn_camera_shutter).setClickable(true);
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
        System.out.println("你个大笨蛋！！！！！！！！！！！！");
        hideFilters();
        hideStickers();
        findViewById(R.id.mode_select_listView).setVisibility(View.VISIBLE);
        return true;
    }




    private FilterAdapter.onFilterChangeListener onFilterChangeListener = new FilterAdapter.onFilterChangeListener(){
        @Override
        public void onFilterChanged(MagicFilterType filterType) {
            magicEngine.setFilter(filterType);
        }
    };
    //特效条显示切换设置
    void setBarVisibility(String name){
        if(name.equals("红润")){
            mRedFaceSeek.setVisibility(View.VISIBLE);
            mSkinWihtenSeek.setVisibility(View.GONE);
            mSkinSmoothSeek.setVisibility(View.GONE);
            mFaceSurgeryBigEyeSeek.setVisibility(View.GONE);
            mFaceSurgeryFaceShapeSeek.setVisibility(View.GONE);
        }
        if(name.equals("美白")){
            mRedFaceSeek.setVisibility(View.GONE);
            mSkinWihtenSeek.setVisibility(View.VISIBLE);
            mSkinSmoothSeek.setVisibility(View.GONE);
            mFaceSurgeryBigEyeSeek.setVisibility(View.GONE);
            mFaceSurgeryFaceShapeSeek.setVisibility(View.GONE);
        }
        if(name.equals("磨皮")){
            mRedFaceSeek.setVisibility(View.GONE);
            mSkinWihtenSeek.setVisibility(View.GONE);
            mSkinSmoothSeek.setVisibility(View.VISIBLE);
            mFaceSurgeryBigEyeSeek.setVisibility(View.GONE);
            mFaceSurgeryFaceShapeSeek.setVisibility(View.GONE);
        }
        if(name.equals("瘦脸")){
            mRedFaceSeek.setVisibility(View.GONE);
            mSkinWihtenSeek.setVisibility(View.GONE);
            mSkinSmoothSeek.setVisibility(View.GONE);
            mFaceSurgeryBigEyeSeek.setVisibility(View.GONE);
            mFaceSurgeryFaceShapeSeek.setVisibility(View.VISIBLE);
        }
        if(name.equals("大眼")){
            mRedFaceSeek.setVisibility(View.GONE);
            mSkinWihtenSeek.setVisibility(View.GONE);
            mSkinSmoothSeek.setVisibility(View.GONE);
            mFaceSurgeryBigEyeSeek.setVisibility(View.VISIBLE);
            mFaceSurgeryFaceShapeSeek.setVisibility(View.GONE);
        }
    }


    private AudioAdapter.onAudioChangeListener onAudioChaneListener = new AudioAdapter.onAudioChangeListener() {
        @Override
        public void onAudioChanged(AudioItemType audioItemType) {
            //这个开始监听语音信息
            audio_item_state.clear();
            switch (audioItemType){
                case NONE:
                    NONE_audio_state = true;
                    SAYHI_audio_state = false;
                    CUSTOM_audio_state = false;
                    JIAYOU_audio_state = false;
                    QIEZI_audio_state = false;
                    BANG_audio_state = false;
                    break;
                case CUSTOM:
                    CUSTOM_audio_state = true;
                    SAYHI_audio_state = false;
                    NONE_audio_state = false;
                    JIAYOU_audio_state = false;
                    QIEZI_audio_state = false;
                    BANG_audio_state = false;
                    break;
                case SAYHI:
                    SAYHI_audio_state = true;
                    NONE_audio_state = false;
                    CUSTOM_audio_state = false;
                    JIAYOU_audio_state = false;
                    QIEZI_audio_state = false;
                    BANG_audio_state = false;
                    break;
                case JIAYOU:
                    SAYHI_audio_state = false;
                    NONE_audio_state = false;
                    CUSTOM_audio_state = false;
                    JIAYOU_audio_state = true;
                    QIEZI_audio_state = false;
                    BANG_audio_state = false;
                case QIEZI:
                    SAYHI_audio_state = false;
                    NONE_audio_state = false;
                    CUSTOM_audio_state = false;
                    JIAYOU_audio_state = false;
                    QIEZI_audio_state = true;
                    BANG_audio_state = false;
                case BANG:
                    SAYHI_audio_state = false;
                    NONE_audio_state = false;
                    CUSTOM_audio_state = false;
                    JIAYOU_audio_state = false;
                    QIEZI_audio_state = false;
                    BANG_audio_state = true;
                default:
                    break;
            }
            //存储语音挂件的状态（语音识别）
            audio_item_state.add(NONE_audio_state);
            audio_item_state.add(CUSTOM_audio_state);
            audio_item_state.add(SAYHI_audio_state);
            audio_item_state.add(JIAYOU_audio_state);
            audio_item_state.add(QIEZI_audio_state);
            audio_item_state.add(BANG_audio_state);
            if (SAYHI_audio_state == true){
                DictationUtil.startSpeechListener(CameraActivity.this, audioItemType, audio_item_state, new DictationListener() {
                    @Override
                    public void onDictationListener(String dictationResultStr) {
                       if (SAYHI_audio_state == true && dictationResultStr.equals("hello") == true){//表示当前要监听“哈喽”
                           if(mode == MODE_PIC){
                               Toast.makeText(getApplicationContext(), "拍照成功", Toast.LENGTH_LONG).show();//click第二次结束录像的交互
                               takePhoto();
                               Toast.makeText(getApplicationContext(), "拍照成功", Toast.LENGTH_LONG).show();//click第二次结束录像的交互
                           }
                            else{
                                takeVideo();
                            }
                        }
                    }
                });
            }
            else if (JIAYOU_audio_state == true){
                DictationUtil.startSpeechListener(CameraActivity.this, audioItemType, audio_item_state, new DictationListener() {
                    @Override
                    public void onDictationListener(String dictationResultStr) {
                        if (SAYHI_audio_state == true && dictationResultStr.equals("加油") == true){//表示当前要监听“哈喽”
                            if(mode == MODE_PIC){
                                Toast.makeText(getApplicationContext(), "拍照成功", Toast.LENGTH_LONG).show();//click第二次结束录像的交互
                                takePhoto();
                                Toast.makeText(getApplicationContext(), "拍照成功", Toast.LENGTH_LONG).show();//click第二次结束录像的交互
                            }
                            else{
                                takeVideo();
                            }
                        }
                    }
                });
            }
            else if (QIEZI_audio_state == true){
                DictationUtil.startSpeechListener(CameraActivity.this, audioItemType, audio_item_state, new DictationListener() {
                    @Override
                    public void onDictationListener(String dictationResultStr) {
                        if (SAYHI_audio_state == true && dictationResultStr.equals("茄子") == true) {//表示当前要监听“哈喽”
                            if (mode == MODE_PIC){
                                Toast.makeText(getApplicationContext(), "拍照成功", Toast.LENGTH_LONG).show();//click第二次结束录像的交互
                                takePhoto();
                                Toast.makeText(getApplicationContext(), "拍照成功", Toast.LENGTH_LONG).show();//click第二次结束录像的交互
                            }
                            else{
                                takeVideo();
                            }
                        }
                    }
                });
            }
            else if (BANG_audio_state == true){
                DictationUtil.startSpeechListener(CameraActivity.this, audioItemType, audio_item_state, new DictationListener() {
                    @Override
                    public void onDictationListener(String dictationResultStr) {
                        if (SAYHI_audio_state == true && dictationResultStr.equals("我最棒") == true){//表示当前要监听“哈喽”
                            if(mode == MODE_PIC){
                                Toast.makeText(getApplicationContext(), "拍照成功", Toast.LENGTH_LONG).show();//click第二次结束录像的交互
                                takePhoto();
                                Toast.makeText(getApplicationContext(), "拍照成功", Toast.LENGTH_LONG).show();//click第二次结束录像的交互
                            }
                            else{
                                takeVideo();
                            }
                        }
                    }
                });
            }
            else if (CUSTOM_audio_state == true){
                //表示当前要监听自定义内容
                showInputDialog();
            }
        }
    };

    public void showInputDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_edit_audio);
        builder.setTitle("请输入唤醒语音");
        final EditText et=new EditText(CameraActivity.this);
        et.setSingleLine();
        et.setHint("請輸入文本");
        builder.setView(et);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final String res = et.getText().toString();
                DictationUtil.startSpeechListener(CameraActivity.this, AudioItemType.CUSTOM, audio_item_state, new DictationListener() {
                    @Override
                    public void onDictationListener(String dictationResultStr) {
                        if (dictationResultStr.equals(res) == true){//表示当前要监听自定义内容
                            if(mode == MODE_PIC){
                                Toast.makeText(getApplicationContext(), "拍照成功", Toast.LENGTH_LONG).show();//click第二次结束录像的交互
                                takePhoto();
                                Toast.makeText(getApplicationContext(), "拍照成功", Toast.LENGTH_LONG).show();//click第二次结束录像的交互
                            }
                            else{
                                takeVideo();
                            }
                        }
                    }
                });
            }
        });
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    private View.OnClickListener btn_listener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_camera_filter:
                    findViewById(R.id.btn_camera_filter1).setBackgroundResource(R.drawable.btn_camera_filter_small_pressed);
                    findViewById(R.id.mode_select_listView).setVisibility(View.GONE);
                    findViewById(R.id.btn_camera_lip).setBackgroundResource(R.drawable.btn_camera_lip);
                    findViewById(R.id.btn_camera_beauty).setBackgroundResource(R.drawable.btn_camera_beauty);
                    showFilters();
                    break;
                case R.id.btn_camera_filter1:
                    //去掉整形的seekerBar
                    mRedFaceSeek.setVisibility(View.GONE);
                    mSkinWihtenSeek.setVisibility(View.GONE);
                    mSkinSmoothSeek.setVisibility(View.GONE);
                    mFaceSurgeryBigEyeSeek.setVisibility(View.GONE);
                    mFaceSurgeryFaceShapeSeek.setVisibility(View.GONE);

                    findViewById(R.id.btn_camera_filter1).setBackgroundResource(R.drawable.btn_camera_filter_small_pressed);
                    findViewById(R.id.btn_camera_lip).setBackgroundResource(R.drawable.btn_camera_lip);
                    findViewById(R.id.btn_camera_beauty).setBackgroundResource(R.drawable.btn_camera_beauty);
                    mAdapter = new FilterAdapter(CameraActivity.this, types);
                    mFilterListView.setAdapter(mAdapter);
                    mAdapter.setOnFilterChangeListener(onFilterChangeListener);
                    break;
                case R.id.btn_camera_switch:
                    magicEngine.switchCamera(stickerView);//?/?????
                    break;
                case R.id.btn_camera_beauty:

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CameraActivity.this);
                    linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                    mFilterListView.setLayoutManager(linearLayoutManager);
                    beautyAdapter = new MenuAdapter(CameraActivity.this, mSurgeryData);
                    mFilterListView.setAdapter(beautyAdapter);
                    //beautyAdapter.setOnBeautyChangeListener(onBeautyChangeListener);
                    beautyAdapter.setOnClickListener(btn_beauty_listener);
                    findViewById(R.id.btn_camera_beauty).setBackgroundResource(R.drawable.btn_camera_beauty_pressed);
                    findViewById(R.id.btn_camera_filter1).setBackgroundResource(R.drawable.btn_camera_filter_small);

                    /*new AlertDialog.Builder(CameraActivity.this)
                            .setSingleChoiceItems(new String[] { "关闭", "1", "2", "3", "4", "5"}, MagicParams.beautyLevel,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            magicEngine.setBeautyLevel(which);
                                            dialog.dismiss();
                                        }
                                    })
                            .setNegativeButton("取消", null)
                            .show();
                    */

                    break;
                case R.id.btn_camera_sticker:
                    findViewById(R.id.mode_select_listView).setVisibility(View.GONE);
                    showStickers();
                    break;
                case R.id.btn_camera_lip:
                    //去掉整形的seekerBar
                    mRedFaceSeek.setVisibility(View.GONE);
                    mSkinWihtenSeek.setVisibility(View.GONE);
                    mSkinSmoothSeek.setVisibility(View.GONE);
                    mFaceSurgeryBigEyeSeek.setVisibility(View.GONE);
                    mFaceSurgeryFaceShapeSeek.setVisibility(View.GONE);
                    //恢复shutter位置
                   // ObjectAnimator animator_shutter_trany3 = ObjectAnimator.ofFloat(findViewById(R.id.btn_camera_shutter), "translationY", 160, 140);//150 100
                   // animator_shutter_trany3.setDuration(200);
                   // animator_shutter_trany3.start();

                    findViewById(R.id.btn_camera_lip).setBackgroundResource(R.drawable.btn_camera_lip_pressed);
                    findViewById(R.id.btn_camera_filter1).setBackgroundResource(R.drawable.btn_camera_filter_small);
                    findViewById(R.id.btn_camera_beauty).setBackgroundResource(R.drawable.btn_camera_beauty);
                    break;
                case R.id.btn_camera_shutter:
                    if (PermissionChecker.checkSelfPermission(CameraActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED) {
                        ActivityCompat.requestPermissions(CameraActivity.this, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                                v.getId());
                    } else {
                        if(mode == MODE_PIC)
                            takePhoto();
                        else {

                            circle = (ImageView)findViewById(R.id.video_point_show);

                            circle.setVisibility(View.VISIBLE);//mlistview2处于显示状态.setVisibility(View.VISIBLE);//mlistview2处于显示状态
                            final AnimationDrawable circleAnimation = (AnimationDrawable) circle.getBackground();
                            circleAnimation.start();

                            takeVideo();
                        }
                    }
                    break;
                case R.id.btn_album:
                    //调用系统相册
                    Intent intent= new Intent(Intent.ACTION_PICK,null);
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
                    startActivityForResult(intent, 0x1);
                    break;
                case R.id.btn_sticker_latest:
                    findViewById(R.id.btn_camera_audio).setBackgroundResource(R.drawable.ic_camera_audio);
                    findViewById(R.id.btn_sticker_latest).setBackgroundResource(R.drawable.ic_sticker_latest_pressed);
                    findViewById(R.id.btn_camera_lip1).setBackgroundResource(R.drawable.ic_lip);
                    GridLayoutManager sticker_gridLayoutMag = new GridLayoutManager(CameraActivity.this,6);
                    sticker_gridLayoutMag.setOrientation(GridLayoutManager.VERTICAL);
                    mAllStickerListView.setLayoutManager(sticker_gridLayoutMag);
                    mAllStickerListView.setHasFixedSize(true);
                    StickerAdapter adapter = new StickerAdapter(CameraActivity.this, sticker_List); // 这里用于切换
                    mAllStickerListView.setAdapter(adapter);
                    adapter.setOnStickerChangeListener(onStickerChaneListener);
                    break;
                case R.id.btn_camera_audio:
                    findViewById(R.id.btn_camera_audio).setBackgroundResource(R.drawable.ic_camera_audio_pressed);
                    findViewById(R.id.btn_sticker_latest).setBackgroundResource(R.drawable.ic_sticker_latest);
                    findViewById(R.id.btn_camera_lip1).setBackgroundResource(R.drawable.ic_lip);
                    // 左边语音识别挂件模块的上滑面板
                    GridLayoutManager audio_linearLayoutMag = new GridLayoutManager(CameraActivity.this,6);
                    audio_linearLayoutMag.setOrientation(GridLayoutManager.VERTICAL);
                    mAllStickerListView.setLayoutManager(audio_linearLayoutMag);
                    mAllStickerListView.setHasFixedSize(true);
                    AudioAdapter adapter1 = new AudioAdapter(CameraActivity.this, audio_sticker_List);
                    mAllStickerListView.setAdapter(adapter1);
                    adapter1.setOnAudioChangeListener(onAudioChaneListener);
                    break;
                case R.id.btn_camera_lip1:
                    findViewById(R.id.btn_camera_audio).setBackgroundResource(R.drawable.ic_camera_audio);
                    findViewById(R.id.btn_sticker_latest).setBackgroundResource(R.drawable.ic_sticker_latest);
                    findViewById(R.id.btn_camera_lip1).setBackgroundResource(R.drawable.ic_lip_pressed);
                    break;

            }
        }
    };


    private ClickUtils.OnClickListener btn_beauty_listener = new ClickUtils.OnClickListener(){
        @Override
        public void onClick(View v, int type, int pos, int child) {
            MenuBean m=mSurgeryData.get(pos);
            String name=m.name;
            beautyAdapter.checkPos=pos;
            v.setSelected(true);
            beautyAdapter.notifyDataSetChanged();
            setBarVisibility(name);
        }
    };

    private StickerAdapter.onStickerChangeListener onStickerChaneListener = new StickerAdapter.onStickerChangeListener() {
        @Override
        public void onStickerChanged(StickerItemType stickerItemType) {
            switch (stickerItemType){
                case NONE:
                    NONE_sticker_state = true;
                    CATEAR_sticker_state = false;
                    HAT_sticker_state = false;
                    RABIT_sticker_state = false;
                    break;
                case CATEAR:
                    CATEAR_sticker_state = true;
                    HAT_sticker_state = false;
                    RABIT_sticker_state = false;
                    NONE_sticker_state = false;
                    break;
                case HAT:
                    HAT_sticker_state = true;//后面引入别的state再考虑切换的问题？？？？？
                    NONE_sticker_state = false;
                    CATEAR_sticker_state = false;
                    RABIT_sticker_state = false;
                    break;
                case RABIT:
                    RABIT_sticker_state = true;//后面引入别的state再考虑切换的问题？？？？？
                    NONE_sticker_state = false;
                    CATEAR_sticker_state = false;
                    HAT_sticker_state = false;
                    break;
                default:
                    break;
            }

            if (CATEAR_sticker_state == true){
                Bitmap tmp_bitmap = BitmapFactory.decodeResource(getResources(),
                        R.drawable.catear_real);
                stickerView.setWaterMark(tmp_bitmap,"catear");
            }
            else if(HAT_sticker_state == true){
                Bitmap tmp_bitmap = BitmapFactory.decodeResource(getResources(),
                        R.drawable.hat_real);
                stickerView.setWaterMark(tmp_bitmap,"hat");
            }
            else if (RABIT_sticker_state == true){
                Bitmap tmp_bitmap = BitmapFactory.decodeResource(getResources(),
                        R.drawable.rabit_real);
                stickerView.setWaterMark(tmp_bitmap,"rabit");
            }
            else if (NONE_sticker_state == true){
                //stickerView.releasePointerCapture();
            }

        }
    };


    private  Bitmap readFromAssets(String filename){
        Bitmap bitmap;
        AssetManager asm=getAssets();
        try {
            InputStream is=asm.open(filename);
            bitmap= BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            Log.e("MainActivity","[*]failed to open "+filename);
            e.printStackTrace();
            return null;
        }
        return Utils.copyBitmap(bitmap); //返回mutable的image
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        bitmap=readFromAssets("ic_blank.png");//这里有点像初始化
        // TODO Auto-generated method stub
        if(data==null)return;
        try {
            String picturePath = GetAlbumPic.getRealPathFromUri(this, data.getData());
            Uri uri;
            //获取到用户所选图片的Uri
            uri = data.getData();
            Log.e("dataUri",uri.toString());
            if (picturePath!=null){
                Intent intent = new Intent(CameraActivity.this, AlbumActivity.class);
                intent.putExtra("picturePath",picturePath);
                //intent.putExtra("dataUri",uri.toString());
                magicEngine.stop();

                startActivity(intent);
            }
        }catch (Exception e){
            Log.d("MainActivity","[*]"+e);
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * 权限申请返回结果
     * @param requestCode 请求码
     * @param permissions 权限数组
     * @param grantResults  申请结果数组，里面都是int类型的数
     */
    /*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){ //同意权限申请
                    if(mode == MODE_PIC)
                        takePhoto();
                    else{
                        takeVideo();
                    }
                }else { //拒绝权限申请
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
                break;
            default:
                break;
        }
    }*/




    private void takePhoto(){
       magicEngine.savePicture(getOutputMediaFile(),null);
    }

    private void takeVideo(){
        if(isRecording) {
            //animator.end();//控制按钮的旋转而已
            magicEngine.stopRecord();
            //GPUCamImgOperator.stopRecord();
            circle.setVisibility(View.INVISIBLE);
            Toast.makeText(getApplicationContext(), "视频已保存", Toast.LENGTH_LONG).show();//click第二次结束录像的交互
        }else {
            //animator.start();
            magicEngine.startRecord();
            //GPUCamImgOperator.startRecord();
        }
        isRecording = !isRecording;
    }




    public File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MagicCamera");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINESE).format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }
}
