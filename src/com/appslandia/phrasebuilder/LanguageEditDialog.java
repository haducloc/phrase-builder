package com.appslandia.phrasebuilder;

import com.appslandia.core.utils.Bool;
import com.appslandia.core.utils.StringUtils;
import com.appslandia.core.utils.Validator;
import com.appslandia.core.views.FormDialog;
import com.appslandia.core.views.LeoEditTextVal;
import com.appslandia.core.views.TextWatcherImpl;
import com.appslandia.phrasebuilder.entities.Language;

import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class LanguageEditDialog extends FormDialog {

	public static final String DIALOG_ARG_LANGUAGE = "language";

	// Dialog Callback
	public interface Callbacks {

		void onDialogSaveLanguage(Language language);

		void onDialogDeleteLanguage(Language language);

		void onDialogCheckLanguage(String sName, Integer languageId);
	}

	// Views
	LeoEditTextVal nameEditText;
	View saveBtn;

	public LanguageEditDialog() {
	}

	@Override
	public void onDestroyView() {
		nameEditText.removeCallbacks();

		super.onDestroyView();
	}

	Language getUpdateLanguage() {
		Bundle args = this.getArguments();
		if (args != null) {
			return (Language) args.getParcelable(DIALOG_ARG_LANGUAGE);
		}
		return null;
	}

	void onCheckLanguageResult(boolean exists) {
		if (exists) {
			nameEditText.setError(getString(R.string.message_error_language_exists));
		} else {
			nameEditText.setError(null);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View viewRoot = inflater.inflate(R.layout.language_edit_dialog, container, false);

		// nameEditText
		nameEditText = (LeoEditTextVal) viewRoot.findViewById(R.id.language_edit_dialog_name_edittext);
		nameEditText.setTempTextWatcher(new TextWatcherImpl() {

			@Override
			public void afterTextChanged(Editable s) {
				String name = s.toString().trim();
				if (name.isEmpty() == false) {
					Language language = getUpdateLanguage();
					((Callbacks) getActivity()).onDialogCheckLanguage(name, language != null ? language._id : null);
				} else {
					nameEditText.setError(null);
				}
			}
		});
		nameEditText.postAttachTextWatcherRunnable();

		// saveBtn
		saveBtn = viewRoot.findViewById(R.id.language_edit_dialog_save_button);
		saveBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Bool isValid = new Bool(true);
				Validator.validateRequired(nameEditText, R.string.message_error_field_required, isValid);

				if (isValid.getValue()) {
					Language language = getUpdateLanguage();
					if (language == null) {
						language = new Language();
					}
					language.name = StringUtils.toText(nameEditText.getText().toString(), true);

					((Callbacks) getActivity()).onDialogSaveLanguage(language);
				}
			}
		});

		// cancelBtn
		View cancelBtn = viewRoot.findViewById(R.id.language_edit_dialog_cancel_button);
		cancelBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				LanguageEditDialog.this.dismissAllowingStateLoss();
			}
		});

		// savedInstanceState
		if (savedInstanceState == null) {
			Language language = getUpdateLanguage();
			if (language != null) {
				nameEditText.setText(language.name);
			}
		}
		return viewRoot;
	}

	@Override
	protected void initRightAction(ImageView actionView) {
		final Language language = getUpdateLanguage();
		if (language != null) {

			actionView.setImageResource(R.drawable.ic_btn_delete_action);
			actionView.setVisibility(View.VISIBLE);
			actionView.setContentDescription(getString(R.string.desc_delete));
			actionView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					((Callbacks) getActivity()).onDialogDeleteLanguage(language);
				}
			});
		} else {
			actionView.setVisibility(View.GONE);
		}
	}

	@Override
	protected void initTitleView(TextView titleTextView) {
		if (getUpdateLanguage() == null) {
			titleTextView.setText(R.string.title_new_language);
		} else {
			titleTextView.setText(R.string.title_edit_language);
		}
	}
}
