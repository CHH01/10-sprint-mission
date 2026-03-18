package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentRequest;
import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicBinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentMapper binaryContentMapper;
  private final BinaryContentStorage binaryContentStorage;

  @Transactional
  public BinaryContentDto create(BinaryContentRequest request) {
    BinaryContent binaryContent = new BinaryContent(
        request.getFileName(),
        request.getContentType(),
        request.getContent() != null ? request.getContent().length : 0
    );
    binaryContentRepository.save(binaryContent);

    if (request.getContent() != null) {
      binaryContentStorage.put(binaryContent.getId(), request.getContent());
    }

    return binaryContentMapper.toDto(binaryContent);
  }

  public BinaryContentDto find(UUID id) {
    BinaryContent binaryContent = binaryContentRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 컨텐츠입니다."));
    return binaryContentMapper.toDto(binaryContent);
  }

  public List<BinaryContentDto> findAllByIdIn(List<UUID> ids) {
    return binaryContentRepository.findAllById(ids).stream()
        .map(binaryContentMapper::toDto)
        .collect(Collectors.toList());
  }

  @Transactional
  public void delete(UUID id) {
    BinaryContent binaryContent = binaryContentRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 컨텐츠입니다."));
    binaryContentRepository.delete(binaryContent);
  }
}
