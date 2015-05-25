package ro.pub.cs.systems.pdsd.practicaltest02.graphicuserinterface;

import ro.pub.cs.systems.pdsd.practicaltest02.R;
import ro.pub.cs.systems.pdsd.practicaltest02.general.Constants;
import ro.pub.cs.systems.pdsd.practicaltest02.networkingthreads.ClientThread;
import ro.pub.cs.systems.pdsd.practicaltest02.networkingthreads.ServerThread;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class PracticalTest02MainActivity extends Activity {
	
	// TODO Server widgets
	private EditText serverPort = null;
	private Button connectButton = null;
	
	// TODO Client widgets
	private EditText clientAddress = null;
	private EditText clientPort = null;
	private EditText clientMinute = null;
	private EditText clientHour = null;
	private Spinner clientReqType = null;
	private Button sendBtn = null;
	private TextView responseView = null;
	
	private ServerThread serverThread             = null;
	private ClientThread clientThread             = null;
	
	private ConnectButtonClickListener connectButtonClickListener = new ConnectButtonClickListener();
	private class ConnectButtonClickListener implements Button.OnClickListener {
		
		@Override
		public void onClick(View view) {
			String serverPortStr = serverPort.getText().toString();
			if (serverPortStr == null || serverPortStr.isEmpty()) {
				Toast.makeText(
					getApplicationContext(),
					"Server port should be filled!",
					Toast.LENGTH_SHORT
				).show();
				return;
			}
			
			serverThread = new ServerThread(Integer.parseInt(serverPortStr));
			if (serverThread.getServerSocket() != null) {
				serverThread.start();
			} else {
				Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not creat server thread!");
			}
			
		}
	}
	
	private ButtonClickListener buttonClickListener = new ButtonClickListener();
	private class ButtonClickListener implements Button.OnClickListener {
		
		@Override
		public void onClick(View view) {
			String clientAddressStr = clientAddress.getText().toString();
			String clientPortStr    = clientPort.getText().toString();
			if (clientAddressStr == null || clientAddressStr.isEmpty() ||
					clientPortStr == null || clientPortStr.isEmpty()) {
				Toast.makeText(
					getApplicationContext(),
					"Client connection parameters should be filled!",
					Toast.LENGTH_SHORT
				).show();
				return;
			}
			
			if (serverThread == null || !serverThread.isAlive()) {
				Log.e(Constants.TAG, "[MAIN ACTIVITY] There is no server to connect to!");
				return;
			}
			
			//TODO get request info from UI
			String hour = clientHour.getText().toString();
			String minute = clientMinute.getText().toString();
			String typeReq = clientReqType.getSelectedItem().toString();
			
			clientThread = new ClientThread(
					clientAddressStr,
					Integer.parseInt(clientPortStr),
					hour,
					minute,
					typeReq, responseView);
			clientThread.start();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_practical_test02_main);
		
		//TODO init widgets
		serverPort = (EditText) findViewById(R.id.server_port_edit_text);
		connectButton = (Button) findViewById(R.id.connect_button);
		connectButton.setOnClickListener(connectButtonClickListener);
		
		clientAddress = (EditText) findViewById(R.id.client_address_edit_text);
		clientPort = (EditText) findViewById(R.id.client_port_edit_text);
		clientMinute = (EditText) findViewById(R.id.editText1);
		clientHour = (EditText) findViewById(R.id.city_edit_text);
		
		clientReqType = (Spinner)findViewById(R.id.information_type_spinner);
		sendBtn = (Button)findViewById(R.id.get_weather_forecast_button);
		sendBtn.setOnClickListener(buttonClickListener);
		responseView = (TextView)findViewById(R.id.weather_forecast_text_view);
	}
	
	@Override
	protected void onDestroy() {
		if (serverThread != null) {
			serverThread.stopThread();
		}
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.practical_test02_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
