package util;

import model.Person;

public class PersonReader {
    private PersonReader() {}

    public static Person read(InputHandler input, String label, String name) {
        String personName = name != null ? name : input.rawScan(String.format("Введите имя %s", label));
        if (personName != null && !personName.isEmpty()) {
            Person frontMan = new Person();
            frontMan.setName(personName);
            frontMan.setBirthday(input.readBirthdayOrNull(String.format("Введите дату рождения %s (или Enter для пропуска)", label)));
            frontMan.setHeight(input.readPositiveIntOrNull(String.format("Введите рост %s (или Enter для пропуска)", label)));
            frontMan.setPassportID(input.readPassportId(String.format("Введите ID паспорта %s)", label)));
            return frontMan;
        }
        return null;
    }

}
