package model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Класс представляющий музыкальную группу
 * Реализует интерфейс @link{Comparable} для возможности сортировки групп по
 * названию
 */
public class MusicBand implements Comparable<MusicBand> {

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssXXX");

    private long id; // Значение поля должно быть больше 0, Значение этого поля должно быть
                     // уникальным, Значение этого поля должно генерироваться автоматически
    private String name; // Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; // Поле не может быть null
    private ZonedDateTime creationDate; // Поле не может быть null, Значение этого поля должно генерироваться
                                        // автоматически
    private Integer numberOfParticipants; // Поле может быть null, Значение поля должно быть больше 0
    private Integer albumsCount; // Поле не может быть null, Значение поля должно быть больше 0
    private MusicGenre genre; // Поле может быть null
    private Person frontMan; // Поле может быть null
    private BigDecimal price; // Поле не может быть null, Значение поля должно быть больше 0
    private Currency currency; // Поле не может быть null

    public MusicBand() {
    }

    public MusicBand(long id, String name, Coordinates coordinates, ZonedDateTime creationDate,
            Integer numberOfParticipants, Integer albumsCount, MusicGenre genre, Person frontMan,
            BigDecimal price, Currency currency) {
        setId(id);
        setName(name);
        setCoordinates(coordinates);
        setCreationDate(creationDate);
        setNumberOfParticipants(numberOfParticipants);
        setAlbumsCount(albumsCount);
        setGenre(genre);
        setFrontMan(frontMan);
        setPrice(price);
        setCurrency(currency);
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        if (price == null) {
            throw new IllegalArgumentException("Стоимость не может быть null.");
        }
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Стоимость должна быть больше 0.");
        }
        this.price = price;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        if (currency == null) {
            throw new IllegalArgumentException("Валюта не может быть null.");
        }
        this.currency = currency;
    }

    /**
     * Сравнивает две группы по названию
     */
    @Override
    public int compareTo(MusicBand other) {
        return this.name.compareToIgnoreCase(other.name);
    }

    @Override
    public String toString() {
        return "MusicBand {\n" +
                "  id                   = " + id + "\n" +
                "  name                 = \"" + name + "\"\n" +
                "  coordinates          = " + coordinates + "\n" +
                "  creationDate         = " + (creationDate != null ? creationDate.format(DATE_FORMATTER) : "null")
                + "\n" +
                "  numberOfParticipants = " + numberOfParticipants + "\n" +
                "  albumsCount          = " + albumsCount + "\n" +
                "  genre                = " + genre + "\n" +
                "  frontMan             = " + frontMan + "\n" +
                "  price                = " + price + "\n" +
                "  currency             = " + currency + "\n" +
                "}";
    }
}
