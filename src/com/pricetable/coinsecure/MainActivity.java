package com.pricetable.coinsecure;

import com.pricetable.coinsecure.R;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
 
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
 
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
 
public class MainActivity extends ListActivity {
	
    private ProgressDialog pDialog;
 
    // URL to get prices JSON
    private static String url = "http://coinsecure.in/api/app_prices.json";
 
    // JSON Node names
    private static final String TAG_PRICES = "prices";
    private static final String TAG_TITLE = "title";
    private static final String TAG_BUY = "buy";
    private static final String TAG_SELL = "sell";
    
    // prices JSONArray
    JSONArray prices = null;
 
    // Hashmap for ListView
    ArrayList<HashMap<String, String>> pricesList;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        pricesList = new ArrayList<HashMap<String, String>>();
 
        ListView lv = getListView();
 
        // Listview on item click listener
        lv.setOnItemClickListener(new OnItemClickListener() {
 
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                // getting values from selected ListItem
                String title = ((TextView) view.findViewById(R.id.title))
                        .getText().toString();
                String buy = ((TextView) view.findViewById(R.id.buy))
                        .getText().toString();
                String sell = ((TextView) view.findViewById(R.id.sell))
                        .getText().toString();
 

 
            }
        });
 
        // Calling async task to get json
        new GetContacts().execute();
    }
    
 
    /**
     * Async task class to get json by making HTTP call
     * */
    private class GetContacts extends AsyncTask<Void, Void, Void> {
 
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
 
        }
 
        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
 
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);
 
            Log.d("Response: ", "> " + jsonStr);
 
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                     
                    // Getting JSON Array node
                    prices = jsonObj.getJSONArray(TAG_PRICES);
 
                    // looping through All Prices
                    for (int i = 0; i < prices.length(); i++) {
                        JSONObject c = prices.getJSONObject(i);
                         
                        String title = c.getString(TAG_TITLE);
                        String buy = c.getString(TAG_BUY);
                        String sell = c.getString(TAG_SELL);
                        
                        final EditText et = (EditText)findViewById(R.id.editText1);
                        double inputamt = Double.parseDouble(et.getText().toString());

                        double buyprice = Double.parseDouble(buy) * inputamt;
                        double sellprice = Double.parseDouble(sell) * inputamt;
                        
                        String buy_price = "Rs " + String.format("%.2f", new BigDecimal(buyprice));
                        String sell_price = "Rs " + String.format("%.2f", new BigDecimal(sellprice));
                        
                        // tmp hashmap for single prices
                        HashMap<String, String> prices = new HashMap<String, String>();
 
                        // adding each child node to HashMap key => value
                        prices.put(TAG_TITLE, title);
                        prices.put(TAG_BUY, buy_price);
                        prices.put(TAG_SELL, sell_price);
                        
                        // adding prices to prices list
                        pricesList.add(prices);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
 
            return null;
        }
 
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            SpecialAdapter adapter = new SpecialAdapter(
                    MainActivity.this, pricesList,
                    R.layout.list_item, new String[] { TAG_TITLE, TAG_BUY,
                            TAG_SELL }, new int[] { R.id.title,
                            R.id.buy, R.id.sell });
 
            setListAdapter(adapter);
        }
 
    }
 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.main, menu);
      return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
      // action with ID action_refresh was selected
      case R.id.action_refresh:
        
        pricesList = new ArrayList<HashMap<String, String>>();
        
        ListView lv = getListView();
 
        // Listview on item click listener
        lv.setOnItemClickListener(new OnItemClickListener() {
 
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                // getting values from selected ListItem
                String title = ((TextView) view.findViewById(R.id.title))
                        .getText().toString();
                String buy = ((TextView) view.findViewById(R.id.buy))
                        .getText().toString();
                String sell = ((TextView) view.findViewById(R.id.sell))
                        .getText().toString();
 

 
            }
        });
 
        // Calling async task to get json
        new GetContacts().execute();
        
        break;
        
      	case R.id.action_options:
      		
      		AlertDialog.Builder builder = new AlertDialog.Builder(this);
      		builder.setMessage("The Coinsecure Price App gets the latest prices from multiple exchanges and displays prices in Indian Rupees. The USD conversion rate is taken from Coinbase. The data is updated every 5 minutes on the server. The refresh button needs to be clicked to get the server information every 5 minutes." + "\n\n" + "Created by Benson Samuel - Crypto Labs Pvt Ltd")
      		       .setCancelable(false)
      		       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
      		           public void onClick(DialogInterface dialog, int id) {
      		                //do things
      		           }
      		       });
      		AlertDialog alert = builder.create();
      		alert.show();
      		
	    break;
      }
      
      return true;

	
    } 
        
}