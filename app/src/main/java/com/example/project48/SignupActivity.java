package com.example.project48;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignupActivity extends AppCompatActivity {

    private EditText editTextUserName;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextPasswordConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);

        // 初始化控件
        editTextUserName = findViewById(R.id.editTextUserName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPasswordConfirm = findViewById(R.id.editTextPasswordConfirm);

        Button buttonRegister = findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        // 获取用户输入
        String userName = editTextUserName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString();
        String passwordConfirm = editTextPasswordConfirm.getText().toString();

        // 输入验证
        if (TextUtils.isEmpty(userName)) {
            editTextUserName.setError("请输入用户名");
            return;
        }

        if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("请输入有效的电子邮件地址");
            return;
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            editTextPassword.setError("密码长度至少为6");
            return;
        }

        if (!password.equals(passwordConfirm)) {
            editTextPasswordConfirm.setError("两次输入的密码不一致");
            return;
        }

        // 向后端服务器发送请求
        sendRegistrationRequest(userName, email, password);
    }

    private void sendRegistrationRequest(String userName, String email, String password) {
        new Thread(() -> {
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("username", userName);
                jsonObject.put("password", password);
                jsonObject.put("email", email); // Assuming you want to include the email in the JSON body.

            } catch (JSONException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(SignupActivity.this, "Error creating JSON for registration", Toast.LENGTH_SHORT).show());
                return; // Stop further execution in case of JSON exception
            }

            RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
            Request request = new Request.Builder()
                    .url("http://192.168.50.143:3000/signup")
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                final String responseBody = response.body() != null ? response.body().string() : null;
                runOnUiThread(() -> {
                    if (response.isSuccessful() && responseBody != null) {
                        Toast.makeText(SignupActivity.this, "注册成功", Toast.LENGTH_SHORT).show(); // "Registration Successful"
                        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(SignupActivity.this, "注册失败", Toast.LENGTH_SHORT).show(); // "Registration Failed"
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(SignupActivity.this, "注册出错", Toast.LENGTH_SHORT).show()); // "Error in Registration"
            }
        }).start();
    }


}




