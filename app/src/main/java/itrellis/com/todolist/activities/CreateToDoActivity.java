package itrellis.com.todolist.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import java.util.Date;
import itrellis.com.todolist.R;
import itrellis.com.todolist.databases.ToDoListDatabase;
import itrellis.com.todolist.models.ToDo;
import itrellis.com.todolist.util.Utils;

/**
 * Created by Joshua Williams 6/9/15
 *
 * Activity that allows a user to create an entry.
 *
 * @version 1.0
 */
public class CreateToDoActivity extends AppCompatActivity{
    private static final String LOG_TAG = "MainActivity";

    private ToDoListDatabase db;
    private Toolbar toolbar;
    private ImageView statusBarBackground;
    private long deadline;
    private Button deadlineButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_to_do);
        db = ToDoListDatabase.getInstance(this);
        setUpLayout();
    }



    /**
     * This method finds and sets up views, sets up toolbar and colors status bar.
     */
    private void setUpLayout(){
        setUpToolBar();

        long timeInMillis = System.currentTimeMillis();
        deadline = timeInMillis  + (1000 * 60 * 60 * 24);
        String time = Utils.getReadableTime(deadline);

        deadlineButton = (Button) findViewById(R.id.time_button);
        deadlineButton.setText(time);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Utils.setUpStatusBar(getWindow(), R.color.darker_gray);
        }
        else{
            Log.i(LOG_TAG, "Setting up layout for non 5.0 user.");

            RelativeLayout toolBarLayout = (RelativeLayout)findViewById(R.id.include);
            statusBarBackground = (ImageView)toolBarLayout.findViewById(R.id.statusbar);
            addListenerForStatusBar();
            setUpStatusBar();
        }
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Create Note!");
    }

    /**
     * Listener method for the deadline Button
     *
     * @param v - the view that will be operated on.
     */
    public void deadlineButton(View v){
        Date today = new Date();
        new SlideDateTimePicker.Builder(getSupportFragmentManager())
                .setListener(listener)
                .setInitialDate(new Date(today.getTime() + (1000 * 60 * 60 * 24))) //Tomorrow's date.
                .build()
                .show();
    }

    /**
     * Listener method for the createToDoButton
     *
     * @param v - the view that will be operated on.
     */
    public void createToDoButton(View v){
        EditText titleTV = (EditText) findViewById(R.id.title_tv);

        if(titleTV.getText().toString().isEmpty()){
            Utils.showToast("Must have a title.");
            return;
        }

        EditText descriptionTV = (EditText) findViewById(R.id.description_tv);
        ToDo toDo = new ToDo(titleTV.getText().toString(), descriptionTV.getText().toString(),
                Long.toString(System.currentTimeMillis()), Long.toString(deadline));
        db.saveToDo(toDo);

        Intent intent = new Intent();
        intent.putExtra("todo", toDo);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * The listener for the Slide Date Time Listener.
     *
     * Credits to Arman Pagilagan
     *
     * Copyright [yyyy] [name of copyright owner]
     *
     * Licensed under the Apache License, Version 2.0 (the "License");
     * you may not use this file except in compliance with the License.
     * You may obtain a copy of the License at
     *
     * http://www.apache.org/licenses/LICENSE-2.0
     *
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS,
     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     * See the License for the specific language governing permissions and
     * limitations under the License.
     */
    final SlideDateTimeListener listener = new SlideDateTimeListener() {
        @Override
        public void onDateTimeSet(Date date) {
            deadline = date.getTime();
            deadlineButton.setText(Utils.getReadableTime(deadline));
        }
    };
}
