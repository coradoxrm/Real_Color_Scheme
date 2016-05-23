package com.example.Scheme;

import android.app.Activity;
import android.app.ListActivity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;
import com.clust4j.algo.MeanShift;
import com.clust4j.algo.MeanShiftParameters;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;

/**
 * Created by zliaky on 2016/5/21.
 */

public class MyMeanShift extends Activity {

    private int counter;    //统计采样点的个数
    private double d[][];   //采样后的图片的数组
    private int label[];    //统计每个cluster中含有几个元素

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.meanshift);

        Bitmap bitmap = BitmapFactory.decodeFile(Global.filename);  //读图片
        if (bitmap == null) {
            debug("bitmap null");
            return;
        }
        ((ImageView) findViewById(R.id.msimage)).setImageBitmap(bitmap);    //设置imageview显示原图
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();    //获取原图的长宽
        int mosaicHeight = height/20;
        int mosaicWidth = width/20;         //计算采样率（目的是把图片大小压缩到400个像素左右）
        d = new double[height*width/(mosaicHeight*mosaicWidth)][3];
        counter = 0;                        //初始化采样点计数器
        for (int i = 0; i < width; i+=mosaicWidth) {
            for (int j = 0; j < height; j+=mosaicHeight) {
                int pixel = bitmap.getPixel(i, j);
                int r = (pixel & 0xff0000) >> 16;
                int g = (pixel & 0xff00) >> 8;
                int b = (pixel & 0xff);                         //计算RGB
                double c = (double)(255 - r) / 255;
                double m = (double)(255 - g) / 255;
                double y = (double)(255 - b) / 255;
                double k = (double)Math.min(c, Math.min(m, y)); //计算CMYK
                if (k > 0.5) continue;                          //黑色过多的点不要，这里和kmeans一样
                d[counter][0] = (double)r/256;
                d[counter][1] = (double)g/256;
                d[counter][2] = (double)b/256;                  //把采样后的像素点放进d[][]中
                counter++;
            }
        }
        System.out.println("counter: "+counter);
        System.out.println("d.length: "+d.length);              //输出counter和d.length，此处counter应该小于d.length
        final Array2DRowRealMatrix mat = new Array2DRowRealMatrix(d, true);
        MeanShift ms = new MeanShiftParameters(0.05).fitNewModel(mat);
        final int[] results = ms.getLabels();                   //这三行为meanshift操作，其中parameter(0.05)可调，results的内容是每个像素对应的cluster编号
        double test[][] = ms.getKernelSeeds();                  //一个不知道干什么用的getKernelSeeds函数，下面注释的部分是输出它看结果
//        for (int i = 0; i < counter; i++) {
//                System.out.println(i+": "+test[i][0]*256+" "+test[i][1]*256+" "+test[i][2]*256);
//        }


        label = new int[counter];
        for (int i = 0; i < counter; i++) {
            label[i] = 0;
        }                                                       //初始化label（统计每个cluster的元素个数）
        for (int i = 0; i < counter; i++) {
//            System.out.println("i: "+i+", results[i]: "+results[i]);
            label[results[i]]++;                                //统计每个cluster的元素个数，记录在label[]里
        }
        for (int i = 0; i < counter; i++) {
            if (label[i] > 10) {                                //如果这个cluster的元素个数大于10
                System.out.println(i+": "+label[i]);
                for (int j = 0; j < results.length; j++) {
                    if (results[j] == i) {
                        System.out.println(d[j][0]*256+" "+d[j][1]*256+" "+d[j][2]*256);    //找第一个cluster是它的点输出颜色值
                        break;
                    }
                }
            }
        }
    }

    void debug(String s) {
        Toast.makeText(MyMeanShift.this,s, Toast.LENGTH_SHORT).show();
    }   //根本没有用上的debug函数=w=
}
