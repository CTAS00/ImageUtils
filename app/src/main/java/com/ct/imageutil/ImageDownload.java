package com.ct.imageutil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import utils.Utils;

/**
 * Created by koudai_nick on 2017/5/2.
 * 图片的处理类
 * 1.从内存
 * 2.从本地
 * 3.从网络。
 *
 *
 */

public class ImageDownload {
    private LruCache<ImageView,Bitmap> mLruCache;
    public static ImageDownload instance;
    private Context mContext;
    private ImageDownload(Context context){
        mContext=context;
        int cacheSize= (int) (Runtime.getRuntime().maxMemory()/4);
        mLruCache=new LruCache<ImageView, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(ImageView key, Bitmap value) {
                return value.getRowBytes()*value.getHeight();
            }
        };
    }
    public static ImageDownload getInstance(Context context){
        if(instance==null){
            synchronized (ImageDownload.class){

                if(instance==null){
                    instance=new ImageDownload(context);
                }
                return instance;
            }
        }
        return instance;

    }

    /**
     * 路径加名称  名称用md5来加密
     */
    public void writeToLocal(String url,Bitmap bitmap){
      //  FileOutputStream fileOutputStream=new FileOutputStream();
         String name=  Utils.hashKeyForDisk(url);
         File file=new File(getCacheDir(),name);
        OutputStream os=null;
        try {
            os=new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            if(os!=null){
                try {
                    os.close();
                    os=null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 从本地获取
     * @param url
     * @return
     */
    public Bitmap readFromLocal(String url){
        String name=Utils.hashKeyForDisk(url);
       File file=null;
        file=new File(getCacheDir(),name);
        Bitmap bitmap=null;
        if(file.exists()){
            bitmap= BitmapFactory.decodeFile(file.getAbsolutePath());
            return bitmap;

        }
        return bitmap;


    }

    /**
     * 获取大致的路径
     * @return
     */
    private String getCacheDir() {
        String state = Environment.getExternalStorageState();
        File dir = null;
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // 有sd卡
            dir = new File(Environment.getExternalStorageDirectory(), "/Android/data/" + mContext.getPackageName()
                    + "/icon");
        } else {
            // 没有sd卡
            dir = new File(mContext.getCacheDir(), "/icon");
        }
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir.getAbsolutePath();
    }

}
