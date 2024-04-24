package fr.sio.chat.app.dao.interfaces;

import fr.sio.chat.app.exceptions.DataAccessException;

public interface IDao<T> {
    Integer insert(T t) throws DataAccessException;
}
