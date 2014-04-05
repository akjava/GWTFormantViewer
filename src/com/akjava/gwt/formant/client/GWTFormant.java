package com.akjava.gwt.formant.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.akjava.gwt.formant.client.BaseFormantDataConverter.BaseFormantData;
import com.akjava.gwt.formant.client.CanvasAndFileChooser.ArrayBufferUploadedListener;
import com.akjava.gwt.html5.client.download.HTML5Download;
import com.akjava.gwt.html5.client.file.Blob;
import com.akjava.gwt.html5.client.file.Uint8Array;
import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.GWTUtils;
import com.akjava.gwt.lib.client.ImageElementUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.StorageControler;
import com.akjava.gwt.lib.client.StorageException;
import com.akjava.gwt.recorder.client.Analyser;
import com.akjava.gwt.recorder.client.AudioContext;
import com.akjava.gwt.recorder.client.Buffer;
import com.akjava.gwt.recorder.client.LocalMediaStream;
import com.akjava.gwt.recorder.client.MediaStreamSource;
import com.akjava.gwt.recorder.client.Recorder;
import com.akjava.gwt.recorder.client.Recorder.ExportListener;
import com.akjava.gwt.recorder.client.RecorderConfig;
import com.akjava.gwt.recorder.client.UserMedia;
import com.akjava.gwt.recorder.client.UserMedia.ErrorListener;
import com.akjava.gwt.recorder.client.UserMedia.SuccessListener;
import com.akjava.gwt.recorder.client.UserMediaError;
import com.akjava.lib.common.utils.CSVUtils;
import com.akjava.lib.common.utils.ValuesUtils;
import com.google.common.base.Converter;
import com.google.common.base.Joiner;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.media.client.Audio;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.typedarrays.shared.ArrayBuffer;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;
import com.google.gwt.xhr.client.XMLHttpRequest.ResponseType;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GWTFormant implements EntryPoint {
	private static final String KEY_SETTING_FORMANT_HEADER="formant_setting_formant_header_";
	public static TextConstants textConstants=GWT.create(TextConstants.class);
	private Recorder recorder;
	private AudioContext audioContext;

	private MediaStreamSource source;
	private LocalMediaStream localMediaStream;
	private Blob lastBlob;
	private boolean recording;
	
	List<CanvasAndFileChooser> canvasList=new ArrayList<CanvasAndFileChooser>();
	
	private String appName="akjava";
	private String version="1.1";
	
	private RecorderConfig recorderConfig=RecorderConfig.create().workerPath("/js/recorderWorker.js");
	 DeckLayoutPanel mainDeck;
	private static GWTFormant entryPoint;
	public static GWTFormant getEntryPoint(){
		return entryPoint;
	}
	public void onModuleLoad() {
		GWTFormant.entryPoint=this;
		
		settingPanel=new SettingPanel();//get zoom need for other widget
		
		DockLayoutPanel rootDock=new DockLayoutPanel(Unit.PX);
		RootLayoutPanel.get().add(rootDock);
		
		createTopPanel(rootDock);
		
		
		mainDeck=new DeckLayoutPanel();
		rootDock.add(mainDeck);
		
		ScrollPanel scroll=new ScrollPanel();
		mainDeck.add(scroll);
		
		VerticalPanel root=new VerticalPanel();
		root.setWidth("100%");
		scroll.add(root);
		HorizontalPanel h=new HorizontalPanel();
		h.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		
		
		record = new Button(textConstants.start_record());
		stop = new Button(textConstants.stop_record());
		record.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				if(localMediaStream==null || localMediaStream.isEnded()){
					UserMedia.getUserMedia(true, new SuccessListener() {
						@Override
						public void onSuccess(LocalMediaStream loaclMediaStream) {
							LogUtils.log("media:"+loaclMediaStream.isEnded());
							startUserMedia(loaclMediaStream);
							startRecord();
						}

					
					}, new ErrorListener(){

						@Override
						public void onError(UserMediaError error) {
							LogUtils.log(error);
						}});
				}else{
					startRecord();
				}
				
				
				
				
				
				
			}
		});
		h.add(record);
		
		stop.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				
				stopAnalyze();
				
			}
		});
		h.add(stop);
		stop.setEnabled(false);
		
		Button stopAll=new Button(textConstants.stop_media(),new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				//source.disconnect(audioContext.getDestination());
				localMediaStream.stop();
				recorder=null;//no more useless maybe
				
			}
		});
		h.add(stopAll);
		
		Button copyTo=new Button(textConstants.copy_to_right(),new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				canvasList.get(0).copyTo(canvasList.get(1));
				repaintBoth();//TODO one side only
			}
		});
		h.add(copyTo);
		/*
		Button play=new Button("play",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
					audio=Audio.createIfSupported();
					if(audio!=null){
						String url=HTML5Download.get().URL().createObjectURL(lastBlob);
						audio.addSource(url);
						audio.play();
						
					}
				}
			
		});
		h.add(play);
		*/

		downloadLinks = new VerticalPanel();
		h.add(downloadLinks);
		
		audioContext=AudioContext.create();
		analyser=analyserSupplier.get();
		
		analyser.setFftSize(1024*getQuality());
		

		CanvasAndFileChooser main=new CanvasAndFileChooser(){
			public void doPlay(){
				if(arrayBuffer!=null){
					
					super.doPlay();
					}else{
						if(lastBlob==null){
							return;
						}
						
						audio=Audio.createIfSupported();
						if(audio!=null){
							String url=HTML5Download.get().URL().createObjectURL(lastBlob);
							audio.addSource(url);
							audio.play();
							
						}else{
							LogUtils.log("null audio element");
						}
					}
			}
		};
		canvasList.add(main);
		main.setListener(new ArrayBufferUploadedListener(){
			@Override
			public void uploaded(ArrayBuffer buffer) {
				targetCanvasIndex=0;
				analyzeWave(buffer);
			}
		});
		
		if(GWTUtils.isIAndroid()){
			root.add(h);
		}else{
			main.getBottoms().add(h);
		}
		
		CanvasAndFileChooser sub=new CanvasAndFileChooser();
		canvasList.add(sub);
		sub.setListener(new ArrayBufferUploadedListener(){
			@Override
			public void uploaded(ArrayBuffer buffer) {
				targetCanvasIndex=1;
				analyzeWave(buffer);
				//targetCanvasIndex=0;//for another action
			}
		});
		
		SplitLayoutPanel split=new SplitLayoutPanel();
		split.addEast(sub, 320);
		split.add(main);
		split.setSize("100%", (512+48)+"px");
		root.add(split);
		createBottoms(root);
		
		
		
		
		formantControler=new FormantControler(512);
		formantControler.setF1Y(hzToIndex(500));
		formantControler.setF2Y(hzToIndex(1500));
		main.getCanvas().addMouseDownHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				formantControler.down(event.getY());
				if(formantControler.isDrugging()){
					repaint();
				}
			}
		});
		
		main.getCanvas().addMouseUpHandler(new MouseUpHandler() {
			@Override
			public void onMouseUp(MouseUpEvent event) {
				formantControler.up(event.getY());
			}
		});
		
		main.getCanvas().addMouseMoveHandler(new MouseMoveHandler() {
			
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				if(formantControler.isDrugging()){
					formantControler.move(event.getY());
					repaint();
				}
			}
		});
		main.getCanvas().addMouseOutHandler(new MouseOutHandler() {
			
			@Override
			public void onMouseOut(MouseOutEvent event) {
				formantControler.up(event.getY());
			}
		});
		
		imagePanel = new VerticalPanel();
		root.add(imagePanel);
		repaintBoth();
		
		
		HorizontalPanel footer=new HorizontalPanel();
		
		String html="Record audio with <a href='https://github.com/mattdiamond/Recorderjs'>recorder.js</a> ,Formant value is based  on <a href='http://home.cc.umanitoba.ca/~krussll/phonetics/acoustic/formants.html'>Acoustic Phonetics</a>,<a href='http://en.wikipedia.org/wiki/Formant#cite_note-7'>Average vowel formants</a>";
		footer.add(new HTML(html));
		
		root.add(footer);
/*		
Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				loadWav();
			}
		});
		*/

		mainDeck.showWidget(0);
		
		
		mainDeck.add(settingPanel);
		
	}
	private SettingPanel settingPanel;
	
	
	private void createTopPanel(DockLayoutPanel rootDock) {
		HorizontalPanel top=new HorizontalPanel();
		top.setStylePrimaryName("gray");
		
		top.setWidth("100%");
		top.setStylePrimaryName("gray");
		Label title=new Label(appName+" "+textConstants.formant()+" "+textConstants.version()+" "+version);
		title.setStylePrimaryName("title");
		title.setStylePrimaryName("title");
		top.add(title);
		top.setSpacing(2);
		
		
		
		HorizontalPanel links=new HorizontalPanel();
		links.setSpacing(4);
		top.add(links);
		Anchor simpleRecognize=new Anchor(textConstants.simplerecognize(),"simplerecognize.html");
		links.add(simpleRecognize);
		
		links.add(new Label("|"));
		
		Anchor wordlesson=new Anchor(textConstants.wordlesson(),"wordlesson.html");
		links.add(wordlesson);
		
		
		
		Anchor setting=new Anchor(textConstants.settings());
		setting.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				updateSettings();
				mainDeck.showWidget(1);//setting
				
			}
		});
		top.add(setting);
		
		
		Anchor howtouse=new Anchor(textConstants.howtouse(),"formant_help.html");
		top.add(howtouse);
		
		rootDock.addNorth(top, 30);
	}

	private FormantControler formantControler;
	
	private void startRecord() {
		if(recorder==null){//I'm not sure i need store recorder for speed or memory.
			source = audioContext.createMediaStreamSource(localMediaStream);//need
			//source.connect(audioContext.getDestination());//just echo or not
			source.connect(analyser);//if you update source,you must recreate recorder.
			recorder=Recorder.create(source,recorderConfig);//worker in /js
			}
		
			//
			
			
		
			canvasList.get(0).reset();//0 share;
		
			record.setEnabled(false);
			stop.setEnabled(true);
			recorder.record();
			recording=true;
			initDraw();
	}
	
	protected void updateSettings() {
		// TODO Auto-generated method stub
		
	}
	
	private class FormantControler{
		private int canvasHeight;
		public FormantControler(int height){
			this.canvasHeight=height;
		}
		private int f1Y;
		public int getF1Y() {
			return f1Y;
		}
		public void setF1Y(int f1y) {
			f1Y = f1y;
		}
		public int getF2Y() {
			return f2Y;
		}
		public void setF2Y(int f2y) {
			f2Y = f2y;
		}
		public boolean isDrugging() {
			return druggingF1 || druggingF2;
		}
		
		private int f2Y;
		private boolean druggingF1;
		private boolean druggingF2;
		
		private int lastY;
		private int around=3;
		public void down(int y){
			int my=(canvasHeight-y);
			
			LogUtils.log(my+","+f1Y);
			
			if(my>=f1Y-around && my<=f1Y+around){
				druggingF1=true;
			}else if(my>=f2Y-around && my<=f2Y+around){
				druggingF2=true;
			}
			lastY=y;
			
			
		}
		public void up(int my){
			druggingF1=false;
			druggingF2=false;
		}
		public void move(int my){
			int df=my-lastY;
			if(df==0){
				return;
			}
			
			if(df>0){
				df=Math.max(df, 1);
			}else{
				df=Math.min(df, -1);
			}
			
			if(druggingF1){
				f1Y-=df;
				if(f1Y<0){
					f1Y=0;
				}
			}else if(druggingF2){
				f2Y-=df;
				if(f2Y<0){
					f2Y=0;
				}
			}
			lastY=my;
		}
	}
	
	private class FormantData{
		private String name;
		private BaseFormantData baseData;
		public BaseFormantData getBaseData() {
			return baseData;
		}
		public void setBaseData(BaseFormantData baseData) {
			this.baseData = baseData;
		}
		public FormantData(String name, int f1, int f2) {
			super();
			this.name = name;
			this.f1 = f1;
			this.f2 = f2;
		}
		private int f1;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public int getF1() {
			return f1;
		}
		public void setF1(int f1) {
			this.f1 = f1;
		}
		public int getF2() {
			return f2;
		}
		public void setF2(int f2) {
			this.f2 = f2;
		}
		private int f2;
		
		public String toString(){
			return name+","+f1+","+f2;
		}
	}
	
	protected void stopAnalyze() {
		
		
		//source.disconnect(analyser);//disconnect
		
		if(recording){
			record.setEnabled(true);
			stop.setEnabled(false);
			
			recorder.stop();
			
			recorder.exportWAV(new ExportListener() {
				
				@Override
				public void onExport(Blob blob) {
					lastBlob=blob;
					downloadLinks.clear();
					Anchor download=HTML5Download.get().generateDownloadLink(blob,"audio/wav", "recorded.wav", textConstants.download_recorded_audio(),true);
					downloadLinks.add(download);
					
				}
			});
			recorder.clear();
			recording=false;
			//source.disconnect(audioContext.getDestination());
		}else{
			recorder=null;//maybe recreate
		}
		
		
		drawTimer.cancel();
		drawCanvas();
	}

	
	List<FormantData> formants;
	public void updateFormantList(){
		formants=Lists.newArrayList();
		List<BaseFormantData> datas=settingPanel.getBaseFormantDatas();
		for(BaseFormantData base:datas){
			FormantData data=createFormantData(base);
			formants.add(data);
		}
		
		formantList.setValue(null);//TODO check not empty
		formantList.setAcceptableValues(formants);
	}
	
	private FormantData createFormantData(BaseFormantData base){
		String text=storageControler.getValue(KEY_SETTING_FORMANT_HEADER+base.getName(),"");
		FormantData data;
		if(!text.isEmpty()){
			data=new FormantConverter().reverse().convert(text);
		}else{
			data=new FormantData(base.getName(), base.getF1(), base.getF2());
		}
		data.setBaseData(base);
		return data;
	}
	
	private void createBottoms(Panel root){
		HorizontalPanel bottoms=new HorizontalPanel();
		bottoms.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		root.add(bottoms);
		showFormant = new CheckBox(textConstants.show_formant());
		bottoms.add(showFormant);
		showFormant.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				repaintBoth();
			}
		});
		showFormant.setValue(true);
		
		
		
	
		/*
		formantDefaultDatas = new ArrayList<GWTFormant.FormantData>();
		formantDefaultDatas.add(new FormantData("i", 280, 2230));
		formantDefaultDatas.add(new FormantData("e", 405, 2080));
		formantDefaultDatas.add(new FormantData("ɛ", 600, 1930));
		formantDefaultDatas.add(new FormantData("æ", 860, 1550));
		formantDefaultDatas.add(new FormantData("ɑ", 830, 1170));
		formantDefaultDatas.add(new FormantData("ɔ", 560, 820));
		formantDefaultDatas.add(new FormantData("o", 430, 980));
		formantDefaultDatas.add(new FormantData("u", 330, 1260));
		formantDefaultDatas.add(new FormantData("ʌ", 680, 1310));
		*/
	
		/*
		String text=storageControler.getValue(KEY_FORMANT_9MAP, "");
		if(!text.isEmpty()){
		formantDatas=FluentIterable.from(CSVUtils.splitLinesWithGuava(text)).transform(new FormantConverter().reverse()).toList();
		
		}else{
		formantDatas = Lists.newArrayList(formantDefaultDatas);
		}
		*/
		formantList = new ValueListBox<GWTFormant.FormantData>(new Renderer<FormantData>() {
			@Override
			public String render(FormantData object) {
				if(object==null){
					return textConstants.default_value();
				}
				return object.getName();
			}

			@Override
			public void render(FormantData object, Appendable appendable) throws IOException {
				// TODO Auto-generated method stub
				
			}
		});
		//formantList.setAcceptableValues(formantDatas);
		
		updateFormantList();
		
		
		bottoms.add(formantList);
		formantList.addValueChangeHandler(new ValueChangeHandler<GWTFormant.FormantData>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<FormantData> event) {
				FormantData data=event.getValue();
				//LogUtils.log(data.getName()+","+data.getF1()+","+data.getF2());
				if(data==null){
					resetBt.setEnabled(false);
					updateBt.setEnabled(false);
					formantControler.setF1Y(hzToIndex(500));
					formantControler.setF2Y(hzToIndex(1500));
				}else{
					resetBt.setEnabled(true);
					updateBt.setEnabled(true);
					formantControler.setF1Y(hzToIndex(data.getF1()));
					formantControler.setF2Y(hzToIndex(data.getF2()));
				}
				
				repaintBoth();
			}
		});
		
		updateBt = new Button(textConstants.save(),new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				FormantData data=formantList.getValue();
				int f1hz=parseIndexHz(formantControler.getF1Y());
				int f2hz=parseIndexHz(formantControler.getF2Y());
				LogUtils.log("f1="+f1hz+",f2="+f2hz);
				if(data==null){
					
				}else{
					data.setF1(f1hz);
					data.setF2(f2hz);
				}
				storeData(data);
				repaintBoth();
			}
		});
		bottoms.add(updateBt);
		resetBt = new Button(textConstants.reset(),new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				FormantData data=formantList.getValue();
				
				if(data!=null){
				
					data.setF1(data.getBaseData().getF1());
					data.setF2(data.getBaseData().getF2());
					
					formantControler.setF1Y(hzToIndex(data.getF1()));
					formantControler.setF2Y(hzToIndex(data.getF2()));
					
					resetData(data);
				
				}else{
					//reset default,maybe never called
					formantControler.setF1Y(hzToIndex(500));
					formantControler.setF2Y(hzToIndex(1500));
				}
				repaintBoth();
				
			}
		});
		bottoms.add(resetBt);
	
		
		resetBt.setEnabled(false);
		updateBt.setEnabled(false);
		
		final HorizontalPanel downloadLinks=new HorizontalPanel();
		
		Button image=new Button(textConstants.generate_image(),new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				generateFormantMapImage();
			}
		});
		bottoms.add(image);
		
		Button download=new Button(textConstants.generate_csv(),new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				downloadLinks.clear();
				
				Anchor anchor=HTML5Download.get().generateTextDownloadLink(getStoreFormantText(), "formant9map.csv", textConstants.download_csv(),true);
				downloadLinks.add(anchor);
				
			}
		});
		bottoms.add(download);
		bottoms.add(downloadLinks);
		
		
	
		
	}
	
	protected void repaintBoth() {
		targetCanvasIndex=0;
		repaint();
		targetCanvasIndex=1;
		repaint();
		targetCanvasIndex=0;
	}
	private ArrayBuffer lastArrayBuffer;
	int f1Canvas=600;
	int f2Canvas=400;
	protected void generateFormantMapImage() {
		imagePanel.clear();
		imageCanvasMarginLeft = 40;
		int marginRight=20;
		imageCanvasMarginTop = 20;
		int marginBottom=20;
		
		Canvas canvas=CanvasUtils.createCanvas(f1Canvas+imageCanvasMarginLeft+marginRight, f2Canvas+imageCanvasMarginTop+marginBottom);
		
		//draw background
		Context2d context=canvas.getContext2d();
		context.setFillStyle(settingPanel.getBackgroundColor());
		context.fillRect(0,0,canvas.getCoordinateSpaceWidth(),canvas.getCoordinateSpaceHeight());
		context.setFillStyle(settingPanel.getInsideColor());
		context.fillRect(imageCanvasMarginLeft, imageCanvasMarginTop, f1Canvas, f2Canvas);
		
		
		
		//draw grid
		context.setStrokeStyle(settingPanel.getMemoryLineColor());
		canvas.getContext2d().setFillStyle("#000");
		canvas.getContext2d().setFont("12px Courier");
		for(int i=-2;i<=2;i++){
		int xplus=f1Canvas/4;
		int yplus=f2Canvas/4;
		CanvasUtils.drawLine(canvas, f1Canvas/2+xplus*i+imageCanvasMarginLeft, 0+imageCanvasMarginTop, f1Canvas/2+xplus*i+imageCanvasMarginLeft, f2Canvas+imageCanvasMarginTop);
		CanvasUtils.drawLine(canvas, imageCanvasMarginLeft, f2Canvas/2+yplus*i+imageCanvasMarginTop, f1Canvas+imageCanvasMarginLeft, f2Canvas/2+yplus*i+imageCanvasMarginTop);
		int vf2=(getEndF2()-getStartF2())/2;
		int vf2plus=-vf2/2;
		vf2+=getStartF2();
		int vf1=(getEndF1()-getStartF1())/2;
		int vf1plus=vf1/2;
		vf1+=getStartF1();
		int offleft=i==-2?0:-10;
		int offright=i==2?4:0;
		canvas.getContext2d().fillText(""+(vf1+vf1plus*i), f1Canvas/2+xplus*i+imageCanvasMarginLeft+offleft, f2Canvas+imageCanvasMarginTop+14);
		canvas.getContext2d().fillText(""+(vf2+vf2plus*i), 6+offright,f2Canvas/2+yplus*i+imageCanvasMarginTop);
		}
		canvas.getContext2d().setFillStyle("#003366");
		context.setFont("bold 16px Courier");
		context.fillText("F1", imageCanvasMarginLeft+60, imageCanvasMarginTop+f2Canvas+16);
		context.fillText("F2", 10, imageCanvasMarginTop+f2Canvas-40);
		//draw titles
		
		//draw border
		context.setStrokeStyle("#000000");
		canvas.getContext2d().setLineWidth(1);
		context.strokeRect(imageCanvasMarginLeft, imageCanvasMarginTop, f1Canvas, f2Canvas);
		
		int rsize=4;
		canvas.getContext2d().setFont(settingPanel.getValueFont());
		
		
		for(FormantData data:formants){
			
			int x=canvasAtF1(data.getF1());
			int y=canvasAtF2(data.getF2());
			LogUtils.log(data+",x="+x+",y="+y);
			if(settingPanel.isShowImageBaseFormant()){
				BaseFormantData defaultData=data.getBaseData();
				
				int xd=canvasAtF1(defaultData.getF1());
				int yd=canvasAtF2(defaultData.getF2());
				
				LogUtils.log(defaultData+",x="+xd+",y="+yd);
				
				canvas.getContext2d().setFillStyle(settingPanel.getBaseRectColor());
				canvas.getContext2d().fillRect(xd-rsize/2, yd-rsize/2, rsize, rsize);
				
				canvas.getContext2d().setStrokeStyle("#888");
				CanvasUtils.drawLine(canvas, xd, yd, x, y);
			}
			
			
			
			
			
			
			
			
			
			
			
			canvas.getContext2d().setFillStyle(settingPanel.getValueRectColor());
			
			canvas.getContext2d().fillRect(x-rsize/2, y-rsize/2, rsize, rsize);
			canvas.getContext2d().setFillStyle(settingPanel.getTextColor());
			canvas.getContext2d().fillText(data.getName(), x+4, y);
			
			
			
		}
		
		Image image=new Image(canvas.toDataUrl());
		imagePanel.add(image);
	}
	

	//int startF1=100;
	//int endF1=1300;
	private int canvasAtF1(int f1){
		//0-1000
		return ((f1-getStartF1())*f1Canvas/(getEndF1()-getStartF1()))+imageCanvasMarginLeft;
		//return f1Canvas-(f1*f1Canvas/1000);
	}
	//int startF2=400;
	//int endF2=3200;
	
	public int getStartF1(){
		return settingPanel.getF1Min();
	}
	public int getEndF1(){
		return settingPanel.getF1Max();
	}
	
	public int getStartF2(){
		return settingPanel.getF2Min();
	}
	public int getEndF2(){
		return settingPanel.getF2Max();
	}
	private int canvasAtF2(int f2){
		//500-2500
		return f2Canvas-((f2-getStartF2())*f2Canvas/(getEndF2()-getStartF2()))+imageCanvasMarginTop;
	}
	
	public static final String KEY_FORMANT_9MAP="key_formant_9map";
	StorageControler storageControler=new StorageControler();
	
	public class FormantConverter extends Converter<FormantData,String>{

		@Override
		protected String doForward(FormantData a) {
			// TODO Auto-generated method stub
			return a.getName()+"\t"+a.getF1()+"\t"+a.getF2();
		}

		@Override
		protected FormantData doBackward(String line) {
			String[] values=line.split("\t");
			String name=values[0];
			int f1=500;
			int f2=1500;
			if(values.length>1){
				f1=ValuesUtils.toInt(values[1], 500);
			}
			if(values.length>2){
				f2=ValuesUtils.toInt(values[2], 1500);
			}
			return new FormantData(name,f1,f2);
		}
		
	}
	
	private String getStoreFormantText(){
		
		List<String> lines=FluentIterable.from(formants).transform(new FormantConverter()).toList();
		return Joiner.on("\r\n").join(lines);
	}
	
	
	protected void storeData(FormantData data) {
		try {
			storageControler.setValue(KEY_SETTING_FORMANT_HEADER+data.getName(),new FormantConverter().convert(data) );
		} catch (StorageException e) {
			Window.alert(e.getMessage());
		}
	}
	protected void resetData(FormantData data) {
		storageControler.removeValue(KEY_SETTING_FORMANT_HEADER+data.getName());
	}

	private void analyzeWave(ArrayBuffer arrayBuffer){
		
		
		Buffer buffer=audioContext.createBuffer(arrayBuffer, false);
		
		source=audioContext.createBufferSource();
		LogUtils.log(source);
		source.setBuffer(buffer);
		
		
		//stream.connect(audioContext.getDestination());
		
		
		
		
		source.connect(analyser);
		
		source.start(0);
		LogUtils.log("status:"+source.getPlaybackState());
		
		initDraw();
	}
	
	private void loadWav() {
		
		XMLHttpRequest request=XMLHttpRequest.create();
		request.setResponseType(ResponseType.ArrayBuffer);
		request.setOnReadyStateChange(new ReadyStateChangeHandler() {
			@Override
			public void onReadyStateChange(XMLHttpRequest xhr) {
				if(xhr.getResponseArrayBuffer()==null){//pre loading
					return;
				}
				LogUtils.log(xhr.getStatusText());
				ArrayBuffer arrayBufer=xhr.getResponseArrayBuffer();
				LogUtils.log(arrayBufer);
				analyzeWave(arrayBufer);
				
			}
		});
		request.open("GET","/test.wav");
		request.send();
	}

	private int[] toColor(int v){
		int[] rgb=new int[3];
		double phase = (double)v/255;
		double shift =Math.PI+Math.PI/4;
		 rgb[0]=(int) (255*(Math.sin(1.5*Math.PI*phase + shift + Math.PI ) + 1)/2.0) ;
		 rgb[1]=(int) (255*(Math.sin(1.5*Math.PI*phase + shift + Math.PI/2 ) + 1)/2.0) ;
		 rgb[2]=(int) (255*(Math.sin(1.5*Math.PI*phase + shift  ) + 1)/2.0) ;
		return rgb;
	}
	
	protected void drawCanvas() {
		
		Canvas canvas=canvasList.get(targetCanvasIndex).getCanvas();
		
		CanvasUtils.clear(canvas);
		int x=0;
		ImageData data=canvas.getContext2d().createImageData(canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());
		
		//List<Integer> f1s=new ArrayList<Integer>();
		
		//something trying analyze
		//int minF1=hzToIndex(200)/zoom;//pixel size is same
		//int maxF1=hzToIndex(1200)/zoom;
		
		
		for(Uint8Array array:arrays){
			//int f1=0;//something trying analyze
			//int f1Index=0; //something trying analyze
			if(x==0){
				LogUtils.log("array-size:"+array.length());
			}
			for(int i=0;i<array.length()&&i<512;i++){//max 512
				
				int y=512-1-i;
				int v=array.get(i);
				
				/*
				 * filter cause another problems
				if(v<64){
					continue;//low filter
				}
				*/
				
				/*//something trying analyze
				if(i<maxF1 && i>minF1){
					if(v>f1){
						f1=v;
						f1Index=i;
					}
				}
				*/
				
				int rgb[]=toColor(v);
				
				data.setAlphaAt(255, x,y);
				data.setRedAt(rgb[2], x,y);
				data.setGreenAt(rgb[1], x,y);
				data.setBlueAt(rgb[0], x,y);
				
				/*
				 * data.setRedAt(v, x,y);
				data.setGreenAt(v, x,y);
				data.setBlueAt(v, x,y);
				 */
			}
			
			//f1s.add(f1Index);////something trying analyze
			x++;
		}
		canvas.getContext2d().putImageData(data, 0, 0);
		
		
		
		/*
		canvas.getContext2d().beginPath();
		canvas.getContext2d().moveTo(0, 512);
		for(int i=0;i<f1s.size();i++){
			int at=f1s.get(i);
			int y=512-1-at;
			canvas.getContext2d().lineTo(i, y);
		}
		canvas.getContext2d().moveTo(512, 512);
		canvas.getContext2d().closePath();
		canvas.getContext2d().stroke();
		*/
		
		
		
		//zoom
		String url=canvas.toDataUrl();
		canvasList.get(targetCanvasIndex).setCanvasImage(ImageElementUtils.create(url));
		//draw 2xscale bassed on bottom
		
		repaint();
		
		targetCanvasIndex=0;//focus on main
	}
	
	//zoom should be getZoom()
	private void repaint(){
		Canvas canvas=canvasList.get(targetCanvasIndex).getCanvas();
		CanvasUtils.clear(canvas);
		ImageElement canvasImage=canvasList.get(targetCanvasIndex).getCanvasImage();
		if(canvasImage!=null){//width is fixed 2x
			canvas.getContext2d().drawImage(canvasImage, 0, (settingPanel.getZoom()-1)*-canvasImage.getHeight(),canvasImage.getWidth()*settingPanel.getZoom(),canvasImage.getHeight()*settingPanel.getZoom());
		}
		
		if(showFormant.getValue()){//ignore sub:1
			
			//draw f1
			canvas.getContext2d().setStrokeStyle("#f00");
			//int f1At=hzToIndex(500)*2;
			int f1At=formantControler.getF1Y();
			int fy1=512-f1At;
			CanvasUtils.drawLine(canvas,0,fy1,2048,fy1);
			
	//f2
	canvas.getContext2d().setStrokeStyle("#0f0");
	int f2At=formantControler.getF2Y();
	int fy2=512-f2At;
	CanvasUtils.drawLine(canvas,0,fy2,2048,fy2);

	//f3
	canvas.getContext2d().setStrokeStyle("#0ff");
	int f3At=hzToIndex(2500);
	int fy3=512-f3At;
	CanvasUtils.drawLine(canvas,0,fy3,2048,fy3);
	
		//FormantData formant=formantList.getValue();
	if(targetCanvasIndex==0){//no need on sub
		int f1=500;
		int f2=1500;
		f1=parseIndexHz(formantControler.getF1Y());
		f2=parseIndexHz(formantControler.getF2Y());
		
			canvas.getContext2d().setFont("18px Courier");
			canvas.getContext2d().setFillStyle("#0ff");
			canvas.getContext2d().fillText("F3:"+2500+"hz",20,20);
			canvas.getContext2d().setFillStyle("#0f0");
			canvas.getContext2d().fillText("F2:"+f2+"hz",20,40);
			canvas.getContext2d().setFillStyle("#f00");
			canvas.getContext2d().fillText("F1:"+f1+"hz",20,60);
	}
		
		}
		
		
		
	}

	long stime;
	Uint8Array array;
	protected void initDraw() {
		if(drawTimer!=null){
			drawTimer.cancel();
		}
		arrays.clear();
		stime=System.currentTimeMillis();
		
		drawTimer=new Timer(){
			@Override
			public void run() {
				updateDraw();
			}
		};
		drawTimer.scheduleRepeating(5);//5 is min?
	}

	private List<Uint8Array> arrays=new ArrayList<Uint8Array>();
	private int maxDrawTime=5000;// 1 sec use 200pixel 2000 is max
	public void updateDraw(){
		int state=source.getPlaybackState();
		if(state==1){
			LogUtils.log("preload");
			return;
		}
		boolean timeOver=false;
		long c=System.currentTimeMillis();
		long ctime=c-stime;
		if(ctime>maxDrawTime){
			timeOver=true;
		}
		
		//LogUtils.log(ctime);
		Uint8Array array=Uint8Array.createUint8(512*getQuality());
		analyser.getByteFrequencyData(array);
		arrays.add(array);
		//LogUtils.log("time:"+(System.currentTimeMillis()-c));
		
		//LogUtils.log("status:"+source.getPlaybackState());
		if(state==3 || timeOver){
			stopAnalyze();
		}
	}
	
	Timer drawTimer;
	Audio audio;
	private Analyser analyser;
	//private Canvas canvas;
	//private ImageElement canvasImage;
	private int targetCanvasIndex;
	private CheckBox showFormant;
	private Button record;
	private Button stop;
	private VerticalPanel downloadLinks;
	
	/**
	 * 
	 * why half works fine?because of stereo?
	 * 
	 */
	public int parseIndexHz(int index){
		return audioContext.getSampleRate()*index/analyser.getFftSize()/settingPanel.getZoom();
	}
	
	public int hzToIndex(int hz){
		return hz*analyser.getFftSize()*settingPanel.getZoom()/audioContext.getSampleRate();
	}
	
	
	
	private Supplier<Analyser> analyserSupplier=Suppliers.memoize(new Supplier<Analyser>() {
		@Override
		public Analyser get() {
			analyser = audioContext.createAnalyser();
			analyser.setSmoothingTimeConstant(0);
			analyser.setFftSize(1024);
			return analyser;
		}
	});
	//private List<FormantData> formantDefaultDatas;
	//private List<FormantData> formantDatas;
	private VerticalPanel imagePanel;
	private ValueListBox<FormantData> formantList;
	private Button resetBt;
	private Button updateBt;
	private int imageCanvasMarginTop;
	private int imageCanvasMarginLeft;
	
	protected void startUserMedia(LocalMediaStream loaclMediaStream) {
		this.localMediaStream=loaclMediaStream;
		
	}
	public void reserveResizeCanvas(int value) {
		//TODO implement
		//but virtual scroll is hard than expected.
	}
	public void changeQualityValue(int value) {
		analyser.setFftSize(1024*value);
	}
	public int getQuality(){
		return settingPanel.getQuality();
	}

}
