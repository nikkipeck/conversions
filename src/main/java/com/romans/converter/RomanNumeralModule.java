package com.romans.converter;

import com.google.inject.AbstractModule;
import com.romans.server.RomanNumeralServer;

public class RomanNumeralModule extends AbstractModule{

	@Override
	protected void configure() {
		 bind(RomanNumeralConverter.class).to(RomanNumeralConverterImpl.class);
	}
}
