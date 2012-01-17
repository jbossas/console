package org.jboss.as.console.client.core;

import com.google.gwt.http.client.URL;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.TokenFormatException;
import com.gwtplatform.mvp.client.proxy.TokenFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Formats tokens from {@code String} values to {@link PlaceRequest} and {@link PlaceRequest} hierarchies and vice-versa. The default implementation
 * uses:
 * <ul>
 * <li>{@code '/'} to separate {@link PlaceRequest}s in a hierarchy;</li>
 * <li>{@code ';'} to separate parameters in a {@link PlaceRequest};</li>
 * <li>{@code '='} to separate the parameter name from its value;</li>
 * <li>{@code '\'} to escape separators inside parameters names and values in a {@link PlaceRequest}.</li>
 * </ul>
 * These symbols cannot be used in a name token. If one of the separating symbol is encountered in a parameter or a value it is escaped using the
 * {@code '\'} character by replacing {@code '/'} with {@code '\0'}, {@code ';'} with {@code '\1'}, {@code '='} with {@code '\2'} and {@code '\'} with
 * {@code '\3'}.
 * <p />
 * Before decoding a {@link String} URL fragment into a {@link PlaceRequest} or a {@link PlaceRequest} hierarchy, {@link NewTokenFormatter} will first
 * pass the {@link String} through {@link URL#decodeQueryString(String)} so that if the URL was URL-encoded by some user agent, like a mail user
 * agent, it is still parsed correctly.
 * <p />
 * For example, {@link NewTokenFormatter} would parse any of the following:
 * 
 * <pre>
 * nameToken1%3Bparam1.1%3Dvalue1.1%3Bparam1.2%3Dvalue1.2%2FnameToken2%2FnameToken3%3Bparam3.1%3Dvalue%03%11
 * nameToken1;param1.1=value1.1;param1.2=value1.2/nameToken2/nameToken3;param3.1=value\03\21
 * </pre>
 * 
 * Into the following hierarchy of {@link PlaceRequest}:
 * 
 * <pre>
 * {
 *   { "nameToken1", { {"param1.1", "value1.1"}, {"parame1.2","value1.2"} },
 *     "nameToken2", {},
 *     "nameToken3", { {"param3.1", "value/3=1"} } }
 * }
 * </pre>
 * 
 * If you want to use different symbols as separator, use the {@link #ParameterTokenFormatter(String, String, String, String)} constructor.
 * 
 * @author Philippe Beaudoin
 * @author Yannis Gonianakis
 * @author Daniel Colchete
 */
public class NewTokenFormatter implements TokenFormatter {

	protected static final String DEFAULT_HIERARCHY_SEPARATOR = "/";
	protected static final String DEFAULT_PARAM_SEPARATOR = ";";
	protected static final String DEFAULT_VALUE_SEPARATOR = "=";
	protected static final String DEFAULT_ESCAPE_CHARACTER = "\\";

	protected static final String ESCAPED_HIERARCHY_SEPARATOR = "\\0"; // escaped version for the DEFAULT_HIERARCHY_SEPARATOR
	protected static final String ESCAPED_PARAM_SEPARATOR = "\\1"; // escaped version for the DEFAULT_PARAM_SEPARATOR
	protected static final String ESCAPED_VALUE_SEPARATOR = "\\2"; // escaped version for the DEFAULT_VALUE_SEPARATOR
	protected static final String ESCAPED_ESCAPE_CHAR = "\\3"; // escaped version for the DEFAULT_ESCAPE_CHARACTER

	private final String escapeCharacter;
	private final String hierarchySeparator;
	private final String paramSeparator;
	private final String valueSeparator;

	/**
	 * Builds a {@link NewTokenFormatter} using the default separators and escape character.
	 */
	@Inject
	public NewTokenFormatter() {
		this(DEFAULT_HIERARCHY_SEPARATOR, DEFAULT_PARAM_SEPARATOR, DEFAULT_VALUE_SEPARATOR, DEFAULT_ESCAPE_CHARACTER);
	}

	/**
	 * This constructor makes it possible to use custom separators in your token formatter. The separators must be 1-letter strings, they must all be
	 * different from one another, and they must be encoded when ran through {@link URL#encodeQueryString(String)})
	 * 
	 * @param hierarchySeparator
	 *            The symbol used to separate {@link PlaceRequest} in a hierarchy. Must be a 1-character string and can't be {@code %}.
	 * @param paramSeparator
	 *            The symbol used to separate parameters in a {@link PlaceRequest}. Must be a 1-character string and can't be {@code %}.
	 * @param valueSeparator
	 *            The symbol used to separate the parameter name from its value. Must be a 1-character string and can't be {@code %}.
	 */
	public NewTokenFormatter(String hierarchySeparator, String paramSeparator, String valueSeparator) {
		this(hierarchySeparator, paramSeparator, valueSeparator, DEFAULT_ESCAPE_CHARACTER);
	}

	/**
	 * This constructor makes it possible to use custom separators and custom escape character in your token formatter. The separators and the escape
	 * character must be 1-letter strings, they must all be different from one another, and they must be encoded when ran through
	 * {@link URL#encodeQueryString(String)})
	 * 
	 * @param hierarchySeparator
	 *            The symbol used to separate {@link PlaceRequest} in a hierarchy. Must be a 1-character string and can't be {@code %}.
	 * @param paramSeparator
	 *            The symbol used to separate parameters in a {@link PlaceRequest}. Must be a 1-character string and can't be {@code %}.
	 * @param valueSeparator
	 *            The symbol used to separate the parameter name from its value. Must be a 1-character string and can't be {@code %}.
	 * @param escapeCharacter
	 *            The symbol used to escape the separator symbols inside parameter names and values. Must be a 1-character string and can't be
	 *            {@code %}.
	 */
	public NewTokenFormatter(String hierarchySeparator, String paramSeparator, String valueSeparator, String escapeCharacter) {
		assert hierarchySeparator.length() == 1;
		assert paramSeparator.length() == 1;
		assert valueSeparator.length() == 1;
		assert escapeCharacter.length() == 1;
		assert !escapeCharacter.equals(hierarchySeparator);
		assert !escapeCharacter.equals(paramSeparator);
		assert !escapeCharacter.equals(valueSeparator);
		assert !hierarchySeparator.equals(paramSeparator);
		assert !hierarchySeparator.equals(valueSeparator);
		assert !paramSeparator.equals(valueSeparator);
		assert !escapeCharacter.equals(URL.encodeQueryString(escapeCharacter));
		assert !valueSeparator.equals(URL.encodeQueryString(valueSeparator));
		assert !hierarchySeparator.equals(URL.encodeQueryString(hierarchySeparator));
		assert !paramSeparator.equals(URL.encodeQueryString(paramSeparator));
		assert !escapeCharacter.equals("%");
		assert !hierarchySeparator.equals("%");
		assert !paramSeparator.equals("%");
		assert !valueSeparator.equals("%");

		this.hierarchySeparator = hierarchySeparator;
		this.paramSeparator = paramSeparator;
		this.valueSeparator = valueSeparator;
		this.escapeCharacter = escapeCharacter;
	}

	@Override
	public String toHistoryToken(List<PlaceRequest> placeRequestHierarchy) throws TokenFormatException {
		StringBuilder out = new StringBuilder();

		for (int i = 0; i < placeRequestHierarchy.size(); ++i) {
			if (i != 0) {
				out.append(hierarchySeparator);
			}
			out.append(toPlaceTokenUnescaped(placeRequestHierarchy.get(i)));
		}

		return out.toString();
	}

	@Override
	public PlaceRequest toPlaceRequest(String placeToken) throws TokenFormatException {
		return unescapedToPlaceRequest(URL.decodeQueryString(placeToken));
	}

	private PlaceRequest unescapedToPlaceRequest(String unescapedPlaceToken) throws TokenFormatException {
		PlaceRequest req = null;

		int split = unescapedPlaceToken.indexOf(paramSeparator);
		if (split == 0) {
			throw new TokenFormatException("Place history token is missing.");
		} else if (split == -1) {
			req = new PlaceRequest(unescapedPlaceToken);
		} else if (split >= 0) {
			req = new PlaceRequest(unescapedPlaceToken.substring(0, split));
			String paramsChunk = unescapedPlaceToken.substring(split + 1);
			String[] paramTokens = paramsChunk.split(paramSeparator);
			for (String paramToken : paramTokens) {
				if (paramToken.isEmpty()) {
					throw new TokenFormatException("Bad parameter: Successive parameters require a single '" + paramSeparator + "' between them.");
				}
				String[] param = splitParamToken(paramToken);
				String key = paramValueUnescape(param[0]);
				String value = paramValueUnescape(param[1]);
				req = req.with(key, value);
			}
		}
		return req;
	}

	@Override
	public List<PlaceRequest> toPlaceRequestHierarchy(String historyToken) throws TokenFormatException {
		historyToken = URL.decodeQueryString(historyToken);

		int split = historyToken.indexOf(hierarchySeparator);
		if (split == 0) {
			throw new TokenFormatException("Place history token is missing.");
		} else {
			List<PlaceRequest> result = new ArrayList<PlaceRequest>();
			if (split == -1) {
				result.add(unescapedToPlaceRequest(historyToken)); // History token consists of a single place token
			} else {
				String[] placeTokens = historyToken.split(hierarchySeparator);
				for (String placeToken : placeTokens) {
					if (placeToken.isEmpty()) {
						throw new TokenFormatException("Bad parameter: Successive place tokens require a single '" + hierarchySeparator + "' between them.");
					}
					result.add(unescapedToPlaceRequest(placeToken));
				}
			}
			return result;
		}
	}
	
	@Override
	public String toPlaceToken(PlaceRequest placeRequest) throws TokenFormatException {
		return toPlaceTokenUnescaped(placeRequest);
	}

	private String toPlaceTokenUnescaped(PlaceRequest placeRequest) throws TokenFormatException {
		StringBuilder out = new StringBuilder();
		out.append(placeRequest.getNameToken());

		Set<String> params = placeRequest.getParameterNames();
		if (params != null) {
			for (String name : params) {
				out.append(paramSeparator).append(paramValueEscape(name)).append(valueSeparator).append(paramValueEscape(placeRequest.getParameter(name, null)));
			}
		}
		
		return out.toString();
	}

	private String[] splitParamToken(String paramToken) {
		String[] param = paramToken.split(valueSeparator, 2);
		if (param.length == 1 // pattern didn't match
				|| param[0].contains(valueSeparator) // un-escaped separator encountered in the key
				|| param[1].contains(valueSeparator)) { // un-escaped separator encountered in the value
			throw new TokenFormatException("Bad parameter: Parameters require a single '" + valueSeparator + "' between the key and value.");
		}
		return param;
	}

	private String paramValueEscape(String value) {

		StringBuffer sbuf = new StringBuffer();
		int len = value.length();

		char escapeChar = escapeCharacter.charAt(0);
		char hierarchyChar = hierarchySeparator.charAt(0);
		char paramChar = paramSeparator.charAt(0);
		char valueChar = valueSeparator.charAt(0);

		for (int i = 0; i < len; i++) {
			char ch = value.charAt(i);

			if (ch == escapeChar) {
				sbuf.append(ESCAPED_ESCAPE_CHAR);
			} else if (ch == hierarchyChar) {
				sbuf.append(ESCAPED_HIERARCHY_SEPARATOR);
			} else if (ch == paramChar) {
				sbuf.append(ESCAPED_PARAM_SEPARATOR);
			} else if (ch == valueChar) {
				sbuf.append(ESCAPED_VALUE_SEPARATOR);
			} else {
				sbuf.append(ch);
			}
		}

		return URL.encodeQueryString(sbuf.toString());
	}

	private String paramValueUnescape(String value) {
		value = URL.decodeQueryString(value);
		
		StringBuffer sbuf = new StringBuffer();
		int len = value.length();

		char escapeChar = escapeCharacter.charAt(0);

		char hierarchyNum = ESCAPED_HIERARCHY_SEPARATOR.charAt(1);
		char paramNum = ESCAPED_PARAM_SEPARATOR.charAt(1);
		char valueNum = ESCAPED_VALUE_SEPARATOR.charAt(1);
		char escapeNum = ESCAPED_ESCAPE_CHAR.charAt(1);

		for (int i = 0; i < len; i++) {
			char ch = value.charAt(i);

			if (ch == escapeChar) {
				i++;
				char ch2 = value.charAt(i);
				if (ch2 == hierarchyNum) {
					sbuf.append(hierarchySeparator);
				} else if (ch2 == paramNum) {
					sbuf.append(paramSeparator);
				} else if (ch2 == valueNum) {
					sbuf.append(valueSeparator);
				} else if (ch2 == escapeNum) {
					sbuf.append('\\');
				}
			} else {
				sbuf.append(ch);
			}
		} 	

		return sbuf.toString();
	}

}
