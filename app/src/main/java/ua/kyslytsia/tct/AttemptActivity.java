package ua.kyslytsia.tct;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import ua.kyslytsia.tct.utils.Chronometer;


public class AttemptActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attempt);

        final Chronometer ch = (Chronometer) findViewById(R.id.chronometer);

        Button buttonStart = (Button) findViewById(R.id.buttonAttemptStart);

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ch.setBase(SystemClock.elapsedRealtime());
                ch.start();
            }
        });

        Button buttonStop = (Button) findViewById(R.id.buttonAttemptStop);
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ch.stop();
            }
        });

//        Button buttonReset = (Button) findViewById(R.id.buttonAttemptReset);
//        buttonReset.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ch.setBase(SystemClock.elapsedRealtime());
//                ch.stop();
//            }
//        });
    }
}
