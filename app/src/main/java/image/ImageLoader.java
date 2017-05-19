package image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.LruCache;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ct.imageutil.ImageBitmap;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;


/**
 * Created by koudai_nick on 2017/5/12.
 * 本地的图片加载策略
 *
 * 1.后台轮询的线程  负责对任务的处理
 * 主要实现就是Handler消息机制  一有任务就可以去进行处理
 *
 */

public class ImageLoader {

    private LruCache<String,Bitmap> mLruCache;

    //更新ui的消息机制  发送消息、处理消息、更新UI
    private Handler mUIHandler;
    //后台轮询线程
    private Handler mThreadHandler;

    //后台的轮询线程
    private Thread mBackThread;
    //从任务队列取出的顺序
    private FLAG_QUEUE type=FLAG_QUEUE.FIFO;


    //用一个任务队列来管理任务
    private LinkedList<Runnable> mLinkedList;

    private ExecutorService threadpool= Executors.newFixedThreadPool(3);

    private Semaphore  mBackSemaphore=new Semaphore(0);

    public static ImageLoader imageLoader;


    public Context context;
    private ImageLoader(){
        int MaxCacheSize= (int) (Runtime.getRuntime().maxMemory()/4);
        mLruCache=new LruCache<String,Bitmap>(MaxCacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes()*value.getHeight();
            }
        };

        mLinkedList=new LinkedList<>();
        //开启一个后台的轮询线程
        mBackThread=new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                mThreadHandler=new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        threadpool.execute(getRunnable());
                    }
                };
                //唤醒
                mBackSemaphore.release();
                Looper.loop();
            }
        });
        mBackThread.start();

    }

    public enum  FLAG_QUEUE{

        FIFO,LIFO;
    }

    /**
     * 获取任务 根据设置的状态去获取
     * 1.对于这个并不会有效果。
     * @return
     */
    private Runnable getRunnable() {
        if(type==FLAG_QUEUE.FIFO){
            return mLinkedList.removeFirst();
        }else if(type==FLAG_QUEUE.LIFO){
            return mLinkedList.removeLast();
        }
        return null;
    }
    public static  ImageLoader getInstance(){
        if(imageLoader==null){
            synchronized (ImageLoader.class){

                if(imageLoader==null){
                    imageLoader=new ImageLoader();
                }
            }
        }
        return imageLoader;
    }

    /**
     * 会有多线程同时处理的情况么？
     *
     * @param imageView
     * @param url
     */
    public void loadImage(final ImageView imageView, final String url){
        imageView.setTag(url);
        if(mUIHandler==null){
            mUIHandler=new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    //更新UI的处理
                    ImageHolder bean= (ImageHolder) msg.obj;
                    String url=bean.url;
                    Bitmap bm=bean.bm;
                    ImageView imageview=bean.imageview;
                    if(url.equals((String)imageview.getTag())){
                        imageview.setImageBitmap(bm);
                    }

                }
            };
        }
        //从内存中去获取
        Bitmap bm=getBitmapFromCache(url);
        if(bm!=null){
            Message message=Message.obtain();
            ImageHolder bean=new ImageHolder();
            bean.bm=bm;
            bean.imageview=imageView;
            bean.url=url;
            message.obj=bean;
            mUIHandler.sendEmptyMessage(0x110);
        }else{
            addTask(new Runnable() {
                @Override
                public void run() {
                    //获得图片需要显示的大小
                    ImageSize imagesize=getImageSize(imageView);
                    //1.getImageViewSize;

                    //压缩图片
                    Bitmap bm=getSampledBitmap(imagesize.width,imagesize.height,url);
                    //把图片加入到缓存
                    addBitmapLrucache(url,bm);



                }


            });

        }
    }

    /**
     * 放到内存中去
     * @param bm
     */
    private void addBitmapLrucache(String url,Bitmap bm) {
        if(getBitmapFromCache(url)==null){
            mLruCache.put(url,bm);
        }
    }

    /**
     * 压缩bitmap
     * @param width
     * @param height
     * @param url
     * @return
     */
    private Bitmap getSampledBitmap(int width, int height, String url) {
        return  null;
    }

    /**
     * 获取到图片的大小
     * @return
     */
    private ImageSize getImageSize(ImageView imageView) {
        int width=imageView.getWidth();
        if(width<=0){
           ViewGroup.LayoutParams lp= imageView.getLayoutParams();
            width=lp.width;

        }
        if(width<=0){


        }





     return  null;
    }

    /**
     * 将任务放到任务队列中去  要加synchronized么
     * 对于并发操作就会带来影响。在这个程序中只有ui和后台的轮询线程  暂时不考虑这个问题。
     * @param runnable
     */
    private  void addTask(Runnable runnable) {
        mLinkedList.add(runnable);
        if(mThreadHandler==null){
            try {
                mBackSemaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        mThreadHandler.sendEmptyMessage(0x112);

    }



    private void compressImage(ImageView imageview){
        



    }

    /**
     * 从内存中去获取bitmap
     */
    private Bitmap getBitmapFromCache(String key) {
     return    mLruCache.get(key);
    }
    class ImageHolder{
        String url;
        Bitmap bm;
        ImageView imageview;
    }
    class ImageSize{
        int width;
        int height;
    }
}
