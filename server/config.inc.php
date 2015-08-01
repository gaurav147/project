<?php


//DB parameters


$dbHost = "localhost";

$dbUser = "root";

$dbPassword = "";

$dbName = "sipdb";

//connect to the db
function connect()

{
	$db = mysql_connect($GLOBALS["dbHost"], $GLOBALS["dbUser"], $GLOBALS["dbPassword"]);
	
if($db == false)
		
die("Error while connecting to the DB");
	
mysql_select_db($GLOBALS["dbName"], $db)
		
or die("Error while connecting to the DB");
	
return $db;
}


?>