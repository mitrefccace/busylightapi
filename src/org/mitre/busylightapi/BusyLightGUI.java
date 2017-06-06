package org.mitre.busylightapi;

import org.mitre.busylightapi.BusyLightAPI.BLColor;
import org.mitre.busylightapi.BusyLightAPI.Product;
import org.mitre.busylightapi.BusyLightAPI.Ringtone;
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
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class BusyLightGUI extends Application {

	private GridPane gridPane;
	private Text status, ontimeT, offtimeT, ringtoneT, sliderT;
	private ComboBox<String> cbVendor, cbProduct, cbColor, cbRingtone;
	private Spinner<Double> ontime, offtime;
	private BusyLightAPI light;
	private RadioButton rbSolid, rbBlink, rbOn, rbOff;
	private Slider slider;
	private ToggleGroup group, groupSound;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {

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
		String[] colors = new String[BLColor.values().length];
		for (int i=0; i < BLColor.values().length; i++) {
			colors[i] = BLColor.values()[i].toString();
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
		rbOn.setSelected(true);
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
		ringtoneT = new Text("Tone:");
		gridPane.add(ringtoneT,  0, 7);
		String[] ringtones = new String[Ringtone.values().length];
		for (int i=0; i < Ringtone.values().length; i++) {
			ringtones[i] = Ringtone.values()[i].toString();
		}
		ObservableList<String> rtoptions = FXCollections.observableArrayList(ringtones);
		cbRingtone = new ComboBox<String>(rtoptions);
		cbRingtone.getSelectionModel().selectFirst();

		gridPane.add(cbRingtone ,  1, 7);		

		slider = new Slider();
		slider.setMin(0);
		slider.setMax(7);
		slider.setValue(3);
		slider.setMajorTickUnit(1);
		slider.setMinorTickCount(0);
		slider.setShowTickLabels(true);
		slider.setShowTickMarks(true);
		slider.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				slider.setValue(Math.round(new_val.doubleValue()));
			}
		});		
		sliderT = new Text("Volume:");
		gridPane.add(sliderT,  0, 8);
		gridPane.add(slider,  1, 8);

		groupSound.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
			public void changed(ObservableValue<? extends Toggle> ov,
					Toggle old_toggle, Toggle new_toggle) {
				if (groupSound.getSelectedToggle() != null) {
					RadioButton rb = (RadioButton)groupSound.getSelectedToggle();
					if (rb.getText().equalsIgnoreCase("on")) {
						ringtoneT.setVisible(true);
						sliderT.setVisible(true);
						cbRingtone.setDisable(false);
						slider.setDisable(false);
					} else {
						ringtoneT.setVisible(false);
						sliderT.setVisible(false);
						cbRingtone.setDisable(true);
						slider.setDisable(true);
					}
				}                
			}
		});			



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

		GridPane gp4 = new GridPane();
		gp4.setHgap(15);
		gp4.setVgap(5);
		gp4.setPadding(new Insets(10, 10, 10, 10));		

		Button button = new Button("Send");
		button.setOnAction(e -> {
			status.setText("");
			if (light != null) {
				light.stopLight();
				light.shutdown();
			}
			light = new BusyLightAPI();
			boolean bRet = light.initDevice(Vendor.valueOf(cbVendor.getSelectionModel().getSelectedItem()) , Product.valueOf(cbProduct.getSelectionModel().getSelectedItem()) , null);
			if (!bRet) {
				System.err.println("Unable to connect to device.");
				System.exit(-1);
			}

			RadioButton rbSound = (RadioButton)groupSound.getSelectedToggle();
			if (rbSound.getText().equalsIgnoreCase("on"))
				light.setSoundEnabled(true);
			else
				light.setSoundEnabled(false);

			//set ringtone and volume
			light.setRingTone(Ringtone.valueOf(cbRingtone.getSelectionModel().getSelectedItem()));
			light.setVolume((short)slider.getValue());

			RadioButton rb = (RadioButton)group.getSelectedToggle();
			BLColor c = BLColor.values()[cbColor.getSelectionModel().getSelectedIndex()];
			if (rb.getText().equalsIgnoreCase("blink")) {
				light.blinkColor(c, (int)(ontime.getValue()*10), (int)(offtime.getValue()*10));
			} else {
				light.steadyColor(c);
			}

		});
		gp4.add(button,  0, 0);

		Button buttonStop = new Button("Stop");
		buttonStop.setOnAction(e -> {
			status.setText("");
			if (light != null)
				light.stopLight();
		});	
		gp4.add(buttonStop,  1, 0);

		gridPane.add(gp4, 1, 9);

		Button buttone = new Button("Exit");
		buttone.setOnAction(e -> {
			if (light != null) {
				light.stopLight();
				light.shutdown();
				light = null;
			}
			Platform.exit();
		});
		gridPane.add(buttone, 2, 9);		

		gridPane.add(status,  1, 10);


		//set the scene
		Scene scene = new Scene(gridPane,330,390);
		stage.setTitle("BusyLight Tester");
		stage.setScene(scene);
		stage.setOnCloseRequest(e -> {
			if (light != null) {
				light.stopLight();
				light.shutdown();
				light = null;
			}			
			Platform.exit();
		});
		stage.show();

	}

}
