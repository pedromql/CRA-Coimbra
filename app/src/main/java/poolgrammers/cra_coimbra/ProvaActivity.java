package poolgrammers.cra_coimbra;

import android.app.ProgressDialog;
import android.content.Context;
import android.preference.PreferenceActivity;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.protocol.HTTP;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;



public class ProvaActivity extends FragmentActivity {

    private String uri = "http://testes-poolgrammers.dei.uc.pt/api/get_provas";
    private String uri1 = "http://testes-poolgrammers.dei.uc.pt/api/get_prova";
    public static String USER_TOKEN = "JTrnS8A957WZnEC0Fkkxf67WPtPoXRn5EOUNSceBpF8nCmsduL";
    private ProgressDialog pDialog;
    TableLayout tabela_sessoes;
    LinearLayout info_provas;
    TextView modalidade;
    TextView regulamento;
    TextView local;
    TextView responsavel_cra;
    TextView juiz_arbitro;
    Spinner provas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultar_prova);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

    }


//Todo maybe alterar para oncreateview
    @Override
    protected void onStart() {
        super.onStart();

        tabela_sessoes = (TableLayout) findViewById(R.id.tabela_sessoes);
        info_provas = (LinearLayout) findViewById(R.id.info_provas);

        //Meter a tabela das sessões e info das provas a hidden
       // tabela_sessoes.setVisibility(View.INVISIBLE);
        //info_provas.setVisibility(View.INVISIBLE);
        hideElements(2);

        //Fazer get das provas para o spinner
        getProvas();
        //AKA quando clicamos no spinner fazer get da info
        selectProva();


    }

    //função que esconde ou as infos(0) ou a tabela(1) ou os dois(2)
    private void hideElements(int i) {
        if (i==0){
            info_provas.setVisibility(View.INVISIBLE);
        }
        if (i==1){
            tabela_sessoes.setVisibility(View.INVISIBLE);

        }
        if(i==2) {
            tabela_sessoes.setVisibility(View.INVISIBLE);
            info_provas.setVisibility(View.INVISIBLE);
        }
    }

    //função que mostra ou as infos(0) ou a tabela(1) ou os dois(2)
    private void showElements(int i) {
        if (i==0){
            info_provas.setVisibility(View.VISIBLE);
        }
        if (i==1){
            tabela_sessoes.setVisibility(View.VISIBLE);

        }
        if(i==2) {
            tabela_sessoes.setVisibility(View.VISIBLE);
            info_provas.setVisibility(View.VISIBLE);
        }
    }

    private void getProvas() {
        pDialog.show();
        // Make RESTful webservice call using AsyncHttpClient object
        /**
         * Create the AsyncHttpClient object;
         * call the get() method with the uri, params and the AsynchResponseHandler interface as parameters
         */
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("token",USER_TOKEN);
        client.get(uri, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                onGetProvasSuccess(bytes);
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                onGetProvasFailure(i);
            }
        });
    }


    private void populateSpinner(JSONArray resultProvas) throws JSONException {
        List<String> list = new ArrayList<String>();
        Spinner provas = (Spinner) findViewById(R.id.spinner_provas);
        list.add("");

        for(int i = 0; i< resultProvas.length(); i++){
            JSONObject prova_auxiliar = resultProvas.getJSONObject(i);
            String prova = prova_auxiliar.getString("designacao");
            list.add(prova);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list );
        provas.setAdapter(adapter);
    }



    private void selectProva() {
        provas = (Spinner) findViewById(R.id.spinner_provas);
        tabela_sessoes = (TableLayout) findViewById(R.id.tabela_sessoes);
        info_provas = (LinearLayout) findViewById(R.id.info_provas);

        provas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String provaSelecionada = provas.getSelectedItem().toString();
                hideElements(2);
                getInfoProva(provaSelecionada);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //HIDE DAS INFOS
                hideElements(2);
            }

        });

    }

    private void getInfoProva(String provaSelecionada) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("token",USER_TOKEN);
        params.put("designacao",provaSelecionada);
        client.get(uri1, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                onGetInfoProvaSuccess(bytes);
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                onGetInfoProvaFailure(i);
            }
        });

    }

    public void onGetInfoProvaSuccess(byte[] bytes){
        String response = new String(bytes);
        // Hide Progress Dialog
        pDialog.hide();

        try {
            // JSON Object
            JSONObject jsonResponse = new JSONObject(response);

            if(jsonResponse.getString("success").compareTo("true")==0) {
                if (jsonResponse.has("result")) {
                    JSONArray result = new JSONArray(jsonResponse.getString("result"));
                    fillProvaInfo(result);
                    Toast.makeText(getApplicationContext(), "You are successfully received Provas!", Toast.LENGTH_LONG).show();

                }
                else {
                    Toast.makeText(getApplicationContext(), "Não há provas!", Toast.LENGTH_LONG).show();
                }
            }
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Problemas na ligação ao servidor!", Toast.LENGTH_LONG).show();
            e.printStackTrace();

        }
    }

    private void fillProvaInfo(JSONArray result) throws JSONException {
//            prova = result[0];
//            localidade = result[1];
//            juiz_arbitro = result[2];
//            responsavel_cra = result[3];
//            sessoes = result[4];
//        $("#designacao").html(prova.designacao);
//        $("#modalidade").html(prova.modalidade);
//        $("#local").html(localidade.nome);
//        $("#ja").html(juiz_arbitro.nome);
//        $("#cra").html(responsavel_cra.nome);
//        $("#regulamento").html("<p>" + prova.nome_regulamento + "</p>").attr("href", prova.path_regulamento);
//

        //buscar text input
        modalidade = (TextView) findViewById(R.id.input_modalidade);
        regulamento = (TextView) findViewById(R.id.input_regulamento);
        local = (TextView) findViewById(R.id.input_local);
        responsavel_cra = (TextView) findViewById(R.id.input_responsavel_cra);
        juiz_arbitro = (TextView) findViewById(R.id.input_juiz_arbitro);

        //definir objectos com cada uma das cenas do backend
        JSONObject prova = result.getJSONObject(0);
        JSONObject localidade = result.getJSONObject(1);
        JSONObject juizArbitroObject = result.getJSONObject(2);
        JSONObject responsavelCraObject = result.getJSONObject(3);
        JSONArray sessoes = new JSONArray(result.getString(4));



        //prova.getString("");


        modalidade.setText(prova.getString("modalidade"));
        regulamento.setText(prova.getString("path_regulamento"));
        Linkify.addLinks(regulamento, Linkify.WEB_URLS);
        local.setText(localidade.getString("nome"));
        responsavel_cra.setText(responsavelCraObject.getString("nome"));
        juiz_arbitro.setText(juizArbitroObject.getString("nome"));

        showElements(0);
        addRows(sessoes);
        showElements(1);
    }


    public void onGetInfoProvaFailure(int statusCode){
        // Hide Progress Dialog
        pDialog.hide();

        Toast.makeText(getApplicationContext(), "Problemas na ligação ao servidor!", Toast.LENGTH_LONG).show();


    }





    public void onGetProvasSuccess(byte[] bytes){
        String response = new String(bytes);
        // Hide Progress Dialog
        pDialog.hide();

        try {
            // JSON Object
            JSONObject jsonResponse = new JSONObject(response);


            if(jsonResponse.getString("success").compareTo("true")==0) {
                if (jsonResponse.has("result")) {
                    JSONArray result = new JSONArray(jsonResponse.getString("result"));
                    //for (int i = 0; i < jsonResponse.get("result"))

                    Toast.makeText(getApplicationContext(), "You are successfully received Provas!", Toast.LENGTH_LONG).show();
                    populateSpinner(result);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Não há provas!", Toast.LENGTH_LONG).show();
                }
            }

//            JSONArray obj = new JSONArray("["+response+"]");
//            JSONObject obj1 = (new JSONArray(response)).getJSONObject(0);
//            Toast.makeText(getApplicationContext(), "You are successfully logged in!", Toast.LENGTH_LONG).show();
//            navigatetoHomeActivity(test);
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Problemas na ligação ao servidor!", Toast.LENGTH_LONG).show();
            e.printStackTrace();

        }
    }



    public void onGetProvasFailure(int statusCode){
        // Hide Progress Dialog
        pDialog.hide();

        Toast.makeText(getApplicationContext(), "Problemas na ligação ao servidor!", Toast.LENGTH_LONG).show();


    }



    // TODO remover elementos da tabela  adicionar o segundo digito em meses e horas
    //TODO alinhar :v
    public void addRows(JSONArray sessoes) throws JSONException {
        for (int i = 0; i < sessoes.length(); i+=2) {
            JSONObject sessaoObject = sessoes.getJSONObject(i);


//            /* Find Tablelayout defined in main.xml */
//            TableLayout tl = (TableLayout) findViewById(R.id.tabela_sessoes);
///* Create a new row to be added. */
//            TableRow tr = new TableRow(this);
//            tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
///* Create a Button to be the row-content. */
//            TextView sessao = new TextView(this);
//            sessao.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
///* Add Button to row. */
//            tr.addView(sessao);
///* Add row to TableLayout. */
////tr.setBackgroundResource(R.drawable.sf_gradient_03);
//            tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
//
//
            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams();
            row.setLayoutParams(lp);
            TextView sessao = new TextView(this);
            TextView data = new TextView(this);
            TextView hora = new TextView(this);
            sessao.setText("Sessão"+((i / 2) + 1));
            data.setText(sessaoObject.getString("dia")+"/"+sessaoObject.getString("mes")+"/"+sessaoObject.getString("ano"));
            hora.setText(sessaoObject.getString("hora")+":"+sessaoObject.getString("minutos"));
            row.addView(sessao);
            row.addView(data);
            row.addView(hora);
            tabela_sessoes.addView(row,((i / 2) + 1));

        }
    }


















}



