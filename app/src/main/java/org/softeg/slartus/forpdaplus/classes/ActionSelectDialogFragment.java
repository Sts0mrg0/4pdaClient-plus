package org.softeg.slartus.forpdaplus.classes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.softeg.slartus.forpdaplus.App;
import org.softeg.slartus.forpdaplus.R;

import java.util.Arrays;

/**
 * Created by radiationx on 16.02.16.
 */
public class ActionSelectDialogFragment {


    public interface OkListener {
        public void execute(CharSequence value);
    }

    public static void showSaveNavigateActionDialog(final Context context,
                                                    final String preferenceKey,
                                                    final String selectedAction,
                                                    final Runnable showTopicAction) {
        final String navigateAction = App.getInstance().getPreferences()
                .getString(preferenceKey, null);
        if (navigateAction == null || !selectedAction.equals(navigateAction)) {
            showSelectDialog(context, preferenceKey, selectedAction, showTopicAction);
        } else {
            showTopicAction.run();
        }

    }

    public static void showSelectDialog(final Context context,
                                        final String preferenceKey,
                                        final String selectedAction,
                                        final Runnable showTopicAction) {
        new MaterialDialog.Builder(context)
                .content(R.string.assign_default)
                .positiveText(R.string.yes)
                .negativeText(R.string.no)
                .neutralText(R.string.ask)
                .onPositive((dialog, which) -> {
                    App.getInstance().getPreferences()
                            .edit().putString(preferenceKey, selectedAction).apply();

                    showTopicAction.run();
                })
                .onNegative((dialog, which) -> showTopicAction.run())
                .onNeutral((dialog, which) -> {
                    App.getInstance().getPreferences()
                            .edit().putString(preferenceKey, null).apply();

                    showTopicAction.run();
                })
                .show();
    }

    public static void execute(final Context context,
                               final String title,
                               final String preferenceKey,
                               CharSequence[] titles,
                               final CharSequence[] values,
                               final OkListener okListener,
                               final String hintForChangeDefault) {
        final String value = App.getInstance().getPreferences().getString(preferenceKey, null);

        if (Arrays.asList(values).contains(value)) {
            okListener.execute(value);
            return;
        }


        final int[] selectedValue = {0};
        new MaterialDialog.Builder(context)
                .title(title)
                .items(titles)
                .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        selectedValue[0] = which;
                        return true;
                    }
                })
                .alwaysCallSingleChoiceCallback()
                .positiveText(R.string.always)
                .neutralText(R.string.only_now)
                .onPositive((dialog, which) -> {
                    final CharSequence newValue = values[selectedValue[0]];
                    App.getInstance().getPreferences()
                            .edit()
                            .putString(preferenceKey, newValue.toString())
                            .apply();

                    if (!TextUtils.isEmpty(hintForChangeDefault))
                        new MaterialDialog.Builder(context)
                                .title(R.string.hint)
                                .content(hintForChangeDefault)
                                .cancelable(false)
                                .positiveText(R.string.ok)
                                .onPositive((dialog1, which1) -> okListener.execute(newValue))
                                .show();
                    else
                        okListener.execute(newValue);
                })
                .onNeutral((dialog, which) -> okListener.execute(values[selectedValue[0]]))
                .show();
    }
}
