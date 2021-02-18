package com.seu.magiccamera.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.seu.magiccamera.R;
import com.seu.magiccamera.helper.ShareToolUtil;

public class ShowPhoActivity extends Activity{

    private ImageView btn_return;
    private ImageView btn_share;
    private ImageView btn_share1;
    private Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_pho);

        Intent intent = getIntent();//getIntent将该项目中包含的原始intent检索出来，将检索出来的intent赋值给一个Intent类型的变量intent
        if (intent != null) {
            String picPath = intent.getStringExtra("picPath");
            bitmap = BitmapFactory.decodeFile(picPath);
            int picWidth = bitmap.getWidth();
            int picHeight = bitmap.getHeight();
            // 获取屏幕信息
            Point screenSize = new Point();
            getWindowManager().getDefaultDisplay().getSize(screenSize);
            int screenWidth = screenSize.x;
            int screeHeight = screenSize.y;

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) ((ImageView) findViewById(R.id.image2Photo1)).getLayoutParams(); //获取当前控件的布局对象
            if ((screenWidth * picHeight) / picWidth > screeHeight * 10 / 11) {
                params.height = screeHeight * 10 / 11;//设置当前控件布局的高度
                findViewById(R.id.image2Photo1).setLayoutParams(params);//将设置好的布局参数应用到控件中
            } else {
                params.height = (screenWidth * picHeight) / picWidth;//设置当前控件布局的高度
                findViewById(R.id.image2Photo1).setLayoutParams(params);//将设置好的布局参数应用到控件中
            }
            ((ImageView) findViewById(R.id.image2Photo1)).setImageBitmap(bitmap);
        }
        initView();
    }

    private void initView(){
        btn_return = (ImageView)findViewById(R.id.btn_return_showpage);
        findViewById(R.id.btn_return_showpage).setOnClickListener(btn_listener);
        btn_share = findViewById(R.id.btn_share);
        btn_share1 = findViewById(R.id.btn_share1);
        btn_share.setOnClickListener(btn_listener);
        btn_share1.setOnClickListener(btn_listener);
    }

    private View.OnClickListener btn_listener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_return_showpage:
                    Intent intent = new Intent(ShowPhoActivity.this, CameraActivity.class);
                    startActivity(intent);
                    break;
                case R.id.btn_share:
                    ShareToolUtil.shareWechatMoment(ShowPhoActivity.this,"分享到朋友圈",bitmap);
                    break;
                case R.id.btn_share1:
                    ShareToolUtil.shareWechatFriend(ShowPhoActivity.this,"分享给好友",bitmap);
                    break;
                default:
                     break;
            }
        }
    };
}
