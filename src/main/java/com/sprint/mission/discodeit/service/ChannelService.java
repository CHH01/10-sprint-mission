package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.*;
import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelDto createPublicChannel(PublicChannelCreateRequest request);
    ChannelDto createPrivateChannel(PrivateChannelCreateRequest request);
    ChannelDto getChannel(UUID id);
    List<ChannelDto> getAllChannels();
    List<ChannelDto> findAllByUserId(UUID userId);
    ChannelDto updateChannel(UUID channelId, ChannelUpdateRequest request);
    void deleteChannel(UUID id);
    
    ChannelDto enterChannel(UUID userId, UUID channelId);
    void leaveChannel(UUID userId, UUID channelId);
}