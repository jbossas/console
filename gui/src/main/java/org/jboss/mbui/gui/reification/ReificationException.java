package org.jboss.mbui.gui.reification;

/**
 * @author Harald Pehl
 * @date 02/22/2013
 */
public class ReificationException extends RuntimeException
{
    public ReificationException()
    {
        super();
    }

    public ReificationException(final String s)
    {
        super(s);
    }

    public ReificationException(final String s, final Throwable throwable)
    {
        super(s, throwable);
    }

    public ReificationException(final Throwable throwable)
    {
        super(throwable);
    }
}
