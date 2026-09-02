package com.tranquility.user.service;

import com.tranquility.file.entity.StoredFile;
import com.tranquility.file.repository.FileRepository;
import com.tranquility.common.file.FileStorage;
import com.tranquility.common.file.UploadFileFactory;
import com.tranquility.common.file.model.UploadResult;
import com.tranquility.user.dto.*;
import com.tranquility.common.user.exception.NoProfilePicException;
import com.tranquility.common.user.exception.UserNotFoundException;
import com.tranquility.user.model.LeaderboardCursor;
import com.tranquility.user.repository.UserRepository;
import com.tranquility.common.utils.CursorCodec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final FileStorage storage;
    private final FileRepository fileRepository;
    private final CursorCodec codec;

    public UserOverview getUser(UUID id) {
        return repository.findUserOverview(id).orElseThrow(UserNotFoundException::new);
    }

    public void updateUser(UUID id, UpdateUserProfileRequest request) {
        int rowsUpdated = repository.update(id, request);
        if (rowsUpdated == 0) throw new UserNotFoundException();
    }

    public CursorPageDto<LeaderboardDto> getLeaderboard(String cursor, int size) {
        size = Math.min(size, 20);  // max = 20 allowed
        List<LeaderboardDto> data;

        if (cursor == null || cursor.isBlank()) // checks is empty() or contains only white space
            data = repository.findLeaderboardFirstPage(PageRequest.of(0, size + 1));
        else {
            LeaderboardCursor decodedCursor = codec.decode(cursor, LeaderboardCursor.class);
            data = repository.findLeaderboardNextPage(
                    decodedCursor.totalPoints(),
                    decodedCursor.currentStreak(),
                    decodedCursor.email(),
                    PageRequest.of(0, size + 1)
            );
        }

        boolean hasNext = data.size() > size;
        String nextCursor = null;

        if (hasNext) {
            data = data.subList(0, size);

            LeaderboardDto last = data.getLast();
            nextCursor = codec.encode(
                new LeaderboardCursor(
                        last.totalPoints(), last.currentStreak(), last.email()
                ));
        }

        return new CursorPageDto<>(data, nextCursor, hasNext);
    }

    public ProfilePicUrlDto getProfilePicUrl(UUID userId) {
        String url = repository.findProfilePicUrlByUserId(userId).orElseThrow(NoProfilePicException::new);
        return new ProfilePicUrlDto(url);
    }

    public ProfilePicUrlDto updateProfilePic(UUID userId, MultipartFile file) throws IOException {
        UploadResult result = storage.upload(
                UploadFileFactory.profilePicture(
                        userId,
                        file.getInputStream(),
                        file.getOriginalFilename(),
                        file.getSize()
                )
        );

        String publicUrl = storage.generatePublicUrl(result.storageLocation());
        StoredFile storedFile = StoredFile.createPublicFile(userId, result, publicUrl);
        fileRepository.save(storedFile);

        return new ProfilePicUrlDto(publicUrl);
    }

//    public void updateAnalysisPoints(String userid, Instant completed) {
//        User user = repository.findById(userid).orElseThrow();
//        int streak = 1;
//        try {
//            long days = ChronoUnit.DAYS.between(user.getLastAnalysis(), completed);
//            if (days == 0)
//                streak = user.getStreak();
//            else if (days == 1)
//                streak = user.getStreak() + 1;
//        } catch (Exception e) {
//            log.error("First analysis: %s".formatted(e.getMessage()));
//        }
//
//        Query query = new Query(Criteria.where("_id").is(userid));
//        Update update = new Update()
//                .inc("points", 10 * streak)
//                .inc("analysisCount", 1)
//                .set("streak", streak)
//                .set("lastAnalysis", completed);
//
//        mongoTemplate.updateFirst(query, update, User.class);
//    }
}

