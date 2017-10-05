package com.elsevier;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for the search engine implementations
 * 
 * Uses 5 example pages, added to Oogle via the addAllPages function
 * which is called in the beforeEach method 
 */
public class OogleTest {
	/**
	 * The instance of the search engine under test
	 */
	private Oogle oogle;
	
	@Before
	public void beforeEach() {
		// create a fresh instance of Oogle for testing
		// and all the pages to it
		oogle = new OogleBasic();
		addAllPages();
	}

	@Test
	public void hasCorrectPageCount() {
		assertEquals(5, oogle.size());
	}
	
    @Test
    public void findsAllPagesWithTheMostCommonWord() {
        // we have 5 test pages, all containing the word "is"
        assertEquals(5, oogle.find("is").size());
    }


    @Test
    public void findsNoPagesWithUnusualWord() {
        assertEquals(0, oogle.find("Aardvark").size());
    }

    @Test
    public void findsCorrectPageForExampleCase() {
        List<Page> searchResult = oogle.find("Microsoft");
        assertEquals(1, searchResult.size());
        assertEquals("http://www.microsoft.com", searchResult.get(0).getUrl());
    }
    
    @Test
    public void searchIsNotCaseSensitive() {
    	List<Page> results = oogle.find("microSOFT");
        assertEquals(1, results.size());
        assertEquals("http://www.microsoft.com", results.get(0).getUrl());
    }

    @Test(expected = IllegalArgumentException.class)
    public void mustProvideNonBlankSearch() {
        oogle.find("");
    }

    
    @Test(expected = IllegalArgumentException.class)
    public void mustProvideNonEmptySearch() {
        oogle.find();
    }

    @Test(expected = IllegalArgumentException.class)
    public void mustProvideNonBlankSearchWords() {
    	// blank word in middle
        oogle.find("is", "", "and");    
    }

    @Test(expected = IllegalArgumentException.class)
    public void mustProvideNonNullSearch() {
        oogle.find("is", null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void cannotAddPageWithBlankUrl() {
        oogle.add(new Page("", "Stuff"));    
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotAddNullPage() {
        oogle.add(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotAddPageWithNoContent() {
        oogle.add(new Page("http://", ""));
    }	
    
    @Test
    public void multiWordSearchProvesAllWordsMustBeInTargetPage() {
    	assertEquals(0, oogle.find("corporate", "and", "aardvark", "software", "company").size());
    }
    
    @Test
    public void combinesSearchTermsUsingAnd_Example1() {
    	List<Page> searchResult = oogle.find("internet", "access");
        assertTrue(resultsContain(searchResult, "http://intranet"));
        assertTrue(resultsContain(searchResult, "http://www.bt.com"));
    }
    
    @Test
    public void combinesSearchTermsUsingAnd_Example2() {
    	List<Page> searchResult = oogle.find("our", "corporate", "official");
    	assertEquals(1, searchResult.size());
    	assertTrue(resultsContain(searchResult, "http://www.google.com"));
    }
    
    @Test
    public void wordOrderInSearchDoesNotMatter() {
    	assertTrue(resultsContain(oogle.find("officially", "worldwide"), "http://www.bt.com"));    	
    	assertTrue(resultsContain(oogle.find("worldwide", "officially"), "http://www.bt.com"));    	
    }
    
    @Test
    public void multiWordSearchWithNoResults() {
        assertEquals(0, oogle.find("world", "man").size());
    }

    
    @Test
    public void lastWordInPageCanBeFound() {
    	assertTrue(resultsContain(oogle.find("officially", "worldwide"), "http://www.bt.com"));
    }    
    
    @Test
    public void findsWordsNotSubStrings() {
    	assertCorrectNumberOfOccurrences("ompany",  0);  // just a substring
    	assertCorrectNumberOfOccurrences("world", 2); // should not pick up worldwide as world
    	assertCorrectNumberOfOccurrences("man", 2);  // shouldn't be confused by management or the punctuation near man in one instance
    }

	private void addAllPages() {
        addPage("http://www.microsoft.com",
                "Microsoft is the finest software company in the world said a Microsoft employee recently.");
        addPage("http://www.google.com",
                "Don't be evil, that is our corporate motto, and whatsa motto with that, quipped a Google official.");
        addPage("http://www.amazon.com",
                "Jeff is our leader. Jeff is the man. Jeff is the king of books and stuff.");
        addPage("http://intranet",
                "Access to the internet will be restricted to management. It is a crazy world out there");
        addPage("http://www.bt.com",
                "We officially provide internet access for both corporate users and the man on the street. That is our aim, worldwide.");
    }

    private void addPage(String url, String content) {
        oogle.add(new Page(url, content));
    }
	
    /**
     * Is the given URL seen anywhere in the list of pages
     * @param pages pages to check
     * @param url url to find
     * @return true if the list of pages contains a page with the given url
     */
    private boolean resultsContain(List<Page> pages, String url) {
		for(Page page:pages) {
			if (page.getUrl().equals(url)) {
				return true;
			}
		}
		return false;
	}
    
    private void assertCorrectNumberOfOccurrences(String searchTerm, int expectedOccurrences) {
    	assertEquals(expectedOccurrences, oogle.find(searchTerm).size());
    }
}
