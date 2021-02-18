package com.seu.magiccamera.helper;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ShareToolUtil {
    private static String sharePicName = "share_pic.jpg";
    private static String sharePicPath =
            Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator+"UmeBrowser"+File.separator+"sharepic"+File.separator;

    public static final String AUTHORITY = "com.ume.browser.fileprovider";

    public static final String PACKAGE_WECHAT = "com.tencent.mm";           //微信包
    public static final String PACKAGE_MOBILE_QQ = "com.tencent.mobileqq";  //手机QQ包
    public static final String PACKAGE_QZONE = "com.qzone";                 //QQ空间APP包
    public static final String PACKAGE_SINA = "com.sina.weibo";             //新浪包

    // 判断是否安装指定app
    public static boolean isInstallApp(Context context, String app_package){
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pInfo = packageManager.getInstalledPackages(0);
        if (pInfo != null) {
            for (int i = 0; i < pInfo.size(); i++) {
                String pn = pInfo.get(i).packageName;
                if (app_package.equals(pn)) {
                    return true;
                }
            }
        }
        return false;
    }

    //分享图片给微信好友
    public static void shareWechatFriend(Context context, String content , Bitmap bitmap){
        File picFile = ShareToolUtil.saveSharePic(context, bitmap);     //获取待分享的bitmap的File
        if (isInstallApp(context, PACKAGE_WECHAT)){
            Intent intent = new Intent();
            ComponentName cop = new ComponentName("com.tencent.mm","com.tencent.mm.ui.tools.ShareImgUI");
            intent.setComponent(cop);
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("image/*");
            if (picFile != null) {
                if (picFile.isFile() && picFile.exists()) {
                    Uri uri;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        uri = FileProvider.getUriForFile(context, AUTHORITY, picFile);
                    } else {
                        uri = Uri.fromFile(picFile);
                    }
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                    //intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, Uri);
                }
            }
            //intent.putExtra("Kdescription", !TextUtils.isEmpty(content) ? content : "");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            Toast.makeText(context, "分享给好友成功", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(context, "您需要安装微信客户端", Toast.LENGTH_LONG).show();
        }
    }

    //分享图片到微信朋友圈
    public static void shareWechatMoment(Context context, String content, Bitmap bitmap) {
        File picFile = ShareToolUtil.saveSharePic(context, bitmap);    //获取待分享的bitmap的File
        if (isInstallApp(context, PACKAGE_WECHAT)){
            Intent intent = new Intent();
            //分享精确到微信的页面，朋友圈页面，或者选择好友分享页面
            ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI");
            intent.setComponent(comp);
            //intent.setAction(Intent.ACTION_SEND_MULTIPLE);// 分享多张图片时使用
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("image/*");
            //添加Uri图片地址--用于添加多张图片
            //ArrayList<Uri> imageUris = new ArrayList<>();
            //intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
            if (picFile != null) {
                if (picFile.isFile() && picFile.exists()) {
                    Uri uri;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        uri = FileProvider.getUriForFile(context, AUTHORITY, picFile);
                    } else {
                        uri = Uri.fromFile(picFile);
                    }
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                }
            }
            intent.putExtra("Kdescription", !TextUtils.isEmpty(content) ? content : "");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "您需要安装微信客户端", Toast.LENGTH_LONG).show();
        }
    }

    //保存图片，并返回一个File类型的文件
    public static File saveSharePic(Context context, Bitmap bitmap){
        File dir = new File(sharePicPath);
        if (!dir.exists()){
            dir.mkdirs();
        }
        File filePic = new File(sharePicPath, sharePicName);
        if (filePic.exists()){
            filePic.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(filePic);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filePic;
    }
}