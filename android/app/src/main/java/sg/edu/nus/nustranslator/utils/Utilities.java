package sg.edu.nus.nustranslator.utils;

import java.text.SimpleDateFormat;
import java.util.Date;


public class Utilities {
    public static String getTimeStamp() {
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        return timeStamp;
    }
}
