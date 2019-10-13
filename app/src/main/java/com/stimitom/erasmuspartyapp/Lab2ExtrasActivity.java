package com.stimitom.erasmuspartyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Lab2ExtrasActivity extends AppCompatActivity implements RequestOperator.RequestOperatorListener {
    private Button sendRequestButton;
    private TextView title;
    private TextView bodyText;
    private ModelPost publication;
    private IndicatingView indicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab2_extras);

        sendRequestButton = (Button) findViewById(R.id.send_request_button);
        title  =(TextView) findViewById(R.id.text_view_title_lab2);
        bodyText = (TextView)findViewById(R.id.text_view_description_lab2);
        indicator = (IndicatingView) findViewById(R.id.generated_graphic);

        sendRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });
    }

    public void sendRequest(){
        RequestOperator ro = new RequestOperator();
        ro.setListener(this);
        ro.start();
    }

    public void updatePublication(){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (publication!=null) {
                    title.setText(publication.getTitle());
                    bodyText.setText(publication.getBodyText());
                }else {
                    title.setText("");
                    bodyText.setText("");
                }
            }
        });
    }

    public void setIndicatorStatus(final int status){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                indicator.setState(status);
                indicator.invalidate();
            }
        });
    }

    @Override
    public void success(ModelPost publication) {
        this.publication = publication;
        updatePublication();
        setIndicatorStatus(IndicatingView.SUCCESS);
    }

    @Override
    public void failed(int responseCode) {
        this.publication = null;
        updatePublication();
        setIndicatorStatus(IndicatingView.FAILED);
    }
}
