package ua.kyslytsia.tct.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import ua.kyslytsia.tct.MainActivity;
import ua.kyslytsia.tct.R;
import ua.kyslytsia.tct.database.Contract;
import ua.kyslytsia.tct.database.DbHelper;

public class CompetitionCursorAdapter extends CursorAdapter {

    public CompetitionCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return inflater.inflate(R.layout.item_competition, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ImageView type = (ImageView) view.findViewById(R.id.imageViewItemCompType);
        int typeId = cursor.getInt(cursor.getColumnIndex(Contract.CompetitionEntry.COLUMN_TYPE_ID));
        switch (typeId) {
            case Contract.TYPE_BIKE_ID:
                type.setImageResource(R.drawable.bike);
        }

        TextView date = (TextView) view.findViewById(R.id.textViewItemCompDate);
        date.setText(cursor.getString(cursor.getColumnIndex(Contract.CompetitionEntry.COLUMN_DATE)));

        TextView typeName = (TextView) view.findViewById(R.id.textViewItemCompType);
        typeName.setText(cursor.getString(cursor.getColumnIndex(Contract.TypeEntry.COLUMN_NAME))); //adapted

        TextView name = (TextView) view.findViewById(R.id.textViewItemCompName);
        name.setText(cursor.getString(cursor.getColumnIndex(Contract.CompetitionEntry.COLUMN_NAME)));

        TextView place = (TextView) view.findViewById(R.id.textViewItemCompPlace);
        place.setText(cursor.getString(cursor.getColumnIndex(Contract.CompetitionEntry.COLUMN_PLACE)));

        TextView dist = (TextView) view.findViewById(R.id.textViewItemCompDist);
        dist.setText(cursor.getString(cursor.getColumnIndex(Contract.DistanceEntry.COLUMN_NAME))); //adapted

        TextView distId = (TextView) view.findViewById(R.id.textViewItemCompDistId);
        distId.setText(cursor.getString(cursor.getColumnIndex(Contract.CompetitionEntry.COLUMN_DISTANCE_ID)));

        TextView rank = (TextView) view.findViewById(R.id.textViewItemCompRank);
        rank.setText(cursor.getString(cursor.getColumnIndex(Contract.CompetitionEntry.COLUMN_RANK)) + context.getString(R.string.maim_layout_rank_concat));

        TextView isClosed = (TextView) view.findViewById(R.id.textViewItemCompIsClosed);
        isClosed.setText(cursor.getString(cursor.getColumnIndex(Contract.CompetitionEntry.COLUMN_IS_CLOSED)));
    }
}
