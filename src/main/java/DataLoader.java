import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;

public class DataLoader {

    public static void main(String[] args) {
        Repository repository = new SPARQLRepository("http://localhost:8080/sparql");
        repository.initialize();

        RepositoryConnection connection = repository.getConnection();

        String query = "PREFIX : <http://www.semanticweb.org/silvia/ontologies/2020/5/untitled-ontology-13#>\n" +
                "SELECT * WHERE {\n" +
                "\t ?n a :Neighbourhood ;\n" +
                "\t\t :n_name ?name ; \n" +
                "\t\t :neighbourhoodIn ?d . \n" +
                "\t ?d a :District .\n" +
                "}\n" +
                "LIMIT 10\n";

        System.out.println("\n\n" + query + "\n\n");

        try (TupleQueryResult resultSet = connection.prepareTupleQuery(query).evaluate()) {
            while (resultSet.hasNext()) {
                BindingSet bindingSet = resultSet.next();
                System.out.println(bindingSet);
            }
        }

    }
}