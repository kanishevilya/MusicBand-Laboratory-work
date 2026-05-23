package server.manager;

import common.model.MusicBand;
import common.util.MusicBandOrdering;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.util.IdGenerator;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock; // Наш замок по ТЗ
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Управление коллекцией на сервере с поддержкой многопоточности через ReentrantLock.
 */
public class CollectionManager {

    private static final Logger log = LogManager.getLogger(CollectionManager.class);
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private TreeMap<Long, MusicBand> collection;
    private final ZonedDateTime initializationDate;

    private final ReentrantLock lock = new ReentrantLock();

    public CollectionManager() {
        this.collection = new TreeMap<>();
        this.initializationDate = ZonedDateTime.now();
    }

    public ReentrantLock getLock() {
        return lock;
    }

    public long generateId() {
        lock.lock();
        try {
            while (true) {
                long newId = IdGenerator.nextId();
                boolean exists = collection.values().stream().anyMatch(b -> b.getId() == newId);
                if (!exists) {
                    return newId;
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public void syncIdCounter() {
        lock.lock();
        try {
            collection.values().stream()
                    .mapToLong(MusicBand::getId)
                    .max()
                    .ifPresent(IdGenerator::syncTo);
        } finally {
            lock.unlock();
        }
    }

    public void insert(Long key, MusicBand band) {
        lock.lock();
        try {
            collection.put(key, band);
        } finally {
            lock.unlock();
        }
    }

    public boolean updateById(long id, MusicBand newBand) {
        lock.lock();
        try {
            for (Map.Entry<Long, MusicBand> entry : collection.entrySet()) {
                if (entry.getValue().getId() == id) {
                    newBand.setId(id);
                    newBand.setCreationDate(entry.getValue().getCreationDate());
                    collection.put(entry.getKey(), newBand);
                    return true;
                }
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    public boolean removeByKey(Long key) {
        lock.lock();
        try {
            return collection.remove(key) != null;
        } finally {
            lock.unlock();
        }
    }

    public void clear() {
        lock.lock();
        try {
            collection.clear();
        } finally {
            lock.unlock();
        }
    }

    public int removeGreater(MusicBand band) {
        lock.lock();
        try {
            log.debug("removeGreater вызван для: {}", band);
            int before = collection.size();
            collection.entrySet().removeIf(e -> e.getValue().compareTo(band) > 0);
            return before - collection.size();
        } finally {
            lock.unlock();
        }
    }

    public boolean replaceIfGreater(Long key, MusicBand newBand, boolean isAutoId) {
        lock.lock();
        try {
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
        } finally {
            lock.unlock();
        }
    }

    public boolean replaceIfLower(Long key, MusicBand newBand, boolean isAutoId) {
        lock.lock();
        try {
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
        } finally {
            lock.unlock();
        }
    }

    public double averageOfAlbumsCount() {
        lock.lock();
        try {
            return collection.values().stream()
                    .mapToInt(MusicBand::getAlbumsCount)
                    .average()
                    .orElse(0);
        } finally {
            lock.unlock();
        }
    }

    public List<MusicBand> filterByAlbumsCount(int albumsCount) {
        lock.lock();
        try {
            return collection.values().stream()
                    .filter(b -> b.getAlbumsCount() == albumsCount)
                    .sorted(MusicBandOrdering.BY_NAME_THEN_ID)
                    .collect(Collectors.toList());
        } finally {
            lock.unlock();
        }
    }

    public List<Integer> getAlbumsCountDescending() {
        lock.lock();
        try {
            return collection.values().stream()
                    .map(MusicBand::getAlbumsCount)
                    .sorted(Comparator.reverseOrder())
                    .collect(Collectors.toList());
        } finally {
            lock.unlock();
        }
    }


    public TreeMap<Long, MusicBand> bandsSortedForClient() {
        lock.lock();
        try {
            return collection.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(MusicBandOrdering.BY_NAME_THEN_ID))
                    .collect(TreeMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), Map::putAll);
        } finally {
            lock.unlock();
        }
    }

    public MusicBand getById(long id) {
        lock.lock();
        try {
            return collection.values().stream().filter(b -> b.getId() == id).findFirst().orElse(null);
        } finally {
            lock.unlock();
        }
    }

    public MusicBand getByKey(Long key) {
        lock.lock();
        try {
            return collection.get(key);
        } finally {
            lock.unlock();
        }
    }

    public boolean containsKey(Long key) {
        lock.lock();
        try {
            return collection.containsKey(key);
        } finally {
            lock.unlock();
        }
    }

    public TreeMap<Long, MusicBand> getCollection() {
        return collection;
    }

    public void setCollection(TreeMap<Long, MusicBand> collection) {
        lock.lock();
        try {
            this.collection = collection;
        } finally {
            lock.unlock();
        }
    }

    public ZonedDateTime getInitializationDate() {
        return initializationDate;
    }

    public int size() {
        lock.lock();
        try {
            return collection.size();
        } finally {
            lock.unlock();
        }
    }

    public boolean isEmpty() {
        lock.lock();
        try {
            return collection.isEmpty();
        } finally {
            lock.unlock();
        }
    }
}