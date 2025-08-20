package com.amrdeveloper.codeviewlibrary;

import android.os.Bundle;
import androidx.activity.compose.setContent;
import androidx.appcompat.app.AppCompatActivity;
import com.amr.app.EditorScreenKt;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Это единственное, что должно быть в этом методе.
        // Мы запускаем наш новый экран.
        setContent(() -> {
            EditorScreenKt.EditorScreen();
        });
    }
}
