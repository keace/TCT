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
import ua.kyslytsia.tct.utils.Chronometer;

public class MembersAdapter extends CursorAdapter {

    private String timeString;

    public MembersAdapter(Context context, Cursor c, int flag) {
        super(context, c, flag);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return inflater.inflate(R.layout.item_member, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView team = (TextView) view.findViewById(R.id.textViewMembersTeam);
        team.setText(cursor.getString(cursor.getColumnIndex(Contract.TEAM_NAME_ADAPTED)));

        TextView number = (TextView) view.findViewById(R.id.textViewMembersNumber);
        number.setText(cursor.getString(cursor.getColumnIndex(Contract.MemberEntry.COLUMN_START_NUMBER)));

        TextView name = (TextView) view.findViewById(R.id.textViewMembersName);
        name.setText(cursor.getString(cursor.getColumnIndex(Contract.PersonEntry.COLUMN_LAST_NAME)) + " " + cursor.getString(cursor.getColumnIndex(Contract.PersonEntry.COLUMN_FIRST_NAME)));

        TextView timeTextView = (TextView) view.findViewById(R.id.textViewMembersTime);

        if (cursor.getString(cursor.getColumnIndex(Contract.MemberEntry.COLUMN_RESULT_TIME)) != null) {
            Long timeMillis = Long.parseLong(cursor.getString(cursor.getColumnIndex(Contract.MemberEntry.COLUMN_RESULT_TIME)));
            timeString = new Chronometer(view.getContext()).timeLongMillisToString(timeMillis);
        }
        timeTextView.setText(timeString);
        timeString = "";

        TextView place = (TextView) view.findViewById(R.id.textViewMembersPlace);
        if (cursor.getString(cursor.getColumnIndex(Contract.MemberEntry.COLUMN_PLACE)) != null) {
            place.setText(cursor.getString(cursor.getColumnIndex(Contract.MemberEntry.COLUMN_PLACE)));
        }
    }
}
