package ua.kyslytsia.tct;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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
import ua.kyslytsia.tct.database.DbHelper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG = "Log! Main Activity";
    public static DbHelper dbHelper;
    public SQLiteDatabase sqLiteDatabase;
    ListView listViewMain;
    CompetitionCursorAdapter competitionCursorAdapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setSubtitle("Список соревнований");
        setSupportActionBar(toolbar);

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        dbHelper = new DbHelper(this);
        sqLiteDatabase = dbHelper.getWritableDatabase();

        //dbHelper.onUpgrade(sqLiteDatabase, 6, 7);

        //TextView textViewMain = (TextView) findViewById(R.id.textViewMain);

        //textViewMain.setText(dbHelper.getDatabaseName());

        listViewMain = (ListView) findViewById(R.id.listViewMain);
        getSupportLoaderManager().initLoader(Contract.COMPETITIONS_LOADER_ID, null, this);
        //Cursor cursorComp = sqLiteDatabase.query(Contract.CompetitionEntry.TABLE_NAME, null, null, null, null, null, null);

        competitionCursorAdapter = new CompetitionCursorAdapter(this, null, Contract.COMPETITIONS_LOADER_ID);
        listViewMain.setAdapter(competitionCursorAdapter);
        listViewMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent membersIntent = new Intent(MainActivity.this, MembersActivity.class);
                //membersIntent.putExtra(Contract.StageOnCompetitionEntry.COLUMN_COMPETITION_ID, id);
                TextView textViewDistanceId = (TextView) view.findViewById(R.id.textViewItemCompDistId);
                TextView textViewIsClosed = (TextView) view.findViewById(R.id.textViewItemCompIsClosed);
                long distanceId = Long.parseLong(textViewDistanceId.getText().toString());
                int isClosed = Integer.parseInt(textViewIsClosed.getText().toString());
                sharedPreferences.edit().putLong(Contract.StageOnCompetitionEntry.COLUMN_COMPETITION_ID, id).apply();
                sharedPreferences.edit().putLong(Contract.CompetitionEntry.COLUMN_DISTANCE_ID, distanceId).apply();
                sharedPreferences.edit().putInt(Contract.CompetitionEntry.COLUMN_IS_CLOSED, isClosed).apply();
                Log.i(LOG, "Get shared preferences competition id = " + sharedPreferences.getLong(Contract.StageOnCompetitionEntry.COLUMN_COMPETITION_ID, 0));
                Log.i(LOG, "Get shared preferences distance id = " + sharedPreferences.getLong(Contract.CompetitionEntry.COLUMN_DISTANCE_ID, 0));
                Log.i(LOG, "Get shared preferences is closed = " + sharedPreferences.getInt(Contract.CompetitionEntry.COLUMN_IS_CLOSED, 0));
                //Log.i(LOG, "Put competition_id to intent = " + id);
                startActivity(membersIntent);
            }
        });

        // FAB
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent newCompetitionIntent = new Intent(MainActivity.this, NewCompetitionActivity.class);
                    startActivity(newCompetitionIntent);
                    /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                            */
                }
            });
        }

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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
        // Handle navigation view item clicks here.
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
            case R.id.attempt: {
                Intent dbAttemptIntent = new Intent(this, AttemptActivity.class);
                startActivity(dbAttemptIntent);
                break;
            }
        }
        /*if (competitionId == R.competitionId.nav_dbhelp) {

        }
        /*else if (competitionId == R.competitionId.nav_gallery) {

        } else if (competitionId == R.competitionId.nav_slideshow) {

        } else if (competitionId == R.competitionId.nav_manage) {

        } else if (competitionId == R.competitionId.nav_share) {

        } else if (competitionId == R.competitionId.nav_send) {

        }
*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = new CursorLoader(MainActivity.this, ContentProvider.COMPETITION_CONTENT_URI, null, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        competitionCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        competitionCursorAdapter.swapCursor(null);
    }
}
