package com.akjava.gwt.formant.client;

import com.akjava.gwt.formant.client.BaseFormantDataConverter.BaseFormantData;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class BaseFormantDataEditor extends VerticalPanel implements Editor<BaseFormantData>{
	  interface Driver extends SimpleBeanEditorDriver<BaseFormantData, BaseFormantDataEditor> {}
TextBox nameEditor;
IntegerBox f1Editor;
IntegerBox f2Editor;
	public BaseFormantDataEditor(){
		super();
		HorizontalPanel names=new HorizontalPanel();
		this.add(names);
		Label nameLabel=new Label("name");
		nameLabel.setWidth("100px");
		names.add(nameLabel);
		
		nameEditor=new TextBox();
		nameEditor.setWidth("200px");
		names.add(nameEditor);
		
		HorizontalPanel f1s=new HorizontalPanel();
		this.add(f1s);
		Label f1Label=new Label("f1");
		f1Label.setWidth("100px");
		f1s.add(f1Label);
		
		f1Editor=new IntegerBox();
		f1Editor.setWidth("200px");
		f1s.add(f1Editor);
		
		HorizontalPanel f2s=new HorizontalPanel();
		this.add(f2s);
		Label f2Label=new Label("f2");
		f2Label.setWidth("100px");
		f2s.add(f2Label);
		
		f2Editor=new IntegerBox();
		f2Editor.setWidth("200px");
		f2s.add(f2Editor);
	}
}
