import csv
import json
import shapely
import geopandas as gpd

from shapely.geometry import Point, Polygon
from shape_creator import string_to_polygon, string_to_point, convert_point


# Definition of the location of the folders
origin_folder = 'datasets/original/'
destination_folder = 'datasets/'


# Loads districts from 01 - distric.csv file
def load_districts():

    file_name = '01 - district.csv'
    print("[INFO] Processing "+file_name)

    # Defining the district list
    header = ""
    new_file_lines = []

    # Reading the district csv
    with open(origin_folder+file_name) as csv_file:
        csv_reader = csv.reader(csv_file, delimiter=',')

        # Iterating the csv
        for row in csv_reader:

            # Header
            if header == "":
                row.pop()
                header = row
                header.append("fixed_geometry")

            # Rows
            else:

                # Removing old geometry values and adding converted ones
                polygon = string_to_polygon(row[3])
                row.pop()
                row.append(polygon)
                new_file_lines.append([row])

    # Writing into the destination file
    with open(destination_folder+file_name, 'w', newline='') as csvfile:
        spamwriter = csv.writer(csvfile, delimiter=',', quoting=csv.QUOTE_MINIMAL)

        # Inserting header
        spamwriter.writerow(header)

        # Inserting rows
        for line in new_file_lines:
            spamwriter.writerow(line[0])


# Copying the indicated CSV file in the destination folder
def copy_csv_file(file_name):

    file = []
    print("[INFO] Copying "+file_name)

    # Reading the file
    with open(origin_folder+file_name) as csv_file:
        data = csv.reader(csv_file, delimiter=';')

        # Iterating through dataset
        for row in data:
            file.append(row)

    # Writing into the destination file
    with open(destination_folder+file_name, 'w', newline='') as csvfile:
        spamwriter = csv.writer(csvfile, delimiter=',', quoting=csv.QUOTE_MINIMAL)

        # Inserting rows
        for line in file:
            spamwriter.writerow(line)


# Copying the indicated JSON file in the destination folder
def copy_json_file(file_name):

    data = {}
    print("[INFO] Copying "+file_name)

    # Reading the file
    with open(origin_folder+file_name) as json_file:
        data = json.load(json_file)

    # Writing rows in new file
    with open(destination_folder+file_name, 'w') as json_dest_file:
        if (type(data) == list):
            json.dump({"objects": data}, json_dest_file)
        else:
            json.dump(data, json_dest_file)


# Elaborates csv and stores elaboration
def elaborate_csv(file_name, id_point_field):
    print("[INFO] Processing "+file_name)
    new_file_lines = []

    # Reading the district csv
    with open(origin_folder+file_name) as csv_file:
        csv_reader = csv.reader(csv_file, delimiter=',')
        header = ""

        # Iterating the csv
        for row in csv_reader:

            # Header
            if header == "":
                header = row
                header[0] = header[0].replace("\"","")
                header.append("fixed_geometry")

            # Rows
            else:

                # Convert location into point
                location_point = ""
                location_point = string_to_point(row[id_point_field])
                row.append(location_point)
                new_file_lines.append(row)

    # Writing into the destination file
    with open(destination_folder+file_name, 'w', newline='') as csvfile:
        spamwriter = csv.writer(csvfile, delimiter=',', quoting=csv.QUOTE_MINIMAL)

        # Inserting header
        spamwriter.writerow(header)

        # Inserting rows
        for line in new_file_lines:
            spamwriter.writerow(line)


# Elaborates csv and stores elaboration
def elaborate_bus_csv(file_name):

    # Setting method variables
    id_first_point_field = 14
    id_second_point_field = 15

    print("[INFO] Processing "+file_name)
    new_file_lines = []

    # Reading the district csv
    with open(origin_folder+file_name) as csv_file:
        csv_reader = csv.reader(csv_file, delimiter=',')
        header = ""

        # Iterating the csv
        for row in csv_reader:

            # Header
            if header == "":
                header = row
                header[0] = header[0].replace("\"","")
                header.append("fixed_geometry")

            # Rows
            else:

                # Convert location into point
                location_point = ""
                location_point = convert_point(Point(float(row[id_first_point_field]), float(row[id_second_point_field])))
                row.append(location_point)
                new_file_lines.append(row)

    # Writing into the destination file
    with open(destination_folder+'03a - bus.csv', 'w', newline='') as csvfile:
        spamwriter = csv.writer(csvfile, delimiter=',', quoting=csv.QUOTE_MINIMAL)

        # Inserting header
        spamwriter.writerow(header)

        # Inserting rows
        for line in new_file_lines:
            spamwriter.writerow(line)

    # Creating the busline file
    with open(destination_folder+'03b - busline.csv', 'w', newline='') as csvfile:

        spamwriter = csv.writer(csvfile, delimiter=',', quoting=csv.QUOTE_MINIMAL)

        # Inserting header
        second_header = []
        second_header.append("bustop_id")
        second_header.append("busline_name")
        spamwriter.writerow(second_header)

        # Inserting rows
        for line in new_file_lines:
            for value in line[13].split(','):
                spamwriter.writerow([line[0], value])

# # Elaborates json and stores elaboration with district id
# def elaborate_json(districts, original_file):
#     print("[INFO] Processing json file "+original_file)
#     new_file_lines = []
#
#     with open('resources/data/01_original/'+original_file) as json_file:
#         data = json.load(json_file)
#
#         # Iterating through dataset
#         for row in data:
#
#             # Checking if the geometrical information are provided
#             if "geo" in row:
#
#                 # Creating the point
#                 location_point = Point(float(row['geo']['latitude']), float(row['geo']['longitude']))
#
#                 # Iterate through districts to find the correct match
#                 district_id = 0
#                 for i in range(1, len(districts)):
#                     if location_point.within(districts[i]):
#                         district_id = i
#                         break
#
#                 # Insert district id inside the row
#                 if district_id != 0:
#                     row['geometry'] = str(location_point)
#                     row['district_id'] = district_id
#                     new_file_lines.append(row)
#
#     # Destination file - name preparation
#     destination_file = original_file.replace("-", "elaborated")
#     destination_file = destination_file.replace(" ", "_")
#
#     # Writing rows in new file
#     with open('resources/data/02_python_elaborated/'+destination_file, 'w') as json_dest_file:
#         json.dump({"poi": new_file_lines}, json_dest_file)
#
