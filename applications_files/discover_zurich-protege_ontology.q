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
