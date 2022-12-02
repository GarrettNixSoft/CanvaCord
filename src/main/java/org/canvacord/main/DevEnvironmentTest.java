package org.canvacord.main;

import org.canvacord.db.DBConnector;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class DevEnvironmentTest {

	public static void main(String[] args) {

		Optional<Connection> dbConnection = DBConnector.connectToDB();

		dbConnection.ifPresent(connection -> {

			System.out.println("Successfully connected to the database!");
			System.out.println("Checking stored regions (test):");

			try {

				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery("select * from regions");

				System.out.println("\nFound regions:\n");
				while (resultSet.next()) {
					System.out.println(resultSet.getString("name"));
				}

			}
			catch (SQLException e) {
				e.printStackTrace();
			}

		});

	}

}
