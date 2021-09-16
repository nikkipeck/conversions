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
	
	@Test
	public void test34() {
		assertEquals("XXXIV", client.convertInt(34));
	}
	
	@Test
	public void test3() {
		assertEquals("III", client.convertInt(3));
	}
}