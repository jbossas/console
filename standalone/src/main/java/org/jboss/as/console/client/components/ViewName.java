package org.jboss.as.console.client.components;

/**
 * A simple class that ties a private name to a displayed title.  The title may very well change with locale but
 * the name will stay constant.  It's useful any time a viewable item does not itself provide for a name.
 *
 * @author Jay Shaughnessy
 */
public final class ViewName {

    private final String name;
    private final String title;

    public ViewName(String name) {
        this(name, null);
    }

    public ViewName(String name, String title) {
        super();
        this.name = name;
        this.title = (null == title || "".equals(title.trim())) ? name : title;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    /*
     * Return just the name so String construction of view path works as expected
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return name;
    }

}
