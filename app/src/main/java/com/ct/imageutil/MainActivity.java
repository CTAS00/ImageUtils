package com.ct.imageutil;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {
    ImageView iv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        iv= (ImageView) findViewById(R.id.iv);
        //说明还没有添加到容器中
        int width=iv.getWidth();
        Log.e("CTAS第一次",width+"");
        if(width<=0){
           RelativeLayout.LayoutParams lp= (RelativeLayout.LayoutParams) iv.getLayoutParams();
            width=lp.width;

        }
        Log.e("CTAS第二次",width+"");
        //检查最大值  这个功能没什么用
//        if(width<=0){
//            width=iv.getMaxWidth();
//        }
        //获取屏幕的尺寸

        //Log.e("CTAS第三次",width+"");
        width=this.getWindowManager().getDefaultDisplay().getWidth();
        Log.e("CTAS第三次",width+"");









    }
}
