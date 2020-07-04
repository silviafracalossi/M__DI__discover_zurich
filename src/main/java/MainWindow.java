
import com.esri.arcgisruntime.geometry.*;
import com.esri.arcgisruntime.mapping.view.Callout;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import javafx.util.Duration;
import org.eclipse.rdf4j.model.util.Literals;
import org.eclipse.rdf4j.query.BindingSet;
import java.util.List;
import static java.lang.Integer.parseInt;

public class MainWindow extends Application {

    private int hexRed = 0xFFFF0000;
    private int hexBlue = 0xFF0000FF;

    // Variable to retrieve data
    private DataLoader dl;

    // Variables to manage filters
    private String filters = "";
    private BindingSet selected_district;
    private List<BindingSet> markers_set;
    private int district_id;

    // Graphical general component
    private Stage general_stage;
    private StackPane stackPane;
    private Scene scene;

    // Components of visualization
    private GraphicsOverlay graphicsOverlay;
    private VBox controlsVBox;
    private MapView mapView;

    // Others
    private static final Duration DURATION = new Duration(500);

    @Override
    public void start(Stage stage) throws Exception {

        // Loading the DataLoader class
        dl = new DataLoader();

        // Adding no filter to initial view
        filters = "";

        // Starting graphical components
        stackPane = new StackPane();
        scene = new Scene(stackPane);
        general_stage = stage;

        // Starting the window based on settings
        startWindow();
    }

    private void startWindow() {

        System.out.println("[INFO] Starting the window...");

        // Setting the stage of the visualization
        general_stage.setTitle("Explore Zurich");
        general_stage.setWidth(1000);
        general_stage.setHeight(650);
        general_stage.setScene(scene);
        general_stage.show();

        // Setting the map of the visualization
        mapView = new MapView();
        double zurich_latitude = 47.3769;
        double zurich_longitude = 8.5417;
        setupMap(zurich_latitude, zurich_longitude);

        // Creating overlay
        setupGraphicsOverlay();

        // Creating filter panel
        setupFilterPanel(filters);

        // Retrieving the markers and visualizing them on the map
        if(this.filters.compareTo("") != 0) {
            markers_set = dl.getMarkerData(district_id, filters);

            if (markers_set != null) {
                addMarkers();
            }
        }

        // Preparing the home - Normal Setting
        if (filters.compareTo("") == 0) {
            System.out.println("[INFO] Showing normal visualization");
            // TODO decomment
//            List<BindingSet> districts_results = dl.getDistrictAreas();
//            if (districts_results != null) {
//                addDistricts(districts_results);
//            }
        } else {

            // Preparing the home - Display District
            if (filters.contains("d")) {
                System.out.println("[INFO] Visualizing district");
                addSingleDistrict(selected_district);
            }
        }

//        // Get the map view's callout
//        Callout callout = mapView.getCallout();
//        mapView.setOnMouseClicked(e -> {
//
//            // check that the primary mouse button was clicked and user is not panning
//            if (e.getButton() == MouseButton.PRIMARY && e.isStillSincePress()) {
//
//                // create a point from where the user clicked and create a map point from a point
//                Point2D point = new Point2D(e.getX(), e.getY());
//                Point mapPoint = mapView.screenToLocation(point);
//
//                // Converting into lat lon coordinates
//                String latLonDecimalDegrees = CoordinateFormatter.toLatitudeLongitude(mapPoint, CoordinateFormatter
//                        .LatitudeLongitudeFormat.DECIMAL_DEGREES, 4);
//
//                // Splitting location into lat and lon
//                String coordinates[] = latLonDecimalDegrees.split(" ");
//                Double lat = Double.parseDouble(coordinates[0].substring(0, coordinates[0].length() - 1));
//                Double lon = Double.parseDouble(coordinates[1].substring(0, coordinates[1].length() - 1));
//
//                // Check closest point
//                double error = 0.0005;
//                if (markers_set != null) {
//                    BindingSet correct_marker = null;
//
//                    // Look for the markers present in the map
//                    for (BindingSet marker: markers_set) {
//                        Point marker_location = point_from_string(Literals.getLabel(marker.getValue("locat"), ""));
//                        double marker_lat = marker_location.getY();
//                        double marker_lon = marker_location.getX();
//
//                        // Check if it is the correct marker
//                        if (lat+error > marker_lat && lat-error < marker_lat &&
//                                lon+error > marker_lon && lon-error < marker_lon) {
//                            correct_marker = marker;
//                            break;
//                        }
//                    }
//
//                    // If the point was found among the markers
//                    if (correct_marker != null) {
//                        createPopUp(correct_marker, callout, mapPoint);
//                    }
//                }
//
//            } else if (e.getButton() == MouseButton.SECONDARY && e.isStillSincePress()) {
//                callout.dismiss();
//            }
//        });

        // Adding map and control panel to the stack pane
        stackPane.getChildren().addAll(mapView, controlsVBox);
        StackPane.setAlignment(controlsVBox, Pos.TOP_LEFT);
        StackPane.setMargin(controlsVBox, new Insets(10, 0, 0, 10));
    }

    // Setting the center of the map and zoom
    private void setupMap(double latitude, double longitude) {
        if (mapView != null) {
            Basemap.Type basemapType = Basemap.Type.STREETS_VECTOR;
            int levelOfDetail = 12;
            ArcGISMap map = new ArcGISMap(basemapType, latitude, longitude, levelOfDetail);
            mapView.setMap(map);
        }
    }

    // Adding the graphics overlay
    private void setupGraphicsOverlay() {
        if (mapView != null) {
            graphicsOverlay = new GraphicsOverlay();
            mapView.getGraphicsOverlays().add(graphicsOverlay);
        }
    }

    // Creating the filter panel visualized on the left side
    private void setupFilterPanel(String filters) {

        // =====================Creating the filter panel===============================
        controlsVBox = new VBox(6);
        controlsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0,0,0,0.5)"),
                CornerRadii.EMPTY, Insets.EMPTY)));
        controlsVBox.setPadding(new Insets(7.0));
        controlsVBox.setMaxSize(260, 120);
        controlsVBox.getStyleClass().add("panel-region");

        // ====================Creating the title of the panel===========================
        Label title = new Label("Filter Section");
        title.setFont(new Font("Arial", 20));
        title.setAlignment(Pos.CENTER);
        title.setStyle("-fx-text-fill: white;");

        // Adding the header box
        HBox title_box = new HBox(10);
        title_box.setAlignment(Pos.CENTER);
        title_box.setPadding(new Insets(7.0));
        title_box.getChildren().addAll(title);

        // =======================Creating the District label=============================
        Label district_label = new Label("District");
        district_label.setFont(new Font("Arial", 15));
        district_label.setAlignment(Pos.CENTER_LEFT);
        district_label.setStyle("-fx-text-fill: white;");

        // Add the district choice box
        ChoiceBox district_choice_box = new ChoiceBox();
        district_choice_box.getItems().add("");
        for (int i=1; i<13; i++) {
            district_choice_box.getItems().add("District " + i);
        }

        // Adding the District box
        HBox district_box = new HBox(10);
        district_box.setAlignment(Pos.CENTER);
        district_box.getChildren().addAll(district_label, district_choice_box);
        district_box.setPadding(new Insets(7.0));

        // =======================Creating the Visualize part===============================
        Label visualize_label = new Label("Visualize the locations for:");
        visualize_label.setFont(new Font("Arial", 15));
        visualize_label.setAlignment(Pos.TOP_LEFT);
        visualize_label.setStyle("-fx-text-fill: white;");
        visualize_label.setPadding(new Insets(0,0,7,0));

        // ==============Parking checks
        CheckBox parking_check = new CheckBox("Parking");
        parking_check.setFont(new Font("Arial", 15));
        parking_check.setAlignment(Pos.CENTER_LEFT);
        parking_check.setStyle("-fx-text-fill: white;");
        parking_check.setPadding(new Insets(0,0,0,10));

        CheckBox car_parking_check = new CheckBox("Car Parking");
        car_parking_check.setFont(new Font("Arial", 15));
        car_parking_check.setAlignment(Pos.CENTER_LEFT);
        car_parking_check.setStyle("-fx-text-fill: white;");
        car_parking_check.setPadding(new Insets(0,0,0,20));

        CheckBox bike_parking_check = new CheckBox("Bike Parking");
        bike_parking_check.setFont(new Font("Arial", 15));
        bike_parking_check.setAlignment(Pos.CENTER_LEFT);
        bike_parking_check.setStyle("-fx-text-fill: white;");
        bike_parking_check.setPadding(new Insets(0,0,10,20));

        // ================Public Transportation
        CheckBox train_station_check = new CheckBox("Train Station");
        train_station_check.setFont(new Font("Arial", 15));
        train_station_check.setAlignment(Pos.CENTER_LEFT);
        train_station_check.setStyle("-fx-text-fill: white;");
        train_station_check.setPadding(new Insets(0,0,0,10));

        CheckBox bus_stop_check = new CheckBox("Bus Stop");
        bus_stop_check.setFont(new Font("Arial", 15));
        bus_stop_check.setAlignment(Pos.CENTER_LEFT);
        bus_stop_check.setStyle("-fx-text-fill: white;");
        bus_stop_check.setPadding(new Insets(0,0,0,10));

        // ================Bike Rental
        CheckBox bike_rental_check = new CheckBox("Bike Rental");
        bike_rental_check.setFont(new Font("Arial", 15));
        bike_rental_check.setAlignment(Pos.CENTER_LEFT);
        bike_rental_check.setStyle("-fx-text-fill: white;");
        bike_rental_check.setPadding(new Insets(0,0,10,10));

        // ================Point Of Interest
        CheckBox poi_check = new CheckBox("Points Of Interest");
        poi_check.setFont(new Font("Arial", 15));
        poi_check.setAlignment(Pos.CENTER_LEFT);
        poi_check.setStyle("-fx-text-fill: white;");
        poi_check.setPadding(new Insets(0,0,0,10));

        CheckBox restaurant_check = new CheckBox("Restaurants");
        restaurant_check.setFont(new Font("Arial", 15));
        restaurant_check.setAlignment(Pos.CENTER_LEFT);
        restaurant_check.setStyle("-fx-text-fill: white;");
        restaurant_check.setPadding(new Insets(0,0,0,20));

        // Add Bar check
        CheckBox bar_check = new CheckBox("Bar");
        bar_check.setFont(new Font("Arial", 15));
        bar_check.setAlignment(Pos.CENTER_LEFT);
        bar_check.setStyle("-fx-text-fill: white;");
        bar_check.setPadding(new Insets(0,0,0,20));

        // Add Museum check
        CheckBox museum_check = new CheckBox("Museum");
        museum_check.setFont(new Font("Arial", 15));
        museum_check.setAlignment(Pos.CENTER_LEFT);
        museum_check.setStyle("-fx-text-fill: white;");
        museum_check.setPadding(new Insets(0,0,0,20));

        // Add Bar check
        CheckBox attraction_check = new CheckBox("Attraction");
        attraction_check.setFont(new Font("Arial", 15));
        attraction_check.setAlignment(Pos.CENTER_LEFT);
        attraction_check.setStyle("-fx-text-fill: white;");
        attraction_check.setPadding(new Insets(0,0,0,20));

        // Add Shop check
        CheckBox shop_check = new CheckBox("Shop");
        shop_check.setFont(new Font("Arial", 15));
        shop_check.setAlignment(Pos.CENTER_LEFT);
        shop_check.setStyle("-fx-text-fill: white;");
        shop_check.setPadding(new Insets(0,0,10,20));

        // ================= Listener for parking check box.
        parking_check.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue ov, Boolean old_val, Boolean new_val) {
                if (new_val == true) {
                    car_parking_check.setSelected(true);
                    bike_parking_check.setSelected(true);
                }

                car_parking_check.setDisable(new_val);
                bike_parking_check.setDisable(new_val);
            }
        });
        car_parking_check.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue ov, Boolean old_val, Boolean new_val) {
                if (new_val == true && bike_parking_check.isSelected()) {
                    car_parking_check.setDisable(new_val);
                    bike_parking_check.setDisable(new_val);
                    parking_check.setSelected(true);
                }
            }
        });
        bike_parking_check.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue ov, Boolean old_val, Boolean new_val) {
                if (new_val == true && car_parking_check.isSelected()) {
                    car_parking_check.setDisable(new_val);
                    bike_parking_check.setDisable(new_val);
                    parking_check.setSelected(true);
                }
            }
        });

        // ================= Listener for Point of Interest checkbox.
        poi_check.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue ov, Boolean old_val, Boolean new_val) {
                if (new_val == true) {
                    restaurant_check.setSelected(true);
                    bar_check.setSelected(true);
                    museum_check.setSelected(true);
                    attraction_check.setSelected(true);
                    shop_check.setSelected(true);
                }

                restaurant_check.setDisable(new_val);
                bar_check.setDisable(new_val);
                museum_check.setDisable(new_val);
                attraction_check.setDisable(new_val);
                shop_check.setDisable(new_val);
            }
        });
        restaurant_check.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue ov, Boolean old_val, Boolean new_val) {
                checkAndDisable(restaurant_check, bar_check, museum_check,
                        attraction_check, shop_check, poi_check);
            }
        });
        bar_check.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue ov, Boolean old_val, Boolean new_val) {
                checkAndDisable(restaurant_check, bar_check, museum_check,
                        attraction_check, shop_check, poi_check);
            }
        });
        museum_check.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue ov, Boolean old_val, Boolean new_val) {
                checkAndDisable(restaurant_check, bar_check, museum_check,
                        attraction_check, shop_check, poi_check);
            }
        });
        attraction_check.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue ov, Boolean old_val, Boolean new_val) {
                checkAndDisable(restaurant_check, bar_check, museum_check,
                        attraction_check, shop_check, poi_check);
            }
        });
        shop_check.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue ov, Boolean old_val, Boolean new_val) {
                checkAndDisable(restaurant_check, bar_check, museum_check,
                        attraction_check, shop_check, poi_check);
            }
        });

        // ======================Handling previous filter=================================

        // Sets checks as selected if they were in the previous interface
        parking_check.setSelected(filters.contains("p"));
        car_parking_check.setSelected(filters.contains("p") || filters.contains("c"));
        bike_parking_check.setSelected(filters.contains("p") || filters.contains("k"));

        train_station_check.setSelected(filters.contains("t"));
        bus_stop_check.setSelected(filters.contains("u"));
        bike_rental_check.setSelected(filters.contains("e"));

        poi_check.setSelected(filters.contains("i"));
        restaurant_check.setSelected(filters.contains("r") || filters.contains("i"));
        bar_check.setSelected(filters.contains("b") || filters.contains("i"));
        museum_check.setSelected(filters.contains("m") || filters.contains("i"));
        attraction_check.setSelected(filters.contains("a") || filters.contains("i"));
        shop_check.setSelected(filters.contains("s") || filters.contains("i"));

        // Sets default value
        if (filters.contains("d")) {
            district_choice_box.setValue("District "+district_id);
        } else {
            district_choice_box.setValue("");
        }

        // ======================Creating the filter button=================================
        Button filter_button = new Button("Apply Filter");
        filter_button.setStyle("-fx-margin: 10;");
        filter_button.setMaxWidth(Double.MAX_VALUE);

        // Setting the commands for the filtering option
        filter_button.setOnAction(e -> {

            System.out.println("---------------Processing the filtering request------------------");

            // Setting filters to empty
            this.filters = "";

            // Retrieve district selected data
            String district_chosen = (String) district_choice_box.getValue();
            if (district_chosen != null && district_chosen != "") {
                int new_district_id = parseInt(district_chosen.substring(9));

                if (new_district_id != district_id) {
                    district_id = new_district_id;
                    selected_district = dl.getDistrictById(district_id);
                }

                // Setting the filter variables
                this.filters += "d";
            } else {
                district_id = 0;
                selected_district = null;
            }

            // Retrieving the selected checkboxes
            this.filters += (parking_check.isSelected()) ? "p" : "";
            this.filters += (car_parking_check.isSelected() && !parking_check.isSelected()) ? "c" : "";
            this.filters += (bike_parking_check.isSelected() && !parking_check.isSelected()) ? "k" : "";

            this.filters += (train_station_check.isSelected()) ? "t" : "";
            this.filters += (bus_stop_check.isSelected()) ? "u" : "";
            this.filters += (bike_rental_check.isSelected()) ? "e" : "";

            this.filters += (poi_check.isSelected()) ? "i" : "";
            this.filters += (restaurant_check.isSelected() && !poi_check.isSelected()) ? "r" : "";
            this.filters += (bar_check.isSelected() && !poi_check.isSelected()) ? "b" : "";
            this.filters += (museum_check.isSelected() && !poi_check.isSelected()) ? "m" : "";
            this.filters += (attraction_check.isSelected() && !poi_check.isSelected()) ? "a" : "";
            this.filters += (shop_check.isSelected() && !poi_check.isSelected()) ? "s" : "";

            // Restart the window
            startWindow();
        });

        // Adding all the elements to the panel
        controlsVBox.getChildren().addAll(title_box, district_box, visualize_label,
                parking_check, car_parking_check, bike_parking_check,
                train_station_check, bus_stop_check, bike_rental_check,
                poi_check, restaurant_check, bar_check, museum_check, attraction_check, shop_check,
                filter_button);
    }

    // Applies the disabling of the checkbox based on the selected ones
    private void checkAndDisable(CheckBox restaurant_check, CheckBox bar_check, CheckBox museum_check,
                                 CheckBox attraction_check, CheckBox shop_check, CheckBox poi_check) {
        if (restaurant_check.isSelected() &&
                bar_check.isSelected() &&
                museum_check.isSelected() &&
                attraction_check.isSelected()) {
            restaurant_check.setDisable(true);
            bar_check.setDisable(true);
            museum_check.setDisable(true);
            attraction_check.setDisable(true);
            shop_check.setDisable(true);
            poi_check.setSelected(true);
        }

    }

    // Showing markers on the map
    private void addMarkers() {
        for (BindingSet bs: markers_set) {
            addSingleMarker(bs);
        }
    }

    // Adding the specified marker to graphics
    private void addSingleMarker(BindingSet marker) {
        if (graphicsOverlay != null) {
            SimpleMarkerSymbol pointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, hexRed, 10.0f);

            // Set different color for different marker
            String iri = marker.getBinding("iri").getValue().toString();
            String[] splitted_IRI = iri.split("/");
            String instance_class = splitted_IRI[4];
            pointSymbol.setOutline(new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, hexBlue, 2.0f));
            Point point = point_from_string(Literals.getLabel(marker.getValue("locat"), ""));
            graphicsOverlay.getGraphics().add(new Graphic (point, pointSymbol));
        }
    }

    // Retrieving District polygons and showing them on the map
    private void addDistricts(List<BindingSet> districts_data) {
        for (BindingSet bs: districts_data) {
            addSingleDistrict(bs);
        }
    }

    // Adding the specified district to graphics
    private void addSingleDistrict(BindingSet district) {
        if (graphicsOverlay != null) {
            Polygon polygon = polygon_from_string(Literals.getLabel(district.getValue("d_area"), ""));
            SimpleLineSymbol polygonSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, hexBlue, 2.0f);
            graphicsOverlay.getGraphics().add(new Graphic(polygon, polygonSymbol));
        }
    }


    // Converting polygon retrieved in string format to actual polygon
    private Polygon polygon_from_string (String text) {

        // Declaring polygon points collection
        PointCollection polygonPoints = new PointCollection(SpatialReferences.getWgs84());

        // Fixing the text structure
        text = text.replace("POLYGON", "");
        text = text.replace("((", "");
        text = text.replace("))", "");

        // Iterating through polygon coordinates
        String[] coordinate_pairs = text.split(",");
        for (int i=0; i<coordinate_pairs.length; i++) {
            String[] coordinates = coordinate_pairs[i].split(" ");
            polygonPoints.add(new com.esri.arcgisruntime.geometry.Point(
                    Double.parseDouble(coordinates[1]),
                    Double.parseDouble(coordinates[0]))
            );
        }

        return new Polygon(polygonPoints);
    }

    // Converting polygon retrieved in string format to actual polygon
    private Point point_from_string (String text) {

        // Fixing the text structure
        text = text.replace("POINT", "");
        text = text.replace("(", "");
        text = text.replace(")", "");

        // Checking if empty characters are saved
        if (text.charAt(0) == ' ') {
            text = text.substring(1);
        }

        String[] coordinate_pairs = text.split(" ");

        // Creating and returning point
        return new Point(Double.parseDouble(coordinate_pairs[1]),
                Double.parseDouble(coordinate_pairs[0]),
                SpatialReferences.getWgs84());
    }

//    // Creating the popup after mouse click
//    private void createPopUp(BindingSet marker, Callout callout, Point mapPoint) {
//
//        // Retrieve class
//        String iri = marker.getBinding("iri").getValue().toString();
//        String[] splitted_IRI = iri.split("/");
//        String instance_class = splitted_IRI[4];
//        String uppercase_class = instance_class.substring(0, 1).toUpperCase() + instance_class.substring(1);
//
//        // Getting the information needed
//        if (uppercase_class.compareTo("Restaurant") == 0 ||
//                uppercase_class.compareTo("Bar") == 0 ||
//                uppercase_class.compareTo("Attraction") == 0 ||
//                uppercase_class.compareTo("Shop") == 0 ||
//                uppercase_class.compareTo("Museum") == 0) {
//
//            // Retrieving information
//            BindingSet full_marker = dl.getPoiByIRI(iri, uppercase_class);
//            if (full_marker != null) {
//
//                // Handling in case the hours are missing
//                String opening_hours = Literals.getLabel(full_marker.getValue("oh"), "");
//                if (opening_hours.compareTo("") != 0){
//                    opening_hours = opening_hours.replace("<p>", "").replace("</p>", "").replace("<b>", "").replace("</b>", "").replace("<br />", "").replace("&ndash;", "-");
//                } else {
//                    opening_hours = "not provided.";
//                }
//
//                // Handling in case the hours are missing
//                String address = Literals.getLabel(full_marker.getValue("addr"), "");
//                if (address.compareTo("") == 0){
//                    address = "not provided.";
//                }
//
//                // Creating text
//                String printed_text = "Name: " + Literals.getLabel(full_marker.getValue("nam"), "") + "\n";
//                printed_text += "Description: " + Literals.getLabel(full_marker.getValue("descr"), "") + "\n";
//                printed_text += "Address: " + address + "\n";
//                printed_text += "Opening Hours: " + opening_hours + "\n";
//
//                // Configurating the popup
//                callout.setTitle(uppercase_class);
//                callout.setDetail(printed_text);
//                callout.showCalloutAt(mapPoint, DURATION);
//            }
//        }
//
//        if (uppercase_class.compareTo("Carparking")  == 0) {
//
//            // Retrieving information
//            BindingSet full_marker = dl.getCarParkingByIRI(iri);
//
//            if (full_marker != null) {
//                // Handling in case the hours are missing
//                String address = Literals.getLabel(full_marker.getValue("addr"), "");
//                if (address.compareTo("") == 0) {
//                    address = "not provided.";
//                }
//
//                // Creating text
//                String printed_text = "Name: " + Literals.getLabel(full_marker.getValue("nam"), "") + "\n";
//                printed_text += "Address: " + address + "\n";
//                printed_text += "Number of Spaces: " + Literals.getLabel(full_marker.getValue("sn"), "") + "\n";
//
//                // Configurating the popup
//                callout.setTitle("CarParking");
//                callout.setDetail(printed_text);
//                callout.showCalloutAt(mapPoint, DURATION);
//            }
//        }
//
//        if (uppercase_class.compareTo("Bikeparking") == 0) {
//            // Retrieving information
//            BindingSet full_marker = dl.getBikeParkingByIRI(iri);
//
//            if (full_marker != null) {
//
//                // Handling in case the hours are missing
//                String vehicleType = Literals.getLabel(full_marker.getValue("vt"), "");
//                if (vehicleType.compareTo("both") == 0) {
//                    vehicleType = "both bike and motorbike.";
//                }
//
//                // Creating text
//                String printed_text = "Vehicle Type: " + vehicleType + "\n";
//                printed_text += "Number of Spaces: " + Literals.getLabel(full_marker.getValue("sn"), "") + "\n";
//
//                // Configurating the popup
//                callout.setTitle("BikeParking");
//                callout.setDetail(printed_text);
//                callout.showCalloutAt(mapPoint, DURATION);
//            }
//        }
//
//        if (uppercase_class.compareTo("Busstop") == 0) {
//            // Retrieving information
//            BindingSet full_marker = dl.getBusStopByIRI(iri);
//
//            if (full_marker != null) {
//
//                // Creating text
//                String printed_text = "Name: " + Literals.getLabel(full_marker.getValue("nam"), "") + "\n";
//                printed_text += "Bus Stop Type: " + Literals.getLabel(full_marker.getValue("bst"), "") + "\n";
//
//                // Configurating the popup
//                callout.setTitle("BusStop");
//                callout.setDetail(printed_text);
//                callout.showCalloutAt(mapPoint, DURATION);
//            }
//        }
//
//        if (uppercase_class.compareTo("Trainstation") == 0) {
//            // Retrieving information
//            BindingSet full_marker = dl.getBusStopByIRI(iri);
//
//            if (full_marker != null) {
//
//                // Creating text
//                String printed_text = "Name: " + Literals.getLabel(full_marker.getValue("nam"), "") + "\n";
//
//                // Configurating the popup
//                callout.setTitle("TrainStation");
//                callout.setDetail(printed_text);
//                callout.showCalloutAt(mapPoint, DURATION);
//            }
//        }
//    }

    @Override
    public void stop() {
        if (mapView != null) {
            mapView.dispose();
        }
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

}
