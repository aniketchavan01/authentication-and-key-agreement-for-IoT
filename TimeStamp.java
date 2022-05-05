import java.sql.Timestamp;

public class TimeStamp {
    static long getTimeStampStatus()
    {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return timestamp.getTime();

    }


    static Boolean isValidTimestamp(Long sentAtSeconds, Long receivedAtSeconds) {
        return Math.abs(sentAtSeconds - receivedAtSeconds) < 400;
    }
}
