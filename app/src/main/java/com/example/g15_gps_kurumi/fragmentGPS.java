package com.example.g15_gps_kurumi;

import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class fragmentGPS extends SupportMapFragment implements OnMapReadyCallback {

    private EditText txtLatitud, txtLongitud;
    private TextView tvLatitud, tvLongitud;
    private GoogleMap nMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view != null) {
            txtLatitud = view.findViewById(R.id.txtLatitud);
            txtLongitud = view.findViewById(R.id.txtLongitud);
            tvLatitud = view.findViewById(R.id.tvLatitud);
            tvLongitud = view.findViewById(R.id.tvLongitud);

            getMapAsync(this);
            showPopupWindow(view);  // Muestra la ventana flotante al inicio
        }
        return view;
    }

    private void showPopupWindow(View view) {
        // Esperar 3 segundos antes de mostrar la ventana flotante
        new Handler().postDelayed(() -> {
            // Inflar el diseño de la ventana flotante
            View popupView = LayoutInflater.from(getContext()).inflate(R.layout.popup_layout, null);

            // Configurar la imagen en la ventana flotante (cambia R.drawable.rias_bunny con tu propia imagen)
            ImageView imageView = popupView.findViewById(R.id.popup_layout);
            imageView.setImageResource(R.drawable.saqsaywaman);

            // Crear la ventana flotante con animaciones de entrada y salida
            PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            popupWindow.setAnimationStyle(android.R.style.Animation_Dialog); // Animación de desvanecimiento

            // Mostrar la ventana flotante en el centro
            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

            // Cerrar la ventana flotante después de 3 segundos
            new Handler().postDelayed(() -> {
                popupWindow.dismiss();
            }, 5000);
        }, 4900); // 7000 milisegundos (7 segundos) de retraso antes de mostrar la ventana flotante
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        nMap = googleMap;
        nMap.setOnMapClickListener(latLng -> {
            txtLatitud.setText(String.valueOf(latLng.latitude));
            txtLongitud.setText(String.valueOf(latLng.longitude));

            // Mostrar las coordenadas en TextViews
            tvLatitud.setText("Latitud: " + String.valueOf(latLng.latitude));
            tvLongitud.setText("Longitud: " + String.valueOf(latLng.longitude));

            // Realizar zoom automático más cercano
            zoomToLocation(latLng);
        });

        nMap.setOnMapLongClickListener(latLng -> {
            txtLatitud.setText(String.valueOf(latLng.latitude));
            txtLongitud.setText(String.valueOf(latLng.longitude));

            // Mostrar las coordenadas en TextViews
            tvLatitud.setText("Latitud: " + String.valueOf(latLng.latitude));
            tvLongitud.setText("Longitud: " + String.valueOf(latLng.longitude));

            // Realizar zoom automático más cercano
            zoomToLocation(latLng);
        });

        LatLng mexico = new LatLng(-13.509856, -71.9842627); // Cambiar a las coordenadas del país deseado
        nMap.addMarker(new MarkerOptions().position(mexico).title("Saqsaywaman, Cusco"));

        // Realizar zoom automático desde la vista superior del país
        zoomToCountry(mexico);
    }

    private void zoomToCountry(LatLng countryLatLng) {
        // Configurar la cámara en la vista superior del país
        CameraPosition countryView = new CameraPosition.Builder()
                .target(countryLatLng)
                .zoom(3.0f) // Ajustar el nivel de zoom según sea necesario
                .tilt(0) // Vista superior
                .build();

        nMap.animateCamera(CameraUpdateFactory.newCameraPosition(countryView), 2000, new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                // Esperar un breve período antes de realizar un zoom progresivo hacia la ubicación específica
                new Handler().postDelayed(() -> zoomToLocation(countryLatLng), 1000);
            }

            @Override
            public void onCancel() {
                // No es necesario implementar en este caso
            }
        });
    }

    private void zoomToLocation(LatLng locationLatLng) {
        // Configurar la cámara en la ubicación específica
        CameraPosition locationView = new CameraPosition.Builder()
                .target(locationLatLng)
                .zoom(15.0f) // Ajustar el nivel de zoom según sea necesario
                .tilt(30) // Vista inclinada
                .build();

        nMap.animateCamera(CameraUpdateFactory.newCameraPosition(locationView));
    }
}
