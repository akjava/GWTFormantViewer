package com.akjava.gwt.formant.client;

import java.util.ArrayList;
import java.util.List;

import com.akjava.gwt.formant.client.BaseFormantDataConverter.BaseFormantData;
import com.akjava.lib.common.utils.CSVUtils;
import com.akjava.lib.common.utils.ValuesUtils;
import com.google.common.base.Converter;
import com.google.common.base.Joiner;

public class BaseFormantDataConverter extends Converter<List<BaseFormantData>,String>{

	public static class BaseFormantData{
		private String name;
		public BaseFormantData(String name, int f1, int f2) {
			super();
			this.name = name;
			this.f1 = f1;
			this.f2 = f2;
		}
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
		private int f1;
		private int f2;
		
		public String toString(){
			return name+","+f1+","+f2;
		}
	}

	@Override
	protected String doForward(List<BaseFormantData> datas) {
		List<String> lines=new ArrayList<String>();
		for(BaseFormantData data:datas){
			lines.add(data.getName()+"\t"+data.getF1()+"\t"+data.getF2());
		}
		return Joiner.on("\r\n").join(lines);
	}

	@Override
	protected List<BaseFormantData> doBackward(String text) {
		List<String[]> vs=CSVUtils.csvTextToArrayList(text, '\t');
		List<BaseFormantData> datas=new ArrayList<BaseFormantDataConverter.BaseFormantData>();
		for(String[] csv:vs){
			if(csv.length==3){
				int f1=ValuesUtils.toInt(csv[1], 0);
				int f2=ValuesUtils.toInt(csv[2], 0);
				datas.add(new BaseFormantData(csv[0], f1, f2));
			}
		}
		return datas;
	}


}
