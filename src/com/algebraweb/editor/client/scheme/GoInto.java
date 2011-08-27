package com.algebraweb.editor.client.scheme;
import java.util.ArrayList;
import java.util.Iterator;


public class GoInto implements GoAble {

	protected String xmlObject;
	protected String howOften;
	protected String humanName;
	protected boolean editable = true;
	protected String nameToPrint = "";

	protected ArrayList<GoAble> childs = new ArrayList<GoAble>();


	public GoInto() {

	}


	public GoInto(String xmlObject, String howOften) {

		this.xmlObject=xmlObject;
		this.howOften = howOften;
		this.humanName = xmlObject;

	}
	
	
	public String getNameToPrint() {
		
		return nameToPrint;
		
	}

	public GoInto(String xmlObject, String howOften, String humanName) {

		this.xmlObject=xmlObject;
		this.howOften = howOften;
		this.humanName = humanName;
		
	}
	
	public GoInto(String xmlObject, String howOften, String humanName, String nameToPrint) {

		this(xmlObject, howOften, humanName);
		this.nameToPrint = nameToPrint;
		
	}

	public void addChild(GoAble child) {
		this.childs.add(child);
	}

	@Override
	public ArrayList<Field> getFields() {

		return null;
	}
	
	public String getHowOften() {
		return howOften;
	}

	public String getHumanName() {
		return humanName;
	}

	@Override
	public String getInternalName() {
		return xmlObject;
	}

	@Override
	public ArrayList<GoAble> getSchema() {
		return childs;
	}

	public String getXmlObject() {
		return xmlObject;
	}

	@Override
	public boolean hasChilds() {
		return childs.size()>0;
	}

	@Override
	public boolean hasFields() {

		return false;
	}

	/**
	 * @return the editable
	 */
	public boolean isEditable() {
		return editable;
	}


	/**
	 * @param editable the editable to set
	 */
	public void setEditable(boolean editable) {
		this.editable = editable;
	}


	public void setHowOften(String howOften) {
		this.howOften = howOften;
	}


	public void setXmlObject(String xmlObject) {
		this.xmlObject = xmlObject;
	}


	@Override
	public String toString() {

		String ret =  "{GOINTO: howOften=" + howOften + " XMLob:" + xmlObject + " childs:(";

		Iterator<GoAble> i = childs.iterator();

		while(i.hasNext()) {

			ret +=i.next().toString();

		}

		ret +=")}";

		return ret;

	}

	

}
