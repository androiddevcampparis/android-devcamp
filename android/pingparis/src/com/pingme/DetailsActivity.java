package com.pingme;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pingme.adapters.ActionsAdapter;
import com.pingme.adapters.ImageAdapter;
import com.pingme.model.ActionsDetail;
import com.pingme.model.POIData;
import com.pingme.service.DownloadAsyncTask;
import com.pingme.service.DownloaderCallback;
import com.pingme.service.WikipediaAsyncTask;
import com.pingme.utils.Utils;

public class DetailsActivity extends Activity implements DownloaderCallback{
	
	public Button plusUnBtn;
	private POIData poiData;
	private static final int DIALOG_ACCOUNTS = 0;
	protected static final String AUTH_TOKEN_TYPE = "";
	Context context; 
	private Gallery gallery;
	    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		context=this;          


		final TextView titleTopbar = (TextView) findViewById(R.id.titleBar);		
		titleTopbar.setText(getString(R.string.detail_place));
		
		// get data from intent and set To View
		poiData = (POIData) getIntent().getSerializableExtra(PingMeService.INTENT_POI_DATA_EXTRA);
		
		final TextView title = (TextView) findViewById(R.id.titleEvent);
		final TextView descr = (TextView) findViewById(R.id.descrEvent);
		final TextView plusUnSum = (TextView)findViewById(R.id.plusUnSum);
		plusUnBtn = (Button) findViewById(R.id.plusUnButton);
		
		title.setText(poiData.getTitle());
		descr.setText(poiData.getDescription());
		plusUnSum.setText( Integer.toString(poiData.getPlusSum()) );
		Log.v("DetailsActivity", "-->" + poiData.isPlus() + " / " + poiData.getPlusSum() );
		plusUnBtn.setPressed( poiData.isPlus() );
		
		plusUnBtn.setOnTouchListener(new OnTouchListener() {
	        @Override
	        public boolean onTouch(View v, MotionEvent event) {
	            if(event.getAction()==MotionEvent.ACTION_DOWN) return true;
	            if(event.getAction()!=MotionEvent.ACTION_UP) return false;
	            gotAccount(false);
	            
	            
	            if (plusUnBtn.isPressed()){
	            	poiData.setPlusSum( poiData.getPlusSum()-1 );
	            	plusUnBtn.setPressed(false);
	            }else{
	            	poiData.setPlusSum( poiData.getPlusSum()+1 );
	            	plusUnBtn.setPressed(true); 
	            }
	            
        		plusUnSum.setText( Integer.toString( poiData.getPlusSum() ) );
	            poiData.setPlus( plusUnBtn.isPressed() );
	            
	    		Intent serviceIntent = new Intent( PingMeService.PING_ACTION_POI_DATA_UPDATE );
	    		serviceIntent.setClassName( context, "com.pingme.PingMeService" );
	    		serviceIntent.putExtra( PingMeService.INTENT_POI_DATA_EXTRA, poiData.copy() );	    		
    			context.startService( serviceIntent );			

	    		return true;
	        }
	    });

		//Add image or search if does not exist
//		if(Utils.isEmpty(poiData.getUrl_image())){
//			new DownloadAsyncTask(this, poiData).execute(null);
//		} else{
//			final ImageView image = (ImageView) findViewById(R.id.imageEvent);
//			PingMeApplication.getImageDownloader().download(poiData.getUrl_image(), image, null, "DetailsActivity");
//		}
		
		gallery = (Gallery) findViewById(R.id.gallery);
		if(poiData.getUrlsImages() == null){
			new DownloadAsyncTask(this, poiData).execute(null);
		} else{
			gallery.setAdapter(new ImageAdapter(this, poiData));
		}

	    gallery.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView parent, View v, int position, long id) {
	            Intent intent = new Intent(DetailsActivity.this, ZoomImageActivity.class);
	            intent.putExtra(ZoomImageActivity.EXTRA, poiData);
	            intent.putExtra(ZoomImageActivity.EXTRA_POS, position);
	            startActivity(intent);
	        }
	    });
		
		//Adapter to list of actions
//        getListView().setSelector(R.drawable.highlight_pressed);
//        setListAdapter(new ActionsAdapter(poiData));
	    ViewGroup listView = (ViewGroup) findViewById(R.id.listAction);
	    List<ActionsDetail> actions = poiData.getActions();
	    boolean first = true;
	    
	    for(final ActionsDetail action: actions){
	    	if(!first){
	    		getLayoutInflater().inflate(R.layout.separator, listView);
	    	}
	    	View view = getLayoutInflater().inflate(R.layout.item_action, null);
	    	listView.addView(view);
	    	
	    	ImageView image = (ImageView) view.findViewById(R.id.imageItem);
			image.setImageResource(action.getIdRes());
			
			TextView text = (TextView) view.findViewById(R.id.textItem);
			text.setText(action.getName());
			
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					action.execute(DetailsActivity.this);
				}
			});
	    }
        
        if(Utils.isEmpty(poiData.getWiki_url())){
        	
        	//Load wikipedia URL
			new WikipediaAsyncTask(new DownloaderCallback() {
			@Override
			public void onError(int code) {
				
			}
			
			@Override
			public void loadingFinished(List<Object> datas) {
				//setListAdapter(new ActionsAdapter(poiData));
			}
		}, poiData).execute(null);
        }
        
        //Reset Location notif to Main Notif
        if( PingMeApplication.getServiceStatus() && getIntent().getExtras().getBoolean(PingMeService.INTENT_IS_NOTIF_EXTRA, false) ){
        	 PingMeApplication.createNotifConfig(this);
        }
	}

//	@Override
//	protected void onListItemClick(ListView l, View v, int position, long id) {
//		try {
//			ActionsDetail details = poiData.getActions().get(position);
//			details.execute(this);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	
	/**
	 * Get the Intent for notification to launch the DetailsActivity
	 * @param context
	 * @param data
	 * @return
	 */
	public static PendingIntent getMyLauncher(Context context, POIData data){
		Intent intent = new Intent(context, DetailsActivity.class);
		intent.putExtra(PingMeService.INTENT_POI_DATA_EXTRA, data);
		intent.putExtra(PingMeService.INTENT_IS_NOTIF_EXTRA, true);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		return contentIntent;
	}

	@Override
	public void loadingFinished(List<Object> datas) {
		if(datas == null || datas.size()==0){
			Log.w("DetailsActivity", "Image from google images are unset");
			return;
		}
		
		gallery.setAdapter(new ImageAdapter(this, poiData));
		//final ImageView image = (ImageView) findViewById(R.id.imageEvent);
		//PingMeApplication.getImageDownloader().download((String) datas.get(0), image, null, "DetailsActivity");
	}

	@Override
	public void onError(int code) {
	}
	
     @Override
     protected Dialog onCreateDialog(int id) {
       switch (id) {
         case DIALOG_ACCOUNTS:
           AlertDialog.Builder builder = new AlertDialog.Builder(this);
           builder.setTitle("Select a Google account");
           final AccountManager manager = AccountManager.get(this);
           final Account[] accounts = manager.getAccountsByType("com.google");
           final int size = accounts.length;
           String[] names = new String[size];
           for (int i = 0; i < size; i++) {
             names[i] = accounts[i].name;
           }
           builder.setItems(names, new DialogInterface.OnClickListener() {
             public void onClick(DialogInterface dialog, int which) {
               gotAccount(manager, accounts[which]);
             }
           });
           return builder.create();
       }
       return null;
     }

     public String md5(String s) {
    	    try {
    	        // Create MD5 Hash
    	        MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
    	        digest.update(s.getBytes());
    	        byte messageDigest[] = digest.digest();
    	        
    	        // Create Hex String
    	        StringBuffer hexString = new StringBuffer();
    	        for (int i=0; i<messageDigest.length; i++)
    	            hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
    	        return hexString.toString();
    	        
    	    } catch (NoSuchAlgorithmException e) {
    	        e.printStackTrace();
    	    }
    	    return "";
    	}
     
     private void gotAccount(boolean tokenExpired) {
       SharedPreferences settings = getSharedPreferences("test", 0);
       String accountName = settings.getString("accountName", null);
       if (accountName != null) {
         AccountManager manager = AccountManager.get(this);
         Account[] accounts = manager.getAccountsByType("com.google");
         int size = accounts.length;
         for (int i = 0; i < size; i++) {
           Account account = accounts[i];
           if (accountName.equals(account.name)) {
        	   if (plusUnBtn.isPressed()){
                   Toast.makeText(this,"-1 "+account.name , Toast.LENGTH_SHORT).show(); 
                   sendData(poiData.getId()+md5(account.name)+true);
        	   }else{
        		   Toast.makeText(this,"+1 "+account.name , Toast.LENGTH_SHORT).show();
        		   sendData(poiData.getId()+md5(account.name)+false);
        	   }
                plusUnBtn.setSelected(true);
             gotAccount(manager, account);
             return;
           }
         }
       }
       showDialog(DIALOG_ACCOUNTS);
     }

     private void gotAccount(final AccountManager manager, final Account account) {
       SharedPreferences settings = getSharedPreferences("test", 0);
       SharedPreferences.Editor editor = settings.edit();
       editor.putString("accountName", account.name);
       editor.commit();            
     }
     
     private void sendData(String s){
    	 
    	 
     }
     
     // ----------------------------------------------------------------------------
 	// Menu
     // ----------------------------------------------------------------------------
     
     @Override
 	public boolean onOptionsItemSelected(MenuItem item) {
 		if (item.getTitle().equals(getString(R.string.menu_configure))) {
 			Intent intent = new Intent(this, ConfigActivity.class);
 			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
 			startActivity(intent);
 			return true;
 		}
 		return false;
 	}
 	
 	@Override
 	public boolean onPrepareOptionsMenu(Menu menu) {
 		menu.clear();
 		menu.add(R.string.menu_configure).setIcon(android.R.drawable.ic_menu_preferences);
 		return super.onPrepareOptionsMenu(menu);
 	}


}
