package com.romans.converter;

/**
 * RomanNumeralConverterImpl
 * converts ints into roman numeral strings according to the definition at https://en.wikipedia.org/wiki/Roman_numerals
 * @author nikki
 *
 */
public class RomanNumeralConverterImpl implements RomanNumeralConverter {

	public static final String[] THOUSANDS = {"", "M", "MM", "MMM"};
	public static final String[] HUNDREDS = {"","C","CC","CCC","CD","D","DC","DCC","DCCC","CM"};
	public static final String[] TENS = {"","X","XX","XXX","XL","L","LX","LXX","LXXX","XC"};
	public static final String[] ONES = {"", "I","II","III","IV","V","VI","VII","VIII", "IX"};

	@Override
	public String intToRomanConversion(int i) {
		StringBuilder sb = new StringBuilder();
    	
    	sb.append(THOUSANDS[i/1000]);
    	sb.append(HUNDREDS[i/100 % 10]);
    	sb.append(TENS[i/10 % 10]);
    	sb.append(ONES[i % 10]);
    	
    	return sb.toString();
	}
}
