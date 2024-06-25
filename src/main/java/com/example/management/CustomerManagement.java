package com.example.management;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CustomerManagement {
    private final String DATABASE_URL = "jdbc:sqlite:store.db";
    private Connection conn;
    private ObservableList<Customer> customers;
    private TableView<Customer> tableView;

    public CustomerManagement() {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(DATABASE_URL);
            setupDatabase();
            customers = FXCollections.observableArrayList();
            loadCustomers();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public VBox getView() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(8);

        Button addButton = new Button("Add Customer");
        addButton.setOnAction(e -> showAddCustomerDialog());

        tableView = new TableView<>();
        TableColumn<Customer, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Customer, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Customer, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        tableView.getColumns().add(idColumn);
        tableView.getColumns().add(nameColumn);
        tableView.getColumns().add(emailColumn);

        tableView.setItems(customers);

        vbox.getChildren().addAll(addButton, tableView);
        return vbox;
    }

    private void setupDatabase() {
        String createCustomersTable = "CREATE TABLE IF NOT EXISTS customers ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT NOT NULL,"
                + "email TEXT NOT NULL);";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createCustomersTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadCustomers() {
        String sql = "SELECT * FROM customers";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Customer customer = new Customer(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email")
                );
                customers.add(customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAddCustomerDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Add Customer");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);

        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();
        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();

        Button addButton = new Button("Add");
        addButton.setOnAction(e -> {
            addCustomer(nameField.getText(), emailField.getText());
            dialog.close();
        });

        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(emailLabel, 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(addButton, 1, 2);

        Scene scene = new Scene(grid, 300, 200);
        dialog.setScene(scene);
        dialog.show();
    }

    private void addCustomer(String name, String email) {
        String sql = "INSERT INTO customers(name, email) VALUES(?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.executeUpdate();
            System.out.println("Customer added successfully.");

            // Add the customer to the ObservableList
            String getLastInsertedId = "SELECT last_insert_rowid()";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(getLastInsertedId)) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    Customer customer = new Customer(id, name, email);
                    customers.add(customer);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
