//package com.flux.com.flux.Channels;
//
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.os.AsyncTask;
//import android.support.v4.widget.DrawerLayout;
//import android.support.v7.app.ActionBarDrawerToggle;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.support.v7.widget.Toolbar;
//import android.util.Log;
//import android.view.GestureDetector;
//import android.view.MotionEvent;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import com.flux.com.flux.AboutActivity;
//import com.flux.com.flux.DrawerAdapter;
//import com.flux.com.flux.Flux;
//import com.flux.com.flux.MainActivity;
//import com.flux.com.flux.R;
//import com.flux.com.flux.Settings;
//
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.json.JSONArray;
//import java.io.BufferedReader;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.URL;
//import java.util.ArrayList;
//
//public class ChannelListActivity extends AppCompatActivity {
//
//    private Toolbar toolbar;
//    String TITLES[] = {"Home","Channels","Settings","About"};
//    int ICONS[] = {R.drawable.traffic_light_50,R.drawable.heart_monitor_50,R.drawable.ic_settings,R.drawable.info_filled_50};
//    final Context context = this;
//    RecyclerView mRecyclerView;                           // Declaring RecyclerView
//    RecyclerView.Adapter mAdapter;                        // Declaring Adapter For Recycler View
//    RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager
//    DrawerLayout Drawer;                                  // Declaring DrawerLayout
//    ActionBarDrawerToggle mDrawerToggle;                  // Declaring Action Bar Drawer Toggle;
//
//    private RecyclerView mCardRecyclerView;
//    private RecyclerView.Adapter mCardAdapter;
//    private RecyclerView.LayoutManager mCardLayoutManager;
//    private static String LOG_TAG = "CardViewActivity";
//    ArrayList<CardsDataObject> array = null;
//    private ProgressDialog pDialog, pd = null;
//    String ipAddress;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_channel_list);
//        ipAddress =((Flux)getApplication()).getIpAddress();
//        mCardRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
//        mCardRecyclerView.setHasFixedSize(true);
//        mCardLayoutManager = new LinearLayoutManager(this);
//        mCardRecyclerView.setLayoutManager(mCardLayoutManager);
//        //  img= (ImageView) findViewById(R.id.i)
//        this.pd = ProgressDialog.show(this, "Working..", "Downloading Data...", true, false);
//        new RetrieveData(ipAddress+"/Flux/retrieveChannels.php").execute();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//    }
//
//    public class RetrieveData extends AsyncTask<String, Void, ArrayList<CardsDataObject>> {
//        InputStream is = null;
//        String result = null;
//        String line = null;
//        String URL = "";
//        public RetrieveData(String URL){
//            this.URL = URL;
//        }
//        @Override
//        protected ArrayList<CardsDataObject> doInBackground(String... strings) {
//
//            JSONArray json_data = null;
//
//            try {
//                HttpClient httpclient = new DefaultHttpClient();
//                HttpPost httppost = new HttpPost(URL);
//                HttpResponse response = httpclient.execute(httppost);
//                HttpEntity entity = response.getEntity();
//                is = entity.getContent();
//                Log.e("pass 1", "connection success ");
//            } catch (Exception e) {
//                Log.e("Fail 1", e.toString());
//            }
//            try {
//                BufferedReader reader = new BufferedReader
//                        (new InputStreamReader(is, "iso-8859-1"), 8);
//                StringBuilder sb = new StringBuilder();
//                while ((line = reader.readLine()) != null) {
//                    sb.append(line + "\n");
//                }
//                is.close();
//                result = sb.toString();
//                Log.e("pass 2", "connection success ");
//            } catch (Exception e) {
//                Log.e("Fail 2", e.toString());
//            }
//            try {
//
//
////change type to jsonobject pls remember me
//                json_data = new JSONObject(result);
//
//
//
//
//            } catch (Exception e) {
//                Log.e("Fail 3", e.toString());
//            }
//            ArrayList<CardsDataObject> results = new ArrayList<>();
//            try {
//
//
////diretso access daw ana dk
//                String wapoitn = json_dat.getJSONObject(i).getString(“overview_polyline”);
//
//
//
//
//                for (int i = 0; i < json_data.length(); i++) {
//                    String id = json_data.getJSONObject(i).getString("id");
//                    String name = json_data.getJSONObject(i).getString("name");
//
//
//                    String wapoitn = json_dat.getJSONObject(i).getString(“overview_polyline”);
//
//
//
//                    String description = json_data.getJSONObject(i).getString("description");
//                    String image = json_data.getJSONObject(i).getString("image");
//                    Log.d("name", name);
//                    CardsDataObject obj = new CardsDataObject(name,  id, description,ipAddress+"/Flux/"+image);
//                    results.add(i, obj);
//                }
//            } catch (Exception e) {
//                Log.e("JSON", "There was an error parsing the JSON", e);
//            }
//
//            return results;
//
//        }
//        @Override
//        protected void onPostExecute(ArrayList<CardsDataObject> array) {
//            super.onPostExecute(array);
//            ChannelListActivity.this.array = array;
//            Initialize();
//            Initialize2();
//            if (ChannelListActivity.this.pd != null) {
//                ChannelListActivity.this.pd.dismiss();
//            }
//        }
//    }
//
//    protected void Initialize(){
//        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
//        setSupportActionBar(toolbar);                   // Setting toolbar as the ActionBar with setSupportActionBar() call
//        toolbar.setTitle("Flux");
//        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView); // Assigning the RecyclerView Object to the xml View
//        mRecyclerView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size
//        mAdapter = new DrawerAdapter(TITLES,ICONS);
//        mRecyclerView.setAdapter(mAdapter);                              // Setting the adapter to RecyclerView
//        final GestureDetector mGestureDetector = new GestureDetector(ChannelListActivity.this, new GestureDetector.SimpleOnGestureListener() {
//
//            @Override public boolean onSingleTapUp(MotionEvent e) {
//                return true;
//            }
//
//        });
//        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
//            @Override
//            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
//                View child = recyclerView.findChildViewUnder(motionEvent.getX(),motionEvent.getY());
//                if((child != null) && mGestureDetector.onTouchEvent(motionEvent)){
//                    Drawer.closeDrawers();
//                    // Toast.makeText(ChannelListActivity.this, "The Item Clicked is: " + recyclerView.getChildPosition(child), Toast.LENGTH_SHORT).show();
//                    if(recyclerView.getChildPosition(child) == 1) {
//                        Intent intent;
//                        intent = new Intent(ChannelListActivity.this,MainActivity.class);
//                        startActivity(intent);
//                    } else if (recyclerView.getChildPosition(child) == 2) {
//                        Intent intent;
//                        intent = new Intent(ChannelListActivity.this,ChannelListActivity.class);
//                        startActivity(intent);
//                    }else if (recyclerView.getChildPosition(child) == 3) {
//                        Intent intent;
//                        intent = new Intent(ChannelListActivity.this,Settings.class);
//                        startActivity(intent);
//                    }else if (recyclerView.getChildPosition(child) == 4) {
//                        Intent intent;
//                        intent = new Intent(ChannelListActivity.this,AboutActivity.class);
//                        startActivity(intent);
//                    }
//                    return true;
//                }
//                return false;
//            }
//
//            @Override
//            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
//
//            }
//
//            @Override
//            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
//
//            }
//        });
//
//        mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager
//        mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager
//        Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);        // Drawer object Assigned to the view
//        mDrawerToggle = new ActionBarDrawerToggle(this,Drawer,toolbar,R.string.app_name,R.string.app_name){
//            @Override
//            public void onDrawerOpened(View drawerView) {
//                super.onDrawerOpened(drawerView);
//                // code here will execute once the drawer is opened( As I dont want anything happened whe drawer is
//                // open I am not going to put anything here)
//            }
//            @Override
//            public void onDrawerClosed(View drawerView) {
//                super.onDrawerClosed(drawerView);
//                // Code here will execute once drawer is closed
//            }
//
//        }; // Drawer Toggle Object Made
//        Drawer.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
//        mDrawerToggle.syncState();               // Finally we set the drawer toggle sync State
//        android.support.v7.app.ActionBar menu = getSupportActionBar();
//        menu.setDisplayShowHomeEnabled(true);
//        //menu.setLogo(R.drawable.flux_100px);
//        //menu.setDisplayUseLogoEnabled(true);
//        menu.setTitle("Channels");
//    }
//
//    public void Initialize2(){
//
//        if(array!=null) {
//            mCardAdapter = new CardsRecyclerViewAdapter(array);
//            mCardRecyclerView.setAdapter(mCardAdapter);
//
//            // Code to Add an item with default animation
//            //((MyRecyclerViewAdapter) mAdapter).addItem(obj, index);
//
//            // Code to remove an item with default animation
//            //((MyRecyclerViewAdapter) mAdapter).deleteItem(index);
//            ((CardsRecyclerViewAdapter) mCardAdapter).setOnItemClickListener(new CardsRecyclerViewAdapter
//                    .MyClickListener() {
//                @Override
//                public void onItemClick(int position, View v, int id, String name, String description) {
//
//                    Log.i(LOG_TAG, " Clicked on Item " + position + " Channel ID: " + id);
//                    Intent intent;
//                    intent = new Intent(ChannelListActivity.this, ChannelViewActivity.class);
//                    intent.putExtra("id", id);
//                    intent.putExtra("name", name);
//                    intent.putExtra("description", description);
//                    startActivity(intent);
//                }
//            });
//        }
//    }
//
//}
