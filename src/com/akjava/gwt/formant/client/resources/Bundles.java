package com.akjava.gwt.formant.client.resources;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

public interface Bundles extends ClientBundle {
	public static Bundles INSTANCE=GWT.create(Bundles.class);
	TextResource canadian();
	TextResource average();
}
