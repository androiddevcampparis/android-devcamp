package com.pingme;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

public class CredentialActivity extends FragmentActivity {

	 static final int NUM_ITEMS = 2;
	    MyAdapter mAdapter;
	    ViewPager mPager;

	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_credential);

	        mAdapter = new MyAdapter(getSupportFragmentManager());

	        mPager = (ViewPager)findViewById(R.id.pager);
	        mPager.setAdapter(mAdapter);
	    }

	    public static class MyAdapter extends FragmentPagerAdapter {
	        public MyAdapter(FragmentManager fm) {
	            super(fm);
	        }

	        @Override
	        public int getCount() {
	            return NUM_ITEMS;
	        }

	        @Override
	        public Fragment getItem(int position) {
	            return ArrayListFragment.newInstance(position);
	        }
	        
	        
	    }

	    public static class ArrayListFragment extends Fragment {
	        int mNum;

	        
	        /**
	         * Create a new instance of CountingFragment, providing "num"
	         * as an argument.
	         */
	        static ArrayListFragment newInstance(int num) {
	            ArrayListFragment f = new ArrayListFragment();

	            // Supply num input as an argument.
	            Bundle args = new Bundle();
	            args.putInt("num", num);
	            f.setArguments(args);

	            return f;
	        }

	        /**
	         * When creating, retrieve this instance's number from its arguments.
	         */
	        @Override
	        public void onCreate(Bundle savedInstanceState) {
	            super.onCreate(savedInstanceState);
	            mNum = getArguments() != null ? getArguments().getInt("num") : 1;
	        }

	        /**
	         * The Fragment's UI is just a simple text view showing its
	         * instance number.
	         */
	        @Override
	        public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                Bundle savedInstanceState) {
	        	View v = null;
	        	switch (mNum){
	        	
	        	case 0 : 
	        		v = inflater.inflate(R.layout.first_fragment, container, false);
	        		break;
	        		
	        	case 1 : 
	        		v = inflater.inflate(R.layout.second_fragment, container, false);
	        		

	        		ImageView paug = (ImageView)v.findViewById(R.id.logopaug);
	        		ImageView datagouv = (ImageView)v.findViewById(R.id.datagouv);
	        		ImageView datapublica = (ImageView)v.findViewById(R.id.datapublica);
	        		ImageView opendatasoft = (ImageView)v.findViewById(R.id.opendatasoft);
	        		ImageView parisdata = (ImageView)v.findViewById(R.id.parisdata);
	        		
	        		paug.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_paug)));
							startActivity(myIntent);
						}
					});
	        		
	        		datagouv.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_datagouv)));
							startActivity(myIntent);
						}
					});
	        		
	        		datapublica.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_datapublica)));
							startActivity(myIntent);
						}
					});
	        		
	        		opendatasoft.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_datasoft)));
							startActivity(myIntent);
						}
					});
	        		

	        		parisdata.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_dataparis)));
							startActivity(myIntent);
						}
					});

	        		break;
	        		
	        	case 2 : 
//	        		Intent i1 = new Intent(getActivity(), ConfigActivity.class);
//	        		i1.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//	        		getActivity().startActivity(i1);
//	        		getActivity().finish();
	        		break;
	        	}
	            return v;
	        }
	    }
	}
