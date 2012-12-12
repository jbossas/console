package org.jboss.as.console.client.shared.runtime.ext;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.domain.topology.TopologyPresenter;
import org.jboss.as.console.client.shared.viewframework.builder.MultipleToOneLayout;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tables.DefaultPager;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;

import java.util.List;

/**
 * Created with IntelliJ IDEA. User: pehlh Date: 15.10.12 Time: 17:04 To change this template use File | Settings | File
 * Templates.
 */
public class ExtensionView
{
    private DefaultCellTable<Extension> extensionTable;
    private ListDataProvider<Extension> dataProvider;
    private Form<Extension> form;
    private TopologyPresenter presenter;

    public Widget asWidget()
    {
        extensionTable = new DefaultCellTable<Extension>(8, new ProvidesKey<Extension>() {
            @Override
            public Object getKey(Extension extension) {
                return extension.getName();
            }
        });
        extensionTable.addColumn(new Column<Extension, String>(new TextCell())
        {
            @Override
            public String getValue(Extension ext)
            {
                return ext.getName();
            }
        }, "Name");

        extensionTable.addColumn(new Column<Extension, String>(new TextCell())
        {
            @Override
            public String getValue(Extension ext)
            {
                return ext.getVersion();
            }
        },"Version");

        dataProvider = new ListDataProvider<Extension>();
        dataProvider.addDataDisplay(extensionTable);

        DefaultPager pager = new DefaultPager();
        pager.setDisplay(extensionTable);


        // -----------------

        form = new Form<Extension>(Extension.class);
        form.setNumColumns(2);
        form.setEnabled(false);

        TextItem name = new TextItem("name", "Name");
        TextItem version = new TextItem("version", "Version");
        TextItem module = new TextItem("module", "Module");
        TextItem subsystem = new TextItem("subsystem", "Subsystem");

        form.setFields(name, version, module, subsystem);

        form.bind(extensionTable);

        MultipleToOneLayout layout = new MultipleToOneLayout()
                .setTitle("Extensions")
                .setPlain(true)
                .setHeadline("Subsystem Extensions")
                .setDescription("The list of installed extensions. Each extension reflects a subsystem.")
                .setMaster(Console.MESSAGES.available("Extensions"), extensionTable)
                .addDetail(Console.CONSTANTS.common_label_attributes(), form.asWidget());

        if(!GWT.isScript())
        {
            // debug util only available in hosted mode
            ToolStrip tools = new ToolStrip();
            tools.addToolButtonRight(new ToolButton("Versions", new ClickHandler() {
                @Override
                public void onClick(ClickEvent clickEvent) {
                    presenter.onDumpVersions();
                }
            }));

            layout.setMasterTools(tools);
        }

        return layout.build();
    }

    public void setExtensions(final List<Extension> extensions)
    {
        dataProvider.setList(extensions);
        extensionTable.selectDefaultEntity();
    }

    public void setPresenter(TopologyPresenter presenter) {
        this.presenter = presenter;
    }

    public TopologyPresenter getPresenter() {
        return presenter;
    }
}
