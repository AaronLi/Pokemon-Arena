package com.dumfing.pokemon_arena;

import java.util.HashMap;
import java.util.Map;

public class OptionConfiguration {
    private final HashMap<Option, Boolean> settings = new HashMap<>(Map.of(Option.ShowAttackResults, true));

    void setOption(Option option, Boolean setting) {
        settings.put(option, setting);
    }

    Boolean getOption(Option option) {
        return settings.getOrDefault(option, false);
    }

    enum Option {
        PokemonDetails,
        AttackDetails,
        ShowAttackResults;

        public String getDisplayName() {
            return switch (this) {
                case PokemonDetails -> "Pokemon Stats";
                case AttackDetails -> "Attack Details";
                case ShowAttackResults -> "Attack Results";
            };
        }
    }

    ;
}
