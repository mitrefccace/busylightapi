package org.mitre.busylightapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManagerFactory;
import org.json.JSONObject;
import org.mitre.busylightapi.BusyLightAPI.Product;
import org.mitre.busylightapi.BusyLightAPI.Vendor;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class lightserver extends Application {

	private BusyLightAPI light;
	private String sPath = "/setbusylight";
	private int port = 6298;
	private HttpServer server;
	private Text tConnectStatus, tStatus, tAgentStatus;
	private ComboBox<String> cbVendor, cbProduct;
	private Button buttonStart, buttonStop, buttonExit, buttonTest;
	private Circle circle;
	private boolean circleBlinkOn = false;
	private Color circleBlinkColor;	
	private BorderPane root;
	private Timeline blinkTimeline, stopTimeline, timeoutTimeline;
	private long lastHeard;
	private boolean hasLight = true;

	private static final int BWIDTH = 330;
	private static final int BHEIGHT = 455;		
	private static final int TIMEOUT_SECS = 60;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		lastHeard = System.currentTimeMillis();

		stage.getIcons().add(new Image("ace.png"));		
		stage.setResizable(false);

		GridPane gridPane = new GridPane();
		gridPane.setHgap(5);
		gridPane.setVgap(5);
		gridPane.setPadding(new Insets(10, 10, 10, 10));

		int row = 0;
		gridPane.add(new Text("Status:"),  0, row);
		tConnectStatus = new Text("Running");
		tConnectStatus.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
		tConnectStatus.setFill(javafx.scene.paint.Color.GREEN);
		gridPane.add(tConnectStatus,  1, row);		

		row++;
		gridPane.add(new Text("Agent Status:"),  0, row);
		tAgentStatus = new Text("Unknown");
		tAgentStatus.setFill(javafx.scene.paint.Color.BLACK);
		gridPane.add(tAgentStatus,  1, row);			

		row++;
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
		HBox hbox = new HBox();
		hbox.setPadding(new Insets(0, 0, 0, 0));
		hbox.setSpacing(5);
		buttonStart = new Button("Start");
		buttonStart.setDisable(true);
		buttonStart.setOnAction(e -> startLs());			
		buttonStop = new Button("Stop");
		buttonStop.setOnAction(e -> stopLs());
		buttonTest = new Button("Test");
		buttonTest.setOnAction(e -> testLight());	
		buttonTest.setDisable(true);
		buttonExit = new Button("Exit");
		buttonExit.setTextFill(Color.RED);
		buttonExit.setOnAction(e -> shutdown());		
		hbox.getChildren().addAll(buttonStart,buttonStop,buttonTest,buttonExit);
		gridPane.add(hbox,  1, row);

		row++;
		tStatus = new Text("");
		gridPane.add(tStatus,  1, row);		

		row++;
		CheckBox cb = new CheckBox("Show icon");
		cb.setSelected(true);
		cb.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				CheckBox chk = (CheckBox) event.getSource();
				if (chk.isSelected()) {
					//show light icon
					stage.setHeight(BHEIGHT);
					root.setBottom(circle);

				} else {
					//hide light icon
					root.setBottom(null);
					stage.setHeight(BHEIGHT - (circle.getRadius() * 2) );
				}

			}
		});
		gridPane.add(cb, 1, row);

		circle = new Circle(0, 0, 75);
		//circle.setFill(Color.rgb(0,255,0));
		circle.setFill(Color.GRAY);
		circle.setStroke(Color.BLACK);
		circle.setStrokeWidth(5);	

		root = new BorderPane();

		//set the scene
		Scene scene = new Scene(root);
		stage.setHeight(BHEIGHT);
		stage.setWidth(BWIDTH);

		root.setCenter(gridPane);

		BorderPane.setAlignment(circle, Pos.CENTER);
		BorderPane.setMargin(circle, new Insets(0,0,15,0)); // optional
		root.setBottom(circle);			

		//set up menu
		MenuBar menuBar = new MenuBar();
		Menu menuFile = new Menu("File");
		MenuItem exitMenuItem = new MenuItem("Exit");
		exitMenuItem.setOnAction(actionEvent -> shutdown());
		menuFile.getItems().addAll(exitMenuItem);
		Menu menuView = new Menu("View");
		Menu menuHelp = new Menu("Help");
		menuBar.getMenus().addAll(menuFile, menuView, menuHelp);
		root.setTop(menuBar);

		stage.setTitle("BusyLight - ACE Direct");

		stage.setScene(scene);
		stage.setOnCloseRequest(e -> {
			shutdown();
		});

		tStatus.setFill(javafx.scene.paint.Color.BLUE);			

		server = getHttpsServer();
		if (server == null) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.initModality(Modality.APPLICATION_MODAL);
			alert.setTitle("Error");
			alert.setHeaderText("Server Error");
			String s ="Unable to start the server. Make sure the program is not already running.";
			alert.setContentText(s);
			alert.showAndWait();
			shutdown();
			System.exit(-1);			
		} else {
			server.createContext(sPath, new MyHandler());
			server.createContext("/", new MyHandler());
			server.start();
			System.out.println("I live to serve: " + "https://localhost:" + port + sPath);
		}

		stage.show();
		stage.requestFocus();			

		blinkTimeline = new Timeline(new KeyFrame(Duration.millis(300), new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (circleBlinkOn) {
					circle.setFill(Color.GRAY);
				} else {
					circle.setFill(circleBlinkColor);
				}
				circleBlinkOn = !circleBlinkOn;
			}
		}));
		blinkTimeline.setCycleCount(Timeline.INDEFINITE);	

		stopTimeline = new Timeline(new KeyFrame(Duration.millis(500), new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {	
				if (blinkTimeline != null) {
					blinkTimeline.stop();
				}
				circle.setFill(Color.GRAY);
			}
		}));		

		timeoutTimeline = new Timeline(new KeyFrame(Duration.millis(TIMEOUT_SECS*1000), new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {	
				if ( (System.currentTimeMillis() - lastHeard) / 1000 > TIMEOUT_SECS) {
					circle.setFill(Color.GRAY);
					if (hasLight && light != null) {
						light.stopLight();
					}
					blinkTimeline.stop();
					tConnectStatus.setFill(javafx.scene.paint.Color.GRAY);
					tConnectStatus.setText("Timed out");
					tAgentStatus.setText("Unknown");
				}
			}
		}));
		timeoutTimeline.setCycleCount(Timeline.INDEFINITE);
		timeoutTimeline.play();

		testLight();
	}

	public void startLs() {
		stopLs();
		tConnectStatus.setFill(javafx.scene.paint.Color.GREEN);
		tConnectStatus.setText("Running");
		tAgentStatus.setText("Unknown");
		buttonStart.setDisable(true);
		buttonStop.setDisable(false);
		buttonTest.setDisable(true);
		if (light == null) {
			hasLight = true;
			light = new BusyLightAPI();
			int[] ret = light.detectBusyLight();
			if (ret[0] != -1 && ret[1] != -1) {
				boolean bRet = light.initDevice(Vendor.values()[ret[0]] , Product.values()[ret[1]] , null);
				if (!bRet) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.initModality(Modality.APPLICATION_MODAL);
					alert.setTitle("Error");
					alert.setHeaderText("Light Error");
					String s ="Unable to connect to the light. Make sure the light is connected and this program is not already running.";
					alert.setContentText(s);
					alert.showAndWait();

					light = null;
					hasLight = false;
				}
			}			
		}
		detectBusylight();

		server = getHttpsServer();
		if (server == null) {
			buttonStart.setDisable(false);
			buttonStop.setDisable(true);
			buttonTest.setDisable(true);
			tConnectStatus.setFill(javafx.scene.paint.Color.GRAY);
			tConnectStatus.setText("Stopped");
			tAgentStatus.setText("Unknown");			
		} else {
			server.createContext(sPath, new MyHandler());
			server.createContext("/", new MyHandler());
			server.start();
			System.out.println("I live to serve: " + "https://localhost:" + port + sPath);
		}

		timeoutTimeline.play();
	}	

	public void testLight() {
		if (light == null) {
			hasLight = true;
			light = new BusyLightAPI();
			int[] ret = light.detectBusyLight();
			if (ret[0] != -1 && ret[1] != -1) {
				boolean bRet = light.initDevice(Vendor.values()[ret[0]] , Product.values()[ret[1]] , null);
				if (!bRet) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.initModality(Modality.APPLICATION_MODAL);
					alert.setTitle("Error");
					alert.setHeaderText("Light Error");
					String s ="Unable to connect to the light. Make sure the light is connected and this program is not already running.";
					alert.setContentText(s);
					alert.showAndWait();

					light = null;
					hasLight = false;
				} else {
					cbVendor.getSelectionModel().select(ret[0]);
					cbProduct.getSelectionModel().select(ret[1]);	
				}
			}			
		}
		detectBusylight();

		blinkTimeline.stop();
		circle.setFill(Color.GRAY);
		circleBlinkOn = false;	
		stopTimeline.setCycleCount(1);
		stopTimeline.play();		

		if (hasLight && light != null) {
			light.rainbow();
		}


	}	

	public void stopLs() {
		buttonStart.setDisable(false);
		buttonStop.setDisable(true);
		buttonTest.setDisable(false);
		if (hasLight) {
			if (light != null) {
				light.stopLight();
			}
		}
		if (server != null)
		{
			server.stop(0);
			server = null;
		}

		blinkTimeline.stop();
		stopTimeline.stop();
		timeoutTimeline.stop();	
		circle.setFill(Color.GRAY);
		circleBlinkOn = false;		

		tStatus.setText("");
		tConnectStatus.setFill(javafx.scene.paint.Color.GRAY);
		tConnectStatus.setText("Stopped");
		tAgentStatus.setText("Unknown");
	}		

	public void shutdown() {
		System.out.println("shutting down...");
		if (light != null) {
			light.stopLight();
			light.shutdown();
			light = null;
		}
		if (blinkTimeline != null) {
			blinkTimeline.stop();
		}
		if (timeoutTimeline != null) {
			timeoutTimeline.stop();
		}
		if (stopTimeline != null) {
			stopTimeline.stop();
		}		
		if (server != null) {
			server.stop(0);
		}
		Platform.exit();		
	}	

	public void detectBusylight() {
		//try to auto-detect Busy Light
		tStatus.setFill(javafx.scene.paint.Color.RED);
		tStatus.setText("No light detected");		
		if (light != null) {
			int[] ret = light.detectBusyLight();
			if (ret[0] != -1 && ret[1] != -1) {
				tStatus.setFill(javafx.scene.paint.Color.BLUE);
				tStatus.setText("BusyLight detected");
				cbVendor.getSelectionModel().select(ret[0]);
				cbProduct.getSelectionModel().select(ret[1]);
			}			
		}
	}

	public HttpsServer getHttpsServer() {
		HttpsServer server = null;

		// load certificate
		InputStream is = lightserver.class.getResourceAsStream("light.jks");
		String s = "      ";
		char[] storepass = s.toCharArray();
		char[] keypass = s.toCharArray();

		try {
			KeyStore keystore = KeyStore.getInstance("JKS");
			keystore.load(is, storepass);

			// setup the key manager factory
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			kmf.init(keystore, keypass);
			// setup the trust manager factory
			TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
			tmf.init(keystore);

			// create https server
			server = HttpsServer.create(new InetSocketAddress("localhost",port), 0);
			// create ssl context
			SSLContext sslContext = SSLContext.getInstance("TLS");
			// setup the HTTPS context and parameters
			sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
			server.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
				public void configure(HttpsParameters params) {
					try {
						// initialize the SSL context
						SSLContext c = SSLContext.getDefault();
						SSLEngine engine = c.createSSLEngine();
						params.setNeedClientAuth(false);
						params.setCipherSuites(engine.getEnabledCipherSuites());
						params.setProtocols(engine.getEnabledProtocols());
						// get the default parameters
						SSLParameters defaultSSLParameters = c.getDefaultSSLParameters();
						params.setSSLParameters(defaultSSLParameters);
					} catch (Exception ex) {
						ex.printStackTrace();
						System.out.println("Failed to create HTTPS server");
					}
				}
			});			

			is.close();
		} catch (Exception e) {
			e.printStackTrace();
			server = null;
		}

		return server;

	}

	class MyHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange t) throws IOException {

			//ping
			if (t.getRequestMethod().equalsIgnoreCase("get")) {
				//send any response
				String response = "";
				response += "<!DOCTYPE html>";
				response += "<html>";
				response += "<head>";
				response += "<title></title>";
				response += "<meta name='viewport' content='width=device-width, initial-scale=1'>";
				response += "<link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css'>";
				response += "</head>";
				response += "<body>";
				response += "<table style='width:100%'>";
				response += "<tr>";
				response += "<td align='center'><h3>BusyLight access granted.</h3></td>";
				response += "</tr>";
				response += "<tr>";
				response += "<td align='center'><i class='fa fa-check' style='font-size:48px;color:green'></i></td>";
				response += "</tr>";
				response += "</table>";
				response += "</body>";
				response += "</html>"; 				

				t.sendResponseHeaders(200, response.length());
				OutputStream os = t.getResponseBody();
				os.write(response.getBytes());
				os.close();
			}

			//turn away any requests we don't expect
			if (!t.getRequestMethod().equalsIgnoreCase("post") || !t.getRequestURI().toString().equalsIgnoreCase("/setbusylight")) {
				//invalid request
				String response = "";
				t.sendResponseHeaders(400, response.length());
				OutputStream os = t.getResponseBody();
				os.write(response.getBytes());
				os.close();	
				return;
			}

			InputStreamReader isr =  new InputStreamReader(t.getRequestBody(),"utf-8");
			BufferedReader br = new BufferedReader(isr);
			int bte;
			StringBuilder json = new StringBuilder(512);
			while ((bte = br.read()) != -1) {
				json.append((char) bte);
			}
			br.close();
			isr.close();

			//process light
			try {

				if (blinkTimeline != null) {
					blinkTimeline.stop();
					circleBlinkOn = false;
				}				

				JSONObject o = new JSONObject(json.toString());
				tAgentStatus.setText(o.getString("status"));
				if (o.getBoolean("stop")) {
					if (hasLight && light != null)
						light.stopLight();
					blinkTimeline.stop();
					circle.setFill(Color.GRAY);
					circleBlinkOn = false;					
				} else {
					int r = o.getInt("r");
					int g = o.getInt("g");
					int b = o.getInt("b");
					if (o.getBoolean("blink")) {
						//blink
						if (hasLight && light != null)
							light.blinkColor(r,g,b, 5, 1);
						if (blinkTimeline != null) {
							blinkTimeline.stop();
							circleBlinkOn = false;
							circleBlinkColor = Color.rgb(r,g,b);
							blinkTimeline.play();
						}					
					} else {
						//solid
						if (hasLight && light != null)
							light.steadyColor(r,g,b);
						circle.setFill(Color.rgb(r,g,b));					
					}
				}

				lastHeard = System.currentTimeMillis();
				tConnectStatus.setFill(javafx.scene.paint.Color.GREEN);
				tConnectStatus.setText("Running");
			} catch (Exception e) {
				System.err.println("error: " + e.getMessage());
				e.printStackTrace();
				if (hasLight && light != null)
					light.stopLight();
				blinkTimeline.stop();
				tConnectStatus.setFill(javafx.scene.paint.Color.RED);
				tConnectStatus.setText("Error");
				tAgentStatus.setText("Unknown");
			}			

			//send any response
			String response = "";
			t.sendResponseHeaders(200, response.length());
			OutputStream os = t.getResponseBody();
			os.write(response.getBytes());
			os.close();
		}
	}

}