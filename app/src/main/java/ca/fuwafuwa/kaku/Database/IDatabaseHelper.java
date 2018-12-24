package ca.fuwafuwa.kaku.Database;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

public interface IDatabaseHelper
{
    <T> Dao<T, Integer> getDbDao(Class clazz) throws SQLException;
}
