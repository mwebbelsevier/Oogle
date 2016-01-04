package com.elsevier;

/**
 * Abstract factory implementation for OogleHighPerformance
 */
public class OogleHighPerformanceFactory implements OogleFactory {
	public Oogle newInstance() {
		return new OogleHighPerformance();
	}
}
