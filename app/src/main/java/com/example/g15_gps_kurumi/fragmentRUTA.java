package com.example.g15_gps_kurumi;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;



public class fragmentRUTA extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private TextView txtDistance, txtTime, txtPrice;
    private Button btnSelectOrigin, btnSelectDestination, btnCalcular;
    private LatLng originLatLng, destinationLatLng;
    private PlacesClient placesClient;


    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final String API_KEY = "AIzaSyAOVYRIgupAurZup5y1PRh8Ismb1A3lLao"; // Reemplaza con tu clave de la API de Google

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_ruta, container, false);

        // Inicializar el Places API Client
        Places.initialize(requireContext(), API_KEY);
        placesClient = Places.createClient(requireContext());

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        txtDistance = root.findViewById(R.id.txtDistance);
        txtTime = root.findViewById(R.id.txtTime);
        txtPrice = root.findViewById(R.id.txtPrice);
        btnSelectOrigin = root.findViewById(R.id.btnSelectOrigin);
        btnSelectDestination = root.findViewById(R.id.btnSelectDestination);
        btnCalcular = root.findViewById(R.id.btnCalcular);

        // Coordenadas de inicio y final
        LatLng inicioCoordenadas = new LatLng(-13.5291981, -71.9495621);
        LatLng finalCoordenadas = new LatLng(-13.5375921, -71.9049677);

        // Actualizar las variables de origen y destino
        originLatLng = inicioCoordenadas;
        destinationLatLng = finalCoordenadas;

        btnSelectOrigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLocation("Inicio", inicioCoordenadas);
            }
        });

        btnSelectDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLocation("Destino", finalCoordenadas);
            }
        });

        btnCalcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateDistance();
            }
        });

        showPopupWindow(root);

        return root;
    }

    private void updateLocation(String locationName, LatLng coordinates) {
        txtDistance.setText("");
        txtTime.setText("");
        txtPrice.setText("");

        if (locationName.equals("Inicio")) {
            btnSelectOrigin.setText(locationName);
            originLatLng = coordinates;
        } else if (locationName.equals("Destino")) {
            btnSelectDestination.setText(locationName);
            destinationLatLng = coordinates;
        }

        updateMapMarkers();
    }

    private void calculateDistance() {
        if (originLatLng != null && destinationLatLng != null) {
            new GetDirections().execute();
        } else {
            Toast.makeText(requireContext(), "Selecciona ubicaciones válidas", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateMapMarkers() {
        mMap.clear();

        if (originLatLng != null) {
            mMap.addMarker(new MarkerOptions().position(originLatLng).title("Origen"));
        }

        if (destinationLatLng != null) {
            mMap.addMarker(new MarkerOptions().position(destinationLatLng).title("Destino"));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Posición inicial del mapa
        LatLng initialLocation = new LatLng(-34, 151);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(initialLocation));

        // Marcador en la posición inicial
        mMap.addMarker(new MarkerOptions().position(initialLocation).title("Marcador de posición inicial"));

        // Programar la actualización de los TextViews después de 5 segundos sin TimerTask
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Simula obtener datos después de 5 segundos
                String distancia = "5.6 km";
                String tiempo = "15 - 20 mins";
                String precio = "S/.7.00 - S/.10.00";

                // Actualiza los TextViews con los datos simulados
                txtDistance.setText(distancia);
                txtTime.setText(tiempo);
                txtPrice.setText(precio);
            }
        }, 15000); // 5000 milisegundos = 5 segundos
    }



    private class GetDirections extends AsyncTask<Void, Void, DirectionsResult> {

        @Override
        protected DirectionsResult doInBackground(Void... voids) {
            try {
                GeoApiContext context = new GeoApiContext.Builder()
                        .apiKey(API_KEY)
                        .build();

                return DirectionsApi.newRequest(context)
                        .origin(new com.google.maps.model.LatLng(originLatLng.latitude, originLatLng.longitude))
                        .destination(new com.google.maps.model.LatLng(destinationLatLng.latitude, destinationLatLng.longitude))
                        .mode(TravelMode.DRIVING)
                        .await();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(DirectionsResult directionsResult) {
            if (directionsResult != null && directionsResult.routes != null && directionsResult.routes.length > 0) {
                // Obtener la primera ruta (puedes ajustar esto según tus necesidades)
                com.google.maps.model.DirectionsRoute route = directionsResult.routes[0];

                // Actualizar TextViews con la distancia y el tiempo
                if (route.legs != null && route.legs.length > 0) {
                    com.google.maps.model.DirectionsLeg leg = route.legs[0];
                    txtDistance.setText(leg.distance.humanReadable);
                    txtTime.setText(leg.duration.humanReadable);

                    // Calcular y mostrar el precio
                    double distanciaEnKm = leg.distance.inMeters / 1000.0;
                    double precioCalculado = calcularPrecio(distanciaEnKm, leg.duration.inSeconds);

                    txtPrice.setText(String.format("%.2f", precioCalculado));
                }

            } else {
                Toast.makeText(requireContext(), "No se pudo calcular la ruta", Toast.LENGTH_SHORT).show();
            }
        }

        private double calcularPrecio(double distancia, long tiempoEnSegundos) {
            // Lógica para calcular el precio basado en la distancia y el tiempo
            // Puedes ajustar esto según tus necesidades específicas
            double precioBase = 5.0; // Precio base
            double precioPorKm = 2.0; // Precio por kilómetro
            double precioPorMinuto = 0.1; // Precio por minuto

            return precioBase + (precioPorKm * distancia) + (precioPorMinuto * tiempoEnSegundos / 60.0);
        }

    }

    private void showPopupWindow(View view) {
        // Esperar 3 segundos antes de mostrar la ventana flotante
        new Handler().postDelayed(() -> {
            // Inflar el diseño de la ventana flotante
            View popupView = LayoutInflater.from(getContext()).inflate(R.layout.popup_layout2, null);

            // Configurar la imagen en la ventana flotante (cambia R.drawable.rias_bunny con tu propia imagen)
            ImageView imageView = popupView.findViewById(R.id.popup_layout);
            imageView.setImageResource(R.drawable.trazo);

            // Crear la ventana flotante con animaciones de entrada y salida
            PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            popupWindow.setAnimationStyle(android.R.style.Animation_Dialog); // Animación de desvanecimiento

            // Mostrar la ventana flotante en el centro
            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

            // Cerrar la ventana flotante después de 3 segundos
            new Handler().postDelayed(() -> {
                popupWindow.dismiss();
            }, 7000);
        }, 25000); // 7000 milisegundos (7 segundos) de retraso antes de mostrar la ventana flotante
    }

}
