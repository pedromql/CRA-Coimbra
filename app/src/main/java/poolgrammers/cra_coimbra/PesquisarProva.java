package poolgrammers.cra_coimbra;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import poolgrammers.cra_coimbra.Util.InfoProvaItem;
import poolgrammers.cra_coimbra.Util.SessionItem;

import static poolgrammers.cra_coimbra.Utility.getServerUrl;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PesquisarProva.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PesquisarProva#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PesquisarProva extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    View mainView;

    private String uri = getServerUrl()+"get_provas";
    private String uri1 = getServerUrl()+"get_prova";
    private ProgressDialog pDialog;
    TableLayout tabela_sessoes;

    ListView info_provas;
    ConsultaProvaAdapter consultaProvaAdapter;
    List<InfoProvaItem> infoProvaArray = new ArrayList<>();

    TextView modalidade;
    TextView regulamento;
    TextView local;
    TextView responsavel_cra;
    TextView juiz_arbitro;
    Spinner provas;

    private OnFragmentInteractionListener mListener;

    public PesquisarProva() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PesquisarProva.
     */
    // TODO: Rename and change types and number of parameters
    public static PesquisarProva newInstance(String param1, String param2) {
        PesquisarProva fragment = new PesquisarProva();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pesquisar_prova, container, false);
        mainView = view;

        pDialog = new ProgressDialog(getContext());
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

//        tabela_sessoes = (TableLayout) view.findViewById(R.id.tabela_sessoes);
//        info_provas = (LinearLayout) view.findViewById(R.id.info_provas);

        info_provas = (ListView) view.findViewById(R.id.consultar_prova_list);
        consultaProvaAdapter = new ConsultaProvaAdapter(this.getContext(), R.layout.info_provas, infoProvaArray);
        info_provas.setAdapter(consultaProvaAdapter);

        //Meter a tabela das sessões e info das provas a hidden
        // tabela_sessoes.setVisibility(View.INVISIBLE);
        //info_provas.setVisibility(View.INVISIBLE);
//        hideElements(2);

        //Fazer get das provas para o spinner
        getProvas();
        //AKA quando clicamos no spinner fazer get da info
        selectProva();

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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
        params.put("token",MainActivity.readTokenFromFile(getContext(), "token"));
        params.put("responder","false");
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
        Spinner provas = (Spinner) mainView.findViewById(R.id.spinner_provas);
        //list.add("");

        for(int i = 0; i< resultProvas.length(); i++){
            JSONObject prova_auxiliar = resultProvas.getJSONObject(i);
            String prova = prova_auxiliar.getString("designacao");
            list.add(prova);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, list );
        provas.setAdapter(adapter);
    }



    private void selectProva() {
        provas = (Spinner) mainView.findViewById(R.id.spinner_provas);
//        tabela_sessoes = (TableLayout) mainView.findViewById(R.id.tabela_sessoes);
//        info_provas = (LinearLayout) mainView.findViewById(R.id.info_provas);

        provas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String provaSelecionada = provas.getSelectedItem().toString();
//                hideElements(2);
                infoProvaArray.clear();
                getInfoProva(provaSelecionada);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //HIDE DAS INFOS
//                hideElements(2);
                infoProvaArray.clear();
                consultaProvaAdapter.notifyDataSetChanged();
            }

        });

    }

    private void getInfoProva(String provaSelecionada) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("token",MainActivity.readTokenFromFile(getContext(), "token"));
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
//                    Toast.makeText(getContext(), "You have successfully received Provas!", Toast.LENGTH_LONG).show();

                }
                else {
                    Toast.makeText(getContext(), "Não há provas!", Toast.LENGTH_LONG).show();
                }
            }
        } catch (JSONException e) {
            Toast.makeText(getContext(), "Problemas na ligação ao servidor!", Toast.LENGTH_LONG).show();
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
        JSONObject prova = result.getJSONObject(0);
        JSONObject localidade = result.getJSONObject(1);
        JSONObject juizArbitroObject = result.getJSONObject(2);
        JSONObject responsavelCraObject = result.getJSONObject(3);
        JSONArray sessoes = new JSONArray(result.getString(4));

        infoProvaArray.add(new InfoProvaItem(1, prova.getString("modalidade"), prova.getString("path_regulamento"), localidade.getString("nome"), responsavelCraObject.getString("nome"), juizArbitroObject.getString("nome")));
        for (int i = 0; i < sessoes.length(); i+=2) {
            JSONObject sessao = sessoes.getJSONObject(i);
            String data = String.format(Locale.UK, "%02d", sessao.getInt("dia")) + "/" + String.format(Locale.UK, "%02d", sessao.getInt("mes")) + "/" + sessao.getInt("ano");
            String tempo = String.format(Locale.UK, "%02d", sessao.getInt("hora")) + ":" + String.format(Locale.UK, "%02d", sessao.getInt("minutos"));
            infoProvaArray.add(new InfoProvaItem(2, data, tempo));

        }
        consultaProvaAdapter.notifyDataSetChanged();
    }


    public void onGetInfoProvaFailure(int statusCode){
        // Hide Progress Dialog
        pDialog.hide();

        Toast.makeText(getContext(), "Problemas na ligação ao servidor!", Toast.LENGTH_LONG).show();


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

                    populateSpinner(result);
                }
                else {
                    Toast.makeText(getContext(), "Não há provas!", Toast.LENGTH_LONG).show();
                }
            }

//            JSONArray obj = new JSONArray("["+response+"]");
//            JSONObject obj1 = (new JSONArray(response)).getJSONObject(0);
//            Toast.makeText(getApplicationContext(), "You are successfully logged in!", Toast.LENGTH_LONG).show();
//            navigatetoHomeActivity(test);
        } catch (JSONException e) {
            Toast.makeText(getContext(), "Problemas na ligação ao servidor!", Toast.LENGTH_LONG).show();
            e.printStackTrace();

        }
    }



    public void onGetProvasFailure(int statusCode){
        // Hide Progress Dialog
        pDialog.hide();

        Toast.makeText(getContext(), "Problemas na ligação ao servidor!", Toast.LENGTH_LONG).show();


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
            TableRow row = new TableRow(getContext());
            TableRow.LayoutParams lp = new TableRow.LayoutParams();
            row.setLayoutParams(lp);
            TextView sessao = new TextView(getContext());
            TextView data = new TextView(getContext());
            TextView hora = new TextView(getContext());
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
