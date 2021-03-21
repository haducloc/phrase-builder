package com.appslandia.phrasebuilder;

import com.appslandia.core.utils.Bool;
import com.appslandia.core.utils.StringUtils;
import com.appslandia.core.utils.Validator;
import com.appslandia.core.views.FormDialog;
import com.appslandia.core.views.LeoEditTextVal;
import com.appslandia.core.views.TextWatcherImpl;
import com.appslandia.phrasebuilder.entities.Label;

import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class LabelEditDialog2 extends FormDialog {

	private static boolean applyLabelChecked = true;

	// Transients
	public static final String STATE_APPLY_LABEL_CHECKED = "applyLabelChecked";

	// Dialog Callback
	public interface Callbacks {

		void onDialogSaveLabel(Label label, boolean tagLabel);

		void onDialogCheckLabel(String sName);
	}

	// Views
	LeoEditTextVal nameEditText;
	CheckBox applyLabelCheckbox;
	View saveBtn;

	public LabelEditDialog2() {
	}

	@Override
	public void onDestroyView() {
		nameEditText.removeCallbacks();

		super.onDestroyView();
	}

	void onCheckLabelResult(boolean exists) {
		if (exists) {
			nameEditText.setError(getString(R.string.message_error_label_exists));
		} else {
			nameEditText.setError(null);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putBoolean(STATE_APPLY_LABEL_CHECKED, applyLabelCheckbox.isChecked());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View viewRoot = inflater.inflate(R.layout.label_edit_dialog2, container, false);

		// nameEditText
		nameEditText = (LeoEditTextVal) viewRoot.findViewById(R.id.label_edit_dialog_name_edittext);
		nameEditText.setTempTextWatcher(new TextWatcherImpl() {

			@Override
			public void afterTextChanged(Editable s) {
				String sName = StringUtils.toSearchable(s.toString());
				if (sName.isEmpty() == false) {
					((Callbacks) getActivity()).onDialogCheckLabel(sName);
				} else {
					nameEditText.setError(null);
				}
			}
		});
		nameEditText.postAttachTextWatcherRunnable();

		// applyLabelCheckbox
		boolean isChecked = (savedInstanceState != null) ? savedInstanceState.getBoolean(STATE_APPLY_LABEL_CHECKED) : LabelEditDialog2.applyLabelChecked;
		applyLabelCheckbox = (CheckBox) viewRoot.findViewById(R.id.label_edit_dialog_tag_label_checkbox);
		applyLabelCheckbox.setChecked(isChecked);

		// saveBtn
		saveBtn = viewRoot.findViewById(R.id.label_edit_dialog_save_button);
		saveBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Bool isValid = new Bool(true);
				Validator.validateRequired(nameEditText, R.string.message_error_field_required, isValid);

				if (isValid.getValue()) {
					Label label = new Label();

					label.name = StringUtils.toLabel(nameEditText.getText().toString());
					label.s_name = StringUtils.toSearchable(label.name);

					// Remember applyLabelCheckbox
					LabelEditDialog2.applyLabelChecked = applyLabelCheckbox.isChecked();

					((Callbacks) getActivity()).onDialogSaveLabel(label, applyLabelCheckbox.isChecked());
				}
			}
		});

		// cancelBtn
		View cancelBtn = viewRoot.findViewById(R.id.label_edit_dialog_cancel_button);
		cancelBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				LabelEditDialog2.this.dismissAllowingStateLoss();
			}
		});

		return viewRoot;
	}

	@Override
	protected void initRightAction(ImageView actionView) {
		actionView.setVisibility(View.GONE);
	}

	@Override
	protected void initTitleView(TextView titleTextView) {
		titleTextView.setText(R.string.title_new_label);
	}
}
