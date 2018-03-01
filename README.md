# Coders

# TITLE - 
	WebSpider

# DESCRIPTION -
	This application find all the broken links throughout the web site. You just need to provide Homepage URL of the website.
	
# Usage -
	Input - Application takes following arguments :
 			1. HomePage URL of Website. It should be absolute URL e.g. https://grofers.com. ---Mandatory
  			2. Browser Options (--Firefox, --Chrome) ---Optional	Default Value - Firefox
  			3. Set Timeout in Seconds (--setTimeout) ---Optional	Default Value - 10 seconds
  			4. Debug Option (--Debug)				 ---Optional	Default Value - Off
			
	Output - Print List of broken links on Console.
			 Creates Report file "Report.txt" listing Broken links.	
	
	Note - Debug Option is only for debugging purpose.

# Compilation and Packaging -
  1. From Command line, go to Project Directory.
  2. Run command "mvn compile"
  3. Run command "mvn clean package". WebSpider-1.00.jar will be created in target folder.

# Execution -
  1. Using Maven Command -	
    With Configurable Settings -
      mvn exec:java -Dexec.mainClass="com.website.links.WebSpider" -Dexec.args="<URL> --setTimeout <Value> --Chrome --Debug"
    With Default Setting -
      mvn exec:java -Dexec.mainClass="com.website.links.WebSpider" -Dexec.args="<URL>"	

  2. Using WebSpider-1.00.jar -
    With Configurable Settings -
      java -jar WebSpider-1.00.jar <URL> --setTimeout <Value> --Chrome --Debug
    With Default Setting -
      java -jar WebSpider-1.00.jar <URL>

# Examples -
  1. Using Maven Command -
    With Configurable Settings -
      mvn exec:java -Dexec.mainClass="com.website.links.WebSpider" -Dexec.args="https://www.google.com --setTimeout 30 --Chrome" 
    With Default Settings -
      mvn exec:java -Dexec.mainClass="com.website.links.WebSpider" -Dexec.args="https://www.google.com" 
  2. Using WebSpider-1.00.jar-	
    With Configurable Settings -
      java -jar WebSpider-1.00.jar https://www.google.com --setTimeout 30 --Chrome
    With Default Settings - 
      java -jar WebSpider-1.00.jar https://www.google.com
	
# Sample Execution -
