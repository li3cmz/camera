package com.seu.magiccamera.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.seu.magiccamera.R;
import com.seu.magiccamera.adapter.AudioAdapter;
import com.seu.magiccamera.adapter.FilterAdapter;
import com.seu.magiccamera.adapter.ModeAdapter;
import com.seu.magiccamera.helper.AudioItemType;
import com.seu.magiccamera.helper.DictationListener;
import com.seu.magiccamera.helper.DictationUtil;
import com.seu.magiccamera.widget.EndlessRecyclerOnScrollListener;
import com.seu.magicfilter.MagicEngine;
import com.seu.magicfilter.filter.helper.MagicFilterType;
import com.seu.magicfilter.utils.MagicParams;
import com.seu.magicfilter.widget.MagicCameraView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by why8222 on 2016/3/17.
 */
public class CameraActivity extends Activity {
    private LinearLayout mFilterLayout;
    private RecyclerView mFilterListView;
    private LinearLayout mStickerLayout;
    private RecyclerView mAudioListView;
    private RecyclerView mModeListView;

    private FilterAdapter mAdapter;
    private ModeAdapter modeAdapter;

    private LinearLayoutManager manager;
    private MagicEngine magicEngine;
    private ObjectAnimator animator;

    private final int MODE_PIC = 1;
    private final int MODE_VIDEO = 2;
    private int mode = MODE_PIC;

    private ImageView btn_shutter;
    private ImageView btn_sticker;

    private boolean filter_closedOrNot;
    private boolean sticker_closedOrNot;
    private boolean isRecording = false;

    private boolean NONE_audio_state = true;
    private boolean CUSTOM_audio_state = false;
    private boolean SAYHI_audio_state = false;


    private ArrayList<Boolean>audio_item_state = new ArrayList<>();
    private List<String> mList = new ArrayList<>();

    private final AudioItemType[] audio_sticker_List = new AudioItemType[]{
            AudioItemType.NONE,
            AudioItemType.CUSTOM,
            AudioItemType.SAYHI,
            AudioItemType.NONE,
            AudioItemType.NONE,
            AudioItemType.NONE,
            AudioItemType.NONE,
            AudioItemType.NONE,
            AudioItemType.NONE,
            AudioItemType.NONE,
            AudioItemType.NONE,
            AudioItemType.NONE,
            AudioItemType.NONE,
            AudioItemType.NONE,
            AudioItemType.NONE,
            AudioItemType.NONE,
            AudioItemType.NONE,
            AudioItemType.NONE,
            AudioItemType.NONE,
            AudioItemType.NONE,
            AudioItemType.NONE,
            AudioItemType.NONE,
            AudioItemType.NONE,
            AudioItemType.NONE,
            AudioItemType.NONE,
            AudioItemType.NONE,
            AudioItemType.NONE,
            AudioItemType.NONE,
            AudioItemType.NONE,
            AudioItemType.NONE,
            AudioItemType.NONE,
            AudioItemType.NONE,
            AudioItemType.NONE,
            AudioItemType.NONE,
            AudioItemType.NONE,
            AudioItemType.NONE,
            AudioItemType.NONE,
            AudioItemType.NONE,
    };
    private final MagicFilterType[] types = new MagicFilterType[]{
            MagicFilterType.NONE,
            MagicFilterType.FAIRYTALE,
            MagicFilterType.SUNRISE,
            MagicFilterType.SUNSET,
            MagicFilterType.WHITECAT,
            MagicFilterType.BLACKCAT,
            MagicFilterType.SKINWHITEN,
            MagicFilterType.HEALTHY,
            MagicFilterType.SWEETS,
            MagicFilterType.ROMANCE,
            MagicFilterType.SAKURA,
            MagicFilterType.WARM,
            MagicFilterType.ANTIQUE,
            MagicFilterType.NOSTALGIA,
            MagicFilterType.CALM,
            MagicFilterType.LATTE,
            MagicFilterType.TENDER,
            MagicFilterType.COOL,
            MagicFilterType.EMERALD,
            MagicFilterType.EVERGREEN,
            MagicFilterType.CRAYON,
            MagicFilterType.SKETCH,
            MagicFilterType.AMARO,
            MagicFilterType.BRANNAN,
            MagicFilterType.BROOKLYN,
            MagicFilterType.EARLYBIRD,
            MagicFilterType.FREUD,
            MagicFilterType.HEFE,
            MagicFilterType.HUDSON,
            MagicFilterType.INKWELL,
            MagicFilterType.KEVIN,
            MagicFilterType.LOMO,
            MagicFilterType.N1977,
            MagicFilterType.NASHVILLE,
            MagicFilterType.PIXAR,
            MagicFilterType.RISE,
            MagicFilterType.SIERRA,
            MagicFilterType.SUTRO,
            MagicFilterType.TOASTER2,
            MagicFilterType.VALENCIA,
            MagicFilterType.WALDEN,
            MagicFilterType.XPROII
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_camera);
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
    }


    private void initView(){
        mFilterLayout = (LinearLayout)findViewById(R.id.layout_filter);
        mStickerLayout = (LinearLayout)findViewById(R.id.layout_sticker);
        mFilterListView = (RecyclerView) findViewById(R.id.filter_listView);
        mAudioListView = (RecyclerView)findViewById(R.id.audio_listView);
        btn_shutter = (ImageView)findViewById(R.id.btn_camera_shutter);
        btn_sticker = (ImageView)findViewById(R.id.btn_camera_sticker);

        findViewById(R.id.btn_camera_filter).setOnClickListener(btn_listener);
        findViewById(R.id.btn_camera_sticker).setOnClickListener(btn_listener);
        findViewById(R.id.btn_camera_shutter).setOnClickListener(btn_listener);
        findViewById(R.id.btn_camera_switch).setOnClickListener(btn_listener);
        findViewById(R.id.btn_camera_beauty).setOnClickListener(btn_listener);
        findViewById(R.id.btn_camera_sticker).setOnClickListener(btn_listener);
        findViewById(R.id.btn_camera_filter1).setOnClickListener(btn_listener);
        findViewById(R.id.btn_camera_lip).setOnClickListener(btn_listener);

        // 右边滤镜模块的上滑面板
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mFilterListView.setLayoutManager(linearLayoutManager);
        mAdapter = new FilterAdapter(this, types);
        mFilterListView.setAdapter(mAdapter);
        mAdapter.setOnFilterChangeListener(onFilterChangeListener);

        // 左边语音识别挂件模块的上滑面板
        GridLayoutManager audio_linearLayoutMag = new GridLayoutManager(this,6);
        audio_linearLayoutMag.setOrientation(GridLayoutManager.VERTICAL);
        mAudioListView.setLayoutManager(audio_linearLayoutMag);
        mAudioListView.setHasFixedSize(true);
        AudioAdapter adapter = new AudioAdapter(this, audio_sticker_List);
        mAudioListView.setAdapter(adapter);
        adapter.setOnAudioChangeListener(onAudioChaneListener);


        // 这是相机界面
        Point screenSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);
        MagicCameraView cameraView = (MagicCameraView)findViewById(R.id.glsurfaceview_camera);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) cameraView.getLayoutParams();
        params.width = screenSize.x;
        params.height = screenSize.y;
        cameraView.setLayoutParams(params);


        // 下面是在判断模式滑动切换事件
        mModeListView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void changeModeToVideo() {
                mode = MODE_VIDEO;
                ((ImageView) findViewById(R.id.btn_camera_shutter)).setImageResource(R.drawable.btn_camera_video_shutter);
            }
            @Override
            public void changeModeToPhoto() {
                mode = MODE_PIC;
                ((ImageView) findViewById(R.id.btn_camera_shutter)).setImageResource(R.drawable.btn_camera_shutter);
                // 当前位置1在中间
                /*
                curClicks.set(0,false);
                curClicks.set(1,true);
                curClicks.set(2,false);
                curClicks.set(3,false);
                modeAdapter.setIsClicks(curClicks);*/
            }
        });


        //这是录制按钮的动画
        animator = ObjectAnimator.ofFloat(btn_shutter,"rotation",0,360);
        animator.setDuration(500);
        animator.setRepeatCount(ValueAnimator.INFINITE);
    }

    // 检测在上滑动板出现的情况下的屏幕点击事件
    public boolean onTouchEvent(MotionEvent event){
        if (event.getAction() != MotionEvent.ACTION_DOWN) {//MotionEvent.ACTION_MOVE
            return false;
        }
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
                    break;
                case CUSTOM:
                    CUSTOM_audio_state = true;
                    SAYHI_audio_state = false;
                    NONE_audio_state = false;
                    break;
                case SAYHI:
                    SAYHI_audio_state = true;//后面引入别的state再考虑切换的问题？？？？？
                    NONE_audio_state = false;
                    CUSTOM_audio_state = false;
                    break;
                default:
                    break;
            }
            //存储语音挂件的状态（语音识别）
            audio_item_state.add(NONE_audio_state);
            audio_item_state.add(CUSTOM_audio_state);
            audio_item_state.add(SAYHI_audio_state);
            if (NONE_audio_state!=true && CUSTOM_audio_state!=true){
                DictationUtil.startSpeechListener(CameraActivity.this, audioItemType, audio_item_state, new DictationListener() {
                    @Override
                    public void onDictationListener(String dictationResultStr) {
                       if (SAYHI_audio_state == true && dictationResultStr.equals("hello") == true){//表示当前要监听“哈喽”
                            if(mode == MODE_PIC)
                                takePhoto();
                            else
                                takeVideo();
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
                            if(mode == MODE_PIC)
                                takePhoto();
                            else
                                takeVideo();
                        }
                    }
                });
            }
        });
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (grantResults.length != 1 || grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if(mode == MODE_PIC)
                takePhoto();
            else
                takeVideo();
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
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
                    findViewById(R.id.btn_camera_filter1).setBackgroundResource(R.drawable.btn_camera_filter_small_pressed);
                    findViewById(R.id.btn_camera_lip).setBackgroundResource(R.drawable.btn_camera_lip);
                    findViewById(R.id.btn_camera_beauty).setBackgroundResource(R.drawable.btn_camera_beauty);
                    break;
                case R.id.btn_camera_switch:
                    magicEngine.switchCamera();
                    break;
                case R.id.btn_camera_beauty:
                    findViewById(R.id.btn_camera_beauty).setBackgroundResource(R.drawable.btn_camera_beauty_pressed);
                    findViewById(R.id.btn_camera_filter1).setBackgroundResource(R.drawable.btn_camera_filter_small);
                    new AlertDialog.Builder(CameraActivity.this)
                            .setSingleChoiceItems(new String[] { "关闭", "1", "2", "3", "4", "5"}, MagicParams.beautyLevel,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            magicEngine.setBeautyLevel(which);
                                            dialog.dismiss();
                                        }
                                    })
                            .setNegativeButton("取消", null)
                            .show();
                    break;
                case R.id.btn_camera_sticker:
                    findViewById(R.id.mode_select_listView).setVisibility(View.GONE);
                    showStickers();
                    break;
                case R.id.btn_camera_lip:
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
                            Toast.makeText(getApplicationContext(), "我要开始录像了", Toast.LENGTH_LONG).show();//click第二次结束录像的交互
                            takeVideo();
                        }
                    }
                    break;
            }
        }
    };

    private void takePhoto(){
        magicEngine.savePicture(getOutputMediaFile(),null);
    }

    private void takeVideo(){
        if(isRecording) {
            //animator.end();//控制按钮的旋转而已
            magicEngine.stopRecord();
        }else {
            //animator.start();
            magicEngine.startRecord();
        }
        isRecording = !isRecording;
    }

    // 开启挂件面板上的东西的显示
    private void showStickers(){
        sticker_closedOrNot = true;
        findViewById(R.id.layout_sticker).setVisibility(View.VISIBLE);
        ObjectAnimator animator = ObjectAnimator.ofFloat(mStickerLayout, "translationY", mStickerLayout.getHeight()+1000, 1280);
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

    // 开启美型滤镜面板上的东西的显示
    private void showFilters(){
        //System.out.println(mFilterLayout.getHeight());
        //获取屏幕宽度和高度
        //WindowManager wm1 = this.getWindowManager();
        //int width1 = wm1.getDefaultDisplay().getWidth();
        //int height1 = wm1.getDefaultDisplay().getHeight();
        //System.out.println(height1);

        //表示当前有上滑面板
        filter_closedOrNot = true;

        findViewById(R.id.btn_camera_beauty).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_camera_filter1).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_camera_lip).setVisibility(View.VISIBLE);
        findViewById(R.id.layout_filter).setVisibility(View.VISIBLE);

        ObjectAnimator animator = ObjectAnimator.ofFloat(mFilterLayout, "translationY", mFilterLayout.getHeight()+1000, 1280);
        animator.setDuration(200);
        animator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                mFilterLayout.setVisibility(View.VISIBLE);
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

    private void hideStickers(){
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
                    findViewById(R.id.layout_sticker).setVisibility(View.GONE);
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
    private void hideFilters(){
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
                    findViewById(R.id.btn_camera_beauty).setVisibility(View.GONE);
                    findViewById(R.id.btn_camera_filter1).setVisibility(View.GONE);
                    findViewById(R.id.btn_camera_lip).setVisibility(View.GONE);
                    findViewById(R.id.layout_filter).setVisibility(View.GONE);
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
