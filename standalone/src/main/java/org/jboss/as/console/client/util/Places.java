package org.jboss.as.console.client.util;

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
        List<PlaceRequest> places = new ArrayList<PlaceRequest>();

        if(urlString.contains("/"))
        {
            String token = urlString.split("/")[1];
            if(token.contains(";"))
            {
                String[] split = token.split(";");
                String place = split[0];
                String params[] = split[1].split("=");

                places.add(new PlaceRequest(place).with(params[0], params[1]));
            }
            else
            {
                places.add(new PlaceRequest(token));
            }
        }
        return places;
    }
}
