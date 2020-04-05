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

        //use kotlin & migrate to androidX
        textWriter = findViewById(R.id.tw);

        textWriter.setColor(Color.BLACK)
                .setWidth(12)
                .setDelay(40)
                .setSizeFactor(50f)
                .setLetterSpacing(30f)
                .setConfig(TextWriter.Configuration.INTERMEDIATE)
                .setText("DUN KIRK")
                .setListener(new TextWriter.Listener() {
                    @Override
                    public void WritingFinished() {

                        textWriter.setColor(Color.RED);
                        textWriter.setWidth(18);
                        //Toast.makeText(MainActivity.this, "boom", Toast.LENGTH_SHORT).show();
                    }
                })
                .startAnimation();
    }

}
