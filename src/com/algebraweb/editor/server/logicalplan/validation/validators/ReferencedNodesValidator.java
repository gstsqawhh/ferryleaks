package com.algebraweb.editor.server.logicalplan.validation.validators;

import java.util.ArrayList;
import java.util.Iterator;

import com.algebraweb.editor.client.node.NodeContent;
import com.algebraweb.editor.client.node.PlanNode;
import com.algebraweb.editor.client.validation.ValidationError;
import com.algebraweb.editor.client.validation.ValidationResult;
import com.algebraweb.editor.server.logicalplan.validation.Validator;

public class ReferencedNodesValidator implements Validator {

	@Override
	public void validate(ArrayList<PlanNode> ps, ArrayList<PlanNode> plan,
			ValidationResult r) {

		Iterator<PlanNode> it = ps.iterator();

		while (it.hasNext()) {

			PlanNode current = it.next();

			if (current != null) {

				Iterator<NodeContent> edges = current.getDirectContentWithInternalName("edge").iterator();

				while (edges.hasNext()) {


					int curId = Integer.parseInt(edges.next().getAttributes().get("to").getVal());

					if (!hasChildWithId(current,curId)) {

						String errorMsg = "Node wishes to be the loving mother of node #" + curId + ", but the child couldn't be found. Maybe you referred to a node introduced after this?";

						r.addError(new ValidationError(current.getId(), errorMsg));

					}


				}
			}



		}


	}


	private boolean hasChildWithId(PlanNode n, int id) {

		Iterator<PlanNode> it = n.getChilds().iterator();

		while (it.hasNext()) {

			PlanNode current = it.next();

			if (current != null && current.getId() == id) return true;

		}

		return false;

	}

}