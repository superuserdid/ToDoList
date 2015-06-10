package itrellis.com.todolist.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import itrellis.com.todolist.R;
import itrellis.com.todolist.models.ToDo;
import itrellis.com.todolist.util.Utils;

/**
 * Created by JoshuaWilliams on 6/9/15.
 *
 * SQLlite Database to manage the creation, update, and delete of to do list items.
 *
 * Uses Singleton Design pattern.
 */
public class ToDoListDatabase extends SQLiteOpenHelper {
    private static final String LOG_TAG = "ToDoListDatabase";
    private static ToDoListDatabase mInstance;

    private static final String DATABASE_NAME = "ToDoListDatabase";
    private static final String TABLE_TODO_LIST = "ToDoLists";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_COMPLETED= "completed";
    private static final String COLUMN_DEADLINE= "deadline";
    private static final String COLUMN_DATE_CREATED = "date";

    private static int DATABASE_VERSION = 1;
    private static int lastId;

    /**
     * Retrieves and instance of the database using the Singleton Design Pattern.
     *
     * @param ctx - the context for the database.
     *
     * @return - an instance of the database.
     */
    public static ToDoListDatabase getInstance(Context ctx) {

        int dbVersion = Utils.getIntFromSharedPrefs(Utils.getString(R.string.database_version));
        DATABASE_VERSION = (dbVersion == 0) ? 1 : dbVersion;
        lastId = Utils.getIntFromSharedPrefs(Utils.getString(R.string.id_index));

        if (mInstance == null) {
            mInstance = new ToDoListDatabase(ctx.getApplicationContext());
        }

        return mInstance;
    }

    /**
     * Private constructor so the user can only access one instance.
     *
     * @param context - the context for the database.
     */
    private ToDoListDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        //SQL statement to create the needed tables.
        String CREATE_USER_INFO_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_TODO_LIST + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + COLUMN_TITLE + " TEXT, "
                + COLUMN_DESCRIPTION + " TEXT, " + COLUMN_DATE_CREATED + " TEXT, " + COLUMN_DEADLINE
                + " TEXT, " + COLUMN_COMPLETED + " TEXT)";

        db.execSQL(CREATE_USER_INFO_TABLE);
        Log.i(LOG_TAG, "DATABASE CREATED --- " + CREATE_USER_INFO_TABLE);

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO_LIST);
        Log.i(LOG_TAG, "Upgrading");
        
        //Database Version keeps track of itself to avoid errors
        DATABASE_VERSION++;
        Utils.saveToSharedPrefsInt(Utils.getString(R.string.database_version), DATABASE_VERSION);
        // Create tables again
        onCreate(db);
    }


    /**
     * Saves the user's todolist  object as a record in the database.
     * @param toDo
     */
    public void saveToDo(ToDo toDo) {
        Log.i(LOG_TAG, "Saving ToDo --- " + toDo.toString());

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, toDo.getTitle());
        values.put(COLUMN_DESCRIPTION, toDo.getDecsription());
        values.put(COLUMN_DATE_CREATED, toDo.getDateCreated());
        values.put(COLUMN_DEADLINE, toDo.getDeadline());
        values.put(COLUMN_COMPLETED, Boolean.toString(toDo.isCompleted()));

        // Inserting Row
        db.insert(TABLE_TODO_LIST, null, values);
        db.close(); // Closing database connection

        toDo.setId(++lastId);
        Utils.saveToSharedPrefsInt(Utils.getString(R.string.id_index), lastId);
    }

    /**
     * Retrieves a todolist object from the database.
     *
     * @param id - the id of the object
     * @return - the appropriate ToDoList Object
     */
    public ToDo getToDo(int id){
        Log.i(LOG_TAG, "Retreiving ToDo --- id = " + id);

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TODO_LIST, new String[] { COLUMN_ID,
                        COLUMN_TITLE, COLUMN_DESCRIPTION, COLUMN_DATE_CREATED,
                        COLUMN_DEADLINE, COLUMN_COMPLETED}, COLUMN_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);

        ToDo toDo = null;
        if (cursor != null){
            cursor.moveToFirst();
            toDo = parseToDo(cursor);
        }
        Log.i(LOG_TAG, "Retreiving ToDo --- " + toDo.toString());
        // return info
        return toDo;
    }

    /**
     * Retreives all records from the database.
     *
     * @return - the list with all of the user's toDos
     */
    public List<ToDo> getAllToDos(){
        Log.i(LOG_TAG, "Retreiving all ToDos");
        List<ToDo> toDoList = new ArrayList<ToDo>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_TODO_LIST;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                //adding ToDoList object to list.
                toDoList.add(parseToDo(cursor));
            } while (cursor.moveToNext());
        }
        // return todolist list
        return toDoList;
    }


    /**
     * Updates a single record in the database.
     *
     * @param toDo - the todo object to be updated.
     */
    public void updateToDo(ToDo toDo) {
        Log.i(LOG_TAG, "Updating To Do ---- " + toDo.toString());

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, toDo.getTitle());
        values.put(COLUMN_DESCRIPTION, toDo.getDecsription());
        values.put(COLUMN_DATE_CREATED, toDo.getDateCreated());
        values.put(COLUMN_DEADLINE, toDo.getDeadline());
        values.put(COLUMN_COMPLETED, Boolean.toString(toDo.isCompleted()));

        db.update(TABLE_TODO_LIST, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(toDo.getId())});

       getAllToDos();
    }

    /**
     * Deletes a single record in the database.
     *
     * @param id - the id of the todo object to be deleted.
     */
    public void deleteToDo(int id) {
        Log.i(LOG_TAG, "Deleting To Do ---- " + id);
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TODO_LIST, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
        Utils.saveToSharedPrefsInt(Utils.getString(R.string.id_index), getAllToDos().size());
    }


    /**
     * Creates a ToDoList Object from the cursor.
     * Only works with this databse.
     *
     * @param cursor - the cursor for the database pointing the record.
     * @return - the ToDoList Object.
     */
    private ToDo parseToDo(Cursor cursor){
        ToDo toDo = new ToDo(cursor.getString(1), cursor.getString(2),
                cursor.getString(3), cursor.getString(4), Boolean.parseBoolean(cursor.getString(5)));
        toDo.setId(cursor.getInt(0));
        Log.i(LOG_TAG, "Parsing --- " + toDo.toString());
        return toDo;
    }

}
