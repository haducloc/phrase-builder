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
import android.widget.ImageView;
import android.widget.TextView;

public class LabelEditDialog extends FormDialog {

	public static final String DIALOG_ARG_LABEL = "label";

	// Dialog Callback
	public interface Callbacks {

		void onDialogSaveLabel(Label label);

		void onDialogDeleteLabel(Label label);

		void onDialogCheckLabel(String sName, Integer labelId);
	}

	// Views
	LeoEditTextVal nameEditText;
	View saveBtn;

	public LabelEditDialog() {
	}

	@Override
	public void onDestroyView() {
		nameEditText.removeCallbacks();

		super.onDestroyView();
	}

	Label getUpdateLabel() {
		Bundle args = this.getArguments();
		if (args != null) {
			return (Label) args.getParcelable(DIALOG_ARG_LABEL);
		}
		return null;
	}

	void onCheckLabelResult(boolean exists) {
		if (exists) {
			nameEditText.setError(getString(R.string.message_error_label_exists));
		} else {
			nameEditText.setError(null);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View viewRoot = inflater.inflate(R.layout.label_edit_dialog, container, false);

		// nameEditText
		nameEditText = (LeoEditTextVal) viewRoot.findViewById(R.id.label_edit_dialog_name_edittext);
		nameEditText.setTempTextWatcher(new TextWatcherImpl() {

			@Override
			public void afterTextChanged(Editable s) {
				String sName = StringUtils.toSearchable(s.toString());
				if (sName.isEmpty() == false) {
					Label label = getUpdateLabel();
					((Callbacks) getActivity()).onDialogCheckLabel(sName, label != null ? label._id : null);
				} else {
					nameEditText.setError(null);
				}
			}
		});
		nameEditText.postAttachTextWatcherRunnable();

		// saveBtn
		saveBtn = viewRoot.findViewById(R.id.label_edit_dialog_save_button);
		saveBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Bool isValid = new Bool(true);
				Validator.validateRequired(nameEditText, R.string.message_error_field_required, isValid);

				if (isValid.getValue()) {
					Label label = getUpdateLabel();
					if (label == null) {
						label = new Label();
					}
					label.name = StringUtils.toLabel(nameEditText.getText().toString());
					label.s_name = StringUtils.toSearchable(label.name);

					((Callbacks) getActivity()).onDialogSaveLabel(label);
				}
			}
		});

		// cancelBtn
		View cancelBtn = viewRoot.findViewById(R.id.label_edit_dialog_cancel_button);
		cancelBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				LabelEditDialog.this.dismissAllowingStateLoss();
			}
		});

		// savedInstanceState
		if (savedInstanceState == null) {
			Label label = getUpdateLabel();
			if (label != null) {
				nameEditText.setText(label.name);
			}
		}
		return viewRoot;
	}

	@Override
	protected void initRightAction(ImageView actionView) {
		final Label label = getUpdateLabel();
		if (label != null) {

			actionView.setImageResource(R.drawable.ic_btn_delete_action);
			actionView.setVisibility(View.VISIBLE);
			actionView.setContentDescription(getString(R.string.desc_delete));
			actionView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					((Callbacks) getActivity()).onDialogDeleteLabel(label);
				}
			});
		} else {
			actionView.setVisibility(View.GONE);
		}
	}

	@Override
	protected void initTitleView(TextView titleTextView) {
		if (getUpdateLabel() == null) {
			titleTextView.setText(R.string.title_new_label);
		} else {
			titleTextView.setText(R.string.title_edit_label);
		}
	}
}
