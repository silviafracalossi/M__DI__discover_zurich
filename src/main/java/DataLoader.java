import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;

import java.util.List;

public class DataLoader {

    private List<BindingSet> districts_result_list = null;

    private static Repository repository;
    private RepositoryConnection connection;

    public static void main(String[] args) {

        // Present also in the construct method
        Repository repository = new SPARQLRepository("http://localhost:8080/sparql");
        repository.initialize();
        RepositoryConnection connection = repository.getConnection();

        String sparqlQuery = "\nPREFIX : <http://www.semanticweb.org/silvia/ontologies/2020/5/untitled-ontology-13#>\n" +
                "SELECT * WHERE {\n" +
                "\t ?n a :Neighbourhood ;\n" +
                "\t\t :n_name ?name ; \n" +
                "\t\t :neighbourhoodIn ?d . \n" +
                "\t ?d a :District .\n" +
                "}\n" +
                "LIMIT 10\n";

        System.out.println("Query executed by main method of DataLoader:");
        System.out.println(sparqlQuery);

        try (TupleQueryResult resultSet = connection.prepareTupleQuery(sparqlQuery).evaluate()) {
            while (resultSet.hasNext()) {
                BindingSet bindingSet = resultSet.next();
                System.out.println(bindingSet);
            }
        }
    }

    // Class constructor which connects to the SPARQL endpoint
    public DataLoader() {
        System.out.println("Connecting to the SPARQL endpoint...");

        // Initializing the SPARQLRepository from the SPARQL endpoint
        repository = new SPARQLRepository("http://localhost:8080/sparql");
        repository.initialize();

        // Connecting to the SPARQL endpoint
        connection = repository.getConnection();
    }


    // Retrieving all the district areas
    public List<BindingSet> getDistrictAreas() {

        if (districts_result_list != null) {
            return districts_result_list;
        }

        // Creating result list variable
        List<BindingSet> result_list = null;

        // Formulating SPARQL Query
        String sparqlQuery = "" +
            "PREFIX : <http://www.semanticweb.org/silvia/ontologies/2020/5/untitled-ontology-13#>\n" +
            "PREFIX rdf:\t<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
            "SELECT ?d ?d_area \n" +
            "WHERE {\n" +
            "\t ?d :d_area ?d_area ;\n" +
            "\t\t rdf:type :District .\n" +
            "}" + "\n";


        // Evaluating the query and retrieving the results
        System.out.println("\n[INFO] Query in getDistrictAreas");
        System.out.println(sparqlQuery);
        try (TupleQueryResult query_result = connection.prepareTupleQuery(sparqlQuery).evaluate()) {
            result_list = QueryResults.asList(query_result);
        }

        // Saving the list as global variable to retrieve it without querying data again
        districts_result_list = result_list;

        return result_list;
    }

//    // Returns polygon of specified district
//    public BindingSet getDistrictById (int district_id) {
//
//        // Creating result list variable
//        List<BindingSet> result_list = null;
//        String district_reference = "<http://example.com/base/district/"+district_id+">";
//
//        // Formulating SPARQL Query
//        String sparqlQuery = "" +
//                "PREFIX : <http://example.org/term/>\n" +
//                "SELECT ?d_area \n" +
//                "WHERE {\n" +
//                "\t" +district_reference+ " :area ?d_area .\n" +
//                "}" + "\n";
//
//        // Evaluating the query and retrieving the results
//        System.out.println("\n[INFO] Query in getDistrictById");
//        System.out.println(sparqlQuery);
//        try (TupleQueryResult query_result = connection.prepareTupleQuery(sparqlQuery).evaluate()) {
//            result_list = QueryResults.asList(query_result);
//        }
//
//        return (result_list != null) ? result_list.get(0) : null;
//    }
//
//
//    // Return the POIs and Facilities requested by the user
//    public List<BindingSet> getMarkerData(int district_id, String filters) {
//
//        // Creating result list variable
//        List<BindingSet> result_list = null;
//
//        // Formulating SPARQL Query
//        String sparqlQuery = "" +
//                "PREFIX : <http://example.org/term/>\n" +
//                "SELECT ";
//
//        // Filter by district
//        String district_reference = "?d";
//        if (district_id != 0) {
//            district_reference = "<http://example.com/base/district/"+district_id+">";
//        }
//
//        // Adding poi condition
//        sparqlQuery += "?iri ?nam ?locat \n" +
//                "WHERE { \n" +
//                "\t OPTIONAL { ?iri :name ?nam } \n" +
//                "\t ?iri :location ?locat . \n" +
//                "\t { ?iri rdf:type :PointOfInterest; :poiIn "+district_reference+" } \n";
//
//        // Adding public transportation and parking if marked
//        sparqlQuery += (filters.contains("t")) ? "\t UNION \n\t { ?iri rdf:type :PublicTransportationStop ; :stopsIn "+district_reference+" } \n" : "" ;
//        sparqlQuery += (filters.contains("p")) ? "\t UNION \n\t { ?iri rdf:type :Parking ; :parkingIn "+district_reference+" } \n" : "" ;
//
//        // Adding district specification
//        if (district_id == 0) {
//            sparqlQuery += "\t ?d rdf:type :District .\n";
//        }
//
//        // Adding class specification based on filters
//        if (!filters.contains("r")) sparqlQuery += "\t FILTER NOT EXISTS {?iri rdf:type :Restaurant} \n";
//        if (!filters.contains("b")) sparqlQuery += "\t FILTER NOT EXISTS {?iri rdf:type :Bar} \n";
//        if (!filters.contains("m")) sparqlQuery += "\t FILTER NOT EXISTS {?iri rdf:type :Museum} \n";
//        if (!filters.contains("a")) sparqlQuery += "\t FILTER NOT EXISTS {?iri rdf:type :Attraction} \n";
//        if (!filters.contains("s")) sparqlQuery += "\t FILTER NOT EXISTS {?iri rdf:type :Shop} \n";
//
//        // Adding limit of 25
//        sparqlQuery += "} \n" ;
//        //+ "LIMIT 20 \n";
//
//        // Evaluating the query and retrieving the results
//        System.out.println("\n[INFO] Query in getMarkerData");
//        System.out.println(sparqlQuery);
//        try (TupleQueryResult query_result = connection.prepareTupleQuery(sparqlQuery).evaluate()) {
//            result_list = QueryResults.asList(query_result);
//        }
//
//        return result_list;
//    }
//
//
//    // Retrieving the POI information of the given IRI
//    public BindingSet getPoiByIRI(String IRI, String instance_class) {
//
//        // Creating result list variable
//        List<BindingSet> result_list = null;
//        String reference = "<"+IRI+">";
//
//        // Formulating SPARQL Query
//        String sparqlQuery = "" +
//                "PREFIX : <http://example.org/term/>\n" +
//                "SELECT * \n" +
//                "WHERE {\n" +
//                "\t " +reference+ " rdf:type :"+instance_class+" .\n" +
//                "\t " +reference+ " :name ?nam . \n" +
//                "\t OPTIONAL {" +reference+ " :description ?descr } \n" +
//                "\t OPTIONAL {" +reference+ " :address ?addr } \n" +
//                "\t OPTIONAL {" +reference+ " :openingHours ?oh } \n" +
//                "}" +
//                "\n";
//
//        // Evaluating the query and retrieving the results
//        System.out.println("\n[INFO] Query in getPOIbyIRI");
//        System.out.println(sparqlQuery);
//        try (TupleQueryResult query_result = connection.prepareTupleQuery(sparqlQuery).evaluate()) {
//            result_list = QueryResults.asList(query_result);
//        }
//
//        return (result_list != null) ? result_list.get(0) : null;
//    }
//
//    // Retrieving the CarParking information of the given IRI
//    public BindingSet getCarParkingByIRI(String IRI) {
//
//        // Creating result list variable
//        List<BindingSet> result_list = null;
//        String reference = "<"+IRI+">";
//
//        // Formulating SPARQL Query
//        String sparqlQuery = "" +
//                "PREFIX : <http://example.org/term/>\n" +
//                "SELECT * \n" +
//                "WHERE {\n" +
//                "\t OPTIONAL {" +reference+ " :name ?nam } \n" +
//                "\t OPTIONAL {" +reference+ " :address ?addr } \n" +
//                "\t OPTIONAL {" +reference+ " :spacesNo ?sn } \n" +
//                "}" +
//                "\n";
//
//        // Evaluating the query and retrieving the results
//        System.out.println("\n[INFO] Query in getCarParkingByIRI");
//        System.out.println(sparqlQuery);
//        try (TupleQueryResult query_result = connection.prepareTupleQuery(sparqlQuery).evaluate()) {
//            result_list = QueryResults.asList(query_result);
//        }
//
//        return (result_list != null) ? result_list.get(0) : null;
//    }
//
//    // Retrieving the BikeParking information of the given IRI
//    public BindingSet getBikeParkingByIRI(String IRI) {
//
//        // Creating result list variable
//        List<BindingSet> result_list = null;
//        String reference = "<"+IRI+">";
//
//        // Formulating SPARQL Query
//        String sparqlQuery = "" +
//                "PREFIX : <http://example.org/term/>\n" +
//                "SELECT * \n" +
//                "WHERE {\n" +
//                "\t OPTIONAL {" +reference+ " :vehicleType ?vt } \n" +
//                "\t OPTIONAL {" +reference+ " :spacesNo ?sn } \n" +
//                "}" +
//                "\n";
//
//        // Evaluating the query and retrieving the results
//        System.out.println("\n[INFO] Query in getBikeParkingByIRI");
//        System.out.println(sparqlQuery);
//        try (TupleQueryResult query_result = connection.prepareTupleQuery(sparqlQuery).evaluate()) {
//            result_list = QueryResults.asList(query_result);
//        }
//
//        return (result_list != null) ? result_list.get(0) : null;
//    }
//
//    // Retrieving the BusStop information of the given IRI
//    public BindingSet getBusStopByIRI(String IRI) {
//
//        // Creating result list variable
//        List<BindingSet> result_list = null;
//        String reference = "<"+IRI+">";
//
//        // Formulating SPARQL Query
//        String sparqlQuery = "" +
//                "PREFIX : <http://example.org/term/>\n" +
//                "SELECT * \n" +
//                "WHERE {\n" +
//                "\t OPTIONAL {" +reference+ " :name ?nam } \n" +
//                "\t OPTIONAL {" +reference+ " :busStopType ?bst } \n" +
//                "}" +
//                "\n";
//
//        // Evaluating the query and retrieving the results
//        System.out.println("\n[INFO] Query in getBusStopByIRI");
//        System.out.println(sparqlQuery);
//        try (TupleQueryResult query_result = connection.prepareTupleQuery(sparqlQuery).evaluate()) {
//            result_list = QueryResults.asList(query_result);
//        }
//
//        return (result_list != null) ? result_list.get(0) : null;
//    }
//
//    // Retrieving the TrainStation information of the given IRI
//    public BindingSet getTrainStationByIRI(String IRI) {
//
//        // Creating result list variable
//        List<BindingSet> result_list = null;
//        String reference = "<"+IRI+">";
//
//        // Formulating SPARQL Query
//        String sparqlQuery = "" +
//                "PREFIX : <http://example.org/term/>\n" +
//                "SELECT * \n" +
//                "WHERE {\n" +
//                "\t OPTIONAL {" +reference+ " :name ?nam } \n" +
//                "}" +
//                "\n";
//
//        // Evaluating the query and retrieving the results
//        System.out.println("\n[INFO] Query in getTrainStationByIRI");
//        System.out.println(sparqlQuery);
//        try (TupleQueryResult query_result = connection.prepareTupleQuery(sparqlQuery).evaluate()) {
//            result_list = QueryResults.asList(query_result);
//        }
//
//        return (result_list != null) ? result_list.get(0) : null;
//    }
}
