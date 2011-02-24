package org.jboss.as.console.client.core;

import com.allen_sauer.gwt.log.client.Log;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 2/17/11
 */
public class Places {

    public static List<PlaceRequest> fromString(String urlString)
    {
        List<PlaceRequest> places = null;
        try {
            places = new ArrayList<PlaceRequest>();

            StringTokenizer tokenizer = new StringTokenizer(urlString, "/");
            while(tokenizer.hasMoreTokens())
            {
                parseSingleToken(places, tokenizer.nextToken());
            }

        } catch (Throwable e) {
            Log.error("Error parsing token: " + urlString);
        }

        return places;
    }

    private static void parseSingleToken(List<PlaceRequest> places, String token) {

        if(token.contains(";")) // parametrized?
        {
            StringTokenizer params = new StringTokenizer(token, ";");
            PlaceRequest request = null;
            while(params.hasMoreTokens())
            {
                String tok = params.nextToken();
                if(tok.contains("="))
                {
                    if(null==request) break;

                    // parameter
                    String[] parameter = tok.split("=");
                    request = request.with(parameter[0], parameter[1]);
                }
                else
                {
                    // address
                    request = new PlaceRequest(tok);

                }
            }

            // exit, either wrong token or different formatter
            if(null==request)
                throw new IllegalArgumentException("Illegal token: "+token);

            places.add(request);
        }
        else
        {
            places.add(new PlaceRequest(token));
        }
    }
}
