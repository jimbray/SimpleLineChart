package xyz.jimbray.simplelineview;

/**
 * Created by Jimbray  .
 * on 2016/8/25
 * Email: jimbray16@gmail.com
 * Description: TODO
 */
public class SimpleLineData {

    private int index;

    private float value;

    //与value性质相同，但优先级比value 高，主要用于显示与value相关的 特殊文字组成
    private String value_text;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public String getValue_text() {
        return value_text;
    }

    public void setValue_text(String value_text) {
        this.value_text = value_text;
    }
}
