package ua.kyslytsia.tct.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import ua.kyslytsia.tct.MainActivity;
import ua.kyslytsia.tct.R;
import ua.kyslytsia.tct.database.Contract;
import ua.kyslytsia.tct.database.DbHelper;

public class CompetitionCursorAdapter extends CursorAdapter {

    public CompetitionCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_competition, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        /*      R.id.textViewItemCompDate,
                R.id.textViewItemCompType,

                R.id.textViewItemCompName,
                R.id.textViewItemCompPlace,

                R.id.textViewItemCompDist,
                R.id.textViewItemCompRank,*/
        DbHelper dbHelper = MainActivity.dbHelper;

        TextView date = (TextView) view.findViewById(R.id.textViewItemCompDate);
        date.setText(cursor.getString(cursor.getColumnIndex(Contract.CompetitionEntry.COLUMN_DATE)));

        TextView type = (TextView) view.findViewById(R.id.textViewItemCompType);
        type.setText(dbHelper.findTypeNameById(cursor.getInt(cursor.getColumnIndex(Contract.CompetitionEntry.COLUMN_TYPE_ID))));

        TextView name = (TextView) view.findViewById(R.id.textViewItemCompName);
        name.setText(cursor.getString(cursor.getColumnIndex(Contract.CompetitionEntry.COLUMN_NAME)));

        TextView place = (TextView) view.findViewById(R.id.textViewItemCompPlace);
        place.setText(cursor.getString(cursor.getColumnIndex(Contract.CompetitionEntry.COLUMN_PLACE)));

        TextView dist = (TextView) view.findViewById(R.id.textViewItemCompDist);
        dist.setText(dbHelper.findDistanceById(cursor.getInt(cursor.getColumnIndex(Contract.CompetitionEntry.COLUMN_DISTANCE_ID))));

        TextView rank = (TextView) view.findViewById(R.id.textViewItemCompRank);
        rank.setText(cursor.getString(cursor.getColumnIndex(Contract.CompetitionEntry.COLUMN_RANK)) + "-й класс");
    }
}
