package poolgrammers.cra_coimbra;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

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

public class LoginActivity extends AppCompatActivity {

    private Button loginButton;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;

    private String uri = "http://testes-poolgrammers.dei.uc.pt/api/login";
    public static String USER_TOKEN = "poolgrammers.cra_coimbra.MainActivity.USER_TOKEN";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

    }




    public void login(View view){

//        RequestParams params = new RequestParams();
//        params.put("user_email", inputEmail.getText().toString().trim());
//        params.put("email", "admin@admin.com");
//        params.put("user_password", md5(inputPassword.getText().toString()));
//        params.put("password", md5("rootadmin"));


//        httppost.setEntity(new UrlEncodedFormEntity(builder
//                .getNameValuePairs(), "UTF-8"));
//
//        // Execute HTTP Post Request
//        HttpResponse response = httpclient.execute(httppost);

//        invokeWS(params);

//        new Teste().execute();

        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        // Check for empty data in the form
        if (!email.isEmpty() && !password.isEmpty()) {
            if(Utility.validate(email)) {
                /**
                 * create RequestParams object, populate it with "user_email" and "user_password"
                 * call invokeWS() with the requestParams Object
                 */

                RequestParams params = new RequestParams();
                params.put("email", email);
//                params.put("email", email);
                params.put("password", md5(password));
//                params.put("password", password);

                invokeWS(params);
            }
            // When Email is invalid

            else{
                Toast.makeText(getApplicationContext(), "Email Inválido", Toast.LENGTH_LONG).show();
            }
        }
        else {
            // Prompt user to enter credentials
            Toast.makeText(getApplicationContext(),"Insira as credenciais!", Toast.LENGTH_LONG).show();
        }

    }

    public void invokeWS(RequestParams params){
        // Show Progress Dialog
        pDialog.show();
        // Make RESTful webservice call using AsyncHttpClient object
        /**
         * Create the AsyncHttpClient object;
         * call the get() method with the uri, params and the AsynchResponseHandler interface as parameters
         */
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(uri, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                success(bytes);
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                failure(i);
            }
        });
    }

    /**
     * Method which navigates from Login Activity to Home Activity
     */
    public void navigatetoHomeActivity(JSONObject responseObject){
        Intent intent = new Intent();
        try {
            intent.putExtra("token",responseObject.getString("token"));
            setResult(RESULT_OK, intent);
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void success(byte[] bytes){
        String response = new String(bytes);
        // Hide Progress Dialog
        pDialog.hide();

        System.out.println (response);
        try {
            // JSON Object
            JSONObject jsonResponse = new JSONObject(response);
            if (jsonResponse.has("token")) {
                Toast.makeText(getApplicationContext(), "You are successfully logged in!", Toast.LENGTH_LONG).show();
                navigatetoHomeActivity(jsonResponse);
            }
            else {
                Toast.makeText(getApplicationContext(), jsonResponse.getString("result"), Toast.LENGTH_LONG).show();
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

    public void failure(int statusCode){
        // Hide Progress Dialog
        pDialog.hide();

        Toast.makeText(getApplicationContext(), "Problemas na ligação ao servidor!", Toast.LENGTH_LONG).show();

//        // When Http response code is '404'
//        if(statusCode == 404){
//            Toast.makeText(getApplicationContext(), "Wrong email or password", Toast.LENGTH_LONG).show();
//        }
//        // When Http response code is '500'
//        else if(statusCode == 500){
//            Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
//        }
//        // When Http response code other than 404, 500
//        else{
//            Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
//        }
    }

//    class Teste extends AsyncTask {
//
//        @Override
//        protected Object doInBackground(Object[] params) {
//            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//            nameValuePairs.add(new BasicNameValuePair("email", "cra@cra.com"));
//            nameValuePairs.add(new BasicNameValuePair("password", "cracracra"));
//
//            HttpClient httpclient=new DefaultHttpClient();
//
//            HttpPost httppost = new HttpPost(uri);
//            httppost.setHeader(HTTP.CONTENT_TYPE,
//                    "application/x-www-form-urlencoded");
//            // Add your data
//            try {
//                httppost.setEntity(new StringEntity("email=admin@admin.com&password=rootadmin"));
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//
//            try {
//                HttpResponse response = httpclient.execute(httppost);
//
//                StringBuilder sb = new StringBuilder();
//                try {
//                    BufferedReader reader =
//                            new BufferedReader(new InputStreamReader(response.getEntity().getContent()), 65728);
//                    String line = null;
//
//                    while ((line = reader.readLine()) != null) {
//                        sb.append(line);
//                    }
//                }
//                catch (IOException e) { e.printStackTrace(); }
//                catch (Exception e) { e.printStackTrace(); }
//
//
//                System.out.println("finalResult " + sb.toString());
//
//
//                Log.d("Response:" , response.toString());
//                return response;
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//
//    }

    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

}
