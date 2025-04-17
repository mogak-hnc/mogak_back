package com.hnc.mogak.zone.application.port.out;

import com.hnc.mogak.zone.adapter.out.persistence.entity.TagEntity;

import java.util.List;
import java.util.Set;

public interface TagPort {

    Set<TagEntity> findOrCreateTags(Set<String> tagList);

}
