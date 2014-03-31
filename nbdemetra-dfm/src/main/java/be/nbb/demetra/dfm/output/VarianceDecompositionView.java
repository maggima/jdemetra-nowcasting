/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.nbb.demetra.dfm.output;

import com.google.common.base.Optional;
import ec.tss.Dfm.DfmResults;
import ec.tss.datatransfer.TssTransferSupport;
import ec.tstoolkit.data.DataBlock;
import ec.tstoolkit.dfm.DfmInformationSet;
import ec.tstoolkit.maths.matrices.Matrix;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
import ec.ui.chart.TsXYDatasets;
import ec.util.chart.SeriesFunction;
import ec.util.chart.TimeSeriesChart;
import ec.util.various.swing.JCommand;
import java.awt.Toolkit;
import java.awt.datatransfer.Transferable;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 *
 * @author charphi
 */
final class VarianceDecompositionView extends javax.swing.JPanel {

    public static final String DFM_RESULTS_PROPERTY = "dfmResults";

    private final int[] horizon = {
        1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 14, 16, 18,
        20, 24, 28, 32, 36, 40, 48, 60, 72, 84, 96, 120, 240, 1000
    };
    private Optional<DfmResults> dfmResults;

    /**
     * Creates new form VarianceDecompositionView
     */
    public VarianceDecompositionView() {
        initComponents();

        this.dfmResults = Optional.absent();

        comboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                updateChart();
            }
        });
        
        chart.setPopupMenu(createChartMenu().getPopupMenu());
        chart.setSeriesRenderer(SeriesFunction.always(TimeSeriesChart.RendererType.STACKED_COLUMN));
        chart.setSeriesFormatter(new SeriesFunction<String>() {
            @Override
            public String apply(int series) {
                return chart.getDataset().getSeriesKey(series).toString();
            }
        });
        chart.setValueFormat(new DecimalFormat("#.###"));
        chart.setPeriodFormat(new DateFormat() {
            final Calendar cal = Calendar.getInstance();

            @Override
            public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
                cal.setTime(date);
                int year = cal.get(Calendar.YEAR);
                int index = year - 2000;
                return index >= 0 && index < horizon.length ? toAppendTo.append(horizon[index]) : toAppendTo;
            }

            @Override
            public Date parse(String source, ParsePosition pos) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });

        addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                switch (evt.getPropertyName()) {
                    case DFM_RESULTS_PROPERTY:
                        updateComboBox();
                        updateChart();
                }
            }
        });

        updateComboBox();
        updateChart();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        chart = new ec.util.chart.swing.JTimeSeriesChart();
        comboBox = new javax.swing.JComboBox();

        setLayout(new java.awt.BorderLayout());
        add(chart, java.awt.BorderLayout.CENTER);

        add(comboBox, java.awt.BorderLayout.PAGE_START);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ec.util.chart.swing.JTimeSeriesChart chart;
    private javax.swing.JComboBox comboBox;
    // End of variables declaration//GEN-END:variables

    //<editor-fold defaultstate="collapsed" desc="Getters/Setters">
    public Optional<DfmResults> getDfmResults() {
        return dfmResults;
    }

    public void setDfmResults(Optional<DfmResults> dfmResults) {
        Optional<DfmResults> old = this.dfmResults;
        this.dfmResults = dfmResults != null ? dfmResults : Optional.<DfmResults>absent();
        firePropertyChange(DFM_RESULTS_PROPERTY, old, this.dfmResults);
    }
    //</editor-fold>

    private void updateComboBox() {
        if (dfmResults.isPresent()) {
            comboBox.setModel(toComboBoxModel(dfmResults.get().getInput()));
            comboBox.setEnabled(true);
        } else {
            comboBox.setModel(new DefaultComboBoxModel());
            comboBox.setEnabled(false);
        }
    }

    private void updateChart() {
        if (dfmResults.isPresent() && comboBox.getSelectedIndex() != -1) {
            TsPeriod start = new TsPeriod(TsFrequency.Yearly, 2000, 0);
            TsXYDatasets.Builder b = TsXYDatasets.builder();
            int i = 0;
            for (DataBlock o : toMatrix(dfmResults.get(), comboBox.getSelectedIndex()).rowList()) {
                b.add("F" + i++, new TsData(start, o.getData(), true));
            }
            chart.setDataset(b.build());
        } else {
            chart.setDataset(null);
        }
    }

    private Matrix toMatrix(DfmResults results, int selectedItem) {
        return results.getVarianceDecompositionIdx(horizon, selectedItem);
    }

    private static DefaultComboBoxModel toComboBoxModel(DfmInformationSet data) {
        DefaultComboBoxModel result = new DefaultComboBoxModel();
        for (int i = 0; i < data.getSeriesCount(); i++) {
            result.addElement("Var " + (i + 1));
        }
        return result;
    }

    private JMenu createChartMenu() {
        JMenu menu = new JMenu();
        JMenuItem item;

        item = menu.add(CopyCommand.INSTANCE.toAction(this));
        item.setText("Copy");

        return menu;
    }

    private static final class CopyCommand extends JCommand<VarianceDecompositionView> {

        public static final CopyCommand INSTANCE = new CopyCommand();

        @Override
        public void execute(VarianceDecompositionView c) throws Exception {
            Optional<DfmResults> dfmResults = c.getDfmResults();
            if (dfmResults.isPresent()) {
                Transferable t = TssTransferSupport.getInstance().fromMatrix(c.toMatrix(dfmResults.get(), c.comboBox.getSelectedIndex()));
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(t, null);
            }
        }

        @Override
        public boolean isEnabled(VarianceDecompositionView c) {
            return c.getDfmResults().isPresent();
        }

        @Override
        public JCommand.ActionAdapter toAction(VarianceDecompositionView c) {
            return super.toAction(c).withWeakPropertyChangeListener(c, DFM_RESULTS_PROPERTY);
        }
    }

}
