package util;

import java.math.BigDecimal;
import model.Coordinates;
import model.Currency;
import model.MusicBand;
import model.MusicGenre;
import model.Person;

/**
 * Читает музыкальную группу
 */
public class MusicBandReader {

    private MusicBandReader() {
    }

    /**
     * Читает музыкальную группу
     * 
     * @param input обработчик ввода
     * @return музыкальная группа
     */
    public static MusicBand read(InputHandler input) {
        MusicBand band = new MusicBand();

        band.setName(input.readRequiredString("Введите название группы"));

        double x = input.readDouble("Введите координату X");
        Float y = input.readFloatLessThanMax("Введите координату Y (максимум 968)", 968f);
        band.setCoordinates(new Coordinates(x, y));

        band.setNumberOfParticipants(
                input.readPositiveIntOrNull("Введите количество участников (или Enter для пропуска)"));

        band.setAlbumsCount(
                input.readRequiredPositiveInt("Введите количество альбомов"));

        band.setGenre(input.readMusicGenreOrNull("Введите жанр"));

        BigDecimal price = input.readRequiredPositiveBigDecimal("Введите стоимость (у.е.)");
        Currency currency = input.readCurrency("Введите валюту");
        band.setPrice(price);
        band.setCurrency(currency);

        String frontManName = input.rawScan("Введите имя фронтмена (или Enter для пропуска)");
        if (frontManName != null && !frontManName.isEmpty()) {
            Person frontMan = PersonReader.read(input, "фронтмена", frontManName);
            if (frontMan != null) {
                band.setFrontMan(frontMan);
            }
        }

        return band;
    }

    public static MusicBand readWithNameCheck(InputHandler input, MusicBand current, boolean checkLower) {
        MusicBand band = new MusicBand();

        String name = input.readRequiredString("Введите название группы");

        if (checkLower && name.compareToIgnoreCase(current.getName()) >= 0) {
            System.out.println("Название не меньше текущего. Ввод прерван.");
            return null;
        } else if (!checkLower && name.compareToIgnoreCase(current.getName()) <= 0) {
            System.out.println("Название не больше текущего. Ввод прерван.");
            return null;
        }

        band.setName(name);

        double x = input.readDouble("Введите координату X");
        Float y = input.readFloatLessThanMax("Введите координату Y (максимум 968)", 968f);
        band.setCoordinates(new Coordinates(x, y));

        band.setNumberOfParticipants(
                input.readPositiveIntOrNull("Введите количество участников (или Enter для пропуска)"));

        band.setAlbumsCount(
                input.readRequiredPositiveInt("Введите количество альбомов"));

        band.setGenre(input.readMusicGenreOrNull("Введите жанр"));

        BigDecimal price = input.readRequiredPositiveBigDecimal("Введите стоимость (у.е.)");
        Currency currency = input.readCurrency("Введите валюту");
        band.setPrice(price);
        band.setCurrency(currency);

        String frontManName = input.rawScan("Введите имя фронтмена (или Enter для пропуска)");
        if (frontManName != null && !frontManName.isEmpty()) {
            Person frontMan = PersonReader.read(input, "фронтмена", frontManName);
            if (frontMan != null) {
                band.setFrontMan(frontMan);
            }
        }

        return band;
    }

    public static MusicBand readForUpdate(InputHandler input, MusicBand current) {
        MusicBand band = new MusicBand();

        String name = input.rawScan("Название группы [" + current.getName() + "]");
        band.setName((name == null || name.isEmpty()) ? current.getName() : name);

        String xStr = input.rawScan("Координата X [" + current.getCoordinates().getX() + "]");
        double x = (xStr == null || xStr.isEmpty()) ? current.getCoordinates().getX() : Double.parseDouble(xStr);

        String yStr = input.rawScan("Координата Y [" + current.getCoordinates().getY() + "]");
        Float y = (yStr == null || yStr.isEmpty()) ? current.getCoordinates().getY() : Float.parseFloat(yStr);

        band.setCoordinates(new Coordinates(x, y));

        String participantsStr = input.rawScan("Количество участников [" +
                (current.getNumberOfParticipants() == null ? "не указано" : current.getNumberOfParticipants()) + "] (введите 'null' для соответ. значения)");
        Integer participants;
        if (participantsStr == null || participantsStr.isEmpty()) {
            participants = current.getNumberOfParticipants();
        } else if (participantsStr.equalsIgnoreCase("null")) {
            if (confirmNull(input)) participants = null;
            else participants = current.getNumberOfParticipants();
        } else {
            try {
                int p = Integer.parseInt(participantsStr);
                if (p <= 0) {
                    System.out.println("Количество участников должно быть > 0. Оставлено прежнее значение.");
                    participants = current.getNumberOfParticipants();
                } else participants = p;
            } catch (NumberFormatException e) {
                System.out.println("Неверный формат числа. Оставлено прежнее значение.");
                participants = current.getNumberOfParticipants();
            }
        }
        band.setNumberOfParticipants(participants);

        String albumsStr = input.rawScan("Количество альбомов [" + current.getAlbumsCount() + "]");
        if (albumsStr == null || albumsStr.isEmpty()) {
            band.setAlbumsCount(current.getAlbumsCount());
        } else {
            try {
                int a = Integer.parseInt(albumsStr);
                if (a <= 0) {
                    throw new IllegalArgumentException("Количество альбомов должно быть > 0");
                }
                band.setAlbumsCount(a);
            } catch (Exception e) {
                System.out.println("Неверное значение для количества альбомов. Оставлено прежнее значение.");
                band.setAlbumsCount(current.getAlbumsCount());
            }
        }

        String genreStr = input.rawScan("Жанр [" + (current.getGenre() == null ? "не указан" : current.getGenre()) + "] (введите 'null' для соответ. значения)");
        if (genreStr == null || genreStr.isEmpty()) {
            band.setGenre(current.getGenre());
        } else if (genreStr.equalsIgnoreCase("null")) {
            if (confirmNull(input)) band.setGenre(null);
            else band.setGenre(current.getGenre());
        } else {
            try {
                band.setGenre(MusicGenre.valueOf(genreStr.toUpperCase()));
            } catch (IllegalArgumentException e) {
                System.out.println("Неизвестный жанр. Оставлено прежнее значение.");
                band.setGenre(current.getGenre());
            }
        }

        String priceStr = input.rawScan("Стоимость [" + current.getPrice() + "]");
        if (priceStr == null || priceStr.isEmpty()) {
            band.setPrice(current.getPrice());
        } else {
            priceStr = priceStr.replace(',', '.');
            try {
                BigDecimal price = new BigDecimal(priceStr);
                if (price.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("Стоимость должна быть > 0");
                }
                band.setPrice(price);
            } catch (Exception e) {
                System.out.println("Неверное значение стоимости. Оставлено прежнее значение.");
                band.setPrice(current.getPrice());
            }
        }

        String currencyStr = input.rawScan("Валюта [" + current.getCurrency() + "]");
        if (currencyStr == null || currencyStr.isEmpty()) {
            band.setCurrency(current.getCurrency());
        } else {
            try {
                try {
                    int idx = Integer.parseInt(currencyStr) - 1;
                    band.setCurrency(Currency.fromOrdinal(idx));
                } catch (NumberFormatException ignored) {
                    band.setCurrency(Currency.valueOf(currencyStr.toUpperCase()));
                }
            } catch (Exception e) {
                System.out.println("Неверная валюта. Оставлено прежнее значение.");
                band.setCurrency(current.getCurrency());
            }
        }

        String frontManName = input.rawScan("Имя фронтмена [" +
                (current.getFrontMan() == null ? "не указано" : current.getFrontMan().getName()) + "] (введите 'null' для соответ. значения, или повторите значение для обновления полей пользователя)");
        if (frontManName == null || frontManName.isEmpty()) {
            band.setFrontMan(current.getFrontMan());
        } else if (frontManName.equalsIgnoreCase("null")) {
            if (confirmNull(input)) band.setFrontMan(null);
            else band.setFrontMan(current.getFrontMan());
        } else {
            Person frontMan = PersonReader.readForUpdate(input, current.getFrontMan(), frontManName);
            if (frontMan != null) {
                band.setFrontMan(frontMan);
            } else {
                band.setFrontMan(current.getFrontMan());
            }
        }

        return band;
    }
    private static boolean confirmNull(InputHandler input) {
        return InputHandler.confirmNull(input);
    }

}
