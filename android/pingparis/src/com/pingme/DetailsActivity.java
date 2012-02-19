package com.pingme;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.pingme.model.POI_Data;
import com.pingme.utils.ImageDownloader;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DetailsActivity extends Activity {
	
	private static String INTENT_DATA = "data";
	private POI_Data poiData;
	public Button plusUnBtn;

	 private static final int DIALOG_ACCOUNTS = 0;
	 protected static final String AUTH_TOKEN_TYPE = "";
	 Context context; 
	    
	    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		context=this;          
		
		// get data from intent and set To View
		poiData = (POI_Data) getIntent().getSerializableExtra(INTENT_DATA);
		
		final TextView title = (TextView) findViewById(R.id.titleEvent);
		final TextView descr = (TextView) findViewById(R.id.descrEvent);
		final ImageView image = (ImageView) findViewById(R.id.imageEvent);
		plusUnBtn = (Button) findViewById(R.id.plusUnButton);
		
		title.setText(poiData.getTitle());
		descr.setText(poiData.getDescr());
		
		new ImageDownloader(this).download(poiData.getUrlImage(), image, null, "DetailsActivity");
		
		
		plusUnBtn.setOnTouchListener(new OnTouchListener() {
		        @Override
		        public boolean onTouch(View v, MotionEvent event) {
		            if(event.getAction()==MotionEvent.ACTION_DOWN) return true;
		            if(event.getAction()!=MotionEvent.ACTION_UP) return false;
		            gotAccount(false);
		            if (plusUnBtn.isPressed()){
		            	plusUnBtn.setPressed(false);
		            }else{
		            	plusUnBtn.setPressed(true); 
		            }
		            return true;
		        }
		    });
	}
	
	
	
	/**
	 * Get the Intent for notification to launch the DetailsActivity
	 * @param context
	 * @param data
	 * @return
	 */
	public static PendingIntent getMyLauncher(Context context, POI_Data data){
		Intent intent = new Intent(context, DetailsActivity.class);
		intent.putExtra(INTENT_DATA, data);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		return contentIntent;
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
}
