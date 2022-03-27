package de.polocloud.database.sql.h2;

import de.polocloud.database.sql.SQLDatabaseProvider;

import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class H2DatabaseProvider extends SQLDatabaseProvider {

    public H2DatabaseProvider() throws SQLException {
        super(DriverManager.getConnection("jdbc:h2:" + new File("storage/database").toPath().toAbsolutePath()));
    }

}
