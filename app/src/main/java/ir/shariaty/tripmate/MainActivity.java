package ir.shariaty.tripmate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.SignInButton;

public class MainActivity extends AppCompatActivity {

    private Button button_start_now, btnRegister;
    private SignInButton btnGoogleSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // layout جدید

        button_start_now = findViewById(R.id.button_start_now);


        button_start_now.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });




    }
}
