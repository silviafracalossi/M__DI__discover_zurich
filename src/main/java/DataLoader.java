import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;

import java.util.ArrayList;
import java.util.List;

public class DataLoader {

    private List<BindingSet> districts_result_list = null;

    private static Repository repository;
    private RepositoryConnection connection;

    private static String base_iri = "http://www.semanticweb.org/silvia/ontologies/2020/5/untitled-ontology-13#";

    public static void main(String[] args) {

        // Present also in the construct method
        Repository repository = new SPARQLRepository("http://localhost:8080/sparql");
        repository.initialize();
        RepositoryConnection connection = repository.getConnection();

        String sparqlQuery = "\nPREFIX : <"+base_iri+">\n" +
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
            "PREFIX : <"+base_iri+">\n" +
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

    // Returns polygon of specified district
    public BindingSet getDistrictById (int district_id) {

        // Creating result list variable
        List<BindingSet> result_list = null;
        String district_reference = "<"+base_iri+"data/District/"+district_id+">";

        // Formulating SPARQL Query
        String sparqlQuery = "" +
                "PREFIX : <"+base_iri+">\n" +
                "SELECT ?d_area \n" +
                "WHERE {\n" +
                "\t" +district_reference+ " :d_area ?d_area .\n" +
                "}" + "\n";

        // Evaluating the query and retrieving the results
        System.out.println("\n[INFO] Query in getDistrictById");
        System.out.println(sparqlQuery);
        try (TupleQueryResult query_result = connection.prepareTupleQuery(sparqlQuery).evaluate()) {
            result_list = QueryResults.asList(query_result);
        }

        return (result_list != null) ? result_list.get(0) : null;
    }


    // Return the POIs and Facilities requested by the user
    public List<BindingSet> getMarkerData(int district_id, String filters) {

        // Creating result list variable
        List<BindingSet> result_list = null;

        // Formulating SPARQL Query
        String sparqlQuery = "" +
                "PREFIX : <"+base_iri+">\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "SELECT ?iri ?locat \n" +
                "WHERE {\n" +
                "\t?iri a :Place ; \n" +
                "\t\t :pl_location ?locat . \n";

        // Filter by district
        if (district_id != 0) {
            sparqlQuery += "\t<"+base_iri+"data/District/"+district_id+"> :d_area ?d_area . \n" +
                    "\tFILTER(geof:sfWithin(?locat, ?d_area))\n";
        }

        // Filtering based on Filter Section
        List<String> added_content = new ArrayList<String>();
        if (filters.contains("p")) {
            added_content.add("\t{ ?iri a :Parking }\n");
        } else {
            added_content.add((filters.contains("c")) ? "\t { ?iri a :CarParking }\n" : "");
            added_content.add((filters.contains("k")) ? "\t { ?iri a :BikeParking }\n" : "");
        }

        added_content.add((filters.contains("t")) ? "\t { ?iri a :TrainStation }\n" : "");
        added_content.add((filters.contains("u")) ? "\t { ?iri a :BusStop }\n" : "");
        added_content.add((filters.contains("e")) ? "\t { ?iri a :BikeRental }\n" : "");

        if (filters.contains("i")) {
            added_content.add("\t{ ?iri a :PointOfInterest }\n");
        } else {
            added_content.add((filters.contains("r")) ? "\t { ?iri a :Restaurant }\n" : "");
            added_content.add((filters.contains("b")) ? "\t { ?iri a :Bar }\n" : "");
            added_content.add((filters.contains("m")) ? "\t { ?iri a :Museum }\n" : "");
            added_content.add((filters.contains("a")) ? "\t { ?iri a :Attraction }\n" : "");
            added_content.add((filters.contains("s")) ? "\t { ?iri a :Shop }\n" : "");
        }

        // Inserting Filter Section conditions in query
        boolean first = true;
        for (String element: added_content) {
            if (element.compareTo("") != 0) {
                if (first) {
                    sparqlQuery += element;
                    first = false;
                } else {
                    sparqlQuery += "\t\tUNION\n" + element;
                }
            }
        }

        sparqlQuery += "} \n";

        // Evaluating the query and retrieving the results
        System.out.println("\n[INFO] Query in getMarkerData");
        System.out.println(sparqlQuery);
        try (TupleQueryResult query_result = connection.prepareTupleQuery(sparqlQuery).evaluate()) {
            result_list = QueryResults.asList(query_result);
        }

        return result_list;
    }


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
