package itrellis.com.todolist.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by JoshuaWilliams on 6/9/15.
 *
 * Data model for the ToDoObject Entry.
 *
 * @version 1.0
 */
public class ToDo implements Parcelable {
    private static final String LOG_TAG = "ToDo (Object)";


    private int id;
    private String title = "";
    private String decsription = "";
    private String dateCreated = "";


    private String deadline = "";
    private boolean completed = false;


    /************************************* Constructors *********************************/

    public ToDo(){}

    public ToDo(String title, String description, String dateCreated, String deadline,
                boolean completed){
        setTitle(title);
        setDescription(description);
        setDateCreated(dateCreated);
        setDeadline(deadline);
        setCompleted(completed);
    }

    public ToDo(String title, String description, String dateCreated, String deadline){
        setTitle(title);
        setDateCreated(dateCreated);
        setDeadline(deadline);

        if(Long.parseLong(deadline) < Calendar.getInstance().getTimeInMillis()){
            setCompleted(true);
        }

        if(description != null){
            setDescription(description);
        }

        Log.i(LOG_TAG, "ToDo Object Creation ---- " + toString());
    }

    private ToDo(Parcel in){
        String[] data = new String[6];
        in.readStringArray(data);
        setId(Integer.parseInt(data[0]));
        setTitle(data[1]);
        setDescription(data[2]);
        setDateCreated(data[3]);
        setDeadline(data[4]);
        setCompleted(Boolean.parseBoolean(data[5]));
    }

    /************************************* END ********************************************/


    /************************************* GETTERS AND SETTERS *********************************/

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDecsription() {
        return decsription;
    }

    public void setDescription(String decsription) {
        this.decsription = decsription;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        Log.i(LOG_TAG, "Setting id ---- " + id);
        this.id = id;
    }
    /************************************* END ********************************************/


    @Override
    public String toString() {
        return "ToDo Object - {" +
                "id = " + id +
                ", title = '" + title + '\'' +
                ", decsription = '" + decsription + '\'' +
                ", dateCreated = '" + dateCreated + '\'' +
                ", deadline = '" + deadline + '\'' +
                ", completed = " + completed +
                "";
    }



    /************************************* Parcelable Interface *********************************/
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        String[] data = new String[]{
                Integer.toString(getId()),
                getTitle(),
                getDecsription(),
                getDateCreated(),
                getDeadline(),
                Boolean.toString(isCompleted())
        };
        parcel.writeStringArray(data);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public ToDo createFromParcel(Parcel in) {
            return new ToDo(in);
        }

        public ToDo[] newArray(int size) {
            return new ToDo[size];
        }
    };

    /************************************* Parcelable Interface *********************************/

}
