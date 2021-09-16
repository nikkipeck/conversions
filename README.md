# conversions
project to perform numeric conversion roman numerals are a numeral system described by https://en.wikipedia.org/wiki/Roman_numerals
Engineering goal was to create a minimally viable solution to meet the following requirements:
* accept an URI at an HTTP endpoint
* parse a query string and convert data from an int to a string representing a roman numeral
* parse a query string representing a min and max value, convert the ints in that range to roman numerals
* limit allowable integers from 1-3,999
Testing goal was to utilize both unit and integration tests
Package Structure: com.romans.converter
* contains the implementation of the int to roman numeral conversion
* contains a client to allow ease of unit tests com.romans.server
* contains a server that will process query requests to a specific context
* contains a client to allow ease of integration test
Dependencies:
* Junit testing framework
* com.google.inject for dependency injection
* com.google.code.gson for json object creation

Future developement:
* scalability and performance improvements to RomanNumeralConverter
* thread range queries and allow them to run in parallel
* robust logging solution
* robust web server solution
