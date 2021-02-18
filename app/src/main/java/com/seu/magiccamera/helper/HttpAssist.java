package com.seu.magiccamera.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Celine on 2018/2/3.
 */

public class HttpAssist {
    private static final String TAG="uploadFile";
    private static final int TIME_OUT=10000;
    private static final String CHARSET = "utf-8"; // 设置编码
    public static final String SUCCESS = "1";
    public static final String FAILURE = "";
    private static String laboratory;

    public static String uploadFile(File file,String opra,String typeOfSt) {  //  1表示进行表情检测  2表示进行风格转换

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("operation", opra);
            jsonObject.put("style", typeOfSt);
        }
        catch (JSONException e){
            Log.d("jsonError","put errror");
        }

        Map<String, String> params = new HashMap<>();
        //设置编码类型为utf-8
        params.put("data", String.valueOf(jsonObject));

        String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
        String PREFIX = "--", LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data"; // 内容类型
        String RequestURL = "http://172.18.233.3:52480";
        String result = null;
        try {
            URL url = new URL(RequestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true); // 允许输入流
            conn.setDoOutput(true); // 允许输出流
            conn.setUseCaches(false); // 不允许使用缓存
            conn.setRequestMethod("POST"); // 请求方式
            conn.setRequestProperty("Charset", CHARSET); // 设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="
                    + BOUNDARY);


            if (file != null) {
                /**
                 * 先传参数
                 */
                //获取map对象里面的数据，并转化为string
                StringBuilder sb11 = new StringBuilder();
                System.out.println("上传名称:"+params.get("data"));
                //上传的表单参数部分，不需要更改
                for (Map.Entry<String, String> entry : params.entrySet()) {//构建表单字段内容
                    sb11.append("--");
                    sb11.append(BOUNDARY);
                    sb11.append(LINE_END);
                    sb11.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINE_END);
                    sb11.append("Content-Type: text/plain; charset=" + CHARSET + LINE_END);
                    sb11.append("Content-Transfer-Encoding: 8bit" + LINE_END);
                    sb11.append(LINE_END);
                    sb11.append(entry.getValue());
                    sb11.append(LINE_END);
                }
                System.out.println(sb11.toString());
                OutputStream outputStream = conn.getOutputStream();
                DataOutputStream dos = new DataOutputStream(outputStream);
                dos.write(sb11.toString().getBytes());//发送表单字段数据

                /**
                 * 当文件不为空，把文件包装并且上传
                 */
                Log.e("FileNotEmpty","11");
                //DataOutputStream dos = new DataOutputStream(outputStream);
                StringBuffer sb = new StringBuffer();
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                /**
                 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
                 * filename是文件的名字，包含后缀名的 比如:abc.png
                 * 上传参数在这里设置修改
                 */
                sb.append("Content-Disposition: form-data; name=\"photo\"; filename=\""
                        + file.getName() + "\"" + LINE_END);
                sb.append("Content-Type: application/octet-stream; charset="
                        + CHARSET + LINE_END);
                sb.append(LINE_END);
                dos.write(sb.toString().getBytes());
                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = is.read(bytes)) != -1) {
                    dos.write(bytes, 0, len);
                }
                is.close();
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END)
                        .getBytes();
                dos.write(end_data);
                dos.flush();


                /**
                 * 获取响应码 200=成功 当响应成功，获取响应的流
                 */
                int res = conn.getResponseCode();
                // 下面对获取到的输入流进行读取
                Log.e("code",Integer.toString(res));
                if (res == 200) {
                    InputStream input = conn.getInputStream();
                    StringBuffer sb1 = new StringBuffer();
                    int ss;
                    while ((ss = input.read()) != -1) {
                        sb1.append((char) ss);
                    }
                    result = sb1.toString();
                    Log.e("result",result);
                    return result;

                }
                else {
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e("MalformedURLException",e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("IOException",e.toString());
        }
        Log.e("UploadFail","11");
        return FAILURE;
    }
    /**
     * 把服务端返回的输入流转换成字符串格式
     * @param inputStream 服务器返回的输入流
     * @param encode 编码格式
     * @return 解析后的字符串
     */
    private static String changeInputStream(InputStream inputStream,
                                            String encode) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        String result="";
        if (inputStream != null) {
            try {
                while ((len = inputStream.read(data)) != -1) {
                    outputStream.write(data,0,len);
                }
                result=new String(outputStream.toByteArray(),encode);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }




    /**
     * 从服务器读取图片流数据，并转换为Bitmap格式
     * @return Bitmap
     */
    public Bitmap getImg(File file,String opra,String typeOfSt){
        Bitmap img = null;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("operation", opra);
            jsonObject.put("style", typeOfSt);
        }
        catch (JSONException e){
            Log.d("jsonError","put errror");
        }

        Map<String, String> params = new HashMap<>();
        //设置编码类型为utf-8
        params.put("data", String.valueOf(jsonObject));

        String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
        String PREFIX = "--", LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data"; // 内容类型
        String RequestURL = "http://172.18.233.3:52480";
        String result = null;
        try {
            URL url = new URL(RequestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true); // 允许输入流
            conn.setDoOutput(true); // 允许输出流
            conn.setUseCaches(false); // 不允许使用缓存
            conn.setRequestMethod("POST"); // 请求方式
            conn.setRequestProperty("Charset", CHARSET); // 设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="
                    + BOUNDARY);


            if (file != null) {
                /**
                 * 先传参数
                 */
                //获取map对象里面的数据，并转化为string
                StringBuilder sb11 = new StringBuilder();
                System.out.println("上传名称:"+params.get("data"));
                //上传的表单参数部分，不需要更改
                for (Map.Entry<String, String> entry : params.entrySet()) {//构建表单字段内容
                    sb11.append("--");
                    sb11.append(BOUNDARY);
                    sb11.append(LINE_END);
                    sb11.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINE_END);
                    sb11.append("Content-Type: text/plain; charset=" + CHARSET + LINE_END);
                    sb11.append("Content-Transfer-Encoding: 8bit" + LINE_END);
                    sb11.append(LINE_END);
                    sb11.append(entry.getValue());
                    sb11.append(LINE_END);
                }
                System.out.println(sb11.toString());
                OutputStream outputStream = conn.getOutputStream();
                DataOutputStream dos = new DataOutputStream(outputStream);
                dos.write(sb11.toString().getBytes());//发送表单字段数据

                /**
                 * 当文件不为空，把文件包装并且上传
                 */
                Log.e("FileNotEmpty","11");
                //DataOutputStream dos = new DataOutputStream(outputStream);
                StringBuffer sb = new StringBuffer();
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                /**
                 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
                 * filename是文件的名字，包含后缀名的 比如:abc.png
                 * 上传参数在这里设置修改
                 */
                sb.append("Content-Disposition: form-data; name=\"photo\"; filename=\""
                        + file.getName() + "\"" + LINE_END);
                sb.append("Content-Type: application/octet-stream; charset="
                        + CHARSET + LINE_END);
                sb.append(LINE_END);
                dos.write(sb.toString().getBytes());
                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = is.read(bytes)) != -1) {
                    dos.write(bytes, 0, len);
                }
                is.close();
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END)
                        .getBytes();
                dos.write(end_data);
                dos.flush();


                /**
                 * 获取响应码 200=成功 当响应成功，获取响应的流
                 */
                int res = conn.getResponseCode();
                // 下面对获取到的输入流进行读取
                Log.e("code",Integer.toString(res));
                if (res == 200) {
                    InputStream iss = conn.getInputStream();
                    img = BitmapFactory.decodeStream(iss);
                    iss.close();
                }
                else {
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e("MalformedURLException",e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("IOException",e.toString());
        }
        Log.e("UploadFail","11");
        return img;
    }

    /**
     * 测试参数
     * @return
     */
    private String getParam(){
        JSONObject jsObj = new JSONObject();
        try {
            jsObj.put("picFormat", "jpg");
            jsObj.put("testParam", "9527");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsObj.toString();
    }

}
