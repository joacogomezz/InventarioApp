package com.joaqo.inventarioapp.core.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiProducts {

    // URL de producción
    private static final String BASE_URL = "https://joaquinproducts.chuy7x.space/";

    // Para desarrollo local, usar:
    // private static final String BASE_URL = "http://10.0.2.2:8080/";

    private static InventaryApi instance;

    public static InventaryApi getInstance() {
        if (instance == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            instance = retrofit.create(InventaryApi.class);
        }
        return instance;
    }
}
