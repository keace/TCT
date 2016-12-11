package ua.kyslytsia.tct.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.CursorAdapter;

import ua.kyslytsia.tct.MainActivity;
import ua.kyslytsia.tct.R;
import ua.kyslytsia.tct.database.Contract;
import ua.kyslytsia.tct.database.DbHelper;

public class AddStageCursorAdapter extends CursorAdapter {

        public AddStageCursorAdapter (Context context, Cursor c, int flag) {
            super(context, c, flag);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.item_add_stage, parent, false);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor c) {

            CheckedTextView addStage = (CheckedTextView) view.findViewById(R.id.checkBoxAddStage);
            addStage.setText(c.getString(c.getColumnIndex(Contract.StageEntry.COLUMN_NAME)));
        }
}