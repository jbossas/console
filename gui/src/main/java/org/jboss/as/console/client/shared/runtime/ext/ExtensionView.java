package org.jboss.as.console.client.shared.runtime.ext;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.layout.MultipleToOneLayout;
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
    private ExtensionManager presenter;

    public Widget asWidget()
    {
        extensionTable = new DefaultCellTable<Extension>(8, new ProvidesKey<Extension>() {
            @Override
            public Object getKey(Extension extension) {
                return extension.getName();
            }
        });
        Column<Extension, String> nameCol = new Column<Extension, String>(new TextCell()) {
            @Override
            public String getValue(Extension ext) {
                return ext.getSubsystem();
            }
        };

        Column<Extension, String> versionCol = new Column<Extension, String>(new TextCell()) {
            @Override
            public String getValue(Extension ext) {
                return ext.getVersion();
            }
        };

        extensionTable.addColumn(nameCol, "Name");
        extensionTable.addColumn(versionCol,"Version");

        extensionTable.addColumn(new Column<Extension, SafeHtml>(new SafeHtmlCell())
        {
            @Override
            public SafeHtml getValue(Extension ext)
            {
                SafeHtmlBuilder html = new SafeHtmlBuilder();

                if(!ext.getCompatibleVersion().equals(ext.getVersion()))
                    html.appendHtmlConstant("<i class='icon-bolt'></i>");

                return html.toSafeHtml();
            }
        },"");

        extensionTable.setColumnWidth(nameCol, 50, Style.Unit.PCT);
        extensionTable.setColumnWidth(versionCol, 40, Style.Unit.PCT);

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
        TextItem compat = new TextItem("compatibleVersion", "Compatible Version");

        form.setFields(name, version, module, subsystem, compat);

        form.bind(extensionTable);

        MultipleToOneLayout layout = new MultipleToOneLayout()
                .setTitle("Extensions")
                .setPlain(true)
                .setHeadline("Subsystem Extensions")
                .setDescription(Console.MESSAGES.extensions_description())
                .setMaster(Console.MESSAGES.available("Extensions"), extensionTable)
                .addDetail("Attributes", form.asWidget());


        // debug util only available in hosted mode
        ToolStrip tools = new ToolStrip();
        tools.addToolButtonRight(new ToolButton(Console.CONSTANTS.common_label_export(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                presenter.onDumpVersions();
            }
        }));

        layout.setMasterTools(tools);


        return layout.build();
    }

    public void setExtensions(final List<Extension> extensions)
    {
        dataProvider.setList(extensions);
        extensionTable.selectDefaultEntity();
    }

    public void setPresenter(ExtensionManager presenter) {
        this.presenter = presenter;
    }
}
