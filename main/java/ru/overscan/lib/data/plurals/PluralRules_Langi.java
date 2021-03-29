package ru.overscan.lib.data.plurals;
/**
 * Plural rules for Langi language:
 *
 * Locales: lag
 *
 * Languages:
 * - Langi (lag)
 *
 * Rules:
 * 	zero → n is 0;
 * 	one → n within 0..2 and n is not 0 and n is not 2;
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
public class PluralRules_Langi extends PluralRules
{
	public int quantityForNumber(int count)
	{
		if (count == 0)
		{
			return QUANTITY_ZERO;
		}
		else if (count > 0 && count < 2)
		{
			return QUANTITY_ONE;
        }
		else
		{
			return QUANTITY_OTHER;
        }
	}
}