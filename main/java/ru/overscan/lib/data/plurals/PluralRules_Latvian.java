package ru.overscan.lib.data.plurals;
/**
 * Plural rules for Latvian language:
 * 
 * Locales: lv
 *
 * Languages:
 * - Latvian (lv)
 *
 * Rules:
 * 	zero → n is 0;
 * 	one → n mod 10 is 1 and n mod 100 is not 11;
 * 	other → everything else
 *
 * Reference CLDR Version 1.9 beta (2010-11-16 21:48:45 GMT)
 * @see http://unicode.org/repos/cldr-tmp/trunk/diff/supplemental/language_plural_rules.html
 * @see http://unicode.org/repos/cldr/trunk/common/supplemental/plurals.xml
 * @see plurals.xml (local copy)
 *
 * @package    I18n_Plural
 * @category   Plural Rules
 * @author     Korney Czukowski
 * @copyright  (c) 2011 Korney Czukowski
 * @license    MIT License
 */

/**
 * Converted to Java by Sam Marshak, 2012 
 */
public class PluralRules_Latvian extends PluralRules
{
	public int quantityForNumber(int count)
	{
		if (count == 0)
		{
			return QUANTITY_ZERO;
		}
		else if (count % 10 == 1 && count % 100 != 11)
		{
			return QUANTITY_ONE;
		}
		else
		{
			return QUANTITY_OTHER;
		}
	}
}