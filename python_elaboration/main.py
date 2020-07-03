import csv
import shapely

from shapely.geometry import Point, Polygon
from files_elaborator import load_districts, copy_csv_file, copy_json_file, elaborate_csv, elaborate_bus_csv


load_districts()                                # 01 - district.csv
copy_csv_file('02 - neighbourhood.csv')         # 02 - neighbourhood.csv
elaborate_bus_csv('03 - bus.csv')               # 03 - bus.csv
copy_json_file('04 - train station.json')       # 04 - train station.json
elaborate_csv("05 - car parking.csv", 60)       # 05 - car parking.csv
elaborate_csv("06 - bike parking.csv", 12)      # 06 - bike parking.csv
copy_json_file("07 - bike rental.json")         # 07 - bike rental.json
elaborate_csv("08 - bike rental.csv", 52)       # 05 - car parking.csv
copy_json_file("09 - restaurant.json")          # 09 - restaurant.json
copy_json_file("10 - bar.json")                 # 10 - bar.json
copy_json_file("11 - museum.json")              # 11 - museum.json
copy_json_file("12 - attraction.json")          # 12 - attraction.json
copy_json_file("13 - shopping.json")            # 13 - shopping.json
print("[INFO] Task completed!")
