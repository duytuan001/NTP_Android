package com.duytuan.ntptimesync;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.duytuan.ntptimesync.sntp.NtpMessage;
import com.duytuan.ntptimesync.ultils.Network;


public class MainActivity extends ActionBarActivity {
	
	private static final int TIMEOUT = 10000;
	private String ntpServerName;
	private boolean isAnalogClock;
	
	//Clock
	private long ntpTime;
	private long baseTime;
	
	private int timer;
	private CountDownTimer cdtimer;
	private int seconds;
	private int minutes;
	private int hours;
	private boolean timeChanged;
	
	private com.duytuan.ntptimesync.view.AnalogClock analogClock;
	private com.duytuan.ntptimesync.view.DigitalClock digitalClock;
	private TextView tvCountDownTimer;
	  
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Init params
        Settings settings 	= (Settings)getApplicationContext();
        ntpServerName 		= settings.getNtpServerName();
        timer 				= settings.getTimer();
        isAnalogClock		= settings.isAnalogClock();
        Log.e("", String.valueOf(timer));
        
        // Init View
        initView();
        
        // Sync first
        new SNTPClient().execute(ntpServerName);
    }

    private void initView(){
    	analogClock = 
    			(com.duytuan.ntptimesync.view.AnalogClock)findViewById(R.id.analog_clock);
    	digitalClock = 
				(com.duytuan.ntptimesync.view.DigitalClock)findViewById(R.id.digital_clock);
    	tvCountDownTimer = (TextView)findViewById(R.id.tv_count_down_timer);
    	
    	Button btnSync = (Button) findViewById(R.id.btn_sync);
    	btnSync.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				new SNTPClient().execute(ntpServerName);
			}
		});
    	
    	showNtpServerName();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
        case R.id.action_select_server:
        	showDialogChoiceServers();
        	break;
        case R.id.action_select_timer:
        	showDialogSetTimer();
        	break;
        case R.id.action_settings:
        	break;
        case R.id.action_sync:
        	new SNTPClient().execute(ntpServerName);
        	break;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    /**
     * Show dialog to choice servers
     * @param title
     * @param items
     * @param selected
     */
    private void showDialogChoiceServers() {
    	Settings settings 	= (Settings)getApplicationContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.action_select_server));

        builder.setSingleChoiceItems(
                Settings.ntpServerList, 
                settings.getNtpServerIndex(), 
                new DialogInterface.OnClickListener() {
             
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        })
        .setCancelable(false)
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				ListView lw = ((AlertDialog)dialog).getListView();
            	Settings settings 	= (Settings)getApplicationContext();
				settings.setNtpServerIndex(lw.getCheckedItemPosition());
				ntpServerName = settings.getNtpServerName();
				showNtpServerName();
				// sync again
				new SNTPClient().execute(ntpServerName);
			}
		});
        
        
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    
    private void showDialogSetTimer(){
    	int hour 			= (int) timer/60;
    	int minute 			= timer - hour*60;
    	
    	TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
            	Settings settings 	= (Settings)getApplicationContext();
            	timer = selectedHour*60 + selectedMinute;
            	settings.setTimer(timer);
            	// reset timer
				if(cdtimer != null){
		        	cdtimer.cancel();
		        }
		        cdtimer = new CountDownTimerToSync(timer*60*1000, 1000).start();
            }
        }, hour, minute, true);
        mTimePicker.setTitle("Sync after:");
        mTimePicker.show();
    }
    
    private void showNtpServerName(){
    	TextView tvNtpServer = (TextView)findViewById(R.id.tv_ntpserver);
    	tvNtpServer.setText("NTP Server:\n" + ntpServerName);
    }
    
    /**
     * 
     * @param ms
     */
    private void runClocks(long ms){
    	
    	runDigitalClock(ms);
    	analogClock.setTime(ms);
		if(cdtimer != null){
        	cdtimer.cancel();
        }
        
		if(cdtimer != null){
        	cdtimer.cancel();
        }
		cdtimer = new CountDownTimerToSync(timer*60*1000, 1000).start();

    }
    
    private Thread t;
    public void runDigitalClock(long miliseconds){
	   	seconds = (int) ((miliseconds / 1000) % 60);
	   	minutes = (int) ((miliseconds / (1000*60)) % 60);
	   	hours   = (int) ((miliseconds / (1000*60*60)) % 24);

	   	timeChanged = false;
	   	t = new Thread() {
      	  @Override
      	  public void run() {
      	    try {
      	      while (!timeChanged) {
      	        Thread.sleep(1000);
      	        runOnUiThread(new Runnable() {
      	          @Override
      	          public void run() {
      	        	long newTime = SystemClock.elapsedRealtime();
      	        	ntpTime = ntpTime + newTime - baseTime;
      	        	
      	        	seconds++;
    				if(seconds == 60){
    					seconds = 0;
    					minutes++;
    					if(minutes == 60){
    						hours++;
    						if(hours == 24){
    							hours = 0;
    						}
    					}
    				}
    				digitalClock.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
      	          }
      	        });
      	      }
      	    } catch (InterruptedException e) {
      	    }
      	  }
      	};

      	t.start();
      	
	}    
    
    /**
     * retrieve SNTP Time
     * @param params
     * @return
     * @throws SocketException
     * @throws UnknownHostException
     * @throws IOException
     */
    private double retrieveSNTPTime(String... params) throws SocketException,	UnknownHostException, IOException {
		String serverName = params[0];
		DatagramSocket socket = new DatagramSocket();
		InetAddress serverAddress = InetAddress.getByName(serverName);
		byte[] buffer = new NtpMessage().toByteArray();
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, 123);
		
		NtpMessage.encodeTimestamp(packet.getData(), 40,
				(System.currentTimeMillis()/1000.0) + 2208988800.0);
		
		socket.send(packet);
		
		packet = new DatagramPacket(buffer, buffer.length);
		
		socket.receive(packet);
		
		// Process response
		NtpMessage message = new NtpMessage(packet.getData());
		
		// Display response
		Log.i("SNTP", "NTP server: " + serverName);
		Log.i("SNTP", message.toString());
		
		socket.close();
		
		return message.transmitTimestamp;
	}
	
    /**
     * SNTPClient
     * @author DuyTuan
     *
     */
	private class SNTPClient extends AsyncTask<String, Void, Integer>{
		private ProgressDialog progress = null;
		private double ntpTime = 0;
		
		@Override
		protected Integer doInBackground(String... params) {
			try {
				ntpTime = retrieveSNTPTime(params);
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return null;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Integer result) {
			TextView textSystemTime = (TextView)findViewById(R.id.tv_system_time);
			TextView textNtpTime = (TextView)findViewById(R.id.tv_ntp_time);
			timeChanged = true;
			if(progress != null){
				double utc = ntpTime - (2208988800.0);
				
				// milliseconds
				long ms = (long) (utc * 1000.0);
				
				//run clocks and timer
				runClocks(ms);
				
				// date/time
				String date = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").format(new Date(ms));
				
				// fraction
				double fraction = ntpTime - ((long) ntpTime);
				String fractionSting = new DecimalFormat(".000000").format(fraction);
				
				textSystemTime.setText("System Time:\n" + new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss.S").format(new Date()));
				textNtpTime.setText("NTP Time:\n" + date + fractionSting);
			
				progress.dismiss();
				progress = null;
			}else{
				textSystemTime.setText("System Time:\n" + new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss.S").format(new Date()));
				textNtpTime.setText("NTP Time:\n-- Not connected Network");
				
				if(cdtimer != null){
		        	cdtimer.cancel();
		        }
		        cdtimer = new CountDownTimerToSync(timer*60*1000, 1000).start();
			}
			
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			Network network 	= new Network(getApplicationContext());
	        if(!network.isOnline()){
	        	network.showNetworkStatusDialog(MainActivity.this);
	        }else{
	        	progress = new ProgressDialog(MainActivity.this);
				
				progress.setMessage(getResources().getString(R.string.sync_progress));
				progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				
				progress.show();
	        }
			
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}
	}
	
	/**
	 * Count down timer to sync NTP Time
	 * @author DuyTuan
	 *
	 */
	public class CountDownTimerToSync extends CountDownTimer {

		int seconds;
		int minutes;
		int hours;
		
        public CountDownTimerToSync(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
        }

        @Override
        public void onTick(long millisUntilFinished) {
        	seconds = (int) (millisUntilFinished / 1000) % 60 ;
	    	minutes = (int) ((millisUntilFinished / (1000*60)) % 60);
	    	hours   = (int) ((millisUntilFinished / (1000*60*60)) % 24);
	    	tvCountDownTimer.setText("Sync Time Remaining:\n" + String.format("%02d:%02d:%02d ", hours, minutes, seconds));
        }
    }

}
