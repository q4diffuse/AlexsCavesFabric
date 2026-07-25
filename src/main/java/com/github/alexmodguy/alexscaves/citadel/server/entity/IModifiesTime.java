package com.github.alexmodguy.alexscaves.citadel.server.entity;

import com.github.alexmodguy.alexscaves.citadel.server.tick.modifier.TickRateModifier;

public interface IModifiesTime {

    boolean isTimeModificationValid(TickRateModifier modifier);

}
