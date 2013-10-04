/*
This file is part of Control.

Control is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Control is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Control.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.samdroid.app.control2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BlueSelecter extends Activity {

	BluetoothAdapter blueAdapter;
	BluetoothSocket s;
	BluetoothDevice d;
	boolean CONNECTED = false;
	OutputStream out;
	TextView textView;
	Bundle b;
	String[] adp, adn;
	String cd;
	ArrayAdapter<String> dArrayAdapter;
	BroadcastReceiver bReceiver;
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.control_ab_blue_selecter , menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.refresh:
            	dArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1); 
  		      	findPairedBluetoothDevices();
  		      	blueAdapter.startDiscovery();
  		      	return true;
            case R.id.help_item:
            	Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://code.google.com/p/the-control-project/wiki/Help"));
            	startActivity(i);
            	return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
	
	protected void onActivityResult (int rqc, int rc, Intent data) {
		if (rqc == 1) {
			if (rc == RESULT_OK) {
				dArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1); 
			    findPairedBluetoothDevices();
			    blueAdapter.startDiscovery();
			} else {
				finish();
			}
		}
	}
	
	public void createDialog(String baddr,Boolean tryA) {
		try {
			blueAdapter = BluetoothAdapter.getDefaultAdapter();
			d = blueAdapter.getRemoteDevice(baddr);
			s = d.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
			s.connect();
			out = s.getOutputStream();
			InputStream in = s.getInputStream();
			//Log.i("Status", "Bluetooth connected");
			byte[] buffer = new byte[4096];
			out.write("CL".getBytes("UTF-8"));
			
			in.read(buffer);
			String msg = new String(buffer, "UTF-8");
			msg = msg.substring(0,msg.indexOf("@"));
			String[] msg2 = msg.split("_"); 
			//Log.i("MSG", msg);
			adn = msg2[0].split("~");
			adp = msg2[1].split("~");
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,adn);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Pick A Controller");
			builder.setAdapter(adapter, new DialogInterface.OnClickListener(){
	               public void onClick(DialogInterface dialog, int which) {
	            	  b.putInt("chosenIndex", which);
	            	  b.putStringArray("portList", adp);
	            	  b.putStringArray("nameList", adn);
					  Intent i = new Intent(getApplicationContext(), MainControler.class);
	      	   		  i.putExtras(b);
	      	   		  try {
	      	   			  unregisterReceiver(bReceiver);
	      	   		  } catch (Exception e) {
	      	   			  e.printStackTrace();
	      	   		  }
	      	   		  startActivity(i);
	           }
			});
			AlertDialog ad = builder.create();
			ad.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (tryA) {
				createDialog(baddr,false);
			} else {
				Toast.makeText(getApplicationContext(), "Unable to get controllers", Toast.LENGTH_LONG).show();
			}
		}
		
	    
	}
	
	private void findPairedBluetoothDevices() {
		 Set<BluetoothDevice> pairedDevices = blueAdapter.getBondedDevices();
		 ListView lv = (ListView) findViewById(R.id.listView1);
		 if (pairedDevices.size() > 0) {
		   for (BluetoothDevice device : pairedDevices) {
		     dArrayAdapter.add(device.getName() + "\nPaired - " + device.getAddress());
		     lv.setAdapter(dArrayAdapter);
		   }
		 }
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.blue_selecter);
		
		blueAdapter = BluetoothAdapter.getDefaultAdapter();
		  if (blueAdapter == null) {
		    Toast.makeText(this, "No bluetooth chip found!", Toast.LENGTH_LONG).show();
		    finish();
		    return;
		  }
		  
		  if (!blueAdapter.isEnabled()) {
			    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			    startActivityForResult(enableBtIntent, 1);
			}
		  
		  dArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1); 
		  bReceiver = new BroadcastReceiver() {
			    public void onReceive(Context context, Intent intent) {
			        String action = intent.getAction();
			        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
			        	ListView lv = (ListView) findViewById(R.id.listView1);
			            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			            if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
			            	dArrayAdapter.add(device.getName() + "\nVisible - " + device.getAddress());
			            }
			            lv.setAdapter(dArrayAdapter);
			        }
			    }
			};
			IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		    registerReceiver(bReceiver, filter);
		  blueAdapter.startDiscovery();
		  findPairedBluetoothDevices();
			
		  
		  ListView pairedDevicesListView = (ListView) findViewById(R.id.listView1);
		  pairedDevicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			  public void onItemClick(AdapterView<?> parent,View view,int position,long id) {
				  blueAdapter.cancelDiscovery();
				  String device[] = (((String)parent.getItemAtPosition(position)).split("\n"))[1].split("-");
				  BluetoothDevice chosenDevice = blueAdapter.getRemoteDevice(device[1].substring(1));
				  b = new Bundle();
				  b.putString("btAddr", chosenDevice.getAddress());
				  createDialog(chosenDevice.getAddress(),true);
			  }
		});
	}

}
