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
    private static String token;
    private static String nome;
    private static String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        token = readTokenFromFile(this, "token");
        email = readTokenFromFile(this, "email");
        nome = readTokenFromFile(this, "nome");
        System.out.println(token + "\n" + email + "\n" + nome);
        if (token == null) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivityForResult(loginIntent, REQ_LOGIN_CODE);
        }
        else {
            Intent intent = new Intent(this, NavDrawer.class);
            startActivity(intent);
//            finish();
        }
        super.onResume();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_LOGIN_CODE) {
                if (intent.hasExtra("token")) {
                    token = intent.getStringExtra("token");
                    email = intent.getStringExtra("email");
                    nome = intent.getStringExtra("nome");
                    saveTokenOnFile(this, intent.getStringExtra("token"), intent.getStringExtra("email"), intent.getStringExtra("nome"));
                    System.out.println(token + "\n" + email + "\n" + nome);
                    Intent new_intent = new Intent(this, NavDrawer.class);
                    startActivity(new_intent);
//                    finish();
                }
            }
        }
    }

    public static String readTokenFromFile(Context context, String key) {
        int lineNumber = 0;
        if (key.compareTo("token") == 0) lineNumber = 1;
        else if (key.compareTo("email") == 0) lineNumber = 2;
        else if (key.compareTo("nome") == 0) lineNumber = 3;
        try {
            Scanner scanner = new Scanner(context.openFileInput("token.txt"));
            for (int i = 1; i < lineNumber; i++) {
                scanner.nextLine();
            }
            return scanner.nextLine();
        } catch (Exception e) {
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
