package ir.shariaty.tripmate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etPassword, reppassword;
    private Button btnRegister;
    private TextView tvLogin;
    private ImageView ivTogglePassword, ivToggleRePassword;

    private boolean isPasswordVisible = false;
    private boolean isRePasswordVisible = false;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // پیدا کردن ویوها
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        reppassword = findViewById(R.id.reppassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.btnGoTologin);
        ivTogglePassword = findViewById(R.id.ivTogglePassword);
        ivToggleRePassword = findViewById(R.id.ivToggleRePassword);

        prefs = getSharedPreferences("UserData", Context.MODE_PRIVATE);

        // قابلیت نمایش/مخفی کردن رمز عبور
        ivTogglePassword.setOnClickListener(view -> {
            if (isPasswordVisible) {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivTogglePassword.setImageResource(R.drawable.hidden); // چشم بسته
                isPasswordVisible = false;
            } else {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivTogglePassword.setImageResource(R.drawable.ic_eye); // چشم باز
                isPasswordVisible = true;
            }
            etPassword.setSelection(etPassword.getText().length());
        });

        ivToggleRePassword.setOnClickListener(view -> {
            if (isRePasswordVisible) {
                reppassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivToggleRePassword.setImageResource(R.drawable.hidden); // چشم بسته
                isRePasswordVisible = false;
            } else {
                reppassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivToggleRePassword.setImageResource(R.drawable.ic_eye); // چشم باز
                isRePasswordVisible = true;
            }
            reppassword.setSelection(reppassword.getText().length());
        });

        // ثبت نام
        btnRegister.setOnClickListener(view -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String repassword = reppassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty() || repassword.isEmpty()) {
                Toast.makeText(this, "لطفاً همه‌ی فیلدها را پر کنید", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(repassword)) {
                Toast.makeText(this, "رمز عبور و تکرار آن یکسان نیستند", Toast.LENGTH_SHORT).show();
            } else {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("username", username);
                editor.putString("password", password);
                editor.apply();

                Toast.makeText(this, "ثبت‌نام با موفقیت انجام شد", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // رفتن به صفحه ورود
        tvLogin.setOnClickListener(view -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
