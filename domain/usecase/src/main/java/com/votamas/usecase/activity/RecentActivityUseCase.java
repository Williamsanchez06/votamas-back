package com.votamas.usecase.activity;

import com.votamas.model.activity.RecentActivity;
import com.votamas.model.activity.gateways.RecentActivityRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class RecentActivityUseCase {
    private final RecentActivityRepository recentActivityRepository;

    public Flux<RecentActivity> execute(int limit) {
        return recentActivityRepository.findRecent(limit);
    }
}
