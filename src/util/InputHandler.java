package util;

import exception.CancelInputException;
import exception.ScriptEndException;
import model.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Обработчик ввода данных
 */
public class InputHandler {

    private final Scanner scanner;

    private boolean interactive;

    public InputHandler(Scanner scanner, boolean interactive) {
        this.scanner = scanner;
        this.interactive = interactive;
    }

    /**
     * Читает строку ввода
     * 
     * @return введенная строка
     * @throws ScriptEndException если ввод завершён (EOF)
     */
    public String readLine() {
        if (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if(line.equalsIgnoreCase("/cancel"))
                throw new CancelInputException("Ввод завершён (/cancel).");
            return line;
        }
        throw new ScriptEndException("Ввод завершён (EOF).");
    }

    /**
     * Читает строку ввода
     * 
     * @param message сообщение для пользователя
     * @return введенная строка
     */
    public String rawScan(String message) {
        if (interactive)
            System.out.print(message + ": ");
        String line = readLine();
        if (line == null)
            return null;
        return line.trim().isEmpty() ? null : line.trim();
    }

    /**
     * Читает строку ввода, требуя непустой строки
     * 
     * @param message сообщение для пользователя
     * @return введенная строка
     */
    public String readRequiredString(String message) {
        while (true) {
            String value = rawScan(message);
            if (value != null && !value.isEmpty()) {
                return value;
            }
            System.out.println("Ошибка: поле не может быть пустым. Попробуйте снова.");
        }
    }

    /**
     * Читает вещественное число
     * 
     * @param message сообщение для пользователя
     * @return введенное число
     */
    public double readDouble(String message) {
        while (true) {
            String value = rawScan(message+" (максимум 14 знаков после запятой)");
            if (value == null) {
                System.out.println("Ошибка: введите число.");
                continue;
            }
            value = value.replace(',', '.');

            String[] parts = value.split("\\.");
            if (parts.length == 2 && parts[1].length() > 15) {
                System.out.println("Ошибка: слишком много знаков после запятой (максимум 14).");
                continue;
            }

            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: требуется вещественное число. Попробуйте снова.");
            }
        }
    }

    /**
     * Читает вещественное число
     * 
     * @param message сообщение для пользователя
     * @return введенное число
     */
    public Float readFloat(String message) {
        return readFloatLessThanMax(message, null);
    }

    /**
     * Читает вещественное число, не превышающее заданное
     * 
     * @param message  сообщение для пользователя
     * @param maxValue максимальное значение
     * @return введенное число
     */
    public Float readFloatLessThanMax(String message, Float maxValue) {
        while (true) {
            String value = rawScan(message+" (максимум 5 знаков после запятой)");
            if (value == null) {
                System.out.println("Ошибка: поле не может быть пустым.");
                continue;
            }
            value = value.replace(',', '.');

            String[] parts = value.split("\\.");
            if (parts.length == 2 && parts[1].length() > 7) {
                System.out.println("Ошибка: слишком много знаков после запятой (максимум 5).");
                continue;
            }

            try {
                float f= Float.parseFloat(value);
                if (maxValue != null && f > maxValue) {
                    System.out.println("Ошибка: значение не должно превышать " + maxValue + ".");
                    continue;
                }
                return f;
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: требуется вещественное число. Попробуйте снова.");
            }
        }
    }

    /**
     * Читает целое число, не меньшее 1 или null
     * 
     * @param message сообщение для пользователя
     * @return введенное число
     */
    public Integer readPositiveIntOrNull(String message) {
        while (true) {
            String value = rawScan(message);
            if (value == null || value.isEmpty())
                return null;
            try {
                double d = Double.parseDouble(value);
                int i;
                if((int)d==d){
                    i=(int)d;
                }else{
                    i = Integer.parseInt(value);
                }
                if (i <= 0) {
                    System.out.println("Ошибка: значение должно быть больше 0.");
                    continue;
                }
                return i;
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: требуется целое число. Попробуйте снова.");
            }
        }
    }

    /**
     * Читает целое число, не меньшее 1
     * 
     * @param message сообщение для пользователя
     * @return введенное число
     */
    public Integer readRequiredPositiveInt(String message) {
        while (true) {
            Integer value = readPositiveIntOrNull(message);
            if (value != null)
                return value;
            System.out.println("Ошибка: поле не может быть пустым.");
        }
    }

    /**
     * Читает жанр музыки
     * 
     * @param message сообщение для пользователя
     * @return введенное значение
     */
    public MusicGenre readMusicGenreOrNull(String message) {
        System.out.println("Доступные жанры: " + MusicGenre.valuesString());
        while (true) {
            String value = rawScan(message + " (или Enter для пропуска)");
            if (value == null || value.isEmpty())
                return null;
            try {
                try{
                    int integer=Integer.parseInt(value)-1;
                    return MusicGenre.fromOrdinal(integer);
                }catch (NumberFormatException ignored){}
                return MusicGenre.valueOf(value.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: неверный жанр. Доступные: " + MusicGenre.valuesString());
            }
        }
    }

    /**
     * Читает дату рождения
     * 
     * @param message сообщение для пользователя
     * @return введенное значение
     */
    public LocalDateTime readBirthdayOrNull(String message) {
        while (true) {
            String value = rawScan(message + " (формат: yyyy-MM-dd HH:mm:ss, или Enter для пропуска)");
            if (value == null || value.isEmpty())
                return null;

            String[] parts = value.split("[- :]");
            if (parts.length != 6) {
                System.out.println("Ошибка: неверный формат даты. Используйте: yyyy-MM-dd HH:mm:ss");
                continue;
            }

            try {
                int year = Integer.parseInt(parts[0]);
                if (year > 9999) {
                    System.out.println("Ошибка: год не может быть больше 9999.");
                    continue;
                }

                return LocalDateTime.parse(value, Person.DATE_FORMATTER);

            } catch (NumberFormatException | DateTimeParseException e) {
                System.out.println("Ошибка: неверный формат даты. Используйте: yyyy-MM-dd HH:mm:ss");
            }
        }
    }

    /**
     * Читает ID паспорта
     * 
     * @param message сообщение для пользователя
     * @return введенное значение
     */
    public String readPassportId(String message) {
        while (true) {
            String value = readRequiredString(message);
            if (value.length() > 28) {
                System.out.println("Ошибка: длина ID паспорта не должна превышать 28 символов.");
            } else {
                return value;
            }
        }
    }

    public static boolean confirmNull(InputHandler input) {
        String confirm = input.rawScan("Установить значение null? (yes/no)");
        return confirm != null && confirm.equalsIgnoreCase("yes");
    }

    public static Integer readNullableInteger(
            InputHandler input,
            String message,
            Integer current,
            boolean allowNull,
            boolean positiveOnly
    ) {

        String raw = input.rawScan(message + " [" + (current == null ? "null" : current) + "]");

        if (raw == null || raw.isEmpty())
            return current;

        if (raw.equalsIgnoreCase("null")) {
            if (!allowNull) {
                System.out.println("Поле не может быть null");
                return current;
            }
            return confirmNull(input) ? null : current;
        }

        Integer value = Integer.parseInt(raw);

        if (positiveOnly && value <= 0)
            throw new IllegalArgumentException("Значение должно быть > 0");

        return value;
    }

    public static String readString(
            InputHandler input,
            String message,
            String current,
            boolean allowNull,
            boolean allowEmpty,
            int maxLength
    ) {

        String raw = input.rawScan(message + " [" + (current == null ? "null" : current) + "]");

        if (raw == null || raw.isEmpty())
            return current;

        if (raw.equalsIgnoreCase("null")) {
            if (!allowNull)
                throw new IllegalArgumentException("Поле не может быть null");

            return confirmNull(input) ? null : current;
        }

        if (maxLength > 0 && raw.length() > maxLength)
            throw new IllegalArgumentException("Слишком длинная строка");

        return raw;
    }

    /**
     * Устанавливает режим интерактивного ввода
     * 
     * @param interactive режим интерактивного ввода
     */
    public void setInteractive(boolean interactive) {
        this.interactive = interactive;
    }

    public Scanner getScanner() {
        return scanner;
    }
}
