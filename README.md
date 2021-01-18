# Silla
### This application is developed by:
Niki Ryom Hansen - nikiryom at gmail dot com\
Rasmus Knoth Neldeborg - rasmus at neldeborg dot com\
Knud Billing - knudjbilling at gmail dot com\
Thomas Dahl - mail at thomasdahl dot one

This project is developed as a final Examproject for AP in Computer Science, KEA.

To run the application, change the server and DB information written in:\
/config/config.properties\
And in\
/src/main/java/dk.dmi.silla/config/Config.class


### Screenshots from Silla

#### Searching with Silla
![Searching With Silla](/Screenshots/01.SearchingWIthSilla.png?raw=true "01.SearchingWIthSilla.png")

As seen in this screenshot, Silla also supports an option for showing empty synops, 
meaning displaying rows even when no data is present in the database.
The SQL query for this feature can be seen in the last screenshot.


#### Displaying results in a DataTable
![Displaying results in a DataTable](/Screenshots/02.ResultTable.png?raw=true "02.ResultTable.png")

This DataTable is 100% generic, getting information about how many columns and names of them from the database.
Furthermore each cell contains relevant metadata that can be accessed by hovering over each cell for some time.

#### Putting values in cart for Quality Control or Exclusion
![Putting values in cart for Quality Control](/Screenshots/08.ValuesAddedToCart.png?raw=true "08.ValuesAddedToCart.png")

#### Example of QC'ed values with 1 exclusion
![One exclusion and everything else QC'ed](/Screenshots/10.AllValuesQCedInResultTable.png?raw=true "10.AllValuesQCedInResultTable.png")

#### Silla also supports raw SQL queries
![Raw SQL Queries](/Screenshots/12.RawSQLSearch.png?raw=true "12.RawSQLSearch.png")

#### These queries update in real time when search conditions change
![Raw SQL updates when search conditions change](/Screenshots/13.AlteredSQLSearchWithSeries.png?raw=true "13.AlteredSQLSearchWithSeries.png")


For more screenshots, see /Screenshots
