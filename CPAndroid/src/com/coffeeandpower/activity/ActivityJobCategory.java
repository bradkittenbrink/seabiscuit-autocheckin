package com.coffeeandpower.activity;

import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.R;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.views.CustomDialog;

public class ActivityJobCategory extends RootActivity
{

	public static final int HANDLE_UPLOAD_JOBS_INFO = 1500;

	private View timePickerLayout;

	private ArrayWheelAdapter<String> jobAdapter;

	private boolean isMajorSelected;

	private String selectedMajorJob;
	private String selectedMinorJob;

	private DataHolder result;

	private ProgressDialog progress;

	{
		isMajorSelected = false;
		selectedMajorJob = "";
		selectedMinorJob = "";
	}

	private Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);

			progress.dismiss();

			switch (msg.what)
			{
			case AppCAP.HTTP_ERROR:
				new CustomDialog(ActivityJobCategory.this, "Error", result.getResponseMessage()).show();
				break;

			case HANDLE_UPLOAD_JOBS_INFO:
				Toast.makeText(ActivityJobCategory.this, result.getResponseMessage(), Toast.LENGTH_SHORT).show();
				break;
			}

			// Finish Activity after upload data
			finish();
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_job_category);

		getTimePickerLayout().setVisibility(View.GONE);
		progress = new ProgressDialog(this);
		progress.setMessage("Uploading...");

		// Kan Kan Wheel adapter
		final WheelView wheelView = (WheelView) findViewById(R.id.wheel_jobs);
		jobAdapter = new ArrayWheelAdapter<String>(this, new String[]
		{ "engineering", "design", "marketing", "legal", "finance", "admin", "investor", "business development",
				"other" });

		jobAdapter.setItemResource(R.layout.wheel_text_item);
		wheelView.setViewAdapter(jobAdapter);
		wheelView.addScrollingListener(scrolledProvince);
	}

	/**
	 * Wheel scrolled listener for jobs adapter
	 */
	OnWheelScrollListener scrolledProvince = new OnWheelScrollListener()
	{
		public void onScrollingStarted(WheelView wheel)
		{
		}

		public void onScrollingFinished(WheelView wheel)
		{
			if (isMajorSelected)
			{
				selectedMajorJob = (String) jobAdapter.getItemText(wheel.getCurrentItem());
			}
			else
			{
				selectedMinorJob = (String) jobAdapter.getItemText(wheel.getCurrentItem());
			}
		}
	};

	private View getTimePickerLayout()
	{
		if (timePickerLayout == null)
		{
			timePickerLayout = findViewById(R.id.picker);
		}
		return timePickerLayout;
	}

	public void onClickMajor(View v)
	{
		getTimePickerLayout().setVisibility(View.VISIBLE);
		((TextView) findViewById(R.id.timepicker_title)).setText("Major Job Category");
		isMajorSelected = true;
	}

	public void onClickMinor(View v)
	{
		getTimePickerLayout().setVisibility(View.VISIBLE);
		((TextView) findViewById(R.id.timepicker_title)).setText("Minor Job Category");
		isMajorSelected = false;
	}

	public void onClickCancel(View v)
	{
		getTimePickerLayout().setVisibility(View.GONE);
	}

	public void onClickDone(View v)
	{
		if (isMajorSelected)
		{
			((Button) findViewById(R.id.button_major)).setText(selectedMajorJob);
		}
		else
		{
			((Button) findViewById(R.id.button_minor)).setText(selectedMinorJob);
		}
		getTimePickerLayout().setVisibility(View.GONE);
	}

	private void uploadJobs()
	{

		if (selectedMajorJob.length() > 0 || selectedMinorJob.length() > 0)
		{
			progress.show();
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					result = AppCAP.getConnection().saveUserJobCategory(selectedMajorJob, selectedMinorJob);
					handler.sendEmptyMessage(result.getResponseCode());
				}
			}).start();
		}
		else
		{
			finish();
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();
	}

	public void onClickBack(View v)
	{
		uploadJobs();
	}

	@Override
	public void onBackPressed()
	{
		if (getTimePickerLayout().getVisibility() == View.VISIBLE)
		{
			getTimePickerLayout().setVisibility(View.GONE);
		}
		else
		{
			uploadJobs();
		}
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}

}
