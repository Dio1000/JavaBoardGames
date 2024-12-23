package me.dariansandru.dbms;

import me.dariansandru.domain.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class DBUpdater {
    public static void insertPlayer(String username, String email, String password, int rating) {
        String insertSQL = """
            INSERT INTO players (username, email, password, rating) 
            VALUES (?, ?, ?, ?);
        """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, password);
            preparedStatement.setInt(4, rating);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
        }
    }

    public static void updateRating(String username, int newRating) {
        String updateSQL = "UPDATE players SET rating = ? WHERE username = ?;";

        try(
            Connection connection = DBConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(updateSQL);){

            preparedStatement.setInt(1, newRating);
            preparedStatement.setString(2, username);
            preparedStatement.executeUpdate();

        } catch(SQLException e){
            System.err.println(e.getMessage());
        }


    }

    public static void insertAdmin(String username, String email, String password, List<String> permissions) {
        String insertSQL = """
            INSERT INTO admins (username, email, password) 
            VALUES (?, ?, ?);
        """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, password);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
        }
    }

    public static void insertPermissions(int admin_id, List<String> permissions) {
        if (permissions.isEmpty()) return;

        for (String permission : permissions) {
            String insertSQL = """
            INSERT INTO admin_permissions (admin_id, permission_name) 
            VALUES (?, ?);
        """;

            try (Connection connection = DBConnection.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {

                preparedStatement.setInt(1, admin_id);
                preparedStatement.setString(2, permission);

                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                System.err.println("SQL Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static void insertReport(int admin_id, int player_id, String report){
        if (report.isEmpty()) return;

        String insertSQL = "INSERT INTO reports (report, admin_id, player_id)" +
                "VALUES (?, ?, ?);";

        try (Connection connection = DBConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);
            ){
            preparedStatement.setString(1, report);
            preparedStatement.setInt(2, admin_id);
            preparedStatement.setInt(3, player_id);

        }catch (SQLException e){
            System.err.println("SQL Error: " + e.getMessage());
        }
    }

    public static void deletePlayer(String username) {
        String deleteSQL = "DELETE FROM players WHERE username = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {

            preparedStatement.setString(1, username);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
