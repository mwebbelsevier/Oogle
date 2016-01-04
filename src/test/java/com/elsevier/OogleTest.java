package com.elsevier;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Tests for the search engine implementations
 * 
 * Uses the Parameterised Junit test runner so that this test
 * suite can be re-run on both the versions of Oogle. The name
 * of the Oogle class under test will be shown in the JUnit results with the
 * tests as children.
 * 
 * Uses 5 example pages, added to Oogle via the addAllPages function
 * which is called in the beforeEach method 
 */
@RunWith(Parameterized.class)
public class OogleTest {
	/**
	 * Will be used to build Oogle instances for each test
	 */
	private final OogleFactory factory;
	
	/**
	 * The instance of the search engine under test
	 */
	private Oogle oogle;
	
	/**
	 * @return the factories to run this test suite with, using first value as a test name
	 */
	@Parameters(name="{0}")
	public static Iterable<Object[]> oogleFactories() {
		Object[][] factories = { 
				// name is first parameter - used to name the test
				// second parameter is the OogleFactory used during the run of the test
				{"OogleBasic", new OogleBasicFactory()},  		
				{"OogleHighPerformance", new OogleHighPerformanceFactory()} 
				};
		return Arrays.asList(factories);
	}
	
	/**
	 * Constructor for test suite - will be called by the 
	 * parameterised test runner against each type of oogle factory
	 * @param factory
	 */
	public OogleTest(String testName, OogleFactory factory) {
		// test name not needed by the class, but used by the @Parameters annotation
		// earlier. It's needed in the constructor signature so that Parameterized
		// can correctly provide the OogleFactory
		this.factory = factory;
	}
	
	@Before
	public void beforeEach() {
		// create a fresh instance of Oogle for testing
		// and all the pages to it
		oogle = factory.newInstance();
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

    @Test
    public void performanceTest() {
        addExtraPagesToSlowSearchDown();

        long start = System.currentTimeMillis();

        for(int i=0; i<1000; i++) {
            oogle.find("internet", "corporate", "is", "man");
        }

        System.out.println("1000 searches using " + oogle.getClass().getCanonicalName() + " took " + (System.currentTimeMillis() - start) + "ms");
    }

    private void addExtraPagesToSlowSearchDown() {
        for (int i=0; i<10000; i++) {
            oogle.add(new Page("http://www." + i + ".com", "The page is test page number " + i));
        }
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
