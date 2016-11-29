package ua.kyslytsia.tct.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;
import java.util.GregorianCalendar;

import ua.kyslytsia.tct.R;

public class CalculatorPenaltyFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final int FRAGMENT_PENALTY_ID = 0;

    private EditText inputMinutes, inputSeconds, inputMillis, inputDistancePenalty, penaltyCost;
    private TextView timeWithPenalty;

    private static GregorianCalendar gregorianCalendar;
    private static StringBuffer sb;

    public CalculatorPenaltyFragment() {
    }

    public static CalculatorPenaltyFragment newInstance() {
        CalculatorPenaltyFragment fragment = new CalculatorPenaltyFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, FRAGMENT_PENALTY_ID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_penalty, container, false);

        inputMinutes = (EditText) rootView.findViewById(R.id.inputMinutes);
        inputSeconds = (EditText) rootView.findViewById(R.id.inputSeconds);
        inputMillis = (EditText) rootView.findViewById(R.id.inputMillis);
        inputDistancePenalty = (EditText) rootView.findViewById(R.id.inputDistancePenalty);
        penaltyCost = (EditText) rootView.findViewById(R.id.penaltyCost);
        timeWithPenalty = (TextView) rootView.findViewById(R.id.timeWithPenalty);

        Button buttonTakeResult = (Button) rootView.findViewById(R.id.buttonPenaltyGetResultTime);
        buttonTakeResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeResult();
            }
        });

        Button buttonClear = (Button) rootView.findViewById(R.id.buttonPenaltyClear);
        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFields();
            }
        });

        gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.clear();

        sb = new StringBuffer();

        return rootView;
    }

    private void takeResult () {
        // If user not input some values set it to "0"
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

        // Calculate the result
        int penaltyTime = Integer.valueOf(inputDistancePenalty.getText().toString()) * Integer.valueOf(penaltyCost.getText().toString());
        gregorianCalendar.set(Calendar.MINUTE, Integer.valueOf(inputMinutes.getText().toString()));
        gregorianCalendar.set(Calendar.SECOND, Integer.valueOf(inputSeconds.getText().toString()));
        gregorianCalendar.set(Calendar.MILLISECOND, Integer.valueOf(inputMillis.getText().toString()));

        if (gregorianCalendar.get(Calendar.MILLISECOND) > 99) {
                int seconds = gregorianCalendar.get(Calendar.MILLISECOND) / 100;
                int millis = gregorianCalendar.get(Calendar.MILLISECOND) % 100;
                gregorianCalendar.add(Calendar.SECOND, seconds);
                gregorianCalendar.set(Calendar.MILLISECOND, millis);
        }

        gregorianCalendar.add(Calendar.SECOND, penaltyTime);

        sb.append(gregorianCalendar.get(Calendar.MINUTE)).append(":").append(gregorianCalendar.get(Calendar.SECOND)).append(":").append(gregorianCalendar.get(Calendar.MILLISECOND));
        timeWithPenalty.setText(sb.toString());
        sb.delete(0, sb.length());
    }

    private void clearFields() {
        inputMinutes.setText("");
        inputSeconds.setText("");
        inputMillis.setText("");
        inputDistancePenalty.setText("");
        inputMinutes.requestFocus();
    }
}