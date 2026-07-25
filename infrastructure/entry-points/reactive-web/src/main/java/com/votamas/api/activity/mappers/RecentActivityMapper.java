package com.votamas.api.activity.mappers;

import com.votamas.api.activity.dtos.RecentActivityResponseDTO;
import com.votamas.model.activity.RecentActivity;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface RecentActivityMapper {
    RecentActivityResponseDTO toResponse(RecentActivity activity);
}
