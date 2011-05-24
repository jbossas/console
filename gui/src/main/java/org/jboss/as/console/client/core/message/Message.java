/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.jboss.as.console.client.core.message;

import java.util.Date;
import java.util.EnumSet;

/**
 * A message to be displayed to the user in one or more places.
 *
 * @author Greg Hinkle
 * @author Ian Springer
 */
public class Message {

    protected String conciseMessage;
    protected String detailedMessage;
    protected Date fired = new Date();
    protected Severity severity;
    protected EnumSet<Option> options;

    // TODO: Add Debug severity?
    public enum Severity {
        Blank("InfoBlank", "info/icn_info_blank.png"), //
        Info("InfoBlock", "info/icn_info_blue.png"), //
        Warning("WarnBlock", "info/icn_info_orange.png"), //
        Error("ErrorBlock", "info/icn_info_red.png"), //
        Fatal("FatalBlock", "info/icn_info_red.png");

        private String style;
        private String icon;

        private Severity(String style, String icon) {
            this.style = style;
            this.icon = icon;
        }

        public String getStyle() {
            return style;
        }

        public String getIcon() {
            return icon;
        }
    };

    public enum Option {
        /**
         * The message will not be persisted in the message center list.
         */
        Transient,

        /**
         * The message will not auto-clear after a delay - it remains on the screen until you navigate away.
         */
        Sticky,

        /**
         * The message will be persisted in the message center list,
         * but will not show up in the main screen message area.
         */
        BackgroundJobResult
    };

    public Message(String conciseMessage) {
        this(conciseMessage, (Severity) null);
    }

    public Message(String conciseMessage, Severity severity) {
        this(conciseMessage, (String) null, severity);
    }

    public Message(String conciseMessage, String detailedMessage) {
        this(conciseMessage, detailedMessage, null);
    }

    public Message(String conciseMessage, Throwable details) {
        this(conciseMessage, details, null);
    }

    public Message(String conciseMessage, EnumSet<Option> options) {
        this(conciseMessage, null, options);
    }

    public Message(String conciseMessage, String detailedMessage, Severity severity) {
        this(conciseMessage, detailedMessage, severity, null);
    }

    public Message(String conciseMessage, Throwable details, Severity severity) {
        this(conciseMessage, details, severity, null);
    }

    public Message(String conciseMessage, Severity severity, EnumSet<Option> options) {
        this(conciseMessage, (String) null, severity, options);
    }

    public Message(String conciseMessage, Throwable details, Severity severity, EnumSet<Option> options) {
        this(conciseMessage, (String)null, severity, options); // ErrorHandler.getAllMessages(details)
    }

    public Message(String conciseMessage, String detailedMessage, Severity severity, EnumSet<Option> options) {
        this.conciseMessage = conciseMessage;
        this.detailedMessage = detailedMessage;
        this.severity = (severity != null) ? severity : Severity.Info;
        this.options = (options != null) ? options : EnumSet.noneOf(Option.class);
    }

    public String getConciseMessage() {
        return conciseMessage;
    }

    public String getDetailedMessage() {
        return detailedMessage;
    }

    public Date getFired() {
        return fired;
    }

    public Severity getSeverity() {
        return severity;
    }

    public boolean isTransient() {
        return options.contains(Option.Transient);
    }

    public boolean isSticky() {
        return options.contains(Option.Sticky);
    }

    public boolean isBackgroundJobResult() {
        return options.contains(Option.BackgroundJobResult);
    }

    @Override
    public String toString() {
        return "Message{" //
            + "conciseMessage='" + this.conciseMessage + '\'' //
            + ", detailedMessage='" + this.detailedMessage + '\'' //
            + ", fired=" + this.fired //
            + ", severity=" + this.severity //
            + ", options=" + this.options + '}';
    }
}
