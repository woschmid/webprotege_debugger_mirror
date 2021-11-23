   WebProtégé
==========

What is WebProtégé?
-------------------

WebProtégé is a free, open source collaborative ontology development environment.

It provides the following features:
- Support for editing OWL 2 ontologies
- A default simple editing interface, which provides access to commonly used OWL constructs
- Full change tracking and revision history
- Collaboration tools such as, sharing and permissions, threaded notes and discussions, watches and email notifications
- Customizable user interface
- Support for editing OBO ontologies
- Multiple file formats for upload and download of ontologies (supported formats: RDF/XML, Turtle, OWL/XML, OBO, and others)

WebProtégé runs as a Web application. End users access it through their Web browsers.
They do not need to download or install any software. We encourage end-users to use

https://webprotege.stanford.edu

If you have downloaded the webprotege war file from GitHub, and would like to deploy it on your own server,
please follow the instructions at:

https://github.com/protegeproject/webprotege/wiki/WebProtégé-4.0.0-beta-x-Installation

Building
--------

To build WebProtégé from source

1) Clone the github repository
   ```
   git clone https://git-ainf.aau.at/interactive-KB-debugging/webprotege_debugger.git
   ```
2) Open a terminal in the directory where you clone the repository to
3) Use maven to package WebProtégé
   ```
   mvn clean package
   ```
   or
   ```
   mvn clean package -DskipTests
   ```
   to skip tests for faster compiles
   
5) The WebProtege .war file will be built into the webprotege-server directory

## Requirements

1) Installation of a **mondodb** instance
2) Create a folder **/srv/webprotege**

Running from Maven
------------------
To run WebProtégé in SuperDev Mode using maven

0) a pre-installed **mongodb** instance 
    ```
    verify that you have installed and started mongodb on your host
    ```

1) Start the GWT code server in one terminal window
    ```
    mvn gwt:codeserver
    ```
2) In a different terminal window start the tomcat server
    ```
    mvn -Denv=dev tomcat7:run
    ```
   1. **IMPORTANT** verify you have created a folder /srv/webprotege which can be written to and read from
   2. **IMPORTANT** verify your tomcat can write into an existing log folder named webprotege (e.g. /var/log/webprotege in Debian/Ubuntu)
3)    
3) Browse to WebProtégé in a Web browser by navigating to [http://localhost:8080](http://localhost:8080)


Bootstrap WebProtégé with an Admin Account
------------------
If not yet done, set up an administrator account

0) a pre-installed **mongodb** instance
    ```
    verify that you have installed and started mongodb on your host
    ```

1) CD into the compiled target directory of the webprotege-cli module 

2) Execute the appropriate webprotege-cli.jar application. Enter your chosen user name, email address and password to complete the setup.
   ```
   java -jar webprotege-cli.jar create-admin-account
   ```
   ```
   Please enter a user name for the administrator:
   ```
   ```
   Please enter an email address for the administrator:
   ```
   ```
   Please enter a password for the administrator account:
   ```
   ```
   Please confirm the password:
   ```
   
   _Note: In the above command, replace ```webprotege-cli.jar``` with the compiled one with the version number._

Edit the WebProtégé Settings
-------------------

1) Sign in using the admin account that you created as part of the setup above.

2) Once you have signed in, and you get the message `WebProtégé is not configured properly` on top of the page
    navigate to the application settings page at the URL obtained by appending #application/settings to the URL of the WebProtégé home page. For example, 

   ``` http://localhost:8080/#application/settings```
3) Enter values for every required (*) fields

Building Docker image
-------------------
```docker build -t webprotege_debugger --build-arg WEBPROTEGE_VERSION=4.0.2 .```


Running from Docker
-------------------

To run WebProtégé using the Docker container

1) Create a new file called "docker-compose.yml" and copy-and-paste the following text:
   ```yml
   version: "3"
   
   services:
   
     wpmongo:
       container_name: webprotege-mongodb
       image: mongo:4.1-bionic
       restart: unless-stopped
       volumes: 
         - ./.protegedata/mongodb:/data/db
   
     webprotege:
       container_name: webprotege
       image: webprotege_debugger
       depends_on:
         - wpmongo
       restart: unless-stopped
       environment:
         - webprotege.mongodb.host=wpmongo
       volumes: 
       - ./.protegedata/protege:/srv/webprotege
       ports:
         - 5000:8080
   ```
2) Enter this following command in the Terminal to start the docker container.
   ```bash
   $ docker-compose up -d
   ```
   
3) Create the admin user (follow the questions prompted to provider username, email and password)
   ```bash
   docker exec -it webprotege java -jar /webprotege-cli.jar create-admin-account
   ```
3) Browse to WebProtégé in a Web browser by navigating to [http://localhost:5000/#application/settings](http://localhost:5000/#application/settings)

4) To stop WebProtégé and MongoDB:
   ```bash
   docker-compose down
   ```
   Sharing the volumes used by the WebProtégé app and MongoDB allow to keep persistent data, even when the containers stop. Default shared data storage:
   - WebProtégé will store its data in the source code folder at `./.protegedata/protege` where you run docker-compose
   - MongoDB will store its data in the source code folder at `./.protegedata/mongodb` where you run docker-compose
   Path to the shared volumes can be changed in the `docker-compose.yml` file.