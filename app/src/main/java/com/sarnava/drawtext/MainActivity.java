package com.sarnava.drawtext;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.sarnava.textwriter.TextWriter;

public class MainActivity extends AppCompatActivity {

    TextWriter textWriter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textWriter = findViewById(R.id.tw);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                textWriter.setColor(Color.BLACK)
                        .setWidth(12)
                        .setDelay(20)
                        .setSizeFactor(40f)
                        .setLetterSpacing(30f)
                        .setConfig(TextWriter.Configuration.INTERMEDIATE)
                        .setText("JURASSICPARK")
                        .setListener(new TextWriter.Listener() {
                            @Override
                            public void WritingFinished() {
                            }
                        })
                        .startAnimation();
            }
        }, 200);
    }

}
