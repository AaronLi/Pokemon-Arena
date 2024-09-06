package com.dumfing.pokemon_arena;

import java.io.IOException;
import java.util.Scanner;

public class PkmnArena {
    public static void main(String[] args) throws IOException {
        Pokedex pokedex = new Pokedex("allPokemon.txt");
        String botName = PkmnTools.enemyName();
        PkmnBattle arena = new PkmnBattle(pokedex, botName);
        Scanner kb = new Scanner(System.in);

        arena.prepareForBattle(kb);
        arena.battle(kb);
    }
}
