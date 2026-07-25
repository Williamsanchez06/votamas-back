package com.votamas.r2dbc.user;

import com.votamas.model.common.pagination.PageQuery;
import com.votamas.model.user.User;
import com.votamas.r2dbc.user.entities.UserData;
import com.votamas.r2dbc.user.mapper.UserRepositoryMapper;
import org.junit.jupiter.api.Test;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserAdapterTest {
    private static final String ADMIN_ROLE = "ADMINISTRADOR";

    private final UserReactiveRepository repository = mock(UserReactiveRepository.class);
    private final UserRepositoryMapper mapper = mock(UserRepositoryMapper.class);
    private final UserAdapter adapter = new UserAdapter(
            repository,
            mapper,
            mock(DatabaseClient.class),
            mock(TransactionalOperator.class)
    );

    @Test
    void shouldExcludeAdministratorsFromContentAndTotalElements() {
        var pagination = new PageQuery(1, 2);
        var userData = UserData.builder()
                .id(UUID.randomUUID())
                .name("Ana")
                .surname("Pérez")
                .email("ana@example.com")
                .password("encoded")
                .active(true)
                .build();
        var user = User.builder()
                .id(userData.id())
                .name(userData.name())
                .surname(userData.surname())
                .email(userData.email())
                .password(userData.password())
                .active(userData.active())
                .build();

        when(repository.findAllExcludingRole(ADMIN_ROLE, 2, 2L))
                .thenReturn(Flux.just(userData));
        when(repository.countExcludingRole(ADMIN_ROLE)).thenReturn(Mono.just(3L));
        when(mapper.toUser(userData)).thenReturn(user);

        StepVerifier.create(adapter.findAll(pagination))
                .expectNextMatches(result ->
                        result.content().equals(java.util.List.of(user))
                                && result.page() == 1
                                && result.size() == 2
                                && result.totalElements() == 3
                                && result.totalPages() == 2)
                .verifyComplete();

        verify(repository).findAllExcludingRole(ADMIN_ROLE, 2, 2L);
        verify(repository).countExcludingRole(ADMIN_ROLE);
    }
}
