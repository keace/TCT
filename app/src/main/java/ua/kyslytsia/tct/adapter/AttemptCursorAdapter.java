package ua.kyslytsia.tct.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;

import ua.kyslytsia.tct.R;
import ua.kyslytsia.tct.database.Contract;

public class AttemptCursorAdapter extends CursorAdapter {

    private HashMap<String, String> inputValues = new HashMap<>();
    private GridView lv;
    private int position = 1;
    int positionFlag = 0;

    public HashMap<String, String> getInputValues() {
        return inputValues;
    }

    public AttemptCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_stage_on_attempt, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(final View view, Context context, Cursor cursor) {
//        TextView stage = (TextView) view.findViewById(R.id.textViewSOAStageName);
//        stage.setText(cursor.getString(cursor.getColumnIndex(Contract.STAGE_NAME_ADAPTED)));
        final ViewHolder holder = (ViewHolder) view.getTag();

        holder.stagePosition = cursor.getString(cursor.getColumnIndex(Contract.StageOnCompetitionEntry.COLUMN_POSITION));
        holder.stageName = cursor.getString(cursor.getColumnIndex(Contract.STAGE_NAME_ADAPTED));

        holder.stagePenaltyEditText.setText(inputValues.get(holder.stageName));
        holder.stagePositionView.setText(holder.stagePosition);
        holder.stageNameView.setText(holder.stageName);

//        holder.stagePenaltyEditText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                // do nothing
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
////                holder.stagePenalty = s.toString();
//                inputValues.put(holder.stageName, s.toString());
//                Log.i("AttCurAdd", holder.stageName + " = " + holder.stagePenalty + ", pos = " + holder.stagePosition);
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                //do nothing
//
//            }
//        });
        holder.stagePenaltyEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    EditText editText = (EditText) v.findViewById(R.id.editTextSOAPenaltyOnStage);
                    holder.stagePenalty = editText.getText().toString();
                    position = Integer.parseInt(holder.stagePosition);
                    positionFlag = 1;
                    inputValues.put(holder.stageName, holder.stagePenalty);
                    Log.i("AttCurAdd", holder.stageName + " = " + holder.stagePenalty + ", pos = " + holder.stagePosition);
                    if (lv == null) {
                        lv = (GridView) v.getParent().getParent();
                    }
                }
            }
        });
    }

    private class ViewHolder {
        String stagePosition;
        String stageName;
        String stagePenalty;

        TextView stageNameView;
        TextView stagePositionView;
        EditText stagePenaltyEditText;

        public ViewHolder(View view) {
            stagePositionView = (TextView) view.findViewById(R.id.textViewSOAStagePosition);
            stageNameView = (TextView) view.findViewById(R.id.textViewSOAStageName);
            stagePenaltyEditText = (EditText) view.findViewById(R.id.editTextSOAPenaltyOnStage);
        }
    }
}
