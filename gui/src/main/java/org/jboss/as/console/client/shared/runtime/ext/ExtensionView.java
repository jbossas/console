package org.jboss.as.console.client.shared.runtime.ext;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.shared.viewframework.builder.SimpleLayout;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tables.DefaultPager;

import java.util.List;

/**
 * Created with IntelliJ IDEA. User: pehlh Date: 15.10.12 Time: 17:04 To change this template use File | Settings | File
 * Templates.
 */
public class ExtensionView extends SuspendableViewImpl implements ExtensionPresenter.MyView
{
    private ExtensionPresenter presenter;
    private DefaultCellTable<String> extensionTable;
    private ListDataProvider<String> dataProvider;

    @Override
    public Widget createWidget()
    {
        extensionTable = new DefaultCellTable<String>(8);
        extensionTable.addColumn(new Column<String, String>(new TextCell())
        {
            @Override
            public String getValue(String object)
            {
                return object;
            }
        }, "Name");

        dataProvider = new ListDataProvider<String>();
        dataProvider.addDataDisplay(extensionTable);

        DefaultPager pager = new DefaultPager();
        pager.setDisplay(extensionTable);

        VerticalPanel extPanel = new VerticalPanel();
        extPanel.setStyleName("fill-layout-width");
        extPanel.getElement().setAttribute("style", "padding-top:15px;");
        extPanel.add(extensionTable.asWidget());
        extPanel.add(pager);

        SimpleLayout layout = new SimpleLayout()
                .setTitle("Extension")
                .setHeadline("Extension Properties")
                .setDescription("The list of installed extensions.")
                .addContent("Extensions", extPanel);
        return layout.build();
    }

    @Override
    public void setPresenter(final ExtensionPresenter presenter)
    {
        this.presenter = presenter;
    }

    @Override
    public void setExtensions(final List<String> extensions)
    {
        dataProvider.setList(extensions);
    }
}
