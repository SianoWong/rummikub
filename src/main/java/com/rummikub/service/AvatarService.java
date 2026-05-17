package com.rummikub.service;

import com.rummikub.enums.ExceptionEnum;
import com.rummikub.exception.CustomException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class AvatarService {

    private static final Path AVATAR_ROOT = Path.of("src/main/resource/avatar");
    private static final String AVATAR_FILE_PREFIX = "avatar";

    public void saveAvatar(Long userId, MultipartFile avatarImage) {
        if (userId == null || avatarImage == null || avatarImage.isEmpty()) {
            return;
        }

        Path userAvatarDirectory = AVATAR_ROOT.resolve(String.valueOf(userId));
        try {
            Files.createDirectories(userAvatarDirectory);
            deleteExistingAvatarFiles(userAvatarDirectory);
            Files.copy(
                    avatarImage.getInputStream(),
                    userAvatarDirectory.resolve(buildAvatarFileName(avatarImage.getOriginalFilename())),
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException e) {
            throw new CustomException(ExceptionEnum.AVATAR_IMAGE_SAVE_FAILED);
        }
    }

    public Resource getAvatar(Long userId) {
        if (userId == null) {
            return null;
        }

        Path userAvatarDirectory = AVATAR_ROOT.resolve(String.valueOf(userId));
        if (!Files.isDirectory(userAvatarDirectory)) {
            return null;
        }

        try (Stream<Path> paths = Files.list(userAvatarDirectory)) {
            Optional<Path> avatarFile = paths
                    .filter(Files::isRegularFile)
                    .min(Comparator.comparing(Path::getFileName));
            if (avatarFile.isEmpty()) {
                return null;
            }
            return new FileSystemResource(avatarFile.get());
        } catch (IOException e) {
            return null;
        }
    }

    private void deleteExistingAvatarFiles(Path userAvatarDirectory) throws IOException {
        try (Stream<Path> paths = Files.list(userAvatarDirectory)) {
            for (Path path : paths.filter(Files::isRegularFile).toList()) {
                Files.deleteIfExists(path);
            }
        }
    }

    private String buildAvatarFileName(String originalFilename) {
        String extension = StringUtils.getFilenameExtension(originalFilename);
        if (!StringUtils.hasText(extension)) {
            return AVATAR_FILE_PREFIX;
        }
        return AVATAR_FILE_PREFIX + "." + extension;
    }
}
