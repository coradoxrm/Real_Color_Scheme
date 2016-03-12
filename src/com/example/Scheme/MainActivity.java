package com.example.Scheme;

import android.app.Activity;
import android.content.ContentResolver;
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
    private Button camBtn = null;
    private Button albumBtn = null;

    private String filename = "/sdcard/temp.jpg";
    Uri imageUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        camBtn = (Button) findViewById(R.id.Camera);
        albumBtn = (Button) findViewById(R.id.Album);

        camBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                imageUri = Uri.fromFile(new File(filename));

                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult (cameraIntent, 1);

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

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1)//拍照
        {

            if (resultCode == Activity.RESULT_OK) {
                Global.filename = "sdcard/temp.jpg";
                Intent intent=new Intent(MainActivity.this,SchemeActivity.class);
                startActivity(intent);

            }
        } else {//相册
            switch (resultCode) {
                case Activity.RESULT_OK: {
                    Uri uri = data.getData();

                    final String scheme = uri.getScheme();
                    String filename = null;
                    if ( scheme == null )
                        filename = uri.getPath();
                    else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
                        filename = uri.getPath();
                    } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
                        Cursor cursor = getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
                        if ( null != cursor ) {
                            if ( cursor.moveToFirst() ) {
                                int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
                                if ( index > -1 ) {
                                    filename= cursor.getString( index );
                                }
                            }
                            cursor.close();
                        }
                    }

                    Global.filename = filename;

                    Intent intent=new Intent(MainActivity.this,SchemeActivity.class);
                    //intent.setData(uri);
                    startActivity(intent);
                    this.finish();


                }
                break;
                case Activity.RESULT_CANCELED:// 取消
                    break;
            }
        }

    }
}
