package poolgrammers.cra_coimbra;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
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

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import poolgrammers.cra_coimbra.Util.InfoProvaItem;

import static poolgrammers.cra_coimbra.Utility.getServerUrl;

public class NavDrawer extends AppCompatActivity
        implements OnNavigationItemSelectedListener, AlterarDisponibilidade.OnFragmentInteractionListener,
        PesquisarProva.OnFragmentInteractionListener, ResponderPreConvocatoria.OnFragmentInteractionListener {

    public DatabaseHelper databaseHelper = new DatabaseHelper(this);

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

        LocalDatabase localDatabase = new LocalDatabase();
        localDatabase.execute();

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

    public class LocalDatabase extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            //Fazer get ao servidor. Se der certo, voltar a criar a BD
            SyncHttpClient client = new SyncHttpClient();
            RequestParams params = new RequestParams();
            params.put("token",MainActivity.readTokenFromFile(getApplicationContext(), "token"));
            params.put("responder","false");
            client.get(getServerUrl()+"get_provas", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int code, Header[] headers, byte[] bytes) {
                    //TODO reiniciar a BD
                    SQLiteDatabase db = databaseHelper.getWritableDatabase();

                    //limpar a BD
                    db.execSQL("delete from prova");
                    db.execSQL("delete from sessao");

                    try {
                        JSONObject jsonResponse = new JSONObject(new String(bytes));
                        if (jsonResponse.getString("success").compareTo("true")==0) {
                            if (jsonResponse.has("result")) {
                                JSONArray result = new JSONArray(jsonResponse.getString("result"));

                                ArrayList<String> provas = new ArrayList<String>();

                                for(int i = 0; i < result.length(); i++){
                                    JSONObject prova = result.getJSONObject(i);
                                    String designacao = prova.getString("designacao");
                                    ContentValues values = new ContentValues();
                                    values.put("designacao",designacao);
                                    db.insert("prova",null, values);
                                    provas.add(designacao);
                                    //TODO get info prova
                                }

                                saveProvaDetails(provas);
                            }
                            else {
//                                Toast.makeText(getContext(), "Não há provas!", Toast.LENGTH_LONG).show();
                            }
                        }

                    } catch (JSONException e) {
//                        Toast.makeText(getContext(), "Problemas na ligação ao servidor!", Toast.LENGTH_LONG).show();
                        e.printStackTrace();

                    }

                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    //TODO neste caso não há nada a fazer, o outros métodos vão usar os dados offline
                    SQLiteDatabase db = databaseHelper.getReadableDatabase();
                    String[] projection = {"*"};
                    Cursor cursor = db.query("prova", projection, null, null, null, null, null);

                    List provas = new ArrayList<>();
                    while(cursor.moveToNext()) {
                        String designacao = cursor.getString(
                                cursor.getColumnIndex("designacao"));
                        String modalidade = cursor.getString(
                                cursor.getColumnIndex("modalidade"));
                        provas.add(designacao);
                        System.out.println("OFFLINE");
                        System.out.println(designacao);
                        System.out.println(modalidade);
                        String[] selector = {designacao};
                        SQLiteDatabase db1 = databaseHelper.getReadableDatabase();
                        Cursor cursorSessoes = db1.query("sessao", projection, "prova_designacao = ?", selector, null, null, null);
                        while (cursorSessoes.moveToNext()) {
                            int id_sessao = cursorSessoes.getInt(
                                    cursorSessoes.getColumnIndex("id_sessao"));
                            System.out.println(id_sessao);
                        }
                    }
                    cursor.close();

                }
            });
            return null;
        }

        public void saveProvaDetails(ArrayList<String> provas) {
            for (int i = 0; i < provas.size(); i++) {
                SyncHttpClient client = new SyncHttpClient();
                RequestParams params = new RequestParams();
                params.put("token",MainActivity.readTokenFromFile(getApplicationContext(), "token"));
                params.put("designacao", provas.get(i));
                client.get(getServerUrl()+"get_prova", params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int code, Header[] headers, byte[] bytes) {
                        //todo update content with retrieved info
                        try {
                            // JSON Object
                            JSONObject jsonResponse = new JSONObject(new String(bytes));

                            if(jsonResponse.getString("success").compareTo("true")==0) {
                                if (jsonResponse.has("result")) {
                                    JSONArray result = new JSONArray(jsonResponse.getString("result"));
                                    JSONObject prova = result.getJSONObject(0);
                                    JSONObject localidade = result.getJSONObject(1);
                                    JSONObject juizArbitroObject = result.getJSONObject(2);
                                    JSONObject responsavelCraObject = result.getJSONObject(3);
                                    JSONArray sessoes = new JSONArray(result.getString(4));

                                    SQLiteDatabase db = databaseHelper.getWritableDatabase();

                                    ContentValues values = new ContentValues();
                                    values.put("modalidade", prova.getString("modalidade"));
                                    values.put("regulamento", prova.getString("path_regulamento"));
                                    values.put("local", localidade.getString("nome"));
                                    values.put("responsavel_cra", responsavelCraObject.getString("nome"));
                                    values.put("juiz_arbitro", juizArbitroObject.getString("nome"));
                                    values.put("id_prova", prova.getString("id_prova"));
                                    values.put("tipo", 1);

                                    String[] selectionArgs = {prova.getString("designacao")};
                                    db.update("prova", values, "designacao = ?", selectionArgs);

                                    for (int i = 0; i < sessoes.length(); i+=2 ) {
                                        JSONObject sessao = sessoes.getJSONObject(i);
                                        values = new ContentValues();
                                        values.put("id_sessao", sessao.getInt("id_sessao"));
                                        values.put("ano", sessao.getInt("ano"));
                                        values.put("mes", sessao.getInt("mes"));
                                        values.put("dia", sessao.getInt("dia"));
                                        values.put("hora", sessao.getInt("hora"));
                                        values.put("minuto", sessao.getInt("minutos"));
                                        values.put("prova_designacao", prova.getString("designacao"));

                                        db.insert("sessao", null, values);
                                    }
                                }
                                else {
//                                    Toast.makeText(getContext(), "Não há provas!", Toast.LENGTH_LONG).show();
                                }
                            }
                        } catch (JSONException e) {
//                            Toast.makeText(getContext(), "Problemas na ligação ao servidor!", Toast.LENGTH_LONG).show();
                            e.printStackTrace();

                        }
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
//                        onGetInfoProvaFailure(i);
                        //nothing to do here
                    }
                });
            }
        }
    }
}
