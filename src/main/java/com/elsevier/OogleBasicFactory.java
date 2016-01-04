package com.elsevier;

/**
 * Abstract factory implementation for OogleBasic
 */
public class OogleBasicFactory implements OogleFactory {
	public Oogle newInstance() {
		return new OogleBasic();
	}
}
