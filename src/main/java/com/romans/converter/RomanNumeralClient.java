package com.romans.converter;

import com.google.inject.Inject;

/*
 * RomanNumeralClient 
 * simple client to allow testing of RomanNumeralConverter
 */
public class RomanNumeralClient {
		
	private RomanNumeralConverter rmc;
	
	@Inject
	public RomanNumeralClient(RomanNumeralConverter rmc) {
		this.rmc = rmc;
	}
	
	public String convertInt(int i){
		return rmc.intToRomanConversion(i);
   }
}