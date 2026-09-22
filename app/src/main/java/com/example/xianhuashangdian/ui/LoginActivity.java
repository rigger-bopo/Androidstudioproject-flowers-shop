package com.example.xianhuashangdian.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.xianhuashangdian.R;
import com.example.xianhuashangdian.data.DatabaseHelper;
import com.example.xianhuashangdian.databinding.ActivityLoginBinding;
import com.example.xianhuashangdian.util.SessionManager;
import com.example.xianhuashangdian.util.ValidationUtils;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {
    public static final String EXTRA_OPEN_FORGOT = "open_forgot_password";

    private ActivityLoginBinding binding;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = DatabaseHelper.getInstance(this);
        sessionManager = new SessionManager(this);
        bindActions();
        showPanel(getIntent().getBooleanExtra(EXTRA_OPEN_FORGOT, false)
                ? binding.forgotPanel : binding.loginPanel);
    }

    private void bindActions() {
        binding.loginTab.setOnClickListener(view -> showPanel(binding.loginPanel));
        binding.registerTab.setOnClickListener(view -> showPanel(binding.registerPanel));
        binding.registerLink.setOnClickListener(view -> showPanel(binding.registerPanel));
        binding.backLoginLink.setOnClickListener(view -> showPanel(binding.loginPanel));
        binding.forgotLink.setOnClickListener(view -> showPanel(binding.forgotPanel));
        binding.backLoginFromForgot.setOnClickListener(view -> showPanel(binding.loginPanel));

        binding.loginButton.setOnClickListener(view -> performLogin());
        binding.registerButton.setOnClickListener(view -> performRegistration());
        binding.resetButton.setOnClickListener(view -> performPasswordReset());
    }

    private void showPanel(View target) {
        View[] panels = {binding.loginPanel, binding.registerPanel, binding.forgotPanel};
        for (View panel : panels) {
            if (panel == target) {
                panel.setVisibility(View.VISIBLE);
                panel.setAlpha(0f);
                panel.setTranslationY(10f);
                panel.animate()
                        .alpha(1f)
                        .translationY(0f)
                        .setDuration(180L)
                        .setInterpolator(new AccelerateDecelerateInterpolator())
                        .start();
            } else {
                panel.setVisibility(View.GONE);
                clearErrors();
            }
        }
        boolean onLogin = target == binding.loginPanel;
        binding.loginTab.setTextColor(getColor(onLogin ? R.color.rose_primary : R.color.muted));
        binding.registerTab.setTextColor(getColor(
                target == binding.registerPanel ? R.color.rose_primary : R.color.muted));
    }

    private void performLogin() {
        clearErrors();
        String username = textOf(binding.loginAccountInput.getText());
        String password = textOf(binding.loginPasswordInput.getText());

        if (!ValidationUtils.isValidUsername(username)) {
            binding.loginAccountLayout.setError(getString(R.string.account_hint));
            return;
        }
        if (!ValidationUtils.isValidPassword(password)) {
            binding.loginPasswordLayout.setError(getString(R.string.password_hint));
            return;
        }

        long userId = databaseHelper.authenticateUser(username, password);
        if (userId < 0) {
            binding.loginPasswordLayout.setError(getString(R.string.wrong_credentials));
            return;
        }

        sessionManager.login(userId, username);
        Toast.makeText(this, R.string.login_success, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    private void performRegistration() {
        clearErrors();
        String username = textOf(binding.registerAccountInput.getText());
        String password = textOf(binding.registerPasswordInput.getText());
        String confirm = textOf(binding.registerConfirmInput.getText());
        String answer = textOf(binding.registerAnswerInput.getText());

        if (!ValidationUtils.isValidUsername(username)) {
            binding.registerAccountLayout.setError(getString(R.string.account_hint));
            return;
        }
        if (!ValidationUtils.isValidPassword(password)) {
            binding.registerPasswordLayout.setError(getString(R.string.password_hint));
            return;
        }
        if (!password.equals(confirm)) {
            binding.registerConfirmLayout.setError("两次输入的密码不一致");
            return;
        }
        if (!ValidationUtils.isNonEmpty(answer)) {
            binding.registerAnswerLayout.setError("请填写验证答案");
            return;
        }

        long userId = databaseHelper.registerUser(username, password, answer);
        if (userId < 0) {
            binding.registerAccountLayout.setError(getString(R.string.account_exists));
            return;
        }

        binding.loginAccountInput.setText(username);
        binding.loginPasswordInput.setText("");
        Toast.makeText(this, R.string.register_success, Toast.LENGTH_SHORT).show();
        showPanel(binding.loginPanel);
    }

    private void performPasswordReset() {
        clearErrors();
        String username = textOf(binding.forgotAccountInput.getText());
        String answer = textOf(binding.forgotAnswerInput.getText());
        String password = textOf(binding.forgotPasswordInput.getText());
        String confirm = textOf(binding.forgotConfirmInput.getText());

        if (!ValidationUtils.isValidUsername(username)) {
            binding.forgotAccountLayout.setError(getString(R.string.account_hint));
            return;
        }
        if (!databaseHelper.userExists(username)) {
            binding.forgotAccountLayout.setError(getString(R.string.account_not_found));
            return;
        }
        if (!ValidationUtils.isNonEmpty(answer)) {
            binding.forgotAnswerLayout.setError("请填写验证答案");
            return;
        }
        if (!ValidationUtils.isValidPassword(password)) {
            binding.forgotPasswordLayout.setError(getString(R.string.password_hint));
            return;
        }
        if (!password.equals(confirm)) {
            binding.forgotConfirmLayout.setError("两次输入的密码不一致");
            return;
        }

        if (!databaseHelper.resetPassword(username, answer, password)) {
            binding.forgotAnswerLayout.setError(getString(R.string.wrong_security_answer));
            return;
        }

        binding.loginAccountInput.setText(username);
        binding.loginPasswordInput.setText("");
        Toast.makeText(this, R.string.reset_success, Toast.LENGTH_SHORT).show();
        showPanel(binding.loginPanel);
    }

    private void clearErrors() {
        TextInputLayout[] layouts = {
                binding.loginAccountLayout,
                binding.loginPasswordLayout,
                binding.registerAccountLayout,
                binding.registerPasswordLayout,
                binding.registerConfirmLayout,
                binding.registerAnswerLayout,
                binding.forgotAccountLayout,
                binding.forgotAnswerLayout,
                binding.forgotPasswordLayout,
                binding.forgotConfirmLayout
        };
        for (TextInputLayout layout : layouts) {
            layout.setError(null);
        }
    }

    private String textOf(CharSequence value) {
        return value == null ? "" : value.toString().trim();
    }
}
