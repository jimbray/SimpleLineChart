package xyz.jimbray.simplelineview;

import android.app.Activity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class ActivityMain extends Activity {

    private SimpleLineView simple_line_view_week, simple_line_view_month;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_ac_main);

        initViews();
    }

    private void initViews() {
        simple_line_view_week = (SimpleLineView) findViewById(R.id.simple_line_view_week);
        simple_line_view_month = (SimpleLineView) findViewById(R.id.simple_line_view_month);

        initData();
    }

    private void initData() {
        initWeekData();
        initMonthData();
    }

    private void initWeekData() {
        List<String>  bottomTexts = new ArrayList<>();
        bottomTexts.add("Sun");
        bottomTexts.add("Mon");
        bottomTexts.add("Tue");
        bottomTexts.add("Wed");
        bottomTexts.add("Thu");
        bottomTexts.add("Fri");
        bottomTexts.add("Sat");


        List<SimpleLineData> data = new ArrayList<>();
        for(int i = 0 ; i < 7; i++) {
            SimpleLineData item = new SimpleLineData();
            item.setIndex(i);
            item.setValue((int) (Math.random() * 99 + 1));
//            item.setValue_text("什么" + item.getValue() + "什么");

            data.add(item);
        }

        simple_line_view_week.setColumnCount(7); //设置 列数
        simple_line_view_week.setData(data);
        simple_line_view_week.setIsDrawBottomText(true); //设置是否绘制底部文字
        simple_line_view_week.setBottomTextList(bottomTexts); //设置底部文字列表
        simple_line_view_week.setTouchPadding(dp2px(5));
        simple_line_view_week.setTopText("week");
    }

    private void initMonthData() {
        List<String>  bottomTexts = new ArrayList<>();

        List<SimpleLineData> data = new ArrayList<>();
        for(int i = 0 ; i < 31; i++) {
            SimpleLineData item = new SimpleLineData();
            item.setIndex(i);
            item.setValue((int) (Math.random() * 99 + 1));

            data.add(item);

            bottomTexts.add((i+1) + "");
        }

        simple_line_view_month.setColumnCount(31);
        simple_line_view_month.setData(data);
        simple_line_view_month.setIsDrawBottomText(false);
        simple_line_view_month.setBottomTextList(bottomTexts);
        simple_line_view_month.setIsDrawBottomText(true);
        simple_line_view_month.setBottomTextStepSize(6);
        simple_line_view_month.setBottomValueSuffix("%");
        simple_line_view_month.setTopText("month");
    }


    private float dp2px(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }


}
