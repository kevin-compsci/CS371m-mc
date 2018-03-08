package tutorials.cs371m.androidtictactoe;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by scottm on 6/7/2016.
 * Initial files created by Mike Scott and Dr. Frank for tutorial purposes for students to learn from. Basic implementations were required to make this program functional.
 * A simple about activity.
 */
public class AboutActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_layout);
    }
}
