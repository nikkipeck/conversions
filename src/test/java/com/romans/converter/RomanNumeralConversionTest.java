package com.romans.converter;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class RomanNumeralConversionTest {
	
	private Injector rninjector;
	private RomanNumeralClient client;
	
	@Before
	public void setup() {
		rninjector = Guice.createInjector(new RomanNumeralModule());
		client = rninjector.getInstance(RomanNumeralClient.class);
	}
	
	@Test //ones
    public void test3() {
        assertEquals("III", client.convertInt(3));
    }
	
	@Test //tens
	public void test34() {
		assertEquals("XXXIV", client.convertInt(34));
	}
	
	@Test //hundreds
	public void test176() {
	    assertEquals("CLXXVI", client.convertInt(176));
	}
	
	@Test //thousands
    public void test3284() {
        assertEquals("MMMCCLXXXIV", client.convertInt(3284));
    }
}