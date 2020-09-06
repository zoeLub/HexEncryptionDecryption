package view;

import model.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.ResourceBundle;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;

/**
 * 
 * @author Zoe Lubanza
 *
 */

public class ApplicationUIcontroller implements Initializable, Utilities
{
	@FXML private AnchorPane pane;
	@FXML private Button uploadBtn;
	@FXML private TextField pathTxtField;
	@FXML private TextArea contentTextArea;
	@FXML private ToggleGroup tGroup;
	@FXML private RadioButton hexToAscii;
	@FXML private RadioButton asciiToHex;
	@FXML private Label percentage;
	@FXML private Label loadingMsg;
	@FXML private ProgressBar progressBar;
	private int min;	//the minimum percentage value for the progressBar
	private int max;	//the maximum percentage value for the progressBar
	private int percent; //the actual percentage
	private FileChooser fileChooser;
	private File file;
	private String filePath;	// path of file selected
	private StringBuilder originalText; //store the text from unkown.txt
	private StringBuilder convertedText;	//the ASCII text containing "South Africa"
	private StringBuilder hexadecimalText;	//The hexadecimal text with 32 byte places
	private HexConvertion hex;	//model
	
	/**
	 * This method handles the action when
	 * the UPLOAD button is clicked
	 * @param evt
	 * @throws IOException
	 */
	@FXML public void uploadBtnEvent(ActionEvent evt) throws IOException
	{
		openFileChooserAndGetPath();
		
		if(filePath!=null) {
			writeToTextField(filePath);
			extractText(filePath);}
	}
	
	/**
	 * This method decide which action to perform
	 * when the RadioButton are selected
	 * @param evt
	 */
	@FXML private void decideAction(ActionEvent evt)
	{
		try {
			if(hexToAscii.isSelected())hexToAscii();
			else if(asciiToHex.isSelected())asciiToHex();}
		catch(Exception e) {e.printStackTrace();}		
	}
	
	/**
	 * This method is called when ASCCI-TO-HEX 
	 * RadioButton is selected
	 * @throws IOException 
	 */
	private void asciiToHex() throws IOException
	{
		if(canConvertAscci()){			
			intializeCounters();
			originalText.setLength(0);	//clears the content of originalText
			convertToHex(convertedText);
			get32Bytes(hex.getHexText());
			writeToTxtArea(hexadecimalText);
			writeToFile(hexadecimalText);
			convertedText.setLength(0);}
		else showAlert("LOAD A FILE FIRST, AND CONVERT TO ASCII");
	}
			
	/** 
	 * @param text
	 */
	private void convertToHex(StringBuilder text)
	{
		hex.getHexText().setLength(0);
		
		Arrays.stream(text.toString().split("\n"))
		.forEach(line -> {
			try {
				hex.asciiToHex(line+"\n");}
			catch (Exception e) {e.printStackTrace();}
		});
	}
		
	/**
	 * This method is called when HEX-TO-ASCCI 
	 * RadioButton is selected
	 * @throws Exception
	 */
	private void hexToAscii() throws Exception
	{
		if(canConvertHex()) {
			intializeCounters();
			convertToAscii(originalText);
			extractInformation(hex.getAsciiText());
			writeToTxtArea(convertedText);
			}
		else showAlert("PLEASE LOAD A FILE FIRST.");
	}
			
	/**
	 * This method take the Hex text and splits it into
	 * lines and passes each line to the method responsible
	 * for converting the Hex text to ASCII
	 * @param originalText
	 */
	private void convertToAscii(StringBuilder originalText)
	{
		convertedText = new StringBuilder();		
		try {
			Arrays.stream(originalText.toString().split("\n"))
			.forEach(line -> hex.hexToAscii(line));}
		catch (Exception e) {showAlert("THE SELECTED FILE IS NOT A HEX FILE");}
	}
	
	/**
	 * This method grabs the lines containing "South Africa" 
	 * @param asciiText
	 */
	private void extractInformation(StringBuilder asciiText)
	{
		Arrays.stream(asciiText.toString().split("\n"))
		.filter(line -> line.contains("South Africa"))
		.forEach(line -> {convertedText.append(line+"\n"); max++;});
	}

	/**
	 * This method displays the text on the TextArea
	 * @param filePath
	 * @throws IOException
	 */
	private void writeToTxtArea(StringBuilder text)
	{
		showNotification();
		initializeTextArea();
		contentTextArea.setText("");	//clear the content of the TextArea
				
		/*
		 * this task is responsible for appending the information on the TextArea
		 * and updating the progress of the work being done.
		 * It helps the program not to freeze when appending on the TextArea
		 */
		
		Task<Void> task = new Task<>() {		  
			@Override public Void call() throws Exception {
		  
		  Arrays.stream(text.toString().split("\n")) 
		  .forEach(line -> {				
			  		contentTextArea.appendText(line+"\n");		  
			  		min++; 			  		
			  		percent = (min*100)/max; 
			  		updateMessage((percent) + "%");
			  		updateProgress(min, max); 
			  	});		  
		  			return null;
		  		} 
		  };
		  
		  task.setOnSucceeded(event -> {
			  hideNotification();
			  addTextArea();
		  });
		  
		  progressBar.progressProperty().bind(task.progressProperty());
		  percentage.textProperty().bind(task.messageProperty());
		  
		  Thread thread = new Thread(task); 
		  thread.setDaemon(true); 
		  thread.start();
	}

	/**
	 * This method places 32 hex bytes per row
	 * @param hexText
	 */
	private void get32Bytes(StringBuilder hexText)
	{
		//Sorry sir I couldn't figure out a simple way of achieving this with functional expression.
		
		int i=0;
		int j=0;
		int step = 32;
		hexadecimalText = new StringBuilder();
		char[] str = hexText.toString().toCharArray();
		
		for(i=i; i<str.length; i++) {
			j=i;
			for(j=j; j<step; j++)
			{
				hexadecimalText.append(str[j]);
				
				
				if(j==str.length-1) {
					i = str.length;
					break;}
				
				else if(j==step-1)
				{
					i = j;
					step = step+32;
					hexadecimalText.append("\n");
					max++;
					break;					
				}
			}
		}
	}
	
	/**
	 * 
	 * @param hexText
	 * @throws IOException
	 */
	private void writeToFile(StringBuilder hexText) throws IOException
	{	
		Files.write(Paths.get("result.txt"), Arrays.asList(hexText.toString()), Charset.defaultCharset());
	}	
	
	/**
	 * this method hides the progressBar and the labels
	 * showing the loading message
	 */
	private void hideNotification()
	{
		progressBar.setVisible(false);
		loadingMsg.setVisible(false);
		percentage.setVisible(false);
	}
	
	/**
	 * this method is responsible for displaying the notification
	 * showing the progress of the conversion
	 */
	private void showNotification()
	{
		progressBar.setVisible(true);
		loadingMsg.setVisible(true);
		percentage.setVisible(true);
	}
	
	/** 
	 * @param path
	 * @return a stringBuilder containing the extracted text from the .txt file
	 * @throws Exception
	 */
	private StringBuilder extractText(String path)
	{	
		try{
			originalText = new StringBuilder();
			
			Files.lines(Paths.get(path))
			.forEach(line -> originalText.append(line+"\n"));} 
		catch (Exception e) {e.printStackTrace();}
		
		return originalText;
	}
	
	/**
	 * this method displays the file path on the TextField
	 * @param filePath
	 */
	private void writeToTextField(String filePath)
	{
		pathTxtField.setText(filePath);
	}

	/**
	 * 
	 */
	private void openFileChooserAndGetPath()throws IOException
	{
		fileChooser = defaultDirectory(fileChooser);
		
		file = null;
		file = fileChooser.showOpenDialog(null);
		
		if(file != null)filePath = file.getAbsolutePath();
		else filePath = null;
	}
		
	/**  
	 * @return
	 */
	private boolean canConvertHex()
	{
		return convertedText != null||filePath != null?true:false;
	}
	
	/**
	 * This method checks if the text has been
	 * converted from Hex-to-ASCII
	 * @return
	 */
	private boolean canConvertAscci()
	{
		return convertedText != null?true:false; 
	}
		
	/** 
	 * @param msg
	 */
	private void showAlert(String msg)
	{
		Alert alert = new Alert(AlertType.ERROR);
		alert.setContentText(msg);
		alert.setTitle("Error Message");
		alert.show();
	}

	/**
	 * initialize the default settings
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) 
	{
		hex= new HexConvertion();
		hexToAscii.setToggleGroup(tGroup);
		asciiToHex.setToggleGroup(tGroup);
	}
	
	/**
	 * This method creates a brand new TextArea for
	 */
	private void initializeTextArea()
	{
		contentTextArea = new TextArea();
		contentTextArea.setPrefSize(495, 304);
		contentTextArea.setLayoutX(5);
		contentTextArea.setLayoutY(145);
		contentTextArea.setEditable(false);
	}
	
	/**
	 * This method resets the counters
	 */
	private void intializeCounters()
	{
		min = 0;
		max = 0;
		percent = 0;
	}
		
	/**
	 * This method ads the contentTextArea into the Pane
	 */
	private void addTextArea()
	{
		pane.getChildren().add(contentTextArea);
		AnchorPane.setTopAnchor(contentTextArea, 145.0);
		AnchorPane.setBottomAnchor(contentTextArea, 135.0);
		AnchorPane.setLeftAnchor(contentTextArea, 5.0);
		AnchorPane.setRightAnchor(contentTextArea, 5.0);
	}

}	