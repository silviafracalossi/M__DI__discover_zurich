import csv
import shapely

from shapely.geometry import Point, Polygon
from districts_loader import load_districts

districts = load_districts()                                # 01 - district.csv
print("[INFO] Task completed!")
