package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.BaseModel;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class BaseStorage<T extends BaseModel> {
    private final Map<Long, T> map = new HashMap<>();

    public Optional<T> save(T obj) {
        if (map.containsKey(obj.getId())) {
            map.replace(obj.getId(), obj);
            return Optional.ofNullable(map.get(obj.getId()));
        }

        long maxId = map.keySet().stream().mapToLong(id -> id).max().orElse(0);
        obj.setId(++maxId);
        map.put(obj.getId(), obj);
        return Optional.ofNullable(map.get(obj.getId()));
    }

    public Optional<T> remove(Long id) {
        T t = map.get(id);
        map.remove(id);
        return Optional.ofNullable(t);
    }

    public Collection<T> findAll() {
        return map.values();
    }

    public Optional<T> findOneById(Long id) {
        return Optional.ofNullable(map.get(id));
    }
}
