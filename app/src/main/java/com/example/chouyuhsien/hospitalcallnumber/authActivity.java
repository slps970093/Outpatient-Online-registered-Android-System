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

public class authActivity extends AppCompatActivity {
    private EditText rocidTxt;
    private Button submit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        rocidTxt=(EditText)findViewById(R.id.rocid);
        submit=(Button)findViewById(R.id.submitbtn);
        submit.setOnClickListener(BtnOnClick);
    }
    private Button.OnClickListener BtnOnClick = new Button.OnClickListener(){
        private Runnable miniThread = new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                String rociddata = rocidTxt.getText().toString();
                String data = "cRocID="+URLEncoder.encode(rociddata);
                String url="http://kevin.hwai.edu.tw/~kevin/chou/callsystem/index.php/api/Case_history/get_history?"+data;
                try{
                    URL mUrl = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) mUrl.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setReadTimeout(5000);
                    conn.setConnectTimeout(5000);
                    if(conn.getResponseCode()==200){
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
                        Log.e("Msg",msg);
                        JSONObject jsonObject = new JSONObject(msg);
                        Log.e("JSON",msg);
                        String cName = jsonObject.getString("name");
                        Log.e("cName",cName);
                        String cid = jsonObject.getString("id");
                        Log.e("cid",cid);
                        //資料傳遞
                        Bundle bundle =new Bundle();
                        bundle.putString("Name",cName);
                        bundle.putString("ID",cid);
                        Intent intent = new Intent();
                        //進行預約動作
                        intent.setClass(authActivity.this,reservationActivity.class);
                        intent.putExtras(bundle);
                        Toast.makeText(authActivity.this,R.string.auth_msg_success,Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    }else{
                        InputStream is = conn.getErrorStream();
                        ByteArrayOutputStream message = new ByteArrayOutputStream();
                        int len=0;
                        byte buffer[] = new byte[1024];
                        while ((len=is.read(buffer))!=-1){
                            message.write(buffer,0,len);
                        }
                        is.close();
                        message.close();
                        String msg = new String (message.toByteArray());
                        Log.e("Msg",msg);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        public void onClick(View v){
            //空值檢查
            String rocid=rocidTxt.getText().toString();
            if(!rocid.isEmpty()){
                //啟動執行緒
                Thread thread = new Thread(miniThread);
                thread.start();
            }else{
                Toast.makeText(authActivity.this,R.string.auth_input_null_msg,Toast.LENGTH_SHORT).show();
            }
        }
    };
}
