package ua.kyslytsia.tct.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.app.DialogFragment;
import android.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import ua.kyslytsia.tct.NewMemberActivity;
import ua.kyslytsia.tct.database.ContentProvider;
import ua.kyslytsia.tct.database.Contract;

public class MembersDialogFragment extends DialogFragment {
    private static final String LOG = "MembersDialogFragment";
    //static long membersId;

    public static MembersDialogFragment newInstance(long memberId) {
        MembersDialogFragment dialogFragment = new MembersDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("memberId", memberId);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final long memberId = getArguments().getLong("memberId");
        Log.i(LOG, "From bundle memberId = " + memberId);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        String[] items = new String[] {"Edit...", "Delete"};
        dialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Log.i(LOG, "Edit... picked");
                        Toast.makeText(getActivity().getApplicationContext(), "Edit... picked", Toast.LENGTH_SHORT).show();
//                        PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).edit().putLong(Contract.MEMBER_ID_ADAPTED, memberId).apply();
                        Intent intentToNewMember = new Intent(getActivity(), NewMemberActivity.class);
                        intentToNewMember.putExtra(Contract.MEMBER_ID_ADAPTED, memberId);
                        startActivity(intentToNewMember);
                        break;
                    case 1:
                        Log.i(LOG, "Delete picked");
                        Toast.makeText(getActivity().getApplicationContext(), "Delete picked", Toast.LENGTH_SHORT).show();
                        String where = Contract.MemberEntry._ID + "=?";
                        String[] whereArgs = new String[] {String.valueOf(memberId)};
                        getActivity().getContentResolver().delete(ContentProvider.MEMBER_CONTENT_URI, where, whereArgs);
                        break;
                }
            }
        });
        return dialogBuilder.create();
    }

//    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity().getApplicationContext());
//    builder.setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
//        @Override
//        public void onClick(DialogInterface dialog, int which) {
//            Log.i(LOG, "Удалить нажато");
//        }
//    });
//    builder.setNeutralButton("Отмена", null);
}