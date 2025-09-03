package org.ocean.leaveservice.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.*;

@Service
@Slf4j
public class DateTimeUtils {

    public static boolean isBeforeToday(LocalDateTime localDateTime) {
        return localDateTime.toLocalDate().isBefore(LocalDate.now());
    }
}
