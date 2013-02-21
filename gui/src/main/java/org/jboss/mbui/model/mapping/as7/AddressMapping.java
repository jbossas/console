package org.jboss.mbui.model.mapping.as7;

import com.allen_sauer.gwt.log.client.Log;
import org.jboss.dmr.client.ModelNode;
import org.jboss.mbui.gui.behaviour.StatementContext;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.jboss.dmr.client.ModelDescriptionConstants.ADDRESS;

/**
 * Address mapping of domain references used within the interface model.
 * Typically as part of a {@link ResourceMapping}.
 * <p/>
 * The mapping currently supports three different types of address value declarations:
 *
 * <ul>
 *     <li>token: key=value</li>
 *     <li>value expression: some.key={value.ref}</li>
 *     <li>token expression: {some.tuple}</li>
 * </ul>
 *
 * A token is a fully qualified address tuple without any parameters, i.e. "subsystem=default".<br/>
 * A value expression carries a parameter for one part of the tuple, i.e. "subsystem={name}".<br/>
 * A token expression references a full tuple, with both the key and the value part, i.e "{selected.profile}/subsystem=datasources".<p/>
 *
 * All expression are resolved against the {@link StatementContext}.
 *
 * @author Heiko Braun
 * @date 9/23/11
 */
public class AddressMapping {

    private List<Token> address = new LinkedList<Token>();
    private int countedWildcards = -1;

    public AddressMapping(List<Token> tuple) {
        this.address = tuple;
    }

    public void add(String parent, String child)
    {
        address.add(new Token(parent, child));
    }

    public ModelNode asResource(StatementContext context) {
        return asResource(new ModelNode(), context);
    }

    public ModelNode asResource(ModelNode baseAddress, StatementContext context) {

        ModelNode model = new ModelNode();
        model.get(ADDRESS).set(baseAddress);

        for(Token token: address)
        {

            // TODO: the parsing of the token structure should not be done repeatedly
            // Make it part of the initialisation

            if(!token.hasKey())
            {
                // a single token or token expression
                // without brackets it needs to be a valid token
                String token_ref = token.getValue();
                String[] resolved_value = null;

                if(token_ref.startsWith("{"))
                {
                    token_ref = token_ref.substring(1, token_ref.length()-1);
                    resolved_value = context.resolveTuple(token_ref);
                }
                else
                {
                    assert token_ref.contains("=") : "Invalid token expression "+token_ref;
                    resolved_value = token_ref.split("=");
                }

                // TODO: is it safe to suppress token expressions that cannot be resolved?
                // i.e /{selected.profile}/subsystem=foobar/ on a standalone server?

                if(null==resolved_value)
                {
                    Log.warn("The token expression '"+token_ref+"' cannot be resolved");
                    //assert resolved_value!=null : "The token expression '"+token_ref+"' cannot be resolved";
                }
                else
                {
                    model.get(ADDRESS).add(resolved_value[0], resolved_value[1]);
                }

            }
            else
            {
                // a value expression. key and value of the expression might be resolved
                String key_ref = token.getKey();
                String value_ref = token.getValue();

                String resolved_key = null;
                String resolved_value = null;

                if(key_ref.startsWith("{"))
                {
                    key_ref = key_ref.substring(1, key_ref.length()-1);
                    resolved_key = context.resolve(key_ref);
                }
                else
                {
                    resolved_key = key_ref;
                }

                if(value_ref.startsWith("{"))
                {
                    value_ref = value_ref.substring(1, value_ref.length()-1);
                    resolved_value = context.resolve(value_ref);
                }
                else
                {
                    resolved_value = value_ref;
                }

                assert resolved_key!=null : "The key '"+key_ref+"' cannot be resolved";
                assert resolved_value!=null : "The value '"+value_ref+"' cannot be resolved";

                model.get(ADDRESS).add(resolved_key, resolved_value);
            }

        }

        return model;
    }

    public static List<Token> parseAddressString(String value) {
        List<Token> address = new LinkedList<Token>();

        if(value.equals("/")) // default parent value
            return address;

        StringTokenizer tok = new StringTokenizer(value, "/");
        while(tok.hasMoreTokens())
        {
            String nextToken = tok.nextToken();
            if(nextToken.contains("="))
            {
                String[] split = nextToken.split("=");
                address.add(new Token(split[0], split[1]));
            }
            else
            {
                address.add(new Token(nextToken));
            }

        }
        return address;
    }

    public static class Token {
        String key;
        String value;

        Token(String key, String value) {
            this.key = key;
            this.value = value;
        }

        Token(String value) {
            this.key = null;
            this.value = value;
        }

        boolean hasKey() {
            return key!=null;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            if(hasKey())
                return key+"="+value;
            else
                return value;
        }
    }

    public static AddressMapping fromString(String address) {
        return new AddressMapping(AddressMapping.parseAddressString(address));
    }

    public static class StringTokenizer {
        private final String deli;
        private final String s;
        private final int len;

        private int pos;
        private String next;

        public StringTokenizer(String s, String deli) {
            this.s = s;
            this.deli = deli;
            len = s.length();
        }

        public StringTokenizer(String s) {
            this(s, " \t\n\r\f");

        }

        public String nextToken() {
            if(!hasMoreTokens()) {
                throw new NoSuchElementException();
            }
            String result = next;
            next = null;
            return result;
        }

        public boolean hasMoreTokens() {
            if (next != null) {
                return true;
            }
            // skip leading delimiters
            while (pos < len && deli.indexOf(s.charAt(pos)) != -1) {
                pos++;
            }

            if (pos >= len) {
                return false;
            }

            int p0 = pos++;
            while (pos < len && deli.indexOf(s.charAt(pos)) == -1) {
                pos++;
            }

            next = s.substring(p0, pos++);
            return true;
        }

    }
}

