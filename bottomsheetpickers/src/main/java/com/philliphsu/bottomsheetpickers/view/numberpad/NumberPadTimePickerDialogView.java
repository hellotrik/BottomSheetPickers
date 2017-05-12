package com.philliphsu.bottomsheetpickers.view.numberpad;

import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TimePicker;

import com.philliphsu.bottomsheetpickers.view.LocaleModel;

import static com.philliphsu.bottomsheetpickers.view.Preconditions.checkNotNull;

final class NumberPadTimePickerDialogView implements INumberPadTimePicker.DialogView {
    private static final String KEY_DIGITS = "digits";
    // TODO: Why do we need the count?
    private static final String KEY_COUNT = "count";
    // TODO: Rename to KEY_HALF_DAY = "half_day" if the AmPmState annotation is renamed to HalfDay.
    private static final String KEY_AM_PM_STATE = "am_pm_state";

    private final @NonNull DialogInterface mDialogInterface;
    private final @NonNull NumberPadTimePicker mTimePicker;
    private final @Nullable OnTimeSetListener mTimeSetListener;
    private final INumberPadTimePicker.DialogPresenter mPresenter;
    // Dummy TimePicker Passed to onTimeSet() callback.
    private final TimePicker mDummy;

    private View mOkButton;

    NumberPadTimePickerDialogView(@NonNull DialogInterface dialogInterface, @NonNull Context context,
                                  @NonNull NumberPadTimePicker timePicker, @Nullable View okButton,
                                  @Nullable OnTimeSetListener listener, boolean is24HourMode) {
        mDialogInterface = checkNotNull(dialogInterface);
        mTimePicker = checkNotNull(timePicker);
        mOkButton = okButton;
        mTimeSetListener = listener;
        mDummy = new TimePicker(context);

        // TODO: If this model is needed by other classes, make it a singleton.
        final LocaleModel localeModel = new LocaleModel(context);
        mPresenter = new NumberPadTimePickerDialogPresenter(this, localeModel, is24HourMode);

        final OnBackspaceClickHandler backspaceClickHandler = new OnBackspaceClickHandler(mPresenter);
        mTimePicker.setOnBackspaceClickListener(backspaceClickHandler);
        mTimePicker.setOnBackspaceLongClickListener(backspaceClickHandler);
        mTimePicker.setOnNumberKeyClickListener(new OnNumberKeyClickListener(mPresenter));
        mTimePicker.setOnAltKeyClickListener(new OnAltKeyClickListener(mPresenter));
    }

    @Override
    public void setNumberKeysEnabled(int start, int end) {
        mTimePicker.setNumberKeysEnabled(start, end);
    }

    @Override
    public void setBackspaceEnabled(boolean enabled) {
        mTimePicker.setBackspaceEnabled(enabled);
    }

    @Override
    public void updateTimeDisplay(CharSequence time) {
        mTimePicker.updateTimeDisplay(time);
    }

    @Override
    public void updateAmPmDisplay(CharSequence ampm) {
        mTimePicker.updateAmPmDisplay(ampm);
    }

    @Override
    public void setOkButtonEnabled(boolean enabled) {
        mOkButton.setEnabled(enabled);
    }

    @Override
    public void setAmPmDisplayVisible(boolean visible) {
        mTimePicker.setAmPmDisplayVisible(visible);
    }

    @Override
    public void setAmPmDisplayIndex(int index) {
        mTimePicker.setAmPmDisplayIndex(index);
    }

    @Override
    public void setLeftAltKeyText(CharSequence text) {
        mTimePicker.setLeftAltKeyText(text);
    }

    @Override
    public void setRightAltKeyText(CharSequence text) {
        mTimePicker.setRightAltKeyText(text);
    }

    @Override
    public void setLeftAltKeyEnabled(boolean enabled) {
        mTimePicker.setLeftAltKeyEnabled(enabled);
    }

    @Override
    public void setRightAltKeyEnabled(boolean enabled) {
        mTimePicker.setRightAltKeyEnabled(enabled);
    }

    @Override
    public void setHeaderDisplayFocused(boolean focused) {
        mTimePicker.setHeaderDisplayFocused(focused);
    }

    @Override
    public void setResult(int hour, int minute) {
        if (mTimeSetListener != null) {
            mTimeSetListener.onTimeSet(mDummy, hour, minute);
        }
    }

    @Override
    public void cancel() {
        mDialogInterface.cancel();
    }

    void onCreate(@Nullable Bundle savedInstanceState) {
        mPresenter.onCreate(readStateFromBundle(savedInstanceState));
    }

    @NonNull
    Bundle onSaveInstanceState(@NonNull Bundle bundle) {
        final INumberPadTimePicker.State state = mPresenter.getState();
        bundle.putIntArray(KEY_DIGITS, state.getDigits());
        // TODO: Why do we need the count?
        bundle.putInt(KEY_COUNT, state.getCount());
        bundle.putInt(KEY_AM_PM_STATE, state.getAmPmState());
        return bundle;
    }

    void onStop() {
        mPresenter.onStop();
    }

    INumberPadTimePicker.DialogPresenter getPresenter() {
        return mPresenter;
    }

    /**
     * Workaround for situations when the 'ok' button is not
     * guaranteed to be available at the time of construction.
     * <p>
     * e.g. {@code AlertDialog}
     */
    void setOkButton(@NonNull View okButton) {
        mOkButton = checkNotNull(okButton);
    }

    @NonNull
    private static INumberPadTimePicker.State readStateFromBundle(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            final int[] digits = savedInstanceState.getIntArray(KEY_DIGITS);
            // TODO: Why do we need the count?
            final int count = savedInstanceState.getInt(KEY_COUNT);
            final @AmPmStates.AmPmState int amPmState = savedInstanceState.getInt(KEY_AM_PM_STATE);
            return new NumberPadTimePickerState(digits, count, amPmState);
        } else {
            return NumberPadTimePickerState.EMPTY;
        }
    }
}
