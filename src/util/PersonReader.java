package util;

import model.Person;

import java.time.LocalDateTime;


/**
 * Читает человека
 */
public class PersonReader {
    private PersonReader() {
    }

    /**
     * Читает человека
     *
     * @param input обработчик ввода
     * @param label метка (например, "фронтмена")
     * @param name  имя
     * @return человек
     */
    public static Person read(InputHandler input, String label, String name) {
        String personName = name != null ? name : input.rawScan(String.format("Введите имя %s", label));
        if (personName != null && !personName.isEmpty()) {
            Person frontMan = new Person();
            frontMan.setName(personName);
            frontMan.setBirthday(input.readBirthdayOrNull(String.format("Введите дату рождения %s", label)));
            frontMan.setHeight(
                    input.readPositiveIntOrNull(String.format("Введите рост %s (или Enter для пропуска)", label)));
            frontMan.setPassportID(input.readPassportId(String.format("Введите ID паспорта %s)", label)));
            return frontMan;
        }
        return null;
    }

    public static Person readForUpdate(InputHandler input, Person current, String nameParam) {
        if (current == null) {
            return read(input, "фронтмена", nameParam);
        }

        Person person = new Person();

        String personName = (nameParam != null && !nameParam.isEmpty())
                ? nameParam
                : input.rawScan(String.format("Имя фронтмена [%s]", current.getName()));
        person.setName((personName == null || personName.isEmpty()) ? current.getName() : personName);

        String currB = current.getBirthday() == null ? "не указано" : current.getBirthday().format(Person.DATE_FORMATTER);
        String birthdayRaw = input.rawScan(String.format("Введите дату рождения фронтмена [%s] (формат yyyy-MM-dd HH:mm:ss) (введите 'null' для соответ. значения)", currB));
        if (birthdayRaw == null || birthdayRaw.isEmpty()) {
            person.setBirthday(current.getBirthday());
        } else if (birthdayRaw.equalsIgnoreCase("null")) {
            person.setBirthday(InputHandler.confirmNull(input) ? null : current.getBirthday());
        } else {
            try {
                person.setBirthday(LocalDateTime.parse(birthdayRaw, Person.DATE_FORMATTER));
            } catch (Exception e) {
                System.out.println("Неверный формат даты. Оставлено прежнее значение.");
                person.setBirthday(current.getBirthday());
            }
        }

        String currH = current.getHeight() == null ? "не указано" : current.getHeight().toString();
        String heightRaw = input.rawScan(String.format("Введите рост фронтмена [%s] (или Enter для пропуска) (введите 'null' для соответ. значения)", currH));
        if (heightRaw == null || heightRaw.isEmpty()) {
            person.setHeight(current.getHeight());
        } else if (heightRaw.equalsIgnoreCase("null")) {
            person.setHeight(InputHandler.confirmNull(input) ? null : current.getHeight());
        } else {
            try {
                int h = Integer.parseInt(heightRaw);
                if (h <= 0) {
                    System.out.println("Рост должен быть >0. Оставлено прежнее значение.");
                    person.setHeight(current.getHeight());
                } else person.setHeight(h);
            } catch (NumberFormatException e) {
                System.out.println("Неверный формат роста. Оставлено прежнее значение.");
                person.setHeight(current.getHeight());
            }
        }

        String passportPrompt = String.format("Passport ID [%s] (max 28 символов)", current.getPassportID());
        String passport = input.rawScan(passportPrompt);
        if (passport == null || passport.isEmpty()) {
            person.setPassportID(current.getPassportID());
        } else {
            if (passport.length() > 28) {
                System.out.println("Passport ID слишком длинный. Оставлено прежнее значение.");
                person.setPassportID(current.getPassportID());
            } else {
                person.setPassportID(passport);
            }
        }

        return person;
    }
}
