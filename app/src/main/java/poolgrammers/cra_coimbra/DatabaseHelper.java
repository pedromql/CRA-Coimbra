package poolgrammers.cra_coimbra;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by f_seq on 24/12/2016.
 */


public class DatabaseHelper extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "poolgrammersdoinggp";

    // Table Names
    private static final String TABLE_PROVA = "prova";
    private static final String TABLE_SESSAO = "sessao";

    // Todo table create statement
    private static final String CREATE_TABLE_PROVA = "CREATE TABLE " +
            TABLE_PROVA + "(" + "designacao" + " TEXT PRIMARY KEY," +
            "id_prova" + " INTEGER," +
            "juiz_arbitro" + " TEXT," +
            "responsavel_cra" + " TEXT," +
            "regulamento" + " TEXT," +
            "local" + " TEXT," +
            "modalidade" + " TEXT," +
            "tipo" + " INTEGER" + ");";

    // Todo table create statement
    private static final String CREATE_TABLE_SESSAO = "CREATE TABLE " +
            TABLE_SESSAO + "(" + "id_sessao" + " INTEGER PRIMARY KEY," +
            "ano" + " INTEGER," +
            "mes" + " INTEGER," +
            "dia" + " INTEGER," +
            "hora" + " INTEGER," +
            "minuto" + " INTEGER," +
            "resposta" + " INTEGER," +
            "prova_designacao" + " TEXT," +
            " FOREIGN KEY (prova_designacao) REFERENCES prova(designacao));";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating required tables
        db.execSQL(CREATE_TABLE_PROVA);
        db.execSQL(CREATE_TABLE_SESSAO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROVA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSAO);

        // create new tables
        onCreate(db);
    }
}