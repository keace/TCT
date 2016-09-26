package ua.kyslytsia.tct;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.GregorianCalendar;

public class CalculatorActivity extends AppCompatActivity {

    static EditText inputMinutes;
    static EditText inputSeconds;
    static EditText inputMillis;
    static EditText inputDistancePenalty;
    static EditText penaltyCost;
    static GregorianCalendar gregorianCalendar;

    TextView timeWithPenalty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_calculator);

        inputMinutes = (EditText) findViewById(R.id.inputMinutes);
        inputSeconds = (EditText) findViewById(R.id.inputSeconds);
        inputMillis = (EditText) findViewById(R.id.inputMillis);
        inputDistancePenalty = (EditText) findViewById(R.id.inputDistancePenalty);
        penaltyCost = (EditText) findViewById(R.id.penaltyCost);
        timeWithPenalty = (TextView) findViewById(R.id.timeWithPenalty);

        gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.clear();
    }

    public void takeResult (View v) {
        if (inputMinutes.getText().toString().isEmpty()) {
            inputMinutes.setText("0");
        }

        if (inputSeconds.getText().toString().isEmpty()) {
            inputSeconds.setText("0");
        }

        if (inputMillis.getText().toString().isEmpty()) {
            inputMillis.setText("0");
        }

        if (inputDistancePenalty.getText().toString().isEmpty()) {
            inputDistancePenalty.setText("0");
        }

        if (penaltyCost.getText().toString().isEmpty()) {
            penaltyCost.setText("0");
        }

        int penaltyTime = Integer.valueOf(inputDistancePenalty.getText().toString()) * Integer.valueOf(penaltyCost.getText().toString());
        gregorianCalendar.set(gregorianCalendar.MINUTE, Integer.valueOf(inputMinutes.getText().toString()));
        gregorianCalendar.set(gregorianCalendar.SECOND, Integer.valueOf(inputSeconds.getText().toString()));
        gregorianCalendar.set(gregorianCalendar.MILLISECOND, Integer.valueOf(inputMillis.getText().toString()));

        gregorianCalendar.add(gregorianCalendar.SECOND, penaltyTime);

        timeWithPenalty.setText(gregorianCalendar.get(gregorianCalendar.MINUTE) + ":" + gregorianCalendar.get(gregorianCalendar.SECOND) + ":" + gregorianCalendar.get(gregorianCalendar.MILLISECOND));
    }

    public void clearFields(View v) {
        inputMinutes.setText("");
        inputSeconds.setText("");
        inputMillis.setText("");
        inputDistancePenalty.setText("");

    }
}
