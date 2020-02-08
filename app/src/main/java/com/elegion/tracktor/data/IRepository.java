package com.elegion.tracktor.data;

import com.elegion.tracktor.data.model.Track;

import java.util.List;

import io.realm.OrderedRealmCollection;

/**
 * @author Azret Magometov
 */
public interface IRepository<T> {

    T getItem(long id);

    List<T> getAll();

    long insertItem(T t);

    boolean deleteItem(long id);

    void updateItem(T t);

    OrderedRealmCollection<Track> getItemsList();

    void clearRepository();

    void insertAll(List<Track> tracks);

}
