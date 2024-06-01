package com.bawnorton.trulyrandom.extend;

import com.bawnorton.trulyrandom.tracker.Team;

public interface TeamMember {
    void trulyrandom$joinTeam(Team team);

    void trulyrandom$leaveTeam();

    Team trulyrandom$getTeam();
}
