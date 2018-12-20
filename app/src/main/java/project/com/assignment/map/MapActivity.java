package project.com.assignment.map;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import project.com.assignment.R;
import project.com.assignment.map.model.Response;
import project.com.assignment.map.network.ApiClient;
import project.com.assignment.map.network.ApiService;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener {



    private GoogleMap mGoogleMap;

    private MapFragment mMapFragment;
    private boolean mMapReady = false;
    LatLng myLocation ;//= new LatLng(34.052235, -118.245683);
    private Double draggedLatitude;
    private Double draggedLongitude;

    private CompositeDisposable disposable = new CompositeDisposable();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        /**
         * Get the reference of the map fragment
         */
        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        /**
         * get the intent and get the location Tag
         */


        String currentLocation = getSharedPreferences(
                Constants.CURRENT_LOCATION_SHARED_PREFERENCE_KEY, 0)
                .getString(Constants.CURRENT_LOCATION_DATA_KEY, null);
       String[] latlong =  currentLocation.split(",");
        double latitude = Double.parseDouble(latlong[0]);
      double longitude = Double.parseDouble(latlong[1]);

        myLocation=new LatLng(latitude,longitude);




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate menu to add items to action bar if it is present.
        //  getMenuInflater().inflate(R.menu.navigation_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();



        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mMapReady = true;
        mGoogleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker arg0) {
                // TODO Auto-generated method stub
                Log.d("System out", "onMarkerDragStart..."+arg0.getPosition().latitude+"..."+arg0.getPosition().longitude);
            }

            @SuppressWarnings("unchecked")
            @Override
            public void onMarkerDragEnd(Marker arg0) {
                // TODO Auto-generated method stub
                Log.d("System out", "onMarkerDragEnd..."+arg0.getPosition().latitude+"..."+arg0.getPosition().longitude);
                draggedLatitude=arg0.getPosition().latitude;
                draggedLongitude=arg0.getPosition().longitude;
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(arg0.getPosition()));
            }

            @Override
            public void onMarkerDrag(Marker arg0) {
                // TODO Auto-generated method stub
                Log.i("System out", "onMarkerDrag...");
            }
        });
        mGoogleMap.setOnMarkerClickListener(this);
        /**
         * Helper Method to put marker on Google map
         */

        CameraPosition cameraPosition = CameraPosition.builder()
                .target(myLocation)
                .zoom(15)
                .bearing(0)
                .tilt(0)
                .build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                1500, null);
        mGoogleMap.addMarker(new MarkerOptions()
                .position(myLocation)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_current_location)).draggable(true));


    }



    @Override
    public boolean onMarkerClick(Marker marker) {
        Toast.makeText(this, marker.getTitle() +draggedLatitude+" "+draggedLongitude, Toast.LENGTH_SHORT).show();
        ApiService apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);

        disposable.add(
                apiService
                        .send(String.valueOf(draggedLatitude),String.valueOf(draggedLongitude))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<Response>() {
                            @Override
                            public void onSuccess(Response response) {

                               Log.i("RESPONSE",response.toString());

                                Toast.makeText(getApplicationContext(),""
                                      ,  Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onError(Throwable e) {
                                e.getStackTrace();
                            }
                        }));
        return false;
    }






}