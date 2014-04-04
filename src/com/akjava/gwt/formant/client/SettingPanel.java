package com.akjava.gwt.formant.client;

import java.io.IOException;
import java.util.List;

import com.akjava.gwt.formant.client.BaseFormantDataConverter.BaseFormantData;
import com.akjava.gwt.formant.client.BaseFormantDataEditor.Driver;
import com.akjava.gwt.formant.client.resources.Bundles;
import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.html5.client.input.ColorBox;
import com.akjava.gwt.lib.client.StorageException;
import com.akjava.gwt.lib.client.widget.cell.ButtonColumn;
import com.akjava.gwt.lib.client.widget.cell.EasyCellTableObjects;
import com.akjava.gwt.lib.client.widget.cell.SimpleCellTable;
import com.akjava.lib.common.utils.ValuesUtils;
import com.google.common.collect.Lists;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SettingPanel extends DockLayoutPanel{
private Driver baseFormantDriver=GWT.create(Driver.class);
private static final String KEY_SETTING_BASE_FORMANT="formant_setting_base_formant";
private static final String KEY_SETTING_ZOOM="formant_setting_zoom";
private static final String KEY_SETTING_SHOW_BASE="formant_setting_show_base";	
private static final String KEY_SETTING_QUALITY="formant_setting_quality";

private static final String KEY_SETTING_FONT_VALUE="formant_setting_font_value";

private static final String KEY_SETTING_COLOR_TEXT="formant_setting_color_text";
private static final String KEY_SETTING_COLOR_INSIDE="formant_setting_color_inside";
private static final String KEY_SETTING_COLOR_BG="formant_setting_color_bg";
private static final String KEY_SETTING_COLOR_BASE="formant_setting_color_base";
private static final String KEY_SETTING_COLOR_VALUE="formant_setting_color_value";
private static final String KEY_SETTING_COLOR_MEMORY="formant_setting_color_memory";

private static final String KEY_SETTING_F1_GRID_MIN="formant_setting_f1_grid_min";
private static final String KEY_SETTING_F1_GRID_MAX="formant_setting_f1_grid_max";
private static final String KEY_SETTING_F2_GRID_MIN="formant_setting_f2_grid_min";
private static final String KEY_SETTING_F2_GRID_MAX="formant_setting_f2_grid_max";

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
	
	tab.add(createImageFormant(),GWTFormant.textConstants.generate_image());//tmp first
	
	tab.add(createBaseFormant(),GWTFormant.textConstants.base_formant());
	//tab.add(createImageFormant(),"generate image");
	tab.add(createZoomAndQuality(),GWTFormant.textConstants.show_formant());
	tab.selectTab(0);
	
	
}

private Panel createQuality(){

	HorizontalPanel quality=new HorizontalPanel();

	Label label=new Label("FFT"+GWTFormant.textConstants.quolity());
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

	Label label=new Label(GWTFormant.textConstants.zoom());
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

private ColorBox textColorBox,insideColorBox,bgColorBox,baseRectBox,valueRectBox,memoryLineBox;
private IntegerBox f1minBox,f1maxBox,f2minBox,f2maxBox;
private TextBox valueFontBox;

private void storeValue(String key,String value){
	try {
		getEntryPoint().storageControler.setValue(key,value);
	} catch (StorageException e) {
		Window.alert(e.getMessage());//usually quota
	}
}

private void storeValue(String key,int value){
	try {
		getEntryPoint().storageControler.setValue(key,value);
	} catch (StorageException e) {
		Window.alert(e.getMessage());//usually quota
	}
}
private Panel createImageFormant(){
	
	VerticalPanel panel=new VerticalPanel();
	
	showBase = new CheckBox(GWTFormant.textConstants.show_base_on_image());
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
	
	valueFontBox=createTextBox(panel, GWTFormant.textConstants.font(), getEntryPoint().storageControler.getValue(KEY_SETTING_FONT_VALUE, "16px Courier"));
	valueFontBox.addValueChangeHandler(new StringStoreHandler(KEY_SETTING_FONT_VALUE));
			
			
	
	Button reset0=new Button(GWTFormant.textConstants.reset(),new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			valueFontBox.setText("16px Courier");
			storeValue(KEY_SETTING_FONT_VALUE, valueFontBox.getText());
		}
	});
	panel.add(reset0);
	
	
	panel.add(new Label("Color"));
	textColorBox=createColorBox(panel, GWTFormant.textConstants.text(), getEntryPoint().storageControler.getValue(KEY_SETTING_COLOR_TEXT, "#000000"));
	insideColorBox=createColorBox(panel, GWTFormant.textConstants.inside(), getEntryPoint().storageControler.getValue(KEY_SETTING_COLOR_INSIDE, "#CCFFFF"));

	bgColorBox=createColorBox(panel, GWTFormant.textConstants.background(), getEntryPoint().storageControler.getValue(KEY_SETTING_COLOR_BG, "#FFFFFF"));

	baseRectBox=createColorBox(panel, GWTFormant.textConstants.baseRect(), getEntryPoint().storageControler.getValue(KEY_SETTING_COLOR_BASE, "#0000FF"));
	valueRectBox=createColorBox(panel, GWTFormant.textConstants.valueRect(), getEntryPoint().storageControler.getValue(KEY_SETTING_COLOR_VALUE, "#FF0000"));
	memoryLineBox=createColorBox(panel, GWTFormant.textConstants.memoryLine(), getEntryPoint().storageControler.getValue(KEY_SETTING_COLOR_MEMORY, "#FFFFFF"));
	
	Button reset=new Button(GWTFormant.textConstants.reset(),new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			textColorBox.setValue("#000000");
			insideColorBox.setValue("#CCFFFF");
			bgColorBox.setValue("#FFFFFF");
			baseRectBox.setValue("#0000FF");
			valueRectBox.setValue("#FF0000");
			memoryLineBox.setValue("#FFFFFF");
			
			storeValue(KEY_SETTING_COLOR_TEXT, textColorBox.getValue());
			storeValue(KEY_SETTING_COLOR_INSIDE, insideColorBox.getValue());
			storeValue(KEY_SETTING_COLOR_BG, bgColorBox.getValue());
			storeValue(KEY_SETTING_COLOR_BASE, baseRectBox.getValue());
			storeValue(KEY_SETTING_COLOR_VALUE, valueRectBox.getValue());
			storeValue(KEY_SETTING_COLOR_MEMORY, memoryLineBox.getValue());
		}
	});
	panel.add(reset);
	
	
	//TODO store when closed
	panel.add(new Label("Grid"));
	
	f1minBox=createIntegerBox(panel,GWTFormant.textConstants.min()+" F1", getEntryPoint().storageControler.getValue(KEY_SETTING_F1_GRID_MIN, 100));
	f1maxBox=createIntegerBox(panel,GWTFormant.textConstants.max()+"F1", getEntryPoint().storageControler.getValue(KEY_SETTING_F1_GRID_MAX, 1300));
	f2minBox=createIntegerBox(panel,GWTFormant.textConstants.min()+"F2", getEntryPoint().storageControler.getValue(KEY_SETTING_F2_GRID_MIN, 400));
	f2maxBox=createIntegerBox(panel,GWTFormant.textConstants.max()+"F2", getEntryPoint().storageControler.getValue(KEY_SETTING_F2_GRID_MAX, 3200));
	
	f1minBox.addValueChangeHandler(new StoreHandler(KEY_SETTING_F1_GRID_MIN));
	f1maxBox.addValueChangeHandler(new StoreHandler(KEY_SETTING_F1_GRID_MAX));
	f2minBox.addValueChangeHandler(new StoreHandler(KEY_SETTING_F2_GRID_MIN));
	f2maxBox.addValueChangeHandler(new StoreHandler(KEY_SETTING_F2_GRID_MAX));
	
	Button reset2=new Button(GWTFormant.textConstants.reset(),new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			f1minBox.setValue(100);
			f1maxBox.setValue(1300);
			f2minBox.setValue(400);
			f2maxBox.setValue(3200);
			
			storeValue(KEY_SETTING_F1_GRID_MIN, f1minBox.getValue());
			storeValue(KEY_SETTING_F1_GRID_MAX, f1maxBox.getValue());
			storeValue(KEY_SETTING_F2_GRID_MIN, f2minBox.getValue());
			storeValue(KEY_SETTING_F2_GRID_MAX, f2maxBox.getValue());
		}
	});
	panel.add(reset2);
	
	
	ScrollPanel scroll=new ScrollPanel();
	scroll.add(panel);
	return scroll;
}

private class StoreHandler implements ValueChangeHandler<Integer>{
private String key;
private StoreHandler(String key){
	this.key=key;
}
	@Override
	public void onValueChange(ValueChangeEvent<Integer> event) {
		storeValue(key, event.getValue());
	}
}

private class StringStoreHandler implements ValueChangeHandler<String>{
private String key;
private StringStoreHandler(String key){
	this.key=key;
}
	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		storeValue(key, event.getValue());
	}
}

public int getF1Min(){
	return f1minBox.getValue();
}

public int getF1Max(){
	return f1maxBox.getValue();
}

public int getF2Min(){
	return f2minBox.getValue();
}

public int getF2Max(){
	return f2maxBox.getValue();
}

public String getValueFont(){
	return valueFontBox.getValue();
}
public String getTextColor(){
	return textColorBox.getValue();
}

public String getBackgroundColor(){
	return bgColorBox.getValue();
}
public String getInsideColor(){
	return insideColorBox.getValue();
}
public String getBaseRectColor(){
	return baseRectBox.getValue();
}
public String getValueRectColor(){
	return valueRectBox.getValue();
}

public String getMemoryLineColor(){
	return memoryLineBox.getValue();
}

//TODO uibinder
public ColorBox createColorBox(Panel parent,String name,String value){
	HorizontalPanel h=new HorizontalPanel();
	parent.add(h);
	
	Label label=new Label(name);
	label.setWidth("100px");
	h.add(label);
	
	ColorBox box=new ColorBox();
	box.setValue(value);
	h.add(box);
	return box;
}

public TextBox createTextBox(Panel parent,String name,String value){
	HorizontalPanel h=new HorizontalPanel();
	parent.add(h);
	
	Label label=new Label(name);
	label.setWidth("100px");
	h.add(label);
	
	TextBox box=new TextBox();
	box.setValue(value);
	h.add(box);
	return box;
}

public IntegerBox createIntegerBox(Panel parent,String name,int value){
	HorizontalPanel h=new HorizontalPanel();
	parent.add(h);
	
	Label label=new Label(name);
	label.setWidth("100px");
	h.add(label);
	
	IntegerBox box=new IntegerBox();
	box.setValue(value);
	h.add(box);
	return box;
}
	
private Panel createBaseFormant(){
	VerticalPanel panel=new VerticalPanel();
	
	
	SimpleCellTable<BaseFormantData> table=new SimpleCellTable<BaseFormantData>(9) {
		@Override
		public void addColumns(CellTable<BaseFormantData> table) {
			TextColumn<BaseFormantData> nameColumn=new TextColumn<BaseFormantDataConverter.BaseFormantData>() {
				@Override
				public String getValue(BaseFormantData object) {
					return object.getName();
				}
			};
			table.addColumn(nameColumn,GWTFormant.textConstants.name());
			
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
					storeBaseFormant();
				}
				
				@Override
				public String getValue(BaseFormantData object) {
					// TODO Auto-generated method stub
					return GWTFormant.textConstants.up();
				}
			};
			table.addColumn(upButton);
			
			ButtonColumn<BaseFormantData> downButton=new ButtonColumn<BaseFormantDataConverter.BaseFormantData>() {
				
				@Override
				public void update(int index, BaseFormantData object, String value) {
					easyCells.downItem(object);
					storeBaseFormant();
				}
				
				@Override
				public String getValue(BaseFormantData object) {
					return GWTFormant.textConstants.down();
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
	
	addButton = new Button(GWTFormant.textConstants.add(),new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			BaseFormantData data=baseFormantDriver.flush();
			easyCells.addItem(data);
			createNewBaseFormantData();
			
			storeBaseFormant();
		}
	});
	buttons.add(addButton);
	addButton.setEnabled(true);
	

	
	updateButton = new Button(GWTFormant.textConstants.update(),new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			baseFormantDriver.flush();
			easyCells.update(true);
			
			storeBaseFormant();
		}
	});
	updateButton.setEnabled(false);
	buttons.add(updateButton);
	
	removeButton = new Button(GWTFormant.textConstants.remove(),new ClickHandler() {
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
	
	HorizontalPanel bottomPanel=new HorizontalPanel();
	panel.add(bottomPanel);
	final ListBox presetBox=new ListBox();
	presetBox.addItem(GWTFormant.textConstants.canadian_vowel());
	presetBox.addItem(GWTFormant.textConstants.average_vowel());
	presetBox.setSelectedIndex(0);
	bottomPanel.add(presetBox);
	
	Button loadBt=new Button(GWTFormant.textConstants.load(),new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			String text;
			if(presetBox.getSelectedIndex()==0){
				text=Bundles.INSTANCE.canadian().getText();
			}else{
				text=Bundles.INSTANCE.average().getText();
			}
			easyCells.setDatas(new BaseFormantDataConverter().reverse().convert(text));
			easyCells.update(true);
			
			storeBaseFormant();
		}
	});
	bottomPanel.add(loadBt);
	
	//TODO importbt
	bottomPanel.add(new Label(GWTFormant.textConstants.upload()));
	FileUploadForm importUpload=FileUtils.createSingleTextFileUploadForm(new DataURLListener() {
		@Override
		public void uploaded(File file, String asStringText) {
			easyCells.setDatas(new BaseFormantDataConverter().reverse().convert(asStringText));
			easyCells.update(true);
		}
	}, true);
	bottomPanel.add(importUpload);
	
	//TODO exportbt
	
	ScrollPanel scroll=new ScrollPanel();
	scroll.add(panel);
	return scroll;

}

protected void storeBaseFormant() {
	String text=new BaseFormantDataConverter().convert(easyCells.getDatas());
	
	try {
		getEntryPoint().storageControler.setValue(KEY_SETTING_BASE_FORMANT,text);
	} catch (StorageException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}



public List<BaseFormantData> getBaseFormantDatas(){
	return easyCells.getDatas();
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
	//bugs no way to catch value change handle.
	storeValue(KEY_SETTING_COLOR_TEXT, textColorBox.getValue());
	storeValue(KEY_SETTING_COLOR_INSIDE, insideColorBox.getValue());
	storeValue(KEY_SETTING_COLOR_BG, bgColorBox.getValue());
	storeValue(KEY_SETTING_COLOR_BASE, baseRectBox.getValue());
	storeValue(KEY_SETTING_COLOR_VALUE, valueRectBox.getValue());
	storeValue(KEY_SETTING_COLOR_MEMORY, memoryLineBox.getValue());
	
	getEntryPoint().updateFormantList();//sync formant data
	getEntryPoint().repaintBoth();
	getEntryPoint().mainDeck.showWidget(0);
}

private void createTopBar() {
	HorizontalPanel buttons=new HorizontalPanel();
	this.addNorth(buttons,30);
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
