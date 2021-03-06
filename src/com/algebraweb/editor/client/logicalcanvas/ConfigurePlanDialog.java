package com.algebraweb.editor.client.logicalcanvas;

import com.algebraweb.editor.client.graphcanvas.EditorCommunicationCallback;
import com.algebraweb.editor.client.graphcanvas.GraphCanvas;
import com.algebraweb.editor.client.services.RemoteManipulationServiceAsync;
import com.algebraweb.editor.shared.logicalplan.EvaluationContext;

/**
 * The dialog window for plan configurations
 * 
 * @author Patrick Brosi
 * 
 */
public class ConfigurePlanDialog extends EvaluationDialog {
	private EditorCommunicationCallback<Void> updateCb = new EditorCommunicationCallback<Void>(
			"evaluating") {
		@Override
		public void onSuccess(Void result) {
			GraphCanvas.hideLoading();
			hide();
		}
	};

	public ConfigurePlanDialog(int pid, RemoteManipulationServiceAsync manServ) {
		super(pid, -1, manServ);
		super.getButtonsPanel().remove(super.getSaveCheckBox());
		super.setText("Configure plan settings");
	}

	@Override
	protected void submit() {
		EvaluationContext c = saveContext();
		getManServ().updatePlanEvaluationContext(c, getPid(), updateCb);
		GraphCanvas.showLoading("Saving...");
		hide();
	}
}