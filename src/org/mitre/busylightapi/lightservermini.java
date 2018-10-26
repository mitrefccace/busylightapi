package org.mitre.busylightapi;
/*
 * This does not need the hid4java API, libraries, or resources
 */
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

import org.json.JSONException;
import org.json.JSONObject;
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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
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
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class lightservermini extends Application {

	private String sPath = "/setbusylight";
	private int port = 6298;
	private HttpServer server;
	private Text tConnectStatus, tStatus, tAgentStatus;
	private Button buttonStart, buttonStop, buttonExit;
	private Circle circle;
	private boolean circleBlinkOn = false;
	private Color circleBlinkColor;	
	private BorderPane root;
	private Timeline blinkTimeline, stopTimeline, timeoutTimeline;
	private long lastHeard;

	private static final int BWIDTH = 140;
	private static final int BHEIGHT = 195;		
	private static final int TIMEOUT_SECS = 60;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		lastHeard = System.currentTimeMillis();

		stage.getIcons().add(new Image("ace.png"));		
		stage.setResizable(false);
		stage.initStyle(StageStyle.UTILITY);

		GridPane gridPane = new GridPane();
		gridPane.setHgap(5);
		gridPane.setVgap(5);
		gridPane.setPadding(new Insets(0, 0, 0, 0));

		int row = 0;
		Text t1 = new Text("Status:");
		t1.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
		tConnectStatus = new Text("Running");
		tConnectStatus.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
		tConnectStatus.setFill(javafx.scene.paint.Color.GREEN);		
		HBox hboxStatus = new HBox();
		hboxStatus.setPadding(new Insets(0, 0, 0, 0));
		hboxStatus.setSpacing(5);	
		hboxStatus.getChildren().addAll(t1,tConnectStatus);
		gridPane.add(hboxStatus, 0, row);

		row++;
		Text t2 = new Text("Agent Status:");
		t2.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
		tAgentStatus = new Text("Unknown");
		tAgentStatus.setFont(Font.font("Verdana", FontWeight.NORMAL, 10));
		tAgentStatus.setFill(javafx.scene.paint.Color.BLACK);
		HBox hboxAstatus = new HBox();
		hboxAstatus.setPadding(new Insets(0, 0, 0, 0));
		hboxAstatus.setSpacing(5);	
		hboxAstatus.getChildren().addAll(t2,tAgentStatus);
		gridPane.add(hboxAstatus, 0, row);

		row++;
		HBox hbox = new HBox();
		hbox.setPadding(new Insets(0, 0, 0, 0));
		hbox.setSpacing(5);
		buttonStart = new Button("Start");
		buttonStart.setFont(Font.font("Verdana", FontWeight.NORMAL, 8));
		buttonStart.setDisable(true);
		buttonStart.setOnAction(e -> startLs());			
		buttonStop = new Button("Stop");
		buttonStop.setFont(Font.font("Verdana", FontWeight.NORMAL, 8));
		buttonStop.setOnAction(e -> stopLs());
		buttonExit = new Button("Exit");
		buttonExit.setFont(Font.font("Verdana", FontWeight.NORMAL, 8));
		buttonExit.setTextFill(Color.RED);
		buttonExit.setOnAction(e -> shutdown());		
		hbox.getChildren().addAll(buttonStart,buttonStop,buttonExit);
		gridPane.add(hbox,  0, row);

		row++;
		tStatus = new Text("");
		gridPane.add(tStatus,  0, row);		

		circle = new Circle(0, 0, 36);
		circle.setFill(Color.GRAY);
		circle.setStroke(Color.BLACK);
		circle.setStrokeWidth(2);	

		root = new BorderPane();

		//set the scene
		Scene scene = new Scene(root);
		stage.setHeight(BHEIGHT);
		stage.setWidth(BWIDTH);

		root.setCenter(gridPane);

		BorderPane.setAlignment(circle, Pos.CENTER);
		BorderPane.setMargin(circle, new Insets(0,0,15,0)); // optional
		root.setBottom(circle);

		stage.setTitle("BusyLight");

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
					blinkTimeline.stop();
					tConnectStatus.setFill(javafx.scene.paint.Color.GRAY);
					tConnectStatus.setText("Timed out");
					tAgentStatus.setText("Unknown");
				}
			}
		}));
		timeoutTimeline.setCycleCount(Timeline.INDEFINITE);
		timeoutTimeline.play();
	}

	public void startLs() {
		stopLs();
		tConnectStatus.setFill(javafx.scene.paint.Color.GREEN);
		tConnectStatus.setText("Running");
		tAgentStatus.setText("Unknown");
		buttonStart.setDisable(true);
		buttonStop.setDisable(false);

		server = getHttpsServer();
		if (server == null) {
			buttonStart.setDisable(false);
			buttonStop.setDisable(true);
			tConnectStatus.setFill(javafx.scene.paint.Color.GRAY);
			tConnectStatus.setText("Stopped");
			tAgentStatus.setText("Unknown");			
		} else {
			server.createContext(sPath, new MyHandler());
			server.createContext("/", new MyHandler());
			server.start();
		}

		timeoutTimeline.play();
	}	

	public void stopLs() {
		buttonStart.setDisable(false);
		buttonStop.setDisable(true);
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

	public HttpsServer getHttpsServer() {
		HttpsServer server = null;

		// load certificate
		InputStream is = lightservermini.class.getResourceAsStream("light.jks");
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
						System.err.println("error - exception while initializing SSL context. continuing anyway...");
					}
				}
			});			

			is.close();
		} catch (Exception e) {
			System.err.println("error - exception while creating HTTPS server object. continuing anyway...");
			server = null;
			try {
				is.close();
			} catch (IOException e1) {
				System.err.println("error - exception while closing Input Stream. continuing anyway...");
			}
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
				
				String capStatus = "";
				try {
					capStatus = o.getString("status").substring(0, 1).toUpperCase() + o.getString("status").substring(1);
				} catch (JSONException je) {
					capStatus = "";
				}
				tAgentStatus.setText(capStatus);
				if (o.getBoolean("stop")) {
					blinkTimeline.stop();
					circle.setFill(Color.GRAY);
					circleBlinkOn = false;					
				} else {
					int r = o.getInt("r");
					int g = o.getInt("g");
					int b = o.getInt("b");
					if (o.getBoolean("blink")) {
						//blink
						if (blinkTimeline != null) {
							blinkTimeline.stop();
							circleBlinkOn = false;
							circleBlinkColor = Color.rgb(r,g,b);
							blinkTimeline.play();
						}					
					} else {
						//solid
						circle.setFill(Color.rgb(r,g,b));					
					}
				}

				lastHeard = System.currentTimeMillis();
				tConnectStatus.setFill(javafx.scene.paint.Color.GREEN);
				tConnectStatus.setText("Running");
			} catch (Exception e) {
				System.err.println("error - exception during light processing. continuing anyway...");
				e.printStackTrace();
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