package org.jboss.as.console.client.tools.mbui.workbench.repository;

import static com.google.gwt.visualization.client.AbstractDataTable.ColumnType.STRING;

import java.util.Stack;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.visualizations.OrgChart;
import org.jboss.mbui.model.Dialog;
import org.jboss.mbui.model.mapping.MappingType;
import org.jboss.mbui.model.structure.Container;
import org.jboss.mbui.model.structure.InteractionUnit;
import org.jboss.mbui.model.structure.TemporalOperator;
import org.jboss.mbui.model.structure.as7.StereoTypes;
import org.jboss.mbui.model.structure.impl.InteractionUnitVisitor;

/**
 * @author Harald Pehl
 * @date 03/07/2013
 */
public class DialogVisualization
{
    static final NameTemplate NAME_TEMPLATE = GWT.create(NameTemplate.class);
    private static final String MAPPED_STYLE = "";//"icon attachment";
    private static final String UNMAPPED_STYLE = "";
    private final OrgChart chart;

    public DialogVisualization(final Dialog dialog)
    {
        this.chart = new OrgChart(createData(dialog), createOptions(dialog));
    }

    protected DataTable createData(final Dialog dialog)
    {
        DataTable dataTable = DataTable.create();
        dataTable.addColumn(STRING, "id");
        dataTable.addColumn(STRING, "parent");
        dataTable.addColumn(STRING, "tooltip");

        OrgChartVisitor visitor = new OrgChartVisitor(dataTable);
        dialog.getInterfaceModel().accept(visitor);

        return dataTable;
    }

    protected OrgChart.Options createOptions(final Dialog dialog)
    {
        OrgChart.Options options = OrgChart.Options.create();
        options.setAllowHtml(true);
        options.setAllowCollapse(true);
        return options;
    }

    public OrgChart getChart()
    {
        return chart;
    }


    static class OrgChartVisitor implements InteractionUnitVisitor
    {
        final DataTable dataTable;
        int row;
        Stack<Container> container;

        OrgChartVisitor(final DataTable dataTable)
        {
            this.dataTable = dataTable;
            this.row = 0;
            this.container = new Stack<Container>();
        }

        @Override
        public void startVisit(final Container container)
        {
            addInteractionUnitRow(container);
            this.container.push(container);
        }

        @Override
        public void visit(final InteractionUnit interactionUnit)
        {
            addInteractionUnitRow(interactionUnit);
        }

        @Override
        public void endVisit(final Container container)
        {
            this.container.pop();
        }

        void addInteractionUnitRow(InteractionUnit interactionUnit)
        {
            String id = interactionUnit.getId().toString();
            String name = interactionUnit.getLabel() == null ? interactionUnit.getId().getLocalPart() : interactionUnit.getLabel();
            Container container = this.container.isEmpty() ? null : this.container.peek();
            String parentId = container != null ? container.getId().toString() : null;
            String style = interactionUnit.hasMapping(MappingType.DMR) ? MAPPED_STYLE : UNMAPPED_STYLE;

            if (interactionUnit instanceof Container)
            {
                TemporalOperator operator = ((Container) interactionUnit).getTemporalOperator();
                if (operator != null)
                {
                    name = NAME_TEMPLATE.name(style, name, operator.name()).asString();
                }
            }
            else
            {
                String classname = interactionUnit.getClass().getName();

                classname = classname.substring(classname.lastIndexOf('.') + 1);
                name = NAME_TEMPLATE.name(style, name, classname).asString();
            }

            StringBuilder tooltip = new StringBuilder();
            tooltip.append("[unit]\n").append(id).append("\n\n");
            if (interactionUnit.doesConsume())
                tooltip.append("[input]\n").append(interactionUnit.getInputs()).append("\n\n");
            if (interactionUnit.doesProduce())
                tooltip.append("[output]\n").append(interactionUnit.getOutputs()).append("\n");


            dataTable.addRow();

            dataTable.setCell(row, 0, id, name, null);
            dataTable.setValue(row, 1, parentId);
            dataTable.setValue(row, 2, tooltip.toString());
            row++;
        }
    }


    interface NameTemplate extends SafeHtmlTemplates
    {
        @Template("<div>{1}<br/><span style=\"color:#666;\">&laquo;{2}&raquo;</span><div class='{0}'></div></div>")
        SafeHtml name(String css, String name, String stereotype);
    }
}
