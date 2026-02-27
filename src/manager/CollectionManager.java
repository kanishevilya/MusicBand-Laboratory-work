package manager;

import model.MusicBand;
import util.IdGenerator;

import java.time.ZonedDateTime;
import java.util.*;

public class CollectionManager {
    private TreeMap<Long, MusicBand> collection;
    private final ZonedDateTime initializationDate;

    private Long idCounter;

    public CollectionManager() {
        this.collection = new TreeMap<>();
        this.initializationDate = ZonedDateTime.now();
        this.idCounter = IdGenerator.nextId();
    }

    public long generateId() {
        long id;
        do {
            id = IdGenerator.nextId();
        } while (collection.containsKey(id)); // O(log n) вместо O(n) stream
        return id;
    }


    public void syncIdCounter() {
        collection.values().stream()
                .mapToLong(MusicBand::getId)
                .max()
                .ifPresent(IdGenerator::syncTo);
    }

    public void insert(MusicBand band) {
        collection.put(band.getId(), band);
    }

    public boolean updateById(long id, MusicBand newBand) {
        if (!collection.containsKey(id)) {
            return false;
        }

        MusicBand oldBand = collection.get(id);

        newBand.setId(id);
        newBand.setCreationDate(oldBand.getCreationDate());

        collection.put(id, newBand);
        return true;
    }

    public boolean removeByKey(Long key) {
        return collection.remove(key) != null;
    }

    public void clear() {
        collection.clear();
    }

    public int removeGreater(MusicBand band) {
        int before = collection.size();
        collection.entrySet().removeIf(e -> e.getValue().compareTo(band) > 0);
        return before - collection.size();
    }

    public boolean replaceIfGreater(Long key, MusicBand newBand) {
        MusicBand existing = collection.get(key);
        if (existing == null)
            return false;
        if (newBand.compareTo(existing) > 0) {
            newBand.setCreationDate(existing.getCreationDate());
            collection.put(key, newBand);
            return true;
        }
        return false;
    }

    public boolean replaceIfLower(Long key, MusicBand newBand) {
        MusicBand existing = collection.get(key);
        if (existing == null)
            return false;
        if (newBand.compareTo(existing) < 0) {
            newBand.setCreationDate(existing.getCreationDate());
            collection.put(key, newBand);
            return true;
        }
        return false;
    }

    public double averageOfAlbumsCount() {
        return collection.values().stream()
                .mapToInt(MusicBand::getAlbumsCount)
                .average()
                .orElse(0);
    }

    public List<MusicBand> filterByAlbumsCount(int albumsCount) {
        List<MusicBand> result = new ArrayList<>();
        for (MusicBand band : collection.values()) {
            if (band.getAlbumsCount() == albumsCount)
                result.add(band);
        }
        return result;
    }

    public List<Integer> getAlbumsCountDescending() {
        List<Integer> counts = new ArrayList<>();
        for (MusicBand band : collection.values()) {
            counts.add(band.getAlbumsCount());
        }
        counts.sort(Comparator.reverseOrder());
        return counts;
    }

    public MusicBand getById(long id) {
        return collection.get(id);
    }

    public boolean containsKey(Long key) {
        return collection.containsKey(key);
    }

    public TreeMap<Long, MusicBand> getCollection() {
        return collection;
    }

    public void setCollection(TreeMap<Long, MusicBand> collection) {
        this.collection = collection;
    }

    public ZonedDateTime getInitializationDate() {
        return initializationDate;
    }

    public int size() {
        return collection.size();
    }

    public boolean isEmpty() {
        return collection.isEmpty();
    }
}
