package com.example.chouyuhsien.hospitalcallnumber;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button addusrbtn;
    private Button resbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addusrbtn = (Button)findViewById(R.id.adduserbtn);
        resbtn =(Button)findViewById(R.id.reservationbtn);
        addusrbtn.setOnClickListener(BtnOnClick);
        resbtn.setOnClickListener(BtnOnClick);
    }
    private Button.OnClickListener BtnOnClick = new Button.OnClickListener(){
        public void onClick(View v){
            Intent intent = new Intent();
            if(v.getId()==R.id.adduserbtn){
                intent.setClass(MainActivity.this,registerActivity.class);
                startActivity(intent);
            }
            if(v.getId()==R.id.reservationbtn){
                intent.setClass(MainActivity.this,authActivity.class);
                startActivity(intent);
            }
        }
    };
}
