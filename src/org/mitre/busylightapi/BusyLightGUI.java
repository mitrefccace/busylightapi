package org.mitre.busylightapi;

import org.mitre.busylightapi.BusyLightAPI.Color;
import org.mitre.busylightapi.BusyLightAPI.Product;
import org.mitre.busylightapi.BusyLightAPI.Vendor;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class BusyLightGUI extends Application {

	private GridPane gridPane;
	private Text status, ontimeT, offtimeT;
	private ComboBox<String> cbVendor, cbProduct, cbColor;
	private Spinner<Double> ontime, offtime;
	private BusyLightAPI light;
	private RadioButton rbSolid, rbBlink, rbOn, rbOff;
	private ToggleGroup group, groupSound;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		light = new BusyLightAPI();
		
		gridPane = new GridPane();
		gridPane.setHgap(5);
		gridPane.setVgap(5);
		gridPane.setPadding(new Insets(10, 10, 10, 10));

		gridPane.add(new Text("Vendor:"),  0, 0);

		String[] vendors = new String[Vendor.values().length];
		for (int i=0; i < Vendor.values().length; i++) {
			vendors[i] = Vendor.values()[i].toString();
		}
		ObservableList<String> options = FXCollections.observableArrayList(vendors);
		cbVendor = new ComboBox<String>(options);
		cbVendor.getSelectionModel().selectFirst();
		gridPane.add(cbVendor ,  1, 0);

		gridPane.add(new Text("Product:"),  0, 1);

		String[] products = new String[Product.values().length];
		for (int i=0; i < Product.values().length; i++) {
			products[i] = Product.values()[i].toString();
		}
		options = FXCollections.observableArrayList(products);
		cbProduct = new ComboBox<String>(options);
		cbProduct.getSelectionModel().selectFirst();
		gridPane.add(cbProduct ,  1, 1);		

		gridPane.add(new Text("Color:"),  0, 2);
		String[] colors = new String[Color.values().length];
		for (int i=0; i < Color.values().length; i++) {
			colors[i] = Color.values()[i].toString();
		}
		options = FXCollections.observableArrayList(colors);
		cbColor = new ComboBox<String>(options);
		cbColor.getSelectionModel().selectFirst();
		gridPane.add(cbColor ,  1, 2);			
		
		gridPane.add(new Text("Type:"),  0, 3);
		rbSolid = new RadioButton("Solid");
		rbBlink = new RadioButton("Blink");
		rbBlink.setSelected(true);
		group = new ToggleGroup();
		rbSolid.setToggleGroup(group);
		rbBlink.setToggleGroup(group);
		GridPane gp2 = new GridPane();
		gp2.add(rbSolid, 0,0);
		gp2.add(rbBlink, 1,0);
		gp2.setHgap(15);
		gp2.setVgap(5);
		gp2.setPadding(new Insets(10, 10, 10, 10));		
		group.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
		    public void changed(ObservableValue<? extends Toggle> ov,
		        Toggle old_toggle, Toggle new_toggle) {
		            if (group.getSelectedToggle() != null) {
		            	RadioButton rb = (RadioButton)group.getSelectedToggle();
		                if (rb.getText().equalsIgnoreCase("blink")) {
		                    ontimeT.setVisible(true);
		                    ontime.setDisable(false);
		            		offtimeT.setVisible(true);
		            		offtime.setDisable(false);	
		                } else {
		                    ontimeT.setVisible(false);
		                    ontime.setDisable(true);
		            		offtimeT.setVisible(false);
		            		offtime.setDisable(true);			                	
		                }
		            }                
		        }
		});		
		gridPane.add(gp2,  1, 3);		

		ontimeT = new Text("On time:");
		gridPane.add(ontimeT,  0, 4);
		ontime = new Spinner<Double>();
        SpinnerValueFactory<Double> vfOn = new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 25.5, 0.5, 0.1);
        ontime.setValueFactory(vfOn);
        gridPane.add(ontime,  1, 4);

        offtimeT = new Text("Off time:");
		gridPane.add(offtimeT,  0, 5);
		offtime = new Spinner<Double>();
		SpinnerValueFactory<Double> vfOff = new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 25.5, 0.1, 0.1);
        offtime.setValueFactory(vfOff);
		gridPane.add(offtime,  1, 5);
		
		//enable at the start
        ontimeT.setVisible(true);
        ontime.setDisable(false);
		offtimeT.setVisible(true);
		offtime.setDisable(false);		
		
		cbVendor.getSelectionModel().select(0);
		cbProduct.getSelectionModel().select(0);
		
		gridPane.add(new Text("Sound:"),  0, 6);
		rbOn = new RadioButton("On");
		rbOff = new RadioButton("Off");
		rbOff.setSelected(true);
		groupSound = new ToggleGroup();
		rbOn.setToggleGroup(groupSound);
		rbOff.setToggleGroup(groupSound);
		GridPane gp3 = new GridPane();
		gp3.add(rbOn, 0,0);
		gp3.add(rbOff, 1,0);
		gp3.setHgap(15);
		gp3.setVgap(5);
		gp3.setPadding(new Insets(10, 10, 10, 10));
		gridPane.add(gp3,  1, 6);
		//disable sound widgets for now
		gp3.setDisable(true);
		
		
		status = new Text("");
		status.setFill(javafx.scene.paint.Color.BLUE);
		
		light = new BusyLightAPI();
		//auto-detect Busy Light
		int[] ret = light.detectBusyLight();
		if (ret[0] != -1 && ret[1] != -1) {
			status.setText("BusyLight detected");
			cbVendor.getSelectionModel().select(ret[0]);
			cbProduct.getSelectionModel().select(ret[1]);
		}
		
		Button button = new Button("Send");
		button.setOnAction(e -> {
			status.setText("");
			if (light != null)
				light.shutdown();
			light = new BusyLightAPI();
			light.initDevice(Vendor.valueOf(cbVendor.getSelectionModel().getSelectedItem()) , Product.valueOf(cbProduct.getSelectionModel().getSelectedItem()) , null);
			
			RadioButton rb = (RadioButton)group.getSelectedToggle();
			Color c = Color.values()[cbColor.getSelectionModel().getSelectedIndex()];
			if (rb.getText().equalsIgnoreCase("blink")) {
				light.blinkColor(c, (int)(ontime.getValue()*10), (int)(offtime.getValue()*10));
			} else {
				light.steadyColor(c);
			}
		});
		gridPane.add(button,  0, 7);
		
		Button buttonStop = new Button("Stop");
		buttonStop.setOnAction(e -> {
			status.setText("");
			if (light != null)
				light.stop();
		});
		gridPane.add(buttonStop,  1, 7);		
		
		Button buttone = new Button("Exit");
		buttone.setOnAction(e -> {
			light.shutdown();
			Platform.exit();
		});
		gridPane.add(buttone, 2,7);		
		
		gridPane.add(status,  1, 8);
		

		//set the scene
		Scene scene = new Scene(gridPane,315,300);
		stage.setTitle("BusyLight Tester");
		stage.setScene(scene);
		stage.setOnCloseRequest(e -> Platform.exit());
		stage.show();

	}

}
