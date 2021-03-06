package ua.kyslytsia.tct.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import ua.kyslytsia.tct.R;
import ua.kyslytsia.tct.database.Contract;

public class StageOnCompetitionCursorAdapter extends CursorAdapter {

    public StageOnCompetitionCursorAdapter(Context context, Cursor c, int flag) {
        super(context, c, flag);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_stage_on_comp, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor c) {
        TextView stagePosition = (TextView) view.findViewById(R.id.textViewSOCPosition);
        stagePosition.setText(String.valueOf(c.getInt(c.getColumnIndex(Contract.StageOnCompetitionEntry.COLUMN_POSITION))));

        TextView stageName = (TextView) view.findViewById(R.id.textViewSOCName);
        stageName.setText(c.getString(c.getColumnIndex(Contract.StageEntry.COLUMN_NAME))); //adapt
    }
}
