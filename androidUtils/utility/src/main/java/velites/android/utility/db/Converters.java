package velites.android.utility.db;

import android.arch.persistence.room.TypeConverter;

import java.util.Calendar;

import velites.java.utility.misc.DateTimeUtil;

public class Converters {
    private Converters() {}

    public static class CalendarAsLong {
        @TypeConverter
        public static Calendar fromTimestamp(Long value) {
            return value == null ? null : DateTimeUtil.convertToCalendar(value, null);
        }

        @TypeConverter
        public static Long toTimestamp(Calendar date) {
            return date == null ? null : date.getTimeInMillis();
        }
    }
}
