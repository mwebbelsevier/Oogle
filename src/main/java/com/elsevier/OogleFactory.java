package com.elsevier;

/**
 * Used to build instances of the search engine
 */
public interface OogleFactory {
	/**
	 * @return new instance of an "Oogle" search engine
	 */
	Oogle newInstance();
}
