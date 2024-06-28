package com.example.lab1_ph44962;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.AuthResult;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class ActivityPhoneLogin extends AppCompatActivity {

    private EditText phoneNumberEditText, otpEditText;
    private Button sendOtpButton, verifyOtpButton;
    private FirebaseAuth mAuth;
    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        // Khởi tạo các thành phần giao diện
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        otpEditText = findViewById(R.id.otpEditText);
        sendOtpButton = findViewById(R.id.sendOtpButton);
        verifyOtpButton = findViewById(R.id.verifyOtpButton);

        // Khởi tạo Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Xử lý sự kiện khi nhấn nút "Gửi mã OTP"
        sendOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = phoneNumberEditText.getText().toString().trim();

                if (TextUtils.isEmpty(phoneNumber)) {
                    Toast.makeText(ActivityPhoneLogin.this, "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Gửi yêu cầu xác thực số điện thoại
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneNumber,
                        60,
                        TimeUnit.SECONDS,
                        ActivityPhoneLogin.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                // Xác thực thành công tự động (nếu có)
                                signInWithPhoneAuthCredential(phoneAuthCredential);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                // Xử lý khi gửi yêu cầu xác thực thất bại
                                Toast.makeText(ActivityPhoneLogin.this, "Gửi mã OTP thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                // Lưu mã OTP đã được gửi để xác thực sau này
                                verificationId = s;
                                Toast.makeText(ActivityPhoneLogin.this, "Mã OTP đã được gửi", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        // Xử lý sự kiện khi nhấn nút "Xác nhận OTP"
        verifyOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = otpEditText.getText().toString().trim();

                if (TextUtils.isEmpty(otp)) {
                    Toast.makeText(ActivityPhoneLogin.this, "Vui lòng nhập mã OTP", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Tạo PhoneAuthCredential từ mã OTP và ID xác thực
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);

                // Đăng nhập bằng PhoneAuthCredential
                signInWithPhoneAuthCredential(credential);
            }
        });
    }

    // Phương thức để đăng nhập bằng PhoneAuthCredential
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Đăng nhập thành công
                            Toast.makeText(ActivityPhoneLogin.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ActivityPhoneLogin.this, MainActivity.class));
                            finish();
                        } else {
                            // Đăng nhập thất bại
                            Toast.makeText(ActivityPhoneLogin.this, "Đăng nhập thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
