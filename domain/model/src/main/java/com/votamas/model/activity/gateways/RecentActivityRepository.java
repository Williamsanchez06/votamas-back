package com.votamas.model.activity.gateways;

import com.votamas.model.activity.RecentActivity;
import reactor.core.publisher.Flux;

public interface RecentActivityRepository {
    Flux<RecentActivity> findRecent(int limit);
}
