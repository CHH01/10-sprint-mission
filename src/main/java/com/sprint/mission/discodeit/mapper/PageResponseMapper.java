package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class PageResponseMapper {

  public <T> PageResponse<T> fromSlice(Slice<T> slice, Function<T, Object> cursorExtractor) {
    Object nextCursor = null;
    if (slice.hasNext() && !slice.getContent().isEmpty()) {
      T lastItem = slice.getContent().get(slice.getContent().size() - 1);
      nextCursor = cursorExtractor.apply(lastItem);
    }

    return new PageResponse<>(
        slice.getContent(),
        nextCursor,
        slice.getSize(),
        slice.hasNext(),
        null
    );
  }

  public <T> PageResponse<T> fromPage(Page<T> page) {
    return new PageResponse<>(
        page.getContent(),
        null,
        page.getSize(),
        page.hasNext(),
        page.getTotalElements()
    );
  }
}
