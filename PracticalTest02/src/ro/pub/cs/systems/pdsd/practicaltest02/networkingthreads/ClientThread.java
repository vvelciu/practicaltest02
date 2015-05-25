package ro.pub.cs.systems.pdsd.practicaltest02.networkingthreads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import ro.pub.cs.systems.pdsd.practicaltest02.general.Constants;
import ro.pub.cs.systems.pdsd.practicaltest02.general.Utilities;
import android.util.Log;
import android.widget.TextView;

public class ClientThread extends Thread {
	
	private String   address;
	private int      port;
	
	private Socket   socket;
	
	private String hour, minute, typeReq;
	private TextView responseView;
	
	public ClientThread(
			String address,
			int port,
			String hour,
			String minute,
			String typeReq,
			TextView responseView
			) {
		this.address                 = address;
		this.port                    = port;
		
		this.hour = hour;
		this.minute = minute;
		this.typeReq = typeReq;
		this.responseView = responseView;
	}
	
	@Override
	public void run() {
		try {
			socket = new Socket(address, port);
			if (socket == null) {
				Log.e(Constants.TAG, "[CLIENT THREAD] Could not create socket!");
			}
			
			BufferedReader bufferedReader = Utilities.getReader(socket);
			PrintWriter    printWriter    = Utilities.getWriter(socket);
			if (bufferedReader != null && printWriter != null) {
				//TODO write request
				if (typeReq.equals("set")) {
					printWriter.append("set," + hour + "," + minute + "\n");
					printWriter.flush();
				}
				if (typeReq.equals("reset")) {
					printWriter.append("reset\n");
					printWriter.flush();
				}
				if (typeReq.equals("poll")) {
					printWriter.append("poll\n");
					printWriter.flush();
				}
				
				//TODO read response
				final String req  = bufferedReader.readLine();
				responseView.post(new Runnable() {

					@Override
					public void run() {
						responseView.setText(req);
					}
				});
				
			} else {
				Log.e(Constants.TAG, "[CLIENT THREAD] BufferedReader / PrintWriter are null!");
			}
			socket.close();
		} catch (IOException ioException) {
			Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
			if (Constants.DEBUG) {
				ioException.printStackTrace();
			}
		}
	}

}
