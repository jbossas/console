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
package org.jboss.as.console.client.shared.subsys.osgi.runtime;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.Handler;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.runtime.RuntimeBaseAddress;
import org.jboss.as.console.client.shared.subsys.osgi.runtime.model.OSGiBundle;
import org.jboss.as.console.client.shared.viewframework.AbstractEntityView;
import org.jboss.as.console.client.shared.viewframework.EntityToDmrBridge;
import org.jboss.as.console.client.shared.viewframework.EntityToDmrBridgeImpl;
import org.jboss.as.console.client.shared.viewframework.FrameworkButton;
import org.jboss.as.console.client.shared.viewframework.FrameworkView;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.tables.TextLinkCell;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormAdapter;
import org.jboss.ballroom.client.widgets.icons.Icons;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.ballroom.client.widgets.window.Feedback;
import org.jboss.dmr.client.ModelDescriptionConstants;
import org.jboss.dmr.client.ModelNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author David Bosschaert
 */
public class BundleRuntimeView extends AbstractEntityView<OSGiBundle> implements FrameworkView {
    private final EntityToDmrBridgeImpl<OSGiBundle> bridge;
    private DefaultCellTable<OSGiBundle> bundleTable;
    private OSGiRuntimePresenter presenter;
    private MyListHandler<OSGiBundle> sortHandler;

    public BundleRuntimeView(ApplicationMetaData propertyMetaData, DispatchAsync dispatcher) {
        super(OSGiBundle.class, propertyMetaData, EnumSet.allOf(FrameworkButton.class));
        bridge = new EntityToDmrBridgeImpl<OSGiBundle>(propertyMetaData, OSGiBundle.class, this, dispatcher) {
            @Override
            protected void onLoadEntitiesSuccess(ModelNode response) {
                if (response.get(ModelDescriptionConstants.RESULT).asList().isEmpty()) {
                    presenter.askToActivateSubsystem();
                } else {
                    super.onLoadEntitiesSuccess(response);
                }
            }
        };
    }

    @Override
    public Widget createWidget() {
        Widget widget = createEmbeddableWidget();
        sortHandler.setList(entityEditor.getDataProvider().getList());
        return widget;
    }

    @Override
    protected ToolStrip createToolStrip() {
        ToolStrip toolStrip = super.createToolStrip();
        ToolButton refreshBtn = new ToolButton(Console.CONSTANTS.common_label_refresh(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                initialLoad(RuntimeBaseAddress.get());
            }
        });
        refreshBtn.ensureDebugId(Console.DEBUG_CONSTANTS.debug_label_refresh_bundleRuntimeView());
        toolStrip.addToolButtonRight(refreshBtn);
        return toolStrip;
    }

    @Override
    public EntityToDmrBridge<OSGiBundle> getEntityBridge() {
        return bridge;
    }

    @Override
    protected DefaultCellTable<OSGiBundle> makeEntityTable() {
        bundleTable = new DefaultCellTable<OSGiBundle>(8);
        sortHandler = new MyListHandler<OSGiBundle>();

        TextColumn<OSGiBundle> idColumn = new TextColumn<OSGiBundle>() {
            @Override
            public String getValue(OSGiBundle record) {
                return record.getName();
            }
        };
        idColumn.setSortable(true);
        sortHandler.setComparator(idColumn, new Comparator<OSGiBundle>() {
            @Override
            public int compare(OSGiBundle o1, OSGiBundle o2) {
                return new Long(o1.getName()).compareTo(new Long(o2.getName()));
            }
        });
        bundleTable.addColumn(idColumn, Console.CONSTANTS.subsys_osgi_bundleID());

        TextColumn<OSGiBundle> symbolicNameColumn = new TextColumn<OSGiBundle>() {
            @Override
            public String getValue(OSGiBundle record) {
                return record.getSymbolicName();
            }
        };
        symbolicNameColumn.setSortable(true);
        sortHandler.setComparator(symbolicNameColumn, new Comparator<OSGiBundle>() {
            @Override
            public int compare(OSGiBundle o1, OSGiBundle o2) {
                return o1.getSymbolicName().compareTo(o2.getSymbolicName());
            }
        });
        bundleTable.addColumn(symbolicNameColumn, Console.CONSTANTS.subsys_osgi_bundleSymbolicName());

        TextColumn<OSGiBundle> versionColumn = new TextColumn<OSGiBundle>() {
            @Override
            public String getValue(OSGiBundle record) {
                return record.getVersion();
            }
        };
        bundleTable.addColumn(versionColumn, Console.CONSTANTS.subsys_osgi_bundleVersion());

        Column<OSGiBundle, ImageResource> stateColumn = new Column<OSGiBundle, ImageResource>(new ImageResourceCell()) {
            @Override
            public ImageResource getValue(OSGiBundle bundle) {
                if ("ACTIVE".equals(bundle.getState()))
                    return Icons.INSTANCE.status_good();
                if ("STARTING".equals(bundle.getState()))
                    return Icons.INSTANCE.status_warn();
                if ("RESOLVED".equals(bundle.getState()))
                    return Icons.INSTANCE.status_none();

                // default
                return Icons.INSTANCE.status_none();
            }
        };
        stateColumn.setSortable(true);
        sortHandler.setComparator(stateColumn, new Comparator<OSGiBundle>() {
            @Override
            public int compare(OSGiBundle o1, OSGiBundle o2) {
                List<String> order = Arrays.asList("RESOLVED", "STARTING", "ACTIVE");
                Integer i1 = order.indexOf(o1.getState());
                Integer i2 = order.indexOf(o2.getState());

                return i1.compareTo(i2);
            }
        });
        bundleTable.addColumn(stateColumn, Console.CONSTANTS.subsys_osgi_bundleState());

        class BundleColumn extends Column<OSGiBundle,OSGiBundle> {
            public BundleColumn(Cell<OSGiBundle> cell) {
                super(cell);
            }

            @Override
            public OSGiBundle getValue(OSGiBundle record) {
                return record;
            }
        };
        TextLinkCell<OSGiBundle> startCell = new TextLinkCell<OSGiBundle>(Console.CONSTANTS.common_label_start()+"&nbsp;&nbsp;", new ActionCell.Delegate<OSGiBundle>() {
            @Override
            public void execute(OSGiBundle bundle) {
                if ("fragment".equals(bundle.getType())) {
                    Feedback.alert(Console.CONSTANTS.subsys_osgi(), Console.MESSAGES.subsys_osgi_cant_start_fragment(bundle.getSymbolicName()));
                } else {
                    presenter.startBundle(bundle);
                }
            }
        });

        final TextLinkCell<OSGiBundle> stopCell = new TextLinkCell<OSGiBundle>(Console.CONSTANTS.common_label_stop(), new ActionCell.Delegate<OSGiBundle>() {
            @Override
            public void execute(OSGiBundle bundle) {
                if ("fragment".equals(bundle.getType())) {
                    Feedback.alert(Console.CONSTANTS.subsys_osgi(), Console.MESSAGES.subsys_osgi_cant_stop_fragment(bundle.getSymbolicName()));
                } else {
                    presenter.stopBundle(bundle);
                }
            }
        });
        List<HasCell<OSGiBundle,OSGiBundle>> buttonCells = new ArrayList<HasCell<OSGiBundle,OSGiBundle>>();
        buttonCells.add(new BundleColumn(startCell));
        buttonCells.add(new BundleColumn(stopCell));
        BundleColumn myColumn = new BundleColumn(new CompositeCell(buttonCells));

        bundleTable.addColumn(myColumn, Console.CONSTANTS.common_label_action());

        bundleTable.addColumnSortHandler(sortHandler);
        bundleTable.getColumnSortList().push(idColumn); // initial sort is on bundle ID

        return bundleTable;
    }

    @Override
    protected FormAdapter<OSGiBundle> makeAddEntityForm() {
        return new Form<OSGiBundle>(OSGiBundle.class); // Empty form, cannot create a bundle here
    }

    @Override
    protected String getEntityDisplayName() {
        return Console.CONSTANTS.subsys_osgi_bundles();
    }

    @Override
    public void refresh() {
        super.refresh();

        // Make sure the new values are properly sorted
        ColumnSortEvent.fire(bundleTable, bundleTable.getColumnSortList());
    }

    public void setPresenter(OSGiRuntimePresenter presenter) {
        this.presenter = presenter;
    }

    // This handler is similar to ColumnSortEvent.ListHandler except that it allows the list to be set after construction
    // This class is generic and could move to a more common place if useful.
    public static class MyListHandler<T> implements Handler {
        private final Map<Column<?, ?>, Comparator<T>> comparators = new HashMap<Column<?, ?>, Comparator<T>>();
        private List<T> list;

        public List<T> getList() {
            return list;
        }

        public void setList(List<T> list) {
            this.list = list;
        }

        public void onColumnSort(ColumnSortEvent event) {
            // Get the sorted column.
            Column<?, ?> column = event.getColumn();
            if (column == null) {
                return;
            }

            // Get the comparator.
            final Comparator<T> comparator = comparators.get(column);
            if (comparator == null) {
                return;
            }

            // Sort using the comparator.
            if (event.isSortAscending()) {
                Collections.sort(list, comparator);
            } else {
                Collections.sort(list, new Comparator<T>() {
                    public int compare(T o1, T o2) {
                        return -comparator.compare(o1, o2);
                    }
                });
            }
        }

        public void setComparator(Column<T, ?> column, Comparator<T> comparator) {
            comparators.put(column, comparator);
        }
    }
}
