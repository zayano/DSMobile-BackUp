package id.sentuh.digitalsignage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CodeAccess extends AppCompatActivity {

    EditText password;
    Button submit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_access);

        password = findViewById(R.id.editText_password);
        submit = findViewById(R.id.button_submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = password.getText().toString();

                switch (text) {
                    case "sentuh":
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case "":
                        Toast.makeText(CodeAccess.this, "Password Harap Di isi", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(CodeAccess.this, "Password Salah", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }
}
