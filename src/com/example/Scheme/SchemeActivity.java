package com.example.Scheme;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class SchemeActivity extends ListActivity {

    private List<Map<String, Object>> mData;
    private Button iter,ok,no;
    private Cluster cluster;
    private int location;
    private MyAdapter adapter;

    private int r,g,b;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.scheme);


        cluster = new Cluster();
        Bitmap bitmap = BitmapFactory.decodeFile(Global.filename);

        if(cluster.setImage(bitmap)) {
            //Log.d("bitmap","not null");
            debug("bitmap not null");
            cluster.initSeed();
            cluster.updateCluster();
            ((ImageView) findViewById(R.id.image)).setImageBitmap(bitmap);
            mData = getData();
            adapter = new MyAdapter(this);
            setListAdapter(adapter);
        }
        else {
            debug("bitmap null");
        }




        iter = (Button)findViewById(R.id.iter);
        iter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cluster.updateSeed();
                cluster.updateCluster();

                Map<String, Object> map = new HashMap<String, Object>();
                for(int i = 0 ; i < cluster.getSeedNum() ; i++) {
                    map.put(String.valueOf(i), cluster.schemeNodes[cluster.getIndex()-1][i]);
                }
                    mData.add(map);
                    adapter.notifyDataSetChanged();
            }

        });

        ok = (Button)findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Date date = new Date();
//                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss"); // 格式化时间
//                String filename = format.format(date) + ".jpg";
//                String destDirName = Environment.getExternalStorageDirectory() + "/repo/";
//                File destDir = new File(destDirName);
//                if (!destDir.exists())
//                    destDir.mkdirs();
//
//                File srcFile = new File(Global.filename);
//                if(!srcFile.exists() || !srcFile.isFile())
//                    return;
//
//                File f = new File(destDirName  +  filename);
//                srcFile.renameTo(f);
//
//                Context context = SchemeActivity.this;
//                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Uri.parse("file://"+ destDirName  +  filename)));

                try
                {
                    sendData(location);
                    debug("senddata");
                }
                catch (IOException ex) {
                    debug("ioexception");
                }

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

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Toast.makeText(SchemeActivity.this, "You click: " + position, Toast.LENGTH_SHORT).show();
        location = position;
    }

    private void sendData(int position) throws IOException
    {
        //int r = cluster.schemeNodes[position][0].r;
        //int g = cluster.schemeNodes[position][0].g;
        //int b = cluster.schemeNodes[position][0].b;
        //int c = (255 - r) * 100 / 255;
        //int m = (255 - g) * 100 / 255;
        //int y = (255 - b) * 100 / 255;
        //int w = ((100 - c) + (100 - m) + (100 - y)) / 3;

        int _r = (r*100)/255;
        int _g = (g*100)/255;
        int _b = (b*100)/255;
        int _max = Math.max(Math.max(_r,_g),_b);
        int k = 100 -_max;
        int c = (_max-_r)*100/_max;
        int m = (_max-_g)*100/_max;
        int y = (_max-_b)*100/_max;

        int w = ((100 - c) + (100 - m) + (100 - y)) / 3;

//        int _max = Math.max(Math.max(r,g),b);
//        int k = 100 -_max;
//        int c = (_max-r)*100/_max;
//        int m = (_max-g)*100/_max;
//        int y = (_max-b)*100/_max;
//        int w = ((100 - c) + (100 - m) + (100 - y)) / 3;

        String msg = c +  "," + m +  "," + y + "," + k;
        msg += "\n";

        Toast.makeText(SchemeActivity.this,"send: " + msg, Toast.LENGTH_SHORT).show();
        debug(msg);
        Global.mmOutputStream.write(msg.getBytes());
    }

    void debug(String s) {
        Toast.makeText(SchemeActivity.this,s, Toast.LENGTH_SHORT).show();
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        Map<String, Object> map = new HashMap<String, Object>();
        for(int i = 0 ; i < cluster.getSeedNum() ; i++) {
            map.put(String.valueOf(i), cluster.schemeNodes[cluster.getIndex()-1][i]);
        }
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

        private void setRGB(int color) {
            r = (color >> 16) & 0xFF;
            g = (color >> 8) & 0xFF;
            b = (color >> 0) & 0xFF;
            Toast.makeText(SchemeActivity.this,"color: " +  r + "," + g + "," + b, Toast.LENGTH_SHORT).show();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder=new ViewHolder();
                convertView = mInflater.inflate(R.layout.list_item, null);
                final ViewHolder finalHolder = holder;
                holder.i1 = (ImageView)convertView.findViewById(R.id.i1);
                holder.i1.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View view) {
                        ColorDrawable draw = (ColorDrawable)finalHolder.i1.getBackground();
                        setRGB(draw.getColor());
                    }});
                holder.i2 = (ImageView)convertView.findViewById(R.id.i2);
                holder.i2.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View view) {
                        ColorDrawable draw = (ColorDrawable)finalHolder.i2.getBackground();
                        setRGB(draw.getColor());
                    }});

                holder.i3 = (ImageView)convertView.findViewById(R.id.i3);
                holder.i3.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View view) {
                        ColorDrawable draw = (ColorDrawable)finalHolder.i3.getBackground();
                        setRGB(draw.getColor());
                    }});
                holder.i4 = (ImageView)convertView.findViewById(R.id.i4);
                holder.i4.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View view) {
                        ColorDrawable draw = (ColorDrawable)finalHolder.i4.getBackground();
                        setRGB(draw.getColor());
                    }});
                holder.i5 = (ImageView)convertView.findViewById(R.id.i5);
                holder.i5.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View view) {
                        ColorDrawable draw = (ColorDrawable)finalHolder.i5.getBackground();
                        setRGB(draw.getColor());
                    }});

                convertView.setTag(holder);

            }else
                holder = (ViewHolder)convertView.getTag();

            Node tmp = (Node)mData.get(position).get("0");
            holder.i1.setBackgroundColor(Color.rgb(tmp.r,tmp.g,tmp.b));
            tmp = (Node)mData.get(position).get("1");
            holder.i2.setBackgroundColor(Color.rgb(tmp.r,tmp.g,tmp.b));
            tmp = (Node)mData.get(position).get("2");
            holder.i3.setBackgroundColor(Color.rgb(tmp.r,tmp.g,tmp.b));
            tmp = (Node)mData.get(position).get("3");
            holder.i4.setBackgroundColor(Color.rgb(tmp.r,tmp.g,tmp.b));
            tmp = (Node)mData.get(position).get("4");
            holder.i5.setBackgroundColor(Color.rgb(tmp.r,tmp.g,tmp.b));

            return convertView;
        }

    }
}
