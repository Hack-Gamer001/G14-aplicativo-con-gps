package com.example.g15_gps_kurumi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button bt1, bt2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt1 = findViewById(R.id.bmujer);
        bt2 = findViewById(R.id.bvaron);

        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reemplazarFragmento(new fragmentRUTA()); // Usar MujeresFragment
            }
        });

        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reemplazarFragmento(new fragmentGPS()); // Usar VaronesFragment
            }
        });

    }

    public void reemplazarFragmento(Fragment fragment) {
        // Declarar un FragmentManager
        FragmentManager fragmentManager = getSupportFragmentManager();
        // Iniciar una transacción de fragmentos
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // Ubicar el recurso del FrameLayout y poner el fragmento
        fragmentTransaction.replace(R.id.frameContenedor, fragment);
        fragmentTransaction.commit();
    }
}

