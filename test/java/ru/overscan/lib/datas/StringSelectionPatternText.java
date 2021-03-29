package ru.overscan.lib.data;

import junit.framework.Assert;

import org.junit.Test;

public class StringSelectionPatternText {
	

	@Test
	public void shouldNotMatchRepetitive(){
		StringSelectionPattern pattern = new StringSelectionPattern("маш 5 5");
		
		Assert.assertTrue(pattern.fit("машина 55"));
	}
	
	
	@Test
	public void shouldMatchStringCaseInsensitive(){
		StringSelectionPattern pattern = new StringSelectionPattern("мама мыла");
		
		Assert.assertTrue(pattern.fit("мама мыла"));
		Assert.assertTrue(pattern.fit("МАМА мыла"));
		Assert.assertTrue(pattern.fit("я смотрел, а МАМА мыла"));
		Assert.assertTrue(pattern.fit("мыла МАМА"));
		Assert.assertFalse(pattern.fit("мама мылом"));
	}
	
	@Test
	public void shouldNotMatchStringCaseSensitive(){
		StringSelectionPattern pattern = new StringSelectionPattern("мама мыла", true);
		
		Assert.assertTrue(pattern.fit("мама мыла"));
		Assert.assertFalse(pattern.fit("мама Мыла"));
		Assert.assertFalse(pattern.fit("мама мылом"));
	}

	@Test
	public void shouldNotMatchStringCaseInsensitiveThisQuotes(){
		StringSelectionPattern pattern = new StringSelectionPattern("ура \"миру мир\"");
		
		Assert.assertTrue(pattern.fit("все кричали Ура и Миру мир"));
		Assert.assertTrue(pattern.fit("все кричали ура и миру мира"));
		Assert.assertTrue(pattern.fit("ура и пальмиру мира"));
		Assert.assertTrue(pattern.fit("ура и пальмиру Мира"));
		Assert.assertFalse(pattern.fit("все кричали ура и миру всему мир"));
	}
	
	@Test
	public void shouldNotMatchStringCaseSensitiveThisQuotes(){
		StringSelectionPattern pattern = new StringSelectionPattern("ура \"миру мир\"", true);
		
		Assert.assertTrue(pattern.fit("все кричали ура и миру мир"));
		Assert.assertTrue(pattern.fit("все кричали ура и миру мира"));
		Assert.assertTrue(pattern.fit("ура и пальмиру мира"));
		Assert.assertFalse(pattern.fit("ура и пальмиру Мира"));
		Assert.assertFalse(pattern.fit("все кричали ура и миру всему мир"));
	}
	
}
