package com.dumfing.pokemon_arena.component;

import com.dumfing.pokemon_arena.PkmnBattle;
import com.dumfing.pokemon_arena.module.PokemonArenaModule;
import com.dumfing.pokemon_arena.module.UtilModule;
import dagger.Component;

import jakarta.inject.Singleton;

@Singleton
@Component(modules = {PokemonArenaModule.class, UtilModule.class})
public interface PokemonArenaComponent {
    PkmnBattle createPkmnBattle();
}
