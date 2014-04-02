package com.akjava.gwt.formant.client;

import java.io.IOException;
import java.util.List;

import com.akjava.gwt.formant.client.BaseFormantDataConverter.BaseFormantData;
import com.akjava.gwt.formant.client.BaseFormantDataEditor.Driver;
import com.akjava.gwt.formant.client.resources.Bundles;
import com.akjava.gwt.lib.client.StorageException;
import com.akjava.gwt.lib.client.widget.cell.ButtonColumn;
import com.akjava.gwt.lib.client.widget.cell.EasyCellTableObjects;
import com.akjava.gwt.lib.client.widget.cell.SimpleCellTable;
import com.akjava.lib.common.utils.ValuesUtils;
import com.google.common.collect.Lists;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SettingPanel extends DockLayoutPanel{
private Driver baseFormantDriver=GWT.create(Driver.class);
private static final String KEY_SETTING_BASE_FORMANT="formant_setting_base_formant";
private static final String KEY_SETTING_ZOOM="formant_setting_zoom";
private static final String KEY_SETTING_SHOW_BASE="formant_setting_show_base";	
private static final String KEY_SETTING_QUALITY="formant_setting_quality";
private ValueListBox<Integer> zoomValueBox;
private ValueListBox<Integer> qualityValueBox;
private CheckBox showBase;
private EasyCellTableObjects<BaseFormantData> easyCells;
private BaseFormantDataEditor baseFormantEditor;
private Button updateButton;
private Button newButton;

private Button removeButton;
private Button addButton;
public SettingPanel(){
	super(Unit.PX);
	createTopBar();
	createBody();
}



private void createBody() {
	VerticalPanel v=new VerticalPanel();
	v.setSize("100%", "100%");
	this.add(v);
	TabLayoutPanel tab=new TabLayoutPanel(25, Unit.PX);
	v.add(tab);
	tab.setSize("100%", "100%");
	
	//zoom
	tab.add(createZoomAndQuality(),"zoom");
	tab.selectTab(0);
	tab.add(createBaseFormant(),"base formant");
	
}

private Panel createQuality(){

	HorizontalPanel quality=new HorizontalPanel();

	Label label=new Label("fftquality");
	label.setWidth("100px");
	quality.add(label);
	List<Integer> qualityValues=Lists.newArrayList(1,2);
	qualityValueBox = new ValueListBox<Integer>(new Renderer<Integer>() {

		@Override
		public String render(Integer object) {
			// TODO Auto-generated method stub
			return String.valueOf(object);
		}

		@Override
		public void render(Integer object, Appendable appendable) throws IOException {
			// TODO Auto-generated method stub
			
		}
	});
	
	int qvalue=getEntryPoint().storageControler.getValue(KEY_SETTING_QUALITY, 1);
	qualityValueBox.setValue(qvalue);
	qualityValueBox.setAcceptableValues(qualityValues);
	quality.add(qualityValueBox);
	qualityValueBox.addValueChangeHandler(new ValueChangeHandler<Integer>() {
		
		@Override
		public void onValueChange(ValueChangeEvent<Integer> event) {
			try {
				getEntryPoint().storageControler.setValue(KEY_SETTING_QUALITY,event.getValue());
			} catch (StorageException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			getEntryPoint().changeQualityValue(event.getValue());
		}
	});
	return quality;
}
private Panel createZoom(){

	HorizontalPanel zoom=new HorizontalPanel();

	Label label=new Label("zoom");
	label.setWidth("100px");
	zoom.add(label);
	List<Integer> zoomValues=Lists.newArrayList(1,2,4);
	zoomValueBox = new ValueListBox<Integer>(new Renderer<Integer>() {

		@Override
		public String render(Integer object) {
			// TODO Auto-generated method stub
			return String.valueOf(object);
		}

		@Override
		public void render(Integer object, Appendable appendable) throws IOException {
			// TODO Auto-generated method stub
			
		}
	});
	
	int zvalue=getEntryPoint().storageControler.getValue(KEY_SETTING_ZOOM, 2);
	zoomValueBox.setValue(zvalue);
	zoomValueBox.setAcceptableValues(zoomValues);
	zoom.add(zoomValueBox);
	zoomValueBox.addValueChangeHandler(new ValueChangeHandler<Integer>() {
		
		@Override
		public void onValueChange(ValueChangeEvent<Integer> event) {
			try {
				getEntryPoint().storageControler.setValue(KEY_SETTING_ZOOM,event.getValue());
			} catch (StorageException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			getEntryPoint().reserveResizeCanvas(event.getValue());
		}
	});
	return zoom;
}


private Panel createZoomAndQuality(){
	VerticalPanel zoomPanel=new VerticalPanel();
	
	zoomPanel.add(createZoom());
	zoomPanel.add(createQuality());
	
	return zoomPanel;
}

private Panel createBaseFormant(){
	VerticalPanel panel=new VerticalPanel();
	
	showBase = new CheckBox("show base on image");
	panel.add(showBase);
	boolean b=ValuesUtils.toBoolean(getEntryPoint().storageControler.getValue(KEY_SETTING_SHOW_BASE, "true"),true);
	showBase.setValue(b);
	
	showBase.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			try {
				getEntryPoint().storageControler.setValue(KEY_SETTING_SHOW_BASE,""+event.getValue());
			} catch (StorageException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	});
	SimpleCellTable<BaseFormantData> table=new SimpleCellTable<BaseFormantData>(9) {
		@Override
		public void addColumns(CellTable<BaseFormantData> table) {
			TextColumn<BaseFormantData> nameColumn=new TextColumn<BaseFormantDataConverter.BaseFormantData>() {
				@Override
				public String getValue(BaseFormantData object) {
					return object.getName();
				}
			};
			table.addColumn(nameColumn,"Name");
			
			TextColumn<BaseFormantData> f1Column=new TextColumn<BaseFormantDataConverter.BaseFormantData>() {
				@Override
				public String getValue(BaseFormantData object) {
					return ""+object.getF1();
				}
			};
			table.addColumn(f1Column,"F1");
			
			
			TextColumn<BaseFormantData> f2Column=new TextColumn<BaseFormantDataConverter.BaseFormantData>() {
				@Override
				public String getValue(BaseFormantData object) {
					return ""+object.getF2();
				}
			};
			table.addColumn(f2Column,"F2");
			
			ButtonColumn<BaseFormantData> upButton=new ButtonColumn<BaseFormantDataConverter.BaseFormantData>() {
				
				@Override
				public void update(int index, BaseFormantData object, String value) {
					easyCells.upItem(object);
				}
				
				@Override
				public String getValue(BaseFormantData object) {
					// TODO Auto-generated method stub
					return "Up";
				}
			};
			table.addColumn(upButton);
			
			ButtonColumn<BaseFormantData> downButton=new ButtonColumn<BaseFormantDataConverter.BaseFormantData>() {
				
				@Override
				public void update(int index, BaseFormantData object, String value) {
					easyCells.downItem(object);
				}
				
				@Override
				public String getValue(BaseFormantData object) {
					return "Down";
				}
			};
			table.addColumn(downButton);
		}
	};
	panel.add(table);
	
	easyCells = new EasyCellTableObjects<BaseFormantDataConverter.BaseFormantData>(table
			) {
		@Override
		public void onSelect(BaseFormantData selection) {
			if(selection!=null){
				baseFormantDriver.edit(selection);
				//baseFormantEditor.setVisible(true);
				updateButton.setEnabled(true);
				removeButton.setEnabled(true);
				newButton.setEnabled(true);
				addButton.setEnabled(false);
			}else{
				//baseFormantEditor.setVisible(false);
				updateButton.setEnabled(false);
				removeButton.setEnabled(false);
				newButton.setEnabled(false);
				addButton.setEnabled(true);
			}		
		}
		
	};
	
	
	baseFormantEditor = new BaseFormantDataEditor();
	baseFormantEditor.setVisible(true);
	
	
	baseFormantDriver.initialize(baseFormantEditor);
	createNewBaseFormantData();
	
	//load data
	String baseText=getEntryPoint().storageControler.getValue(KEY_SETTING_BASE_FORMANT, Bundles.INSTANCE.canadian().getText());
	
	easyCells.setDatas(new BaseFormantDataConverter().reverse().convert(baseText));
	easyCells.update(true);
	
	HorizontalPanel buttons=new HorizontalPanel();
	panel.add(buttons);
	
	newButton = new Button("New",new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			easyCells.unselect();
			createNewBaseFormantData();
		}
	});
	buttons.add(newButton);
	newButton.setEnabled(false);
	
	addButton = new Button("Add",new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			BaseFormantData data=baseFormantDriver.flush();
			easyCells.addItem(data);
			createNewBaseFormantData();
		}
	});
	buttons.add(addButton);
	addButton.setEnabled(true);
	

	
	updateButton = new Button("Update",new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			baseFormantDriver.flush();
			easyCells.update(true);
			//updateBaseFormant convert and store?
			//TODO store
		}
	});
	updateButton.setEnabled(false);
	buttons.add(updateButton);
	
	removeButton = new Button("remove",new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			if(easyCells.getSelection()!=null){
				easyCells.removeItem(easyCells.getSelection());
			}
		}
	});
	buttons.add(removeButton);
	removeButton.setEnabled(false);
	
	panel.add(baseFormantEditor);
	
	//reset & import
	
	//HorizontalPanel bottomPanel=new HorizontalPanel();
	
	return panel;
}

private void synchBaseFormantData(){
	//store data;

}

private void createNewBaseFormantData(){
	baseFormantDriver.edit(new BaseFormantData("", 500, 1500));
}

public GWTFormant getEntryPoint(){
	return GWTFormant.getEntryPoint();
}

public int getZoom(){
	return zoomValueBox.getValue();
}

protected void doClose() {
	getEntryPoint().mainDeck.showWidget(0);
}

private void createTopBar() {
	HorizontalPanel buttons=new HorizontalPanel();
	this.addNorth(buttons,25);
	Button close=new Button(GWTFormant.textConstants.close(),new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			doClose();
		}
	});
	buttons.add(close);
}

public boolean isShowImageBaseFormant(){
	return showBase.getValue();
}

public int getQuality() {
	return qualityValueBox.getValue();	
}

}
