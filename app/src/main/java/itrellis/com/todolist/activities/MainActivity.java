package itrellis.com.todolist.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.List;
import itrellis.com.todolist.R;
import itrellis.com.todolist.adapters.ToDoListAdapter;
import itrellis.com.todolist.databases.ToDoListDatabase;
import itrellis.com.todolist.models.ToDo;
import itrellis.com.todolist.util.Utils;

/**
 *
 * Created by Joshua Williams 6/9/15
 *
 * Activity that shows the user's entries in a list.
 *
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity {

    private static final long ANIM_DURATION = 300; //how long the animation is displayed. 3/10 of a second.
    private static final String LOG_TAG = "MainActivity";

    private ToDoListDatabase db;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ImageView statusBarBackground;
    private int NEW_TO_DO = 50;
    private ToDoListAdapter toDoListAdapter;
    public static TextView noToDosTV;
    public static List<ToDo> toDoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Utils.setContext(getApplicationContext());
        setUpLayout();
    }

    /**
     * This method finds and sets up views, sets up toolbar and colors status bar.
     */
    private void setUpLayout(){
        setUpToolBar();

        noToDosTV = (TextView) findViewById(R.id.no_todos);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Utils.setUpStatusBar(getWindow(), R.color.darker_gray);
        }
        else{
            Log.i(LOG_TAG, "Setting up layout for non 5.0 user.");

            recyclerView.setVisibility(View.VISIBLE);
            RelativeLayout toolBarLayout = (RelativeLayout)findViewById(R.id.include);
            statusBarBackground = (ImageView)toolBarLayout.findViewById(R.id.statusbar);
            addListenerForStatusBar();
            setUpStatusBar();
        }
        loadData();

    }

    /**
     * Loads the user's data from the sqlite database
     */
    private void loadData() {
        db = ToDoListDatabase.getInstance(this);
        progressBar.setVisibility(View.INVISIBLE);

        toDoList = db.getAllToDos();
        toDoListAdapter = new ToDoListAdapter(this, toDoList);
        if(toDoList.size() == 0){
            noToDosTV.setVisibility(View.VISIBLE);
            return;
        }
        recyclerView.setAdapter(toDoListAdapter);
    }

    /**
     *
     * Android L guidleines allows the option for the status bar to now be colored. For pre 5.0 devices,
     * A simulated status bar color is provided this way. Since in the onCreate a view is not technically drawn yet,
     * it is rather cumbersome to find a way to set the color in the method. A work around is given here, first
     * in the styles xml the status bar is set to translucent which also makes it overlay the window.
     * The status bar height for the device is calculated then the image view is then set to those dimensions
     * and colored thus giving the status bar color. All of this is done through this listener which listens for when
     * the view is drawn on the screen.
     *
     */
    private void addListenerForStatusBar() {
        Log.i(LOG_TAG, "Listener added.");
        final ViewTreeObserver observer= statusBarBackground.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                setUpStatusBar();
                statusBarBackground.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    /**
     * Sets up the status bar by getting the status bar height, then setting the imageview that acts as a
     * status bar to the same height to give pre 5.0 devices a colored status bar. This works because the style of the
     * application has a translucent status bar.
     */
    private void setUpStatusBar() {
        int statusBarHeight = Utils.getStatusBarHeight();
        android.view.ViewGroup.LayoutParams layoutParams = statusBarBackground.getLayoutParams();
        Log.i(LOG_TAG, "Status bar height - Original - " + statusBarBackground.getHeight());
        layoutParams.height = statusBarHeight;
        Log.i(LOG_TAG, "Status bar height - After - " + statusBarBackground.getHeight());
        statusBarBackground.setLayoutParams(layoutParams);
        statusBarBackground.setBackgroundColor(getResources().getColor(R.color.darker_gray));
    }

    /**
     * Sets up the toolbar.
     */
    private void setUpToolBar() {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("ToDoList");
    }

    public void addToDo(View v){
        Intent i = new Intent(MainActivity.this, CreateToDoActivity.class);
        startActivityForResult(i, NEW_TO_DO);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Checks the user's result to see if the user successfully added an entry.
        //If so then it updates the list by setting a new instance of the adapter to the
        //recycler view. Notifying the adapter was not working on some versions of Android
        if (requestCode == NEW_TO_DO) {
            if(resultCode == RESULT_OK){
                ToDo toDo = data.getParcelableExtra("todo");
                toDoList.add(toDo);
                noToDosTV.setVisibility(View.INVISIBLE);
                toDoListAdapter = new ToDoListAdapter(MainActivity.this, toDoList);
                recyclerView.setAdapter(toDoListAdapter);
            }
        }
    }

}
