# RiskEngine
Mercury Code Challenge.

The goal of this code challenge is to build a prototype risk engine. The risk engine:
●	Initializes from a data set containing spendable balances for a fixed set of tokens for each user.

●	Provides a synchronous WithdrawBalance API that checks if a user has sufficient balance of a token - the requested_quantity and token

●	If the user does not have sufficient balance the API returns INSUFFICIENT_BALANCE

●	If the user has sufficient balance the API:

○	Decrements the user’s spendable balance of the token by the requested_amount
○	Returns SUFFICIENT_BALANCE
●	Once the trade has settled the risk engine receives a Kafka message containing the actual amounts of the bought and sold tokens - bought_token, bought_quantity, sold_token, sold_quantity. Note - the sold_quantity is likely to be different to the requested_quantity. Upon receiving this message the risk engine:
○	Modifies the available balance of the sold_token to account for the difference between sold_quantity and the requested_quantity
○	Increments the balance of the bought_token by the bought_quantity

For example:

For this user…
User_Id	Symbol
	USD	EUR	BTC	BCH	ETH
100	0	3128.39	81.807344	68.653219	136.152897

●	WithdrawBalance( 100, USD, 250 ) will return INSUFFICIENT_BALANCE
●	WithdrawBalance( 100, BTC, 7) will return SUFFICIENT_BALANCE, after modifying the available balances…
	USD	EUR	BTC	BCH	ETH
100	0	3128.39	74.807344	68.653219	136.152897

●	When the risk engine receives a settlement message…
{ “user_id”:”100”, 
“bought_token”:”ETH”, “bought_quantity”:”87.35”, 
“sold_token”:BTC”, “sold_quantity”:”6.9” }
●	...it modifies the available balances…
	USD	EUR	BTC	BCH	ETH
100	0	3128.39	74.907344	68.653219	223.502897


Implementation notes:
●	There is a test data set containing available balances for 900 users here. Don’t worry about putting this in a database and loading it - feel free to include this data inline in your test code.
●	This should be implemented as a Linux Microservice that provides a REST API and is a Kafka client.
●	Given the latency requirements on the WithdrawBalance API, I’m assuming that you will keep the table of available balances in RAM. However you should assume that you have insufficient RAM to store balances for all users. In this prototype please store no more than 300 users’ data in RAM. I’m interested in your strategy for deciding which users’ data gets replaced…
●	Feel free to add items (sequence numbers, order ids, etc.) to the APIs as you think fit. Similarly use whatever API/Message/Variable naming scheme you are comfortable with.
●	Use whatever language you feel most comfortable with.
●	I don’t expect this to take too long - if there are parts you don’t get to (unit tests, error conditions, etc.) please describe these.

Development resources:
●	Kafka docker image, and quickstart: https://hub.docker.com/r/confluent/kafka/
●	Kafka client libraries: https://cwiki.apache.org/confluence/display/KAFKA/Clients
●	Kafka Server Setup: https://kafka.apache.org/quickstart 

