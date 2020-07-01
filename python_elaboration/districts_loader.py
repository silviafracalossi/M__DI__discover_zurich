import csv
import shapely
from shapely.geometry import Point, Polygon
from shape_creator import string_to_polygon

# Loads districts from 01 - distric.csv file
def load_districts():
    print("[INFO] Loading districts from file...")

    # Defining the district list
    header = ""
    new_file_lines = []
    districts = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]

    # Reading the district csv
    with open('datasets/original/01 - district.csv') as csv_file:
        csv_reader = csv.reader(csv_file, delimiter=',')

        # Iterating the csv
        for row in csv_reader:

            # Header
            if header == "":
                row.pop()
                header = row

            # Rows
            else:
                districts[int(row[1])] = string_to_polygon(row[3])

                # Removing old geometry values and adding converted ones
                row.pop()
                row.append(districts[int(row[1])])
                new_file_lines.append([row])

    # Writing into the destination file
    with open('datasets/01 - district.csv', 'w', newline='') as csvfile:
        spamwriter = csv.writer(csvfile, delimiter=',', quoting=csv.QUOTE_MINIMAL)

        # Inserting header
        header.append("geometry")
        spamwriter.writerow(header)

        # Inserting rows
        for line in new_file_lines:
            spamwriter.writerow(line[0])

    # Returning list of district id and polygon
    return districts
