package com.dumfing.pokemon_arena.module;

import dagger.Module;
import dagger.Provides;

import jakarta.inject.Singleton;
import java.util.Scanner;

@Module
public class UtilModule {
    @Provides
    @Singleton
    Scanner providesScanner(){
        return new Scanner(System.in);
    }
}
