[QueryItem="GEO_EXPERIMENT"]
PREFIX geof: <http://www.opengis.net/ont/geosparql/function/>
PREFIX : <http://www.semanticweb.org/silvia/ontologies/2020/5/untitled-ontology-13#>
SELECT *
WHERE {
	?bs a :BusStop ;
		:bs_location ?point .
	?d a :District ;
		:d_area ?area ;
		:d_name ?name .
	
	FILTER(geof:sfWithin(?point,?area))
}

[QueryItem="universal_query"]
PREFIX : <http://www.semanticweb.org/silvia/ontologies/2020/5/untitled-ontology-13#>


SELECT * WHERE {
	?s ?p ?o ; a :Place .
} LIMIT 25
