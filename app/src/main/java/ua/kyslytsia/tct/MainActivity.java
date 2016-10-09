package ua.kyslytsia.tct;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import ua.kyslytsia.tct.adapter.CompetitionCursorAdapter;
import ua.kyslytsia.tct.database.Contract;
import ua.kyslytsia.tct.database.DbHelper;
import ua.kyslytsia.tct.database.TctDb;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static DbHelper dbHelper;
    public SQLiteDatabase sqLiteDatabase;
    ListView listViewMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHelper = new DbHelper(this);
        sqLiteDatabase = dbHelper.getWritableDatabase();

//        dbHelper.onUpgrade(dbHelper.sqLiteDatabase, 3, 4);
//        dbHelper.onCreate(dbHelper.sqLiteDatabase);

        TextView textViewMain = (TextView) findViewById(R.id.textViewMain);

        textViewMain.setText(dbHelper.getDatabaseName());

        listViewMain = (ListView) findViewById(R.id.listViewMain);
        /*String[] fromComp = new String[]{
                Contract.CompetitionEntry.COLUMN_DATE,
                Contract.CompetitionEntry.COLUMN_NAME,
                Contract.CompetitionEntry.COLUMN_PLACE,
                Contract.CompetitionEntry.COLUMN_TYPE_ID,
                //dbHelper.findTypeNameById(Integer.valueOf(Contract.CompetitionEntry.COLUMN_TYPE_ID)),
                Contract.CompetitionEntry.COLUMN_DISTANCE_ID,
                Contract.CompetitionEntry.COLUMN_RANK};

        int[] toComp = new int[] {
                R.id.textViewItemCompDate,
                R.id.textViewItemCompName,
                R.id.textViewItemCompPlace,
                R.id.textViewItemCompType,
                R.id.textViewItemCompDist,
                R.id.textViewItemCompRank,
        };
*/
        Cursor cursorComp = sqLiteDatabase.query(Contract.CompetitionEntry.TABLE_NAME, null, null, null, null, null, null);
/*
        SimpleCursorAdapter cursorAdapterComp = new SimpleCursorAdapter(this, R.layout.item_competition, cursorComp, fromComp, toComp, 1);
       */
        CompetitionCursorAdapter competitionCursorAdapter = new CompetitionCursorAdapter(this, cursorComp, true);
        listViewMain.setAdapter(competitionCursorAdapter);

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
            drawer.setDrawerListener(toggle);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
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
    }

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
        }
        /*if (id == R.id.nav_dbhelp) {

        }
        /*else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
