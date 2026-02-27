package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Person {

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private String name; //Поле не может быть null, Строка не может быть пустой

    private LocalDateTime birthday; //Поле может быть null

    private Integer height; //Поле может быть null, Значение поля должно быть больше 0

    private String passportID; //Длина строки не должна быть больше 28, Поле не может быть null


    public Person() {
    }

    public Person(String name, LocalDateTime birthday, Integer height, String passportID) {
        setName(name);
        setBirthday(birthday);
        setHeight(height);
        setPassportID(passportID);
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя персоны не может быть null или пустым.");
        }
        this.name = name;
    }


    public LocalDateTime getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDateTime birthday) {
        this.birthday = birthday;
    }


    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        if (height != null && height <= 0) {
            throw new IllegalArgumentException("Рост должен быть больше 0.");
        }
        this.height = height;
    }

    public String getPassportID() {
        return passportID;
    }

    public void setPassportID(String passportID) {
        if (passportID == null) {
            throw new IllegalArgumentException("ID паспорта не может быть null.");
        }
        if (passportID.length() > 28) {
            throw new IllegalArgumentException("ID паспорта не должен превышать 28 символов.");
        }
        this.passportID = passportID;
    }

    @Override
    public String toString() {
        return "Person {\n" +
                "  name       = \"" + name + "\"\n" +
                "  birthday   = " + (birthday != null ? birthday.format(DATE_FORMATTER) : "null") + "\n" +
                "  height     = " + height + "\n" +
                "  passportID = \"" + passportID + "\"\n" +
                "}";
    }
}
