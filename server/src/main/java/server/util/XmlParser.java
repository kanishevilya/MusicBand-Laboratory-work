package server.util;

import common.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.manager.CollectionManager;

import java.io.*;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * Загрузка коллекции из XML.
 */
public class XmlParser {

    private static final Logger log = LogManager.getLogger(XmlParser.class);

    public void load(String filePath, CollectionManager collectionManager) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("Файл не найден: " + filePath);
        }
        if (!file.canRead()) {
            throw new IOException("Нет прав на чтение файла: " + filePath);
        }

        StringBuilder content = new StringBuilder();
        try (Scanner scanner = new Scanner(new BufferedReader(new FileReader(file)))) {
            while (scanner.hasNextLine()) {
                content.append(scanner.nextLine()).append("\n");
            }
        }

        TreeMap<Long, MusicBand> loaded = parseCollection(content.toString());
        collectionManager.setCollection(loaded);
        collectionManager.syncIdCounter();
        log.info("Загружено {} элементов из файла: {}", loaded.size(), filePath);
    }

    private TreeMap<Long, MusicBand> parseCollection(String xml) {
        TreeMap<Long, MusicBand> result = new TreeMap<>();
        int start = 0;
        while (true) {
            int entryStart = xml.indexOf("<entry>", start);
            if (entryStart < 0) {
                break;
            }
            int entryEnd = xml.indexOf("</entry>", entryStart);
            if (entryEnd < 0) {
                break;
            }
            String entryXml = xml.substring(entryStart, entryEnd + 8);
            try {
                long key = Long.parseLong(extract(entryXml, "key"));
                String bandXml = extract(entryXml, "band");
                MusicBand band = parseBand(bandXml);
                result.put(key, band);
            } catch (Exception e) {
                log.warn("Ошибка разбора элемента коллекции, пропускаем: {}", e.getMessage());
            }
            start = entryEnd + 8;
        }
        return result;
    }

    private MusicBand parseBand(String xml) {
        MusicBand band = new MusicBand();

        long id = Long.parseLong(extract(xml, "id"));
        band.setId(id);
        band.setName(extract(xml, "name"));

        String coordXml = extract(xml, "coordinates");
        double cx = Double.parseDouble(extract(coordXml, "x"));
        float cy = Float.parseFloat(extract(coordXml, "y"));
        band.setCoordinates(new Coordinates(cx, cy));

        String dateStr = extract(xml, "creationDate");
        band.setCreationDate(ZonedDateTime.parse(dateStr, MusicBand.DATE_FORMATTER));

        String numOfParts = extractNullable(xml, "numberOfParticipants");
        if (numOfParts != null) {
            band.setNumberOfParticipants(Integer.parseInt(numOfParts));
        }

        band.setAlbumsCount(Integer.parseInt(extract(xml, "albumsCount")));

        String genreStr = extractNullable(xml, "genre");
        if (genreStr != null) {
            band.setGenre(MusicGenre.valueOf(genreStr));
        }

        String frontManXml = extractNullable(xml, "frontMan");
        if (frontManXml != null) {
            Person person = new Person();
            person.setName(extract(frontManXml, "personName"));
            String birthday = extractNullable(frontManXml, "birthday");
            if (birthday != null) {
                try {
                    person.setBirthday(LocalDateTime.parse(birthday, Person.DATE_FORMATTER));
                } catch (DateTimeParseException ignored) {
                }
            }
            String height = extractNullable(frontManXml, "height");
            if (height != null) {
                person.setHeight(Integer.parseInt(height));
            }
            person.setPassportID(extract(frontManXml, "passportID"));
            band.setFrontMan(person);
        }

        return band;
    }

    private String extract(String xml, String tagName) {
        String open = "<" + tagName + ">";
        String close = "</" + tagName + ">";
        int start = xml.indexOf(open);
        int end = xml.indexOf(close);
        if (start < 0 || end < 0) {
            throw new IllegalArgumentException("Тег не найден: " + tagName);
        }
        return xml.substring(start + open.length(), end).trim();
    }

    private String extractNullable(String xml, String tagName) {
        String open = "<" + tagName + ">";
        String close = "</" + tagName + ">";
        int start = xml.indexOf(open);
        int end = xml.indexOf(close);
        if (start < 0 || end < 0) {
            return null;
        }
        String val = xml.substring(start + open.length(), end).trim();
        return val.isEmpty() ? null : val;
    }
}
