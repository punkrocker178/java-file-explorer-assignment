## About:
This is a simple javafx project about File explorer with search functionality of [Apache Lucene](https://lucene.apache.org/)  

## Prerequisite:
- JDK11
- [JavaFX 11](https://gluonhq.com/products/javafx/)
- Add javafx module path in your project : ```--module-path C:\Users\<user>\Downloads\javafx-sdk-11\lib --add-modules=javafx.controls```
- [Guide](https://stackoverflow.com/questions/52144931/how-to-add-javafx-runtime-to-eclipse-in-java-11)  
**Note:** ```module-path``` is the path to your downloaded JavaFX
## Upcoming work:
- Implement Lucene index & search functions
- Enhance UI
- Open file
- Create / Remove / Rename folders
- Apply multithreading or async task for lucene indexing

## Program Flow:
1. When first started, user is prompted to choose where to store index files for lucene to run
2. After selected the directory, that directory will be the base path of the application. Lucene starts indexing the base path.
3. After Lucene done indexing you can type some content then click the search button
4. Results will show file name and file location


## References: 
- https://stackoverflow.com/questions/16176701/switch-between-panes-in-javafx
- https://www.baeldung.com/lucene-file-search
- https://lucene.apache.org/core/8_5_0/demo/overview-summary.html#overview.description

## Run:
Open terminal and run the command accordingly (Suppose you are in the project directory) 
- Windows:
```java --module-path .\library\javafx-sdk-11.0.2_windows\lib --add-modules=javafx.controls,javafx.fxml -Dfile.encoding=windows-1252 -jar .\out\artifacts\java_lucene_project_jar\java_lucene_project.jar```
- Linux:
```java --module-path ./library/javafx-sdk-11.0.2_linux/lib --add-modules=javafx.controls,javafx.fxml -Dfile.encoding=UTF-8 -jar ./out/artifacts/java_lucene_project_jar/java_lucene_project.jar```

## Demo:
[pic1]: Screenshots/Screenshot%20from%202020-03-31%2017-33-20.png "Choose directory for index"
[pic2]: Screenshots/Screenshot%20from%202020-03-31%2017-33-32.png "Main view"
[pic3]: Screenshots/Screenshot%20from%202020-03-31%2017-33-49.png "Table view"

Choose Index directory:  
![alt text][pic1]

File explorer view:  
![alt text][pic2]

Results from lucene index:  
![alt text][pic3]
