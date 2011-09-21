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

package org.jboss.as.console.client.shared.subsys.deploymentscanner;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.shared.subsys.deploymentscanner.model.DeploymentScanner;
import org.jboss.as.console.client.shared.viewframework.AddEntityWindow;
import org.jboss.as.console.client.shared.viewframework.AttributeMetadata;
import org.jboss.as.console.client.shared.viewframework.Columns.EnabledColumn;
import org.jboss.as.console.client.shared.viewframework.Columns.NameColumn;
import org.jboss.as.console.client.shared.viewframework.EntityAttributes;
import org.jboss.as.console.client.shared.viewframework.EntityToDmrBridge;
import org.jboss.as.console.client.shared.viewframework.EntityDetails;
import org.jboss.as.console.client.shared.viewframework.EntityEditor;
import org.jboss.as.console.client.shared.viewframework.EntityPopupWindow;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormAdapter;
import org.jboss.ballroom.client.widgets.forms.FormItem;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;



/**
 * Main view class for Deployment Scanners.  This class assembles the editor and reacts to 
 * FrameworkView callbacks.
 * 
 * @author Stan Silvert
 */
public class ScannerView extends SuspendableViewImpl implements ScannerPresenter.MyView {

    private EntityToDmrBridge scannerBridge;
    private EntityEditor<DeploymentScanner> scannerEditor;
    private EntityAttributes attributes;

    @Override
    public Widget createWidget() {
        scannerEditor = makeScannerEditor();
        
        TabLayoutPanel tabLayoutpanel = new TabLayoutPanel(25, Style.Unit.PX);
        tabLayoutpanel.addStyleName("default-tabpanel");
        
        tabLayoutpanel.add(scannerEditor.asWidget(), Console.CONSTANTS.subsys_deploymentscanner_scanners());

        return tabLayoutpanel;
    }
    
    private EntityEditor<DeploymentScanner> makeScannerEditor() {
        EntityDetails<DeploymentScanner> scannerDetails = new EntityDetails<DeploymentScanner>(Console.CONSTANTS.subsys_deploymentscanner_scanners(), 
                                                               makeEditScannerForm(), 
                                                               scannerBridge);
        String title = Console.CONSTANTS.common_label_add() + " " + Console.CONSTANTS.subsys_deploymentscanner_scanners();
        EntityPopupWindow<DeploymentScanner> window = new AddEntityWindow<DeploymentScanner>(title, 
                                                                                             makeAddScannerForm(), 
                                                                                             scannerBridge);
        DefaultCellTable<DeploymentScanner> table = makeScannerTable();
        return new EntityEditor<DeploymentScanner>(Console.CONSTANTS.subsys_deploymentscanner_scanners(), window, table, scannerDetails);
    }
    
    private DefaultCellTable<DeploymentScanner> makeScannerTable() {
        DefaultCellTable<DeploymentScanner> table = new DefaultCellTable<DeploymentScanner>(4);
        
        table.addColumn(new NameColumn(), NameColumn.LABEL);
        table.addColumn(new EnabledColumn(), EnabledColumn.LABEL);
        
        return table;
    }

    private FormAdapter<DeploymentScanner> makeAddScannerForm() {
        Form<DeploymentScanner> form = new Form(DeploymentScanner.class);
        form.setNumColumns(1);
        form.setFields(attributes.findAttribute("name").getItemForAdd(),
                       attributes.findAttribute("path").getItemForAdd(),
                       attributes.findAttribute("relativeTo").getItemForAdd(),
                       attributes.findAttribute("enabled").getItemForAdd());
        return form;
    }
    
    private FormAdapter<DeploymentScanner> makeEditScannerForm() {
        Form<DeploymentScanner> form = new Form(DeploymentScanner.class);
        form.setNumColumns(2);
        FormItem[] items = new FormItem[attributes.getAllAttributes().size()];
        int i=0;
        for (AttributeMetadata attrib : attributes.getAllAttributes()) {
            items[i++] = attrib.getItemForEdit();
        }
        form.setFields(items);
        return form;
    }
    
    /**
     * Called when the user requests details to be edited.
     * @param isEnabled 
     */
    @Override
    public void setEditingEnabled(boolean isEnabled) {
        scannerEditor.setEditingEnabled(isEnabled);
    }
    
    /**
     * Called whenever there is a change to any DeploymentScanner
     */
    @Override
    public void refresh() {
        DeploymentScanner lastLoggerConfigEdited = null;
        if (this.scannerBridge.getNameOfLastEdited() != null) {
            // Look up by name.
            lastLoggerConfigEdited = (DeploymentScanner)scannerBridge.findEntity(scannerBridge.getNameOfLastEdited());
        }

        scannerEditor.updateEntityList(scannerBridge.getEntityList(), lastLoggerConfigEdited);
    }

    /**
     * Called when Presenter is created.
     * @param bridge 
     */
    @Override
    public void setEntityToDmrBridge(EntityToDmrBridge bridge) {
        this.scannerBridge = bridge;
        this.attributes = bridge.getEntityAttributes();
    }
    
}
