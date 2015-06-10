package itrellis.com.todolist.adapters;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.List;
import itrellis.com.todolist.R;
import itrellis.com.todolist.activities.MainActivity;
import itrellis.com.todolist.databases.ToDoListDatabase;
import itrellis.com.todolist.models.ToDo;
import itrellis.com.todolist.util.Utils;

/**
 * Created by JoshuaWilliams on 6/9/15.
 *
 * RecyclerView Adapter for the List of Todos
 */
public class ToDoListAdapter extends RecyclerView.Adapter<ToDoListAdapter.ToDoCard>
        implements View.OnLongClickListener,
        View.OnClickListener{

    private static final String LOG_TAG = "ToDoListAdapter";
    private static final long ONE_DAY = 86400000;

    private List<ToDo> toDoList;
    private AlertDialog alert;
    private ToDoListDatabase db;
    private Activity context;

    //Constructor
    public ToDoListAdapter(Activity context, List<ToDo> toDoList) {
        this.toDoList = toDoList;
        this.context = context;
        db = ToDoListDatabase.getInstance(context);
    }


    @Override
    public ToDoListAdapter.ToDoCard onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.todo_card, parent, false);

        return new ToDoCard((RelativeLayout)v);
    }

    @Override
    public void onBindViewHolder(ToDoListAdapter.ToDoCard toDoCard, int position) {
        //Sets up the view
        ToDo toDo = getItem(position);
        toDoCard.titleTV.setText(toDo.getTitle());

        long deadline = Long.parseLong(toDo.getDeadline());
        toDoCard.deadlineTV.setText(Utils.getReadableTime(deadline));

        if(toDo.getDecsription().isEmpty()){
            toDoCard.descriptionTV.setVisibility(View.GONE);
        }
        else{
            toDoCard.descriptionTV.setVisibility(View.VISIBLE);
            toDoCard.descriptionTV.setText(toDo.getDecsription());
        }


        //If it is past the deadline then highlight the card.
        if(System.currentTimeMillis() > deadline){
            toDoCard.cardView.setCardBackgroundColor(Utils.getColor(R.color.yellow));
        }
        else{
            toDoCard.cardView.setCardBackgroundColor(Utils.getColor(R.color.white));
        }

        //If the note is complete then set the view appropriately
        if(toDo.isCompleted()){
            toDoCard.checkbox.setChecked(true);
        }
        else{
            toDoCard.checkbox.setChecked(false);
        }

        toDoCard.checkbox.setOnClickListener(this);
        toDoCard.checkbox.setOnLongClickListener(this);

        toDoCard.cardView.setOnClickListener(this);
        toDoCard.cardView.setOnLongClickListener(this);

        toDoCard.checkbox.setTag(toDo);
        toDoCard.cardView.setTag(toDo);
    }

    @Override
    public int getItemCount() {
        return toDoList.size();
    }


    public ToDo getItem(int i){
        return toDoList.get(i);
    }


    @Override
    public void onClick(View view) {
        final ToDo toDo = (ToDo) view.getTag();

        switch (view.getId()){
            case R.id.checkbox:
                //Set the ToDoObject to whatever it is not when the checkbox is clicked.
                toDo.setCompleted(!toDo.isCompleted());
                db.updateToDo(toDo); //Update the database.
                notifyDataSetChanged(); //notifying the adapter will set the view appropriately since we changed the dataset.
                break;

            default:
                //Shows an alert dialog with the details of the
                String completed;
                
                if(toDo.isCompleted()){
                    completed = "Unfinished";
                }
                else{
                    completed = "Completed";
                }
                
                alert = new AlertDialog.Builder(context)
                        .setTitle(toDo.getTitle())
                        .setMessage("Created: " + Utils.getReadableTime(Long.parseLong(toDo.getDateCreated())) +"\n\n" + toDo.getDecsription())
                        .setPositiveButton(completed, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                toDo.setCompleted(!toDo.isCompleted());
                                db.updateToDo(toDo);
                                notifyDataSetChanged();
                                alert.dismiss();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                                alert.dismiss();
                            }
                        })
                        .create();

                alert.show();

                break;
        }

    }

    @Override
    public boolean onLongClick(View view) {
        Log.i(LOG_TAG, " --------- On Long Click ------ ");
        final ToDo toDo = (ToDo) view.getTag();

        //Shows an alert dialog that asks the user if they want to delete the entry.
        alert = new AlertDialog.Builder(context)
                .setTitle("Delete entry")
                .setMessage(context.getString(R.string.delete_entry_text))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        db.deleteToDo(toDo.getId());
                        toDoList.remove(toDo);
                        notifyDataSetChanged();

                        //If there are currenly no entries then notify the main activity by setting the
                        //NoToDos Textview to visible.
                        if (toDoList.size() == 0) {
                            MainActivity.noToDosTV.setVisibility(View.VISIBLE);
                        }
                        alert.dismiss();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        alert.dismiss();
                    }
                })
                .create();

        alert.show();
        return true;
    }

    /**
     * This class is the viewholder for thie RecylcerView. It holds each view so that when scrolling, if already created,
     * the system will not create the view again from scratch.
     */
    static class ToDoCard extends RecyclerView.ViewHolder {
        TextView titleTV, descriptionTV, deadlineTV;
        CheckBox checkbox;
        CardView cardView;

        public ToDoCard(RelativeLayout v) {
            super(v);
            checkbox = (CheckBox) v.findViewById(R.id.checkbox);
            cardView = (CardView) v.findViewById(R.id.card_view);
            titleTV = (TextView) v.findViewById(R.id.title_tv);
            descriptionTV = (TextView) v.findViewById(R.id.description_tv);
            deadlineTV = (TextView) v.findViewById(R.id.time_tv);
        }
    }
}
