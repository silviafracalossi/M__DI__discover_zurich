# Discover Zurich
Project for the Data Integration course.


## Steps for Data Preparation (skippable)
- Download the files and place them in the "datasets/original" folder
- Execute in the main directory "python python_elaboration/main.py"
- Create a PostgreSQL database called "discover_zurich"
- Populate the database in one of these two ways:
    - In the database query tool, run "datasets/database/1_creation_script.sql", all the files in "datasets/database/insertion scripts" and manually import the CSV files that can be found in "datasets/database/" into the corresponding table
    - Restore the database from a custom backup. File: "datasets/database/3_database_backup_custom"
- Run the script "datasets/database/"
- Configure the project as listed below

## Steps for Project configuration
- Connect the data sources to Denodo using the "application_files/discover_zurich-denodo.vql" file
- Start the Denodo Virtual DataPort server
- Start the endpoint:
    - Make sure that the ontop command in "start_sparql_endpoint.sh" points to the "ontop-cli-4.0.0-beta-2-SNAPSHOT-geof" one
    - Run the "start_sparql_endpoint.sh" file
- Open the project in Intellij IDEA
- Compile the Maven file "pom.xml"
- Make sure that the JDBC driver for Denodo is configured:
    - Right-click on the top project folder
    - select "Open Module Settings" > "Modules" > "Dependencies2
    - click '+' at the bottom and select "JARs or directories..."
    - select the Denodo JDBC driver (directory example: /.../DenodoPlatform7.0/tools/client-drivers/jdbc/denodo-vdp-jdbcdriver.jar)
- Execute the main method in the "src/main/java/MainWindow.java" file
