/*
 * LinkSpider.java
 * v1.00
 * 28.02.2018
 * 
 * Description - This thread fetches Complete URL Set recursively iterating over links. * 
 * 
 */
package com.website.links;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class LinkSpider extends Thread {
	String parentUrl;
	
	public LinkSpider(String url) {
		this.parentUrl = url;
	}

	@Override
	public void run() {
		try {	
			/*********** Find all links on the CurrentPage ****************/
			List<HtmlAnchor>  currentListOfLinks = ((HtmlPage)WebSpider.webClient.getPage(parentUrl)).getAnchors();
			
			if(WebSpider.debug) {
				System.out.println("Spider thread size " +currentListOfLinks.size());
			}
			
			/*********** Find new Links and add to the Parent LinkedHashSet **********/
			Set<String> newLinks = new HashSet<String>();
			for(HtmlAnchor objLink : currentListOfLinks) {
				newLinks.add(WebSpider.getCompleteUrl(objLink));
			}			
			newLinks.removeAll(WebSpider.linkedHashSetOfLinks);
			
			if(WebSpider.debug) {
				System.out.println("New Links Size " +newLinks.size());
			}
			
			/****** Spawn ChildSpider over each new link recursively ******/
			if(newLinks.size()>0) {	
				WebSpider.linkedHashSetOfLinks.addAll(newLinks);
				for(String strLink : newLinks) {
					if(WebSpider.debug) {
						System.out.println("No. of links : "+WebSpider.linkedHashSetOfLinks.size()+"\t New Link URL: "+strLink);
					}
					
					/*****Comment this Code starting from here to run on Single Page*******/
					if((strLink.startsWith("http"))&&(strLink.contains(WebSpider.homeUrl)))
					{
						if(WebSpider.debug) {
							System.out.println("Going to iterate links over  "+strLink);
						}
						LinkSpider childSpider = new LinkSpider(strLink);
						childSpider.start();
						if(WebSpider.debug) {
							System.out.println("Fly over from "+strLink);
						}
						childSpider.join();
						if(WebSpider.debug) {
							System.out.println("Joined back to "+strLink);
						}
					}
					/*****Comments should end here to run on Single Page*******/
				}
			}
		}
    	catch(FailingHttpStatusCodeException failCodeException) {
    		if(WebSpider.debug) {
    			System.out.println(parentUrl + " not connected. Status Code : "+failCodeException.getStatusCode());
    		}
    	}
		catch(Exception genericException) {
			if(WebSpider.debug) {
				System.out.println("LinkSpider : Exception Occured - "+genericException.getMessage() );
				genericException.printStackTrace();
			}
		}		
	}
}