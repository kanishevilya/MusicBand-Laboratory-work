package util;

import model.Coordinates;
import model.MusicBand;
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

        String frontManName = input.rawScan("Введите имя фронтмена (или Enter для пропуска)");
        if (frontManName != null && !frontManName.isEmpty()) {
            Person frontMan = PersonReader.read(input, "фронтмена", frontManName);
            if (frontMan != null) {
                band.setFrontMan(frontMan);
            }
        }

        return band;
    }
}
