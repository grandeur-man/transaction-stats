# transaction-statistics

Instructions.

1. To compile the apllication, run the commmand below in a terminal/command prompt window
	mvn clean package
	
2. To start the application, run command below:
	java -jar target/transaction-stats-1.0-SNAPSHOT.jar
	
3. The configured duration after which a transaction will expire and no longer be included in the statistics result is 60s. It can be changed in the configuration file (application.properties)

Endpoints

	Post transactions

		Http method: POST 
		Path: /transactions
		Host: localhost:8080

		Headers
			Content-Type: application/json
			Cache-Control: no-cache

		Sample Request
		{
		  "amount": 841.6481620436773,
		  "timestamp": 1530776513054
		}

		Response Code
		 When timestamp is older than the configured duration: 204
		 Transaction with valid timestamp : 201
 
 	Get Statistics
	
		Http method: GET
		Path: /statistics
		Host: localhost:8080
		
		Headers
			Content-Type: application/json
			
		Sample Response:
		{
		    "sum": 89194.18939950266,
		    "avg": 518.5708686017597,
		    "max": 985.8658652043173,
		    "min": 15.240670078822793,
		    "count": 172
		}

Implemantation Details:

In order to achieve storing transactions and retreiving statistics of all transactions within the configured duration, I implemented a single threaded scheduler that 
purges old data and update the statistics. So this service has a tolerance of 10ms (which means worst case is that results can only contain data that are older than the configured time by 10milliseconds.

Get and Store transactions occurs in contant time.

The solution is also implemented to be thread safe using a concurrent HashMap as the in-memory store.