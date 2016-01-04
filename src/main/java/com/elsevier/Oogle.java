package com.elsevier;

import java.util.List;

/**
 * Search engine that can receive crawled pages
 * and provide search results
 */
public interface Oogle {
	/**
	 * Receive a page from the web crawler - note, this will not be run multi-threaded
	 * The page must have a url and search text that are not blank
	 * It is expected that the page text will be words separated by space, comma or full stop
	 * @param page Details of page found by the crawler
	 */
	void add(Page page);
	
	/**
	 * Find all pages which have ALL the words specified, case insensitive
	 * @param words array of search terms - none of these can be blank or empty
	 * @return a list of pages which meet the search criteria
	 */
	List<Page> find(String ... words);
	
	/**
	 * @return how many pages are currently known to Oogle
	 */
	int size();
}
