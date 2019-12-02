 package com.example.classno5;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView=findViewById(R.id.text);
        imageView=findViewById(R.id.image);
        //运用glide框架加载图片
//        Glide.with(this).load("https://avatars2.githubusercontent.com/u/14975630?v=4&s=120").into(imageView);
        //自定义的获取图片的方法，只能加载部分图片，有些链接不支持
        //思考：怎样将获取网络图片的代码完善
        getP();
        getDataAsync();

    }

    //创建handler，handler以匿名内部类的形式存在
    Handler handler=new Handler(){
        public void handleMessage(Message msg){
            //调用父类handleMessage（）方法
            super.handleMessage(msg);
            //如果msg的id为1
            if(msg.what==1){
                Bundle data=msg.getData();
                String responseBody=data.getString("responseBody");

                getJsonString(responseBody);
                //textView.setText(responseBody);
            }
            if(msg.what==2){
                Bundle data=msg.getData();
                byte[] bytes= data.getByteArray("bytes");
                Bitmap myPhoto= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                imageView.setImageBitmap(myPhoto);
            }
        }
    };
    public void getJsonString(String jsonString){

        try {
            JSONObject jo=new JSONObject(jsonString);
            //jo调用getString（）获取相应的值
//            textView.setText(jo.getString("success"));
            //jo调用getJSONArray()得到一个数组
            JSONArray jsonArray=jo.getJSONArray("data");
            //获取数组中的JSONObject
            JSONObject jsonObject_0=jsonArray.getJSONObject(0);
//            获取jsonObject_0的值并显示
            //textView.setText(jsonObject_0.getString("title"));

            JSONObject jsonObject_0_auther=jsonObject_0.getJSONObject("author");
            String s=jsonObject_0_auther.getString("avatar_url");
            textView.setText(s);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getDataAsync(){
        //创建client
        final OkHttpClient client=new OkHttpClient();
        //创建request
        final Request request=new Request.Builder().url("https://cnodejs.org/api/v1/topics").build();
        //发起请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //打印失败日志
                Log.d("failed","错误");
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                //获取responseBody
                String responseBody;
                if(response.isSuccessful()){
                    responseBody=response.body().string();
                    //创建message
                    Message msg=Message.obtain();
                    //创建bundle
                    Bundle data=new Bundle();
                    //将responseBody放进data中
                    data.putString("responseBody",responseBody);
                    //将data放进msg中
                    msg.setData(data);
                    //给msg设定一个id
                    msg.what=1;
                    //将msg传递给handler
                    handler.sendMessage(msg);
                }else {
                    Log.d("failed","错误");
                }
            }
        });

    }
    public void getP(){
        //创建client
        final OkHttpClient client=new OkHttpClient();
        //创建request
        final Request request=new Request.Builder().url("https://cn.bing.com/sa/simg/hpb/LaDigue_EN-CA1115245085_1920x1080.jpg").build();
        //发起请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //打印失败日志
                Log.d("failed","请求网络错误");
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                //获取responseBody
                String responseBody;
                if(response.isSuccessful()){

                    byte[] bytes=response.body().bytes();

                    //创建message
                    Message msg=Message.obtain();
                    //创建bundle
                    Bundle data=new Bundle();
                    //将responseBody放进data中
                    data.putByteArray("bytes",bytes);
                    //将data放进msg中
                    msg.setData(data);
                    //给msg设定一个id
                    msg.what=2;
                    //将msg传递给handler
                    handler.sendMessage(msg);
                }else {
                    Log.d("failed","处理图片错误");
                }
            }
        });
    }
}
