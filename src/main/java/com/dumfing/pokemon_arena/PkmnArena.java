package com.dumfing.pokemon_arena;

import com.dumfing.pokemon_arena.component.DaggerPokemonArenaComponent;
import com.dumfing.pokemon_arena.component.PokemonArenaComponent;

public class PkmnArena {
    public static void main(String[] args) {
        PokemonArenaComponent component = DaggerPokemonArenaComponent.create();

        component.createPkmnBattle().battle();
    }
}
