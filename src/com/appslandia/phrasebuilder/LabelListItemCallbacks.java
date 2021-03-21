package com.appslandia.phrasebuilder;

import com.appslandia.phrasebuilder.entities.Label;

public interface LabelListItemCallbacks {

	void onAdapterClickLabel(Label Label);

	void onAdapterDeleteLabel(Label label);

	void onAdapterTestLabel(Label Label);
}
