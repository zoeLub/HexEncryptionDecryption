package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * 
 * @author Zoe Lubanza
 *
 */

public class Main extends Application {
	@Override
	public void start(Stage myStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/view/ApplicationUI.fxml"));
		Scene scene = new Scene(root);
		myStage.setScene(scene);
		myStage.show();		
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
