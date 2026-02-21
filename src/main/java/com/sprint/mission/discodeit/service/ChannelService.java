package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.*;
import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelResponse createPublicChannel(PublicChannelCreateRequest request);
    ChannelResponse createPrivateChannel(PrivateChannelCreateRequest request);
    ChannelResponse getChannel(UUID id);
    List<ChannelResponse> getAllChannels();
    List<ChannelResponse> findAllByUserId(UUID userId);
    ChannelResponse updateChannel(UUID channelId, ChannelUpdateRequest request);
    void deleteChannel(UUID id);
    
    ChannelResponse enterChannel(UUID userId, UUID channelId);
    void leaveChannel(UUID userId, UUID channelId);
}