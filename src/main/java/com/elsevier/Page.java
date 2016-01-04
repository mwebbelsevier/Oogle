package com.elsevier;

/**
 * A POJO for storing the data of a page
 */
public class Page {
	/**
	 * Source URL of the page
	 */
	private String url;
	
	/**
	 * Original plaintext of the page
	 */
	private String text;

	/**
	 * @param url source url
	 * @param text plaintext of the page
	 */
	public Page(String url, String text) {
		this.url = url;
		this.text = text;
	}

	/**
	 * @return Source URL of the page
	 */
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return plaintext of the page
	 */
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
