package poolgrammers.cra_coimbra;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class NavDrawer extends AppCompatActivity
        implements OnNavigationItemSelectedListener, AlterarDisponibilidade.OnFragmentInteractionListener,
        PesquisarProva.OnFragmentInteractionListener, ResponderPreConvocatoria.OnFragmentInteractionListener {

    private AlarmManager alarmManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TextView nomeArbitro = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nome_arbitro);
        nomeArbitro.setText(MainActivity.readTokenFromFile(this, "nome"));

        startNotificationFetcher();

        //Começa no ecrã de pesquisa de prova, just because
        Fragment fragment = null;
        try {
            fragment = (Fragment) PesquisarProva.class.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragments_content, fragment).commit();


    }

    public void startNotificationFetcher() {
        Intent intent = new Intent(this, NotificationsBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this.getApplicationContext(), 234324243, intent, 0);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long firstTime = SystemClock.elapsedRealtime();
        firstTime += 3 * 1000;
        //De 4 em 4 horas
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime,(60*60*4 * 1000), pendingIntent);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            moveTaskToBack(true);
        }
    }


    //Caso se queiram pôr settings
    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_drawer, menu);
        return true;
    }*/

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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        Fragment fragment = null;
        Class fragmentClass = null;

        int id = item.getItemId();

        if (id == R.id.nav_consultar_prova) {
            fragmentClass = PesquisarProva.class;
        } else if (id == R.id.nav_responder_pre_convocatoria) {
            fragmentClass = ResponderPreConvocatoria.class;
        } else if (id == R.id.nav_alterar_disponibilidade) {
            fragmentClass = AlterarDisponibilidade.class;
        } else if (id == R.id.nav_logout) {
            //Cancels alarm
            Intent intent = new Intent(this, NotificationsBroadcastReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this.getApplicationContext(), 234324243, intent, 0);
            alarmManager.cancel(pendingIntent);
            //Delete token, removes all fragments and finishes activity.
            MainActivity.deleteTokenFile(this);
            FragmentManager fragmentManager = getSupportFragmentManager();
            List<Fragment> fragments = fragmentManager.getFragments();
            if (fragments != null) {
                for (Fragment frag : fragments) {
                    if (frag != null && frag.isVisible())
                        fragmentManager.beginTransaction().remove(frag);
                }
            }
            finish();
            return false;
        } else {
            fragmentClass = PesquisarProva.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragments_content, fragment).commit();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
