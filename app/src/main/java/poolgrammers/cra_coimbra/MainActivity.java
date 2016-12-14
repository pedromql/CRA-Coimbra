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

        token = readTokenFromFile(this);
        if (token == null) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivityForResult(loginIntent, REQ_LOGIN_CODE);
        }
        else {
            Intent intent = new Intent(this, NavDrawer.class);
            startActivity(intent);
            finish();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_LOGIN_CODE) {
                if (intent.hasExtra("token")) {
                    saveTokenOnFile(this, intent.getStringExtra("token"));
                    System.out.println(readTokenFromFile(this));
                    Intent new_intent = new Intent(this, NavDrawer.class);
                    startActivity(new_intent);
                    finish();
                }
            }
        }
    }

    public static String readTokenFromFile(Context context) {
        try {
            Scanner scanner = new Scanner(context.openFileInput("token.txt"));
            return scanner.nextLine();
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public static void saveTokenOnFile(Context context, String token) {
        try {
            PrintStream printStream = new PrintStream(context.openFileOutput("token.txt",Context.MODE_PRIVATE));
            printStream.println(token);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteTokenFile(Context context) {
        return context.deleteFile("token.txt");
    }

}
