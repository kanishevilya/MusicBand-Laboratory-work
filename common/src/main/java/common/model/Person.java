package common.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Персона (фронтмен и т.п.).
 */
public class Person implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private String name;
    private LocalDateTime birthday;
    private Integer height;
    private String passportID;

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
        return "Person {\n"
                + "  name       = \"" + name + "\"\n"
                + "  birthday   = " + (birthday != null ? birthday.format(DATE_FORMATTER) : "null") + "\n"
                + "  height     = " + height + "\n"
                + "  passportID = \"" + passportID + "\"\n"
                + "}";
    }
}
