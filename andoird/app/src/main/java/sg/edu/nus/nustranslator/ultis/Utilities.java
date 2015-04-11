package sg.edu.nus.nustranslator.ultis;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Storm on 4/10/2015.
 */
public class Utilities {
    public static String getTimeStamp() {
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        return timeStamp;
    }
}
