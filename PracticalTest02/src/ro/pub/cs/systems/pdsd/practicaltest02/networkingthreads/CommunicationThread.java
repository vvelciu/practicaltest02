package ro.pub.cs.systems.pdsd.practicaltest02.networkingthreads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ro.pub.cs.systems.pdsd.practicaltest02.general.Constants;
import ro.pub.cs.systems.pdsd.practicaltest02.general.Utilities;
import ro.pub.cs.systems.pdsd.practicaltest02.model.WeatherForecastInformation;
import android.util.Log;

public class CommunicationThread extends Thread {
	
	private ServerThread serverThread;
	private Socket       socket;
	HashMap<InetAddress, String> db;
	
	public CommunicationThread(ServerThread serverThread, Socket socket, HashMap<InetAddress, String> db) {
		this.serverThread = serverThread;
		this.socket       = socket;
		this.db = db;
	}
	
	@Override
	public void run() {
		if (socket != null) {
			try {
				BufferedReader bufferedReader = Utilities.getReader(socket);
				PrintWriter    printWriter    = Utilities.getWriter(socket);
				if (bufferedReader != null && printWriter != null) {
					Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client!");

					//TODO parse request
					String reqStr = bufferedReader.readLine();
					
					String [] params = reqStr.split(",");
					String response = "";
					if (params[0].equals("set")) {
						db.put(socket.getInetAddress(), params[1] + " " + params[2]);
						response = "ok\n";
					}
					if (params[0].equals("reset")) {
						db.remove(socket.getInetAddress());
						response = "ok\n";
					}
					if (params[0].equals("poll")) {
						Log.i(Constants.TAG, "[COMMUNICATION THREAD] Poll received");
						HttpClient httpClient = new DefaultHttpClient();
						HttpGet httpGet = new HttpGet("http://www.timeapi.org/utc/now");
						ResponseHandler<String> responseHandler = new BasicResponseHandler();
				        String pageSourceCode = httpClient.execute(httpGet, responseHandler);
				        
				        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd'T'hh:HH:ss'+01:00'");
				        
				        Date date = format1.parse(pageSourceCode);
				        
				        SimpleDateFormat format = new SimpleDateFormat("hh mm");
				        
				        if (!db.containsKey(socket.getInetAddress())) {
				        	response = "none\n";
				        }
				        else {
					        Date alarm = format.parse(db.get(socket.getInetAddress()));
					        Date alarm2 = date;
					        alarm2.setHours(alarm.getHours());
					        alarm2.setMinutes(alarm.getMinutes());
					        
					        if (date.before(alarm2)) {
					        	response = "inactive\n";
					        }
					        else {
					        	response = "active\n";
					        }
				        }
					}
					//TODO write response
					printWriter.append(response);
					printWriter.flush();
				} else {
					Log.e(Constants.TAG, "[COMMUNICATION THREAD] BufferedReader / PrintWriter are null!");
				}
				socket.close();
			} catch (IOException ioException) {
				Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
				if (Constants.DEBUG) {
					ioException.printStackTrace();
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		} else {
			Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
		}
	}

}
