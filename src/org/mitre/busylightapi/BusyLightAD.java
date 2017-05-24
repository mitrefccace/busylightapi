package org.mitre.busylightapi;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.mitre.busylightapi.BusyLightAPI.Product;
import org.mitre.busylightapi.BusyLightAPI.Vendor;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class BusyLightAD extends Application {

	private GridPane gridPane;
	private TextField tfToken, tfURL;
	private Text tStatus;
	private ComboBox<String> cbVendor, cbProduct;
	private BusyLightAPI light;
	private PollingThread pthread;

	public static void main(String[] args) {

		//PROXY??
		// HTTP
		/*
		System.setProperty("http.proxyHost", "http://gatekeeper.mitre.org");
		System.setProperty("http.proxyPort", "80");
		System.setProperty("http.nonProxyHosts", "*.mitre.org|localhost|127.0.0.1");

		// HTTPS
		System.setProperty("https.proxyHost", "https://gatekeeper.mitre.org");
		System.setProperty("https.proxyPort", "80");
		System.setProperty("https.nonProxyHosts", "*.mitre.org|localhost|127.0.0.1");
		 */





		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		
		stage.getIcons().add(new Image("ace.png"));

		tStatus = new Text("");

		gridPane = new GridPane();
		gridPane.setHgap(5);
		gridPane.setVgap(5);
		gridPane.setPadding(new Insets(10, 10, 10, 10));

		int row = 0;
		gridPane.add(new Text("Vendor:"),  0, row);

		String[] vendors = new String[Vendor.values().length];
		for (int i=0; i < Vendor.values().length; i++) {
			vendors[i] = Vendor.values()[i].toString();
		}
		ObservableList<String> options = FXCollections.observableArrayList(vendors);
		cbVendor = new ComboBox<String>(options);
		cbVendor.getSelectionModel().selectFirst();
		gridPane.add(cbVendor ,  1, row);

		row++;
		gridPane.add(new Text("Product:"),  0, row);

		String[] products = new String[Product.values().length];
		for (int i=0; i < Product.values().length; i++) {
			products[i] = Product.values()[i].toString();
		}
		options = FXCollections.observableArrayList(products);
		cbProduct = new ComboBox<String>(options);
		cbProduct.getSelectionModel().selectFirst();
		gridPane.add(cbProduct ,  1, row);		

		row++;
		gridPane.add(new Text("Token:"),  0, row);
		tfToken = new TextField();
		tfToken.setPrefColumnCount(10);
		gridPane.add(tfToken,  1, row);

		row++;
		gridPane.add(new Text("URL:"),  0, row);
		tfURL = new TextField();
		tfURL.setPrefColumnCount(50);
		tfURL.setText("https://<domain>/getagentstatus");
		gridPane.add(tfURL,  1, row);		

		row++;
		Button buttonReg = new Button("Register");
		buttonReg.setOnAction(e -> {
			//do stuff
			tStatus.setText("");
		});	
		GridPane gridPane2 = new GridPane();
		gridPane2.setHgap(5);
		gridPane2.setVgap(5);
		gridPane2.add(buttonReg,  0, 0);

		light = new BusyLightAPI();
		pthread = new PollingThread("polling thread", light);
		//auto-detect Busy Light
		int[] ret = light.detectBusyLight();
		if (ret[0] != -1 && ret[1] != -1) {
			tStatus.setFill(javafx.scene.paint.Color.BLUE);
			tStatus.setText("BusyLight detected");
			System.out.println("BusyLight detected");
			cbVendor.getSelectionModel().select(ret[0]);
			cbProduct.getSelectionModel().select(ret[1]);
		}		

		Button buttonExit = new Button("Exit");
		buttonExit.setOnAction(e -> {
			if (light != null) {
				light.stop();
				light.shutdown();
				light = null;
			}
			if (pthread != null) {
				if (pthread.isAlive())
					pthread.interrupt();
				pthread = null;
			}				
			Platform.exit();
		});	
		gridPane2.add(buttonExit,  1, 0);	
		gridPane.add(gridPane2,  1, row);		

		row++;
		gridPane.add(tStatus,  0, row);
		
		//set the scene
		Scene scene = new Scene(gridPane,375,390);
		stage.setTitle("BusyLight - ACE Direct");
		stage.setScene(scene);
		stage.setOnCloseRequest(e -> {
			if (light != null) {
				light.stop();
				light.shutdown();
				light = null;
			}
			if (pthread != null) {
				if (pthread.isAlive())
					pthread.interrupt();
				pthread = null;
			}			
			Platform.exit();
		});
		stage.show();
		
		pthread.start();

	}

}

class PollingThread extends Thread {

	private static final int FREQUENCY_SECS = 1;
	private boolean bAlive;
	private BusyLightAPI theLight;

	public PollingThread(String name, BusyLightAPI light) {
		super(name);
		bAlive = true;
		theLight = light;
	}

	@Override
	public void run() {
		while (bAlive) {
			try {
				String url = "http://localhost:1234/getagentstatus/123abc";
				//url = "https://dev3demo.task3acrdemo.com:8005/getagentstatus/123abc";
				Client client = ClientBuilder.newClient();
				//client.register(GZipEncoder.class);
				WebTarget target = client.target(url);

				Response response = target.request()
						.accept("application/json; charset=utf-8")
						.acceptEncoding("gzip").get();

				String json = response.readEntity(String.class);
				System.out.println(json);
				response.close();

				//System.out.println("Sent GET request...");
				Thread.sleep(FREQUENCY_SECS * 1000);
			} catch (InterruptedException e) {
				bAlive = false;
			}
		}
	}
}