package poolgrammers.cra_coimbra;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class MainActivity extends Activity {
    private static final int REQ_LOGIN_CODE = 69;
    private String token;
    private String nome;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        token = readTokenFromFile(this, "token");
        if (token == null) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivityForResult(loginIntent, REQ_LOGIN_CODE);
        }
        else {
            //TODO Normal behavior
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_LOGIN_CODE) {
                if (intent.hasExtra("token")) {
                    saveTokenOnFile(this, intent.getStringExtra("token"), intent.getStringExtra("email"), intent.getStringExtra("nome"));
                    System.out.println(readTokenFromFile(this, "token"));
                    //TODO Normal behavior
                }
            }
        }
    }

    public static String readTokenFromFile(Context context, String key) {
        int lineNumber = 0;
        if (key.compareTo("token") == 1) lineNumber = 1;
        else if (key.compareTo("email") == 1) lineNumber = 2;
        else if (key.compareTo("nome") == 1) lineNumber = 3;
        try {
            Scanner scanner = new Scanner(context.openFileInput("token.txt"));
            for (int i = 1; i < lineNumber; i++) {
                scanner.nextLine();
            }
            return scanner.nextLine();
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public static void saveTokenOnFile(Context context, String token, String email, String nome) {
        try {
            PrintStream printStream = new PrintStream(context.openFileOutput("token.txt",Context.MODE_PRIVATE));
            printStream.println(token);
            printStream.println(email);
            printStream.println(nome);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteTokenFile(Context context) {
        return context.deleteFile("token.txt");
    }

}
