package com.tcftu.user.tcftuApp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * Created by user on 2017/8/11.
 */

public class FollowHelpDialogFragment1 extends DialogFragment {
    public Dialog onCreateDialog(Bundle savedlnstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.help_dialogfragment1,null);
        View tv1 = v.findViewById(R.id.help1);
        View tv2 = v.findViewById(R.id.help2);
        View tv3 = v.findViewById(R.id.help3);
        final CheckBox  dontShowAgain =(CheckBox)v.findViewById(R.id.checkBox);

        ((TextView) tv1).setText("(１) 本APP僅提供「課程報名前一天推播提醒」功能，無線上報名服務。\n");
        ((TextView) tv2).setText("(２) 本APP若未即時更新，即使用最新版本，將影響推播提醒功能之準確度。\n");
        ((TextView) tv3).setText("(３)「課程資訊」會因訓練單位申請「課程事項變更」而有所異動，故課程資訊請以產業人才投資方案報名網站為主。\n");

        /*不再顯示勾選框*/
        dontShowAgain.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                boolean checkBoxResult;
                if (dontShowAgain.isChecked())
                {
                    checkBoxResult = true;
                    setCheckboxState(checkBoxResult);
                }
                else
                {
                    checkBoxResult = false;
                    setCheckboxState(checkBoxResult);
                }
            }
        });
        builder.setTitle("❈ 注意事項 ❈")
                .setView(v)
                .setPositiveButton("關閉", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        return builder.create();
    }
    public void setCheckboxState(boolean chk) {
        // 記錄勾選方塊是否被打勾
        SharedPreferences settings = getActivity().getSharedPreferences("showit",0);
        settings.edit().putBoolean("skipMessage", chk).apply();
    }
    @Override
    public void onResume(){

        int width = getResources().getDimensionPixelSize(R.dimen.helpFragment_width);
        int height = getResources().getDimensionPixelSize(R.dimen.helpFragment_height);
        getDialog().getWindow().setLayout(width, height);

        super.onResume();
    }
}
