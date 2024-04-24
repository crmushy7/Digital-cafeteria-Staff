package Others;

import android.content.Context;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class OurTime {
    private static String timeStatus="";
    private static String orderTime="";
    public static void init(Context context)
    {
        Calendar calendar = Calendar.getInstance();
        String currentdate = DateFormat.getInstance().format(calendar.getTime());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault());
        SimpleDateFormat simpleOrderDateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String formattedTime = simpleDateFormat.format(new Date());
        String formattedOrderTime = simpleOrderDateFormat.format(new Date());


        orderTime=currentdate+"Hrs ";

        int currentHour=calendar.get(Calendar.HOUR_OF_DAY);
        if(currentHour>=0 && currentHour<12)
        {
            timeStatus="BreakFast";
        }else if(currentHour>=12 && currentHour<16)
        {
            timeStatus="Lunch";
        } else if (currentHour>=16 && currentHour<24) {
            timeStatus="Dinner";
        }else{
            }


    }
    public static String getTimeStatus(){
        return timeStatus;
    }
    public static String getOrderTime(){
        return orderTime;
    }
}
