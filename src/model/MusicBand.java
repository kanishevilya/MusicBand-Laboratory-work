package model;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


public class MusicBand implements Comparable<MusicBand> {

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

    private long id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private ZonedDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Integer numberOfParticipants; //Поле может быть null, Значение поля должно быть больше 0
    private Integer albumsCount; //Поле не может быть null, Значение поля должно быть больше 0
    private MusicGenre genre; //Поле может быть null
    private Person frontMan; //Поле может быть null


    public MusicBand() {
    }

    public MusicBand(long id, String name, Coordinates coordinates, ZonedDateTime creationDate,
                     Integer numberOfParticipants, Integer albumsCount, MusicGenre genre, Person frontMan) {
        setId(id);
        setName(name);
        setCoordinates(coordinates);
        setCreationDate(creationDate);
        setNumberOfParticipants(numberOfParticipants);
        setAlbumsCount(albumsCount);
        setGenre(genre);
        setFrontMan(frontMan);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID группы должен быть больше 0.");
        }
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Название группы не может быть null или пустым.");
        }
        this.name = name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        if (coordinates == null) {
            throw new IllegalArgumentException("Координаты группы не могут быть null.");
        }
        this.coordinates = coordinates;
    }

    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(ZonedDateTime creationDate) {
        if (creationDate == null) {
            throw new IllegalArgumentException("Дата создания не может быть null.");
        }
        this.creationDate = creationDate;
    }

    public Integer getNumberOfParticipants() {
        return numberOfParticipants;
    }

    public void setNumberOfParticipants(Integer numberOfParticipants) {
        if (numberOfParticipants != null && numberOfParticipants <= 0) {
            throw new IllegalArgumentException("Количество участников должно быть больше 0.");
        }
        this.numberOfParticipants = numberOfParticipants;
    }

    public Integer getAlbumsCount() {
        return albumsCount;
    }

    public void setAlbumsCount(Integer albumsCount) {
        if (albumsCount == null) {
            throw new IllegalArgumentException("Количество альбомов не может быть null.");
        }
        if (albumsCount <= 0) {
            throw new IllegalArgumentException("Количество альбомов должно быть больше 0.");
        }
        this.albumsCount = albumsCount;
    }

    public MusicGenre getGenre() {
        return genre;
    }

    public void setGenre(MusicGenre genre) {
        this.genre = genre;
    }

    public Person getFrontMan() {
        return frontMan;
    }

    public void setFrontMan(Person frontMan) {
        this.frontMan = frontMan;
    }


    @Override
    public int compareTo(MusicBand other) {
        return this.name.compareToIgnoreCase(other.name);
    }

    @Override
    public String toString() {
        return "MusicBand{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + (creationDate != null ? creationDate.format(DATE_FORMATTER) : "null") +
                ", numberOfParticipants=" + numberOfParticipants +
                ", albumsCount=" + albumsCount +
                ", genre=" + genre +
                ", frontMan=" + frontMan +
                "}";
    }
}
