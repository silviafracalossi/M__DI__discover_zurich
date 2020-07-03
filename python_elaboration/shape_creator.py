import csv
import shapely
import geopandas as gpd

from shapely.geometry import Point, Polygon

# Converting string coordinates into a polygon
def string_to_polygon(polygon_text):
    result = []
    collection = []

    polygon_text = polygon_text.replace("POLYGON ", "").replace("((", "").replace("))", "")

    # Iterating through each polygon point
    for point in polygon_text.split(","):

        # Splitting the two coordinate values
        coordinates = point.split(" ")
        collection.append(Point(float(coordinates[0]), float(coordinates[1])))

    # Converting the point in the used format
    geo_df = gpd.GeoDataFrame([],
        geometry=gpd.GeoSeries(collection))
    geo_df.crs = 'epsg:2056'
    geo_df = geo_df.to_crs('epsg:4326')
    location_points = geo_df['geometry']

    for index, row in geo_df.iterrows():
        coordinates = str(row['geometry']).replace("POINT ", "").replace("(", "").replace(")", "").split(" ")[::-1]
        result.append(tuple([float(i) for i in coordinates]))

    return Polygon(result)


# Converting string coordinates into a point
def string_to_point(point_text):

    point_text = point_text.replace("POINT ", "").replace("(", "").replace(")", "")
    coordinates = point_text.split(" ")

    return convert_point(Point(float(coordinates[0]), float(coordinates[1])))


# Converting the point in the used format
def convert_point(original_point):
    geo_df = gpd.GeoDataFrame([],
        geometry=gpd.GeoSeries(original_point))
    geo_df.crs = 'epsg:2056'
    geo_df = geo_df.to_crs('epsg:4326')
    location_points = geo_df['geometry'].iloc[0]

    # Setting latitude first and returning point
    fixed_coordinates = str(location_points).replace("POINT ", "").replace("(", "").replace(")", "").split(" ")

    return Point(float(fixed_coordinates[1]), float(fixed_coordinates[0]))
