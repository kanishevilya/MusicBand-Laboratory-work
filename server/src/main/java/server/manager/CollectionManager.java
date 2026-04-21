package server.manager;

import common.model.MusicBand;
import common.util.MusicBandOrdering;
import server.util.IdGenerator;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Управление коллекцией на сервере.
 */
public class CollectionManager {

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private TreeMap<Long, MusicBand> collection;
    private final ZonedDateTime initializationDate;

    public CollectionManager() {
        this.collection = new TreeMap<>();
        this.initializationDate = ZonedDateTime.now();
    }

    public long generateId() {
        while (true) {
            long newId = IdGenerator.nextId();
            boolean exists = collection.values().stream().anyMatch(b -> b.getId() == newId);
            if (!exists) {
                return newId;
            }
        }
    }

    public void syncIdCounter() {
        collection.values().stream()
                .mapToLong(MusicBand::getId)
                .max()
                .ifPresent(IdGenerator::syncTo);
    }

    public void insert(Long key, MusicBand band) {
        collection.put(key, band);
    }

    public boolean updateById(long id, MusicBand newBand) {
        for (Map.Entry<Long, MusicBand> entry : collection.entrySet()) {
            if (entry.getValue().getId() == id) {
                newBand.setId(id);
                newBand.setCreationDate(entry.getValue().getCreationDate());
                collection.put(entry.getKey(), newBand);
                return true;
            }
        }
        return false;
    }

    public boolean removeByKey(Long key) {
        return collection.remove(key) != null;
    }

    public void clear() {
        collection.clear();
    }

    public int removeGreater(MusicBand band) {
        System.out.println(band.toString());
        int before = collection.size();
        collection.entrySet().removeIf(e -> e.getValue().compareTo(band) > 0);
        return before - collection.size();
    }

    public boolean replaceIfGreater(Long key, MusicBand newBand, boolean isAutoId) {
        MusicBand existing = collection.get(key);
        if (existing == null) {
            return false;
        }
        if (newBand.compareTo(existing) > 0) {
            if (isAutoId) {
                newBand.setId(IdGenerator.nextId());
            } else {
                newBand.setId(existing.getId());
            }
            newBand.setCreationDate(existing.getCreationDate());
            collection.put(key, newBand);
            return true;
        }
        return false;
    }

    public boolean replaceIfLower(Long key, MusicBand newBand, boolean isAutoId) {
        MusicBand existing = collection.get(key);
        if (existing == null) {
            return false;
        }
        if (newBand.compareTo(existing) < 0) {
            if (isAutoId) {
                newBand.setId(IdGenerator.nextId());
            } else {
                newBand.setId(existing.getId());
            }
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
        return collection.values().stream()
                .filter(b -> b.getAlbumsCount() == albumsCount)
                .sorted(MusicBandOrdering.BY_NAME_THEN_ID)
                .collect(Collectors.toList());
    }

    public List<Integer> getAlbumsCountDescending() {
        return collection.values().stream()
                .map(MusicBand::getAlbumsCount)
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    public Stream<Map.Entry<Long, MusicBand>> bandsSortedForClient() {
        return collection.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(MusicBandOrdering.BY_NAME_THEN_ID));
    }

    public MusicBand getById(long id) {
        return collection.values().stream().filter(b -> b.getId() == id).findFirst().orElse(null);
    }

    public MusicBand getByKey(Long key) {
        return collection.get(key);
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
