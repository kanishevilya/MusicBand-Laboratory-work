package util;

import manager.CollectionManager;
import model.*;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Записывает коллекцию в XML-файл
 */
public class XmlWriter {

    /**
     * Записывает коллекцию в XML-файл
     * 
     * @param filePath          путь к файлу
     * @param collectionManager менеджер коллекции
     * @throws IOException если произошла ошибка ввода-вывода
     */
    public String save(String filePath, CollectionManager collectionManager) throws IOException {
        File file = new File(filePath);

        if (file.exists() && !file.canWrite()) {
            String parent = file.getParent();
            String name = file.getName();
            String baseName;
            String ext = "";

            int dotIndex = name.lastIndexOf('.');
            if (dotIndex != -1) {
                baseName = name.substring(0, dotIndex);
                ext = name.substring(dotIndex);
            } else {
                baseName = name;
            }

            int counter = 1;
            File newFile;
            do {
                String newName = baseName + "+" + counter + ext;
                newFile = parent == null ? new File(newName) : new File(parent, newName);
                counter++;
            } while (newFile.exists());

            file = newFile;
            System.out.println("Нет прав на запись в исходный файл. Коллекция будет сохранена в новый файл: " + file.getAbsolutePath());
            filePath=newFile.getAbsolutePath();
        }

        if (!file.exists()) {
            file.createNewFile();
        }

        String xml = buildXml(collectionManager);
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))) {
            bos.write(xml.getBytes(StandardCharsets.UTF_8));
            bos.flush();
        }
        return filePath;
    }

    /**
     * Строит XML-строку из коллекции
     * 
     * @param collectionManager менеджер коллекции
     * @return XML-строка
     */
    private String buildXml(CollectionManager collectionManager) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<collection>\n");
        for (Map.Entry<Long, MusicBand> entry : collectionManager.getCollection().entrySet()) {
            sb.append("  <entry>\n");
            sb.append("    <key>").append(entry.getKey()).append("</key>\n");
            sb.append(buildBandXml(entry.getValue()));
            sb.append("  </entry>\n");
        }
        sb.append("</collection>\n");
        return sb.toString();
    }

    /**
     * Строит XML-строку из музыкальной группы
     * 
     * @param band музыкальная группа
     * @return XML-строка
     */
    private String buildBandXml(MusicBand band) {
        StringBuilder sb = new StringBuilder();
        sb.append("    <band>\n");
        sb.append("      <id>").append(band.getId()).append("</id>\n");
        sb.append("      <name>").append(escapeXml(band.getName())).append("</name>\n");
        sb.append("      <coordinates>\n");
        sb.append("        <x>").append(band.getCoordinates().getX()).append("</x>\n");
        sb.append("        <y>").append(band.getCoordinates().getY()).append("</y>\n");
        sb.append("      </coordinates>\n");
        sb.append("      <creationDate>")
                .append(band.getCreationDate().format(MusicBand.DATE_FORMATTER))
                .append("</creationDate>\n");
        if (band.getNumberOfParticipants() != null) {
            sb.append("      <numberOfParticipants>")
                    .append(band.getNumberOfParticipants())
                    .append("</numberOfParticipants>\n");
        }
        sb.append("      <albumsCount>").append(band.getAlbumsCount()).append("</albumsCount>\n");
        if (band.getGenre() != null) {
            sb.append("      <genre>").append(band.getGenre().name()).append("</genre>\n");
        }
        if (band.getFrontMan() != null) {
            sb.append(buildPersonXml(band.getFrontMan()));
        }
        sb.append("    </band>\n");
        return sb.toString();
    }

    /**
     * Строит XML-строку из человека
     * 
     * @param person человек
     * @return XML-строка
     */
    private String buildPersonXml(Person person) {
        StringBuilder sb = new StringBuilder();
        sb.append("      <frontMan>\n");
        sb.append("        <personName>").append(escapeXml(person.getName())).append("</personName>\n");
        if (person.getBirthday() != null) {
            sb.append("        <birthday>")
                    .append(person.getBirthday().format(Person.DATE_FORMATTER))
                    .append("</birthday>\n");
        }
        if (person.getHeight() != null) {
            sb.append("        <height>").append(person.getHeight()).append("</height>\n");
        }
        sb.append("        <passportID>").append(escapeXml(person.getPassportID())).append("</passportID>\n");
        sb.append("      </frontMan>\n");
        return sb.toString();
    }

    /**
     * Экранирует XML-строку
     * 
     * @param s строка
     * @return экранированная строка
     */
    private String escapeXml(String s) {
        if (s == null)
            return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
