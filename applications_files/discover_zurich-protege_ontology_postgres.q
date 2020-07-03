[QueryItem="districts"]
PREFIX : <http://www.semanticweb.org/silvia/ontologies/2020/5/untitled-ontology-13#>
SELECT *
WHERE {
	?district a :District ; :d_name ?name ; :d_area ?area
}

[QueryItem="neighbourhoods"]
PREFIX : <http://www.semanticweb.org/silvia/ontologies/2020/5/untitled-ontology-13#>
SELECT *
WHERE {
	?n a :Neighbourhood ; :n_name ?name ; :neighbourhoodIn ?d . 
	?d a :District .
}

[QueryItem="car_parking"]
PREFIX : <http://www.semanticweb.org/silvia/ontologies/2020/5/untitled-ontology-13#>
SELECT *
WHERE {
	?cp a :CarParking ; :cp_address ?address ; :cp_name ?name ; :cp_location ?location ; :cp_spaces_no ?spaces_no .
}

[QueryItem="experiment"]
PREFIX geof: <http://www.opengis.net/ont/geosparql/function/>
PREFIX geo: <http://www.opengis.net/ont/geosparql#>
PREFIX : <http://www.semanticweb.org/silvia/ontologies/2020/5/untitled-ontology-13#>
SELECT *
WHERE {
	?cp a :CarParking ; :cp_name ?name ; :cp_location ?location .
	?cpx a :CarParking ; :cp_name ?namex ; :cp_location ?locationx .
	FILTER (geof:distance (?location, ?locations, :n)<2000000)
}

[QueryItem="dataloader_query"]
PREFIX 	: <http://www.semanticweb.org/silvia/ontologies/2020/5/untitled-ontology-13#>
PREFIX rdf:	<http://www.w3.org/1999/02/22-rdf-syntax-ns#>
SELECT ?d ?area 
WHERE {
   ?d :d_area ?area .
   ?d rdf:type :District .
}

[QueryItem="shops"]
PREFIX : <http://www.semanticweb.org/silvia/ontologies/2020/5/untitled-ontology-13#>
SELECT *
WHERE {
	?s a :PointOfInterest ;
		?p ?o .
}

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
