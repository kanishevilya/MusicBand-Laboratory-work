package util;

import manager.CollectionManager;
import model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;


public class XmlWriter {

    public void save(String filePath, CollectionManager collectionManager) throws IOException {
        File file = new File(filePath);
        if (file.exists() && !file.canWrite()) {
            throw new IOException("Нет прав на запись в файл: " + filePath);
        }

        String xml = buildXml(collectionManager);
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))) {
            bos.write(xml.getBytes(StandardCharsets.UTF_8));
            bos.flush();
        }
    }

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
