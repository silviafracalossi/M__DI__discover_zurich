-- Enable PostGIS (as of 3.0 contains just geometry/geography)
CREATE EXTENSION postgis;

CREATE TABLE special_bus (
	id INTEGER PRIMARY KEY,
	name VARCHAR(50),
	lat FLOAT,
	lng FLOAT,
	coordinates VARCHAR(100),
	point geometry
);

CREATE TABLE districts (
	objid INT PRIMARY KEY,
	knr INT,
	kname VARCHAR(15),
	geometry geometry
);
