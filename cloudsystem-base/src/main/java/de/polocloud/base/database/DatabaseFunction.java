package de.polocloud.base.database;

import java.sql.SQLException;

@FunctionalInterface
public interface DatabaseFunction<I, O> {

    O apply(I i) throws SQLException;
}

