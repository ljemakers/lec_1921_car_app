package com.carapp;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

import androidx.core.text.HtmlCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class CarActivity extends ComActivity implements Orientation.Listener {

    WebView videoView ;
    Button forward;
    Button backward;
    Button left;
    Button right;
    Button stop;

    EditText status ;
    EditText pitch ;
    EditText roll ;

    RequestQueue requestQueue ;
    boolean motionEnabled = false ;

    // orientation sensor
    private Orientation orientation;
    private AttitudeIndicator attitudeIndicator;
    // -- orientation sensor

    public double prettyDegree( double degree ) {
        degree = degree % 360 ;

        if( 180 < degree ) {
            degree = degree - 360 ;
        }
        return degree ;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car);

        this.requestQueue = Volley.newRequestQueue(this);

        orientation = new Orientation(this);
        attitudeIndicator = this.findViewById(R.id.attitude_indicator);

        this.status = this.findViewById(R.id.status);
        this.pitch = this.findViewById(R.id.pitch);
        this.roll = this.findViewById(R.id.roll);

        // hide keyboard always
        this.status.setInputType(InputType.TYPE_NULL);
        this.pitch.setInputType(InputType.TYPE_NULL);
        this.roll.setInputType(InputType.TYPE_NULL);

        videoView = this.findViewById(R.id.videoView);
        forward = this.findViewById(R.id.forward);
        backward = this.findViewById(R.id.backward);
        left = this.findViewById(R.id.left);
        right = this.findViewById(R.id.right);
        stop = this.findViewById(R.id.stop);

        forward.setText(HtmlCompat.fromHtml("&uarr;", HtmlCompat.FROM_HTML_MODE_COMPACT));
        backward.setText(HtmlCompat.fromHtml("&darr;", HtmlCompat.FROM_HTML_MODE_COMPACT));
        left.setText(HtmlCompat.fromHtml("&larr;", HtmlCompat.FROM_HTML_MODE_COMPACT));
        right.setText(HtmlCompat.fromHtml("&rarr;", HtmlCompat.FROM_HTML_MODE_COMPACT));
        // stop.setText(HtmlCompat.fromHtml("&bull;", HtmlCompat.FROM_HTML_MODE_COMPACT));

        this.paintUI();

        forward.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                moveCar( "forward" );
            }
        });

        backward.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                moveCar( "backward" );
            }
        });

        left.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                moveCar( "left" );
            }
        });

        right.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                moveCar( "right" );
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if( motionEnabled ) {
                    moveCar("stop");
                }

                motionEnabled = ! motionEnabled ;

                paintUI();
            }
        });
    }

    public void paintUI() {
        if( motionEnabled )  {
            stop.setText( "STOP" );
        } else {
            stop.setText( "START" );
        }

        forward.setEnabled( motionEnabled );
        backward.setEnabled( motionEnabled );
        left.setEnabled( motionEnabled );
        right.setEnabled( motionEnabled );
    }

    private long then = System.currentTimeMillis();

    // pitch roll 값이 변했을 경우, 차를 제어한다.
    public void pitchRollUpdated( double pitch, double roll ) {
        pitch = -prettyDegree(pitch);
        roll = -prettyDegree(roll);

        String text = String.format("pitch: %05.2f  roll %05.2f", pitch, roll);

        this.pitch.setText( String.format( "%5.2f", pitch));
        this.roll.setText( String.format( "%5.2f", roll));

        final long now = System.currentTimeMillis();

        if( ! motionEnabled ) {
            // do nothing!
        }else if (now - then < 700 ) {
            // do nothing!
        } else {
            if (15 <= roll) {
                this.moveCar("right");
            } else if ( -15 >= roll) {
                this.moveCar("left");
            } else if (30 <= pitch) {
                this.moveCar("forward");
            } else if (5 >= pitch) {
                this.moveCar("backward");
            } else {
                this.moveCar("stop");
            }

            then = now ;
        }
    }

    public void moveCar(String motion) {

        // Instantiate the RequestQueue.
        String url = String.format("http://10.3.141.1/car.json?motion=%s", motion);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        status.setText( response.toString() );
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                status.setText("That didn't work!");
            }
        });

        // Add the request to the RequestQueue.
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.v( "sunabove", "onResume");

        videoView.getSettings().setLoadWithOverviewMode(true);
        videoView.getSettings().setUseWideViewPort(true);

        videoView.loadUrl( "http://10.3.141.1/video_feed" );

        orientation.startListening(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        orientation.stopListening();
    }

    @Override
    public void onOrientationChanged(float pitch, float roll) {
        attitudeIndicator.setAttitude(pitch, roll);
    }

}
