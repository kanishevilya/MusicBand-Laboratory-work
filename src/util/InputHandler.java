package util;

import exception.ScriptEndException;
import model.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Scanner;


public class InputHandler {

    private final Scanner scanner;

    private boolean interactive;

    public InputHandler(Scanner scanner, boolean interactive) {
        this.scanner = scanner;
        this.interactive = interactive;
    }

    public String readLine() {
        if (scanner.hasNextLine())
            return scanner.nextLine();
        return null;
    }
    
    public String rawScan(String message) {
        if (interactive)
            System.out.print(message + ": ");
        String line = readLine();
        if (line == null)
            return null;
        return line.trim().isEmpty() ? null : line.trim();
    }

    private String rawScanOrThrow(String message) {
        String val = rawScan(message    );
        if (val == null) {
            throw new ScriptEndException(
                    "Неожиданный конец ввода при запросе поля: \"" + message + "\"");
        }
        return val;
    }

    public String readRequiredString(String message) {
        while (true) {
            String value = rawScan(message);
            if (value != null && !value.isEmpty()){
                return value;
            }
            System.out.println("Ошибка: поле не может быть пустым. Попробуйте снова.");
        }
    }


    public double readDouble(String message) {
        while (true) {
            String value = rawScan(message);
            if (value == null) {
                System.out.println("Ошибка: введите число.");
                continue;
            }
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: требуется вещественное число. Попробуйте снова.");
            }
        }
    }

    public Float readFloat(String message) {
        return readFloatLessThanMax(message, null);
    }

    public Float readFloatLessThanMax(String message, Float maxValue) {
        while (true) {
            String value = rawScan(message);
            if (value == null) {
                System.out.println("Ошибка: поле не может быть пустым.");
                continue;
            }
            try {
                Float f = Float.parseFloat(value);
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
    
    public Integer readPositiveIntOrNull(String message) {
        while (true) {
            String value = rawScan(message);
            if (value == null || value.isEmpty())
                return null;
            try {
                int i = Integer.parseInt(value);
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

    public Integer readRequiredPositiveInt(String message) {
        while (true) {
            Integer value = readPositiveIntOrNull(message);
            if (value != null)
                return value;
            System.out.println("Ошибка: поле не может быть пустым.");
        }
    }

    public MusicGenre readMusicGenreOrNull(String message) {
        System.out.println("Доступные жанры: " + MusicGenre.valuesString());
        while (true) {
            String value = rawScan(message + " (или Enter для пропуска)");
            if (value == null || value.isEmpty())
                return null;
            try {
                return MusicGenre.valueOf(value.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: неверный жанр. Доступные: " + MusicGenre.valuesString());
            }
        }
    }

    public LocalDateTime readBirthdayOrNull(String message) {
        while (true) {
            String value = rawScan(message + " (формат: yyyy-MM-dd'T'HH:mm:ss, или Enter для пропуска)");
            if (value == null || value.isEmpty())
                return null;
            try {
                return LocalDateTime.parse(value, Person.DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("Ошибка: неверный формат даты. Используйте: yyyy-MM-dd'T'HH:mm:ss");
            }
        }
    }


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

    public void setInteractive(boolean interactive) {
        this.interactive = interactive;
    }

    public Scanner getScanner() {
        return scanner;
    }
}
