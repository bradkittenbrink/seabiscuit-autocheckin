package com.coffeeandpower.activity;

import java.util.ArrayList;

import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.R;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.cont.UserResume;
import com.coffeeandpower.utils.Executor;
import com.coffeeandpower.utils.Executor.ExecutorInterface;

public class ActivityJobCategory extends RootActivity {

	private View timePickerLayout;

	private ArrayWheelAdapter<String> jobAdapter;

	private boolean isMajorSelected;

	private String selectedMajorJob;
	private String selectedMinorJob;

	private DataHolder result;

	private Executor exe;

	private UserResume userResumeData;

	{
		isMajorSelected = false;
		selectedMajorJob = "";
		selectedMinorJob = "";
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_job_category);

		// Executor
		exe = new Executor(ActivityJobCategory.this);
		exe.setExecutorListener(new ExecutorInterface() {
			@Override
			public void onErrorReceived() {
				errorReceived();
			}

			@Override
			public void onActionFinished(int action) {
				actionFinished(action);
			}
		});

		getTimePickerLayout().setVisibility(View.GONE);

		// Kan Kan Wheel adapter
		final WheelView wheelView = (WheelView) findViewById(R.id.wheel_jobs);
		jobAdapter = new ArrayWheelAdapter<String>(this, new String[] { "engineering", "design", "marketing", "legal", "finance", "admin",
				"investor", "business development", "other" });

		jobAdapter.setItemResource(R.layout.wheel_text_item);
		wheelView.setViewAdapter(jobAdapter);
		wheelView.addScrollingListener(scrolledProvince);
	}

	/**
	 * Wheel scrolled listener for jobs adapter
	 */
	OnWheelScrollListener scrolledProvince = new OnWheelScrollListener() {
		public void onScrollingStarted(WheelView wheel) {
		}

		public void onScrollingFinished(WheelView wheel) {
			if (isMajorSelected) {
				selectedMajorJob = (String) jobAdapter.getItemText(wheel.getCurrentItem());
			} else {
				selectedMinorJob = (String) jobAdapter.getItemText(wheel.getCurrentItem());
			}
		}
	};

	private void errorReceived() {

	}

	private void actionFinished(int action) {
		result = exe.getResult();

		switch (action) {

		case Executor.HANDLE_SAVE_USER_JOB_CATEGORY:
			Toast.makeText(ActivityJobCategory.this, result.getResponseMessage(), Toast.LENGTH_SHORT).show();
			finish();
			break;

		case Executor.HANDLE_GET_USER_RESUME:
			if (result.getObject() != null) {
				if (result.getObject() instanceof ArrayList<?>) {
					ArrayList<Object> tempArray = (ArrayList<Object>) result.getObject();
					if (tempArray != null) {
						if (!tempArray.isEmpty()) {
							if (tempArray.get(0) instanceof UserResume) {
								userResumeData = (UserResume) tempArray.get(0);
								updateUserDataInUI();
							}
						}
					}
				}
			}
			break;
		}
	}

	private View getTimePickerLayout() {
		if (timePickerLayout == null) {
			timePickerLayout = findViewById(R.id.picker);
		}
		return timePickerLayout;
	}

	public void onClickMajor(View v) {
		getTimePickerLayout().setVisibility(View.VISIBLE);
		((TextView) findViewById(R.id.timepicker_title)).setText("Major Job Category");
		isMajorSelected = true;
	}

	public void onClickMinor(View v) {
		getTimePickerLayout().setVisibility(View.VISIBLE);
		((TextView) findViewById(R.id.timepicker_title)).setText("Minor Job Category");
		isMajorSelected = false;
	}

	public void onClickCancel(View v) {
		getTimePickerLayout().setVisibility(View.GONE);
	}

	public void onClickDone(View v) {
		if (isMajorSelected) {
			((Button) findViewById(R.id.button_major)).setText(selectedMajorJob);
		} else {
			((Button) findViewById(R.id.button_minor)).setText(selectedMinorJob);
		}
		getTimePickerLayout().setVisibility(View.GONE);
	}

	private void updateUserDataInUI() {
		if (userResumeData != null) {
			selectedMajorJob = userResumeData.getMajorJob();
			selectedMinorJob = userResumeData.getMinorJob();
			((Button) findViewById(R.id.button_major)).setText(userResumeData.getMajorJob());
			((Button) findViewById(R.id.button_minor)).setText(userResumeData.getMinorJob());
		}
	}

	private void uploadJobs() {
		if (selectedMajorJob.length() > 0 || selectedMinorJob.length() > 0) {
			exe.saveUserJobCategory(selectedMajorJob, selectedMinorJob);
		} else {
			finish();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		exe.getResumeForUserId(AppCAP.getLoggedInUserId());
	}

	public void onClickBack(View v) {
		uploadJobs();
	}

	@Override
	public void onBackPressed() {
		if (getTimePickerLayout().getVisibility() == View.VISIBLE) {
			getTimePickerLayout().setVisibility(View.GONE);
		} else {
			uploadJobs();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
