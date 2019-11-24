 package com.example.classno5;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView=findViewById(R.id.text);
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

}
