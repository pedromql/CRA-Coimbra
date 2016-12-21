package poolgrammers.cra_coimbra;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static poolgrammers.cra_coimbra.Utility.getServerUrl;


public class NotificationsBroadcastReceiver extends BroadcastReceiver {

    private String uri = getServerUrl() + "get_notifications";


    @Override
    public void onReceive(Context context, Intent intent) {
        getNotifications(context);
    }

    private void getNotifications(final Context context) {
        // Make RESTful webservice call using AsyncHttpClient object
        /**
         * Create the AsyncHttpClient object;
         * call the get() method with the uri, params and the AsynchResponseHandler interface as parameters
         */
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("token", MainActivity.readTokenFromFile(context, "token"));
        client.get(uri, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    onGetNotificationsSuccess(context, bytes);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Log.e("Get notifications", "Connection problem - " + throwable);
            }
        });
    }

    public void onGetNotificationsSuccess(Context context, byte[] bytes) throws JSONException {
        String response = new String(bytes);


        try {
            // JSON Object
            JSONObject jsonResponse = new JSONObject(response);


            if (jsonResponse.getString("success").compareTo("true") == 0) {
                if (jsonResponse.has("notifications")) {
                    // Vibrate the mobile phone
                    Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(1000);
                    JSONArray result = new JSONArray(jsonResponse.getString("notifications"));

                    for (int i = 0; i < result.length(); i++) {
                        JSONObject prova_auxiliar = result.getJSONObject(i);
                        String prova = prova_auxiliar.getString("designacao");
                        String tipo = prova_auxiliar.getString("tipo_prova");
                        pushNotification(context, prova, tipo, i);
                    }

                }

            } else {
                //Descomentar para testar que funca
                //String output = jsonResponse.getString("result");
                // pushNotification(context, output, "");
            }

        } catch (JSONException e) {
            //NO TOASTS FOR YOU.
            //Toast.makeText(context, "Problemas na ligação ao servidor!", Toast.LENGTH_LONG).show();
            e.printStackTrace();

        }
    }

    private void pushNotification(Context context, String prova, String tipo, int i) {
        String title = "CRA - Coimbra - ";

        switch (tipo) {
            case "-1":
                title += "Prova criada";
                break;
            case "0":
                title += "Pré-Convocatória";
                break;
            case "1":
                title += "Convocatória Final";
                break;
            case "2":
                title += "Renovar Filiação";
                prova = "Verifique o email enviado";
                break;
            case "3":
                title += "Exame Médico";
                prova = "Validade do exame por expirar";
                break;
            default:
                title = "CRA - Coimbra";
                break;
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_swim)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_swim))
                .setContentTitle(title)
                .setContentText(prova);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(i, mBuilder.build());
    }

}
