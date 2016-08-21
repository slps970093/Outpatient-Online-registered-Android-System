package com.example.chouyuhsien.hospitalcallnumber;

import android.content.Intent;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class registerActivity extends AppCompatActivity {
    private Button submit;
    private EditText cNameTxt;
    private EditText cRocidTxt;
    private EditText cBirthdayTxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        cNameTxt=(EditText)findViewById(R.id.cName);
        cRocidTxt=(EditText)findViewById(R.id.cRocid);
        cBirthdayTxt=(EditText)findViewById(R.id.cBirthday);
        submit=(Button)findViewById(R.id.submitbtn);
        submit.setOnClickListener(btnOnclick);
    }
    private Button.OnClickListener btnOnclick = new Button.OnClickListener(){
        private Runnable miniThread = new Runnable() {
            @Override
            public void run() {
                //進行聯網動作
                Looper.prepare();
                String post_url = "http://kevin.hwai.edu.tw/~kevin/chou/callsystem/index.php/api/Case_history/history"; //網址
                String cName = cNameTxt.getText().toString();
                String cRicid = cRocidTxt.getText().toString();
                String cBirthday = cBirthdayTxt.getText().toString();
                try{
                    //開始連線
                    HttpURLConnection conn = (HttpURLConnection) new URL(post_url).openConnection();
                    conn.setRequestMethod("POST");  //請求方式 GET/POST
                    conn.setReadTimeout(5000);  //設定讀取超時
                    conn.setConnectTimeout(5000);   //設定連線超時
                    conn.setDoOutput(true); //設定運行輸入
                    conn.setDoInput(true);  //設定運行輸入
                    conn.setUseCaches(false);   //是否緩存 (POST請輸入false GET請輸入true)
                    //請求數據
                    String data = "cName="+ URLEncoder.encode(cName,"UTF-8")+"&cBirthday="+ URLEncoder.encode(cBirthday,"UTF-8")+"&cRocID="+URLEncoder.encode(cRicid,"UTF-8");
                    Log.e("data=",data);
                    OutputStream out = conn.getOutputStream();
                    out.write(data.getBytes());
                    out.flush();

                    if(conn.getResponseCode()==201){
                        InputStream is = conn.getInputStream();
                        ByteArrayOutputStream message = new ByteArrayOutputStream();
                        int len=0;
                        byte buffer[] = new byte[1024];
                        while ((len=is.read(buffer))!=-1){
                            message.write(buffer,0,len);
                        }
                        is.close();
                        message.close();
                        String msg = new String (message.toByteArray());
                        JSONObject jsonObject = new JSONObject(msg);
                        boolean status_msg = jsonObject.getBoolean("status");
                        if(status_msg){
                            Toast.makeText(registerActivity.this,R.string.data_upload_success,Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            Log.e("status","success");
                            intent.setClass(registerActivity.this,MainActivity.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(registerActivity.this,R.string.data_upload_failed,Toast.LENGTH_SHORT).show();
                            Log.e("msg",msg);
                            Intent intent = new Intent();
                            intent.setClass(registerActivity.this,MainActivity.class);
                            startActivity(intent);
                        }
                        Looper.loop();
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        public void onClick(View v){
            String cName = cNameTxt.getText().toString();
            String cRicid = cRocidTxt.getText().toString();
            String cBirthday = cBirthdayTxt.getText().toString();
            if(!cName.isEmpty()){
                if(!cRicid.isEmpty()){
                    if(!cBirthday.isEmpty()) {
                        //啟用執行緒
                        Thread thread = new Thread(miniThread);
                        thread.start();
                    }else{
                        Toast.makeText(registerActivity.this,R.string.reg_input_null_msg,Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(registerActivity.this,R.string.reg_input_null_msg,Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(registerActivity.this,R.string.reg_input_null_msg,Toast.LENGTH_SHORT).show();
            }
        }
    };
}
