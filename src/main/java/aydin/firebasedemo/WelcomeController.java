package aydin.firebasedemo;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.util.concurrent.ExecutionException;
import java.lang.InterruptedException;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WelcomeController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    // Register button
    @FXML
    private void handleRegister(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Email and password are required.");
            return;
        }

        try {
            //Create user in Firebase Auth
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(email)
                    .setEmailVerified(false)
                    .setPassword(password);

            UserRecord userRecord = DemoApp.fauth.createUser(request);

            //store credentials
            Map<String, Object> data = new HashMap<>();
            data.put("email", email);
            data.put("password", password);

            DemoApp.fstore.collection("Users")
                    .document(userRecord.getUid())
                    .set(data);

            messageLabel.setText("Registration successful! You can sign in now.");

        } catch (FirebaseAuthException e) {
            e.printStackTrace();
            messageLabel.setText("Auth error: " + e.getMessage());
        }
    }

    // Sign In
    @FXML
    private void handleSignIn(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Email and password are required.");
            return;
        }

        try {
            ApiFuture<QuerySnapshot> future = DemoApp.fstore.collection("Users")
                    .whereEqualTo("email", email)
                    .whereEqualTo("password", password)
                    .get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            if (!documents.isEmpty()) {
                messageLabel.setText("");
                try {
                    DemoApp.setRoot("primary");
                } catch (java.io.IOException ex) {
                    ex.printStackTrace();
                    messageLabel.setText("Error loading main screen.");
                }
            } else {
                messageLabel.setText("Invalid credentials.");
            }

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            messageLabel.setText("Error: " + e.getMessage());
        }
    }
}