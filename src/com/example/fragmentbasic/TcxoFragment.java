package com.example.fragmentbasic;

import java.util.HashMap;

import com.example.fragmentbasic.MyServiceTest.TcxoReceiver;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class TcxoFragment extends Fragment {
	private static final String TAG = "kent_TcxoFragment";
	private Button start, stop;
	private TextView time, info;
	private EditText edit_time;
	private int time_request;
	private Context mContext;
	private View rootView;
	private TcxoReceiver mTcxoReceiver;
	private StringBuilder mSB;

	// Returns a new instance of this fragment for the given section number.
	public static TcxoFragment newInstance() {
		TcxoFragment fragment = new TcxoFragment();
		return fragment;
	}

	public TcxoFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_tcxo, container, false);
		mContext = container.getContext();
		mSB = new StringBuilder();
		init();
		startService();
		mTcxoReceiver = new TcxoReceiver();
		IntentFilter mIntentFilter = new IntentFilter();
		mIntentFilter.addAction("project.action.fix.location");
		mContext.registerReceiver(new TcxoReceiver(), mIntentFilter);

		start.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "start");
				if (edit_time.getText() != null) {
					time_request = Integer.parseInt(edit_time.getText().toString());
				}
				Intent intent = new Intent("project.action.start.gps");
				intent.putExtra("Time", time_request);
				mContext.sendBroadcast(intent);
			}
		});

		stop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "stop");
				Intent intent = new Intent("project.action.stop.gps");
				mContext.sendBroadcast(intent);
			}
		});
		
		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((FragmentMain) activity).onSectionAttached(3);
	}

	public void init() {
		start = (Button) rootView.findViewById(R.id.start);
		stop = (Button) rootView.findViewById(R.id.stop);
		time = (TextView) rootView.findViewById(R.id.time);
		info = (TextView) rootView.findViewById(R.id.info);
		edit_time = (EditText) rootView.findViewById(R.id.edit_time);
		info.setText("Gps information:");
	
	}
	
	public void startService() {
		Log.d(TAG, "startService");
		getActivity().startService(new Intent(getActivity(), MyServiceTest.class));
	}
	
	class TcxoReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Log.d(TAG, "Tcxo onReceiver");
			Bundle bundle = intent.getExtras();
			int counter = bundle.getInt("Counter", 0);
			double Lat = bundle.getDouble("Lat", 0);
			double Long = bundle.getDouble("Long", 0);
			double acc = bundle.getDouble("Accuracy", 0);
			mSB.append("Counter = " + counter + "\t" + 
			             "Lat: " + Lat + "\t" + 
			             "Long: " + Long + "\t" +
			             "Acc: " + acc + "\n");
			
			info.setText(mSB);
			
		}
	}
}
