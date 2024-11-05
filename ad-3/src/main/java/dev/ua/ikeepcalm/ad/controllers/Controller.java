package dev.ua.ikeepcalm.ad.controllers;

import dev.ua.ikeepcalm.ad.entities.User;
import dev.ua.ikeepcalm.ad.storage.BTree;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class Controller {

    @FXML
    public MenuItem aboutBTree;
    @FXML
    public MenuItem generateValues;

    BTree bTree = new BTree(25);

    @FXML
    public Button searchButton;

    @FXML
    public TextField searchField;

    @FXML
    public Button refreshButton;

    @FXML
    public Button saveButton;

    @FXML
    public Button deleteButton;

    @FXML
    public TableView<User> tableView;
    @FXML
    public TableColumn<User, Integer> idColumn;
    @FXML
    private TableColumn<User, String> emailColumn;
    @FXML
    private TableColumn<User, String> usernameColumn;
    @FXML
    private TableColumn<User, String> passwordColumn;

    @FXML
    private TextField emailField;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;

    @FXML
    private Pagination pagination;

    private int userId = 0;
    private final int rowsPerPage = 6;

    public void initialize() {
        deleteButton.setVisible(false);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));

        generateValues.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog("10");
            dialog.setTitle("Generate Values");
            dialog.setHeaderText("Generate Random Users");
            dialog.setContentText("Please enter the number of users to generate:");

            dialog.showAndWait().ifPresent(number -> {
                try {
                    int count = Integer.parseInt(number);
                    for (int i = 0; i < count; i++) {
                        User newUser = new User("email" + i + "@example.com", "user" + i, "password" + i);
                        bTree.insert(newUser);
                    }
                    updatePagination();
                } catch (NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid Input");
                    alert.setHeaderText("Invalid Number");
                    alert.setContentText("Please enter a valid number.");
                    alert.showAndWait();
                }
            });
        });

        aboutBTree.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("About B-Tree");
            alert.setHeaderText("B-Tree");
            alert.setContentText("A B-tree is a self-balancing tree data structure that maintains sorted data and allows searches, sequential access, insertions, and deletions in logarithmic time. The B-tree generalizes the binary search tree, allowing for nodes with more than two children. Unlike self-balancing binary search trees, the B-tree is well suited for storage systems that read and write relatively large blocks of data, such as disks. It is commonly used in databases and file systems.");
            alert.showAndWait();
        });

        searchButton.setOnAction(event -> {
            String search = searchField.getText();
            if (!search.isEmpty()) {
                int id = 0;
                try {
                    id = Integer.parseInt(search);
                } catch (NumberFormatException e) {
                    searchField.setStyle("-fx-border-color: red;");
                }

                User user = bTree.search(id);
                int comparisonCount = bTree.getComparisonCount();
                if (comparisonCount > 0) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Search result");
                    alert.setHeaderText("Search result");
                    alert.setContentText("Comparison count: " + comparisonCount + "\n" + (user != null ? user.toString() : "User not found"));
                    alert.showAndWait();
                    searchField.setStyle("");
                }
                if (user != null) {
                    tableView.getItems().setAll(user);
                } else {
                    tableView.getItems().clear();
                }
            }
        });

        refreshButton.setOnAction(event -> {
            searchField.clear();
            searchField.setStyle("");
            updatePagination();
        });

        tableView.setPlaceholder(new Label("No data to display"));
        tableView.setOnMouseClicked(event -> {
            User user = tableView.getSelectionModel().getSelectedItem();
            if (user != null) {
                userId = user.getId();
                emailField.setText(user.getEmail());
                usernameField.setText(user.getUsername());
                passwordField.setText(user.getPassword());
                deleteButton.setVisible(true);
            }

        });

        saveButton.setOnAction(event -> {
            String email = emailField.getText();
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (validateFields(email, username, password)) {
                if (userId != 0) {
                    bTree.update(new User(userId, email, username, password));
                    deleteButton.setVisible(false);
                    updatePagination();
                    clearFields();
                    userId = 0;
                } else {
                    User newUser = new User(email, username, password);
                    bTree.insert(newUser);
                    updatePagination();
                    clearFields();
                }
            }
        });

        deleteButton.setOnAction(event -> {
            bTree.delete(userId);
            updatePagination();
            clearFields();
            userId = 0;
            deleteButton.setVisible(false);
        });

        pagination.setPageFactory(this::createPage);

        updatePagination();
    }

    private boolean validateFields(String email, String username, String password) {
        boolean valid = true;
        if (email.isEmpty()) {
            emailField.setStyle("-fx-border-color: red;");
            valid = false;
        } else {
            emailField.setStyle("");
        }
        if (username.isEmpty()) {
            usernameField.setStyle("-fx-border-color: red;");
            valid = false;
        } else {
            usernameField.setStyle("");
        }
        if (password.isEmpty()) {
            passwordField.setStyle("-fx-border-color: red;");
            valid = false;
        } else {
            passwordField.setStyle("");
        }
        return valid;
    }

    private void clearFields() {
        emailField.clear();
        usernameField.clear();
        passwordField.clear();
    }

    private void updatePagination() {
        int userCount = bTree.getUserCount();
        int pageCount = (int) Math.ceil((double) userCount / rowsPerPage);
        pagination.setPageCount(pageCount);
        updateTableView(0);
    }

    private void updateTableView(int pageIndex) {
        List<User> users = bTree.getUsersForPage(pageIndex, rowsPerPage);
        if (users.isEmpty()) {
            tableView.getItems().clear();
            return;
        }
        tableView.getItems().setAll(users);
    }

    private TableView<User> createPage(int pageIndex) {
        updateTableView(pageIndex);
        return tableView;
    }
}