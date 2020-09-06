package model;

import java.nio.file.Paths;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public interface Utilities
{
	default FileChooser defaultDirectory(FileChooser fileChooser) 
	{
		fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(Paths.get(".").toAbsolutePath().toFile());
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Text Files", "*.txt"));
		return fileChooser;
	}
}
