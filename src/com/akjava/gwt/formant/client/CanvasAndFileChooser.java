package com.akjava.gwt.formant.client;

import com.akjava.gwt.html5.client.download.HTML5Download;
import com.akjava.gwt.html5.client.file.Blob;
import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataArrayListener;
import com.akjava.gwt.html5.client.file.Uint8Array;
import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.GWTUtils;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.media.client.Audio;
import com.google.gwt.typedarrays.shared.ArrayBuffer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

public class CanvasAndFileChooser extends DockLayoutPanel{
private Canvas canvas;
public ArrayBufferUploadedListener getListener() {
	return listener;
}

public void setListener(ArrayBufferUploadedListener listener) {
	this.listener = listener;
}

public Canvas getCanvas() {
	return canvas;
}

public ArrayBuffer getArrayBuffer() {
	return arrayBuffer;
}

protected ArrayBuffer arrayBuffer;
private ArrayBufferUploadedListener listener;
private FileUploadForm upload;
private HorizontalPanel bottoms;
private ImageElement canvasImage;
	public ImageElement getCanvasImage() {
	return canvasImage;
}

public void setCanvasImage(ImageElement canvasImage) {
	this.canvasImage = canvasImage;
}

	public HorizontalPanel getBottoms() {
	return bottoms;
}

	public CanvasAndFileChooser(){
		super(Unit.PX);
		//center
		ScrollPanel scroll=new ScrollPanel();
		
		
		canvas = CanvasUtils.createCanvas(2048, 512);
		canvas.setStylePrimaryName("blackbg");
		scroll.add(canvas);
		
		
		bottoms = new HorizontalPanel();
		addNorth(bottoms,30);//need close to permisssion
		
		upload = FileUtils.createSingleFileUploadForm(
				new DataArrayListener() {
					@Override
					public void uploaded(File file, Uint8Array array) {
						arrayBuffer=array.getBuffer();
						if(listener!=null){
							listener.uploaded(arrayBuffer);
						}
						//analyzeWave(lastArrayBuffer);
					}
				}
				,false,true);
		bottoms.add(upload);
		
		Button playFileBt=new Button(GWTFormant.textConstants.play(),new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				doPlay();
			}
		});
		playFileBt.setWidth("60px");
		if(!GWTUtils.isIAndroid()){
			bottoms.add(playFileBt);//android can't play so far
		}
		
		
		add(scroll);//add main last
	}
	
	public static interface ArrayBufferUploadedListener{
		public void uploaded(ArrayBuffer buffer);
	}
	public void reset(){
		arrayBuffer=null;
		upload.reset();
	}
	
	public void doPlay(){
		if(arrayBuffer!=null){
			Audio audio=Audio.createIfSupported();
			Blob blob=Blob.createBlob(arrayBuffer);
			audio.addSource(HTML5Download.get().URL().createObjectURL(blob));
			audio.play();
			}
	}

	public void copyTo(CanvasAndFileChooser canvasAndFileChooser) {
		canvasAndFileChooser.arrayBuffer=this.getArrayBuffer();
		canvasAndFileChooser.canvasImage=this.getCanvasImage();
	}
}
