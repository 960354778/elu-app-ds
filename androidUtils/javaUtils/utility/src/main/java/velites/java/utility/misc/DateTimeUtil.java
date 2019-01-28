package velites.java.utility.misc;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.IllegalFormatException;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Created by regis on 17/4/20.
 */

public final class DateTimeUtil {
    private DateTimeUtil() {}

    public static final SimpleDateFormat createSimpleDateFormat(String format, Locale loc, TimeZone zone) {
        SimpleDateFormat sdf = loc == null ? new SimpleDateFormat(format) : new SimpleDateFormat(format, loc);
        if (zone != null) {
            sdf.setTimeZone(zone);
        }
        return sdf;
    }

    public static final String DEFAULT_DATETIME_FORMAT_DETAIL = "yyyy-MM-dd HH:mm:ss.SSSSSSZ";
    public static final DateFormat DEFAULT_DATEFORMAT_DATETIME_DETAIL = createSimpleDateFormat(DEFAULT_DATETIME_FORMAT_DETAIL, StringUtil.INVARIANT_LOCALE, null);
    public static final String DEFAULT_DATE_FORMAT_SIMPLE = "yyyyMMdd";
    public static final DateFormat DEFAULT_DATEFORMAT_DATE_SIMPLE = createSimpleDateFormat(DEFAULT_DATE_FORMAT_SIMPLE, StringUtil.INVARIANT_LOCALE, null);

    public static final int MS_PER_SECOND = 1000;
    public static final int SECONDS_PER_MINUTE = 60;
    public static final int MINUTES_PER_HOUR = 60;
    public static final int HOURS_PER_DAY = 60;

    private static final String DELIMITER_SECOND_MS = ".";
    private static final String DELIMITER_MINUTE_SECOND = ":";
    private static final String DELIMITER_HOUR_MINUTE = ":";
    private static final String DELIMITER_DAY_HOUR = ".";

    public enum DurationFormatEnsure {
        NATURE, ENSURE_MINUTE, ENSURE_HOUR, ENSURE_DAY
    }
    public enum DurationFormatLimit {
        NATURE, LIMIT_SECOND, LIMIT_MINUTE, LIMIT_HOUR
    }

    public static final String formatDuration(int ms, DurationFormatLimit limit, DurationFormatEnsure ensure, int digitsInMs) {
        int s = 0, m = 0, h = 0, d = 0;
        s = ms / MS_PER_SECOND;
        ms %= MS_PER_SECOND;
        if (s > 0 && limit != DurationFormatLimit.LIMIT_SECOND) {
            m = s/ SECONDS_PER_MINUTE;
            s %= SECONDS_PER_MINUTE;
            if (m > 0 || limit != DurationFormatLimit.LIMIT_MINUTE) {
                h = m / MINUTES_PER_HOUR;
                m %= MINUTES_PER_HOUR;
                if (h > 0 && limit != DurationFormatLimit.LIMIT_HOUR) {
                    d = h / HOURS_PER_DAY;
                    h %= HOURS_PER_DAY;
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        if (d > 0 || ensure == DurationFormatEnsure.ENSURE_DAY) {
            sb.append(d);
            sb.append(DELIMITER_DAY_HOUR);
        }
        if (sb.length() > 0 || h > 0 || ensure == DurationFormatEnsure.ENSURE_HOUR) {
            sb.append(StringUtil.padLeft(String.valueOf(h), '0', 2));
            sb.append(DELIMITER_HOUR_MINUTE);
        }
        if (sb.length() > 0 || m > 0 || ensure == DurationFormatEnsure.ENSURE_MINUTE) {
            sb.append(StringUtil.padLeft(String.valueOf(m), '0', 2));
            sb.append(DELIMITER_MINUTE_SECOND);
        }
        sb.append(StringUtil.padLeft(String.valueOf(s), '0', 2));
        sb.append(DELIMITER_SECOND_MS);
        if (digitsInMs > 0) {
            int diff = digitsInMs - 3;
            if (diff < 0) {
                ms = (int)Math.round(ms / Math.pow(10.0, -diff));
            }
            sb.append(ms);
            for (int i = 0; i < diff; i++) {
                sb.append("0");
            }
        } else {
            sb.append(ms);
        }
        return sb.toString();
    }

    private static final char DELIMITER_1 = '.';
    private static final char DELIMITER_2 = ':';
    public static final int parseDuration(String str) throws ParseException {
        if (str == null) {
            throw new ParseException("str is null", -1);
        }
        int ms = -1, s = -1, m = -1, h = -1, d = -1;
        String v = StringUtil.STRING_EMPTY;
        for (int i = str.length() - 1; i >= 0; i--) {
            Character c = str.charAt(i);
            if (Character.isDigit(c)) {
                v = c + v;
                if (i > 0) {
                    continue;
                }
                c = null;
            }
            Byte k = null;
            if (k == null && (c == null || c == DELIMITER_1)) {
                if (StringUtil.isNullOrEmpty(v)) {
                    throw new ParseException(StringUtil.formatInvariant("Unexpected '%s' in \"%s\"", c, str), i);
                }
                if (ms < 0) {
                    k = 1;
                } else if (m >= 0 && h < 0) {
                    k = 4;
                } else {
                    throw new ParseException(StringUtil.formatInvariant("Unexpected '%s' in \"%s\"", c, str), i);
                }
            }
            if (k == null && (c == null || c == DELIMITER_2)) {
                if (StringUtil.isNullOrEmpty(v)) {
                    throw new ParseException(StringUtil.formatInvariant("Unexpected '%s' in \"%s\"", c, str), i);
                }
                if (ms >= 0 && s < 0) {
                    k = 2;
                } else if (s >= 0 && m < 0) {
                    k = 3;
                } else {
                    throw new ParseException(StringUtil.formatInvariant("Unexpected '%s' in \"%s\"", c, str), i);
                }
            }
            if (k == null) {
                throw new ParseException(StringUtil.formatInvariant("Unexpected '%s' in \"%s\"", c, str), i);
            }
            switch (k) {
                case 1:
                    v = StringUtil.padRight(v, '0', 3);
                    if (v.length() > 3) {
                        v = v.substring(0, 3) + '.' + v.substring(3);
                        ms = Math.round(Float.parseFloat(v));
                    } else {
                        ms = Integer.parseInt(v);
                    }
                    break;
                case 2:
                    s = Integer.parseInt(v);
                    break;
                case 3:
                    m = Integer.parseInt(v);
                    break;
                case 4:
                    h = Integer.parseInt(v);
                    break;
            }
            v = StringUtil.STRING_EMPTY;
        }
        return ((((Math.max(d, 0) * HOURS_PER_DAY) + Math.max(h, 0)) * MINUTES_PER_HOUR + Math.max(m, 0)) * SECONDS_PER_MINUTE + Math.max(s, 0)) * MS_PER_SECOND + ms;
    }

    public static Calendar convertToCalendar(Date date, TimeZone zone) {
        Calendar ret = Calendar.getInstance(zone == null ? TimeZone.getDefault() : zone, StringUtil.INVARIANT_LOCALE);
        ret.setTime(date);
        return ret;
    }

    public static Calendar convertToCalendar(long timestamp, TimeZone zone) {
        Calendar ret = Calendar.getInstance(zone == null ? TimeZone.getDefault() : zone, StringUtil.INVARIANT_LOCALE);
        ret.setTime(new Date(timestamp));
        return ret;
    }

    public static Calendar now(TimeZone zone) {
        return convertToCalendar(new Date(), zone);
    }

    public static Calendar now() {
        return now(null);
    }

    public static long nowTimestamp() {
        return new Date().getTime();
    }

    public static Calendar getDate(Calendar c) {
        return new GregorianCalendar(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
    }

    public static Calendar nowDate(TimeZone zone) {
        return getDate(now(zone));
    }

    public static Calendar nowDate() {
        return nowDate(null);
    }

    public static Calendar add(Calendar base, long amount, TimeUnit unit) {
        return convertToCalendar(base.getTimeInMillis() + unit.toMillis(amount), base.getTimeZone());
    }

    public static class CalendarTimestampAdapter extends TypeAdapter<Calendar> implements JsonSerializer<Calendar>, JsonDeserializer<Calendar> {

        @Override
        public Calendar deserialize(JsonElement json, Type typeOfT,
                                    JsonDeserializationContext context) throws JsonParseException {
            if(json == null){
                return null;
            } else {
                try {
                    return convertToCalendar(json.getAsLong(), null);
                } catch (Exception e) {
                    return null;
                }
            }
        }

        @Override
        public JsonElement serialize(Calendar src, Type typeOfSrc,
                                     JsonSerializationContext context) {
            if(src == null){
                return JsonNull.INSTANCE;
            }
            return new JsonPrimitive(src.getTimeInMillis());
        }

        @Override
        public void write(JsonWriter out, Calendar value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.getTimeInMillis());
            }
        }

        @Override
        public Calendar read(JsonReader in) throws IOException {
            JsonToken jt = in.peek();
            if (jt == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            return convertToCalendar(in.nextLong(), null);
        }
    }
}
