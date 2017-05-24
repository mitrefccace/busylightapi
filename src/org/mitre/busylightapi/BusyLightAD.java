package org.mitre.busylightapi;

import java.net.URISyntaxException;

import org.json.JSONObject;
import org.mitre.busylightapi.BusyLightAPI.Product;
import org.mitre.busylightapi.BusyLightAPI.Vendor;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class BusyLightAD extends Application {

	private GridPane gridPane;
	private TextField tfToken;
	private ComboBox<String> cbVendor, cbProduct;
	private BusyLightAPI light;
	private static Socket socket = null;

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

		try {
			socket = IO.socket("http://localhost:3000");
			//socket = IO.socket("http://ntldevdemo.task3acrdemo.com:8005/"); //MIKE'S

			socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
				
				//on connect, client should send token for authentication
				@Override
				public void call(Object... args) {
					socket.emit("blregister", "{\"token\": \"abc123xyz\"}");
				}

			}).on("message-stream", new Emitter.Listener() {
				//MIKE'S SERVER
				@Override
				public void call(Object... args) {
					JSONObject obj = (JSONObject)args[0];
					System.out.println(obj.toString());
				}
			}).on("welcome", new Emitter.Listener() {
				@Override
				public void call(Object... args) {
					JSONObject obj = (JSONObject)args[0];
					System.out.println(obj.toString());
				}
			}).on("time", new Emitter.Listener() {

				@Override
				public void call(Object... args) {
					JSONObject obj = (JSONObject)args[0];
					System.out.println(obj.toString());
				}

			}).on("message", new Emitter.Listener() {
				@Override
				public void call(Object... args) {
					JSONObject obj = (JSONObject)args[0];
					System.out.println(obj.toString());
				}
			}).on("error", new Emitter.Listener() {
				@Override
				public void call(Object... args) {
					JSONObject obj = (JSONObject)args[0];
					System.out.println(obj.toString());
				}
			}).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

				@Override
				public void call(Object... args) {System.out.println("Disconnected.");}

			}).on(Socket.EVENT_CONNECT, new Emitter.Listener() {

				@Override
				public void call(Object... args) {System.out.println("Connected.");}

			});
			socket.connect();


		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		

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

		gridPane.add(new Text("Token:"),  0, 2);
		tfToken = new TextField();
		tfToken.setPrefColumnCount(10);
		gridPane.add(tfToken,  1, 2);

		Button buttonReg = new Button("Register");
		buttonReg.setOnAction(e -> {
			//do stuff
		});	
		gridPane.add(buttonReg,  0, 3);

		Button buttonExit = new Button("Exit");
		buttonExit.setOnAction(e -> {
			if (light != null) {
				light.stop();
				light.shutdown();
				light = null;
			}
			if (socket != null && socket.connected()) {
				socket.disconnect();
				socket.close();
				socket = null;
			}
			Platform.exit();
		});	
		gridPane.add(buttonExit,  1, 3);		


		//set the scene
		Scene scene = new Scene(gridPane,330,390);
		stage.setTitle("BusyLight - ACE Direct");
		stage.setScene(scene);
		stage.setOnCloseRequest(e -> {
			if (light != null) {
				light.stop();
				light.shutdown();
				light = null;
			}	
			if (socket != null && socket.connected()) {
				socket.disconnect();
				socket.close();
				socket = null;
			}			
			Platform.exit();
		});
		stage.show();

	}

}
