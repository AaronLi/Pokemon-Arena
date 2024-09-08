package com.dumfing.pokemon_arena.module;

import com.dumfing.pokemon_arena.OptionConfiguration;
import com.dumfing.pokemon_arena.Party;
import com.dumfing.pokemon_arena.PkmnBattle;
import com.dumfing.pokemon_arena.PkmnTools;
import com.dumfing.pokemon_arena.Pokedex;
import dagger.Module;
import dagger.Provides;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.Scanner;

@Module
public class PokemonArenaModule {

    @Getter
    @Setter
    @Builder
    static class UserAndComputerParty{
        Party userParty;
        Party computerParty;
    }

    @SneakyThrows
    @Provides
    @Singleton
    Pokedex providesPokedex() {
        return Pokedex.fromFile("allPokemon.txt");
    }

    @Provides
    @Singleton
    OptionConfiguration providesOptionConfiguration() {
        return new OptionConfiguration();
    }

    @Provides
    @Singleton
    Party providesUserParty(Pokedex pokedex, Scanner kb, OptionConfiguration options) {
        return Party.pickParty(pokedex, kb, options); // let the user pick
    }

    @Provides
    @Singleton
    UserAndComputerParty providesBothParties(@Named("BOT_NAME") String botName, Pokedex pokedex, Party userParty) {

        Party computerParty = new Party(botName).fillPartyWithRandomPokemon(pokedex, userParty, 6);
        return UserAndComputerParty.builder()
                .userParty(userParty)
                .computerParty(computerParty)
                .build(); // let the computer pick
    }

    @Provides
    @Singleton
    PkmnBattle providesPokemonBattle(OptionConfiguration options, Scanner kb, UserAndComputerParty userAndComputerParty) {
        return new PkmnBattle(kb, options, userAndComputerParty.getUserParty(), userAndComputerParty.getComputerParty());
    }

    @SneakyThrows
    @Provides
    @Singleton
    @Named("BOT_NAME")
    String botName() {
        return PkmnTools.enemyName();
    }


}
