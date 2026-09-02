package com.tranquility.user.repository;

import com.tranquility.user.dto.UpdateUserProfileRequest;
import java.util.UUID;

public interface UserRepositoryCustom {

    int update(UUID id, UpdateUserProfileRequest request);

}
