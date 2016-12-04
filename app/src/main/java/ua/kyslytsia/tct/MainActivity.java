package ua.kyslytsia.tct;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import ua.kyslytsia.tct.adapter.CompetitionCursorAdapter;
import ua.kyslytsia.tct.database.ContentProvider;
import ua.kyslytsia.tct.database.Contract;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG = "Log! Main Activity";

    ListView listViewMain;
    CompetitionCursorAdapter competitionCursorAdapter;
    SharedPreferences sharedPreferences;
    Toolbar toolbar;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = initToolbar();

        initCompetitionsListView();
        initFAB();
        initNavigationDrawer(toolbar);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }
    }

/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }*/

/*    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_calculator: {
                Intent calcIntent = new Intent(this, CalculatorActivity.class);
                startActivity(calcIntent);
                break;
            }
            case R.id.nav_dbhelp: {
                Intent dbHelpIntent = new Intent(this, DBHelpActivity.class);
                startActivity(dbHelpIntent);
                break;
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, ContentProvider.COMPETITION_CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        competitionCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        competitionCursorAdapter.swapCursor(null);
    }

    private Toolbar initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setSubtitle(R.string.main_activity_toolbar_subtitle);
        }
        setSupportActionBar(toolbar);
        return toolbar;
    }

    @Override
    protected void onRestart() {
        getContentResolver().notifyChange(ContentProvider.COMPETITION_CONTENT_URI, null);
        super.onRestart();
    }

    private void initFAB() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent newCompetitionIntent = new Intent(MainActivity.this, NewCompetitionActivity.class);
                    startActivity(newCompetitionIntent);
                }
            });
        }
    }

    private void initNavigationDrawer(Toolbar toolbar) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (drawer != null) {
            drawer.addDrawerListener(toggle);
        }
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }
    }

    private void initCompetitionsListView(){
        listViewMain = (ListView) findViewById(R.id.listViewMain);
        getSupportLoaderManager().initLoader(Contract.COMPETITIONS_LOADER_ID, null, this);
        competitionCursorAdapter = new CompetitionCursorAdapter(this, null, Contract.COMPETITIONS_LOADER_ID);

        listViewMain.setAdapter(competitionCursorAdapter);
        listViewMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                putIdsToSharedPreferencesAndGoToMembers(view, id);
            }
        });

        listViewMain.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, final long id) {
                showCompetitionAlertDialog(id);
                return true;
            }
        });
    }
    private void showCompetitionAlertDialog(final long id){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle(R.string.main_activity_alert_dialog_title)
                .setNegativeButton(R.string.main_activity_alert_dialog_negative, null)
                .setPositiveButton(R.string.main_activity_alert_dialog_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteCompetition(id);
            }
        });
        alertDialog.create();
        alertDialog.show();
    }

    private void putIdsToSharedPreferencesAndGoToMembers(View view, long competitionId){
        TextView textViewDistanceId = (TextView) view.findViewById(R.id.textViewItemCompDistId);
        TextView textViewIsClosed = (TextView) view.findViewById(R.id.textViewItemCompIsClosed);
        long distanceId = Long.parseLong(textViewDistanceId.getText().toString());
        int isClosed = Integer.parseInt(textViewIsClosed.getText().toString());

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putLong(Contract.StageOnCompetitionEntry.COLUMN_COMPETITION_ID, competitionId).apply();
        sharedPreferences.edit().putLong(Contract.CompetitionEntry.COLUMN_DISTANCE_ID, distanceId).apply();
        sharedPreferences.edit().putInt(Contract.CompetitionEntry.COLUMN_IS_CLOSED, isClosed).apply();
        Log.i(LOG, "Get shared preferences competition id = " + sharedPreferences.getLong(Contract.StageOnCompetitionEntry.COLUMN_COMPETITION_ID, 0) +
                "distance id = " + sharedPreferences.getLong(Contract.CompetitionEntry.COLUMN_DISTANCE_ID, 0) +
                "is closed = " + sharedPreferences.getInt(Contract.CompetitionEntry.COLUMN_IS_CLOSED, 0));

        Intent membersIntent = new Intent(MainActivity.this, MembersActivity.class);
        startActivity(membersIntent);
    }

    private void deleteCompetition (long id) {
        String where = Contract.CompetitionEntry._ID + "=?";
        String[] args = new String[]{String.valueOf(id)};
        getContentResolver().delete(ContentProvider.COMPETITION_CONTENT_URI, where, args);
    }
}