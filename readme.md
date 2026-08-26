
# Build the application:
`./gradlew.ps1 build`

# Build without tests:
`./gradlew.ps1 build -x test`

# Run tests:
`./gradlew.ps1 clean test --info`

# Configure debug execution:
-  Run Tomcat Server
-  Artifact: Gradle : com.romanpulov : piastria-wss.bootWar, output directory: D:\prj\piastria-wss\build\libs
-  HTTP Port: 3000
-  Deployment -> Application context: `/ide_piastria_wss_war`
-  Open browser after launch: `http://localhost:3000/ide_piastria_wss_war/payment-objects`
-  CORS setup for TomCat: 
   - web.xml.cors

# Run one test only:
`./gradlew.ps1 test --tests *DataGeneratorTest`

# Build and deploy to local Tomcat:
`./gradlew.ps1 clean build deploy`

# Build and deploy Int version:
- `./gradlew.ps1 build`
- `./gradlew.ps1 deployInt`
- copy piastria-int-wss.xml

# Build and deploy Prod version:
* `./gradlew.ps1 build`
* `./gradlew.ps1 deployProd`
*  copy piastria-wss.xml

# Build with specified tomcat home directory:
`./gradlew.ps1 clean build deployInt -Ptomcat_home=D:/prj/apache-tomcat-9.0.24`

# Generate the ER diagram (db/model/erd.md, db/model/erd.png):
`./generate_erd.ps1`
