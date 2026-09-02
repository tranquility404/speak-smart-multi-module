package com.tranquility.user.repository;

import com.tranquility.user.dto.UpdateUserProfileRequest;
import lombok.RequiredArgsConstructor;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom {

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public int update(UUID id, UpdateUserProfileRequest request) {
        List<String> updates = new ArrayList<>();
        MapSqlParameterSource params = new MapSqlParameterSource();

        addUpdate(updates, params, "username", "username", request.username());
        addUpdate(updates, params, "name", "name", request.name());
        addUpdate(updates, params, "bio", "bio", request.bio());

        if (updates.isEmpty()) return 0;

        String sql = """
                UPDATE user_profiles
                SET %s
                WHERE id = :id
                """.formatted(String.join(", ", updates));

        params.addValue("id", id);
        return jdbc.update(sql, params);
    }

    private void addUpdate(List<String> updates,
                           MapSqlParameterSource params,
                           String column,
                           String parameter,
                           JsonNullable<?> value
    ) {
        if (!value.isPresent()) return;
        updates.add(column + " = :" + parameter);
        params.addValue(parameter, value.orElse(null));
    }
}