package xyz.royliu.akeyboard;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;

import xyz.royliu.library.AKeyboard;
import xyz.royliu.library.AKeyboardManager;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    AKeyboardManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText) findViewById(R.id.edit);
        manager = new AKeyboardManager(this);
        manager.attachTo(editText);
    }
}
