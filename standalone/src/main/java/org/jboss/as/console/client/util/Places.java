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

        // TODO: Tis is currently limited to a a parent/child hierarchy with depth 1
        if(urlString.contains("/"))
        {
            String[] parentChild = urlString.split("/");

            String parent = parentChild[0];
            places.add(new PlaceRequest(parent));

            String child = parentChild[1];
            if(child.contains(";"))
            {
                String[] split = child.split(";");
                String childPlace = split[0];
                String params[] = split[1].split("=");

                places.add(new PlaceRequest(childPlace).with(params[0], params[1]));
            }
            else
            {
                places.add(new PlaceRequest(child));
            }
        }

        return places;
    }
}
