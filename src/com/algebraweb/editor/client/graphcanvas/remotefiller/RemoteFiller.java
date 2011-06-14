package com.algebraweb.editor.client.graphcanvas.remotefiller;

import java.util.ArrayList;


import com.algebraweb.editor.client.RawNode;
import com.algebraweb.editor.client.graphcanvas.GraphManipulationCallback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;




public class RemoteFiller {
	
	
	private ArrayList<RawNode> nodes;
	
	private GraphCanvasRemoteFillingMachine m;
	
	GraphManipulationCallback cb;
	
	private RemoteFillingServiceAsync commServ = (RemoteFillingServiceAsync) GWT.create(RemoteFillingService.class);
	
	private String filler;
	
	public RemoteFiller(String filler) {
		
		this.filler=filler;
		
				
	}
	
	public void init(GraphCanvasRemoteFillingMachine m, GraphManipulationCallback cb) {
			
		commServ.getRawNodes(filler,nodeCallback);
		this.m=m;
		this.cb=cb;
		
	}
	
	
	private AsyncCallback<ArrayList<RawNode>> nodeCallback = new AsyncCallback<ArrayList<RawNode>>() {

		@Override
		public void onFailure(Throwable caught) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onSuccess(ArrayList<RawNode> result) {

			RemoteFiller.this.nodes = result;
		    RemoteFiller.this.m.fillWith(RemoteFiller.this.nodes);
			if (RemoteFiller.this.cb!= null) RemoteFiller.this.cb.onComplete();
				
		}

	};

}
