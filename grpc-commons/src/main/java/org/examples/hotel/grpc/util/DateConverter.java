package org.examples.hotel.grpc.util;

import org.examples.hotel.grpc.common.DateProto;

import java.time.LocalDate;

/**
 * Utilitaire pour convertir entre java.time.LocalDate et DateProto
 */
public class DateConverter {

    /**
     * Convertit un LocalDate en DateProto
     */
    public static DateProto toProto(LocalDate date) {
        if (date == null) {
            return null;
        }
        return DateProto.newBuilder()
                .setYear(date.getYear())
                .setMonth(date.getMonthValue())
                .setDay(date.getDayOfMonth())
                .build();
    }

    /**
     * Convertit un DateProto en LocalDate
     */
    public static LocalDate fromProto(DateProto proto) {
        if (proto == null) {
            return null;
        }
        return LocalDate.of(proto.getYear(), proto.getMonth(), proto.getDay());
    }

    /**
     * Convertit une chaîne ISO (yyyy-MM-dd) en DateProto
     */
    public static DateProto fromString(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        LocalDate date = LocalDate.parse(dateStr);
        return toProto(date);
    }

    /**
     * Convertit un DateProto en chaîne ISO (yyyy-MM-dd)
     */
    public static String toString(DateProto proto) {
        if (proto == null) {
            return null;
        }
        return fromProto(proto).toString();
    }

    /**
     * Crée un DateProto pour aujourd'hui
     */
    public static DateProto today() {
        return toProto(LocalDate.now());
    }

    /**
     * Vérifie si une date proto est valide
     */
    public static boolean isValid(DateProto proto) {
        if (proto == null) {
            return false;
        }
        try {
            LocalDate.of(proto.getYear(), proto.getMonth(), proto.getDay());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Compare deux DateProto (-1, 0, 1)
     */
    public static int compare(DateProto d1, DateProto d2) {
        if (d1 == null || d2 == null) {
            throw new IllegalArgumentException("Dates cannot be null");
        }
        LocalDate date1 = fromProto(d1);
        LocalDate date2 = fromProto(d2);
        return date1.compareTo(date2);
    }
}

