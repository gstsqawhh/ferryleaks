package com.algebraweb.editor.server.logicalplan.xmlbuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Text;

import com.algebraweb.editor.client.logicalcanvas.EvaluationContext;
import com.algebraweb.editor.client.logicalcanvas.GraphIsEmptyException;
import com.algebraweb.editor.client.logicalcanvas.GraphNotConnectedException;
import com.algebraweb.editor.client.logicalcanvas.PlanHasCycleException;
import com.algebraweb.editor.client.logicalcanvas.PlanManipulationException;
import com.algebraweb.editor.client.node.ContentNode;
import com.algebraweb.editor.client.node.ContentVal;
import com.algebraweb.editor.client.node.NodeContent;
import com.algebraweb.editor.client.node.PlanNode;
import com.algebraweb.editor.client.node.Property;
import com.algebraweb.editor.client.node.QueryPlan;
import com.algebraweb.editor.server.logicalplan.QueryPlanBundle;

public class XMLNodePlanBuilder {


	public XMLNodePlanBuilder() {
	}

	private List<PlanNode> getAllNodesUnderThis(PlanNode rootNode) throws PlanHasCycleException {
		return getAllNodesUnderThis(rootNode, new ArrayList<PlanNode>());
	}

	private List<PlanNode> getAllNodesUnderThis(PlanNode rootNode, List<PlanNode> way) throws PlanHasCycleException {
		List<PlanNode> nodes = new ArrayList<PlanNode>();

		if (rootNode == null) return nodes;

		Iterator<PlanNode> it = rootNode.getChilds().iterator();
		nodes.add(rootNode);

		while (it.hasNext()) {
			PlanNode cur = it.next();
			List<PlanNode> wayCopy = new ArrayList<PlanNode>();
			wayCopy.addAll(way);
			if (wayCopy.contains(cur) && cur != null) throw new PlanHasCycleException(cur.getId());
			wayCopy.add(cur);
			Iterator<PlanNode> i = getAllNodesUnderThis(cur,wayCopy).iterator();

			while (i.hasNext()) {
				PlanNode current = i.next();
				if (!nodes.contains(current)) {
					nodes.add(current);
				}else{
					nodes.remove(current);
					nodes.add(current);
				}
			}
		}

		return nodes;

	}

	@SuppressWarnings("unused")
	private Map<Integer,Integer> getNodeIdReplacements(PlanNode rootNode,int offset) throws PlanHasCycleException {
		HashMap<Integer,Integer> retMap = new HashMap<Integer,Integer>();
		List<PlanNode> nodes = getAllNodesUnderThis(rootNode);

		Collections.reverse(nodes);
		Iterator<PlanNode> it = nodes.iterator();

		while (it.hasNext()) {
			int id = it.next().getId();
			System.out.println("Mapping " + id + " to " + offset);
			retMap.put(id,offset);
			offset++;
		}
		return retMap;
	}

	public Element getNodePlan(int id, PlanNode rootNode, EvaluationContext c, ServletContext context) throws PlanManipulationException, PlanHasCycleException {
		Element nodePlan = new Element("query_plan");
		nodePlan.setAttribute("id", Integer.toString(id));
		Element logicalPlan = new Element("logical_query_plan");
		logicalPlan.setAttribute("unique_names", "true");

		if (!rootNode.getKind().equals("serialize relation")) {
			System.out.println("Adding serialize relation from eval context...");
			SerializeRelationBuilder srb = new SerializeRelationBuilder(c,context);
			rootNode = srb.addSerializRelation(rootNode);
		}

		HashMap<Integer,Integer> repl = new HashMap<Integer,Integer>();//getNodeIdReplacements(rootNode,0);
		List<PlanNode> nodes = getAllNodesUnderThis(rootNode);

		Collections.reverse(nodes);
		Iterator<PlanNode> it = nodes.iterator();

		while (it.hasNext()) {
			logicalPlan.addContent(getXMLElementFromContentNode(it.next(),repl));
		}

		nodePlan.addContent(logicalPlan);
		return nodePlan;
	}

	public Document getNodePlan(QueryPlan p, ServletContext context) throws PlanManipulationException, GraphNotConnectedException, GraphIsEmptyException, PlanHasCycleException {
		Document d = new Document();
		d.addContent(getNodePlan(p.getId(),p.getRootNode(),p.getEvContext(),context));
		return d;
	}

	public Document getPlanBundle(QueryPlanBundle b, ServletContext context) throws PlanManipulationException, GraphNotConnectedException, GraphIsEmptyException, PlanHasCycleException {
		Element planBundle = new Element ("query_plan_bundle");
		Iterator<QueryPlan> it = b.getPlans().values().iterator();

		while (it.hasNext()) {
			QueryPlan cur = it.next();
			planBundle.addContent(getNodePlan(cur.getId(), cur.getRootNode(), cur.getEvContext(), context));
		}

		Document d = new Document();
		d.addContent(planBundle);
		return d;
	}


	public Element getXMLElementFromContentNode(ContentNode n) {
		return getXMLElementFromContentNode(n, new HashMap<Integer,Integer>());
	}


	public Element getXMLElementFromContentNode(ContentNode n,HashMap<Integer,Integer> nodeIdReplacements) {
		Element ret = null;

		if (n instanceof PlanNode) {
			ret = new Element("node");
			if (nodeIdReplacements.get(((PlanNode) n).getId()) != null) {
				ret.setAttribute("id", Integer.toString(nodeIdReplacements.get(((PlanNode) n).getId())));
			}else{
				ret.setAttribute("id", Integer.toString(((PlanNode)n).getId()));
			}
			ret.setAttribute("kind", ((PlanNode)n).getKind());
		}else{
			ret = new Element(n.getInternalName());
			Iterator<Property> it = ((NodeContent)n).getAttributes().properties().iterator();

			while(it.hasNext()) {
				Property c = it.next();
				if (c.getPropertyVal().getType().equals("__NID") &&
						nodeIdReplacements.get(Integer.parseInt(c.getPropertyVal().getVal()))!=null) {
					ret.setAttribute(c.getPropertyName(), Integer.toString(nodeIdReplacements.get(Integer.parseInt(c.getPropertyVal().getVal()))));
				}else{
					if (c.getPropertyVal().getVal() != null)
						ret.setAttribute(c.getPropertyName(), c.getPropertyVal().getVal());
				}
			}
			if (n instanceof ContentVal && ((ContentVal)n).getValue() !=null && ((ContentVal)n).getValue().getVal() !="") {
				ret.addContent(new Text(((ContentVal)n).getValue().getVal()));
			}
		}

		Iterator<NodeContent> i = n.getContent().iterator();

		while (i.hasNext()) {
			ret.addContent(getXMLElementFromContentNode(i.next(),nodeIdReplacements));
		}
		return ret;
	}
}
