package com.example.chouyuhsien.hospitalcallnumber;

import android.content.Intent;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class reservationActivity extends AppCompatActivity {
    private ListView duelist1;
    private ArrayList<String> a_sid = new ArrayList();
    private ArrayList<String> a_date = new ArrayList();
    private ArrayList<String> a_did = new ArrayList();
    private ArrayList<String> a_time = new ArrayList();
    private ArrayList show_data = new ArrayList();
    //傳值資料存放
    public String Name;
    public String Birthday;
    public String id;
    private int tmp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);
        Log.e("status", "init");
        Thread init_Thread = new Thread(init);
        init_Thread.start();
        //接收另一個Activity資料
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        //資料傳遞 (Bunble)
        id = bundle.getString("ID");
        Name = bundle.getString("Name");
        //驗證資料是否存在
        if (id.isEmpty()) {
            if (Birthday.isEmpty()) {
                if (Name.isEmpty()) {
                    goto_Auth_Page();
                }
            } else {
                goto_Auth_Page();
            }
            goto_Auth_Page();
        }
        //列表
        duelist1 = (ListView) findViewById(R.id.listView);
        //產生資料
        ArrayAdapter<String> adapterData = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, show_data);
        duelist1.setAdapter(adapterData);
        duelist1.setOnItemClickListener(lstOnclick);
    }

    private void goto_Auth_Page() {
        Intent goto_Auth = new Intent();
        goto_Auth.setClass(reservationActivity.this, authActivity.class);
        Toast.makeText(reservationActivity.this, R.string.res_Exception_values_null, Toast.LENGTH_SHORT).show();
        startActivity(goto_Auth);
    }

    private ListView.OnItemClickListener lstOnclick = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String sel = adapterView.getItemAtPosition(i).toString();
            Log.e("SEL", sel);
            tmp = show_data.indexOf(sel);
            Log.e("sid", a_sid.get(tmp));
            Thread post = new Thread(post_data);
            post.start();
        }
    };
    //資料送出
    private Runnable post_data = new Runnable() {
        @Override
        public void run() {
            Looper.prepare();
            Log.e("thread", "success");
            String sid = a_sid.get(tmp);
            Log.e("thread->sid", sid);
            String uid = id;
            Log.e("thread->uid", uid);
            String link = "http://kevin.hwai.edu.tw/~kevin/chou/callsystem/index.php/api/reservation";
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(link).openConnection();
                conn.setRequestMethod("POST");
                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setUseCaches(false);
                String data = "sid=" + URLEncoder.encode(sid, "UTF-8") + "&cid=" + URLEncoder.encode(uid, "UTF-8");
                Log.e("data", data);
                OutputStream out = conn.getOutputStream();
                out.write(data.getBytes());
                out.flush();
                String cde = String.valueOf(conn.getResponseCode());
                Log.e("code",cde);
                if (conn.getResponseCode() == 201) {
                    InputStream is = conn.getInputStream();
                    ByteArrayOutputStream message = new ByteArrayOutputStream();
                    int len = 0;
                    byte buffer[] = new byte[1024];
                    while ((len = is.read(buffer)) != -1) {
                        message.write(buffer, 0, len);
                    }
                    is.close();
                    message.close();
                    String msg = new String(message.toByteArray());
                    Log.e("msg",msg);
                    if (!msg.isEmpty()) {
                        JSONObject jsonObject = new JSONObject(msg);
                        boolean status = jsonObject.getBoolean("status");
                        if (status) {
                            Toast.makeText(reservationActivity.this, R.string.res_success, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            intent.setClass(reservationActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(reservationActivity.this, R.string.res_failed, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            intent.setClass(reservationActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }
                }else{
                    InputStream is = conn.getErrorStream();
                    ByteArrayOutputStream message = new ByteArrayOutputStream();
                    int len = 0;
                    byte buffer[] = new byte[1024];
                    while ((len = is.read(buffer)) != -1) {
                        message.write(buffer, 0, len);
                    }
                    is.close();
                    message.close();
                    String msg = new String(message.toByteArray());
                    Log.e("msg",msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Looper.loop();
        }
    };
    //APP 資料載入初始化 (聯網)
    private Runnable init = new Runnable() {
        @Override
        public void run() {
            try {
                Looper.prepare();
                String url = "http://kevin.hwai.edu.tw/~kevin/chou/callsystem/index.php/api/scheduling";
                URL mUrl = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) mUrl.openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);
                if (conn.getResponseCode() == 200) {
                    InputStream is = conn.getInputStream();
                    ByteArrayOutputStream message = new ByteArrayOutputStream();
                    int len = 0;
                    byte buffer[] = new byte[1024];
                    while ((len = is.read(buffer)) != -1) {
                        message.write(buffer, 0, len);
                    }
                    is.close();
                    message.close();
                    String msg = new String (message.toByteArray());
                    if (!msg.equals("failed")) {
                        if (!msg.isEmpty()) {
                            JSONObject jsonObject = new JSONObject(msg);
                            JSONArray sid = jsonObject.getJSONArray("sid");
                            JSONArray date = jsonObject.getJSONArray("date");
                            JSONArray did = jsonObject.getJSONArray("did");
                            JSONArray time = jsonObject.getJSONArray("time");
                            //塞資料
                            for (int i = 0; i <= sid.length() - 1; i++) {
                                a_sid.add(sid.get(i).toString());
                                a_date.add(date.get(i).toString());
                                a_did.add(did.get(i).toString());
                                a_time.add(time.get(i).toString());
                                String str = "看診日期為" + a_date.get(i) + "開始時間" + a_time.get(i);
                                show_data.add(str);
                            }
                        }
                    }
                } else {
                    InputStream is = conn.getErrorStream();
                    ByteArrayOutputStream message = new ByteArrayOutputStream();
                    int len = 0;
                    byte buffer[] = new byte[1024];
                    while ((len = is.read(buffer)) != -1) {
                        message.write(buffer, 0, len);
                    }
                    is.close();
                    message.close();
                    String msg = new String(message.toByteArray());
                    Log.e("Msg", msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Looper.loop();
        }
    };
}
