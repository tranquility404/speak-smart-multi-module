package com.tranquility.user.validation;

import com.tranquility.common.file.FileReadException;
import com.tranquility.common.utils.FileUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Set;

public class ProfilePictureValidator implements ConstraintValidator<ValidProfilePicture, MultipartFile> {

    private static final long MAX_SIZE = 5 * 1024 * 1024;   // 5 MB

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty())
            return violation(context, "Profile picture is required");

        if (file.getSize() > MAX_SIZE)
            return violation(context, "Profile picture must be below 5 MB");

        if (!ALLOWED_TYPES.contains(detectContentType(file)))
            return violation(context, "Profile picture must be JPEG, PNG, or WebP");

        return true;
    }

    private String detectContentType(MultipartFile file) {
        String detectedType = null;
        try {
            detectedType = FileUtils.detectContentType(file.getInputStream());
        } catch (IOException e) {
            throw new FileReadException("Failed to read file!");
        }
        return detectedType;
    }

    private boolean violation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation(); // disabling default "Invalid profile picture" from ValidProfilePicture.class
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();

        return false;
    }
}