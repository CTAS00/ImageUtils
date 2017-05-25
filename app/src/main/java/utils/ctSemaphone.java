package utils;

/**
 * Created by koudai_nick on 2017/5/19.
 */

public class ctSemaphone {
    private boolean single;
    /**
     * 阻塞方法
     */
    public synchronized  void acquire(){
        try {
            while(!single){
                wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        single=false;
    }
    /**
     * 释放方法
     */
    public synchronized  void release(){
        single=true;
        notifyAll();
    }

}
