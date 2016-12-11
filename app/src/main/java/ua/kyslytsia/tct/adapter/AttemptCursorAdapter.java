package ua.kyslytsia.tct.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import java.util.HashMap;

import ua.kyslytsia.tct.R;
import ua.kyslytsia.tct.database.Contract;

public class AttemptCursorAdapter extends CursorAdapter {

    private HashMap<Integer, Integer> inputValues = new HashMap<>();
    private GridView gridView;
    private int position = 0;

    public HashMap<Integer, Integer> getInputValues() {
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
        final ViewHolder holder = (ViewHolder) view.getTag();

        holder.stagePosition = cursor.getInt(cursor.getColumnIndex(Contract.StageOnCompetitionEntry.COLUMN_POSITION));
        holder.stageName = cursor.getString(cursor.getColumnIndex(Contract.STAGE_NAME_ADAPTED));

        if (inputValues.get(holder.stagePosition) != null) {
            holder.stagePenaltyEditText.setText(String.valueOf(inputValues.get(holder.stagePosition)));
        }
        holder.stagePositionView.setText(String.valueOf(holder.stagePosition));
        holder.stageNameView.setText(holder.stageName);

        holder.stagePenaltyEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    EditText editText = (EditText) v.findViewById(R.id.editTextSOAPenaltyOnStage);
                    if (editText.getText().toString().equals("")) {
                        holder.stagePenalty = 0;
                    } else {
                        holder.stagePenalty = Integer.parseInt(editText.getText().toString());
                    }
                    position = holder.stagePosition;
                    inputValues.put(holder.stagePosition, holder.stagePenalty);
                    if (gridView == null) {
                        gridView = (GridView) v.getParent().getParent();
                    }
                }
            }
        });
    }

    private class ViewHolder {
        int stagePosition;
        String stageName;
        int stagePenalty;

        TextView stageNameView;
        TextView stagePositionView;
        EditText stagePenaltyEditText;

        ViewHolder(View view) {
            stagePositionView = (TextView) view.findViewById(R.id.textViewSOAStagePosition);
            stageNameView = (TextView) view.findViewById(R.id.textViewSOAStageName);
            stagePenaltyEditText = (EditText) view.findViewById(R.id.editTextSOAPenaltyOnStage);
        }
    }
}
