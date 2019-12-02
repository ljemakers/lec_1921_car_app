package com.carapp;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends ComActivity {

    boolean isActive = false ;
    boolean serverActive = false ;
    TextView status ;

    int mode = 0 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.status = this.findViewById(R.id.status);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.v( "sunabove", "onResume");

        this.isActive = true ;
        serverActive = false ;

        checkServer();
    }

    private void checkServer() {

        status.setTextColor(Color.parseColor("#009688"));
        status.setText( "서버 연결중입니다.\n잠시만 기다려 주세요!" );

        new Thread(new Runnable() {
            @Override
            public void run() {
                while( ! serverActive && isActive ) {
                    try {
                        mode = 1;

                        sleep( 3000 );

                        URL url = new URL("http://10.3.141.1/info.html");
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        //conn.setConnectTimeout( 10_000 ); // timing out in a minute

                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String str;
                        while ((str = in.readLine()) != null) {
                            //
                        }

                        serverActive = true;

                        sleep( 3_000 );

                        mode = 2;

                    } catch (Exception e) {
                        mode = 3 ;
                    }

                    sleep( 5_000 );
                }
            }
        }).start();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("sunabove", "run: postDelayerd");
                if( 1 == mode ) {
                    status.setTextColor(Color.parseColor("#009688"));
                    status.setText( "서버 연결중입니다.\n잠시만 기다려 주세요!" );
                } else if ( 2 == mode ) {
                    status.setTextColor(Color.parseColor("#009688"));
                    status.setText( "서버에 성공하였습니다." );

                    sleep( 2_000 );
                } else if ( 3 == mode ) {
                    status.setTextColor(Color.parseColor("#FF0000"));
                    status.setText( "서버 연결에 실패하였습니다.\n잠시후 다시 연결을 시도합니다." );
                }

                if( serverActive ) {
                    isActive = false ;
                    startActivity(new android.content.Intent( MainActivity.this, com.carapp.CarActivity.class));
                } else {
                    handler.postDelayed( this, 500 );
                }
            }
        }, 500);
    }
}
