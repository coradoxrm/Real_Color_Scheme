package com.example.Scheme;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class SchemeActivity extends ListActivity {

    private List<Map<String, Object>> mData;
    private Button iter,ok,no;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scheme);

        Bitmap bitmap = BitmapFactory.decodeFile(Global.filename);
        ((ImageView) findViewById(R.id.image)).setImageBitmap(bitmap);

        //
        mData = getData();
        MyAdapter adapter = new MyAdapter(this);
        setListAdapter(adapter);

        iter = (Button)findViewById(R.id.iter);
        iter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("title", "G1");
                    map.put("info", "google 1");
                    map.put("img", R.drawable.cam);

                    mData.add(map);
                    adapter.notifyDataSetChanged();
            }

        });

        ok = (Button)findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = new Date();
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss"); // 格式化时间
                String filename = format.format(date) + ".jpg";

                String destDirName = Environment.getExternalStorageDirectory() + "/repo/";
                File destDir = new File(destDirName);
                if (!destDir.exists())
                    destDir.mkdirs();

                File srcFile = new File(Global.filename);
                if(!srcFile.exists() || !srcFile.isFile())
                    return;

                File f = new File(destDirName  +  filename);
                srcFile.renameTo(f);

                Context context = SchemeActivity.this;
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Uri.parse("file://"+ destDirName  +  filename)));

            }

        });



        no = (Button)findViewById(R.id.no);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SchemeActivity.this,MainActivity.class);
                startActivity(intent);

            }

        });




    }



    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("title", "G1");
        map.put("info", "google 1");
        map.put("img", R.drawable.cam);
        list.add(map);

        return list;
    }


    public final class ViewHolder{
        public ImageView i1,i2,i3,i4,i5;
    }

    public class MyAdapter extends BaseAdapter {

        private LayoutInflater mInflater;


        public MyAdapter(Context context){
            this.mInflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder=new ViewHolder();
                convertView = mInflater.inflate(R.layout.list_item, null);
                holder.i1 = (ImageView)convertView.findViewById(R.id.i1);
                holder.i2 = (ImageView)convertView.findViewById(R.id.i2);
                holder.i3 = (ImageView)convertView.findViewById(R.id.i3);
                holder.i4 = (ImageView)convertView.findViewById(R.id.i4);
                holder.i5 = (ImageView)convertView.findViewById(R.id.i5);
                convertView.setTag(holder);
            }else
                holder = (ViewHolder)convertView.getTag();

            //holder.img.setBackgroundResource((Integer)mData.get(position).get("img"));
            holder.i1.setBackgroundColor(Color.rgb(255,0,0));
            holder.i2.setBackgroundColor(Color.rgb(255,255,0));
            holder.i3.setBackgroundColor(Color.rgb(255,0,255));
            holder.i4.setBackgroundColor(Color.rgb(0,255,0));
            holder.i5.setBackgroundColor(Color.rgb(0,0,255));

            return convertView;
        }

    }
}
