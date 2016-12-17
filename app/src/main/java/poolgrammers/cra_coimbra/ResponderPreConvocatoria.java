package poolgrammers.cra_coimbra;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
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
import poolgrammers.cra_coimbra.Util.SessionItem;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ResponderPreConvocatoria.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ResponderPreConvocatoria#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResponderPreConvocatoria extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ProgressDialog pDialog;

    private Spinner provasSpinner;
    private List<String> provas = new ArrayList<>();
    ArrayAdapter<String> provasAdapter;
    private String prova_seleccionada;
    private List<SessionItem> sessoes = new ArrayList<>();
    private ListView listaSessoes;
    SessionAdapter sessionAdapter;

    private OnFragmentInteractionListener mListener;

    public ResponderPreConvocatoria() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ResponderPreConvocatoria.
     */
    // TODO: Rename and change types and number of parameters
    public static ResponderPreConvocatoria newInstance(String param1, String param2) {
        ResponderPreConvocatoria fragment = new ResponderPreConvocatoria();
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

        getProvas();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_responder_pre_convocatoria, container, false);

        provasSpinner = (Spinner) view.findViewById(R.id.responder_pre_convocatoria_spinner);

        provasAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, provas );
        provasSpinner.setAdapter(provasAdapter);

        provasSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sessoes.clear();
                prova_seleccionada = adapterView.getItemAtPosition(i).toString();
                getSessoes(adapterView.getItemAtPosition(i).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                sessoes.clear();
                sessionAdapter.notifyDataSetChanged();
            }
        });

        listaSessoes = (ListView) view.findViewById(R.id.responder_pre_convocatoria_lista_sessoes);
        sessionAdapter = new SessionAdapter(this.getContext(), R.layout.session_item, sessoes);
        listaSessoes.setAdapter(sessionAdapter);

        Button submitButton = (Button) view.findViewById(R.id.responder_pre_convocatoria_submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submeterRespostasPreConvocatoria();
            }
        });

        /*
        List<SessionItem> sessionItemList = new ArrayList<>();
        sessionItemList.add(new SessionItem("cona",true));
        sessionItemList.add(new SessionItem("puta",true));

        ListView listaSessoes = (ListView) view.findViewById(R.id.responder_pre_convocatoria_lista_sessoes);
        SessionAdapter customAdapter = new SessionAdapter(this.getContext(), R.layout.session_item, sessionItemList);

        listaSessoes.setAdapter(customAdapter);
        */

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

    public void getProvas(){
        // Progress dialog
        pDialog = new ProgressDialog(this.getContext());
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        RequestParams params = new RequestParams();
        params.put("token", MainActivity.readTokenFromFile(this.getContext(), "token"));
        params.put("responder", true);

        // Show Progress Dialog
        pDialog.show();
        // Make RESTful webservice call using AsyncHttpClient object
        /**
         * Create the AsyncHttpClient object;
         * call the get() method with the uri, params and the AsynchResponseHandler interface as parameters
         */
        AsyncHttpClient client = new AsyncHttpClient();
        String uri = "http://testes-poolgrammers.dei.uc.pt/api/get_provas";
        client.get(uri, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int code, Header[] headers, byte[] bytes) {
                String response = new String(bytes);

                pDialog.hide();

                System.out.println (response);
                try {
                    // JSON Object
                    JSONObject jsonResponse = new JSONObject(response);
                    if (jsonResponse.getBoolean("success")) {
                        JSONArray provasJSON = new JSONArray(jsonResponse.getString("result"));
                        for (int i = 0; i < provasJSON.length(); i++) {
                            JSONObject prova = provasJSON.getJSONObject(i);
                            if (prova.getInt("tipo") == 0) provas.add(prova.getString("designacao"));
                        }
                        provasAdapter.notifyDataSetChanged();
                    }
                    else {
                        Toast.makeText(getContext(), "Problemas na ligação ao servidor!", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getContext(), "Problemas na ligação ao servidor!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int code, Header[] headers, byte[] bytes, Throwable throwable) {
                pDialog.hide();

                Toast.makeText(getContext(), "Problemas na ligação ao servidor!", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getSessoes(String designacao) {
        // Progress dialog
        pDialog = new ProgressDialog(this.getContext());
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        RequestParams params = new RequestParams();
        params.put("token", MainActivity.readTokenFromFile(this.getContext(), "token"));
        params.put("designacao", designacao);

        // Show Progress Dialog
        pDialog.show();
        // Make RESTful webservice call using AsyncHttpClient object
        /**
         * Create the AsyncHttpClient object;
         * call the get() method with the uri, params and the AsynchResponseHandler interface as parameters
         */
        AsyncHttpClient client = new AsyncHttpClient();
        String uri = "http://testes-poolgrammers.dei.uc.pt/api/get_prova";
        client.get(uri, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int code, Header[] headers, byte[] bytes) {
                String response = new String(bytes);

                pDialog.hide();

                System.out.println (response);
                try {
                    // JSON Object
                    JSONObject jsonResponse = new JSONObject(response);
                    if (jsonResponse.getBoolean("success")) {
                        JSONArray provaJSON = new JSONArray(jsonResponse.getString("result"));
                        JSONArray sessoesJSON = provaJSON.getJSONArray(4);
                        for (int i = 0; i < sessoesJSON.length(); i+=2) {
                            JSONObject sessao = sessoesJSON.getJSONObject(i);
                            String data = String.format(Locale.UK, "%02d", sessao.getInt("dia")) + "/" + String.format(Locale.UK, "%02d", sessao.getInt("mes")) + "/" + sessao.getInt("ano");
                            String tempo = String.format(Locale.UK, "%02d", sessao.getInt("hora")) + ":" + String.format(Locale.UK, "%02d", sessao.getInt("minutos"));
                            int idSessao = sessao.getInt("id_sessao");
                            int idProva = sessao.getInt("id_prova");
                            sessoes.add(new SessionItem(data, tempo, idSessao, idProva, false));
                        }
                        sessionAdapter.notifyDataSetChanged();
                    }
                    else {
                        Toast.makeText(getContext(), "Problemas na ligação ao servidor!", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getContext(), "Problemas na ligação ao servidor!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int code, Header[] headers, byte[] bytes, Throwable throwable) {
                pDialog.hide();

                Toast.makeText(getContext(), "Problemas na ligação ao servidor!", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void submeterRespostasPreConvocatoria() {
        // Progress dialog
        pDialog = new ProgressDialog(this.getContext());
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        String dicionarioSessoes = "";

        for (int i = 0; i < sessoes.size(); i++) {
            if (i > 0) dicionarioSessoes += ",";
            dicionarioSessoes += "" + sessoes.get(i).getIdSessao() + ":";
            if (sessoes.get(i).getChecked()) {
                dicionarioSessoes += "1";
            }
            else {
                dicionarioSessoes += "-1";
            }
        }

        RequestParams params = new RequestParams();
        params.put("token", MainActivity.readTokenFromFile(this.getContext(), "token"));
        params.put("dicionario_sessoes", dicionarioSessoes);

        // Show Progress Dialog
        pDialog.show();
        // Make RESTful webservice call using AsyncHttpClient object
        /**
         * Create the AsyncHttpClient object;
         * call the get() method with the uri, params and the AsynchResponseHandler interface as parameters
         */
        AsyncHttpClient client = new AsyncHttpClient();
        String uri = "http://testes-poolgrammers.dei.uc.pt/api/responder_pre_convocatoria";
        client.post(uri, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int code, Header[] headers, byte[] bytes) {
                String response = new String(bytes);

                pDialog.hide();

                System.out.println (response);
                try {
                    // JSON Object
                    JSONObject jsonResponse = new JSONObject(response);
                    if (jsonResponse.getBoolean("success")) {
                        Toast.makeText(getContext(), "Resposta submetida com sucesso!", Toast.LENGTH_LONG).show();
                        sessoes.clear();
                        sessionAdapter.notifyDataSetChanged();
                        provas.remove(prova_seleccionada);
                        provasAdapter.notifyDataSetChanged();
                        prova_seleccionada = "";
                    }
                    else {
                        Toast.makeText(getContext(), jsonResponse.getString("result"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getContext(), "Problemas na ligação ao servidor!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int code, Header[] headers, byte[] bytes, Throwable throwable) {
                pDialog.hide();

                Toast.makeText(getContext(), "Problemas na ligação ao servidor!", Toast.LENGTH_LONG).show();
            }
        });
    }

}
