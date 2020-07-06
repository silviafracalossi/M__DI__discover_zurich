# Discover Zurich
Project for the Data Integration course.

## Steps for Project configuration
- Create a PostgreSQL database called "discover_zurich", extending postgis
- Populate the database by restoring the custom backup using the file: "datasets/database/3_database_backup_custom"
- Keep the PostgreSQL server on
- Make sure that the ontop command in "postgres_endpoint.sh" executes the latest ontop/geof version
- Run the "postgres_endpoint.sh" file
- Open the project in Intellij IDEA
- Compile the Maven file "pom.xml"
- Make sure that the JDBC driver for Denodo is configured:
    - Right-click on the top project folder
    - select "Open Module Settings" > "Modules" > "Dependencies2
    - click '+' at the bottom and select "JARs or directories..."
    - select the Denodo JDBC driver (directory example: /.../DenodoPlatform7.0/tools/client-drivers/jdbc/denodo-vdp-jdbcdriver.jar)
- Execute the main method in the "src/main/java/MainWindow.java" file
