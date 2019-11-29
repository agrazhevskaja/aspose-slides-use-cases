package com.aspose.slides.demoapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button btnOpenPresentation = findViewById(R.id.btnOpenPresentation);
        btnOpenPresentation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                execPresentationActivity();
            }
        });
    }

    private void execPresentationActivity() {
        Intent openPresentationIntent = new Intent(MainActivity.this, PresentationActivity.class);
        startActivity(openPresentationIntent);
    }
}
