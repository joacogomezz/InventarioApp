package com.joaqo.inventarioapp.core.navigation;

import androidx.navigation.NavGraphBuilder;
import androidx.navigation.NavHostController;

/**
 * Interface para registrar grafos de navegación de features
 * Cada feature implementa esta interface para definir sus rutas
 */
public interface FeatureNavGraph {
    void registerGraph(NavGraphBuilder navGraphBuilder, NavHostController navController);
}
