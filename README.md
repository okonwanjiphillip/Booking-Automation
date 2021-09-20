# Booking-Automation
===============


This is a selenium test suite used in automating the Search Functionality of Booking.com. Page Object Model is the design pattern used for this test.

---------------


## What is implemented in this sample Test Automation Framework?

* Tests specified using [Page Object Model - POM]
* Build tool: [Maven]
* IDE: IntelliJ or Eclipse (Project was done using IntelliJ, would be preferable)
* TestNG is also used.
* Programming language: Java - JDK 15 preferrably
* Test Data Management: Here, resources/data.json is where the test data is stored
* Browser automation: Using Selenium WebDriver(http://code.google.com/p/selenium/wiki/GettingStarted) for browser interaction
* Take screenshots on storing them on the report generated
* Integrated [Extent reports] to get 'pretty' and 'meaningful' reports from test execution.

---------------

## Usage Steps:

### Install / configure Maven:

**Maven** - Download maven.

(You can use homebrew to install, or IntelliJ should come with it's own version of maven)

### IntelliJ IDEA

* Import the existing folder into IntelliJ and it should ask you to set your JDK and it should automatically build your project depends on your configurations and you should be able to use IntelliJ.
* To be safe, run -mvn clean and -mvn install
* Go to your TestNG configurations which should come inbuilt with IntelliJ and add this to your VM options: 

		-ea -Dtestng.dtd.http=true
		
![Capture](https://user-images.githubusercontent.com/23248883/133949225-3c5fb630-93c7-4048-b035-f89b43a2c08a.PNG)

### Dependent libraries

All required libraries are already called in maven.
Chromdriver is already deployed on github, remote webdriver is already declared in the pom.xml file but the test base hasn't been configured to run it, so you would have to use chromedriver.exe

### Running sample tests


### Reports

Extent report is going to be automatically generated and available in

    /test-report/BookingReport.html
		

### Overview of Project

In the src/test/java/base we have the Testbase( This houses the setup of the project, the methods which starts the chromedriver ), TestUtils(We have methods which are useful and would be used through the project), OptionsManager(This houses the browser options for both Chrome and Firefox). 
In the src/test/java/test we have the Search.java class which houses the Search test case and all other test cases to come.

---------------

## Contact

For any information about this sample project, send an email to okechukwu.okonwanji@outlook.com.
---------------
