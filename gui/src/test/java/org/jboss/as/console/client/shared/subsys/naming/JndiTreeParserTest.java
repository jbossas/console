package org.jboss.as.console.client.shared.subsys.naming;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

import org.jboss.dmr.client.ModelNode;
import org.junit.Test;

public class JndiTreeParserTest extends GWTTestCase {
    @Override
    public String getModuleName() {
        return "org.jboss.as.console.App";
    }

    @Test
    public void testJndiTreeParsing() {
        JndiTreeParser jtp = new JndiTreeParser();

        ModelNode children = new ModelNode();
        ModelNode inner = new ModelNode();
        inner.get("class-name").set("org.acme.MyClass");
        inner.get("value").set("org.acme.MyClass@xyz");
        children.get("SomeEJB").set(inner);

        ModelNode parent = new ModelNode();
        parent.get("class-name").set("javax.naming.Context");
        parent.get("children").set(children);

        ModelNode dep = new ModelNode();
        dep.get("my-ejb-1.0").set(parent);

        ModelNode root = new ModelNode();
        root.get("java:global").set(dep);

        ModelNode appData = new ModelNode();
        appData.get("class-name").set("java.lang.String");
        appData.get("value").set("my-app-1.0.0");

        ModelNode appName = new ModelNode();
        appName.get("AppName").set(appData);

        ModelNode javaApp = new ModelNode();
        javaApp.get("java:app").set(appName);

        ModelNode myApp = new ModelNode();
        myApp.get("my-app-1.0.jar").set(javaApp);
        root.get("applications").set(myApp);

        // System.out.println(root.toString());
        CellTree ct = jtp.parse(root.asPropertyList());
        JndiTreeParser.JndiTreeModel tm = (JndiTreeParser.JndiTreeModel) ct.getTreeViewModel();
        JndiEntry topEntry = tm.rootEntry;

        assertEquals("JNDI", topEntry.getName());
        assertEquals("", topEntry.getURI());
        assertEquals("", topEntry.getValue());
        assertEquals(2, topEntry.getChildren().size());

        JndiEntry rootEntry = topEntry.getChildren().get(0);
        assertEquals("java:global", rootEntry.getName());
        assertEquals("", rootEntry.getURI());
        assertEquals("", rootEntry.getValue());
        assertEquals(1, rootEntry.getChildren().size());

        JndiEntry depEntry = rootEntry.getChildren().get(0);
        assertEquals("my-ejb-1.0", depEntry.getName());
        assertEquals("java:global/my-ejb-1.0", depEntry.getURI());
        assertEquals("", depEntry.getValue());
        assertEquals(1, depEntry.getChildren().size());

        JndiEntry childEntry = depEntry.getChildren().get(0);
        assertEquals("SomeEJB", childEntry.getName());
        assertEquals("java:global/my-ejb-1.0/SomeEJB", childEntry.getURI());
        assertEquals("MyClass@xyz", childEntry.getValue());
        assertEquals(0, childEntry.getChildren().size());

        JndiEntry appsEntry = topEntry.getChildren().get(1);
        assertEquals("applications", appsEntry.getName());
        assertEquals("", appsEntry.getURI());
        assertEquals("", appsEntry.getValue());
        assertEquals(1, appsEntry.getChildren().size());

        JndiEntry myAppEntry = appsEntry.getChildren().get(0);
        assertEquals("my-app-1.0.jar", myAppEntry.getName());
        assertEquals("", myAppEntry.getURI());
        assertEquals("", myAppEntry.getValue());
        assertEquals(1, myAppEntry.getChildren().size());

        JndiEntry javaAppEntry = myAppEntry.getChildren().get(0);
        assertEquals("java:app", javaAppEntry.getName());
        assertEquals("", javaAppEntry.getURI());
        assertEquals("", javaAppEntry.getValue());
        assertEquals(1, javaAppEntry.getChildren().size());

        JndiEntry myAppData = javaAppEntry.getChildren().get(0);
        assertEquals("AppName", myAppData.getName());
        assertEquals("java:app/AppName", myAppData.getURI());
        assertEquals("my-app-1.0.0", myAppData.getValue());
        assertEquals(0, myAppData.getChildren().size());
    }

    @Test
    public void testSelectionModel() {
        JndiTreeParser jtp = new JndiTreeParser();

        SelectionModel<JndiEntry> sm = jtp.getSelectionModel();
        assertTrue(sm instanceof SingleSelectionModel);

        assertSame(sm, jtp.getSelectionModel());
    }
}
