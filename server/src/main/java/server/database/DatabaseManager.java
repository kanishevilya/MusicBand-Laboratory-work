package server.database;

import common.model.MusicBand;
import common.model.Coordinates;
import common.model.Person;
import common.model.MusicGenre;
import server.util.PasswordHasher;

import java.sql.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.TreeMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DatabaseManager {
    private static final Logger logger = LogManager.getLogger(DatabaseManager.class);

    private static final String URL = "***REMOVED***";
    private static final String USER = "***REMOVED***";
    private static final String PASSWORD = "***REMOVED***";

    private Connection connection;

    public DatabaseManager() {
        try {
            Class.forName("org.postgresql.Driver");
            connect();
            createTablesIfNotExist();
        } catch (ClassNotFoundException e) {
            logger.fatal("Драйвер PostgreSQL не найден в classpath!", e);
            throw new RuntimeException(e);
        } catch (SQLException e) {
            logger.error("Не удалось первично подключиться к БД! Сервер продолжит работу, переподключение будет выполнено при первой необходимости.");
        }
    }

    private void connect() throws SQLException {
        this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
        logger.info("Успешное подключение к базе данных!");
    }

    private synchronized void ensureConnection() throws SQLException {
        if (connection == null || connection.isClosed() || !connection.isValid(2)) {
            logger.warn("Обнаружен обрыв соединения с БД. Пытаемся переподключиться...");
            connect();
        }
    }

    /**
     * Создание структуры таблиц и сиквенса.
     */
    private void createTablesIfNotExist() {
        try {
            ensureConnection();
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(
                        """
                        CREATE TABLE IF NOT EXISTS users (
                        id SERIAL PRIMARY KEY, 
                        login VARCHAR(255) UNIQUE NOT NULL, 
                        password_hash VARCHAR(64) NOT NULL);
                        """
                );

                statement.executeUpdate("CREATE SEQUENCE IF NOT EXISTS music_band_id_seq START WITH 1;");

                statement.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS music_bands (" +
                                "id BIGINT PRIMARY KEY DEFAULT nextval('music_band_id_seq'), " +
                                "name TEXT NOT NULL CHECK (name <> ''), " +
                                "x DOUBLE PRECISION NOT NULL, " +
                                "y REAL NOT NULL CHECK (y <= 968), " +
                                "creation_date TIMESTAMPTZ NOT NULL, " +
                                "number_of_participants INT CHECK (number_of_participants > 0), " +
                                "albums_count INT NOT NULL CHECK (albums_count > 0), " +
                                "genre VARCHAR(50), " +
                                "frontman_name TEXT CHECK (frontman_name <> ''), " +
                                "frontman_birthday TIMESTAMP, " +
                                "frontman_height INT CHECK (frontman_height > 0), " +
                                "frontman_passport_id VARCHAR(28), " +
                                "owner_login VARCHAR(255) NOT NULL REFERENCES users(login));"
                );
                logger.info("Проверка/создание структуры таблиц в БД успешно завершено.");
            }
        } catch (SQLException e) {
            logger.error("Ошибка при создании таблиц БД.", e);
        }
    }

    /**
     * Регистрация нового пользователя в системе.
     */
    public synchronized boolean registerUser(String login, String password) {
        String query = "INSERT INTO users (login, password_hash) VALUES (?, ?);";
        try {
            ensureConnection();
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, login);
                ps.setString(2, PasswordHasher.hashPassword(password, login));

                ps.executeUpdate();
                logger.info("Пользователь {} успешно зарегистрирован в БД.", login);
                return true;
            }
        } catch (SQLException e) {
            logger.warn("Не удалось зарегистрировать пользователя {} (возможно, логин занят или БД недоступна).", login);
            return false;
        }
    }

    /**
     * Проверка подлинности пользователя (Авторизация).
     */
    public synchronized boolean validateUser(String login, String password) {
        String query = "SELECT * FROM users WHERE login = ? AND password_hash = ?;";
        try {
            ensureConnection();
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, login);
                ps.setString(2, PasswordHasher.hashPassword(password, login));

                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка при валидации пользователя {} в БД.", login, e);
            return false;
        }
    }

    /**
     * Загрузка всей коллекции из БД в память при старте сервера.
     */
    public synchronized TreeMap<Long, MusicBand> loadCollection() {
        TreeMap<Long, MusicBand> collection = new TreeMap<>();
        String query = "SELECT * FROM music_bands;";

        try {
            ensureConnection();
            try (Statement statement = connection.createStatement();
                 ResultSet rs = statement.executeQuery(query)) {

                while (rs.next()) {
                    Coordinates coords = new Coordinates();
                    coords.setX(rs.getDouble("x"));
                    coords.setY(rs.getFloat("y"));

                    Person frontMan = null;
                    String fmName = rs.getString("frontman_name");
                    if (fmName != null) {
                        frontMan = new Person();
                        frontMan.setName(fmName);
                        Timestamp bday = rs.getTimestamp("frontman_birthday");
                        if (bday != null) {
                            frontMan.setBirthday(bday.toLocalDateTime());
                        }
                        int height = rs.getInt("frontman_height");
                        if (!rs.wasNull()) {
                            frontMan.setHeight(height);
                        }
                        frontMan.setPassportID(rs.getString("frontman_passport_id"));
                    }

                    MusicBand band = new MusicBand();
                    band.setId(rs.getLong("id"));
                    band.setName(rs.getString("name"));
                    band.setCoordinates(coords);

                    Timestamp dbDate = rs.getTimestamp("creation_date");
                    if (dbDate != null) {
                        band.setCreationDate(ZonedDateTime.ofInstant(dbDate.toInstant(), ZoneId.systemDefault()));
                    } else {
                        band.setCreationDate(ZonedDateTime.now());
                    }

                    int parts = rs.getInt("number_of_participants");
                    if (!rs.wasNull()) {
                        band.setNumberOfParticipants(parts);
                    }
                    band.setAlbumsCount(rs.getInt("albums_count"));

                    String genreStr = rs.getString("genre");
                    if (genreStr != null) {
                        band.setGenre(MusicGenre.valueOf(genreStr));
                    }

                    band.setFrontMan(frontMan);
                    band.setOwnerLogin(rs.getString("owner_login"));

                    collection.put(band.getId(), band);
                }
                logger.info("Коллекция успешно загружена из БД. Всего элементов: {}", collection.size());
            }
        } catch (SQLException e) {
            logger.error("Критическая ошибка при загрузке коллекции из БД! БД недоступна.", e);
        }
        return collection;
    }

    public synchronized boolean clearUserBands(String username) {
        String query = "DELETE FROM music_bands WHERE owner_login = ?;";
        try {
            ensureConnection();
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, username);
                int deletedRows = ps.executeUpdate();
                logger.info("Пользователь {} очистил коллекцию. Удалено записей в БД: {}", username, deletedRows);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Ошибка при массовом удалении элементов пользователя {}", username, e);
            return false;
        }
    }

    /**
     * Массовое удаление по списку ID (для remove_greater и т.д.) за 1 запрос.
     */
    public synchronized boolean deleteMusicBands(List<Long> ids, String username) {
        if (ids == null || ids.isEmpty()) {
            return true;
        }

        StringBuilder queryBuilder = new StringBuilder("DELETE FROM music_bands WHERE owner_login = ? AND id IN (");
        for (int i = 0; i < ids.size(); i++) {
            queryBuilder.append("?");
            if (i < ids.size() - 1) queryBuilder.append(", ");
        }
        queryBuilder.append(");");

        try {
            ensureConnection();
            try (PreparedStatement ps = connection.prepareStatement(queryBuilder.toString())) {
                ps.setString(1, username);

                for (int i = 0; i < ids.size(); i++) {
                    ps.setLong(i + 2, ids.get(i));
                }

                int deletedRows = ps.executeUpdate();
                logger.info("Батч-удаление выполнено. Удалено {} элементов пользователя {}", deletedRows, username);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Ошибка при пакетном удалении элементов", e);
            return false;
        }
    }

    public synchronized long insertMusicBand(MusicBand band, String ownerLogin) {
        String query = "INSERT INTO music_bands (name, x, y, creation_date, number_of_participants, " +
                "albums_count, genre, frontman_name, frontman_birthday, frontman_height, " +
                "frontman_passport_id, owner_login) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id;";

        try {
            ensureConnection();
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, band.getName());
                ps.setDouble(2, band.getCoordinates().getX());
                ps.setFloat(3, band.getCoordinates().getY());

                if (band.getCreationDate() == null) {
                    band.setCreationDate(ZonedDateTime.now());
                }
                Timestamp timestamp = Timestamp.from(band.getCreationDate().toInstant());
                ps.setTimestamp(4, timestamp);

                if (band.getNumberOfParticipants() != null) {
                    ps.setInt(5, band.getNumberOfParticipants());
                } else {
                    ps.setNull(5, Types.INTEGER);
                }

                ps.setInt(6, band.getAlbumsCount());

                if (band.getGenre() != null) {
                    ps.setString(7, band.getGenre().name());
                } else {
                    ps.setNull(7, Types.VARCHAR);
                }

                Person fm = band.getFrontMan();
                if (fm != null) {
                    ps.setString(8, fm.getName());
                    if (fm.getBirthday() != null) {
                        ps.setTimestamp(9, Timestamp.valueOf(fm.getBirthday()));
                    } else {
                        ps.setNull(9, Types.TIMESTAMP);
                    }
                    if (fm.getHeight() != null) {
                        ps.setInt(10, fm.getHeight());
                    } else {
                        ps.setNull(10, Types.INTEGER);
                    }
                    ps.setString(11, fm.getPassportID());
                } else {
                    ps.setNull(8, Types.VARCHAR);
                    ps.setNull(9, Types.TIMESTAMP);
                    ps.setNull(10, Types.INTEGER);
                    ps.setNull(11, Types.VARCHAR);
                }

                ps.setString(12, ownerLogin);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        long generatedId = rs.getLong(1);
                        logger.info("Группа '{}' успешно добавлена в БД под ID: {}. Владелец: {}", band.getName(), generatedId, ownerLogin);
                        return generatedId;
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка при вставке MusicBand в базу данных!", e);
        }
        return -1;
    }

    /**
     * Удаление элемента из БД по его ID с жесткой проверкой владельца.
     */
    public synchronized boolean deleteMusicBand(long id, String username) {
        String query = "DELETE FROM music_bands WHERE id = ? AND owner_login = ?;";
        try {
            ensureConnection();
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setLong(1, id);
                ps.setString(2, username);

                int affectedRows = ps.executeUpdate();
                if (affectedRows > 0) {
                    logger.info("Объект с ID {} успешно удален из БД пользователем {}.", id, username);
                    return true;
                } else {
                    logger.warn("Попытка удаления ID {} пользователем {} отклонена: объект не существует или нет прав.", id, username);
                    return false;
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка при удалении MusicBand из базы данных!", e);
            return false;
        }
    }

    /**
     * Обновление существующего элемента в БД по его ID (только если текущий юзер — владелец).
     */
    public synchronized boolean updateMusicBand(long id, MusicBand band, String username) {
        String query = "UPDATE music_bands SET name = ?, x = ?, y = ?, number_of_participants = ?, " +
                "albums_count = ?, genre = ?, frontman_name = ?, frontman_birthday = ?, " +
                "frontman_height = ?, frontman_passport_id = ? " +
                "WHERE id = ? AND owner_login = ?;";

        try {
            ensureConnection();
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, band.getName());
                ps.setDouble(2, band.getCoordinates().getX());
                ps.setFloat(3, band.getCoordinates().getY());

                if (band.getNumberOfParticipants() != null) {
                    ps.setInt(4, band.getNumberOfParticipants());
                } else {
                    ps.setNull(4, Types.INTEGER);
                }

                ps.setInt(5, band.getAlbumsCount());

                if (band.getGenre() != null) {
                    ps.setString(6, band.getGenre().name());
                } else {
                    ps.setNull(6, Types.VARCHAR);
                }

                Person fm = band.getFrontMan();
                if (fm != null) {
                    ps.setString(7, fm.getName());
                    if (fm.getBirthday() != null) {
                        ps.setTimestamp(8, Timestamp.valueOf(fm.getBirthday()));
                    } else {
                        ps.setNull(8, Types.TIMESTAMP);
                    }
                    if (fm.getHeight() != null) {
                        ps.setInt(9, fm.getHeight());
                    } else {
                        ps.setNull(9, Types.INTEGER);
                    }
                    ps.setString(10, fm.getPassportID());
                } else {
                    ps.setNull(7, Types.VARCHAR);
                    ps.setNull(8, Types.TIMESTAMP);
                    ps.setNull(9, Types.INTEGER);
                    ps.setNull(10, Types.VARCHAR);
                }

                ps.setLong(11, id);
                ps.setString(12, username);

                int affectedRows = ps.executeUpdate();
                if (affectedRows > 0) {
                    logger.info("Объект с ID {} успешно обновлен в БД владельцем {}.", id, username);
                    return true;
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка при обновлении MusicBand в базе данных!", e);
        }
        return false;
    }
}