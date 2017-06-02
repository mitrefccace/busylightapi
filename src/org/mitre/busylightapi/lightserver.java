package org.mitre.busylightapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import org.json.JSONObject;
import org.mitre.busylightapi.BusyLightAPI.Product;
import org.mitre.busylightapi.BusyLightAPI.Vendor;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

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

	private static final int BWIDTH = 310;
	private static final int BHEIGHT = 410;		
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
			System.out.println("shutting down...");
			if (hasLight) {
				if (light != null) {
					light.stop();
					light.shutdown();
				}
			}
			server.stop(0);
			Platform.exit();
		});

		//init the light
		/*
		light = new BusyLightAPI();
		int[] ret = light.detectBusyLight();
		if (ret[0] != -1 && ret[1] != -1) {
			boolean bRet = light.initDevice(Vendor.values()[ret[0]] , Product.values()[ret[1]] , null);
			if (!bRet) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.initModality(Modality.APPLICATION_MODAL);
				alert.setTitle("Error");
				alert.setHeaderText("Light Error");
				String s ="Unable to connect to light. Make sure the program is not already running.";
				alert.setContentText(s);
				alert.showAndWait();

				light = null;
				hasLight = false;
				//shutdown();
				//System.exit(-1);
			}
		}
		cbVendor.getSelectionModel().select(ret[0]);
		cbProduct.getSelectionModel().select(ret[1]);	
		tStatus.setText("BusyLight detected");
		*/
		
		tStatus.setFill(javafx.scene.paint.Color.BLUE);			

		//start the server
		try {
			server = HttpServer.create(new InetSocketAddress("localhost",port), 0);
			server.createContext(sPath, new MyHandler());
			System.out.println("I live to serve: " + "http://localhost:" + port + sPath);
			server.start();
		} catch (Exception e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.initModality(Modality.APPLICATION_MODAL);
			alert.setTitle("Error");
			alert.setHeaderText("Server Error");
			String s ="Unable to start the server. Make sure the program is not already running.";
			alert.setContentText(s);
			alert.showAndWait();
			shutdown();
			System.exit(-1);
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
					if (hasLight) {
						light.stop();
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
					String s ="Unable to connect to light. Make sure the program is not already running.";
					alert.setContentText(s);
					alert.showAndWait();
					
					light = null;
					hasLight = false;
					//shutdown();
					//System.exit(-1);
				}
			}			
		}
		detectBusylight();

		try {
			server = HttpServer.create(new InetSocketAddress("localhost",port), 0);
			server.createContext(sPath, new MyHandler());
			System.out.println("I live to serve: " + "http://localhost:" + port + sPath);
			server.start();			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			buttonStart.setDisable(false);
			buttonStop.setDisable(true);
			buttonTest.setDisable(true);
			tConnectStatus.setFill(javafx.scene.paint.Color.GRAY);
			tConnectStatus.setText("Stopped");
			tAgentStatus.setText("Unknown");
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
					String s ="Unable to connect to light. Make sure the program is not already running.";
					alert.setContentText(s);
					alert.showAndWait();

					light = null;
					hasLight = false;
					//shutdown();
					//System.exit(-1);
				} else {
					cbVendor.getSelectionModel().select(ret[0]);
					cbProduct.getSelectionModel().select(ret[1]);	
				}
			}			
		}
		detectBusylight();

		blinkTimeline.stop();
		//circle.setFill(Color.rgb(0,255,0));
		circle.setFill(Color.GRAY);
		circleBlinkOn = false;	
		stopTimeline.setCycleCount(1);
		stopTimeline.play();		

		if (hasLight) {
			//light.ping();
			light.rainbow();
		}


	}	

	public void stopLs() {
		buttonStart.setDisable(false);
		buttonStop.setDisable(true);
		buttonTest.setDisable(false);
		if (hasLight) {
			if (light != null) {
				light.stop();
			}
		}
		if (server != null)
		{
			server.stop(0);
			server = null;
		}

		blinkTimeline.stop();
		timeoutTimeline.stop();
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
			light.stop();
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

	class MyHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange t) throws IOException {

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
					if (hasLight)
						light.stop();
					blinkTimeline.stop();
					circle.setFill(Color.GRAY);
					circleBlinkOn = false;					
				} else {
					int r = o.getInt("r");
					int g = o.getInt("g");
					int b = o.getInt("b");
					if (o.getBoolean("blink")) {
						//blink
						if (hasLight)
							light.blinkColor(r,g,b, 5, 1);
						if (blinkTimeline != null) {
							blinkTimeline.stop();
							circleBlinkOn = false;
							circleBlinkColor = Color.rgb(r,g,b);
							blinkTimeline.play();
						}					
					} else {
						//solid
						if (hasLight)
							light.steadyColor(r,g,b);
						circle.setFill(Color.rgb(r,g,b));					
					}
				}

				lastHeard = System.currentTimeMillis();
				tConnectStatus.setFill(javafx.scene.paint.Color.GREEN);
				tConnectStatus.setText("Running");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				System.err.println(e.getMessage());
				if (hasLight)
					light.stop();
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