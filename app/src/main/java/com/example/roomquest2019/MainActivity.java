/*
 * Copyright 2019 Esri
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 *Author(s)/Modified by: Jose Banuelos, Christopher Koenig, Thomas Saldana, Nicholas Perez,
 * Daniel Martinez, Jasmine Pena, Eugene Kim, Gan Liu
 *
 * Date: 6 June 2019
 *
 */

package com.example.roomquest2019;

//=====All necessary libraries for this project=====
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.MediaRouteButton;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.layers.SublayerList;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.GeoView;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.IdentifyGraphicsOverlayResult;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.mapping.view.NavigationChangedEvent;
import com.esri.arcgisruntime.mapping.view.NavigationChangedListener;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.tasks.geocode.GeocodeParameters;
import com.esri.arcgisruntime.tasks.geocode.GeocodeResult;
import com.esri.arcgisruntime.tasks.geocode.LocatorTask;
import com.esri.arcgisruntime.tasks.geocode.SuggestResult;
import com.esri.arcgisruntime.tasks.networkanalysis.Route;
import com.esri.arcgisruntime.tasks.networkanalysis.RouteParameters;
import com.esri.arcgisruntime.tasks.networkanalysis.RouteResult;
import com.esri.arcgisruntime.tasks.networkanalysis.RouteTask;
import com.esri.arcgisruntime.tasks.networkanalysis.Stop;
import com.github.clans.fab.FloatingActionButton;
import static com.esri.arcgisruntime.geometry.GeometryEngine.within;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

public class MainActivity<map> extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, GridDialog.Communicator, RepairGrid.Communicator
{
    //=====Initial defined variables=====
    private SceneView mSceneView;//this represents the view
    private MapView mMapView;
    //private MapView flowMapView;
    private ArcGISScene scene;//this represents the model
    private ArcGISMap map;
    private SearchView mAddressSearchView, mUtilitySearchView;
    private LocatorTask mLocatorTask, mLocatorTaskUtil;
    private GraphicsOverlay mGraphicsOverlay;
    private GeocodeParameters mAddressGeocodeParameters;
    private PictureMarkerSymbol mPinSourceSymbol;
    private SublayerList sublayers;
    private SublayerList flowsublayers;
    private LocationDisplay mLocationDisplay;
    private Spinner mSpinner;
    private RouteTask mRouteTask;
    private RouteParameters mRouteParams;
    private Route mRoute;
    private SimpleLineSymbol mRouteSymbol;
    private Geometry PrimaryBoundary,SecondaryBoundary,mCurrentExtentGeometry;
    //private ArcGISImageServiceLayer sceneImageLayer;
    private ArcGISMapImageLayer mapImageLayer;
    private ArcGISMapImageLayer flowmapImageLayer;
    private List<Stop> routeStops;
    private Graphic routeGraphic;
    private List routes;
    private RouteResult result;
    private Stop DevicePointLocation,DestinationStop;
    private ImageButton RecenterButton,NorthButton;
    private Button StartButton,StopButton;
    private Button FireHydrant;//OPENS A FORM DEMANDING REPAIR SPECS
    private ListenableFuture<RouteParameters> listenableFuture;
    private Viewpoint InitialPoint;
    private DrawerLayout drawer;
    private FloatingActionButton BasementButton,FloorButton1,FloorButton2,FloorButton3,FloorButton4,FloorButton5;
    private FloatingActionButton lowFLow, mediumFlow, highFlow;//DISPLAY FIRE HYDRANTS ACCORDING TO FLOW RATES
    private int requestCode = 2, case_switch = 1;
    private final String TAG = MainActivity.class.getSimpleName();
    private final String COLUMN_NAME_ADDRESS = "address";
    private final String[] mColumnNames = { BaseColumns._ID, COLUMN_NAME_ADDRESS };
    String[] reqPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    //<<<<<<< HEAD
    //private android.support.v7.app.AlertDialog dialog;
    //private android.support.v7.app.AlertDialog.Builder builder;
    private AlertDialog dialog;
    private AlertDialog.Builder builder;
    private SearchView search;
    private String message,usernameText,passwordText;
    private EditText usernameInput, passwordInput;
    private ConstraintLayout logoutLayout;
    private Button logoutButton, annotationButton;
    private boolean loggedIn = false;
    //=======
    //KL, some variables that might be needed later
    private Callout mCallout;
    private ServiceFeatureTable mServiceFeatureTable;
    FeatureLayer featureLayer;
    //KL, new IdentifyFeature variables
    private Point mapPoint;
    private android.graphics.Point screenPoint;
    private Vector<Integer> layerIndex = new Vector<>(); //Vector array for holding feature layer IDs
    private int breakLoop; //Used for querying one feature
    private int number_of_non_feature_layers;

//>>>>>>> 1e0a230498bdc1154274a3e449a11fdd3710a795

    //======Creates essential buttons and variables for starting the app======
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //Initiates the main layout view

        //License - Lite
        ArcGISRuntimeEnvironment.setLicense("runtimelite,1000,rud5742506182,none,4N5X0H4AH46HPGXX3191");

        //Creates a geocode variable and a map layers variable from an online service
        map = new ArcGISMap(Basemap.createStreetsVector()); //Creates a new base map layer
        InitialPoint = new Viewpoint(34.182199, -117.323556, 9000); //Defines the initial viewpoint of the map
        mGraphicsOverlay = new GraphicsOverlay(); // Defines the graphics overlay
        mLocatorTask = new LocatorTask(getResources().getString(R.string.GeocodeServer)); //Initiates the geocode service
        mLocatorTaskUtil = new LocatorTask(getResources().getString(R.string.util_search)); //initiate geocode for utilities search
        mapImageLayer = new ArcGISMapImageLayer(getResources().getString(R.string.MapServer)); //Contains all of the map layer
        flowmapImageLayer = new ArcGISMapImageLayer(getResources().getString(R.string.flow_group));
        /*
        * scene = new ArcGISScene();// base layer
        * 
        * */

        mMapView = findViewById(R.id.mapView); // inflate MapView from layout
        //flowMapView = findViewById(R.id.View);
        StartButton = findViewById(R.id.start); //Initiates the start for routing
        StopButton = findViewById(R.id.stop); //Initiates the stop button for routing
        logoutButton = findViewById(R.id.logout);  //Initiates logout button for routing QFOSTER
        annotationButton = findViewById(R.id.annotations);
        FireHydrant = findViewById(R.id.repairlog);//Initiates repairlog button for routing QFOSTER
        sublayers = mapImageLayer.getSublayers(); //Defines the retrieved map layers from the server
        flowsublayers = flowmapImageLayer.getSublayers();//defines flow map layers from server
        mapImageLayer.addDoneLoadingListener(() -> //Sets all map layers from the server to false by default
        {
            for (int i =0;i<sublayers.size();i++)
            {
                sublayers.get(i).setVisible(false); //Sets all layers to false
            }
            number_of_non_feature_layers = 1;
        });
        map.getOperationalLayers().add(mapImageLayer);
        map.setMinScale(20000); //Sets the max zoom out scale
        map.setInitialViewpoint(InitialPoint); //Sets map view to initial point
        ReturnFloorView(1); // Initializes the initial map view by returning map layers relating to the first floor
        mMapView.setMap(map); //Sets the map once all necessary map layers are initiated


        System.out.println(sublayers.size());
        mCallout = mMapView.getCallout();


        //KL, new Identify Feature stuff goes here
        screenPoint = new android.graphics.Point();
        //-----Loads Utility Group layers in this exact order-----
        LoadPolylineGroup();
        LoadPointGroup();
        LoadAnnotationGroup();
        LoadFlowGroup();
        //--------------------------------------------------------
        DetectMapView(); //Detects map clicks



        //Defines the variables for creating the route system
        mRouteSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 5); //Defines the color of the route
        mRouteTask = new RouteTask(getApplicationContext(), getResources().getString(R.string.routing_service)); // create RouteTask instance
        listenableFuture = mRouteTask.createDefaultParametersAsync();
        routeStops = new ArrayList<>(); //Creates new array to hold route stops
        final ListenableFuture<RouteParameters> listenableFuture = mRouteTask.createDefaultParametersAsync();

        //Sets Start and Stop buttons to invisible by default
        StartButton.setVisibility(View.INVISIBLE);
        StopButton.setVisibility(View.INVISIBLE);
        logoutButton.setVisibility(View.INVISIBLE);
        annotationButton.setVisibility(View.INVISIBLE);
        FireHydrant.setVisibility(View.INVISIBLE);//SHOULD ONLY BE VISIBLE AFTER MTX LOGIN!
        RoutingButtons(); //Used for detecting the start and stop buttons

        //Initiates pin drawable
        BitmapDrawable pinDrawable = (BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.coyote_paw);
        try
        {
            mPinSourceSymbol = PictureMarkerSymbol.createAsync(pinDrawable).get();
        }
        catch (InterruptedException | ExecutionException e)
        {
            Log.e(TAG, "Picture Marker Symbol error: " + e.getMessage());
        }
        //Sets the size of the pin width and height
        mPinSourceSymbol.setWidth(50f);
        mPinSourceSymbol.setHeight(56f);

        //Initiates the search view button
        // get place address attributes
        mAddressSearchView = findViewById(R.id.addressSearchView);
        mAddressSearchView.setIconified(true);
        mAddressSearchView.setQueryHint(getResources().getString(R.string.address_search_hint));
        mAddressGeocodeParameters = new GeocodeParameters();
        mAddressGeocodeParameters.getResultAttributeNames().add("Match_addr"); //Retrieves the attribute name associated with the map layer from the geocode service
        mAddressGeocodeParameters.setMaxResults(1); // return only the closest result
        setupAddressSearchView(); //Initializes and detects the search view button


        mUtilitySearchView = findViewById(R.id.utilititesSearchView);
        mUtilitySearchView.setIconified(true);
        mUtilitySearchView.setQueryHint("Search utilities");
        setupAddressSearchViewUtil();

        //Initiates fire buttons
        lowFLow = findViewById(R.id.flow_item3);
        mediumFlow = findViewById(R.id.flow_item2);
        highFlow = findViewById(R.id.flow_item1);
        //make them invisible in visitor/student view
        lowFLow.setVisibility(View.GONE);
        mediumFlow.setVisibility(View.GONE);
        highFlow.setVisibility(View.GONE);
        //Initiates the floor buttons
        BasementButton = findViewById(R.id.menu_item6);
        FloorButton1 = findViewById(R.id.menu_item5);
        FloorButton2 = findViewById(R.id.menu_item4);
        FloorButton3 = findViewById(R.id.menu_item3);
        FloorButton4 = findViewById(R.id.menu_item2);
        FloorButton5 = findViewById(R.id.menu_item1);
        SetupFloorButtons(); //Used for detecting floor button clicks
        SetupFlowRateButtons();//DETECT FLOW RATE BUTTONS
        SetupLogoutButton();    //Used for detecting logout button  //QFOSTER


        //=====Creates and initiates the navigation drawer button and the listener=====
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar); //Initiates the toolbar layout
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        SetupNavigationDrawer(); //Used for detecting the navigation drawer
        DetectScreenTap(map, featureLayer); //Creates the listener for detecting screen taps
        //KL, modified DetectScreenTap to know what map and feature layer we're messing with

        //Geometry extents
        //(xmin,ymin,xmax,ymax)
        PrimaryBoundary = new Envelope(-13065022.750359,4047321.888750,-13055620.594074,4061062.468048, SpatialReference.create(102100)); //Primary outer boundary
        SecondaryBoundary = new Envelope(-13061728.743000,4052018.857932,-13058810.706190,4054717.974725, SpatialReference.create(102100)); //Secondary inner boundary

        //=====Creates the spinner buttons and the listener=====
        mSpinner = findViewById(R.id.spinner); // Get the Spinner from layout
        mLocationDisplay = mMapView.getLocationDisplay(); // get the MapView's LocationDisplay
        LocationDisplayPermissions();
        LocationDisplayListener(); //Detects changes in device location
        ViewpointListener(); //Detects changes in screen viewpoint

        // Populate the list for the navigation display option for the spinner's Adapter
        ArrayList<ItemData> list = new ArrayList<>();
        list.add(new ItemData("", R.drawable.locationdisplaydisabled));
        list.add(new ItemData("", R.drawable.locationdisplayheading));
        SpinnerAdapter adapter = new SpinnerAdapter(this, R.layout.spinner_layout, R.id.txt, list); //Creates and initiates the spinner adapter to hold spinner's button list
        mSpinner.setAdapter(adapter); //Initiates the spinner adapater
        SetupSpinnerLayout(); //Initiates the Spinner button layout function

        //Initiates the recenter and north button
        RecenterButton = findViewById(R.id.recenter);
        NorthButton = findViewById(R.id.north);
        RecenterButton.setVisibility(View.INVISIBLE);
        NorthButton.setVisibility(View.VISIBLE);
        North(); //Used for detecting the North button clicks
        Recenter(); //Used for detecting the Recenter button clicks

        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
        View prompt = inflater.inflate(R.layout.admin_layout,null);

        builder = new AlertDialog.Builder(MainActivity.this);        //QFOSTER
        usernameInput = prompt.findViewById(R.id.UsernameBox);
        passwordInput = prompt.findViewById(R.id.PasscodeBox);
        passwordInput.clearFocus();
        usernameInput.requestFocus();
        builder.setView(prompt);
        builder.setTitle("Utilities Login");
        logoutLayout= findViewById(R.id.logoutView);
        logoutButton = findViewById(R.id.logout);

        builder = new AlertDialog.Builder(MainActivity.this);

        builder.setView(prompt);
        builder.setTitle("Utilities Login");                                                   //QFOSTER
        DialogPrompt();

        //set flowrate buttons invisible/gone
        annotationButton.setVisibility(View.INVISIBLE);
        mUtilitySearchView.setVisibility(View.INVISIBLE);
        AnnotationListener();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) // If request is cancelled, the result arrays are empty.
        {
            mLocationDisplay.startAsync(); // Location permission was granted.
        }
        else // If permission was denied, show toast to inform user what was chosen.
        {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.location_permission_denied), Toast.LENGTH_SHORT).show();
            mSpinner.setSelection(0, true); // Update UI to reflect that the location display did not actually start
        }
    }

    //=====Detects the Back button on the Home buttons display=====
    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    //=====Action Settings=====
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu); // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    //=====Detects the Search and Grid Button=====
    // Action bar item clicks are handled here.
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if(loggedIn) {              //QFOSTER
            if(id==R.id.reset)      //true if logged in and clicked reset button
            {
                mMapView.getCallout().dismiss();
                routeStops.clear(); //Clears the array containing route stops
                mMapView.setViewpointRotationAsync(0);  //resets rotation to default
                mMapView.setViewpoint(InitialPoint);    //sets viewpoint to middle of campus
                StartButton.setVisibility(View.INVISIBLE);
                StopButton.setVisibility(View.INVISIBLE);
            }

            return true;

        }

        //no inspection SimplifiableIfStatement
        if (id == R.id.addressSearchView) //Detects the search button
        {
            item.setActionView(mAddressSearchView);
            mAddressSearchView.setIconified(false); //Opens the search button
            return true;
        }
        else if (id==R.id.reset) //Detects the Clear button
        {
            //Clears all the graphics from the map scene
            //Resets the map back to the first floor layers
            mMapView.getCallout().dismiss();
            mGraphicsOverlay.getGraphics().clear();
            mMapView.getGraphicsOverlays().clear();
            routeStops.clear(); //Clears the array containing route stops
            mMapView.setViewpointRotationAsync(0);
            mMapView.setViewpoint(InitialPoint);
            ReturnFloorView(1);
            mAddressSearchView.setIconified(true);
            StartButton.setVisibility(View.INVISIBLE);
            StopButton.setVisibility(View.INVISIBLE);
        }
        else if (id==R.id.grid) //Detects the grid button
        {
            android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
            GridDialog gridDialog = new GridDialog();
            gridDialog.show(manager, "Grid"); //Initiates the dialog for grid layers
        }
        //Jose, added repair log detect
        else if (id==R.id.repairlog) //Detects the grid button
        {
            android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
            RepairGrid repairDialog = new RepairGrid();//TRY GRIDDIALOG() INSTEAD
            repairDialog.show(manager, "Repairlog"); //Initiates the dialog for repair layers
        }
        return super.onOptionsItemSelected(item);
    }

    //=====Detects the navigation drawer buttons=====
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        if(loggedIn) {                  //Prevents drawer items from doing anything when logged in
            return true;
        }
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.bldg_1)
        {  //administration
            mMapView.getGraphicsOverlays().remove(mGraphicsOverlay);
            geoCodeTypedAddress("Administration");
        }
        else if (id == R.id.bldg_2)
        {  //auto_fleet_services
            mMapView.getGraphicsOverlays().remove(mGraphicsOverlay);
            geoCodeTypedAddress("Auto Fleet Services");
        }
        else if (id == R.id.bldg_3)
        {  //animal_house
            mMapView.getGraphicsOverlays().remove(mGraphicsOverlay);
            geoCodeTypedAddress("Animal House / Vivarium");
        }
        /*else if (id == R.id.bldg_4)
        {  //academic_research
            geoCodeTypedAddress("Academic Research"); //=====FIX=====
        }*/
        else if (id == R.id.bldg_5)
        { //administrative_services
            mMapView.getGraphicsOverlays().remove(mGraphicsOverlay);
            geoCodeTypedAddress("Administrative Services");
        }
        /*else if (id == R.id.bldg_6)
        {  //arrowhead_village_housing
            geoCodeTypedAddress("Arrowhead Village Housing"); //=====FIX=====
        }*/
        else if (id == R.id.bldg_7)
        {  //biological_sciences
            mMapView.getGraphicsOverlays().remove(mGraphicsOverlay);
            geoCodeTypedAddress("Biological Sciences");
        }
        else if (id == R.id.bldg_8)
        {  //coyote_bookstore
            mMapView.getGraphicsOverlays().remove(mGraphicsOverlay);
            geoCodeTypedAddress("Coyote Bookstore");
        }
        else if (id == R.id.bldg_9)
        {  //childrens_center
            mMapView.getGraphicsOverlays().remove(mGraphicsOverlay);
            geoCodeTypedAddress("Children's Center");
        }
        /*else if (id == R.id.bldg_10)
        { //coyote_commons
            geoCodeTypedAddress("Coyote Commons"); //=====FIX=====
        }*/
        else if (id == R.id.bldg_11)
        { //college_education
            mMapView.getGraphicsOverlays().remove(mGraphicsOverlay);
            geoCodeTypedAddress("College Education");
        }
        else if (id == R.id.bldg_12)
        { //chaparral_hall
            mMapView.getGraphicsOverlays().remove(mGraphicsOverlay);
            geoCodeTypedAddress("Chaparral Hall");
        }
        else if (id == R.id.bldg_13)
        { //chemical_sciences
            mMapView.getGraphicsOverlays().remove(mGraphicsOverlay);
            geoCodeTypedAddress("Chemical Sciences");
        }
        /*else if (id == R.id.bldg_14)
        { //coyote_village
            mMapView.getGraphicsOverlays().remove(mGraphicsOverlay);
            geoCodeTypedAddress("Coyote Village"); //=====FIX=====
        }
        else if (id == R.id.bldg_15)
        { //developmental_disabilities
            geoCodeTypedAddress("Developmental Disabilities"); //=====FIX=====
        }*/
        else if (id == R.id.bldg_16)
        {//environmental_health_safety
            mMapView.getGraphicsOverlays().remove(mGraphicsOverlay);
            geoCodeTypedAddress("Environmental Health Safety");
        }
        else if (id == R.id.bldg_17)
        {//facilities_planning
            mMapView.getGraphicsOverlays().remove(mGraphicsOverlay);
            geoCodeTypedAddress("Facilities Management");
        }
        else if (id == R.id.bldg_18)
        { //faculty_office
            mMapView.getGraphicsOverlays().remove(mGraphicsOverlay);
            geoCodeTypedAddress("Faculty Office");
        }
        else if (id == R.id.bldg_19)
        { //hvac_central_plant
            mMapView.getGraphicsOverlays().remove(mGraphicsOverlay);
            geoCodeTypedAddress("HVAC Central Plant");
        }
        else if (id == R.id.bldg_20)
        { //student_health_center
            mMapView.getGraphicsOverlays().remove(mGraphicsOverlay);
            geoCodeTypedAddress("Student Health Center");
        }
        else if (id == R.id.bldg_21)
        {  //health_pe_complex
            mMapView.getGraphicsOverlays().remove(mGraphicsOverlay);
            geoCodeTypedAddress("Health PE Complex");
        }
        else if (id == R.id.bldg_22)
        { //information_centers
            mMapView.getGraphicsOverlays().remove(mGraphicsOverlay);
            geoCodeTypedAddress("UH171 University Hall Information Center");
        }
        else if (id == R.id.bldg_23)
        { //jack_brown_hall
            mMapView.getGraphicsOverlays().remove(mGraphicsOverlay);
            geoCodeTypedAddress("Jack Brown Hall");
        }
        /*else if (id == R.id.bldg_24)
        { //meeting_center
            geoCodeTypedAddress("Meeting Center"); //=====FIX=====
        }
        else if (id == R.id.bldg_25)
        { //murillo_family_observatory
            geoCodeTypedAddress("Murillo Family Observatory"); //=====FIX=====
        }*/
        else if (id == R.id.bldg_26)
        { //performing_arts
            mMapView.getGraphicsOverlays().remove(mGraphicsOverlay);
            geoCodeTypedAddress("Performing Arts");
        }
        else if (id == R.id.bldg_27)
        { //physical_education
            mMapView.getGraphicsOverlays().remove(mGraphicsOverlay);
            geoCodeTypedAddress("Physical Education");
        }
        else if (id == R.id.bldg_28)
        { //parking_structure_west
            mMapView.getGraphicsOverlays().remove(mGraphicsOverlay);
            geoCodeTypedAddress("Parking Structure West");
        }
        else if (id == R.id.bldg_29)
        { //parking_structure_east
            mMapView.getGraphicsOverlays().remove(mGraphicsOverlay);
            geoCodeTypedAddress("Parking Structure East");
        }
        else if (id == R.id.bldg_30)
        { //john_pfau_library
            mMapView.getGraphicsOverlays().remove(mGraphicsOverlay);
            geoCodeTypedAddress("John Pfau Library");
        }
        else if (id == R.id.bldg_31)
        { //physical_sciences
            mMapView.getGraphicsOverlays().remove(mGraphicsOverlay);
            geoCodeTypedAddress("Physical Sciences");
        }
        else if (id == R.id.bldg_32)
        { //plant_central_warehouse
            mMapView.getGraphicsOverlays().remove(mGraphicsOverlay);
            geoCodeTypedAddress("Plant Central Warehouse");
        }
        else if (id == R.id.bldg_33)
        { //rec_center
            mMapView.getGraphicsOverlays().remove(mGraphicsOverlay);
            geoCodeTypedAddress("Recreation");
        }
        else if (id == R.id.bldg_34)
        { //social_behavioral_sciences
            mMapView.getGraphicsOverlays().remove(mGraphicsOverlay);
            geoCodeTypedAddress("Social Behavioral Sciences");
        }
        else if (id == R.id.bldg_35)
        { //sierra_hall
            mMapView.getGraphicsOverlays().remove(mGraphicsOverlay);
            geoCodeTypedAddress("Sierra Hall");
        }
        else if (id == R.id.bldg_36)
        { //santos_manuel_student_union
            mMapView.getGraphicsOverlays().remove(mGraphicsOverlay);
            geoCodeTypedAddress("Santos Manuel Student Union");
        }
        /*else if (id == R.id.bldg_37)
        { //serrano_village
            geoCodeTypedAddress("Serrano Village"); //=====FIX=====
        }
        else if (id == R.id.bldg_38)
        { //temp_classrooms
            geoCodeTypedAddress("Temp Classrooms"); //=====FIX=====
        }*/
        else if (id == R.id.bldg_39)
        { //temp_kinesiology_annex
            mMapView.getGraphicsOverlays().remove(mGraphicsOverlay);
            geoCodeTypedAddress("Temporary Kinesiology Annex");
        }
        else if (id == R.id.bldg_40)
        { //temp_offices
            mMapView.getGraphicsOverlays().remove(mGraphicsOverlay);
            geoCodeTypedAddress("Temporary Offices");
        }
        else if (id == R.id.bldg_41)
        { //university_enterprises
            mMapView.getGraphicsOverlays().remove(mGraphicsOverlay);
            geoCodeTypedAddress("University Enterprises");
        }
        else if (id == R.id.bldg_42)
        { //university_hall
            mMapView.getGraphicsOverlays().remove(mGraphicsOverlay);
            geoCodeTypedAddress("University Hall");
        }
        else if (id == R.id.bldg_43)
        { //university_police
            mMapView.getGraphicsOverlays().remove(mGraphicsOverlay);
            geoCodeTypedAddress("University Police");
        }
        /*else if (id == R.id.bldg_44)
        { //university_village
            geoCodeTypedAddress("University Village"); //=====FIX=====
        }*/
        else if (id == R.id.bldg_45)
        { //visual_arts_center
            mMapView.getGraphicsOverlays().remove(mGraphicsOverlay);
            geoCodeTypedAddress("Visual Arts / RAFFMA");
        }
        else if (id == R.id.bldg_46)
        { //yasuda_center
            mMapView.getGraphicsOverlays().remove(mGraphicsOverlay);
            geoCodeTypedAddress("Yasuda Center");
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void SetupLogoutButton()
    {
        logoutButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {           //Statements occur when logout button is pressed
                mMapView.getCallout().dismiss();    //Statements to get app back to non admin mode
                mGraphicsOverlay.getGraphics().clear();
                mMapView.getGraphicsOverlays().clear();
                routeStops.clear(); //Clears the array containing route stops
                mMapView.setViewpointRotationAsync(0);
                mMapView.setViewpoint(InitialPoint);
                ReturnFloorView(1);
                mAddressSearchView.setIconified(true);
                StartButton.setVisibility(View.INVISIBLE);
                StopButton.setVisibility(View.INVISIBLE);

                //enable buttons
                BasementButton.setVisibility(View.VISIBLE);   //diables Basement button from being clickable

                FloorButton1.setVisibility(View.VISIBLE);    //diables floor buttons from being clickable
                FloorButton2.setVisibility(View.VISIBLE);
                FloorButton3.setVisibility(View.VISIBLE);
                FloorButton4.setVisibility(View.VISIBLE);
                FloorButton5.setVisibility(View.VISIBLE);      //end disable floor buttons

                mAddressSearchView.setVisibility(View.VISIBLE);      //shows the search bar
                drawer.setDrawerLockMode(0);                         //makes drawer clickable
                //end enable buttons

                logoutButton.setVisibility(View.INVISIBLE);       //Hides logout button
                FireHydrant.setVisibility(View.INVISIBLE);

                //disable flow rate buttons
                lowFLow.setVisibility(View.GONE);
                mediumFlow.setVisibility(View.GONE);
                highFlow.setVisibility(View.GONE);

                loggedIn = false;                                //bool to show if logged in (Used to reneable navigation bar)
                HideFeatureLayers(); //hide feature layers since we're logged out
                annotationButton.setVisibility(View.INVISIBLE);
                mUtilitySearchView.setVisibility(View.GONE);

               //mLocatorTask = new LocatorTask(getResources().getString(R.string.GeocodeServer)); //Initiates the geocode service

                Toast.makeText(getApplicationContext(), "Logged Out", Toast.LENGTH_LONG).show();    //message showing leooged out
            }
        });
    }
    //private void RepairLogListener(){}

    private void AnnotationListener()
    {
        annotationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallout.dismiss();

                System.out.println("# of map layers "+map.getOperationalLayers().size());

                switch(case_switch) //Switch statement for enabling and disabling annotations other point layers
                {
                    case 0:
                    {
                        Toast.makeText(MainActivity.this,"Annotations Disabled",Toast.LENGTH_SHORT).show();
                        for(int i=0;i<map.getOperationalLayers().size();i++)
                        {
                            if(i>=50 + number_of_non_feature_layers)
                            {
                                map.getOperationalLayers().get(i).setVisible(false);
                            }
                        }
                        case_switch = 1;
                        break;
                    }
                    case 1:
                    {
                        Toast.makeText(MainActivity.this,"Annotations Enabled",Toast.LENGTH_SHORT).show();
                        for(int i=0;i<map.getOperationalLayers().size();i++)
                        {
                            if(i>=50 + number_of_non_feature_layers)
                            {
                                map.getOperationalLayers().get(i).setVisible(true);
                            }
                        }
                        case_switch = 0;
                        break;
                    }
                }
            }
        });
    }

    //=====Creates the listeners for the floor buttons=====
    public void SetupFloorButtons()
    {
        BasementButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mMapView.getGraphicsOverlays().clear();
                mGraphicsOverlay.getGraphics().clear();
                ReturnFloorView(0);
                Toast.makeText(MainActivity.this,"Basement",Toast.LENGTH_SHORT).show(); //Displays text message
            }
        });

        //=====Creates 1st Floor Button listener=====
        FloorButton1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mMapView.getGraphicsOverlays().clear();
                mGraphicsOverlay.getGraphics().clear();
                ReturnFloorView(1);
                Toast.makeText(MainActivity.this,"1st Floor",Toast.LENGTH_SHORT).show();
            }
        });

        //=====Creates 2nd Floor Button listener=====
        FloorButton2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mMapView.getGraphicsOverlays().clear();
                mGraphicsOverlay.getGraphics().clear();
                ReturnFloorView(2);
                Toast.makeText(MainActivity.this,"2nd Floor",Toast.LENGTH_SHORT).show();
            }
        });

        //=====Creates 3rd Floor Button listener=====
        FloorButton3.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mMapView.getGraphicsOverlays().clear(); //clear map of existing graphics
                mGraphicsOverlay.getGraphics().clear();
                ReturnFloorView(3);
                Toast.makeText(MainActivity.this,"3rd Floor",Toast.LENGTH_SHORT).show();
            }
        });

        //=====Creates 4th Floor Button listener=====
        FloorButton4.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mMapView.getGraphicsOverlays().clear(); //clear map of existing graphics
                mGraphicsOverlay.getGraphics().clear();
                ReturnFloorView(4);
                Toast.makeText(MainActivity.this,"4th Floor",Toast.LENGTH_SHORT).show();
            }
        });

        //=====Creates 5th Floor Button listener=====
        FloorButton5.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mMapView.getGraphicsOverlays().clear(); //clear map of existing graphics
                mGraphicsOverlay.getGraphics().clear();
                ReturnFloorView(5);
                Toast.makeText(MainActivity.this,"5th Floor",Toast.LENGTH_SHORT).show();
            }
        });
    }

    //create a fire hydrant flow rate setup
    public void SetupFlowRateButtons(){
        //=====Creates blue fire hydrant Button listener=====
        lowFLow.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mMapView.getGraphicsOverlays().clear();
                mGraphicsOverlay.getGraphics().clear();
                ReturnFlowGroup(700);
                Toast.makeText(MainActivity.this,"Low flow rate",Toast.LENGTH_SHORT).show();
            }
        });
        //=====Creates green fire hydrant button listener========
        mediumFlow.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mMapView.getGraphicsOverlays().clear();
                mGraphicsOverlay.getGraphics().clear();
                ReturnFlowGroup(1000);
                Toast.makeText(MainActivity.this,"standard flow rate",Toast.LENGTH_SHORT).show();
            }
        });
        //====Creates red fire hydrant button listener======
        highFlow.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mMapView.getGraphicsOverlays().clear();
                mGraphicsOverlay.getGraphics().clear();
                ReturnFlowGroup(1500);
                Toast.makeText(MainActivity.this,"High flow rate",Toast.LENGTH_SHORT).show();
            }
        });
    }

    //=====Creates the navigation drawer listener=====
    public void SetupNavigationDrawer()
    {
        drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener()
        {
            @Override
            public void onDrawerOpened(View drawerView)
            {
                //Closes all callouts
                //Used for counteracting a bug
                mMapView.getCallout().dismiss();
                mAddressSearchView.setIconified(true); //Closes the search view button if it is open
            }
        });
    }

    //=====Creates the location display listener=====
    public void LocationDisplayPermissions()
    {
        mLocationDisplay.addDataSourceStatusChangedListener(new LocationDisplay.DataSourceStatusChangedListener() // Listen to changes in the status of the location data source.
        {
            @Override
            public void onStatusChanged(LocationDisplay.DataSourceStatusChangedEvent dataSourceStatusChangedEvent)
            {
                if (dataSourceStatusChangedEvent.isStarted()) // If LocationDisplay started OK, then continue.
                {
                    return;
                }

                if (dataSourceStatusChangedEvent.getError() == null) // No error is reported, then continue.
                {
                    return;
                }

                // If an error is found, handle the failure to start.
                // Check permissions to see if failure may be due to lack of permissions.
                boolean permissionCheck1 = ContextCompat.checkSelfPermission(MainActivity.this, reqPermissions[0]) == PackageManager.PERMISSION_GRANTED;
                boolean permissionCheck2 = ContextCompat.checkSelfPermission(MainActivity.this, reqPermissions[1]) == PackageManager.PERMISSION_GRANTED;

                if (!(permissionCheck1 && permissionCheck2))
                {
                    // If permissions are not already granted, request permission from the user.
                    ActivityCompat.requestPermissions(MainActivity.this, reqPermissions, requestCode);
                }
                else
                {
                    // Report other unknown failure types to the user - for example, location services may not be enabled on the device.
                    String message = String.format("Error in DataSourceStatusChangedListener: %s", dataSourceStatusChangedEvent.getSource().getLocationDataSource().getError().getMessage());
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();

                    // Update UI to reflect that the location display did not actually start
                    mSpinner.setSelection(0, true);
                }
            }
        });
    }

    //=====Creates the Spinner layout listener=====
    public void SetupSpinnerLayout()
    {
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                switch (position)
                {
                    case 0:
                        // Stop Location Display
                        if (mLocationDisplay.isStarted())
                        {
                            mGraphicsOverlay.getGraphics().remove(routeGraphic); //Removes the drawn route layer when device location is disabled
                            mLocationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.OFF);
                            mLocationDisplay.stop();
                            StartButton.setVisibility(View.INVISIBLE);
                            StopButton.setVisibility(View.INVISIBLE);
                            RecenterButton.setVisibility(View.INVISIBLE);
                            Toast.makeText(getApplicationContext(),"Navigation Mode Disabled", Toast.LENGTH_LONG).show();
                        }
                        break;
                    case 1:
                        // Initiate the Location display for navigation mode
                        if (!mLocationDisplay.isStarted())
                        {
                            mLocationDisplay.startAsync();
                            mLocationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.COMPASS_NAVIGATION); //Compass view
                            mGraphicsOverlay.getGraphics().remove(routeGraphic);
                            StartButton.setVisibility(View.INVISIBLE);
                            StopButton.setVisibility(View.INVISIBLE);
                            RecenterButton.setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(),"Navigation Mode Enabled", Toast.LENGTH_LONG).show();
                        }
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    //=====Creates the north button listener=====
    public void North()
    {
        //Resets the map rotation
        NorthButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mMapView.setViewpointRotationAsync(0);
            }
        });
    }

    //=====Creates the recenter button listener=====
    public void Recenter()
    {
        //Recenters the map onto the device location
        //Recenter Button appears next to the North button after navigation mode is enabled
        RecenterButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mLocationDisplay.isStarted())
                {
                    mLocationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.COMPASS_NAVIGATION);
                    Toast.makeText(getApplicationContext(),"Recenter", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //=====Creates the start and stop button listeners=====
    public void RoutingButtons()
    {
        //Starts the routing sequence if device location is enabled and a destination point is present
        StartButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(!within(mLocationDisplay.getMapLocation().getExtent(),SecondaryBoundary))
                {
                    mGraphicsOverlay.getGraphics().remove(routeGraphic); //Removes drawn route from the map
                    StartButton.setVisibility(View.INVISIBLE);
                    StopButton.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "No route discovered"+"\n"+"Device location out of bounds", Toast.LENGTH_LONG).show();
                }
                else if(mLocationDisplay.isStarted() && !routeStops.isEmpty() && within(mLocationDisplay.getMapLocation().getExtent(),SecondaryBoundary))
                {
                    mLocationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.COMPASS_NAVIGATION);
                    StartButton.setVisibility(View.INVISIBLE);
                    StopButton.setVisibility(View.VISIBLE);
                    RoutingSequence(); //Initiates the routing sequence when start button is clicked
                }
            }
        });

        //Stops the routing sequence
        StopButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(mLocationDisplay.isStarted() && mGraphicsOverlay.getGraphics().contains(routeGraphic))
                {
                    mGraphicsOverlay.getGraphics().remove(routeGraphic); //Removes the routing graphic from the map
                    StopButton.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    //=====Creates the location display listener=====
    public void LocationDisplayListener()
    {
        mLocationDisplay.addLocationChangedListener(new LocationDisplay.LocationChangedListener() {
            @Override
            public void onLocationChanged(LocationDisplay.LocationChangedEvent locationChangedEvent)
            {
                if (mLocationDisplay.isStarted()) //Initiates if user enables device location
                {
                    if(!within(mLocationDisplay.getMapLocation().getExtent(),PrimaryBoundary)) //If device location is outside the primary outer boundary, navigation mode will be disabled
                    {
                        mGraphicsOverlay.getGraphics().remove(routeGraphic); //Removes drawn route from the map
                        StartButton.setVisibility(View.INVISIBLE);
                        StopButton.setVisibility(View.INVISIBLE);
                        RecenterButton.setVisibility(View.INVISIBLE);
                        mLocationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.OFF);
                        mLocationDisplay.stop();
                        mSpinner.setSelection(0);
                        Toast.makeText(getApplicationContext(),"Navigation Mode Disabled"+"\n"+"Device location out of bounds", Toast.LENGTH_LONG).show();
                    }
                    //If device location is inside the primary outer boundary but outside the inner secondary boundary, navigation mode will remain enabled but routing is is disabled
                    else if(!within(mLocationDisplay.getMapLocation().getExtent(),SecondaryBoundary))
                    {
                        if(mGraphicsOverlay.getGraphics().contains(routeGraphic))
                        {
                            Toast.makeText(getApplicationContext(), "No route discovered"+"\n"+"Device location out of bounds", Toast.LENGTH_LONG).show();
                        }
                        mGraphicsOverlay.getGraphics().remove(routeGraphic); //Removes drawn route from the map
                        StopButton.setVisibility(View.INVISIBLE);
                    }
                    //Routing will update with changes in device location so long as the user stays within the inner secondary boundary
                    else if(!routeStops.isEmpty() && mGraphicsOverlay.getGraphics().contains(routeGraphic) && within(mLocationDisplay.getMapLocation().getExtent(),SecondaryBoundary))
                    {
                        RoutingSequence();
                    }
                }
            }
        });
    }

    //=====Creates the viewpoint geometry listener=====
    public void ViewpointListener()
    {
        mMapView.addNavigationChangedListener(new NavigationChangedListener()
        {
            @Override
            public void navigationChanged(NavigationChangedEvent navigationChangedEvent)
            {
                mCurrentExtentGeometry = mMapView.getCurrentViewpoint(Viewpoint.Type.BOUNDING_GEOMETRY).getTargetGeometry(); //Used for obtaining the current viewpoint geometry of the screen
                if(!within(mCurrentExtentGeometry,PrimaryBoundary)) //User scrolls beyond the boundary, viewpoint resets back to the center
                {
                    //Reset viewpoint to initial point if current viewpoint geometry is not within container
                    mMapView.setViewpoint(InitialPoint);
                }
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility") //Suppresses clickable warning
    //KL, we'll need to swap to this version later
    public void DetectScreenTap(ArcGISMap map,FeatureLayer featureLayer)
    //public void DetectScreenTap()
    {
        // add listener to handle screen taps
        mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(this, mMapView)
        {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent motionEvent)
            {
                if (loggedIn) {

                    Callout mCallout = mMapView.getCallout();
                    // remove any existing callouts
                    if (mCallout.isShowing()) {
                        mCallout.dismiss();
                    }
                    // get the point that was clicked and convert it to a point in map coordinates
                    final Point clickPoint = mMapView.screenToLocation(new android.graphics.Point(Math.round(motionEvent.getX()), Math.round(motionEvent.getY())));
                    int tolerance = 10; // create a selection tolerance
                    double mapTolerance = tolerance * mMapView.getUnitsPerDensityIndependentPixel();
                    // use tolerance to create an envelope to query
                    Envelope envelope = new Envelope(clickPoint.getX() - mapTolerance, clickPoint.getY() - mapTolerance,clickPoint.getX() + mapTolerance, clickPoint.getY() + mapTolerance, map.getSpatialReference());
                    QueryParameters query = new QueryParameters();
                    query.setGeometry(envelope);
                    //KL, we need to modify this bit to the new functions
                    screenPoint.set(Math.round(motionEvent.getX()),Math.round(motionEvent.getY())); //Gets screen point
                    mapPoint = mMapView.screenToLocation(screenPoint); //Converst screen point to map view point
                    selectFeaturesAt(mapPoint); //Start feature selection function
                    return super.onSingleTapConfirmed(motionEvent);
                }
                else{
                    identifyGraphic(motionEvent); //Identifies the nav point on screen tap
                    mAddressSearchView.setIconified(true); //Closes the search view button
                    return true;
                }

            }
        });
    }

    //=====Detects grid layout buttons when grid dialog is initiated=====
    //@Override
    public void onDialogMessage(String message)
    {
        mMapView.getCallout().dismiss(); //Dismisses all callouts showing
        mMapView.getGraphicsOverlays().clear(); //clear map of existing graphics
        mGraphicsOverlay.getGraphics().clear();
        switch(message)
        {
            case "Bicycle Racks":
                ReturnGridLayer(1); //Retrieves first floor layers and grid layer at index 1
                break;
            case "Parking Permit Dispensers":
                ReturnGridLayer(2); //Retrieves first floor layers and grid layer at index 2
                break;
            case "Disability Parking Areas":
                ReturnGridLayer(3); //Retrieves first floor layers and grid layer at index 3
                break;
            case "Information Centers":
                ReturnGridLayer(4); //Retrieves first floor layers and grid layer at index 4
                break;
            case "Campus Shuttle":
                ReturnGridLayer(5); //Retrieves first floor layers and grid layer at index 5
                break;
            case "Emergency Phones":
                ReturnGridLayer(6); //Retrieves first floor layers and grid layer at index 6
                break;
            case "Restrooms":
                ReturnGridLayer(7); //Retrieves first floor layers and grid layer at index 7
                break;
            case "EV Charging Stations":
                ReturnGridLayer(8); //Retrieves first floor layers and grid layer at index 8
                break;
            case "Health Center":
                ReturnGridLayer(9); //Retrieves first floor layers and grid layer at index 9
                break;
            case "ATM":
                ReturnGridLayer(10); //Retrieves first floor layers and grid layer at index 10
                break;
            case "Evacuation Sites":
                ReturnGridLayer(11); //Retrieves first floor layers and grid layer at index 11
                break;
            case "Dining":
                ReturnGridLayer(12); //Retrieves first floor layers and grid layer at index 12
                break;
        }
        Toast.makeText(this, message,Toast.LENGTH_SHORT).show();
    }

    public void DialogPrompt()          //QFOSTER BEGIN
    {

        builder.setPositiveButton("Sign-in", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                usernameText = usernameInput.getText().toString();
                passwordText = passwordInput.getText().toString();

                if((usernameText + " " + passwordText).equals(getResources().getString(R.string.AdminData)))
                {
                    usernameInput.getEditableText().clear();
                    passwordInput.getEditableText().clear();
                    Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_LONG).show();
                    mapImageLayer.addDoneLoadingListener(() -> //Sets all map layers from the server to false by default    //DISABLE BUTTONS
                    {
                        for (int i =0;i<sublayers.size();i++)
                        {
                            sublayers.get(i).setVisible(false); //Sets all layers to false
                        }
                    });
                    //disable buttons
                    BasementButton.setVisibility(View.GONE);   //disables Basement button from being clickable
                    FloorButton1.setVisibility(View.GONE);     //disables floor buttons
                    FloorButton2.setVisibility(View.GONE);
                    FloorButton3.setVisibility(View.GONE);
                    FloorButton4.setVisibility(View.GONE);
                    FloorButton5.setVisibility(View.GONE);      //end floor button disable
                    mAddressSearchView.setVisibility(View.GONE);    //hides the search bar
                    drawer.setDrawerLockMode(1);                    //makes drawer unclickable
                    //end disable buttons


                    logoutButton.setVisibility(View.VISIBLE);       //Shows logout button
                    loggedIn = true;                                //bool to show if logged in (Used to disable navigation bar)
                    ShowFeatureLayers(); //show feature layers since we're logged in
                    annotationButton.setVisibility(View.VISIBLE);

                    mUtilitySearchView.setVisibility(View.VISIBLE); //show utility search bar

                   //mLocatorTask = new LocatorTask(getResources().getString(R.string.util_search)); //Initiates the geocode service

                }
                //JLM, added a supervisor tier that can accept or deny edit requests made by maintenance
                else if((usernameText + " " + passwordText).equals(getResources().getString(R.string.SupervisorData))){
                    usernameInput.getEditableText().clear();
                    passwordInput.getEditableText().clear();
                    Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_LONG).show();
                    mapImageLayer.addDoneLoadingListener(() -> //Sets all map layers from the server to false by default    //DISABLE BUTTONS
                    {
                        for (int i =0;i<sublayers.size();i++)
                        {
                            sublayers.get(i).setVisible(false); //Sets all layers to false
                        }
                    });
                    //enable
                    //disable buttons
                    BasementButton.setVisibility(View.GONE);   //disables Basement button from being clickable
                    FloorButton1.setVisibility(View.GONE);     //disables floor buttons
                    FloorButton2.setVisibility(View.GONE);
                    FloorButton3.setVisibility(View.GONE);
                    FloorButton4.setVisibility(View.GONE);
                    FloorButton5.setVisibility(View.GONE);      //end floor button disable
                    mAddressSearchView.setVisibility(View.GONE);    //hides the search bar
                    drawer.setDrawerLockMode(1);                    //makes drawer unclickable
                    //end disable buttons

                    logoutButton.setVisibility(View.VISIBLE);       //Shows logout button
                    loggedIn = true;                                //bool to show if logged in (Used to disable navigation bar)
                    ShowMaintenanceLayers(); //show feature layers since we're logged in
                    //allowFacultyEditAccess();
                    annotationButton.setVisibility(View.VISIBLE);
                    //FireHydrant.setVisibility(View.VISIBLE);

                    mUtilitySearchView.setVisibility(View.VISIBLE); //show utility search bar


                }//JLM, maintenance has access to water and can make requests to edit metadata
                else if((usernameText + " " + passwordText).equals(getResources().getString(R.string.MaintenanceData))){
                    usernameInput.getEditableText().clear();
                    passwordInput.getEditableText().clear();
                    Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_LONG).show();
                    mapImageLayer.addDoneLoadingListener(() ->
                    {
                        for(int i =0; i <sublayers.size(); i++){
                            sublayers.get(i).setVisible(false);
                        }
                    });
                    //disable buttons
                    BasementButton.setVisibility(View.GONE);   //disables Basement button from being clickable
                    FloorButton1.setVisibility(View.GONE);     //disables floor buttons
                    FloorButton2.setVisibility(View.GONE);
                    FloorButton3.setVisibility(View.GONE);
                    FloorButton4.setVisibility(View.GONE);
                    FloorButton5.setVisibility(View.GONE);      //end floor button disable
                    mAddressSearchView.setVisibility(View.GONE);    //hides the search bar
                    drawer.setDrawerLockMode(0);                    //makes drawer clickable
                    //end disable buttons

                    //enable fire buttons
                    lowFLow.setVisibility(View.VISIBLE);
                    mediumFlow.setVisibility(View.VISIBLE);
                    highFlow.setVisibility(View.VISIBLE);
                    //end enable fire buttons
                    logoutButton.setVisibility(View.VISIBLE);       //Shows logout button
                    loggedIn = true;                                //bool to show if logged in (Used to disable navigation bar)
                    ShowMaintenanceLayers();
                    //LoadFlowGroup(); //show feature layers since we're logged in
                    //editAccess = false; until supervisor grants access
                    annotationButton.setVisibility(View.VISIBLE);
                    FireHydrant.setVisibility(View.VISIBLE);
                    mUtilitySearchView.setVisibility(View.VISIBLE); //show utility search bar


                }
                else
                {
                    usernameInput.getEditableText().clear();    //clear username text box
                    passwordInput.getEditableText().clear();    //clear password text box
                    Toast.makeText(getApplicationContext(),"Login Failed", Toast.LENGTH_LONG).show();
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //search.setIconified(true);
                usernameInput.getEditableText().clear();
                passwordInput.getEditableText().clear();
            }
        });
        dialog = builder.create();
    }

    //=====Sets up the address SearchView. Uses MatrixCursor to show suggestions to the user as the user inputs text=====
    private void setupAddressSearchView()
    {
        mAddressSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public boolean onQueryTextSubmit(String address)
            {
                geoCodeTypedAddress(address); // geocode typed address
                mAddressSearchView.clearFocus(); // clear focus from search views
                mAddressSearchView.setIconified(true);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                //Dismisses all callouts showing
                //Used for countering a bug
                mMapView.getCallout().dismiss();
                if (!newText.equals("")) // as long as newText isn't empty, get suggestions from the locatorTask
                {
                    final ListenableFuture<List<SuggestResult>> suggestionsFuture = mLocatorTask.suggestAsync(newText);
                    suggestionsFuture.addDoneListener(new Runnable()
                    {
                        @Override public void run()
                        {
                            try
                            {
                                List<SuggestResult> suggestResults = suggestionsFuture.get(); // get the results of the async operation
                                MatrixCursor suggestionsCursor = new MatrixCursor(mColumnNames);
                                int key = 0;
                                for (SuggestResult result : suggestResults) // add each address suggestion to a new row
                                {
                                    suggestionsCursor.addRow(new Object[] { key++, result.getLabel() });
                                }
                                String[] cols = new String[] { COLUMN_NAME_ADDRESS };  // define SimpleCursorAdapter
                                int[] to = new int[] { R.id.suggestion_address };
                                final SimpleCursorAdapter suggestionAdapter = new SimpleCursorAdapter(MainActivity.this, R.layout.suggestion, suggestionsCursor, cols, to, 0);
                                mAddressSearchView.setSuggestionsAdapter(suggestionAdapter);
                                mAddressSearchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() // handle an address suggestion being chosen
                                {
                                    @Override public boolean onSuggestionSelect(int position)
                                    {
                                        return false;
                                    }
                                    @Override public boolean onSuggestionClick(int position)
                                    {
                                        MatrixCursor selectedRow = (MatrixCursor) suggestionAdapter.getItem(position); // get the selected row
                                        int selectedCursorIndex = selectedRow.getColumnIndex(COLUMN_NAME_ADDRESS); // get the row's index
                                        String address = selectedRow.getString(selectedCursorIndex);  // get the string from the row at index
                                        mAddressSearchView.setQuery(address, true); // use clicked suggestion as query
                                        mAddressSearchView.setIconified(true);
                                        invalidateOptionsMenu();
                                        return true;
                                    }
                                });
                            }
                            catch (Exception e)
                            {
                                Log.e(TAG, "Geocode suggestion error: " + e.getMessage());
                            }
                        }
                    });
                }
                return true;
            }
        });
    }

    //=====Identifies the Graphic at the tapped point.=====
    //@param motionEvent containing a tapped screen point
    private void identifyGraphic(MotionEvent motionEvent)
    {
        android.graphics.Point screenPoint = new android.graphics.Point(Math.round(motionEvent.getX()), Math.round(motionEvent.getY())); // get the screen point
        final ListenableFuture<IdentifyGraphicsOverlayResult> identifyResultsFuture = mMapView.identifyGraphicsOverlayAsync(mGraphicsOverlay, screenPoint, 10, false); // from the graphics overlay, get graphics near the tapped location
        identifyResultsFuture.addDoneListener(new Runnable()
        {
            @Override public void run()
            {
                try
                {
                    IdentifyGraphicsOverlayResult identifyGraphicsOverlayResult = identifyResultsFuture.get();
                    List<Graphic> graphics = identifyGraphicsOverlayResult.getGraphics();
                    if (graphics.size() > 0) // if a graphic has been identified
                    {
                        Graphic identifiedGraphic = graphics.get(0); //get the first graphic identified
                        showCallout(identifiedGraphic);
                        if(mLocationDisplay.isStarted() && !routeStops.isEmpty() && !mGraphicsOverlay.getGraphics().contains(routeGraphic))
                        {
                            //Set Start button button to visible when graphic has been identified
                            //Device location must be active
                            StartButton.setVisibility(View.VISIBLE);
                            StopButton.setVisibility(View.INVISIBLE);
                        }
                    }
                    else // dismiss all callouts when no graphic is identified
                    {
                        mMapView.getCallout().dismiss();
                    }
                }
                catch (Exception e)
                {
                    Log.e(TAG, "Identify error: " + e.getMessage());
                }
            }
        });
    }

    // create a TextView for the Callout
    private void showCallout(final Graphic graphic)
    {
        TextView calloutContent = new TextView(getApplicationContext());
        calloutContent.setTextColor(Color.BLACK);
        calloutContent.setText(graphic.getAttributes().get("Match_addr").toString()); // set the text of the Callout to graphic's attributes
        Callout mCallout = mMapView.getCallout(); // get Callout
        mCallout.setShowOptions(new Callout.ShowOptions(true, false, false)); // set Callout options: animateCallout: true, recenterMap: false, animateRecenter: false
        mCallout.setContent(calloutContent); // set the leader position and show the callout
        Point calloutLocation = graphic.computeCalloutLocation(graphic.getGeometry().getExtent().getCenter(), mMapView);
        mCallout.setGeoElement(graphic, calloutLocation);
        mCallout.show();
    }

    //=====Geocode an address passed in by the user====
    //@param address read in from searchViews
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void geoCodeTypedAddress(final String address)
    {
        if(address.equals("adminlogin"))        //QFOSTER
        {
            passwordInput.clearFocus();
            usernameInput.requestFocus();   //puts cursor on username textbox
            dialog.show();  //opens login screen

        }//JLM, prompt user/password login box for supervisor
        else if(address.equals("superlogin"))   //QFOSTER
        {
            passwordInput.clearFocus();
            usernameInput.requestFocus();   //puts cursor on username textbox
            dialog.show();  //opens login screen
        }
        else if(address.equals("mtxlogin"))   //QFOSTER
        {
            passwordInput.clearFocus();
            usernameInput.requestFocus();   //puts cursor on username textbox
            dialog.show();  //opens login screen
        }
        /*else if(address.equals("showall"))  //shortcut to login for debugging
        {
            Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_LONG).show();
            mapImageLayer.addDoneLoadingListener(() -> //Sets all map layers from the server to false by default    //DISABLE BUTTONS
            {
                for (int i =0;i<sublayers.size();i++)
                {
                    sublayers.get(i).setVisible(false); //Sets all layers to false
                }
            });
            //disable buttons
            BasementButton.setVisibility(View.GONE);   //diables Basement button from being clickable
            FloorButton1.setVisibility(View.GONE);     //disables floor buttons
            FloorButton2.setVisibility(View.GONE);
            FloorButton3.setVisibility(View.GONE);
            FloorButton4.setVisibility(View.GONE);
            FloorButton5.setVisibility(View.GONE);      //end floor button disable
            mAddressSearchView.setVisibility(View.GONE);    //hides the search bar
            drawer.setDrawerLockMode(1);                    //makes drawer unclickable
            //end disable buttons

            logoutButton.setVisibility(View.VISIBLE);       //Shows logout button
            loggedIn = true;                                //bool to show if logged in (Used to disable navigation bar)
            ShowFeatureLayers(); //show feature layers since we're logged in
            annotationButton.setVisibility(View.VISIBLE);

            mUtilitySearchView.setVisibility(View.VISIBLE); //show utility search bar

            //mLocatorTask = new LocatorTask(getResources().getString(R.string.util_search)); //Initiates the geocode service


        }*/
        else if (address != null) // check that address isn't null
        {
            mLocatorTask.addDoneLoadingListener(new Runnable() // Execute async task to find the address
            {
                @Override
                public void run()
                {
                    if (mLocatorTask.getLoadStatus() == LoadStatus.LOADED)
                    {
                        final ListenableFuture<List<GeocodeResult>> geocodeResultListenableFuture = mLocatorTask.geocodeAsync(address, mAddressGeocodeParameters); // Call geocodeAsync passing in an address
                        geocodeResultListenableFuture.addDoneListener(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                try
                                {
                                    List<GeocodeResult> geocodeResults = geocodeResultListenableFuture.get(); // Get the results of the async operation
                                    if (geocodeResults.size() > 0)
                                    {
                                        displaySearchResult(geocodeResults.get(0));
                                    }
                                    else
                                    {
                                        Toast.makeText(getApplicationContext(), getString(R.string.location_not_found) + " " + address, Toast.LENGTH_LONG).show();
                                    }
                                }
                                catch (InterruptedException | ExecutionException e)
                                {
                                    Log.e(TAG, "Geocode error: " + e.getMessage());
                                    Toast.makeText(getApplicationContext(), getString(R.string.geo_locate_error), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                    else
                    {
                        Log.i(TAG, "Trying to reload locator task");
                        mLocatorTask.retryLoadAsync();
                    }
                }
            });
            mLocatorTask.loadAsync();
            mAddressSearchView.setIconified(true);
            invalidateOptionsMenu();
        }
    }

    //=====Turns a GeocodeResult into a Point and adds it to a GraphicOverlay which is then drawn on the map.=====
    //@param geocodeResult a single geocode result
    private void displaySearchResult(GeocodeResult geocodeResult)
    {
        mMapView.getGraphicsOverlays().clear(); //clear map of existing graphics
        mGraphicsOverlay.getGraphics().clear();
        Point resultPoint = geocodeResult.getDisplayLocation(); // create graphic object for resulting location
        Graphic resultLocGraphic = new Graphic(resultPoint, geocodeResult.getAttributes(), mPinSourceSymbol);
        mGraphicsOverlay.getGraphics().add(resultLocGraphic); // add graphic to location layer
        mMapView.getGraphicsOverlays().add(mGraphicsOverlay); // set the graphics overlay to the map
        if(!loggedIn) {
            FloorSearchView(geocodeResult.getAttributes().toString());
        }
        showCallout(resultLocGraphic);
        mMapView.setViewpointAsync(new Viewpoint(geocodeResult.getExtent()),1); // zoom map to result over 3 seconds

        DestinationStop = new Stop(geocodeResult.getExtent().getCenter()); // Creates a stop for the destination location
        routeStops.clear(); //Clears the array of any existing data
        routeStops.add(DestinationStop); //Adds the first stop location

        StartButton.setVisibility(View.INVISIBLE);
        StopButton.setVisibility(View.INVISIBLE);

        if(mLocationDisplay.isStarted())
        {
            //Sets Start button to visible
            //Used for initiating the routing sequence
            StartButton.setVisibility(View.VISIBLE);
        }
    }

    //=====A void function to show the floor based on the parsed text of the address string=====
    public void FloorSearchView(final String address)
    {
        for (int i=0; i < address.length(); i++)
        {
            if(address.charAt(i) == '0')
            {
                ReturnFloorView(0);
                Toast.makeText(MainActivity.this,"Basement Floor",Toast.LENGTH_SHORT).show();
                break;
            }
            else if(address.charAt(i) == '1')
            {
                ReturnFloorView(1);
                Toast.makeText(MainActivity.this,"1st Floor",Toast.LENGTH_SHORT).show();
                break;
            }
            else if(address.charAt(i) == '2')
            {
                ReturnFloorView(2);
                Toast.makeText(MainActivity.this,"2nd Floor",Toast.LENGTH_SHORT).show();
                break;
            }
            else if(address.charAt(i) == '3')
            {
                ReturnFloorView(3);
                Toast.makeText(MainActivity.this,"3rd Floor",Toast.LENGTH_SHORT).show();
                break;
            }
            else if(address.charAt(i) == '4')
            {
                ReturnFloorView(4);
                Toast.makeText(MainActivity.this,"4th Floor",Toast.LENGTH_SHORT).show();
                break;
            }
            else if(address.charAt(i) == '5')
            {
                ReturnFloorView(5);
                Toast.makeText(MainActivity.this,"5th Floor",Toast.LENGTH_SHORT).show();
                break;
            }
            else if(i == address.length()-1)
            {
                ReturnFloorView(1);
                break;
            }
        }
    }
    //====A void function to show specific map layers based on the flow rate number====
    //flow layers are referenced from the server
    public void ReturnFlowGroup(int flow_rate){
        StartButton.setVisibility(View.INVISIBLE);
        StopButton.setVisibility(View.INVISIBLE);
        mMapView.getCallout().dismiss();
        flowmapImageLayer.addDoneLoadingListener(() ->
        {
            for(int i =0; i <flowsublayers.size();i++){
                flowsublayers.get(i).setVisible(false);
            }
            //enable all layers relating to certain structures to be visible
            flowsublayers.get(0).setVisible(true);

            if(flow_rate == 700){
                flowsublayers.get(0).setVisible(true);
            }
            else if(flow_rate == 1000){
                flowsublayers.get(1).setVisible(true);
            }
            else if(flow_rate == 1500){
                flowsublayers.get(2).setVisible(true);
            }
        });
    }
//
    //=====A void function to show specific map layers based on the floor level number=====
    //Floor layers are referenced from the server
    public void ReturnFloorView(int floor_level)
    {
        StartButton.setVisibility(View.INVISIBLE);
        StopButton.setVisibility(View.INVISIBLE);
        mMapView.getCallout().dismiss(); //Dismisses all callouts showing
        mapImageLayer.addDoneLoadingListener(() ->
        {
            for (int i =0;i<sublayers.size();i++)
            {
                sublayers.get(i).setVisible(false); //Sets all layers to false
            }

            //Enables all the layers relating to certain structures to be visible
            //==================================
            sublayers.get(6).setVisible(true);
            sublayers.get(8).setVisible(true);
            sublayers.get(27).setVisible(true);
            sublayers.get(28).setVisible(true);
            sublayers.get(29).setVisible(true);
            sublayers.get(30).setVisible(true);
            sublayers.get(31).setVisible(true);
            sublayers.get(32).setVisible(true);
            sublayers.get(33).setVisible(true);
            sublayers.get(34).setVisible(true);
            sublayers.get(35).setVisible(true);
            sublayers.get(36).setVisible(true);
            //==================================

            if(floor_level == 0) //Basement layers
            {
                sublayers.get(5).setVisible(true); //Sets layer at index 5 to true
                sublayers.get(19).setVisible(true); //Sets layer at index 19 to true
                sublayers.get(20).setVisible(true); //Sets layer at index 20 to true
                sublayers.get(26).setVisible(true); //Sets layer at index 26 to true
            }
            else if(floor_level == 1) //First floor layers
            {
                sublayers.get(4).setVisible(true);
                sublayers.get(17).setVisible(true);
                sublayers.get(18).setVisible(true);
                sublayers.get(25).setVisible(true);
            }
            else if(floor_level == 2) //Second floor layers
            {
                sublayers.get(3).setVisible(true);
                sublayers.get(15).setVisible(true);
                sublayers.get(16).setVisible(true);
                sublayers.get(24).setVisible(true);
            }
            else if(floor_level == 3) //Third floor layers
            {
                sublayers.get(2).setVisible(true);
                sublayers.get(13).setVisible(true);
                sublayers.get(14).setVisible(true);
                sublayers.get(23).setVisible(true);
            }
            else if(floor_level == 4) //Fourth floor layers
            {
                sublayers.get(1).setVisible(true);
                sublayers.get(11).setVisible(true);
                sublayers.get(12).setVisible(true);
                sublayers.get(22).setVisible(true);
            }
            else if(floor_level == 5) //Fifth floor layers
            {
                sublayers.get(0).setVisible(true);
                sublayers.get(9).setVisible(true);
                sublayers.get(10).setVisible(true);
                sublayers.get(21).setVisible(true);
            }
        });
    }

    //=====A void function to show specific grid layers based on the grid index=====
    //Grid layers are referenced from the server
    //Returns grid layers based on index
    public void ReturnGridLayer(int index)
    {
        StartButton.setVisibility(View.INVISIBLE);
        StopButton.setVisibility(View.INVISIBLE);
        mapImageLayer.addDoneLoadingListener(() ->
        {
            for (int i=0;i<sublayers.size();i++)
            {
                sublayers.get(i).setVisible(false);
            }

            //Enables the layers relating to the first floor and structures
            //==================================
            sublayers.get(4).setVisible(true);
            sublayers.get(6).setVisible(true);
            sublayers.get(7).setVisible(true);
            sublayers.get(8).setVisible(true);
            sublayers.get(17).setVisible(true);
            sublayers.get(18).setVisible(true);
            sublayers.get(25).setVisible(true);
            sublayers.get(27).setVisible(true);
            sublayers.get(28).setVisible(true);
            sublayers.get(29).setVisible(true);
            sublayers.get(30).setVisible(true);
            sublayers.get(31).setVisible(true);
            sublayers.get(32).setVisible(true);
            sublayers.get(33).setVisible(true);
            sublayers.get(34).setVisible(true);
            sublayers.get(35).setVisible(true);
            sublayers.get(36).setVisible(true);
            //==================================

            for (int j =0;j<18;j++)
            {
                sublayers.get(7).getSubLayerContents().get(j).setVisible(false); //Sets all grid layers to false
            }
            if (index == 1)
            {
                sublayers.get(7).getSubLayerContents().get(0).setVisible(true); //Sets sublayer index 0 of parent layer grid 7 to true
            }
            else if (index == 2)
            {
                sublayers.get(7).getSubLayerContents().get(1).setVisible(true); //Sets sublayer index 1 of parent layer grid to true
            }
            else if (index == 3)
            {
                sublayers.get(7).getSubLayerContents().get(2).setVisible(true); //Sets sublayer index 2 of parent layer grid to true
            }
            else if (index == 4)
            {
                sublayers.get(7).getSubLayerContents().get(3).setVisible(true); //Sets sublayer index 3 of parent layer grid to true
            }
            else if (index == 5)
            {
                sublayers.get(7).getSubLayerContents().get(4).setVisible(true); //Sets sublayer index 4 of parent layer grid to true
            }
            else if (index == 6)
            {
                sublayers.get(7).getSubLayerContents().get(5).setVisible(true); //Sets sublayer index 5 of parent layer grid to true
            }
            else if (index == 7)
            {
                sublayers.get(7).getSubLayerContents().get(6).setVisible(true); //Sets sublayer index 6 of parent layer grid to true
            }
            else if (index == 8)
            {
                sublayers.get(7).getSubLayerContents().get(7).setVisible(true); //Sets sublayer index 7 of parent layer grid to true
            }
            else if (index == 9)
            {
                sublayers.get(7).getSubLayerContents().get(8).setVisible(true); //Sets sublayer index 8 of parent layer grid to true
            }
            else if (index == 10)
            {
                sublayers.get(7).getSubLayerContents().get(9).setVisible(true); //Sets sublayer index 9 of parent layer grid to true
            }
            else if (index == 11)
            {
                sublayers.get(7).getSubLayerContents().get(10).setVisible(true); //Sets sublayer index 10 of parent layer grid to true
                sublayers.get(7).getSubLayerContents().get(11).setVisible(true); //Sets sublayer index 11 of parent layer grid to true
            }
            else if (index == 12)
            {
                sublayers.get(7).getSubLayerContents().get(12).setVisible(true); //Sets sublayer index 12 of parent layer grid to true
            }
        });
    }

    //=====A void function to create a route between device location and a destination=====
    //Device location must be enabled
    public void RoutingSequence()
    {
        listenableFuture.addDoneListener(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    if (listenableFuture.isDone())
                    {
                        DestinationStop = routeStops.get(0); //Finds the route stop from previously used routStop array
                        DevicePointLocation = new Stop(mLocationDisplay.getLocation().getPosition()); // Creates a stop for the device location
                        mRouteParams = mRouteTask.createDefaultParametersAsync().get();
                        routeStops.clear();
                        routeStops.add(DestinationStop);
                        routeStops.add(DevicePointLocation);
                        mRouteParams.setStops(routeStops);
                        result = mRouteTask.solveRouteAsync(mRouteParams).get();
                        routes = result.getRoutes();
                        mRoute = (Route) routes.get(0);
                        mGraphicsOverlay.getGraphics().remove(routeGraphic);
                        routeGraphic = new Graphic(mRoute.getRouteGeometry(), mRouteSymbol);
                        mGraphicsOverlay.getGraphics().add(routeGraphic);
                    }
                }
                catch (Exception e)
                {
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }

    //=====When app is in a paused state=====
    //Resets the map to initial viewpoint
    @Override
    protected void onPause()
    {
        super.onPause();
        mMapView.pause();
    }

    //=====When app resumes its activity after a pause state=====
    //Resets the map to initial viewpoint
    @Override
    protected void onResume()
    {
        super.onResume();
        mMapView.resume();
    }

    //=====When app is closed=====
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mMapView.dispose();
    }

    //=====Sets visibility of annotation and point group utility layers to false=====
    private void HideInitialLayers()
    {
        for(int i=number_of_non_feature_layers + 50;i<map.getOperationalLayers().size();i++)
        {
            if(i >= number_of_non_feature_layers + 50)
            {
                map.getOperationalLayers().get(i).setVisible(false);
            }
        }
    }

    //=====Sets visibility of feature layers to false=====
    private void HideFeatureLayers()
    {
        for(int i=number_of_non_feature_layers;i<map.getOperationalLayers().size();i++)
        {
            if(i>=number_of_non_feature_layers)
            {
                map.getOperationalLayers().get(i).setVisible(false);
            }
        }
    }

    //=====Sets visibility of feature layers to true=====
    private void ShowFeatureLayers()
    {
        for(int i=number_of_non_feature_layers;i <50 + number_of_non_feature_layers ;i++)
        {
            if(i>=number_of_non_feature_layers)
            {
                map.getOperationalLayers().get(i).setVisible(true);
            }
        }
    }

    //JLM,=====Sets visibility of maintenance feature layers ======
    private void ShowMaintenanceLayers()
    {
        /*
*private SceneView mSceneView;

    private void setupScene() {
        if (mSceneView != null) {
            Basemap.Type basemapType = Basemap.Type.IMAGERY_WITH_LABELS;
            ArcGISScene scene = new ArcGISScene(basemapType);
            mSceneView.setScene(scene);
            addFireHydrantLayer();
            setElevationSource(scene);
        }
    }

    private void addFireHydrantLayer() {

        String url = "https://services9.arcgis.com/KlzFarGOomCHxtRQ/arcgis/rest/services/CSUSB_Utilities_Project_2020/FeatureServer/49";
final ServiceFeatureTable serviceFeatureTable = new ServiceFeatureTable(url);
        FeatureLayer featureLayer = new FeatureLayer(serviceFeatureTable);
        mSceneView.getScene().getOperationalLayers().add(featureLayer);

        Camera camera = new Camera(
        34.1818096,
        -117.3259524,
        16000.0,
        0.0,
        50.0,
        0);
        mSceneView.setViewpointCamera(camera);
        }

private void setElevationSource(ArcGISScene scene) {
        ArcGISTiledElevationSource elevationSource = new ArcGISTiledElevationSource(
        "http://elevation3d.arcgis.com/arcgis/rest/services/WorldElevation3D/Terrain3D/ImageServer");
        scene.getBaseSurface().getElevationSources().add(elevationSource);
        }
*/
        //System.out.print(number_of_non_feature_layers);//debugging purposes
        for(int i = number_of_non_feature_layers; i < 50 + number_of_non_feature_layers; i++){

            if( i >=12 && i<=30){
                map.getOperationalLayers().get(i).setVisible(true);
            }
        }

    }


    //=====Detects map screen clicks=====
    @SuppressLint("ClickableViewAccessibility")
    private void DetectMapView()
    {
        //mSceneView = mMapView, DefaultSceneViewOnTouchListener(context: this, mSceneView)
        mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(this, mMapView)
        {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent motionEvent)
            {
                screenPoint.set(Math.round(motionEvent.getX()),Math.round(motionEvent.getY())); //Gets screen point
                //mSceneView = mMapView
                mapPoint = mMapView.screenToLocation(screenPoint); //Converst screen point to map view point
                selectFeaturesAt(mapPoint); //Start feature selection function
                return true;
            }
        });
    }

    //=====Adds the utility annotation group feature layer=====
    private void LoadAnnotationGroup()
    {
        ServiceFeatureTable serviceFeatureTable = new ServiceFeatureTable(getResources().getString(R.string.annotation_group));
        FeatureLayer featureLayer = new FeatureLayer(serviceFeatureTable);
        featureLayer.setVisible(false); //make sure the layer isn't visible while being loaded in
        map.getOperationalLayers().add(featureLayer);
        layerIndex.add(3); //Adds only feature layer index 3
    }

    //=====Adds the utility point group feature layers=====
    private void LoadPointGroup()
    {
        for(int i = 1;i<32;i++)
        {
            if(i!=3) //Add all feature layers except index 3
            {
                ServiceFeatureTable serviceFeatureTable = new ServiceFeatureTable(getResources().getString(R.string.point_group)+i);
                FeatureLayer featureLayer = new FeatureLayer(serviceFeatureTable);
                featureLayer.setVisible(false); //make sure the layer isn't visible while being loaded in
                map.getOperationalLayers().add(featureLayer);
                layerIndex.add(i);
            }
        }
    }

    //======Adds flow point layers============
    private void LoadFlowGroup(){
        int layer =  0;
        ServiceFeatureTable serviceFeatureTable = new ServiceFeatureTable(getResources().getString(R.string.flow_group)+layer);
        FeatureLayer featureLayer = new FeatureLayer(serviceFeatureTable);
        featureLayer.setVisible(false);
        map.getOperationalLayers().add(featureLayer);
        layerIndex.add(layer);
    }

    //=====Adds the utility polyline group feature layers=====
    private void LoadPolylineGroup()
    {
        for(int i = 1;i<54;i++)
        {
            if(i!=3 && i!=7 && i!=8) //Add all feature layers except indeces 3, 7, and 8
            {
                ServiceFeatureTable serviceFeatureTable = new ServiceFeatureTable(getResources().getString(R.string.polyline_group)+i);
                FeatureLayer featureLayer = new FeatureLayer(serviceFeatureTable);
                featureLayer.setVisible(false); //make sure the layer isn't visible while being loaded in
                map.getOperationalLayers().add(featureLayer);
                layerIndex.add(i);
            }
        }
    }

    //=====A function that returns a variable of type ServiceFeatureTable when given a map operational layer index=====
    ServiceFeatureTable returnFeatureServiceLink(int Operational_Layer_Index)
    {
        //KL, need to do a lot of number fudging since we have non_feature layers
        ServiceFeatureTable serviceFeatureTable;
        if(Operational_Layer_Index < number_of_non_feature_layers + 50)
        {
            serviceFeatureTable = new ServiceFeatureTable(getResources().getString(R.string.polyline_group)+layerIndex.get(Operational_Layer_Index - number_of_non_feature_layers));
            System.out.println("Utility Polyline Group");
        }
        else if (Operational_Layer_Index < number_of_non_feature_layers + 80)
        {
            serviceFeatureTable = new ServiceFeatureTable(getResources().getString(R.string.point_group)+layerIndex.get(Operational_Layer_Index - number_of_non_feature_layers));
            System.out.println("Utility Point Group");
        }
        else
        {
            serviceFeatureTable = new ServiceFeatureTable(getResources().getString(R.string.annotation_group));
            System.out.println("Utility Annotation Group");
        }
        System.out.println("Feature Layer ID: " + layerIndex.get(Operational_Layer_Index - number_of_non_feature_layers));
        return serviceFeatureTable;
    }

    //=====Shows feature layer attributes when given query parameters, map point, and a service feature table link=====
    private void ShowAttributes(QueryParameters query, Point clickPoint, ServiceFeatureTable mServiceFeatureTable)
    {
        //Loads all fields that exist in the feature table service link of the feature query result
        final ListenableFuture<FeatureQueryResult> future = mServiceFeatureTable.queryFeaturesAsync(query, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL);
        future.addDoneListener(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run()
            {
                try
                {
                    FeatureQueryResult result = future.get(); //Retrieves feature query results from the service feature table link
                    Iterator<Feature> iterator = result.iterator(); //Creates an array of feature attributes
                    //-----Initial setup for the callout text box-----
                    TextView calloutContent = new TextView(getApplicationContext());
                    calloutContent.setTextColor(Color.BLACK);
                    calloutContent.setSingleLine(false);
                    calloutContent.setVerticalScrollBarEnabled(true);
                    calloutContent.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
                    calloutContent.setMovementMethod(new ScrollingMovementMethod());
                    calloutContent.setLines(13); //Sets 13 lines of text space for 12 attributes plus an extra line space
                    //------------------------------------------------
                    Feature feature;
                    while (iterator.hasNext()) //Iterates through all existing feature attribute fields that exist in the iterator array
                    {
                        feature = iterator.next();
                        Map<String, Object> attr = feature.getAttributes(); //Retrieves all available feature attributes
                        Set<String> keys = attr.keySet(); //Retrieves feature attribute fields
                        calloutContent.setText("Layer: "+attr.get("Layer") +"\n----------------------------------------\n"); //Creates title text for callout message
                        for (String key : keys) {
                            Object value = attr.get(key); //Value of the corresponding attribute field
                            if(key.equals("Layer")) //Skips the appending process for when key returns attribute field 'Layer'
                            {
                                continue;
                            }
                            if (value instanceof GregorianCalendar) //Reformats date-type value to 'dd-MMM-yyyy' if this data exists
                            {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
                                value = simpleDateFormat.format(((GregorianCalendar) value).getTime());
                            }
                            calloutContent.append(key + " | " + value + "\n"); //Formats each callout line text to 'Field | Value'
                        }
                        if(!iterator.hasNext()) //Show callout when the next iterator element does not exist
                        {
                            mMapView.setViewpointGeometryAsync(clickPoint.getExtent(), 100);
                            mCallout.setLocation(clickPoint);
                            mCallout.setContent(calloutContent);
                            mCallout.show();
                        }
                    }
                }
                catch (Exception e)
                {
                    Log.e(getResources().getString(R.string.app_name), "Select feature failed: " + e.getMessage());
                }
            }
        });
    }

    //=====Highlights all queried features=====
    private void selectFeaturesAt(Point point)
    {
        if(loggedIn)
        {
            mGraphicsOverlay.getGraphics().clear();
        }

        mCallout.dismiss(); //Dismisses all callouts
        final double mapTolerance = 10 * mMapView.getUnitsPerDensityIndependentPixel(); //Map click tolerance
        Envelope envelope = new Envelope(point.getX() - mapTolerance, point.getY() - mapTolerance,point.getX() + mapTolerance, point.getY() + mapTolerance, mMapView.getSpatialReference());
        QueryParameters query = new QueryParameters();
        query.setGeometry(envelope); //Used to set a geometric boundary to check for all features surrounding the clicked map point within that boundary

        breakLoop = 1; //Initial integer value
        for (Layer layer : mMapView.getMap().getOperationalLayers()) //Cycles through all layers in the map operational layers array
        {
            if(breakLoop==0)
            {
                break;
            }
            if (layer instanceof com.esri.arcgisruntime.layers.ArcGISMapImageLayer)
            {
                continue;
            }
            final FeatureLayer featureLayer = (FeatureLayer) layer;
            final ListenableFuture<FeatureQueryResult> featureQueryResultFuture = featureLayer.selectFeaturesAsync(query, FeatureLayer.SelectionMode.NEW); //Highlights feature that is found in the query
            featureQueryResultFuture.addDoneListener(() -> {
                try
                {
                    //Checks for only the first visible feature result in the query
                    if(featureLayer.isVisible() && featureQueryResultFuture.get().iterator().hasNext() && breakLoop == 1)
                    {

                        System.out.println("Map Operational Layer Index: " + map.getOperationalLayers().indexOf(layer));
                        ServiceFeatureTable mServiceFeatureTable = returnFeatureServiceLink(map.getOperationalLayers().indexOf(layer)); //Returns service feature table link
                        ShowAttributes(query,point, mServiceFeatureTable); //Show attribute for this feature
                        breakLoop = 0; //Resets integer value so that no more additional feature query attributes can be retrieved
                    }
                }
                catch (Exception e)
                {
                    e.getStackTrace();
                }
            });
        }
    }

    private void setupAddressSearchViewUtil()
    {
        mUtilitySearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public boolean onQueryTextSubmit(String address)
            {
                geoCodeTypedAddressUtil(address); // geocode typed address
                mUtilitySearchView.clearFocus(); // clear focus from search views
                mUtilitySearchView.setIconified(true);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                //Dismisses all callouts showing
                //Used for countering a bug
                mMapView.getCallout().dismiss();
                if (!newText.equals("")) // as long as newText isn't empty, get suggestions from the locatorTask
                {
                    final ListenableFuture<List<SuggestResult>> suggestionsFuture = mLocatorTaskUtil.suggestAsync(newText);
                    suggestionsFuture.addDoneListener(new Runnable()
                    {
                        @Override public void run()
                        {
                            try
                            {
                                List<SuggestResult> suggestResults = suggestionsFuture.get(); // get the results of the async operation
                                MatrixCursor suggestionsCursor = new MatrixCursor(mColumnNames);
                                int key = 0;
                                for (SuggestResult result : suggestResults) // add each address suggestion to a new row
                                {
                                    suggestionsCursor.addRow(new Object[] { key++, result.getLabel() });
                                }
                                String[] cols = new String[] { COLUMN_NAME_ADDRESS };  // define SimpleCursorAdapter
                                int[] to = new int[] { R.id.suggestion_address };
                                final SimpleCursorAdapter suggestionAdapter = new SimpleCursorAdapter(MainActivity.this, R.layout.suggestion, suggestionsCursor, cols, to, 0);
                                mUtilitySearchView.setSuggestionsAdapter(suggestionAdapter);
                                mUtilitySearchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() // handle an address suggestion being chosen
                                {
                                    @Override public boolean onSuggestionSelect(int position)
                                    {
                                        return false;
                                    }
                                    @Override public boolean onSuggestionClick(int position)
                                    {
                                        MatrixCursor selectedRow = (MatrixCursor) suggestionAdapter.getItem(position); // get the selected row
                                        int selectedCursorIndex = selectedRow.getColumnIndex(COLUMN_NAME_ADDRESS); // get the row's index
                                        String address = selectedRow.getString(selectedCursorIndex);  // get the string from the row at index
                                        mUtilitySearchView.setQuery(address, true); // use clicked suggestion as query
                                        mUtilitySearchView.setIconified(true);
                                        invalidateOptionsMenu();
                                        return true;
                                    }
                                });
                            }
                            catch (Exception e)
                            {
                                Log.e(TAG, "Geocode suggestion error(Util): " + e.getMessage());
                            }
                        }
                    });
                }
                return true;
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void geoCodeTypedAddressUtil(final String address)      //geocode for utilitites layers
    {
        if (address != null) // check that address isn't null
        {
            mLocatorTaskUtil.addDoneLoadingListener(new Runnable() // Execute async task to find the address
            {
                @Override
                public void run()
                {
                    if (mLocatorTaskUtil.getLoadStatus() == LoadStatus.LOADED)
                    {
                        final ListenableFuture<List<GeocodeResult>> geocodeResultListenableFuture = mLocatorTaskUtil.geocodeAsync(address, mAddressGeocodeParameters); // Call geocodeAsync passing in an address
                        geocodeResultListenableFuture.addDoneListener(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                try
                                {
                                    List<GeocodeResult> geocodeResults = geocodeResultListenableFuture.get(); // Get the results of the async operation
                                    if (geocodeResults.size() > 0)
                                    {
                                        displaySearchResult(geocodeResults.get(0));
                                    }
                                    else
                                    {
                                        Toast.makeText(getApplicationContext(), getString(R.string.location_not_found) + " " + address, Toast.LENGTH_LONG).show();
                                    }
                                }
                                catch (InterruptedException | ExecutionException e)
                                {
                                    Log.e(TAG, "Geocode error (Util): " + e.getMessage());
                                    Toast.makeText(getApplicationContext(), getString(R.string.geo_locate_error), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                    else
                    {
                        Log.i(TAG, "Trying to reload locator task");
                        mLocatorTaskUtil.retryLoadAsync();
                    }
                }
            });
            mLocatorTaskUtil.loadAsync();
            mUtilitySearchView.setIconified(true);
            invalidateOptionsMenu();
        }
    }
}