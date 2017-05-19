package xyz.royliu.library;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.lang.reflect.Method;

public class AKeyboardManager implements View.OnFocusChangeListener {

    public static final int KEYBOARD_TYPE_DEFAULT = 0;

    private Context mContext;
    private ViewGroup mRootView;
    private LinearLayout mKeyboardViewContainer;

    private FrameLayout.LayoutParams mKeyboardViewLayoutParams;
    private AKeyboardView keyboardView;

    private AKeyboard[] mDefaultKeyboards;

    private EditText mCurrentEdt;

    public AKeyboardManager(Activity activity) {
        this(activity, R.layout.default_keyboard);
    }


    public AKeyboardManager(Activity activity, int layoutId) {
        mContext = activity;
        mRootView = (ViewGroup) (activity.getWindow().getDecorView().findViewById(android.R.id.content));

        mKeyboardViewContainer = (LinearLayout) LayoutInflater.from(mContext).inflate(layoutId, null);
        keyboardView = (AKeyboardView) mKeyboardViewContainer.findViewById(R.id.keyboardview);

        mKeyboardViewLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.34 + DensityUtils.dip2px(36)));
        mKeyboardViewLayoutParams.gravity = Gravity.BOTTOM;
    }


    public void attachTo(EditText edt, AKeyboard keyboard) {
        this.mCurrentEdt = edt;
        hideSystemSoftKeyboard(edt);
        edt.setTag(R.id.edittext_bind_keyboard, keyboard);
        edt.setOnFocusChangeListener(this);
    }

    public void attachTo(EditText edt) {
        this.mCurrentEdt = edt;
        attachTo(edt, KEYBOARD_TYPE_DEFAULT);
    }

    public void attachTo(EditText edt, int type) {
        if (type == KEYBOARD_TYPE_DEFAULT) {
            defaultKeyboard();
            this.mCurrentEdt = edt;
            hideSystemSoftKeyboard(edt);
            edt.setTag(R.id.edittext_bind_keyboard, mDefaultKeyboards[0]);
            edt.setOnFocusChangeListener(this);
        }
    }

    public void changeAttachTo(EditText edt, AKeyboard keyboard) {
        keyboardView.setKeyBackground(mContext.getResources().getDrawable(R.drawable.sel_btn_keyboard_key_num));
        keyboard.setCurEditText(edt);
        refreshKeyboard(keyboard);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v instanceof EditText) {
            EditText attachEditText = (EditText) v;
            if (hasFocus) {
                showSoftKeyboard(attachEditText, true);
            } else {
                hideSoftKeyboard(attachEditText);
            }
        }
    }

    private AKeyboard getKeyboard(View view) {
        Object tag = view.getTag(R.id.edittext_bind_keyboard);
        if (null != tag && tag instanceof AKeyboard) {
            return (AKeyboard) tag;
        }
        return null;
    }

    private void refreshKeyboard(AKeyboard keyboard) {
        keyboardView.setKeyboard(keyboard);
        keyboardView.setEnabled(true);
        keyboardView.setPreviewEnabled(false);
        keyboardView.setOnKeyboardActionListener(keyboard);
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        keyboardView.measure(width, height);
//        mKeyboardHeight = keyboardView.getMeasuredHeight();
    }

    public void showSoftKeyboard(EditText view, boolean showAnim) {
        AKeyboard keyboard = getKeyboard(view);
        keyboard.setCurEditText(view);
        refreshKeyboard(keyboard);

        //将键盘布局加入到根布局中.
        mRootView.addView(mKeyboardViewContainer, mKeyboardViewLayoutParams);
        //设置加载动画.
        if (showAnim) {
            mKeyboardViewContainer.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.down_to_up));
        }
//        int moveHeight = getMoveHeight(view);
//        if (moveHeight > 0) {
//            mRootView.getChildAt(0).scrollBy(0, moveHeight); //移动屏幕
//        } else {
//            moveHeight = 0;
//        }
//
//        view.setTag(R.id.keyboard_view_move_height, moveHeight);
    }

    public void hideSoftKeyboard(EditText view) {
        int moveHeight = 0;
//        Object tag = view.getTag(R.id.keyboard_view_move_height);
//        if (null != tag) moveHeight = (int) tag;
//        if (moveHeight > 0) { //复原屏幕
//            mRootView.getChildAt(0).scrollBy(0, -1 * moveHeight);
//            view.setTag(R.id.keyboard_view_move_height, 0);
//        }

        mRootView.removeView(mKeyboardViewContainer); //将键盘从根布局中移除.

//        mKeyboardViewContainer.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.up_to_hide));
    }

    private void defaultKeyboard() {
        mDefaultKeyboards = new AKeyboard[3];
        mDefaultKeyboards[0] = new AKeyboard(mContext, R.xml.keyboard_abc) {
            @Override
            public boolean handleSpecialKey(int primaryCode) {
                if (primaryCode == 123123) {
                    changeAttachTo(mCurrentEdt, mDefaultKeyboards[1]);
                    return true;
                } else if (primaryCode == 789789) {
                    changeAttachTo(mCurrentEdt, mDefaultKeyboards[2]);
                    return true;
                }
                return false;
            }
        };
        mDefaultKeyboards[1] = new AKeyboard(mContext, R.xml.keyboard_num) {
            @Override
            public boolean handleSpecialKey(int primaryCode) {
                if (primaryCode == 741741) {
                    changeAttachTo(mCurrentEdt, mDefaultKeyboards[0]);
                    return true;
                }
                return false;
            }
        };
        mDefaultKeyboards[2] = new AKeyboard(mContext, R.xml.keyboard_symbol) {
            @Override
            public boolean handleSpecialKey(int primaryCode) {
                if (primaryCode == 123123) {
                    changeAttachTo(mCurrentEdt, mDefaultKeyboards[0]);
                    return true;
                } else if (primaryCode == 456456) {
                    changeAttachTo(mCurrentEdt, mDefaultKeyboards[1]);
                    return true;
                }
                return false;
            }
        };
    }

    /**
     * 隐藏系统键盘
     *
     * @param editText
     */
    public static void hideSystemSoftKeyboard(EditText editText) {
        int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt >= 11) {
            try {
                Class<EditText> cls = EditText.class;
                Method setShowSoftInputOnFocus;
                setShowSoftInputOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(editText, false);
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            editText.setInputType(InputType.TYPE_NULL);
        }
    }

}
