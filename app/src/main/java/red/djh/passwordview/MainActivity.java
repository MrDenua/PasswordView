package red.djh.passwordview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PasswordView passwordView = findViewById(R.id.password_view);
        passwordView.setOnInputFinishListener(password -> {
            Toast.makeText(this, Arrays.toString(password), Toast.LENGTH_SHORT).show();
        });
    }
}
