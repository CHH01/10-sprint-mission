package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusMapper readStatusMapper;

    @Transactional
    public ReadStatusDto createStatus(ReadStatusCreateRequest request) {
        if (readStatusRepository.findByUser_IdAndChannel_Id(request.getUserId(), request.getChannelId()).isPresent()) {
            throw new IllegalArgumentException("ReadStatus with userId " + request.getUserId() + " and channelId " + request.getChannelId() + " already exists");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        Channel channel = channelRepository.findById(request.getChannelId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));

        ReadStatus readStatus = new ReadStatus(user, channel, request.getLastReadAt());
        readStatusRepository.save(readStatus);
        return readStatusMapper.toDto(readStatus);
    }

    public ReadStatusDto findStatus(UUID id) {
        ReadStatus readStatus = readStatusRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 읽기 상태입니다."));
        return readStatusMapper.toDto(readStatus);
    }

    public List<ReadStatusDto> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUser_Id(userId).stream()
                .map(readStatusMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReadStatusDto updateStatus(UUID readStatusId, ReadStatusUpdateRequest request) {
        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 읽기 상태입니다."));
        
        if (request.getNewLastReadAt() != null) {
            readStatus.updateLastReadAt(request.getNewLastReadAt());
        } else {
            readStatus.updateLastReadAt();
        }
        
        return readStatusMapper.toDto(readStatus);
    }

    @Transactional
    public void deleteStatus(UUID id) {
        readStatusRepository.deleteById(id);
    }
}
