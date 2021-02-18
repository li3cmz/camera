package com.seu.magiccamera.util;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class MyRender implements GLSurfaceView.Renderer {

//    public GPUImageFilter filter;
//    public MagicBeautyFilter beautyFilter = new MagicBeautyFilter();;
//    public Bitmap bitmap;
//    public Bitmap filterBitmap;
//    public boolean isRotated;
//    public boolean filterIsChanged = false;

//    public MyRender(Context context) {
//        super(context);
//    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //First:设置清空屏幕用的颜色，前三个参数对应红绿蓝，最后一个对应alpha

//        GLES20.glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glDisable(GL10.GL_DITHER);
        GLES20.glClearColor(0,0, 0, 0);
        GLES20.glEnable(GL10.GL_CULL_FACE);
        GLES20.glEnable(GL10.GL_DEPTH_TEST);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //Second:设置视口尺寸，即告诉opengl可以用来渲染的surface大小

        GLES20.glViewport(0,0,width,height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //Third:清空屏幕，擦除屏幕上所有的颜色，并用之前glClearColor定义的颜色填充整个屏幕
//        Log.d("MyRender onDrawFrame", "is Rrawing Frame");
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
//        if(filterIsChanged) {
//            Log.d("MyRender onDrawFrame", "bitmap is null");
//            //filterBitmap = drawPhoto();
//            //filterIsChanged = false;
//            Log.d("MyRender onDrawFrame", "filterIsChanged drawPhoto");
//        }
    }
//
//    public Bitmap drawPhoto(){
//        if(bitmap == null) {
//            Log.d("MyRender", "bitmap is null");
//        }
//        int width = bitmap.getWidth();
//        int height = bitmap.getHeight();
//        int[] mFrameBuffers = new int[1];
//        int[] mFrameBufferTextures = new int[1];
//        if(beautyFilter == null)
//            beautyFilter = new MagicBeautyFilter();
//        beautyFilter.init();
//        beautyFilter.onDisplaySizeChanged(width, height);
//        beautyFilter.onInputSizeChanged(width, height);
//
//        if(filter != null) {
//            filter.onInputSizeChanged(width, height);
//            filter.onDisplaySizeChanged(width, height);
//        }
//        GLES20.glGenFramebuffers(1, mFrameBuffers, 0);
//        GLES20.glGenTextures(1, mFrameBufferTextures, 0);
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFrameBufferTextures[0]);
//        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0,
//                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
//        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
//                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
//        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
//                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
//        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
//                GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
//        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
//                GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
//        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);
//        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
//                GLES20.GL_TEXTURE_2D, mFrameBufferTextures[0], 0);
//
//        GLES20.glViewport(0, 0, width, height);
//        int textureId = OpenGlUtils.loadTexture(bitmap, OpenGlUtils.NO_TEXTURE, false);
//
//        FloatBuffer gLCubeBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.CUBE.length * 4)
//                .order(ByteOrder.nativeOrder())
//                .asFloatBuffer();
//        FloatBuffer gLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4)
//                .order(ByteOrder.nativeOrder())
//                .asFloatBuffer();
//        gLCubeBuffer.put(TextureRotationUtil.CUBE).position(0);
//        if(isRotated)
//            gLTextureBuffer.put(TextureRotationUtil.getRotation(Rotation.NORMAL, false, false)).position(0);
//        else
//            gLTextureBuffer.put(TextureRotationUtil.getRotation(Rotation.NORMAL, false, true)).position(0);
//
//        Log.d("MyRender", "isDrawing");
//
//        if(filter == null){
//            beautyFilter.onDrawFrame(textureId, gLCubeBuffer, gLTextureBuffer);
//            Log.d("inMyRender", "filter is null onDrawFrame");
//        }else{
//            beautyFilter.onDrawFrame(textureId);
//            filter.onDrawFrame(mFrameBufferTextures[0], gLCubeBuffer, gLTextureBuffer);
//            Log.d("inMyRender", "filtering onDrawFrame");
//        }
//        IntBuffer ib = IntBuffer.allocate(width * height);
//        GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, ib);
//        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//        result.copyPixelsFromBuffer(ib);
//
//        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
//        GLES20.glDeleteTextures(1, new int[]{textureId}, 0);
//        GLES20.glDeleteFramebuffers(mFrameBuffers.length, mFrameBuffers, 0);
//        GLES20.glDeleteTextures(mFrameBufferTextures.length, mFrameBufferTextures, 0);
//
//
//        if(filter != null) {
//            filter.onDisplaySizeChanged(width, height);
//            filter.onInputSizeChanged(width, height);
//        }
//        return result;
//    }
//
//    public void setBitmap(Bitmap _bitmap, boolean _isRotated) {
//        bitmap = _bitmap.copy(Bitmap.Config.RGB_565, true);
//        isRotated = _isRotated;
//    }
//    //美颜滤镜级别
//    public void setBeautyLevel(int level){
//        beautyFilter.setBeautyLevel(level);
//    }
//
//    //设置滤镜样式
//    public void setFilter(final MagicFilterType type){
////        new Thread() {
////            @Override
////            public void run() {
//                if (filter != null)
//                    filter.destroy();
//                filter = null;
//                filter = MagicFilterFactory.initFilters(type);
//                if (filter != null)
//                    filter.init();
////                onFilterChanged();
////            }
////        }.start();
////        filterIsChanged = true;
//    }

//    public void drawPhotoExtenal() {
//        filterBitmap = drawPhoto();
//        filterIsChanged = false;
//    }
}
