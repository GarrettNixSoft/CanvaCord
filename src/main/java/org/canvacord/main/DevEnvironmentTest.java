package org.canvacord.main;

import org.canvacord.db.DBConnector;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Scanner;

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

			System.out.println("\nRecord this test timestamp in the database? (y/n)");
			Scanner input = new Scanner(System.in);
			String response = input.nextLine();

			if (response.toLowerCase().startsWith("y")) {

				System.out.println("Recording timestamp to database...");

				String sql = "INSERT INTO connections(timestamp) VALUES (?)";

				try {

					PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
					preparedStatement.setString(1, LocalDateTime.now().toString());

					if (preparedStatement.executeUpdate() == 1) {
						System.out.println("Success!");
					}
					else {
						System.out.println("Something went wrong.");
					}

				}
				catch (SQLException e) {
					e.printStackTrace();
				}

			}
			else {
				System.out.println("Test will not be recorded.");
			}


		});

	}

}
