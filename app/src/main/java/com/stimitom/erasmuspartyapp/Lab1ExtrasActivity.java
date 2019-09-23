package com.stimitom.erasmuspartyapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class Lab1ExtrasActivity extends AppCompatActivity {

    private Button removeButton;
    private TextView toBeRemovedTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab1_extras);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


        removeButton =  findViewById(R.id.button_remove);
        toBeRemovedTextView = findViewById(R.id.remove_text_view);
        removeButton.setOnClickListener(textViewRemover);


    }

    View.OnClickListener textViewRemover  = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Removes the textView
            ((ViewGroup)toBeRemovedTextView.getParent()).removeView(toBeRemovedTextView);
        }
    };
}
