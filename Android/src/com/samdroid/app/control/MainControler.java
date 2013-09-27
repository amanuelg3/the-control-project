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


package com.samdroid.app.control;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.util.Log;

public class MainControler extends Activity implements SensorEventListener {
        
        BluetoothAdapter blueAdapter;
        BluetoothSocket s;
        BluetoothDevice d;
        public boolean CONNECTED, LayoutMade = false;
        public OutputStream out;
        public InputStream in;
        Display dis;
        int Orientation = 1;
        float density = 1;
        String btaddr, cd;
        boolean[] sens = new boolean[9];
        SensorManager sm;
        Sensor acc, gyro, linacc;
        public AlertDialog.Builder builder;
        public String[] msgList;
        float[] prevacc = new float[3];
        float[] matrixR = new float[9];
        float[] matrixI = new float[9];
        float[] matrixValues = new float[3];
        float[] diff = new float[3];
        boolean needsCenter = true;
        MenuItem gyroIcon;
        String name = "Conrtrol";
        boolean doGyro = true; boolean mab = false;
        
        
        public static final int[] avIcons = {R.drawable.av_add_to_queue,R.drawable.av_download,R.drawable.av_fast_forward,R.drawable.av_full_screen,
                R.drawable.av_make_available_offline,R.drawable.av_next,R.drawable.av_pause,R.drawable.av_pause_over_video,R.drawable.av_play,
                R.drawable.av_play_over_video,R.drawable.av_play_pause,R.drawable.av_previous,R.drawable.av_repeat,R.drawable.av_replay,
                R.drawable.av_return_from_full_screen,R.drawable.av_rewind,R.drawable.av_shuffle,R.drawable.av_stop,R.drawable.av_upload,
                R.drawable.av_volume_down,R.drawable.av_volume_up};
        public static final int[] navIcons = {R.drawable.navigation_accept,R.drawable.navigation_back,R.drawable.navigation_cancel,
                R.drawable.navigation_forward,R.drawable.navigation_next_item,R.drawable.navigation_previous_item,R.drawable.navigation_refresh};
        
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu items for use in the action bar
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.main_conroller_actionbar, menu);
            gyroIcon = menu.findItem(R.id.rot_reset);
            if (doGyro == false){gyroIcon.setVisible(false);}
            mab = true;
            return super.onCreateOptionsMenu(menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle presses on the action bar items
            switch (item.getItemId()) {
                case R.id.rot_reset:
                	Log.e("action","bar");
                    diff = matrixValues.clone();
                    Log.e("diff0", Float.toString(diff[0]));
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }
        
        private AdapterView.OnItemClickListener oicL = new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent,View view,int position,long id) {
                        try {
                                Log.i("TaG",""+view.getTag());
                                out.write(((String)view.getTag()+position+"/n").getBytes());
                        } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "Connection Lost", Toast.LENGTH_SHORT).show();
                        }
                }
                
        };
        
        private InfinateSeekBarView.OnSeekStatChangeInterface isbL = new InfinateSeekBarView.OnSeekStatChangeInterface() {
                @Override
                public void onSeekStatChange(View v, int p) {
                        try {
                                out.write(((String)v.getTag()+p+"/n").getBytes());
                        } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "Connection Lost", Toast.LENGTH_SHORT).show();
                        }
                }
        };
        
        private AnalogStickView.OnStickMoveInterface asmL = new AnalogStickView.OnStickMoveInterface() {
                @Override
                public void onMove(View v, int x, int y) {
                        try {
                                out.write(((String)v.getTag()+x+":"+y+"\n").getBytes());
                        } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "Connection Lost", Toast.LENGTH_SHORT).show();
                        }
                }
        };
        
        private OnTouchListener btnDown = new OnTouchListener() { //Button Push Event
                public boolean onTouch(View v,MotionEvent me) {
                if (CONNECTED) {
                        try {
                                out.write(((String) v.getTag()+"1\n").getBytes("UTF-8"));
                        } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "Connection Lost", Toast.LENGTH_SHORT).show();
    }}return false;}};
        
        private OnClickListener btnUp = new OnClickListener() { //Button Release Event
        public void onClick(View v) {
                if (CONNECTED) {
                        try {
                                out.write(((String) v.getTag()+"0\n").getBytes("UTF-8"));
                        } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "Connection Lost", Toast.LENGTH_SHORT).show();
    }}}};
        
    OnSeekBarChangeListener sbL = new OnSeekBarChangeListener() {
        public void onProgressChanged(SeekBar v, int progress, boolean isUser) {
                if (CONNECTED) {
                        try {
                                        out.write(((String)v.getTag()+progress+"\n").getBytes("UTF-8"));
                                } catch (UnsupportedEncodingException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                } catch (IOException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                }
                }
        }
        public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
        }
        public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
        }
    };
    
    CompoundButton.OnCheckedChangeListener tbL = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton v, boolean isChecked) {
            if (isChecked) {
                try {
                                        out.write(((String)v.getTag()+"1\n").getBytes("UTF-8"));
                                } catch (UnsupportedEncodingException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                } catch (IOException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                }
            } else {
                try {
                                        out.write(((String)v.getTag()+"0\n").getBytes("UTF-8"));
                                } catch (UnsupportedEncodingException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                } catch (IOException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                }
            }
        }
    };
    
    public String safeRead() throws IOException {
        int b = in.read();
        while (b != 64) {
                //Log.d("RECV", ""+b);
                b = in.read();
        }
        //Log.d("RECV!!", ""+b);
        byte[] buffer = new byte[4096];
        in.read(buffer);
        String msg = new String(buffer, "UTF-8");
        msg = msg.substring(0,msg.indexOf("@"));
        Log.i("MSG", msg);
        return msg;
        
    }
    
        private void setupMF(final String baddr) {
                try {
                        blueAdapter = BluetoothAdapter.getDefaultAdapter();
                        d = blueAdapter.getRemoteDevice(baddr);
                        s = d.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                        s.connect();
                        out = s.getOutputStream();
                        in = s.getInputStream();
                        out.write(("GL:"+cd).getBytes("UTF-8"));
                        safeRead();
                        Log.i("msg", "Connected Message");
                        setupLayout(true);
                        CONNECTED  = true;
                        Log.i("MF", "Connected");
                }catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
        }
        
        private void setupLayout(boolean sendPrompt) {
                setContentView(R.layout.main_controler);
                try {
                		if (sendPrompt){
                			out.write("SENDLAYOUT".getBytes("UTF-8"));
                		}
                        
                        Orientation = Integer.parseInt(safeRead());
                        setRequestedOrientation(Orientation);//Land = 0  Port = 1
                        out.write("SetOrentation".getBytes("UTF-8"));
                        Log.e("msg", "nowReading");
                        String msg = safeRead();
                        if (msg != "NONE") {
                        String[] insL = msg.split("~");
                                        
                        LinearLayout[] lla = new LinearLayout[32];
                        lla[0] = (LinearLayout)findViewById(R.id.LL1);
                        int nextLL = 1;
                        
                        for (String insR : insL) {
                                Log.i("Split", insR);
                                String[] ins = insR.split("_");
                                Log.i("Split2", ins[1]);
                                if (Integer.parseInt(ins[0]) == 0) {
                                        Button btn = new Button(getApplicationContext());
                                        btn.setText(ins[2]);
                                        btn.setTag(ins[3]);
                                        btn.setOnClickListener(btnUp);
                                        btn.setOnTouchListener(btnDown);
                                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)(Integer.parseInt(ins[4])*density),(int)(Integer.parseInt(ins[5])*density),Float.parseFloat(ins[6]));
                                        btn.setLayoutParams(params);
                                        lla[Integer.parseInt(ins[1])].addView(btn);
                                        Log.i("View", "addBtn");
                                } else if (Integer.parseInt(ins[0]) == 1) {
                                        lla[nextLL] = new LinearLayout(getApplicationContext());
                                        lla[nextLL].setOrientation(Integer.parseInt(ins[2])); //H=0  V=1
                                        lla[nextLL].setGravity(Integer.parseInt(ins[3]));
                                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)(Integer.parseInt(ins[4])*density),(int)(Integer.parseInt(ins[5])*density),Float.parseFloat(ins[6]));
                                        lla[nextLL].setLayoutParams(params);
                                        lla[Integer.parseInt(ins[1])].addView(lla[nextLL]);
                                        nextLL++;
                                        Log.i("View", "addLL");
                                } else if (Integer.parseInt(ins[0]) == 2) {
                                        SeekBar sb = new SeekBar(getApplicationContext());
                                        sb.setMax(1000);
                                        sb.setProgress(Integer.parseInt(ins[2]));
                                        sb.setTag(ins[3]);
                                        sb.setOnSeekBarChangeListener(sbL);
                                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)(Integer.parseInt(ins[4])*density),(int)(Integer.parseInt(ins[5])*density),Float.parseFloat(ins[6]));
                                        sb.setLayoutParams(params);
                                        lla[Integer.parseInt(ins[1])].addView(sb);
                                } else if (Integer.parseInt(ins[0]) == 3) { //0:3 1:parent 2:state 3:on 4:off 5:
                                        ToggleButton tb = new ToggleButton(getApplicationContext());
                                        tb.setTextOn((CharSequence)ins[3]);
                                        tb.setTextOff((CharSequence)ins[4]);
                                        tb.setChecked(Integer.parseInt(ins[2]) == 1);                                   
                                        tb.setTag(ins[5]);
                                        tb.setOnCheckedChangeListener(tbL);
                                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)(Integer.parseInt(ins[6])*density),(int)(Integer.parseInt(ins[7])*density),Float.parseFloat(ins[8]));
                                        tb.setLayoutParams(params);
                                        lla[Integer.parseInt(ins[1])].addView(tb);
                                } else if (Integer.parseInt(ins[0]) == 4) {
                                        AnalogStickView asv = new AnalogStickView(getApplicationContext());
                                        asv.setTag(ins[2]);
                                        asv.setOnStickMoveListener(asmL);
                                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)(Integer.parseInt(ins[3])*density),(int)(Integer.parseInt(ins[4])*density),Float.parseFloat(ins[5]));
                                        asv.setLayoutParams(params);
                                        lla[Integer.parseInt(ins[1])].addView(asv);
                                } else if (Integer.parseInt(ins[0]) == 5) {
                                        InfinateSeekBarView isbv = new InfinateSeekBarView(getApplicationContext());
                                        isbv.setOnSeekStatChangeInterface(isbL);
                                        isbv.setTag(ins[2]);
                                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)(Integer.parseInt(ins[3])*density),(int)(Integer.parseInt(ins[4])*density),Float.parseFloat(ins[5]));
                                        isbv.setLayoutParams(params);
                                        lla[Integer.parseInt(ins[1])].addView(isbv);
                                } else if (Integer.parseInt(ins[0]) == 6) {
                                        ListView lv = new ListView(getApplicationContext());
                                        String tag = ins[2];
                                        lv.setTag(tag);
                                        ArrayAdapter<String> a = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1);
                                        String[] aaL = ins[3].split(",");
                                        for (String item: aaL) {
                                                a.add(item);
                                        }
                                        lv.setAdapter(a);
                                        lv.setOnItemClickListener(oicL);
                                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)(Integer.parseInt(ins[4])*density),(int)(Integer.parseInt(ins[5])*density),Float.parseFloat(ins[6]));
                                        lv.setLayoutParams(params);
                                        lla[Integer.parseInt(ins[1])].addView(lv);
                                }else if (Integer.parseInt(ins[0]) == 7) {
                                        ImageButton btn = new ImageButton(getApplicationContext());
                                        switch(Integer.parseInt(ins[2])) {
                                                case 0:btn.setImageResource(avIcons[Integer.parseInt(ins[3])]);break;
                                                case 1:btn.setImageResource(navIcons[Integer.parseInt(ins[3])]);break;
                                        }
                                        btn.setTag(ins[4]);
                                        btn.setOnClickListener(btnUp);
                                        btn.setOnTouchListener(btnDown);
                                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)(Integer.parseInt(ins[5])*density),(int)(Integer.parseInt(ins[6])*density),Float.parseFloat(ins[7]));
                                        btn.setLayoutParams(params);
                                        lla[Integer.parseInt(ins[1])].addView(btn);
                                }else if (Integer.parseInt(ins[0]) == 8) {
                                		TextView tv = new TextView(getApplicationContext());
                                		tv.setText(ins[2]);
                                		tv.setTextSize(Float.parseFloat(ins[3]));
                                		tv.setTypeface(android.graphics.Typeface.DEFAULT, Integer.parseInt(ins[4])); //1 for Bold, 0 for normal
                                		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)(Integer.parseInt(ins[5])*density),(int)(Integer.parseInt(ins[6])*density),Float.parseFloat(ins[7]));
                                		tv.setLayoutParams(params);
                                		lla[Integer.parseInt(ins[1])].addView(tv);
                                } else {
                                        Log.i("View", "FAKE INSTRUCTION: "+insR);
                        }}}
                        out.write("LayoutMade".getBytes("UTF-8"));
                        String msg2 = safeRead();
                        int i = 0;
                        char[] ca = msg2.toCharArray();
                        for (char c : ca) {
                                if (Integer.parseInt(String.valueOf(c)) == 1) {
                                        sens[i] = true;
                                } else {
                                        sens[i] = false;
                                }
                                i++;
                        }
                        
                        sm = (SensorManager)getSystemService(SENSOR_SERVICE);
                        if (sens[0]|| sens[1] || sens[2]) {
                                acc = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                                Log.i("Sensor","Added Accelerometer");
                        }
                        if (sens[3]|| sens[4] || sens[5]) {
                                gyro = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
                                acc = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                                Log.i("Sensor","Added FakeGyro");
                        } else {
                        	if (mab) {
                        		gyroIcon.setVisible(false);
                        	}
                        	doGyro = false;
                        }
                        if (sens[6]|| sens[7] || sens[8]) {
                        	linacc = sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
                            Log.i("Sensor","Added LinAcc");
                        }
                        LayoutMade = true;
                        out.write("LayoutFullyMade".getBytes("UTF-8"));
                        }catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                }
        }
        
        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.main_controler);
                Log.i("ON","Create");
                
                Bundle b = getIntent().getExtras();
                btaddr = b.getString("btAddr");
                cd = b.getStringArray("portList")[b.getInt("chosenIndex")];
                
                name = b.getStringArray("nameList")[b.getInt("chosenIndex")];
                
                setupMF(btaddr);

                dis = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
                density = this.getResources().getDisplayMetrics().density;
                //Toast.makeText(getApplicationContext(), Float.toString(density), Toast.LENGTH_LONG).show();
        }
        protected void onStop() {
                super.onStop();
                try {
                        out.write("ONSTOP".getBytes());
                } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                finish();
                CONNECTED = false;
        }
        protected void onStart() {
                super.onStart();
                Log.i("ON","Start");
                Log.i("prog","done");
        }
        protected void onResume() {
        super.onResume();
        Log.i("ON","Resume");
        
        setTitle(name);
        getActionBar().setIcon(R.drawable.ic_launcher);
        
        if (sens[0]|| sens[1] || sens[2]) {
                sm.registerListener(this, acc, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (sens[3]|| sens[4] || sens[5]) {
        	sm.registerListener(this, gyro, SensorManager.SENSOR_DELAY_NORMAL);
        	sm.registerListener(this, acc, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (sens[6]|| sens[7] || sens[8]) {
        	sm.registerListener(this, linacc, SensorManager.SENSOR_DELAY_NORMAL);
        }
   }
        
        public void onSaveInstanceState(Bundle sis) {
                  super.onSaveInstanceState(sis);
                  sis.putString("btaddr", btaddr);
        }
        
        public void onRestoreInstanceState(Bundle sis) {
                  super.onRestoreInstanceState(sis);
                  btaddr = sis.getString("btaddr");
        }
        
        protected void onPause() {
        super.onPause();
        if (sm != null) {
                sm.unregisterListener(this);
        }
    }
        
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
                
    }

        public void onDestroy() {
                super.onDestroy();
                try {
                        in.close();
                        out.close();
                        s.close();
                } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                finish();
                CONNECTED = false;
        }
        
    public void onSensorChanged(SensorEvent event) {
        if (CONNECTED) {
                
                        try {
                                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                                        String msg = "Acc";
                                        float x = 0,y = 0;
                                        switch (dis.getRotation()) {
                                        	case Surface.ROTATION_0:
                                        		x = event.values[0];
                                        		y = event.values[1];
                                        		break;
                                        	case Surface.ROTATION_90:
                                        		x = -event.values[1];
                                        		y = event.values[0];
                                        		break;
                                        	case Surface.ROTATION_180:
                                        		x = -event.values[0];
                                        		y = -event.values[1];
                                        		break;
                                        	case Surface.ROTATION_270:
                                        		x = event.values[1];
                                        		y = -event.values[0];
                                        		break;
                                        }
                                        if (sens[0]) {
                                                msg = msg + ":" + x;
                                        }
                                        if (sens[1]) {
                                                msg = msg + ":" + y;
                                        }
                                        if (sens[2]) {
                                                msg = msg + ":" + event.values[2];
                                        }
                                        if (sens[0] | sens[1] | sens[2]) {
                                        	out.write((msg+"\n").getBytes("UTF-8"));
                                        }
                                        prevacc = event.values;
                                }
                                if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                                        String msg1 = "Gyro";
                                        boolean success = false;
                                        //Log.i("GyroData", Float.toString(prevacc[0]));
                                        if (prevacc != null) {
                                        	success = SensorManager.getRotationMatrix(matrixR,matrixI,prevacc,event.values);
                                        	//Log.i("Gyro", Boolean.toString(success));
                                        }
                                        if(success){
                                        	   SensorManager.getOrientation(matrixR, matrixValues);
                                        	   double yaw = Math.toDegrees(matrixValues[0]);
                                        	   double pitch = Math.toDegrees(matrixValues[1]);
                                        	   double roll = Math.toDegrees(matrixValues[2]);
                                        	   if (sens[3]) {
                                               		msg1 = msg1 + ":" + (roll-Math.toDegrees(diff[2]));
                                        	   }
                                        	   if (sens[4]) {
                                        		   msg1 = msg1 + ":" + (pitch-Math.toDegrees(diff[1]));
                                        	   }
                                        	   if (sens[5]) {
                                        		   msg1 = msg1 + ":" + (yaw-Math.toDegrees(diff[0]));
                                        	   }
                                        	   out.write((msg1+"\n").getBytes("UTF-8"));
                                        }
                                }
                                if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
                                	String msg2 = "LinAcc";
                                	float x = 0,y = 0;
                                	switch (dis.getRotation()) {
                                		case Surface.ROTATION_0:
                                			x = event.values[0];
                                			y = event.values[1];
                                			break;
                                		case Surface.ROTATION_90:
                                			x = -event.values[1];
                                			y = event.values[0];
                                			break;
                                		case Surface.ROTATION_180:
                                			x = -event.values[0];
                                			y = -event.values[1];
                                			break;
                                		case Surface.ROTATION_270:
                                			x = event.values[1];
                                			y = -event.values[0];
                                			break;
                                	}
                                	if (sens[6]) {
                                		msg2 = msg2 + ":" + x;
                                	}
                                	if (sens[7]) {
                                        msg2 = msg2 + ":" + y;
                                	}
                                	if (sens[8]) {
                                        msg2 = msg2 + ":" + event.values[2];
                                	}
                                	//Log.i("LINACC", msg);
                                	out.write((msg2+"\n").getBytes("UTF-8"));
                                }
                        } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                        }

        }
    }

}
