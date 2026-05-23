package io.github.mojtaba.microservice.starter.iam.servicemodel.util;

import com.github.mfathi91.time.PersianDate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtil {

    private final static DateTimeFormatter YMFormatter = DateTimeFormatter.ofPattern("yyyyMM");
    public static final String DATE_TIME_DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static String getShamsiYM(Date date) {
        if (date == null) {
            return "";
        }
        return YMFormatter.format(PersianDate.fromGregorian(toLocalDate(date)));
    }

    public static LocalDate toLocalDate(Date input) {
        return input.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static Date toDate(LocalDateTime input){
        return Date.from(input.atZone(ZoneId.systemDefault()).toInstant());
    }

}
