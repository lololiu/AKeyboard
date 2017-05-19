package xyz.royliu.library;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.widget.EditText;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liulou on 2017/4/10.
 * desc:
 */

public abstract class AKeyboard extends Keyboard implements KeyboardView.OnKeyboardActionListener {
    private static final String TAG = "AKeyboard";
    protected EditText etCurrent;

    private static final String TAG_KEYBOARD = "Keyboard";
    private static final String TAG_ROW = "Row";
    private static final String TAG_KEY = "Key";

    private List<Key> mKeys = null;

    private Map<Integer, AKeyStyle> mKeyStyle = new HashMap<>();

    private ArrayList<Row> rows = new ArrayList<>();

    public AKeyboard(Context context, int xmlLayoutResId) {
        super(context, xmlLayoutResId);
        mKeys = getKeys();
        loadKeyboard(context, context.getResources().getXml(xmlLayoutResId));
    }


    public AKeyboard(Context context, int xmlLayoutResId, int modeId, int width, int height) {
        super(context, xmlLayoutResId, modeId, width, height);
        loadKeyboard(context, context.getResources().getXml(xmlLayoutResId));
    }

    public void loadKeyboard(Context context, XmlResourceParser parser) {
        boolean inKey = false;
        boolean inRow = false;
        boolean leftMostKey = false;
        int row = 0;
        int comuln = 0;
        int x = 0;
        int y = 0;
        Key key = null;
        Row currentRow = null;
        Resources res = context.getResources();
        boolean skipRow = false;
        try {
            int event;
            while ((event = parser.next()) != XmlResourceParser.END_DOCUMENT) {
                if (event == XmlResourceParser.START_TAG) {
                    String tag = parser.getName();
                    if (TAG_ROW.equals(tag)) {
                        inRow = true;

                    } else if (TAG_KEY.equals(tag)) {
                        inKey = true;
                        parseKeyStyleAttr(res, parser, comuln);
                    } else if (TAG_KEYBOARD.equals(tag)) {
//                        parseKeyboardAttributes(res, parser);
                    }
                } else if (event == XmlResourceParser.END_TAG) {
                    if (inKey) {
                        inKey = false;
                        comuln++;
                    } else if (inRow) {
                        inRow = false;
                        row++;
                    } else {
                        // TODO: error or extend?
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Parse error:" + e);
            e.printStackTrace();
        }
    }

    private void parseKeyStyleAttr(Resources res, XmlResourceParser parser, int comuln) {
        TypedArray a = res.obtainAttributes(Xml.asAttributeSet(parser),
                R.styleable.AKey);
        if (a.getIndexCount() > 0) {
            int textColor = a.getColor(R.styleable.AKey_text_color, -1);
            float textSize = a.getDimension(R.styleable.AKey_text_size, 0);
            Drawable background = a.getDrawable(R.styleable.AKey_background);
            String label = a.getString(R.styleable.AKey_key_label);
            mKeyStyle.put(comuln, new AKeyStyle(background, textSize, textColor, label));
        }
        a.recycle();
    }

    private void skipToEndOfRow(XmlResourceParser parser)
            throws XmlPullParserException, IOException {
        int event;
        while ((event = parser.next()) != XmlResourceParser.END_DOCUMENT) {
            if (event == XmlResourceParser.END_TAG
                    && parser.getName().equals(TAG_ROW)) {
                break;
            }
        }
    }

    public abstract boolean handleSpecialKey(int primaryCode);

    @Override
    public void onPress(int i) {

    }

    @Override
    public void onRelease(int i) {

    }

    public void setCurEditText(EditText etCurrent) {
        this.etCurrent = etCurrent;
    }

    public EditText getCurEditText() {
        return etCurrent;
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        if (null != etCurrent && etCurrent.hasFocus() && !handleSpecialKey(primaryCode)) {
            Editable editable = etCurrent.getText();
            int start = etCurrent.getSelectionStart();

            if (primaryCode == Keyboard.KEYCODE_DELETE) { //回退
                if (!TextUtils.isEmpty(editable)) {
                    if (start > 0) {
                        editable.delete(start - 1, start);
                    }
                }
            } else { //其他默认
                editable.insert(start, Character.toString((char) primaryCode));
            }
        }
    }

    @Override
    public void onText(CharSequence charSequence) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }

    public Map<Integer, AKeyStyle> getKeyStyleMap() {
        return mKeyStyle;
    }
}
