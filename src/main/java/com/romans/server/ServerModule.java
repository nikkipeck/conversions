package com.romans.server;

import com.google.inject.AbstractModule;
import com.romans.converter.RomanNumeralConverter;
import com.romans.converter.RomanNumeralConverterImpl;

public class ServerModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(RomanNumeralServer.class);
		bind(RomanNumeralConverter.class).to(RomanNumeralConverterImpl.class);
	}

}
