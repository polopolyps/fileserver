Polopoly Fileserver
===================

The Fileserver is a Java webapp that provides a simple  RESTful API to store and retrieve files. The files are persisted in the underlying file system and available via http. 

This server was developed by PS  and have been implemented and improved in many projects(List of projects that use it). The project is part of the PS TOOLS umbrella and initially was know as Image server.

The tool is commonly used together with the Polopoly HttpImageManager to avoid to store the images as content, persisted in the DB.
<dl>
  <dt>The project and the tool is not supported by Atex and is maintained in a collaborative way. Please feel free to fork this project.</dt>
  
</dl>  


Getting started.
----------------
You will need Java and Maven installed. Checkout the code and run
    
    mvn install

This will build the application and run the integration test. 

A fileserver.war file  will be generated under target/

The maven project is configure to use the Jetty web container pluging and can easily used running 
    
    mvn jetty:run

The server is configure to listen the port 8082

How to install.
---------------

Deploy the fileserver.war in your preferred web container. Our recommendation is Apache Tomcat.

Configuration:
--------------

There is two modes: Test and Production. Each mode provide a default properties that can be overridden.

Production configuration:

    fileserver.FileRepositoryPath=/var/file_repository/
    fileserver.BackUpRepositoryPath=/var/file_repository-backup/
    fileserver.ActivateBackUp=false


Know issues:
------------

Mime types are need to be configured. http://docs.oracle.com/javase/7/docs/api/javax/activation/MimetypesFileTypeMap.html

The RESTfull API
----------------

1.  To upload a file:

Perform a http post indicating the Content-Type

    curl -H "Content-Type:image/jpeg" -v --data-binary @src/test/fixtures/images/test_image.jpg http://localhost:8082/fileserver/file/test_image.jpg
 
The response will contained the attributes:

    Location: http://localhost:8082/fileserver/file/34/filename/test_image.jpg
    Metadata: http://localhost:8082/fileserver/metadata/34/filename/test_image.jpg.metadata

2.  Getting the file:

Perform a http get with the Location attribute as url.

    curl http://localhost:8082/fileserver/file/34/filename/test_image.jpg -o copy-of-test_image.jpg

 
3.  Getting the file metadata:

    curl http://localhost:8082/fileserver/metadata/34/filename/test_image.jpg.metadata

Response:

    filename=test_image.jpg
    mimeType=image/jpeg
    path=/Users/ubaldo/fileserver0-99/34
