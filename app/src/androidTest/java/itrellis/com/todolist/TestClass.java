package itrellis.com.todolist;

import android.app.Activity;
import org.junit.Test;
import java.util.List;
import itrellis.com.todolist.databases.ToDoListDatabase;
import itrellis.com.todolist.models.ToDo;
import itrellis.com.todolist.util.Utils;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Joshua Williams on 6/10/15.
 *
 * This class tests some of the Utilities class methods.
 *
 * @version 1.0
 */
public class TestClass {
    private Activity activity;

    public TestClass(Activity activity){
        this.activity = activity;
    }

    /**
     * Some unit tests testing some of the Utilities class methods
     * along with some the database methods.
     */
    @Test
    public void TestMethods(){
        //Ensure that retreiving from the shared prefs is good to go
        String result = Utils.getString(R.string.package_name);
        assertEquals("com.itrellis.todolist_", result);

        //Make sure the activity is not null.
        assertNotNull(activity);

        //Try to create 2 instances of the database.
        ToDoListDatabase toDoListDatabase =  ToDoListDatabase.getInstance(activity);
        ToDoListDatabase secondDatabase = ToDoListDatabase.getInstance(activity);

        //Ensure that these point to the same instance since we are using the Singleton Design Pattern
        assertEquals(toDoListDatabase, secondDatabase);

        List<ToDo> toDoList = toDoListDatabase.getAllToDos();

        //Make sure the toDolist is populated.
        assertNotEquals(0, toDoList.size());

        //Ensures the ToDoDatabase methods are returning correctly.
        ToDo toDo = toDoListDatabase.getToDo(toDoList.get(0).getId());
        assertEquals(toDo, toDoList.get(0));

        int lastIndex = Utils.getIntFromSharedPrefs(Utils.getString(R.string.id_index));

        //Since in the database I keep up with the index and the index is equal to the
        //id of the last entry, then they should always be equal.
        assertEquals(lastIndex, toDoList.get(toDoList.size() - 1).getId());

        int databaseVersion = Utils.getIntFromSharedPrefs(Utils.getString(R.string.database_version));

        //Ensures that we are correctly keeping up with the database version.
        assertEquals(databaseVersion, toDoListDatabase.getDatabaseVersion());

        //Delete the entry
        toDoListDatabase.deleteToDo(toDo.getId());

        //Ensure that the ToDom entry is not currently in the database.
        assertNull(toDoListDatabase.getToDo(toDo.getId()));

        toDoList.remove(toDo);

        //Create a new toDoObject
        toDo = new ToDo("Test", "Test Title", "100000", "200000", false);

        //Save it to the database
        toDoListDatabase.saveToDo(toDo);

        //Retrieve all toDosObjects
        toDoList = toDoListDatabase.getAllToDos();

        //Make sure the new entry was saved and is currently in the list
        assertTrue(toDoList.contains(toDo));

        //Update the Entry
        toDo.setCompleted(true);

        //Update the database.
        toDoListDatabase.updateToDo(toDo);

        //Retrieve all of the entries from the database.
        toDoList = toDoListDatabase.getAllToDos();

        //Perform a linear search for the entry
        for(ToDo dbToDo : toDoList){
            //Once the entry is test to see if its update stuck.
            if(dbToDo.getId() == toDo.getId()){
                assertTrue(toDo.isCompleted());
            }
        }
    }
}
