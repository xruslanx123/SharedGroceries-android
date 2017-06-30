package sharedgroceries.ruslan.sharedgroceries;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Ruslan on 29/06/2017.
 */

public class Item {

    int count;
    String timeStamp;
    String title;

    public Item(int count, String timeStamp, String title) {
        this.count = count;
        this.timeStamp = timeStamp;
        this.title = title;
    }

    public Item() {}

    public int getCount() {return count;}

    public void setCount(int count) {
        this.count = count;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static String timeStampString(long time){
        Date date = new Date(time);
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
        return formatter.format(date);
    }

    public static long timeStampLong(String time){
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
        try {
            Date date = formatter.parse(time);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
