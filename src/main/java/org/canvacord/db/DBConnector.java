package org.canvacord.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Optional;

public class DBConnector {

	public static Optional<Connection> connectToDB() {
		try {
			Connection connection = DriverManager.getConnection("jdbc:mysql://sql9.freemysqlhosting.net/sql9582292", "sql9582292", "IuATrw88Z1");
			return Optional.of(connection);
		}
		catch (Exception e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}

}
