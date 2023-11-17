package com.bawnorton.trulyrandom.client.extend;

import com.bawnorton.trulyrandom.random.Randomiser;

public interface DataConfigurationExtender {
    ThreadLocal<Randomiser> randomiserThreadLocal = ThreadLocal.withInitial(Randomiser::new);
    Randomiser trulyRandom$getRandomiser();
}
