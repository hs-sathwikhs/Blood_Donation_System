package com.blooddonation.patterns;

import com.blooddonation.model.Camp;
import org.springframework.context.ApplicationEvent;

public class CampPublishedEvent extends ApplicationEvent {

    private final Camp camp;

    public CampPublishedEvent(Object source, Camp camp) {
        super(source);
        this.camp = camp;
    }

    public Camp getCamp() {
        return camp;
    }
}
