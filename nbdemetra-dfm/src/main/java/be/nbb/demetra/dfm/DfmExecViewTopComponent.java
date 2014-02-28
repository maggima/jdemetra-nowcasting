/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.nbb.demetra.dfm;

import ec.nbdemetra.ui.DemetraUiIcon;
import ec.nbdemetra.ui.properties.OpenIdePropertySheetBeanEditor;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.nbdemetra.ws.ui.WorkspaceTopComponent;
import ec.tss.Dfm.DfmDocument;
import ec.tss.Dfm.DfmProcessingFactory;
import ec.tstoolkit.algorithm.CompositeResults;
import ec.tstoolkit.algorithm.IProcessingHook;
import ec.tstoolkit.algorithm.IProcessingNode;
import ec.tstoolkit.dfm.DfmEstimationSpec;
import ec.util.various.swing.JCommand;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.SwingWorker;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.api.settings.ConvertAsProperties;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewDescription;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.util.Cancellable;
import static org.openide.util.ImageUtilities.createDisabledIcon;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//be.nbb.demetra.dfm//DfmExecView//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "DfmExecViewTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_NEVER
)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@Messages({
    "CTL_DfmExecViewAction=DfmExecView",
    "CTL_DfmExecViewTopComponent=DfmExecView Window",
    "HINT_DfmExecViewTopComponent=This is a DfmExecView window"
})
public final class DfmExecViewTopComponent extends WorkspaceTopComponent<DfmDocument> implements MultiViewElement, MultiViewDescription {

    public static final String DFM_STATE_PROPERTY = "dfmState";

    public enum DfmState {

        READY, STARTED, DONE, FAILED, CANCELLED
    };
    private DfmState dfmState;

    public DfmExecViewTopComponent() {
        this(null);
    }

    public DfmExecViewTopComponent(WorkspaceItem<DfmDocument> document) {
        super(document);
        initComponents();
        setName(Bundle.CTL_DfmExecViewTopComponent());
        setToolTipText(Bundle.HINT_DfmExecViewTopComponent());
        jEditorPane1.setEditable(false);
        this.dfmState = DfmState.READY;

        addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                switch (evt.getPropertyName()) {
                    case DFM_STATE_PROPERTY:
                        onDfmStateChange();
                        break;
                }
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jEditorPane1 = new javax.swing.JEditorPane();

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.LINE_AXIS));

        jScrollPane1.setViewportView(jEditorPane1);

        add(jScrollPane1);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
    void writeProperties(java.util.Properties p) {
    }

    void readProperties(java.util.Properties p) {
    }

    //<editor-fold defaultstate="collapsed" desc="MultiViewElement">
    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    @Override
    public JComponent getVisualRepresentation() {
        return this;
    }

    @Override
    public JComponent getToolbarRepresentation() {
        JToolBar toolBar = new JToolBar();
        toolBar.addSeparator();
        toolBar.add(Box.createRigidArea(new Dimension(5, 0)));

        JButton start = toolBar.add(StartCommand.INSTANCE.toAction(this));
        start.setIcon(DemetraUiIcon.COMPILE_16);
        start.setDisabledIcon(createDisabledIcon(start.getIcon()));
        start.setToolTipText("Start");

        JButton edit = toolBar.add(EditSpecCommand.INSTANCE.toAction(this));
        edit.setIcon(DemetraUiIcon.PREFERENCES);
        edit.setDisabledIcon(createDisabledIcon(edit.getIcon()));
        edit.setToolTipText("Specification");

        JButton clear = toolBar.add(ClearCommand.INSTANCE.toAction(this));
        clear.setIcon(DemetraUiIcon.EDIT_CLEAR_16);
        clear.setDisabledIcon(createDisabledIcon(clear.getIcon()));
        clear.setToolTipText("Clear");

        return toolBar;
    }

    @Override
    public void setMultiViewCallback(MultiViewElementCallback callback) {
    }

    @Override
    public CloseOperationState canCloseElement() {
        return CloseOperationState.STATE_OK;
    }

    @Override
    public void componentActivated() {
        super.componentActivated();
    }

    @Override
    public void componentDeactivated() {
        super.componentDeactivated();
    }

    @Override
    public void componentHidden() {
        super.componentHidden();
    }

    @Override
    public void componentShowing() {
        super.componentShowing();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="MultiViewDescription">
    @Override
    public MultiViewElement createElement() {
        return this;
    }

    @Override
    public String preferredID() {
        return super.preferredID();
    }
    //</editor-fold>

    @Override
    protected String getContextPath() {
        return DfmDocumentManager.CONTEXTPATH;
    }

    private DfmState getDfmState() {
        return dfmState;
    }

    private void setDfmState(DfmState state) {
        DfmState old = this.dfmState;
        this.dfmState = state;
        firePropertyChange(DFM_STATE_PROPERTY, old, this.dfmState);
    }

    private ProgressHandle progressHandle;
    private SwingWorkerImpl swingWorker;

    private void onDfmStateChange() {
        switch (dfmState) {
            case CANCELLED:
                progressHandle.finish();
                break;
            case DONE:
                progressHandle.finish();
                break;
            case FAILED:
                progressHandle.finish();
                break;
            case READY:
                break;
            case STARTED:
                jEditorPane1.setText("");
                swingWorker = new SwingWorkerImpl();
                progressHandle = ProgressHandleFactory.createHandle(getName(), new Cancellable() {
                    @Override
                    public boolean cancel() {
                        swingWorker.cancel(false);
                        return true;
                    }
                }, new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        DfmExecViewTopComponent.this.open();
                        DfmExecViewTopComponent.this.requestActive();
                    }
                });
                progressHandle.start();
                swingWorker.execute();
                break;
        }
    }

    private final class SwingWorkerImpl extends SwingWorker<CompositeResults, IProcessingHook.HookInformation<IProcessingNode, DfmProcessingFactory.EstimationInfo>> implements IProcessingHook<IProcessingNode, DfmProcessingFactory.EstimationInfo> {

        @Override
        protected CompositeResults doInBackground() throws Exception {
            getDocument().getElement().getProcessor().register(this);
            CompositeResults rslt = getDocument().getElement().getResults();
            getDocument().getElement().getProcessor().unregister(this);
            return rslt;
        }

        @Override
        protected void done() {
            if (isCancelled()) {
                setDfmState(DfmState.CANCELLED);
            } else {
                try {
                    CompositeResults results = get();
                    setDfmState(DfmState.DONE);
                } catch (InterruptedException | ExecutionException ex) {
                    setDfmState(DfmState.FAILED);
                }
            }
        }

        @Override
        public void process(HookInformation<IProcessingNode, DfmProcessingFactory.EstimationInfo> info, boolean cancancel) {
            if (isCancelled()) {
                info.cancel = true;
            }
            publish(info);
        }

        @Override
        protected void process(List<HookInformation<IProcessingNode, DfmProcessingFactory.EstimationInfo>> chunks) {
            for (HookInformation<IProcessingNode, DfmProcessingFactory.EstimationInfo> info : chunks) {
                progressHandle.progress(info.message);
                StringBuilder txt = new StringBuilder();
                txt.append(info.source.getName()).append('\t')
                        .append(info.message).append('\t').append(info.information.loglikelihood);
                txt.append("\r\n");
                final String msg = jEditorPane1.getText() + txt.toString();
                jEditorPane1.setText(msg);
                jEditorPane1.repaint();
            }
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Commands">
    private static abstract class DfmExecCommand extends JCommand<DfmExecViewTopComponent> {

        @Override
        public JCommand.ActionAdapter toAction(DfmExecViewTopComponent c) {
            return super.toAction(c).withWeakPropertyChangeListener(c, DFM_STATE_PROPERTY);
        }
    }

    private static final class StartCommand extends DfmExecCommand {

        public static final StartCommand INSTANCE = new StartCommand();

        @Override
        public void execute(DfmExecViewTopComponent c) throws Exception {
            c.setDfmState(DfmState.STARTED);
        }

        @Override
        public boolean isEnabled(DfmExecViewTopComponent c) {
            return c.getDfmState() == DfmState.READY;
        }
    }

    private static final class EditSpecCommand extends DfmExecCommand {

        public static final EditSpecCommand INSTANCE = new EditSpecCommand();

        @Override
        public void execute(DfmExecViewTopComponent c) throws Exception {
            DfmEstimationSpec newValue = c.getDocument().getElement().getSpecification().getEstimationSpec().clone();
            if (OpenIdePropertySheetBeanEditor.editSheet(DfmSheets.onDfmEstimationSpec(newValue), "Edit spec", null)) {
                c.getDocument().getElement().getSpecification().setEstimationSpec(newValue);
                c.getDocument().getElement().clear();
                c.setDfmState(DfmState.READY);
            }
        }

        @Override
        public boolean isEnabled(DfmExecViewTopComponent c) {
            return c.getDfmState() != DfmState.STARTED;
        }
    }

    private static final class ClearCommand extends DfmExecCommand {

        public static final ClearCommand INSTANCE = new ClearCommand();

        @Override
        public void execute(DfmExecViewTopComponent c) throws Exception {
            c.getDocument().getElement().clear();
            c.setDfmState(DfmState.READY);
        }

        @Override
        public boolean isEnabled(DfmExecViewTopComponent c) {
            return c.getDfmState() != DfmState.STARTED && c.getDfmState() != DfmState.READY;
        }
    }
    //</editor-fold>
}
