/*
 * WebSpider.java        
 * v1.00 
 * 27/02/2018
 *
 * Description - This application find all the broken links throughout the web site.
 * 
 * Input - 
 * 		Program takes following arguments from commandline :
 * 			1. HomePage URL of Website. It should be absolute URL e.g. https://grofers.com. ---Mandatory
 * 			2. Browser Options (--Firefox, --Chrome) ---Optional	Default Value - Firefox
 * 			3. Set Timeout in Seconds (--setTimeout) ---Optional	Default Value - 10 seconds
 * 			4. Debug Option (--Debug)				 ---Optional	Default Value - Off
 * 
 * Output - 
 * 			1. Print List of broken links.
 * 			2. Creates Report file "Report.txt" listing Broken links.	
 * 
 */

package com.website.links;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.logging.Level;
import org.apache.commons.logging.LogFactory;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CSSParseException;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.IncorrectnessListener;
import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HTMLParserListener;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.JavaScriptErrorListener;

/************************ Main Class **********************/
public class WebSpider {
	
	static WebClient webClient = new WebClient(BrowserVersion.FIREFOX_17);
	static LinkedHashSet<String> brokenLinks = new LinkedHashSet<String>();
	static LinkedHashSet<String> linkedHashSetOfLinks = new LinkedHashSet<String>();
	static String homeUrl = "";
	static boolean debug = false;
	static int timeOut = 10;	
	static String logFile = "Report.txt";
	static BufferedWriter fileLogger = null;
	
	/*
	 * Execution Starts here.
	 */
	public static void main(String[] arguments){			
	    try {
	    	int argumentsIndex = 0;
	    	/**** Reading HomePage URL of Website *****/
	    	for(String argument : arguments){
	    		argumentsIndex++;
	    		if(argument.startsWith("http")) {
	    			homeUrl = argument;
	    		}
	    		
	    		switch(argument) {
	    			case "--Debug" 		: 
		    							 debug = true;
		    							 break;
	    							 
	    			case "--Firefox" 	: 
		    							 webClient = new WebClient(BrowserVersion.FIREFOX_17);
		    							 break;
	    			case "--Chrome" 	: 
							 			 webClient = new WebClient(BrowserVersion.CHROME);
							 			 break;
	    			case "--setTimeout" : 
		    							 try {
		    								 timeOut = Integer.parseInt(arguments[argumentsIndex]);
		    							 }
		    							 catch(Exception valueException) {
		    								 System.out.println("Invalid timeout value "+arguments[argumentsIndex]);
		    								 System.out.println("Exiting");
		    								 return;
		    							 }
		    							 break;
			 		default 		: 	 
			 							 break;
	    		}
	    	}
	    	
	    	if(!homeUrl.startsWith("http")){
	    		System.out.println("Kindly provide valid URL as suggested below:\n1. http://google.com \n2. https://google.com");
	    		System.out.println("Exiting");
	    		return;
	    	}
	    	
	    	/*
	    	 * Supress HTML Unit Errors & Logging.
	    	 * Set WebClient Options.
	    	 */
	    	supressLogging();
	    	setWebClientOptions();
	    	
	    	Date startDate = new Date();
	    	long startMillisecs = startDate.getTime();
	    	
	    	/***** Verifying HomePage URL ********/
	    	if(findBrokenLink(homeUrl))
	    	{		    
		    	System.out.println("Invalid URL provided - "+homeUrl);
		    } else {
	    		
		    	System.out.println("Web Spider Started.");
		    	System.out.println("Please wait while links are getting loaded...");
		    	fileLogger = new BufferedWriter(new FileWriter(logFile, true));
		    	/***** Get Complete Set of Links present on the Website ********/
		    	getAllLinks(homeUrl);
		    			        
		        System.out.println("Total No. of Links : "+linkedHashSetOfLinks.size());
		        
		        /***** Find all Broken Links present on the Website ********/
		        for(String urlLink : linkedHashSetOfLinks) {
			        
			        if(findBrokenLink(urlLink)) {
			        	brokenLinks.add(urlLink);
			        }
		
		        }
		        fileLogger.write("********************************* "+ homeUrl+" *********************************");
		        
		        /***** Printing Results ********/
		        if(brokenLinks.size()==0) {
		        	System.out.println("All links are working fine.");
		        	fileLogger.write("No Broken Link found");
		        }
		        else {
		        	System.out.println("Total No. of Links : "+linkedHashSetOfLinks.size());
		        	fileLogger.write("Total No. of Links : "+linkedHashSetOfLinks.size());
		        	fileLogger.newLine();
			        System.out.println("No. of Broken Links : "+brokenLinks.size());
			        fileLogger.write("No. of Broken Links : "+brokenLinks.size());
			        fileLogger.newLine();
			        fileLogger.flush();
			        
			        int counterBrokenLink=0;
			        for(String urlLink : brokenLinks) {
			        		counterBrokenLink++;
				        	System.out.println(counterBrokenLink+". Broken Link : " + urlLink);		
					        fileLogger.write(counterBrokenLink+". Broken Link : " + urlLink);
					        fileLogger.newLine();
					        fileLogger.flush();
			        }
		        }
		        fileLogger.close();
	    	}
	    	
	    	Date endDate = new Date();
	    	long endMillisecs = endDate.getTime();
	    	
	    	long executionTimeInMinutes = (endMillisecs - startMillisecs);
	    	
	    	System.out.println("Total Execution time in minutes : "+executionTimeInMinutes/60000);
	    }
	    catch(Exception genericException) {
	    	if(debug) {
	    		genericException.printStackTrace();
	    	}
		}

	}
	
	/*
	 * Spawns LinkSpider thread instance to fetch all links throughout the website.
	 */
	public static void getAllLinks(String currentUrl) {
		try {		
			
			LinkSpider linkSpider = new LinkSpider(currentUrl);
			linkSpider.start();
			if(WebSpider.debug) {
				System.out.println("linkSpider Started.");
			}
			linkSpider.join();	
			if(WebSpider.debug) {
				System.out.println("linkSpider ended.");
			}
		}
    	catch(FailingHttpStatusCodeException failCodeException) {
    		if(debug) {
    			failCodeException.printStackTrace();
    		}
    	}
		catch(Exception genericException) {
			if(debug) {
				genericException.printStackTrace();
			}
		}
	}
		
	/*
	 * Appends HomeUrl only if relative url is present
	 */
	public static String getCompleteUrl(HtmlAnchor objLink)
	{
		String[] symbolsCompleteURL = {"http","../","./"};
		
		for(String symbol : symbolsCompleteURL) {
	        if((objLink.getHrefAttribute().startsWith(symbol))) {
	        	return objLink.getHrefAttribute();
	        } 
		} 		
		
		if(objLink.getHrefAttribute().startsWith("//")) {
			return objLink.getHrefAttribute().substring(2);
		}
		
        return homeUrl + objLink.getHrefAttribute();
	}
	/*
	 * Verify if specified url is broken. If yes, then return true, otherwise return false.
	 */
	public static boolean findBrokenLink(String currentUrl)	{
		int statusCode = 0;
		try {
			statusCode = webClient.getPage(currentUrl).getWebResponse().getStatusCode();
        	if(statusCode==200) {
        		if(debug) {
        			//System.out.println("Working Link : "+currentUrl);
        		}
        		return false;
        	} else {
        		if(debug) {
        			//System.out.println("Broken Link : "+currentUrl+ "\tstatusCode : "+statusCode);
        		}
        		return true;
        	}  
    	}
    	catch(FailingHttpStatusCodeException failCodeException) {
    		if(debug) {
    			System.out.println(currentUrl + " not connected. Status Code : "+failCodeException.getStatusCode());
    		}
    		return true;
    	}
    	catch(Exception genericException) {
    		if(debug) {
    			System.out.println("ValidateLinks : Exception Occured - "+genericException.getMessage() );
    			genericException.printStackTrace();
    		}
    		return true;
    	}
	}
	
	/*
	 * Supress HTMLUnit Logging of Errors and Warnings
	 */
	public static void supressLogging() {
    	
    	LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");

        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF); 
        java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);
	}
	
	/*
	 * Set WebClient Options
	 */
	@SuppressWarnings("serial")
	public static void setWebClientOptions() {
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        /******Timeout for Java Script Execution in Milliseconds********/
        webClient.setJavaScriptTimeout(timeOut*1000);
        webClient.getOptions().setJavaScriptEnabled(true);
        if(debug) {
        	System.out.println("Timeout set to " + timeOut +" seconds.");
        }
        //webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        /******Timeout for WebConnection during Connection Establishment and Data Read Operation in Milliseconds********/
        webClient.getOptions().setTimeout(timeOut*1000);
        //webClient.getOptions().setHomePage(homeUrl);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setUseInsecureSSL(true);
        
        webClient.setIncorrectnessListener(new IncorrectnessListener() {

            @Override
            public void notify(String arg0, Object arg1) {
                // TODO Auto-generated method stub

            }
        });
        
        webClient.setCssErrorHandler(new SilentCssErrorHandler() {

            @Override
            public void warning(CSSParseException exception) throws CSSException {
                // TODO Auto-generated method stub

            }

            @Override
            public void fatalError(CSSParseException exception) throws CSSException {
                // TODO Auto-generated method stub

            }

            @Override
            public void error(CSSParseException exception) throws CSSException {
                // TODO Auto-generated method stub

            }
        });
        
        webClient.setHTMLParserListener(new HTMLParserListener() {

			@Override
			public void error(String arg0, URL arg1, String arg2, int arg3, int arg4, String arg5) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void warning(String arg0, URL arg1, String arg2, int arg3, int arg4, String arg5) {
				// TODO Auto-generated method stub
				
			}
        });
        webClient.setJavaScriptErrorListener(new JavaScriptErrorListener() {

			@Override
			public void loadScriptError(HtmlPage arg0, URL arg1, Exception arg2) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void malformedScriptURL(HtmlPage arg0, String arg1, MalformedURLException arg2) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void scriptException(HtmlPage arg0, ScriptException arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void timeoutError(HtmlPage arg0, long arg1, long arg2) {
				// TODO Auto-generated method stub
				
			}
        	
        });	
	}
}