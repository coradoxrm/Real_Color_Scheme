package com.example.Scheme;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;



import android.os.Environment;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;



public class MainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */

    private static final String LOG_TAG = "HelloCamera";
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    private Button camBtn = null;
    private Button albumBtn = null;

    private Uri fileUri;

    public static final int MEDIA_TYPE_IMAGE = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        camBtn = (Button) findViewById(R.id.Camera);
        albumBtn = (Button) findViewById(R.id.Album);

        camBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 1);
            }

        });

        albumBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);//调用android的图库
                startActivityForResult(intent, 2);
            }

        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1)//拍照
        {

            if (resultCode == Activity.RESULT_OK) {
                String sdStatus = Environment.getExternalStorageState();
                if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
                    Log.i("TestFile",
                            "SD card is not avaiable/writeable right now.");
                    return;
                }


                String name = new DateFormat().format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";
                Toast.makeText(this, name, Toast.LENGTH_LONG).show();
                Bundle bundle = data.getExtras();
                Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式

                FileOutputStream b = null;
                //???????????????????????????????为什么不能直接保存在系统相册位置呢？？？？？？？？？？？？
                File file = new File("/sdcard/myImage/");
                boolean res = file.mkdirs();// 创建文件夹


                //Log.i("Testmkdir", "ans = "+ res);


                String fileName = "/sdcard/myImage/" + name;
                //sendBroadcast(fileName);

                try {
                    b = new FileOutputStream(fileName);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        b.flush();
                        b.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

                File f = new File(fileName);
                Uri uri = Uri.fromFile(f);
                intent.setData(uri);
                sendBroadcast(intent);//这个广播的目的就是更新图库，发了这个广播进入相册就可以找到你保存的图片了！，记得要传你更新的file哦

                ((ImageView) findViewById(R.id.imageView)).setImageBitmap(bitmap);// 将图片显示在ImageView里
            }
        } else {//相册
            switch (resultCode) {
                case Activity.RESULT_OK: {
                    Uri uri = data.getData();
                    Cursor cursor = this.getContentResolver().query(uri, null,
                            null, null, null);
                    cursor.moveToFirst();
                    String imgNo = cursor.getString(0); // 图片编号
                    String imgPath = cursor.getString(1); // 图片文件路径
                    String imgSize = cursor.getString(2); // 图片大小
                    String imgName = cursor.getString(3); // 图片文件名
                    cursor.close();
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = false;
                    options.inSampleSize = 10;
                    Bitmap bitmap = BitmapFactory.decodeFile(imgPath, options);

                    ((ImageView) findViewById(R.id.imageView)).setImageBitmap(bitmap);// 将图片显示在ImageView里
                }
                break;
                case Activity.RESULT_CANCELED:// 取消
                    break;
            }
        }

    }
}
