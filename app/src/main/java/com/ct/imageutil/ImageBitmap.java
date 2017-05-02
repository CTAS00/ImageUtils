package com.ct.imageutil;

import android.graphics.BitmapFactory;

/**
 * Created by koudai_nick on 2017/5/2.
 * Bitmap的高效加载  bitmap就是一张图片 缩小bitmap一定程度上能避免oom
 */

public class ImageBitmap {


    public static void decodeBitmap(){




    }


    public static int dealInSampleSize(BitmapFactory.Options options,int reqWidth,int reqHeight){

        int width=options.outWidth;
        int height=options.outHeight;
        int InSampleSize=1;
        //不能比期望值小  问题的本质就是多乘了一次
        //解决方法1.
        while(width/InSampleSize>=reqWidth &&height/InSampleSize>=reqHeight){
            InSampleSize*=2;
        }
        InSampleSize=InSampleSize/2;
        //解决方法2.  第一次由自己去处理
        if(width>reqWidth||height>reqHeight){
            int halfWidth=width/2;
            int halfHeight=height/2;

            while(halfWidth/InSampleSize>=reqWidth &&halfHeight/InSampleSize>=reqHeight){
                InSampleSize*=2;
            }

        }


        return InSampleSize;






    }
}
