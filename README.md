# CurrencyConverter

This mini web application was a class project. 

The following are needed to run this application: SQL database, Apache Tomcat, Java, and a public API key. 

## How the application works: 

1. User selects a currency from a drop-down menu, enters the amount in USD into the search field, and chooses a date.

<img width="1190" alt="Screen Shot 2022-05-27 at 1 37 47 PM" src="https://user-images.githubusercontent.com/26610251/170770908-332f3c53-6a13-4ac7-a3db-bbe7760173b8.png">

2. An HTTP GET request is sent to the Java Servlet using Ajax. 
3. If a date was not entered or a date was entered that does not yet exist in the database, the server acts as a client which automatically sends an HTTP GET request to the public API (using an API key as part of the URI), and requests the currency exchange information of either the current date or the specified date. The server parses the JSON data sent by the public API, and then populates the database with the new currency exchange data. 
4. Otherwise, if a date was entered that does exist in the database, the server simply retrieves the information from the database.
5. The data is returned to the client as a JSONobject. JavaScript parses the JSON data and the new currency information is displayed on the webpage. 

For example: 

<img width="643" alt="Screen Shot 2022-05-27 at 1 46 24 PM" src="https://user-images.githubusercontent.com/26610251/170772199-a25b8f75-099d-453d-b275-a11ed62d5167.png">

A date was not entered, so the current date's currency exchange was returned. 

<img width="597" alt="Screen Shot 2022-05-27 at 1 46 42 PM" src="https://user-images.githubusercontent.com/26610251/170772212-25029b85-ce4e-4a4e-aa7d-b55c96543305.png">



## Directory structure: 

```bash

.
├── build
│   ├── empty
│   ├── generated-sources
│   │   └── ap-source-output
│   └── web
│       ├── CS425_P1.js
│       ├── META-INF
│       │   ├── MANIFEST.MF
│       │   └── context.xml
│       ├── WEB-INF
│       │   ├── classes
│       │   │   └── edu
│       │   │       └── jsu
│       │   │           └── mcis
│       │   │               └── cs425
│       │   │                   └── p1
│       │   │                       ├── CurrencyDatabase.class
│       │   │                       ├── CurrencyServlet.class
│       │   │                       └── ExchangeServlet.class
│       │   ├── lib
│       │   │   ├── json-simple-1.1.1.jar
│       │   │   └── mysql-connector-java-8.0.19.jar
│       │   └── web.xml
│       ├── index.html
│       └── jquery-3.6.0.min.js
├── build.xml
├── dist
│   └── CS425_P1.war
├── nbproject
│   ├── ant-deploy.xml
│   ├── build-impl.xml
│   ├── genfiles.properties
│   ├── private
│   │   ├── private.properties
│   │   └── private.xml
│   ├── project.properties
│   └── project.xml
├── src
│   ├── conf
│   │   └── MANIFEST.MF
│   └── java
│       └── edu
│           └── jsu
│               └── mcis
│                   └── cs425
│                       └── p1
│                           ├── CurrencyDatabase.java
│                           ├── CurrencyServlet.java
│                           └── ExchangeServlet.java
├── test
└── web
    ├── CS425_P1.js
    ├── META-INF
    │   └── context.xml
    ├── WEB-INF
    │   └── web.xml
    ├── index.html
    └── jquery-3.6.0.min.js


```
