package com.gys.util;

import java.util.Collection;
import java.util.Map;

/**
 * 校验工具类
 */
public class ValidateUtil {

    /** digit characters */
    public static final String digits = "0123456789";


    /** lower-case letter characters */
    public static final String lowercaseLetters = "abcdefghijklmnopqrstuvwxyz";

    /** upper-case letter characters */
    public static final String uppercaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /** letter characters */
    public static final String letters = lowercaseLetters + uppercaseLetters;

    /** decimal point character differs by language and culture */
    public static final String decimalPointDelimiter = ".";

    /** non-digit characters which are allowed in phone numbers */
    public static final String phoneNumberDelimiters = "()- ";

    /** characters which are allowed in US phone numbers */
    public static final String validUSPhoneChars = digits + phoneNumberDelimiters;

    /** characters which are allowed in international phone numbers(a leading + is OK) */
    public static final String validWorldPhoneChars = digits + phoneNumberDelimiters + "+";

    /** non-digit characters which are allowed in Social Security Numbers */
    public static final String SSNDelimiters = "- ";

    /** characters which are allowed in Social Security Numbers */
    public static final String validSSNChars = digits + SSNDelimiters;

    /** U.S. Social Security Numbers have 9 digits. They are formatted as 123-45-6789. */
    public static final int digitsInSocialSecurityNumber = 9;

    /** U.S. phone numbers have 10 digits. They are formatted as 123 456 7890 or(123) 456-7890. */
    public static final int digitsInUSPhoneNumber = 10;
    public static final int digitsInUSPhoneAreaCode = 3;
    public static final int digitsInUSPhoneMainNumber = 7;

    /** non-digit characters which are allowed in ZIP Codes */
    public static final String ZipCodeDelimiters = "-";

    /** our preferred delimiter for reformatting ZIP Codes */
    public static final String ZipCodeDelimeter = "-";

    /** characters which are allowed in Social Security Numbers */
    public static final String validZipCodeChars = digits + ZipCodeDelimiters;

    /** U.S. ZIP codes have 5 or 9 digits. They are formatted as 12345 or 12345-6789. */
    public static final int digitsInZipCode1 = 5;

    /** U.S. ZIP codes have 5 or 9 digits. They are formatted as 12345 or 12345-6789. */
    public static final int digitsInZipCode2 = 9;

    /** non-digit characters which are allowed in credit card numbers */
    public static final String creditCardDelimiters = " -";

    /** An array of ints representing the number of days in each month of the year.
     *  Note: February varies depending on the year */
    public static final int[] daysInMonth = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    /** Delimiter for USStateCodes String */
    public static final String USStateCodeDelimiter = "|";

    /** Valid U.S. Postal Codes for states, territories, armed forces, etc.
     * See http://www.usps.gov/ncsc/lookups/abbr_state.txt. */
    public static final String USStateCodes = "AL|AK|AS|AZ|AR|CA|CO|CT|DE|DC|FM|FL|GA|GU|HI|ID|IL|IN|IA|KS|KY|LA|ME|MH|MD|MA|MI|MN|MS|MO|MT|NE|NV|NH|NJ|NM|NY|NC|ND|MP|OH|OK|OR|PW|PA|PR|RI|SC|SD|TN|TX|UT|VT|VI|VA|WA|WV|WI|WY|AE|AA|AE|AE|AP";

    /** Valid contiguous U.S. postal codes */
    public static final String ContiguousUSStateCodes = "AL|AZ|AR|CA|CO|CT|DE|DC|FL|GA|ID|IL|IN|IA|KS|KY|LA|ME|MD|MA|MI|MN|MS|MO|MT|NE|NV|NH|NJ|NM|NY|NC|ND|OH|OK|OR|PA|RI|SC|SD|TN|TX|UT|VT|VA|WA|WV|WI|WY";

    public static boolean areEqual(Object obj, Object obj2) {
        if (obj == null) {
            return obj2 == null;
        } else {
            return obj.equals(obj2);
        }
    }

    /** Check whether string s is empty. */
    public static boolean isEmpty(String s) {
        return (s == null) || s.length() == 0;
    }

    /** Check whether collection c is empty. */
    public static <E> boolean isEmpty(Collection<E> c) {
        return (c == null) || c.isEmpty();
    }

    /** Check whether map m is empty. */
    public static <K,E> boolean isEmpty(Map<K,E> m) {
        return (m == null) || m.isEmpty();
    }

    /** Check whether charsequence c is empty. */
    public static <E> boolean isEmpty(CharSequence c) {
        return (c == null) || c.length() == 0;
    }

    /** Check whether string s is NOT empty. */
    public static boolean isNotEmpty(String s) {
        return (s != null) && s.length() > 0;
    }

    /** Check whether collection c is NOT empty. */
    public static <E> boolean isNotEmpty(Collection<E> c) {
        return (c != null) && !c.isEmpty();
    }

    /** Check whether charsequence c is NOT empty. */
    public static <E> boolean isNotEmpty(CharSequence c) {
        return ((c != null) && (c.length() > 0));
    }

    public static boolean isString(Object obj) {
        return ((obj != null) && (obj instanceof String));
    }

    @SuppressWarnings("unchecked")
    public static boolean isEmpty(Object value) {
        if (value == null) return true;
        if (value instanceof String) return ((String) value).length() == 0;
        if (value instanceof Collection) return ((Collection<? extends Object>) value).size() == 0;
        if (value instanceof Map) return ((Map<? extends Object, ? extends Object>) value).size() == 0;
        if (value instanceof CharSequence) return ((CharSequence) value).length() == 0;
        if (value instanceof Boolean) return false;
        if (value instanceof Number) return false;
        if (value instanceof Character) return false;
        if (value instanceof java.util.Date) return false;
        return false;
    }

    public static boolean isNotEmpty(Object value) {
        return !isEmpty(value);
    }
}
